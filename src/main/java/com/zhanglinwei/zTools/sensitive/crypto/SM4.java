package com.zhanglinwei.zTools.sensitive.crypto;

import com.zhanglinwei.zTools.util.AssertUtils;

public abstract class SM4 extends Crypto {

    protected static final String SM4_PREFIX = "SM4[";
    protected static final String SM4_SUFFIX = "]";
    private static final String EXPRESSION = "SM4[%s]";
    protected static final byte[] SboxTable = new byte[]{-42, -112, -23, -2, -52, -31, 61, -73, 22, -74, 20, -62, 40, -5, 44, 5, 43, 103, -102, 118, 42, -66, 4, -61, -86, 68, 19, 38, 73, -122, 6, -103, -100, 66, 80, -12, -111, -17, -104, 122, 51, 84, 11, 67, -19, -49, -84, 98, -28, -77, 28, -87, -55, 8, -24, -107, -128, -33, -108, -6, 117, -113, 63, -90, 71, 7, -89, -4, -13, 115, 23, -70, -125, 89, 60, 25, -26, -123, 79, -88, 104, 107, -127, -78, 113, 100, -38, -117, -8, -21, 15, 75, 112, 86, -99, 53, 30, 36, 14, 94, 99, 88, -47, -94, 37, 34, 124, 59, 1, 33, 120, -121, -44, 0, 70, 87, -97, -45, 39, 82, 76, 54, 2, -25, -96, -60, -56, -98, -22, -65, -118, -46, 64, -57, 56, -75, -93, -9, -14, -50, -7, 97, 21, -95, -32, -82, 93, -92, -101, 52, 26, 85, -83, -109, 50, 48, -11, -116, -79, -29, 29, -10, -30, 46, -126, 102, -54, 96, -64, 41, 35, -85, 13, 83, 78, 111, -43, -37, 55, 69, -34, -3, -114, 47, 3, -1, 106, 114, 109, 108, 91, 81, -115, 27, -81, -110, -69, -35, -68, 127, 17, -39, 92, 65, 31, 16, 90, -40, 10, -63, 49, -120, -91, -51, 123, -67, 45, 116, -48, 18, -72, -27, -76, -80, -119, 105, -105, 74, 12, -106, 119, 126, 101, -71, -15, 9, -59, 110, -58, -124, 24, -16, 125, -20, 58, -36, 77, 32, 121, -18, 95, 62, -41, -53, 57, 72};
    protected static final int[] FK = new int[]{-1548633402, 1453994832, 1736282519, -1301273892};
    protected static final int[] CK = new int[]{462357, 472066609, 943670861, 1415275113, 1886879365, -1936483679, -1464879427, -993275175, -521670923, -66909679, 404694573, 876298825, 1347903077, 1819507329, -2003855715, -1532251463, -1060647211, -589042959, -117504499, 337322537, 808926789, 1280531041, 1752135293, -2071227751, -1599623499, -1128019247, -656414995, -184876535, 269950501, 741554753, 1213159005, 1684763257};

    @Override
    public boolean isEncrypted(String text) {
        return AssertUtils.isNotBlank(text) && text.startsWith(SM4_PREFIX) && text.endsWith(SM4_SUFFIX);
    }

    @Override
    public String getPrefix() {
        return SM4_PREFIX;
    }

    @Override
    public String getSuffix() {
        return SM4_SUFFIX;
    }

    @Override
    public String getExpression() {
        return EXPRESSION;
    }

    private static long GET_ULONG_BE(byte[] b, int i) {
        return ((long) (b[i] & 0xFF) << 24) |
                ((long) (b[i + 1] & 0xFF) << 16) |
                ((long) (b[i + 2] & 0xFF) << 8) |
                (b[i + 3] & 0xFF);
    }

    private static void PUT_ULONG_BE(long n, byte[] b, int i) {
        b[i] = (byte) (n >>> 24);  // 获取高8位
        b[i + 1] = (byte) (n >>> 16);  // 获取次高8位
        b[i + 2] = (byte) (n >>> 8);   // 获取次低8位
        b[i + 3] = (byte) n;           // 获取低8位
    }

    private static long SHL(long x, int n) {
        return x << n;
    }

    private static long ROTL(long x, int n) {
        n = n % 32;  // 确保 n 是小于 32 的
        return (x << n) | (x >>> (32 - n));  // 使用无符号右移 >>> 代替 >>
    }

