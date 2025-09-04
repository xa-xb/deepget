package my.iris.service.user.impl;

import my.iris.model.CloneAndSanitize;
import my.iris.model.user.entity.UserLogEntity;
import my.iris.model.user.vo.UserLogVo;
import my.iris.repository.user.UserLogRepository;
import my.iris.service.user.UserLogService;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.util.DbUtils;
import my.iris.util.JsonUtils;
import my.iris.util.TaskContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserLogServiceImpl implements UserLogService {

    @Resource
    private UserLogRepository userLogRepository;

    @Override
    public void addLog(String operation, Object content) {
        addLog(TaskContext.getUserToken().getUserId(), operation, content);
    }


    @Override
    public void addLog(Long userId, String operation, Object content) {
        if (content instanceof CloneAndSanitize<?> obj) {
            content = obj.cloneAndSanitize();
        }
        var userLogEntity = new UserLogEntity()
                .setUserId(userId)
                .setIp(TaskContext.getClientIp())
                .setOperation(operation)
                .setContent(JsonUtils.stringify(content))
                .setDuration(TaskContext.getDuration());
        userLogRepository.save(userLogEntity);
    }


    @Override
    public Page<UserLogVo> getPage(String name, String ip, String operation, Pageable pageable) {
        return userLogRepository.getPage(
                DbUtils.getLikeWords(name),
                DbUtils.getLikeWords(ip),
                DbUtils.getLikeWords(operation),
                pageable);
    }
}
