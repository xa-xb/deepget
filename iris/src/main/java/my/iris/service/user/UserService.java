package my.iris.service.user;

import lombok.Data;
import lombok.experimental.Accessors;
import my.iris.model.ApiResult;
import my.iris.model.user.entity.UserEntity;
import my.iris.model.user.vo.UserVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {
    String USER_NAME_EXIST_ERROR = "用户名已被注册";

    void bindEmail(Long userId, String email);

    /**
     * Changes the user's password and invalidates all active sessions for security.
     *
     * @param userId   the ID of the user
     * @param password the new plain-text password to set
     * @return {@code null} if the operation succeeds; otherwise, a string describing the error
     */

    String changePassword(long userId, String password);

    void deleteSessionsByUserId(long userId);

    UserEntity findUserByAccount(String account);

    SignInResult findUserByAccountAndPassword(String account, String password);

    /**
     * Resets password using email verification code.
     *
     * @param email       email address
     * @param code        verification code sent to email
     * @param newPassword new password to set
     * @return an {@code ApiResult} containing:
     *         <ul>
     *             <li>{@code code = 1} if successful</li>
     *             <li>other {@code code} values indicate failure, with {@code msg} providing the error reason</li>
     *         </ul>
     */
    ApiResult<Void> resetPasswordByEmailCode(String email, String code, String newPassword);

    SignInResult signIn(String account, String password);

    void signInLog(UserEntity userEntity);

    void signOut();

    void signOutAll();

    String signUp(String username, String password, Boolean autoSignIn);

    ApiResult<Void> signUpByAdmin(String username, String password);

    Page<UserVo> getPage(String name, String mobile, String email, Integer signInTimeStatus, Pageable pageable);

    UserVo getUserVoById(long id);

    @Accessors(chain = true)
    @Data
    class SignInResult {
        String error;
        UserEntity userEntity;
    }
}