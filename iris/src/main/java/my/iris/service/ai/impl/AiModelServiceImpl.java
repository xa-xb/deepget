package my.iris.service.ai.impl;

import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.cache.AiCache;
import my.iris.model.ApiResult;
import my.iris.model.IdDto;
import my.iris.model.ai.dto.AiModelDto;
import my.iris.model.ai.dto.AiModelQueryDto;
import my.iris.model.ai.entity.AiChatEntity;
import my.iris.model.ai.entity.AiModelEntity;
import my.iris.model.ai.entity.AiProviderEntity;
import my.iris.model.ai.vo.AiModelVo;
import my.iris.repository.ai.AiChatRepository;
import my.iris.repository.ai.AiModelRepository;
import my.iris.service.ai.AiModelService;
import my.iris.service.system.AdminLogService;
import my.iris.util.DbUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    @Resource
    private AiChatRepository aiChatRepository;

    @Override
    public ApiResult<Void> delete(IdDto idDto) {
        if (aiChatRepository.exists(Example.of(new AiChatEntity().setModelId(idDto.id())))) {
            return ApiResult.error("Please delete the chat history for this model.");
        }
        aiModelRepository.deleteById(idDto.id());
        adminLogService.addLog("delete_model", idDto);
        aiCache.update();
        return ApiResult.success();
    }


    @Override
    public Page<AiModelVo> getPage(AiModelQueryDto queryDto) {
        var pageRequest = PageRequest.of(
                queryDto.getPage() - 1,
                queryDto.getPageSize());
        return aiModelRepository.getPage(pageRequest, DbUtils.getLikeWords(queryDto.getName()),
                queryDto.getProviderId(), queryDto.getEnabled());
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
        adminLogService.addLog((isNew ? "add" : "edit") + "_ai_model", aiModelDto);
        aiCache.update();
        return ApiResult.success();
    }
}
