import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtils {
    public static final String ASE_KEY = "W3X5ZQL11";
    private static final String charset = "utf-8";
    private static final String AES = "AES";

    public static String getASEEncode(String result) {
        return encode(ASE_KEY, result);
    }

    public static String getASEDecode(String result) {
        return decode(ASE_KEY, result);
    }

    public static String encode(String encodeRules, String content) {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(AES);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(encodeRules.getBytes());
            keygen.init(128, random);
            SecretKey secretKey = keygen.generateKey();
            byte[] keyEncoded = secretKey.getEncoded();
            SecretKey key = new SecretKeySpec(keyEncoded, AES);

            // 验证密钥长度
            if (keyEncoded.length * 8 != 128) {
                throw new RuntimeException("Generated key is not 128 bits long: " + (keyEncoded.length * 8) + " bits");
            }

            System.out.println("Generated Key (Hex): " + bytesToHex(keyEncoded));

            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] bytes_AES = cipher.doFinal(content.getBytes(charset));
            // 使用 Base64 进行编码
            String encode = Base64.getEncoder().encodeToString(bytes_AES);
            return encode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String decode(String encodeRules, String content) {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(AES);
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(encodeRules.getBytes());
            keygen.init(128, random);
            SecretKey secretKey = keygen.generateKey();
            SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), AES);
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, key);
            // 使用 Base64 进行解码
            byte[] decodedBytes = Base64.getDecoder().decode(content);
            String decode = new String(cipher.doFinal(decodedBytes), charset);
            return decode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String original = String.valueOf(System.currentTimeMillis());
        String encoded = encode(ASE_KEY, original);
        String decoded = decode(ASE_KEY, encoded);

        System.out.println("Original: " + original);
        System.out.println("Encoded: " + encoded);
        System.out.println("Decoded: " + decoded);
    }
}
