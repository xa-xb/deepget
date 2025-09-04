package my.iris.util;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class RsaUtils {

    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final int MAX_ENCRYPT_BLOCK = 245; // 2048位RSA，最大加密块大小 = 256 - 11
    private static final int MAX_DECRYPT_BLOCK = 256; // 2048位RSA，最大解密块大小 = 256

    private RsaUtils() {
    }

    public static RsaKeyPair initKeyPair() {
        try {
            var keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
            keyPairGen.initialize(2048);
            var keyPair = keyPairGen.generateKeyPair();

            return new RsaKeyPair(base64Encode(keyPair.getPublic().getEncoded()),
                    base64Encode(keyPair.getPrivate().getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            LogUtils.error(RsaUtils.class, "RsaUtils.initKeyPair()", e);
            return null;
        }
    }

    public static String encryptByPublicKey(String data, String base64PublicKey) throws GeneralSecurityException {
        if (data == null || base64PublicKey == null) throw new IllegalArgumentException("参数不能为空");
        var publicKey = getPublicKeyFromBase64(base64PublicKey);
        return encrypt(data.getBytes(StandardCharsets.UTF_8), publicKey, MAX_ENCRYPT_BLOCK);
    }

    public static String decryptByPrivateKey(String base64EncryptedData, String base64PrivateKey) throws GeneralSecurityException {
        if (base64EncryptedData == null || base64PrivateKey == null) throw new IllegalArgumentException("参数不能为空");
        var privateKey = getPrivateKeyFromBase64(base64PrivateKey);
        var encryptedData = base64Decode(base64EncryptedData);
        var decrypted = decrypt(encryptedData, privateKey, MAX_DECRYPT_BLOCK);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static String encryptByPrivateKey(String data, String base64PrivateKey) throws GeneralSecurityException {
        if (data == null || base64PrivateKey == null) throw new IllegalArgumentException("参数不能为空");
        var privateKey = getPrivateKeyFromBase64(base64PrivateKey);
        return encrypt(data.getBytes(StandardCharsets.UTF_8), privateKey, MAX_ENCRYPT_BLOCK);
    }

    public static String decryptByPublicKey(String base64EncryptedData, String base64PublicKey) throws GeneralSecurityException {
        if (base64EncryptedData == null || base64PublicKey == null) throw new IllegalArgumentException("参数不能为空");
        var publicKey = getPublicKeyFromBase64(base64PublicKey);
        var encryptedData = base64Decode(base64EncryptedData);
        var decrypted = decrypt(encryptedData, publicKey, MAX_DECRYPT_BLOCK);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static String sign(String data, String base64PrivateKey) throws GeneralSecurityException {
        if (data == null || base64PrivateKey == null) throw new IllegalArgumentException("参数不能为空");
        var privateKey = getPrivateKeyFromBase64(base64PrivateKey);
        var signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        var signBytes = signature.sign();
        return base64Encode(signBytes);
    }

    public static boolean verify(String data, String base64PublicKey, String base64Sign) throws GeneralSecurityException {
        if (data == null || base64PublicKey == null || base64Sign == null)
            throw new IllegalArgumentException("参数不能为空");
        var publicKey = getPublicKeyFromBase64(base64PublicKey);
        var signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        var signBytes = base64Decode(base64Sign);
        return signature.verify(signBytes);
    }

    private static String encrypt(byte[] data, Key key, int maxBlock) throws GeneralSecurityException {
        var cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        var encrypted = segmentData(data, cipher, maxBlock);
        return base64Encode(encrypted);
    }

    private static byte[] decrypt(byte[] encryptedData, Key key, int maxBlock) throws GeneralSecurityException {
        var cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return segmentData(encryptedData, cipher, maxBlock);
    }

    private static byte[] segmentData(byte[] data, Cipher cipher, int maxBlock) throws GeneralSecurityException {
        try (var out = new ByteArrayOutputStream()) {
            var inputLen = data.length;
            var offset = 0;
            while (inputLen - offset > 0) {
                var blockLen = Math.min(maxBlock, inputLen - offset);
                var processed = cipher.doFinal(data, offset, blockLen);
                out.write(processed);
                offset += blockLen;
            }
            return out.toByteArray();
        } catch (Exception e) {
            throw new GeneralSecurityException("RSA 分段处理失败", e);
        }
    }

    private static PublicKey getPublicKeyFromBase64(String base64Key) throws GeneralSecurityException {
        var keyBytes = base64Decode(base64Key);
        var keySpec = new X509EncodedKeySpec(keyBytes);
        var keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    private static PrivateKey getPrivateKeyFromBase64(String base64Key) throws GeneralSecurityException {
        var keyBytes = base64Decode(base64Key);
        var keySpec = new PKCS8EncodedKeySpec(keyBytes);
        var keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    private static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private static byte[] base64Decode(String base64Str) {
        return Base64.getDecoder().decode(base64Str);
    }

    public static record RsaKeyPair(String publicKey, String privateKey) {
    }

    private static void sample(String[] args) throws Exception {
        var keyPair = initKeyPair();
        var publicKey = keyPair.publicKey();
        var privateKey = keyPair.privateKey();

        System.out.println("公钥(Base64): " + publicKey);
        System.out.println("私钥(Base64): " + privateKey);

        var data = "Hello RSA!";
        System.out.println("原文: " + data);

        var encryptedByPublic = encryptByPublicKey(data, publicKey);
        System.out.println("公钥加密: " + encryptedByPublic);
        var decryptedByPrivate = decryptByPrivateKey(encryptedByPublic, privateKey);
        System.out.println("私钥解密: " + decryptedByPrivate);

        var encryptedByPrivate = encryptByPrivateKey(data, privateKey);
        System.out.println("私钥加密: " + encryptedByPrivate);
        var decryptedByPublic = decryptByPublicKey(encryptedByPrivate, publicKey);
        System.out.println("公钥解密: " + decryptedByPublic);

        var sign = sign(data, privateKey);
        System.out.println("签名: " + sign);
        var verify = verify(data, publicKey, sign);
        System.out.println("验签: " + verify);
    }
}