    private static void SWAP(long[] sk, int i) {
        if (i != 31 - i) {  // 检查是否为同一元素，避免无意义的交换
            sk[i] ^= sk[31 - i];
            sk[31 - i] ^= sk[i];
            sk[i] ^= sk[31 - i];
        }
    }

    private static byte sm4Sbox(byte inch) {
        return SboxTable[inch & 0xFF];
    }

    private static long sm4Lt(long ka) {
        long bb = 0L;
        long c = 0L;

        for (int i = 0; i < 4; i++) {
            bb |= (long) sm4Sbox((byte) ((ka >> (8 * (3 - i))) & 0xFF)) << (8 * (3 - i));
        }

        c = bb ^ ROTL(bb, 2) ^ ROTL(bb, 10) ^ ROTL(bb, 18) ^ ROTL(bb, 24);

        return c;
    }

    private static long sm4F(long x0, long x1, long x2, long x3, long rk) {
        return x0 ^ sm4Lt(x1 ^ x2 ^ x3 ^ rk);
    }

    private static long sm4CalciRK(long ka) {
        byte[] a = new byte[4];
        PUT_ULONG_BE(ka, a, 0);

        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = sm4Sbox(a[i]);
        }

        long bb = GET_ULONG_BE(b, 0);
        return bb ^ ROTL(bb, 13) ^ ROTL(bb, 23);
    }

    private static void sm4_setkey(long[] SK, byte[] key) {
        long[] MK = new long[4];
        long[] k = new long[36];

        for (int i = 0; i < 4; i++) {
            MK[i] = GET_ULONG_BE(key, i * 4);
        }

        k[0] = MK[0] ^ FK[0];
        k[1] = MK[1] ^ FK[1];
        k[2] = MK[2] ^ FK[2];
        k[3] = MK[3] ^ FK[3];

        for (int i = 0; i < 32; i++) {
            k[i + 4] = k[i] ^ sm4CalciRK(k[i + 1] ^ k[i + 2] ^ k[i + 3] ^ CK[i]);
            SK[i] = k[i + 4];
        }
    }

    protected static void sm4_one_round(long[] sk, byte[] input, byte[] output) {
        long[] ulbuf = new long[36];

        for (int i = 0; i < 4; i++) {
            ulbuf[i] = GET_ULONG_BE(input, i * 4);
        }

        for (int i = 0; i < 32; i++) {
            ulbuf[i + 4] = sm4F(ulbuf[i], ulbuf[i + 1], ulbuf[i + 2], ulbuf[i + 3], sk[i]);
        }

        for (int i = 0; i < 4; i++) {
            PUT_ULONG_BE(ulbuf[35 - i], output, i * 4);
        }
    }

    protected static byte[] padding(byte[] input, int mode) {
        if (input == null) {
            return null;
        }

        byte[] ret;

        if (mode == 1) {
            int paddingLength = 16 - input.length % 16;
            ret = new byte[input.length + paddingLength];
            System.arraycopy(input, 0, ret, 0, input.length);
            ret[input.length] = (byte) 0x80;
            for (int i = input.length + 1; i < ret.length; i++) {
                ret[i] = 0x00;
            }
        } else {
            int index = -1;
            for (int i = input.length - 1; i >= 0; i--) {
                if (input[i] == (byte) 0x80) {
                    index = i;
                    break;
                }
            }

            if (index == -1) {
                ret = input;
            } else {
                ret = new byte[index];
                System.arraycopy(input, 0, ret, 0, index);
            }
        }

        return ret;
    }

    public static void sm4_setkey_enc(SM4Context ctx, byte[] key) {
        if (ctx == null) {
            throw new IllegalArgumentException("SM4Context (ctx) cannot be null.");
        }

        if (key == null || key.length != 16) {
            throw new IllegalArgumentException("Invalid key: Key must be a 16-byte array.");
        }

        ctx.mode = 1;
        sm4_setkey(ctx.sk, key);
    }

    public static void sm4_setkey_dec(SM4Context ctx, byte[] key) {
        if (ctx == null) {
            throw new IllegalArgumentException("SM4Context (ctx) cannot be null.");
        }

        if (key == null || key.length != 16) {
            throw new IllegalArgumentException("Invalid key: Key must be a 16-byte array.");
        }

        ctx.mode = 0;
        sm4_setkey(ctx.sk, key);

        for (int i = 0; i < 16; ++i) {
            SWAP(ctx.sk, i);
        }
    }





    public static class SM4Context {
        public int mode = 1;
        public long[] sk = new long[32];
        public boolean isPadding = true;
    }

}
