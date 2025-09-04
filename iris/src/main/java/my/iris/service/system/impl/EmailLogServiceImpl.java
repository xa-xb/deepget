package my.iris.service.system.impl;

import my.iris.repository.system.EmailLogRepository;
import my.iris.service.system.EmailLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class EmailLogServiceImpl implements EmailLogService {

    @Resource
    EmailLogRepository emailLogRepository;

    @Override
    public long getLastId() {
        return emailLogRepository.getLastId();
    }
}
