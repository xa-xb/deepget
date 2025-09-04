package my.iris.service.user.impl;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import my.iris.auth.UserToken;
import my.iris.model.ApiResult;
import my.iris.model.user.entity.UserEntity;
import my.iris.model.user.entity.UserHotEntity;
import my.iris.model.user.vo.UserVo;
import my.iris.repository.user.UserHotRepository;
import my.iris.repository.user.UserRepository;
import my.iris.service.system.EmailService;
import my.iris.service.user.UserLogService;
import my.iris.service.user.UserService;
import my.iris.task.SessionTask;
import my.iris.util.*;
import my.iris.util.status.AccountStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    @PersistenceContext
    EntityManager entityManager;

    @Resource
    PasswordEncoder passwordEncoder;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    UserLogService userLogService;
    @Resource
    private UserRepository userRepository;

    @Resource
    private UserHotRepository userHotRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private SessionTask sessionTask;

    @Override
    public void bindEmail(Long userId, String email) {
        userRepository.findByEmailForUpdate(email).ifPresent(entity -> {
            if (!Objects.equals(entity.getId(), userId)) {
                entity.setEmail(null);
            }

        });
        userRepository.findByIdForUpdate(userId).ifPresent(entity -> entity.setEmail(email));
    }

    /**
     * change password
     *
     * @param userId   user id
     * @param password password
     * @return null: success, else error msg
     */
    @Override
    public String changePassword(long userId, String password) {
        var entity = userRepository.findByIdForUpdate(userId).orElse(null);
        if (entity == null) {
            return "用户不存在";
        }
        entity.setPassword(passwordEncoder.encode(password));
        deleteSessionsByUserId(userId);
        return null;
    }

    /**
     * delete all sessions by user id
     *
     * @param userId user id
     */
    public void deleteSessionsByUserId(long userId) {
        var key = UserToken.REDIS_USER_PREFIX + userId;
        var items = stringRedisTemplate.opsForSet().members(key);
        if (items != null) {
            stringRedisTemplate.delete(items);
        }
        stringRedisTemplate.delete(key);
    }

    @Override
    public UserEntity findUserByAccount(String account) {
        if (ValidatorUtils.validateUserName(account) == null) {
            return userRepository.findByNameForUpdate(account).orElse(null);
        } else if (ValidatorUtils.validateMobile(account) == null) {
            return userRepository.findByMobileForUpdate(account).orElse(null);
        } else if (ValidatorUtils.validateEmail(account) == null) {
            return userRepository.findByEmailForUpdate(account).orElse(null);
        }
        return null;
    }

    @Override
    public SignInResult findUserByAccountAndPassword(String account, String password) {
        SignInResult signInResult = new SignInResult();
        var userEntity = findUserByAccount(account);
        if (userEntity == null) {
            return signInResult.setError(ValidatorUtils.ACCOUNT_NOT_EXIST);
        }
        signInResult.setUserEntity(userEntity);
        if (!passwordEncoder.matches(password, signInResult.getUserEntity().getPassword())) {
            return signInResult.setError(ValidatorUtils.ACCOUNT_OR_PWD_ERROR);
        } else if (signInResult.getUserEntity().getStatus() > 0) {
            signInResult.setError(AccountStatus.getStatusName(
                    signInResult.getUserEntity().getStatus()
            ));
        }
        if (signInResult.getError() != null) {
            signInResult.setUserEntity(null);
        }
        return signInResult;
    }

    @Override
    public ApiResult<Void> resetPasswordByEmailCode(String email, String code, String newPassword) {
        var msg = emailService.verifyCode(email, "reset_password", code);
        if (msg != null) return ApiResult.error(msg);
        userRepository.findByEmailForUpdate(email).ifPresent(entity -> {
            entity.setPassword(passwordEncoder.encode(newPassword));
            deleteSessionsByUserId(entity.getId());
            userLogService.addLog(entity.getId(), "reset_password_by_email_code", Map.of("email", email));
        });
        return ApiResult.success();
    }


    @Override
    public SignInResult signIn(String account, String password) {
        SignInResult signInResult = findUserByAccountAndPassword(account, password);
        if (signInResult.getError() == null) {
            UserToken userToken = new UserToken(signInResult.getUserEntity());
            userToken.save(TaskContext.getSession());
            signInLog(signInResult.getUserEntity());
        }
        return signInResult;
    }

    @Override
    public void signInLog(UserEntity userEntity) {
        userEntity.setLastSignInAt(LocalDateTime.now())
                .setLastSignInIp(TaskContext.getClientIp());
        userLogService.addLog("sign_in", Map.of("username", userEntity.getName()));
    }

    /**
     * 退出登录
     */
    @Override
    public void signOut() {
        Optional.of(TaskContext.getUserToken())
                .ifPresent(token -> {
                    userLogService.addLog("sign_out", Map.of("username", token.getUsername()));
                    TaskContext.getSession().clear();
                });
    }

    @Override
    public void signOutAll() {
        Optional.of(TaskContext.getUserToken())
                .ifPresent(token -> {
                    userLogService.addLog("sign_out_all", Map.of("username", token.getUsername()));
                    stringRedisTemplate.delete(UserToken.REDIS_USER_PREFIX + token.getUserId());
                });
    }

    /**
     * register user
     *
     * @return error msg
     */

    @Override
    public String signUp(String username, String password, Boolean autoSignIn) {
        var msg = ValidatorUtils.validateUserName(username);
        if (msg != null) {
            return msg;
        }
        msg = ValidatorUtils.validatePassword(password);
        if (msg != null) {
            return msg;
        }
        userRepository.findOne(Example.of(new UserEntity().setEmail("")));
        if (userRepository.findOne(Example.of(new UserEntity().setName(username))).isPresent()) {
            return USER_NAME_EXIST_ERROR;
        }

        UserEntity userEntity = new UserEntity()
                .setName(username)
                .setPassword(passwordEncoder.encode(password))
                .setCreatedIp(TaskContext.getClientIp());
        try {
            userRepository.saveAndFlush(userEntity);
            entityManager.refresh(userEntity);
            userHotRepository.save(new UserHotEntity().setUserId(userEntity.getId()));
        } catch (DataIntegrityViolationException ex) {
            DbUtils.rollback();
            return USER_NAME_EXIST_ERROR;
        } catch (Exception ex) {
            LogUtils.error(this.getClass(), "", ex);
            DbUtils.rollback();
            return "未知错误,请联系管理员.";
        }
        CaptchaUtils.clear();
        if (autoSignIn) {
            UserToken userToken = new UserToken(userEntity);
            userToken.save(TaskContext.getSession());
            userEntity.setLastSignInAt(userEntity.getCreatedAt())
                    .setLastSignInIp(userEntity.getCreatedIp());
        }
        userLogService.addLog("sign_up", Map.of("username", username));
        return null;
    }

    @Override
    public ApiResult<Void> signUpByAdmin(String username, String password) {
        var msg = signUp(username, password, false);
        if (msg != null) {
            return ApiResult.error(msg);
        }
        userLogService.addLog("sign_up", Map.of("username", username));
        return ApiResult.success();
    }

    @Override
    public Page<UserVo> getPage(String name, String mobile, String email, Integer signStatus, Pageable pageable) {
        return userRepository.getPage(
                DbUtils.getLikeWords(name),
                DbUtils.getLikeWords(mobile),
                DbUtils.getLikeWords(email),
                signStatus,
                pageable);
    }

    @Override
    public UserVo getUserVoById(long id) {
        return userRepository.getUserVoById(id);
    }
}
