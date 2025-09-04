package my.iris.controller.api.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.model.ApiResult;
import my.iris.model.QueryDto;
import my.iris.service.user.SettingService;
import my.iris.util.TaskContext;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * user settings
 */
@RestController
@RequestMapping(path = "/api/v1/setting", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "user setting controller", description = "user setting controller")
public class SettingController {

    @Resource
    SettingService settingService;

    @Operation(summary = "bind email", description = "required fields: code, email")
    @PostMapping("bind_email")
    public ApiResult<Void> bindEmail(@RequestBody @Validated QueryDto queryDto) {
        return settingService.bindEmail(
                TaskContext.getUserToken().getUserId(),
                queryDto.email(),
                queryDto.code()
        );
    }
}
