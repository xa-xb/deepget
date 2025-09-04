package my.iris.controller.api.v1.ai;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.model.ApiResult;
import my.iris.model.ai.dto.AiChatDto;
import my.iris.service.ai.AiChatService;
import my.iris.util.TaskContext;
import my.iris.validation.UUIDv7;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(path = "/api/v1/ai/chat", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "user ai chat", description = "user ai chat controller")
public class ChatController {

    @Resource
    AiChatService chatService;


    @Operation(
            summary = "Post request to prepare response",
            description = """
                    The response returns a chat UUID which can be used to request the final response (e.g., via SSE or polling).
                    Please use it within one minute
                    
                    Example response:
                    ```
                    {
                      "code": 1,
                      "message": "",
                      "data": "12345678-9abc-def0-123456789012"
                    }
                    ```
                    """
    )
    @PostMapping("prepare")
    public ApiResult<String> prepare(@RequestBody @Validated AiChatDto aiChatDto) {
        return chatService.prepare(TaskContext.getUserToken().getUserId(), aiChatDto);
    }


    @Operation(
            summary = "Chat with streaming response by chat uuid",
            description = """
                    Starts a chat session that streams responses using Server-Sent Events (SSE).
                    The response is sent as a series of events in `text/event-stream` format.
                    
                    Example SSE message:
                    ```
                    event: message
                    data: {"m":"hi😀"}
                    
                    event: end
                    data: {"m":""}
                    
                    event: error
                    data: {"m":"error info"}
                    
                    event: thread
                    data: {"uuid":"12345678-9012-3456-0123456789012"}
                    ```
                    """,
            parameters = {
                    @Parameter(
                            name = "uuid",
                            in = ParameterIn.QUERY,
                            description = "UUID (v7) returned from the /prepare endpoint to identify the chat session",
                            required = true,
                            example = "018fa76c-e3d8-77c0-8f45-123456789012"
                    )
            }
    )
    @ApiResponse(
            responseCode = "200",
            description = "Returns chat messages in streaming (SSE)",
            content = {
                    @Content(mediaType = "text/event-stream")
            }
    )

    @GetMapping(value = "stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@UUIDv7 String chatUuid) {
        return chatService.stream(TaskContext.getUserToken().getUserId(), chatUuid);
    }
}
