package my.iris.controller.admin.system;

import io.swagger.v3.oas.annotations.tags.Tag;
import my.iris.auth.Authorize;
import my.iris.cache.PaymentCache;
import my.iris.model.ApiResult;
import my.iris.model.QueryDto;
import my.iris.model.system.entity.PaymentEntity;
import my.iris.repository.system.PaymentRepository;
import my.iris.service.system.AdminLogService;
import my.iris.service.system.PaymentService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/system/payment", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "admin system payment", description = "admin system payment controller")
public class PaymentController {

    @Resource
    AdminLogService adminLogService;
    @Resource
    PaymentCache paymentCache;
    @Resource
    PaymentService paymentService;

    @Resource
    PaymentRepository paymentRepository;


    @Authorize("/system/payment/edit")
    @PostMapping("edit")
    public ApiResult<Void> edit(@RequestBody String jsonStr) {
       return null;
    }

    @Authorize("/system/payment/edit")
    @PostMapping("get")
    public ApiResult<?> get(@RequestBody QueryDto query) {
      return null;
    }

    @Authorize("/system/payment/query")
    @PostMapping("list")
    public ApiResult<List<PaymentEntity>> list() {
        return ApiResult.success(paymentRepository.findAllByOrderByOrderNum());
    }
}
