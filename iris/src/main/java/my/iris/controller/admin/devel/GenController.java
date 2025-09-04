package my.iris.controller.admin.devel;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.devel.dto.GenDto;
import my.iris.model.devel.vo.GenVo;
import my.iris.service.devel.GenService;
import my.iris.util.CaptchaUtils;
import my.iris.util.ValidatorUtils;
import my.iris.validation.groups.Add;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin/devel/gen", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin development generate code", description = "generate code")
public class GenController {
    @Resource
    GenService genService;

    @Authorize("/devel/gen/query")
    @PostMapping("list")
    public ApiResult<GenVo> list() {
        return genService.list();
    }

    @Authorize("/devel/gen/add")
    @PostMapping("add")
    public ApiResult<String> add(@RequestBody @Validated(Add.class) GenDto genDto) {
        var msg = ValidatorUtils.validateCaptcha(genDto.captcha());
        if (msg == null) {
            CaptchaUtils.clear();
        } else {
            return ApiResult.badRequest(msg);
        }
        return genService.add(genDto);
    }
}
