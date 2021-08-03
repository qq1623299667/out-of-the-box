package com.encrypt;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.crypto.digest.MD5;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

@Slf4j
public class RSAUtil {
    /** 签名算法 */
    private static final String SIGNATURE_ALGORITHM = "Sha256WithRSA";


    public static void main(String[] args) throws NoSuchAlgorithmException, IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException, IOException {
//        String publicKeyString = "";
//        String privateKeyString = "";
//        String msg = "hello secret";
//        log.info(msg);
//        PublicKey pubKey = getPubKey();
//        PrivateKey privateKey = getPrivateKey();
        KeyPair keyPair = generateKeyPair();
//        PrivateKey privateKey = keyPair.getPrivate();
//        PublicKey pubKey = keyPair.getPublic();
//        String encrypt = encrypt(getPubKey(getKeyString(keyPair.getPublic())), msg);
        String publicKeyString = getKeyString(keyPair.getPublic());
        log.info(publicKeyString);
//        String decrypt = decrypt(getPrivateKey(getKeyString(keyPair.getPrivate())), encrypt);
        String privateKeyString = getKeyString(keyPair.getPrivate());
        log.info(privateKeyString);
    }

    // 获取公私钥字符串
    private static String getKeyString(Key key) {
        return Base64Encoder.encode(key.getEncoded());
    }

    // 通过字符串获取公钥
    public static PublicKey getPubKey(String pubKey) {
        PublicKey publicKey = null;
        try {
            X509EncodedKeySpec bobPubKeySpec = new X509EncodedKeySpec(
                    Base64Decoder.decode(pubKey));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(bobPubKeySpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

    // 通过字符串获取私钥
    public static PrivateKey getPrivateKey(String priKey) {
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec priPKCS8;
        try {
            priPKCS8 = new PKCS8EncodedKeySpec(Base64Decoder.decode(priKey));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            privateKey = keyf.generatePrivate(priPKCS8);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    // 生成密钥对
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        // 获取指定算法的密钥对生成器
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");

        // 初始化密钥对生成器（指定密钥长度, 使用默认的安全随机数源）
        gen.initialize(2048);

        // 随机生成一对密钥（包含公钥和私钥）
        KeyPair keyPair = gen.generateKeyPair();

        return keyPair;
    }

    // 加密的方法
    public static String encrypt(PublicKey pubKey, String message) throws
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, IOException {
        // Get a cipher object.
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);

        byte[] bytes = message.getBytes("UTF-8");

        // encode the message
        final byte[] raw = doSafeFinal(cipher, bytes);

        // converts to base64 for easier display.
        return Base64Encoder.encode(raw);
    }

    // 解密数据
    public static String decrypt(PrivateKey priKey, String encrypted) throws
            InvalidKeyException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, IOException {

        //decode the BASE64 coded message
        byte[] raw = Base64Decoder.decode(encrypted);

        // Get a cipher object.
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);

        // decode the message
        final byte[] bytes = doSafeFinal(cipher, raw);

        // converts the decoded message to a String
        return new String(bytes, "UTF-8");
    }

    private static byte[] doSafeFinal(Cipher cipher, byte[] text) throws
            IllegalBlockSizeException, BadPaddingException,
            IOException {

        // avoid overrun the block size of the cipher
        int blockSize = cipher.getBlockSize();
        if (blockSize > 0) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            for (int offset = 0; offset < text.length; offset += blockSize) {
                if (text.length - offset <= blockSize) {
                    len = text.length - offset;
                } else {
                    len = blockSize;
                }
                out.write(cipher.doFinal(text, offset, len));
            }
            return out.toByteArray();
        } else {
            return cipher.doFinal(text);
        }
    }



    /**
     * 私钥签名（数据）: 用私钥对指定字节数组数据进行签名, 返回签名信息
     */
    private static byte[] sign(byte[] data, PrivateKey priKey) throws Exception {
        // 根据指定算法获取签名工具
        Signature sign = Signature.getInstance(SIGNATURE_ALGORITHM);

        // 用私钥初始化签名工具
        sign.initSign(priKey);

        // 添加要签名的数据
        sign.update(data);

        // 计算签名结果（签名信息）
        byte[] signInfo = sign.sign();

        return signInfo;
    }

    // 产生签名
    public static String sign(String data,String privateKeyString) throws Exception {
        byte[] sign = sign(Base64Decoder.decode(data), RSAUtil.getPrivateKey(privateKeyString));
        return Base64Encoder.encode(sign);
    }

    /**
     * 公钥验签（数据）: 用公钥校验指定数据的签名是否来自对应的私钥
     */
    private static boolean verify(byte[] data, byte[] signInfo, PublicKey pubKey) throws Exception {
        // 根据指定算法获取签名工具
        Signature sign = Signature.getInstance(SIGNATURE_ALGORITHM);

        // 用公钥初始化签名工具
        sign.initVerify(pubKey);

        // 添加要校验的数据
        sign.update(data);

        // 校验数据的签名信息是否正确,
        // 如果返回 true, 说明该数据的签名信息来自该公钥对应的私钥,
        // 同一个私钥的签名, 数据和签名信息一一对应, 只要其中有一点修改, 则用公钥无法校验通过,
        // 因此可以用私钥签名, 然后用公钥来校验数据的完整性与签名者（所有者）
        boolean verify = sign.verify(signInfo);

        return verify;
    }

    // 验证签名
    public static boolean verify(String data, String signInfo,String publicKeyString) throws Exception {
        return verify(Base64Decoder.decode(data),Base64Decoder.decode(signInfo), RSAUtil.getPubKey(publicKeyString));
    }

    // 做md5hash
    public static String md5Hash(String endTime,String mac,String salt) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, IOException {
        MD5 md5 = MD5.create();
        byte[] digest = md5.digest(endTime + mac + salt);
        String encode = Base64Encoder.encode(digest);
//        String encrypt = RSAUtil.encrypt(RSAUtil.getPubKey(publicKeyString), encode);
        return encode;
    }
}
