package my.iris.service.system.impl;

import my.iris.service.system.PaymentService;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import my.iris.cache.PaymentCache;
import my.iris.model.system.entity.PaymentEntity;
import my.iris.repository.system.PaymentRepository;
import my.iris.util.JsonUtils;
import my.iris.util.SecurityUtils;
import my.iris.util.ValidatorUtils;
import my.iris.validation.groups.Edit;

import java.sql.Timestamp;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    @Resource
    PaymentCache paymentCache;
    @Resource
    PaymentRepository paymentRepository;

    /**
     * @param jsonStr like {"name":"alipay","enable":true,"orderNum":9, "xxx": "xxx"...}
     * @return null if success, or error msg
     */
    public String save(String jsonStr) {
        var postEntity = JsonUtils.parse(jsonStr, PaymentEntity.class);
        if (postEntity == null) {
            return "数据错误";
        }
        var msg = ValidatorUtils.validate(postEntity, Edit.class);
        if (msg != null) {
            return msg;
        }
        var entity = paymentRepository.findByNameForWrite(postEntity.getName());
        var payment = paymentCache.getPaymentByName(postEntity.getName());
        if (entity == null || payment == null) {
            return "支付方式不存在, name:" + postEntity.getName();
        }
        var configObj = payment.newConfigInstance(jsonStr);
        msg = ValidatorUtils.validate(configObj);
        if (msg != null) {
            return msg;
        }
        var configJson = JsonUtils.stringify(configObj);
        if (configJson == null) {
            configJson = "{}";
        }
        entity.setConfig(SecurityUtils.aesEncrypt(configJson));
        entity.setEnable(postEntity.getEnable());
        entity.setOrderNum(postEntity.getOrderNum());
        entity.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        paymentRepository.saveAndFlush(entity);
        paymentCache.update();
        return null;
    }
}
