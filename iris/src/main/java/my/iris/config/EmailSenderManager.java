package my.iris.config;

import my.iris.cache.SystemCache;
import my.iris.service.system.EmailLogService;
import my.iris.service.system.EmailService;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class EmailSenderManager {
    private final ReentrantLock lock = new ReentrantLock();
    long counter;
    private List<JavaMailSenderImpl> mailSenders;

    EmailLogService emailLogService;
    private final SystemCache systemCache;

    public EmailSenderManager(
            EmailLogService emailLogService,
            SystemCache systemCache
    ) {
        this.emailLogService = emailLogService;
        this.systemCache = systemCache;
        init();
    }

    public void init() {
        lock.lock();
        try {
            mailSenders = new LinkedList<>();
            systemCache.getSmtpServers().forEach((smtpServer) -> {
                var sender = EmailService.createMailSender(smtpServer);
                if (sender == null) {
                    return;
                }
                mailSenders.add(sender);
            });
            counter = emailLogService.getLastId();
        } finally {
            lock.unlock();
        }
    }

    public JavaMailSenderImpl getMailSender() {
        lock.lock();
        try {
            var result = mailSenders.get((int) (counter % mailSenders.size()));
            counter++;
            return result;
        } finally {
            lock.unlock();
        }

    }
}

