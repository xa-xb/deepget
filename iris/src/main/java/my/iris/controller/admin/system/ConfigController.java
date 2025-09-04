package my.iris.controller.admin.system;

import io.swagger.v3.oas.annotations.tags.Tag;
import my.iris.auth.Authorize;
import my.iris.model.ApiResult;
import my.iris.model.system.dto.ConfigDto;
import my.iris.model.system.dto.SmtpServerDto;
import my.iris.model.system.dto.SmtpTestDto;
import my.iris.service.system.EmailService;
import my.iris.service.system.SystemService;
import my.iris.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/system/config", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin system config", description = "admin system config controller")
public class ConfigController {

    @Resource
    EmailService emailService;

    @Resource
    SystemService systemService;

    @Authorize("/system/config/save")
    @Operation(
            summary = "刷新 RSA 密钥对",
            description = "重新生成并保存新的 RSA 密钥对"
    )
    @PostMapping("/refresh_rsa_key")
    public ApiResult<Void> refreshRsaKey() {
        return systemService.refreshRsaKey();
    }

    @Authorize("/system/config/save")
    @Operation(summary = "保存系统配置", description = "smtpServers[0].password需要RSA加密")
    @PostMapping("save")
    public ApiResult<Void> save(@RequestBody @Validated ConfigDto configDto) {
        var msg = decodeSmtpServers(configDto.getSmtpServers());
        if (msg != null) {
            return ApiResult.error(msg);
        }
        return systemService.saveConfig(configDto);
    }

    @Authorize("/system/config/query")
    @PostMapping("")
    public ApiResult<ConfigDto> query() {
        return ApiResult.success(systemService.getConfig());
    }

    @Authorize("/system/config/edit")
    @Operation(summary = "发送测试邮件", description = "smtpServers[0].password需要RSA加密")
    @PostMapping("test_email")
    public ApiResult<Void> testEmail(@RequestBody @Validated SmtpTestDto smtpTestDto) {
        var msg = decodeSmtpServers(smtpTestDto.smtpServers());
        if (msg != null) {
            return ApiResult.error(msg);
        }
        return emailService.testMail(smtpTestDto);
    }

    private String decodeSmtpServers(List<SmtpServerDto> smtpServers) {
        for (int i = 0; i < smtpServers.size(); i++) {
            var item = smtpServers.get(i);
            item.setPassword(SecurityUtils.rsaDecrypt(item.getPassword()));
            if (item.getPassword() == null) {
                return String.format("smtpServers[%d].password 需要RSA加密后传输", i);
            }
        }
        return null;
    }

}
