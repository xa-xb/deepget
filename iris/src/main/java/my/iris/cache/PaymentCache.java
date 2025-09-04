package my.iris.cache;

import my.iris.repository.system.PaymentRepository;
import my.iris.util.LogUtils;
import my.iris.util.SecurityUtils;
import my.iris.util.payment.Balance;
import my.iris.util.payment.Payment;
import my.iris.util.payment.WeChat;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

@Component
public class PaymentCache {

    private final PaymentRepository paymentRepository;
    private LinkedHashMap<String, Payment> payments;

    public PaymentCache(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    /**
     * 初始化
     */
    @PostConstruct
    public synchronized void update() {
        LinkedHashMap<String, Payment> map = new LinkedHashMap<>();
        paymentRepository.findAllByOrderByOrderNum().forEach(row -> {
            String config = SecurityUtils.aesDecrypt(row.getConfig());
            Payment payment = null;
            switch (row.getName()) {
                case Balance.NAME -> payment = new Balance(row.getNameCn(), row.getEnable());
                case WeChat.NAME -> payment = new WeChat(row.getNameCn(), row.getEnable());
                default -> LogUtils.error(PaymentCache.class, "unknown payment, name: " + row.getName());
            }

            if (payment != null) {
                payment.setConfig(config);
                map.put(payment.getName(), payment);
            }

        });
        payments = map;
    }

    /**
     * 根据支付名称获取支付方式
     *
     * @param name 支付名称(英文)
     * @return 支付方式
     */
    public Payment getPaymentByName(String name) {
        return payments.get(name);
    }

    public List<String> getAvailableNames() {
        return payments.values().stream().filter(Payment::getEnable).map(Payment::getName).toList();
    }

    public List<Payment> getPayments() {
        return payments.values().stream().toList();
    }

    public List<Payment> getAvailablePayments() {
        return payments.values().stream().filter(Payment::getEnable).toList();
    }

}
