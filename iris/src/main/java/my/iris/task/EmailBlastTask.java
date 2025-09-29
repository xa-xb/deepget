package my.iris.task;

import my.iris.config.EmailSenderManager;
import my.iris.model.email.entity.EmailLogEntity;
import my.iris.model.email.vo.EmailBlastVo;
import my.iris.repository.email.EmailLogRepository;
import my.iris.service.email.EmailBlastService;
import my.iris.service.email.EmailService;
import my.iris.util.JsonUtils;
import my.iris.util.LogUtils;
import my.iris.util.UUIDUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.locks.LockSupport;


@Component
public class EmailBlastTask {
    private final EmailSenderManager emailSenderManager;
    private final EmailLogRepository emailLogRepository;
    int total = 0;
    ConfigurableApplicationContext applicationContext;
    EmailService emailService;
    StringRedisTemplate stringRedisTemplate;

    public EmailBlastTask(
            ConfigurableApplicationContext configurableApplicationContext,
            EmailService emailService,
            StringRedisTemplate stringRedisTemplate, EmailSenderManager emailSenderManager, EmailLogRepository emailLogRepository) {
        this.applicationContext = configurableApplicationContext;
        this.emailService = emailService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.emailSenderManager = emailSenderManager;
        this.emailLogRepository = emailLogRepository;
    }

    boolean isWorkingTime() {
        var hour = LocalDateTime.now().getHour();
        return hour >= 7 && hour < 18;
    }

    @Scheduled(initialDelay = 1_000, fixedDelay = 60_000)
    private void sendAdEmail() {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        total = 0;
        while (!applicationContext.isClosed() && isWorkingTime()) {
            try {
                if (!send()) {
                    break;
                }
                total++;
            } catch (Exception e) {
                LogUtils.error(getClass(), "", e);
                break;
            }
            LockSupport.parkNanos(1_000_000_000L);
        }
        if (total > 0) {
            LogUtils.info(getClass(), String.format("The email blast task has send ad email. Total: %d", total));
        }
    }

    @SuppressWarnings("unchecked")
    boolean send() {
        StreamReadOptions options = StreamReadOptions.empty()
                .count(1);// 每次只读一条
        var records = stringRedisTemplate.opsForStream()
                .read(options, StreamOffset.create(EmailBlastService.REDIS_KEY, ReadOffset.from("0")));
        if (records == null || records.isEmpty()) {
            return false;
        }

        var rec = records.getFirst();
        var obj = JsonUtils.parse((String) rec.getValue().get("json"), EmailBlastVo.class);

        if (obj != null) {
            String err = null;
            JavaMailSenderImpl sender = null;
            if (!obj.force()) {
                if (emailLogRepository.findLatestWithin30Days(obj.to(), "ad").isPresent()) {
                    err = "task duplation in 30 days";
                }
            }

            if (err == null && StringUtils.hasText(obj.sender())) {
                sender = emailSenderManager.getSenderByName(obj.sender());
                if (sender == null) {
                    err = "smtp account doesn't exists: " + obj.sender();
                }
            }
            if (err == null) {
                if (sender == null) {
                    emailService.sendAd(obj.to());
                } else {
                    emailService.sendAd(obj.to(), sender);
                }
            } else {
                var logEntity = new EmailLogEntity()
                        .setUserId(null)
                        .setUuid(UUIDUtils.generateUuidV7())
                        .setCreatedAt(LocalDateTime.now())
                        .setDuration(0)
                        .setFrom(StringUtils.hasText(obj.sender()) ? obj.sender() : null)
                        .setTo(obj.to())
                        .setErr(err)
                        .setAction("ad")
                        .setData(null)
                        .setIp("127.0.0.1");
                emailLogRepository.saveAndFlush(logEntity);
            }
        }
        stringRedisTemplate.opsForStream().delete(EmailBlastService.REDIS_KEY, rec.getId());
        return true;
    }
}
