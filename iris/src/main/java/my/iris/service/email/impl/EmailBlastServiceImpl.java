package my.iris.service.email.impl;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.config.EmailSenderManager;
import my.iris.model.ApiResult;
import my.iris.model.email.dto.EmailBlastDto;
import my.iris.model.email.vo.EmailBlastVo;
import my.iris.service.email.EmailBlastService;
import my.iris.service.system.AdminLogService;
import my.iris.util.JsonUtils;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EmailBlastServiceImpl implements EmailBlastService {

    @Resource
    AdminLogService adminLogService;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    private EmailSenderManager emailSenderManager;


    @Override
    public ApiResult<Void> add(EmailBlastDto emailBlastDto) {
        if (
                StringUtils.hasText(emailBlastDto.sender())
                && emailSenderManager.getSenderByName(emailBlastDto.sender()) == null) {
            return ApiResult.error("email sender doesn't exists");
        }
        var existsEmails = getList();
        emailBlastDto.emails().stream().distinct().forEach(email -> {
            existsEmails.forEach(existsEmail -> {
                if (email.equals(existsEmail.to())) {
                    stringRedisTemplate.opsForStream().delete(REDIS_KEY, existsEmail.id());
                }
            });
            var record = new EmailBlastVo(
                    null,
                    email,
                    emailBlastDto.sender(),
                    emailBlastDto.force(),
                    LocalDateTime.now());
            stringRedisTemplate.opsForStream().add(
                    REDIS_KEY,
                    Map.of("json", JsonUtils.stringify(record)));
        });
        adminLogService.addLog("add_email_blast", emailBlastDto);

        return ApiResult.success();
    }

    @Override
    public ApiResult<Void> delete(String emailBlastId) {
        stringRedisTemplate.opsForStream()
                .delete(REDIS_KEY, emailBlastId);
        return ApiResult.success();
    }

    @Override
    public ApiResult<Void> deleteAll() {
        stringRedisTemplate.delete(REDIS_KEY);
        return ApiResult.success();
    }

    @Override
    public List<EmailBlastVo> getList() {
        var list = stringRedisTemplate
                .opsForStream()
                .range(REDIS_KEY, Range.unbounded());
        if (list == null) {
            return List.of();
        }
        return list.stream()
                .map(item -> {
                    var map = item.getValue();
                    var rec = JsonUtils.parse((String) map.get("json"), EmailBlastVo.class);
                    if (rec != null) {
                        rec = rec.withId(item.getId().getValue());
                    }
                    return rec;
                }).toList();
    }
}
