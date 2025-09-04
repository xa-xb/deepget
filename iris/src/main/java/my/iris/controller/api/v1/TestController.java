package my.iris.controller.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import my.iris.model.ApiResult;
import my.iris.util.Helper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

@RestController
@RequestMapping(path = "/api/v1/test", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "user test", description = "user test Controller, no authentication required for this controller")
public class TestController {

    @Operation(summary = "request headers", description = "get request headers for debug")
    @RequestMapping(value = "header", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult<Map<String, Object>> header(HttpServletRequest request) {
        Map<String, Object> map = new LinkedHashMap<>();
        var names = request.getHeaderNames();
        while (names.hasMoreElements()) {
            var name = names.nextElement();
            var values = Collections.list(request.getHeaders(name));
            map.put(name, String.join(", ", values));
        }
        map.put("x-client-ip", Helper.getClientIp(request));
        return ApiResult.success(map);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "event stream",
                    content = @Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE))
    })
    @Operation(summary = "server send event", description = """
            Server-Sent Events: send one message per second until the target count is reached.
            
            Example response:
            ```
            event: message
            data:{"id":1,"message":"xx"}
            
            event: message
            data:{"id":2,"message":"xx"}
            
            ...
            
            event: end
            data: {"message":""}
            ```
            """)
    @GetMapping(value = "sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getSseEmitter(
            @Parameter(description = "Number of messages to send (maximum 60)", example = "10", required = true)
            @RequestParam(defaultValue = "10")
            @Max(60)
            Long num) {
        SseEmitter emitter = new SseEmitter(80_000L);
        emitter.onTimeout(emitter::complete);

        Thread.startVirtualThread(() -> {
            try {
                for (long i = 1; i <= num; i++) {
                    emitter.send(Map.of(
                            "id", i,
                            "message", Helper.randomString(4)
                    ));
                    LockSupport.parkNanos(1_000_000_000L);
                }
                emitter.send(SseEmitter.event().name("end").data(Map.of("message", "")));
                emitter.complete();
            } catch (Exception ignored) {
            }
        });
        return emitter;
    }

}
