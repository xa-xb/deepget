package my.iris.service.email;

import my.iris.model.ApiResult;
import my.iris.model.email.dto.EmailBlastDto;
import my.iris.model.email.vo.EmailBlastVo;

import java.util.List;

public interface EmailBlastService {
    String REDIS_KEY = "email_blast";

    ApiResult<Void> add(EmailBlastDto emailBlastDto);
    ApiResult<Void> delete(String emailBlastId);
    ApiResult<Void> deleteAll();
    List<EmailBlastVo> getList();
}