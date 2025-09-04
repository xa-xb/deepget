package my.iris.controller.admin.system;

import io.swagger.v3.oas.annotations.tags.Tag;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.system.vo.SystemInfoVo;
import my.iris.service.system.SystemService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/system/info", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin system info", description = "admin system info controller")
public class InfoController {

    @Resource
    SystemService systemService;

    @Authorize("/system/info/query")
    @PostMapping(value = "get")
    public ApiResult<SystemInfoVo> get() {
        return ApiResult.success(systemService.getInfo());
    }


}
