package com.zhanglinwei.zTools.sensitive.crypto;

public abstract class Crypto {

    public abstract String encrypt(String content, String secretKey, String iv) throws Exception;
    public abstract String decrypt(String content, String secretKey, String iv) throws Exception;
    public abstract boolean isEncrypted(String text);
    public abstract String getPrefix();
    public abstract String getSuffix();
    public abstract String getExpression();

    protected static byte[] hexStringToBytes(String hexString) {
        if (hexString != null && !hexString.equals("")) {
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];

            for (int i = 0; i < length; ++i) {
                int pos = i * 2;
                d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }

            return d;
        } else {
            return null;
        }
    }

    protected static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    protected static byte[] hexToByte(String hex) throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        } else {
            char[] arr = hex.toCharArray();
            byte[] b = new byte[hex.length() / 2];
            int i = 0;
            int j = 0;

            for (int l = hex.length(); i < l; ++j) {
                String swap = "" + arr[i++] + arr[i];
                int byteint = Integer.parseInt(swap, 16) & 255;
                b[j] = (byte) byteint;
                ++i;
            }

            return b;
        }
    }

    protected static String byteToHex(byte[] b) {
        if (b == null) {
            throw new IllegalArgumentException("Input byte array cannot be null.");
        }
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte value : b) {
            sb.append(String.format("%02x", value & 0xFF));
        }
        return sb.toString();
    }

}
