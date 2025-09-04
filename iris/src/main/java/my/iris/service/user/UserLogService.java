package my.iris.service.user;

import my.iris.model.user.vo.UserLogVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserLogService {
    void addLog(String operation, Object content);
    void addLog(Long userId, String operation, Object content);

    Page<UserLogVo> getPage(String name, String ip, String operation, Pageable pageable);
}