package my.iris.controller.api.v1.ai;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.auth.UserToken;
import my.iris.model.ApiResult;
import my.iris.model.UuidDto;
import my.iris.model.ai.dto.AiThreadRenameDto;
import my.iris.model.ai.vo.AiChatClientVo;
import my.iris.model.ai.vo.AiThreadClientVo;
import my.iris.service.ai.AiThreadService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/ai/thread", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "user ai thread", description = "user ai thread controller")
public class ThreadController {
    @Resource
    AiThreadService aiThreadService;

    @Operation(summary = "delete thread", description = "delete thread")
    @PostMapping("delete")
    public ApiResult<Void> delete(UserToken userToken,
                                        @RequestBody @Validated UuidDto uuidDto) {
        return aiThreadService.delete(UUID.fromString(uuidDto.getUuid()), userToken.getUserId());
    }

    @Operation(summary = "delete all threads", description = "delete all threads")
    @PostMapping("delete_all")
    public ApiResult<Void> deleteAll(UserToken userToken) {
        return aiThreadService.deleteAll(userToken.getUserId());
    }


    @Operation(summary = "get chat history for thread", description = "chat history")
    @PostMapping("get")
    public ApiResult<List<AiChatClientVo>> get(UserToken userToken,
                                                     @RequestBody @Validated UuidDto uuidDto) {
        return aiThreadService.get(UUID.fromString(uuidDto.getUuid()), userToken.getUserId());
    }

    @Operation(summary = "list threads", description = "list threads")
    @PostMapping("list")
    public ApiResult<List<AiThreadClientVo>> list(UserToken userToken) {
        return ApiResult.success(aiThreadService.listClientThreads(userToken.getUserId()));
    }

    @Operation(summary = "rename thread", description = "rename thread")
    @PostMapping("rename")
    public ApiResult<Void> rename(UserToken userToken,
                                        @RequestBody @Validated AiThreadRenameDto aiThreadRenameDto) {
        return aiThreadService.rename(
                UUID.fromString(aiThreadRenameDto.getUuid()),
                aiThreadRenameDto.getNewName(),
                userToken.getUserId());
    }
}
