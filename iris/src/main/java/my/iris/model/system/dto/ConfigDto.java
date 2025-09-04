package my.iris.model.system.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;
import my.iris.model.CloneAndSanitize;
import my.iris.util.JsonUtils;

import java.util.List;

@Accessors(chain = true)
@Data
public class ConfigDto implements CloneAndSanitize<ConfigDto> {
    @NotNull
    String icpNumber;

    @NotNull
    String frontendPath;

    @NotNull
    String siteName;

    @NotNull
    String siteUrl;

    @Min(0)
    @NotNull
    Integer dailyEmailLimit;

    @Min(0)
    @NotNull
    Integer dailySmsLimit;

    @NotNull
    String realIpHeader;

    @NotNull
    @Valid
    List<SmtpServerDto> smtpServers;

    @Override
    public ConfigDto cloneAndSanitize() {
        // 移除敏感信息
        var clone = JsonUtils.clone(this);
        for (var smtpServerDto : clone.smtpServers) {
            smtpServerDto.setPassword("******");
        }
        return clone;
    }

    public ConfigDto setRealIpHeader(String realIpHeader) {
        if (realIpHeader != null) this.realIpHeader = realIpHeader.trim();
        return this;
    }
}
