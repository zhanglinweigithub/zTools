package com.zhanglinwei.zTools.sensitive.crypto;

import java.nio.charset.StandardCharsets;

public class SM4_CBC extends SM4 {


    @Override
    public String encrypt(String content, String secretKey, String iv) {
        return encrypt_cbc(content, secretKey, iv);
    }

    @Override
    public String decrypt(String content, String secretKey, String iv) {
        return decrypt_cbc(content, secretKey, iv);
    }

    public static String encrypt_cbc(String content, String secretKey, String iv) {
        SM4.SM4Context ctx = new SM4.SM4Context();
        ctx.isPadding = true;
        ctx.mode = 1;

        byte[] keyBytes = hexStringToBytes(secretKey);
        byte[] ivBytes = hexStringToBytes(iv);

        sm4_setkey_enc(ctx, keyBytes);
        byte[] encrypted = sm4_crypt_cbc(ctx, ivBytes, content.getBytes(StandardCharsets.UTF_8));
        return byteToHex(encrypted);
    }

    public static String decrypt_cbc(String content, String secretKey, String iv) {
        byte[] encrypted = hexStringToBytes(content);

        SM4Context ctx = new SM4Context();
        ctx.isPadding = true;
        ctx.mode = 0;

        byte[] keyBytes = hexStringToBytes(secretKey);
        byte[] ivBytes = hexStringToBytes(iv);

        sm4_setkey_dec(ctx, keyBytes);
        byte[] decrypted = sm4_crypt_cbc(ctx, ivBytes, encrypted);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    public static byte[] sm4_crypt_cbc(SM4Context ctx, byte[] iv, byte[] input) {
        if (iv == null || iv.length != 16) {
            throw new IllegalArgumentException("IV must be 16 bytes long.");
        }

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

        byte[] temp = new byte[16];
        byte[] currentBlock = new byte[16];
        byte[] ivCopy = iv.clone();

        for (int i = 0; i < blocks; i++) {
            int blockStart = i * 16;
            int blockEnd = Math.min((i + 1) * 16, input.length);

            System.arraycopy(input, blockStart, currentBlock, 0, blockEnd - blockStart);

            if (ctx.mode == 1) {
                for (int j = 0; j < 16; j++) {
                    currentBlock[j] ^= ivCopy[j];
                }
            }

            sm4_one_round(ctx.sk, currentBlock, temp);

            if (ctx.mode == 0) {
                for (int j = 0; j < 16; j++) {
                    temp[j] ^= ivCopy[j];
                }
            }

            System.arraycopy(temp, 0, output, i * 16, 16);
            System.arraycopy(temp, 0, ivCopy, 0, 16);
        }

        if (ctx.isPadding && ctx.mode == 0) {
            output = padding(output, 0);
        }

        return output;
    }


}
