package my.iris.cache;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import my.iris.model.ai.OpenRouterConfig;
import my.iris.model.ai.dto.AiConfigDto;
import my.iris.model.ai.vo.AiModelClientVo;
import my.iris.model.ai.vo.AiModelVo;
import my.iris.model.ai.vo.AiProviderVo;
import my.iris.repository.ai.AiModelRepository;
import my.iris.repository.ai.AiProviderRepository;
import my.iris.repository.system.SystemRepository;
import my.iris.util.Helper;
import my.iris.util.JsonUtils;
import my.iris.util.SecurityUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;

@Component
public class AiCache {
    private final AiProviderRepository aiProviderRepository;
    private final AiModelRepository aiModelRepository;
    private final SystemRepository systemRepository;

    @Getter
    AiConfigDto aiConfigDto;
    List<AiModelVo> aiModelVos;
    @Getter
    List<AiModelClientVo> aiModelClientVos;
    List<AiProviderVo> aiProviderVos;


    public AiCache(AiProviderRepository aiProviderRepository, AiModelRepository aiModelRepository, SystemRepository systemRepository) {
        this.aiProviderRepository = aiProviderRepository;
        this.aiModelRepository = aiModelRepository;
        this.systemRepository = systemRepository;
    }

    @PostConstruct
    public synchronized void update() {
        updateAiConfig();
        aiProviderVos = aiProviderRepository.getList();
        this.aiModelVos = aiModelRepository.getList().stream().filter(AiModelVo::enabled).toList();
        this.aiModelClientVos = aiModelVos.stream()
                .map(aiModelVo -> new AiModelClientVo(
                        aiModelVo.id(),
                        aiModelVo.name(),
                        aiModelVo.iconSvg(),
                        aiModelVo.providerId(),
                        aiModelVo.providerName(),
                        aiModelVo.intro(),
                        aiModelVo.createdAt()))
                .toList();
    }

    private void updateAiConfig() {
        AiConfigDto aiConfigDto = new AiConfigDto();
        systemRepository.findFirstByEntityAndAttribute("ai", "memory_count")
                .ifPresent(entity -> {
                    aiConfigDto.setMemoryCount(Helper.parseNumber(entity.getValue(), Long.class));
                    if (aiConfigDto.getMemoryCount() < 0) aiConfigDto.setMemoryCount(0L);
                });
        systemRepository.findFirstByEntityAndAttribute("ai", "open_router_config")
                .ifPresent(entity -> {
                    var str = SecurityUtils.aesDecrypt(entity.getValue());
                    aiConfigDto.setOpenRouterConfig(JsonUtils.parse(str, OpenRouterConfig.class));
                    if (aiConfigDto.getOpenRouterConfig() == null)
                        aiConfigDto.setOpenRouterConfig(new OpenRouterConfig("", ""));
                });

        this.aiConfigDto = aiConfigDto;
    }


    public ModelInstance createModelInstanceById(Long modelId) {
        var aiModelVo = aiModelVos.stream()
                .filter(item -> item.id().equals(modelId))
                .findFirst()
                .orElse(null);
        var aiProviderVo = aiProviderVos.stream()
                .filter(item -> item.id().equals(aiModelVo.providerId()))
                .findFirst()
                .orElse(null);
        if (aiModelVo == null || aiProviderVo == null) return null;

        var builder = OpenAiStreamingChatModel.builder();
        if (aiProviderVo.openRouter()) {
            builder.apiKey(aiConfigDto.getOpenRouterConfig().apiKey())
                    .baseUrl(aiConfigDto.getOpenRouterConfig().apiUrl());
        } else {
            builder.apiKey(aiProviderVo.apiKey())
                    .baseUrl(aiProviderVo.url());
        }
        String modelName = aiProviderVo.openRouter() ? aiModelVo.openRouterName() : aiModelVo.sysName();
        return new ModelInstance(builder.build(), modelName);

    }


    public record ModelInstance(StreamingChatModel chatModel, String modelName) {
    }

}
