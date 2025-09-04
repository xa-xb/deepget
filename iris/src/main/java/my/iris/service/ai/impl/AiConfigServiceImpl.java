package my.iris.service.ai.impl;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.cache.AiCache;
import my.iris.model.ApiResult;
import my.iris.model.ai.OpenRouterConfig;
import my.iris.model.ai.dto.AiConfigDto;
import my.iris.repository.system.SystemRepository;
import my.iris.service.ai.AiConfigService;
import my.iris.service.system.AdminLogService;
import my.iris.util.Helper;
import my.iris.util.JsonUtils;
import my.iris.util.SecurityUtils;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AiConfigServiceImpl implements AiConfigService {

    @Resource
    AdminLogService adminLogService;

    @Resource
    SystemRepository systemRepository;
    @Resource
    private AiCache aiCache;

    @Override
    public AiConfigDto get() {
        return aiCache.getAiConfigDto();
    }

    @Override
    public ApiResult<Void> save(AiConfigDto aiConfigDto) {
        systemRepository.updateValueByEa(aiConfigDto.getMemoryCount(), "ai", "memory_count");
        var str = SecurityUtils.aesEncrypt(JsonUtils.stringify(aiConfigDto.getOpenRouterConfig()));
        systemRepository.updateValueByEa(str, "ai", "open_router_config");
        adminLogService.addLog(("edit") + " ai aiConfig", aiConfigDto);
        aiCache.update();
        return ApiResult.success();
    }
}
