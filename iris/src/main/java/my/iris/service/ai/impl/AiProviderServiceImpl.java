package my.iris.service.ai.impl;

import dev.langchain4j.model.ModelProvider;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import my.iris.cache.AiCache;
import my.iris.model.ApiResult;
import my.iris.model.IdDto;
import my.iris.model.ai.dto.AiProviderDto;
import my.iris.model.ai.entity.AiModelEntity;
import my.iris.model.ai.entity.AiProviderEntity;
import my.iris.model.ai.vo.AiProviderVo;
import my.iris.repository.ai.AiModelRepository;
import my.iris.repository.ai.AiProviderRepository;
import my.iris.service.ai.AiProviderService;
import my.iris.service.system.AdminLogService;
import my.iris.service.system.UploadService;
import my.iris.util.DbUtils;
import my.iris.util.SecurityUtils;
import my.iris.util.ValidatorUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Transactional
public class AiProviderServiceImpl implements AiProviderService {
    @Resource
    AdminLogService adminLogService;

    @Resource
    AiProviderRepository aiProviderRepository;

    @Resource
    private AiModelRepository aiModelRepository;

    @Resource
    AiCache aiCache;

    @Resource
    UploadService uploadService;

    @Override
    public ApiResult<Void> delete(IdDto idDto) {
        var provider = DbUtils.findByIdForUpdate(AiProviderEntity.class, idDto.id());
        if (provider == null) {
            return ApiResult.error("数据不存在, id: " + idDto.id());
        }
        var example = Example.of(new AiModelEntity().setProvider(provider));
        if (aiModelRepository.exists(example)) {
            return ApiResult.error("请先删除该供应商下的模型");
        }
        aiProviderRepository.deleteById(idDto.id());
        adminLogService.addLog("delete ai provider", idDto);
        aiCache.update();
        return ApiResult.success();
    }

    @Override
    public List<String> getApiCompatibles() {
        List<String> apiCompatibles = new LinkedList<>();
        for (var provider : ModelProvider.values()) {
            apiCompatibles.add(provider.name());
        }
        return apiCompatibles;
    }

    @Override
    public List<AiProviderVo> getList() {
        return aiProviderRepository.getList();
    }

    @Override
    public ApiResult<Void> save(AiProviderDto aiProviderDto) {
        var msg = ValidatorUtils.validateSvg(aiProviderDto.getIconSvg());
        if (msg != null) {
            return ApiResult.badRequest(msg);
        }
        boolean isNew = aiProviderDto.getId() == null;
        var entity = isNew
                ? new AiProviderEntity()
                : DbUtils.findByIdForUpdate(AiProviderEntity.class, aiProviderDto.getId());
        if (entity == null) {
            return ApiResult.error("数据不存在, id: " + aiProviderDto.getId());
        }
        entity.setName(aiProviderDto.getName())
                .setIconSvg(aiProviderDto.getIconSvg())
                .setApiCompatible(aiProviderDto.getApiCompatible())
                .setUrl(aiProviderDto.getUrl())
                .setOpenRouter(aiProviderDto.getOpenRouter())
                .setApiKey(SecurityUtils.aesEncrypt(aiProviderDto.getApiKey()))
                .setApiUrl(aiProviderDto.getApiUrl())
                .setOrderNum(aiProviderDto.getOrderNum());
        aiProviderRepository.save(entity);
        adminLogService.addLog((isNew ? "add" : "edit") + " ai provider", aiProviderDto);
        aiCache.update();
        return ApiResult.success();
    }

    @Override
    public ApiResult<String> upload(MultipartFile file) {
        var msg = uploadService.checkFile(file, 0, true);
        if (msg != null) {
            return ApiResult.badRequest(msg);
        }
        var uploadResult = uploadService.upload(file, false);
        if (uploadResult.uri() != null) {
            adminLogService.addLog("upload",
                    Map.of("a", Objects.requireNonNull(file.getOriginalFilename()),
                    "size", file.getSize(),
                    "uri", uploadResult.uri())
            );
            return ApiResult.success(uploadResult.uri());
        } else {
            return ApiResult.error(uploadResult.error());
        }
    }
}
