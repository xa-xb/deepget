package my.iris.service.system.impl;

import my.iris.model.CloneAndSanitize;
import my.iris.model.system.entity.AdminLogEntity;
import my.iris.model.system.vo.AdminLogVo;
import my.iris.repository.system.AdminLogRepository;
import my.iris.repository.system.AdminRepository;
import my.iris.service.system.AdminLogService;
import my.iris.util.DbUtils;
import my.iris.util.JsonUtils;
import my.iris.util.TaskContext;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AdminLogServiceImpl implements AdminLogService {
    @Resource
    AdminRepository adminRepository;

    @Resource
    AdminLogRepository adminLogRepository;

    @Override
    public void addLog(String operation, Object content) {
        var userId = TaskContext.getUserToken().getUserId();
        if (content instanceof CloneAndSanitize<?> obj) {
            content = obj.cloneAndSanitize();
        }
        adminRepository.updateLastActionAt(userId);
        var adminLogEntity = new AdminLogEntity()
                .setUserId(userId)
                .setIp(TaskContext.getClientIp())
                .setOperation(operation)
                .setContent(JsonUtils.stringify(content))
                .setDuration(TaskContext.getDuration());
        adminLogRepository.save(adminLogEntity);
    }

    public Page<AdminLogVo> getPage(
            String name,
            String ip,
            String operation,
            Pageable pageable) {
        return adminLogRepository.getPage(
                DbUtils.getLikeWords(name),
                DbUtils.getLikeWords(ip),
                DbUtils.getLikeWords(operation),
                pageable);
    }
}
