package com.zhanglinwei.zTools.sensitive.crypto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SM4_ECB extends SM4{

    @Override
    public String encrypt(String content, String secretKey, String iv) throws Exception {
        return encrypt_ecb(content, secretKey, iv);
    }

    @Override
    public String decrypt(String content, String secretKey, String iv) throws Exception {
        return decrypt_ecb(content, secretKey, iv);
    }

    public static String encrypt_ecb(String content, String secretKey, String iv) {
        SM4Context ctx = new SM4Context();
        ctx.isPadding = true;
        ctx.mode = 1;
        byte[] keyBytes = hexStringToBytes(secretKey);

        sm4_setkey_enc(ctx, keyBytes);
        byte[] encrypted = sm4_crypt_ecb(ctx, content.getBytes(StandardCharsets.UTF_8));
        return byteToHex(encrypted);
    }

    public static String decrypt_ecb(String content, String secretKey, String iv) {
        byte[] encrypted = hexToByte(content);
        SM4Context ctx = new SM4Context();
        ctx.isPadding = true;
        ctx.mode = 0;

        byte[] keyBytes = hexStringToBytes(secretKey);

        sm4_setkey_dec(ctx, keyBytes);
        byte[] decrypted = sm4_crypt_ecb(ctx, encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    private static byte[] sm4_crypt_ecb(SM4Context ctx, byte[] input) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null.");
        }

        if (ctx.isPadding && ctx.mode == 1) {
            input = padding(input, 1);
        }

        int length = input.length;
        int blocks = length / 16;
        if (length % 16 != 0) {
            blocks++;
        }

        byte[] output = new byte[blocks * 16];

        for (int i = 0; i < blocks; i++) {
            byte[] block = Arrays.copyOfRange(input, i * 16, Math.min((i + 1) * 16, input.length));
            byte[] encryptedBlock = new byte[16];

            if (block.length < 16) {
                Arrays.fill(block, block.length, 16, (byte) 0);
            }

            sm4_one_round(ctx.sk, block, encryptedBlock);

            System.arraycopy(encryptedBlock, 0, output, i * 16, 16);
        }

        if (ctx.isPadding && ctx.mode == 0) {
            output = padding(output, 0);
        }

        return output;
    }

}
