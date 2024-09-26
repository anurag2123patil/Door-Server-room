package logicBox;

import java.math.BigInteger;

public class Utility {
    public static byte[] stringToBytes(String str, boolean includeNull) {

        int len;

        if (includeNull)
            len = str.length() + 1;
        else
            len = str.length();

        byte[] b = new byte[len];

        for (int i = 0; i < str.length(); i++) {
            b[i] = (byte) str.charAt(i);
        }
        return b;
    }

    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + 'x', new BigInteger(1, data));
    }
}
