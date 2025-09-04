package my.iris.service.devel;

import my.iris.model.ApiResult;
import my.iris.model.devel.dto.GenDto;
import my.iris.model.devel.vo.GenVo;

public interface GenService {
    ApiResult<GenVo> list();
    ApiResult<String> add(GenDto genDto);
}
