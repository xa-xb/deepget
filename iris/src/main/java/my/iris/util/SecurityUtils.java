package my.iris.util;

import my.iris.cache.SystemCache;
import my.iris.config.AppConfig;
import my.iris.config.AppProperties;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.crypto.encrypt.AesBytesEncryptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static my.iris.util.Helper.bytesToHex;

/**
 * 安全助手函数,提供加密解密、hash等功能.
 * AES使用密钥轮换策略，方便系统更换密钥，当新密钥存在时，会同时使用新旧密钥进行解密，以兼容旧数据.
 */
@Component
public final class SecurityUtils {
    static AesBytesEncryptor AES_BYTES_ENCRYPTOR;
    static AesBytesEncryptor AES_BYTES_ENCRYPTOR_NEW;
    static SystemCache systemCache;


    public SecurityUtils(AppProperties appProperties) {
        if (Strings.isEmpty(appProperties.getKey())) {
            LogUtils.error(this.getClass(), "need config: app.key");
        }
        AES_BYTES_ENCRYPTOR = new AesBytesEncryptor(appProperties.getKey(), SecurityUtils.sha3_256(appProperties.getKey()));
        if (StringUtils.hasText(appProperties.getNewKey())) {
            AES_BYTES_ENCRYPTOR_NEW = new AesBytesEncryptor(appProperties.getNewKey(), SecurityUtils.sha3_256(appProperties.getNewKey()));
        }
    }
    @PostConstruct
    private void init(){
        systemCache = AppConfig.getContext().getBean(SystemCache.class);
    }

    public static String aesDecrypt(String encryptedText) {
        try {
            return new String(AES_BYTES_ENCRYPTOR.decrypt(Base64.getDecoder().decode(encryptedText)));
        } catch (Exception e) {
            if (AES_BYTES_ENCRYPTOR_NEW != null) {
                try {
                    return new String(AES_BYTES_ENCRYPTOR_NEW.decrypt(Base64.getDecoder().decode(encryptedText)));
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    public static String aesEncrypt(String text) {
        if (AES_BYTES_ENCRYPTOR_NEW != null) {
            return Base64.getEncoder().encodeToString(AES_BYTES_ENCRYPTOR_NEW.encrypt(text.getBytes()));
        }
        return Base64.getEncoder().encodeToString(AES_BYTES_ENCRYPTOR.encrypt(text.getBytes()));
    }

    /**
     * 计算字符串的hash
     *
     * @param algorithm hash算法,如：MD5 SHA-256
     * @param text      待计算hash值的字符串
     * @return hash值
     */
    public static String hash(String algorithm, String text) {
        try {
            byte[] bytes = MessageDigest.getInstance(algorithm).digest(text.getBytes());
            return bytesToHex(bytes);
        } catch (NoSuchAlgorithmException ex) {
            LogUtils.error(SecurityUtils.class, "", ex);
            return null;
        }
    }

    /**
     * 计算流的hash
     *
     * @param algorithm   hash算法,如：MD5 SHA-256
     * @param inputStream 待计算hash值的流
     * @return hash值
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public static String hash(String algorithm, InputStream inputStream) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            DigestInputStream dis = new DigestInputStream(inputStream, md);
            byte[] buffer = new byte[10240];
            while (dis.read(buffer) > -1) {
            }
            String hash = bytesToHex(dis.getMessageDigest().digest());
            dis.close();
            return hash;
        } catch (Exception ex) {
            LogUtils.error(SecurityUtils.class, "", ex);
            return null;
        }
    }


    public static String rsaDecrypt(String encryptedText) {
        try {
            return RsaUtils.decryptByPrivateKey(encryptedText, systemCache.getRsaPrivateKey());
        } catch (Exception e) {
            LogUtils.error(SecurityUtils.class,  e);
            return null;
        }
    }

    public static String rsaEncrypt(String plainText) {
        try {
            return RsaUtils.encryptByPublicKey(plainText, systemCache.getRsaPublicKey());
        } catch (GeneralSecurityException e) {
            LogUtils.error(SecurityUtils.class,  e);
            return null;
        }

    }

    /**
     * 字符串的sha256哈希
     *
     * @param text 待计算hash值的字符串
     * @return hash
     */
    public static String sha256(String text) {
        return hash("SHA-256", text);
    }

    /**
     * 计算流的sha256哈希值
     *
     * @param inputStream 待计算hash值的流
     * @return hash
     */
    public static String sha256(InputStream inputStream) {
        return hash("SHA-256", inputStream);
    }

    /**
     * 字符串的sha3-256哈希
     *
     * @param text 待计算hash值的字符串
     * @return hash
     */
    public static String sha3_256(String text) {
        return hash("SHA3-256", text);
    }

    /**
     * 计算流的sha3-256哈希值
     *
     * @param inputStream 待计算hash值的流
     * @return hash
     */
    public static String sha3_256(InputStream inputStream) {
        return hash("SHA3-256", inputStream);
    }


}
