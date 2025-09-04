package my.iris.service.user;

import my.iris.model.ApiResult;

public interface SettingService {
    ApiResult<Void> bindEmail(Long userId, String email, String code);
}
