package my.iris.service.user.impl;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.auth.UserToken;
import my.iris.model.ApiResult;
import my.iris.repository.user.UserRepository;
import my.iris.service.email.EmailService;
import my.iris.service.user.SettingService;
import my.iris.service.user.UserLogService;
import my.iris.service.user.UserService;
import my.iris.util.TaskContext;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Transactional
public class SettingServiceImpl implements SettingService {

    @Resource
    UserService userService;

    @Resource
    UserRepository userRepository;

    @Resource
    UserLogService userLogService;

    @Resource
    private EmailService emailService;

    @Override
    public ApiResult<Void> bindEmail(Long userId, String email, String code) {
        var msg = emailService.verifyCode(email, "bind_email", code);
        if (msg != null) return ApiResult.badRequest(msg);
        userService.bindEmail(userId, email);
        userLogService.addLog("bind_email", Map.of("email", email));
        userRepository.findById(userId).ifPresent(userEntity ->
                new UserToken(userEntity).save(TaskContext.getSession()));
        return ApiResult.success();
    }

}

