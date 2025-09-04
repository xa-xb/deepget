package my.iris.service.system;

import my.iris.model.ApiResult;
import my.iris.model.RsaPubKeyVo;
import my.iris.model.system.dto.ConfigDto;
import my.iris.model.system.vo.SystemInfoVo;

public interface SystemService {
    String getBuildTime();
    ConfigDto getConfig();

    /**
     * refresh rsa key
     */
    ApiResult<Void> refreshRsaKey();
    ApiResult<Void> saveConfig(ConfigDto config);
    SystemInfoVo getInfo();

    ApiResult<RsaPubKeyVo> getRsaPubKey();
}