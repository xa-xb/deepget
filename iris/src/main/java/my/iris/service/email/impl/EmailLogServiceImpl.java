package my.iris.service.email.impl;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.model.email.dto.EmailQueryDto;
import my.iris.model.email.vo.EmailLogVo;
import my.iris.repository.email.EmailLogRepository;
import my.iris.service.email.EmailLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class EmailLogServiceImpl implements EmailLogService {

    @Resource
    EmailLogRepository emailLogRepository;


    @Override
    public long getLastId() {
        return emailLogRepository.getLastId();
    }

    @Override
    public Page<EmailLogVo> getPage(Pageable pageable, EmailQueryDto queryDto) {
        return emailLogRepository.getPage(pageable, queryDto.getAction(), queryDto.getFrom(), queryDto.getTo());
    }


}
