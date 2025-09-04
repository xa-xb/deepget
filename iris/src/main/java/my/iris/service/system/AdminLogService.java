package my.iris.service.system;

import my.iris.model.system.vo.AdminLogVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminLogService {
    void addLog(String operation, Object content);

    Page<AdminLogVo> getPage(
            String name,
            String ip,
            String operation,
            Pageable pageable);
}