package my.iris.controller.admin.email;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import my.iris.auth.Authorize;
import my.iris.config.EmailSenderManager;
import my.iris.model.ApiResult;
import my.iris.model.IdStrDto;
import my.iris.model.email.dto.EmailBlastDto;
import my.iris.model.email.vo.EmailBlastVo;
import my.iris.service.email.EmailBlastService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/email/emailBlast", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin email emailBlast", description = "email emailBlast management")
public class EmailBlastController {

    @Resource
    EmailBlastService emailBlastService;
    @Resource
    private EmailSenderManager emailSenderManager;

    @Authorize("/email/emailBlast/add")
    @PostMapping("add")
    public ApiResult<Void> add(@RequestBody @Validated EmailBlastDto emailBlastDto) {
        return emailBlastService.add(emailBlastDto);
    }


    @Authorize("/email/emailBlast/delete")
    @PostMapping("delete")
    public ApiResult<Void> delete(@RequestBody @Validated IdStrDto idStrDto) {
        return emailBlastService.delete(idStrDto.id());
    }
    @Authorize("/email/emailBlast/delete")
    @PostMapping("delete_all")
    public ApiResult<Void> deleteAll() {
        return emailBlastService.deleteAll();
    }

    @Authorize("/email/emailBlast/query")
    @PostMapping("list")
    public ApiResult<List<EmailBlastVo>> list() {
        return ApiResult.success(emailBlastService.getList());
    }

    @Authorize("/email/emailBlast/query")
    @PostMapping("senders")
    public ApiResult<List<String>> getSenders() {
        return ApiResult.success(
                emailSenderManager.getSenderNames()
        );
    }
}
