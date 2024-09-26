package logicBox;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesEncryptionAlgorithm {

    // Function for AES encryption
    public static byte[] encrypt128(String data, String key) throws Exception {

        if (key.length() == 16) {
            byte[] aesKey = Utility.stringToBytes(key, false);
            SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey, "AES");

            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
                byte[] inputBytes = Utility.stringToBytes(data, false);
                return cipher.doFinal(inputBytes);
            } catch (Exception e) {
                System.out.println("Error while encrypting: " + e.toString());
            }
        } else {
            System.out.println("AES key length is not equal to 16 bytes.\n Current key length : " + key.length());
        }
        return null;
    }

    // Function for AES decryption
    public static String decrypt128(byte[] data, String key) {
        if (key.length() == 16) {
            byte[] aesKey = Utility.stringToBytes(key, false);
            SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey, "AES");
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
                return new String(cipher.doFinal(data));
            } catch (Exception e) {

                System.out.println("Error while decrypting: " + e.toString());
            }
        } else {
            System.out.println("AES key length is not equal to 16 bytes.\n Current key length : " + key.length());
        }
        return null;

    }
}
