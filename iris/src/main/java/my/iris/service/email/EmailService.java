package my.iris.service.email;

import my.iris.model.ApiResult;
import my.iris.model.system.dto.SmtpServerDto;
import my.iris.model.system.dto.SmtpTestDto;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Optional;

public interface EmailService {

    void send(Long userId, String to, String subject, String html, String logType, String logData, String logIp, JavaMailSenderImpl mailSender);

    void sendAsync(Long userId, String to, String subject, String html, String logType, String logData, String logIp, JavaMailSenderImpl mailSender);

    void sendAsync(Long userId, String to, String subject, String html, String logType, String logData, String logIp);

    void sendAsync(Long userId, String to, String subject, String html, String logType, String logData);

    void sendWelcomeEmail(Long userId, String to);

    /**
     * Sends a verification code to the specified recipient.
     *
     * @param userId user id
     * @param action action
     * @param to   the target email address
     * @param code the verification code to send
     * @return an {@code Optional} containing an error message if sending fails; {@code Optional.empty()} indicates success
     */
    Optional<String> sendVerificationCode(Long userId, String action, String to, String code);

    ApiResult<Void> testMail(SmtpTestDto smtpTestDto);

    /**
     * Checks whether the given email address has exceeded the daily sending limit.
     *
     * @param email the target email address
     * @return an {@code Optional} containing an error message if sending fails; {@code Optional.empty()} indicates success
     */
    Optional<String> validateDailyEmailLimit(String email);
    String verifyCode(String email, String action, String code);

    static JavaMailSenderImpl createMailSender(SmtpServerDto smtpServer) {
        if (!smtpServer.getEnable()) {
            return null;
        }
        var sender = new JavaMailSenderImpl();
        sender.setDefaultEncoding("UTF-8");
        sender.setPort(smtpServer.getPort());
        sender.setHost(smtpServer.getHost());
        sender.setUsername(smtpServer.getName());
        sender.setPassword(smtpServer.getPassword());
        sender.getJavaMailProperties().put(
                "mail.smtp.auth", "true"
        );
        if (smtpServer.getStarttls()) {
            sender.getJavaMailProperties().putAll(Map.of(
                    "mail.smtp.starttls.enable", "true",
                    "mail.smtp.starttls.required", "true"
            ));
        } else {
            sender.getJavaMailProperties().put(
                    "mail.smtp.ssl.enable", "true"
            );
        }
        return sender;
    }
}
