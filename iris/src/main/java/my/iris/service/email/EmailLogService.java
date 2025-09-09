package my.iris.service.email;

import my.iris.model.email.dto.EmailQueryDto;
import my.iris.model.email.vo.EmailLogVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmailLogService {
    long getLastId();

    Page<EmailLogVo> getPage(Pageable pageable, EmailQueryDto queryDto);
}