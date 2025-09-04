package my.iris.service.system.impl;


import my.iris.cache.SystemCache;
import my.iris.config.EmailSenderManager;
import my.iris.model.ApiResult;
import my.iris.model.system.dto.SmtpTestDto;
import my.iris.model.system.entity.EmailLogEntity;
import my.iris.repository.system.EmailLogRepository;
import my.iris.repository.user.UserRepository;
import my.iris.service.system.AdminLogService;
import my.iris.service.system.EmailService;
import my.iris.util.Helper;
import my.iris.util.TaskContext;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import my.iris.util.UUIDUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


@Service
@Transactional
public class EmailServiceImpl implements EmailService {
    AdminLogService adminLogService;
    EmailSenderManager emailSenderManager;
    EmailLogRepository emailLogRepository;
    SystemCache systemCache;
    UserRepository userRepository;

    public EmailServiceImpl(
            AdminLogService adminLogService,
            EmailSenderManager emailSenderManager,
            EmailLogRepository emailLogRepository,
            SystemCache systemCache,
            UserRepository userRepository
    ) {
        this.adminLogService = adminLogService;
        this.emailLogRepository = emailLogRepository;
        this.emailSenderManager = emailSenderManager;
        this.systemCache = systemCache;
        this.userRepository = userRepository;
    }

    /**
     * 发送邮件
     */
    @Override
    public void send(Long userId, String to, String subject, String html,
                     String action, String logData, String logIp,
                     JavaMailSenderImpl mailSender) {
        var startTime = System.currentTimeMillis();
        var now = LocalDateTime.now();
        String errMsg = null;
        MimeMessage message = mailSender.createMimeMessage();
        try {
            var helper = new MimeMessageHelper(message, true);
            helper.setFrom(Objects.requireNonNull(mailSender.getUsername()), systemCache.getSiteName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);

        } catch (Exception e) {
            errMsg = e.getMessage();
        } finally {
            var logEntity = new EmailLogEntity()
                    .setUserId(userId)
                    .setUuid(UUIDUtils.generateUuidV7())
                    .setCreatedAt(now)
                    .setDuration((int) (System.currentTimeMillis() - startTime))
                    .setFrom(mailSender.getUsername())
                    .setTo(to)
                    .setErr(errMsg)
                    .setAction(action)
                    .setData(logData)
                    .setIp(logIp);
            emailLogRepository.saveAndFlush(logEntity);
        }
    }

    /**
     * 发送邮件,异步
     */
    @Override
    public void sendAsync(Long userId, String to, String subject, String html,
                          String action, String logData, String logIp,
                          JavaMailSenderImpl mailSender) {
        Thread.ofVirtual().start(() -> send(userId, to, subject, html, action, logData, logIp, mailSender));
    }

    @Override
    public void sendAsync(Long userId, String to, String subject, String html,
                          String action, String logData, String logIp) {
        sendAsync(userId, to, subject, html, action, logData, logIp, emailSenderManager.getMailSender());
    }

    @Override
    public void sendAsync(Long userId, String to, String subject, String html,
                          String action, String logData) {
        sendAsync(userId, to, subject, html, action, logData, TaskContext.getClientIp());
    }

    /**
     * send welcome email.
     */
    @Override
    public void sendWelcomeEmail(Long userId, String to) {
        // 构建邮件内容
        String emailContent = Helper.renderTemplate("mail/welcome",
                Map.of("siteName", systemCache.getSiteName(),
                        "siteUrl", systemCache.getSiteUrl()));
        sendAsync(userId, to, "欢迎加入 - " + systemCache.getSiteName(), emailContent, "welcome", null);

    }

    @Override
    public Optional<String> sendVerificationCode(Long userId, String action, String to, String code) {
        return validateDailyEmailLimit(to).or(() -> {
            // 构建邮件内容
            String emailContent = Helper.renderTemplate("mail/verification_code", Map.of("code", code));
            sendAsync(userId, to, "验证码 - " + systemCache.getSiteName(), emailContent, action, code);
            return Optional.empty();
        });
    }

    /**
     * send test mail.
     */
    @Override
    public ApiResult<Void> testMail(SmtpTestDto smtpTestDto) {
        String emailContent = Helper.renderTemplate("mail/test", Map.of());
        smtpTestDto.smtpServers().forEach(smtpServer -> {
            var sender = EmailService.createMailSender(smtpServer);
            if (sender == null) {
                return;
            }
            sendAsync(null, smtpTestDto.to(),
                    "测试邮件 from " + sender.getUsername(),
                    emailContent, "test_mail", null,
                    TaskContext.getClientIp(), sender);

        });
        adminLogService.addLog("发送测试邮件", smtpTestDto);
        return ApiResult.success();
    }

    @Override
    public Optional<String> validateDailyEmailLimit(String email) {
        if (emailLogRepository.countByEmailToday(email) > systemCache.getDailyEmailLimit()) {
            return Optional.of("该邮箱今日发送次数已达上限");
        }
        return Optional.empty();
    }

    @Override
    public String verifyCode(String email, String action, String code) {
        var entity = emailLogRepository.findLatestWithin5Minutes(email, action).orElse(null);
        if (entity != null && Objects.equals(entity.getData(), code)) {
            entity.setData(null);
            return null;
        }
        return "邮箱验证码错误";
    }
}
