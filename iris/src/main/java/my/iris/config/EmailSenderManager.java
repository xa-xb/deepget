package my.iris.config;

import my.iris.cache.SystemCache;
import my.iris.service.email.EmailLogService;
import my.iris.service.email.EmailService;
import org.jspecify.annotations.Nullable;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class EmailSenderManager {
    private final ReentrantLock lock = new ReentrantLock();
    long counter;
    private List<JavaMailSenderImpl> senders;

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
            senders = new LinkedList<>();
            systemCache.getSmtpServers().forEach((smtpServer) -> {
                var sender = EmailService.createMailSender(smtpServer);
                if (sender == null) {
                    return;
                }
                senders.add(sender);
            });
            counter = emailLogService.getLastId();
        } finally {
            lock.unlock();
        }
    }

    public JavaMailSenderImpl getSender() {
        lock.lock();
        try {
            var result = senders.get((int) (counter % senders.size()));
            counter++;
            return result;
        } finally {
            lock.unlock();
        }
    }

    public List<String> getSenderNames() {
        return senders.stream()
                .map(JavaMailSenderImpl::getUsername)
                .toList();
    }

    @Nullable
    public JavaMailSenderImpl getSenderByName(String senderEmail) {
        if (senderEmail == null) return null;
        return senders.stream()
                .filter(item -> senderEmail.equals(item.getUsername()))
                .findFirst()
                .orElse(null);
    }
}

