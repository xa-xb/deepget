package my.iris.service.ai.impl;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.cache.AiCache;
import my.iris.model.ApiResult;
import my.iris.model.IdDto;
import my.iris.model.ai.dto.AiModelDto;
import my.iris.model.ai.entity.AiModelEntity;
import my.iris.model.ai.entity.AiProviderEntity;
import my.iris.model.ai.vo.AiModelVo;
import my.iris.repository.ai.AiModelRepository;
import my.iris.service.ai.AiModelService;
import my.iris.service.system.AdminLogService;
import my.iris.util.DbUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional
public class AiModelServiceImpl implements AiModelService {
    @Resource
    AdminLogService adminLogService;

    @Resource
    AiModelRepository aiModelRepository;

    @Resource
    AiCache aiCache;

    @Override
    public ApiResult<Void> delete(IdDto idDto) {
        aiModelRepository.deleteById(idDto.id());
        adminLogService.addLog("delete model", idDto);
        aiCache.update();
        return ApiResult.success();
    }

    @Override
    public List<AiModelVo> getList() {
        return aiModelRepository.getList();
    }


    @Override
    public ApiResult<Void> save(AiModelDto aiModelDto) {
        boolean isNew = aiModelDto.getId() == null;
        AiModelEntity aiModelEntity = isNew
                ? new AiModelEntity()
                : DbUtils.findByIdForUpdate(AiModelEntity.class, aiModelDto.getId());
        if (aiModelEntity == null) {
            return ApiResult.error("模型不存在");
        }
        var provider = DbUtils.findByIdForUpdate(AiProviderEntity.class, aiModelDto.getProviderId());
        if (provider == null) {
            return ApiResult.error("供应商不存在");
        }
        aiModelEntity
                .setName(aiModelDto.getName())
                .setSysName(aiModelDto.getSysName())
                .setOpenRouterName(aiModelDto.getOpenRouterName())
                .setProvider(provider)
                .setCnyPerToken(aiModelDto.getCnyPerToken())
                .setFree(aiModelDto.getFree())
                .setOrderNum(aiModelDto.getOrderNum())
                .setEnabled(aiModelDto.getEnabled())
                .setIntro(aiModelDto.getIntro());
        if (!StringUtils.hasText(aiModelEntity.getSysName())) {
            aiModelEntity.setSysName(aiModelEntity.getName());
        }
        aiModelRepository.save(aiModelEntity);
        adminLogService.addLog((isNew ? "add" : "edit") + " ai model", aiModelDto);
        aiCache.update();
        return ApiResult.success();
    }
}
