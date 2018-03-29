/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.project.common.tools;

import cn.com.inhand.tools.exception.ParameterErrorException;
import cn.com.inhand.tools.utilities.security.SecurityInterface;
import java.security.Key;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author hanchuanjun
 */
public class ScrtTools  implements SecurityInterface{
    
    private static final String Algorithm = "AES";
    private byte[] keyBytes;
    //APlus_1234567_GE
    private static final byte[] iv = "APlus_1234567_GE".getBytes();

    public ScrtTools() {
            this.keyBytes = "APlus_1234567_GE".getBytes();
    }

    @Override
    public void setParams(HashMap map) {
        // TODO Auto-generated method stub

    }

    @Override
    public byte[] unwrap(byte[] src, byte[] key) throws ParameterErrorException {
        if (key != null) {
            this.keyBytes = key;
        }
        // TODO Auto-generated method stub
        try {
            //int srcLen = Utility.getNTimesLen(src.length, 16);
            byte[] data = src;//Utility.padBytes(src, srcLen);
            return this.getSecretBytes(data, keyBytes, iv, 1);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParameterErrorException(e.getMessage());
        }
    }

    @Override
    public byte[] wrap(byte[] src, byte[] key) throws ParameterErrorException {
        if (key != null) {
            this.keyBytes = key;
        }
        try {

            return this.getSecretBytes(src, keyBytes, iv, 0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParameterErrorException(e.getMessage());
        }
    }

    private byte[] getSecretBytes(byte[] src, byte privateKey[], byte[] iv, int mode)
            throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
		// 初始化
        // DES算法必须是56位
        // DESede算法可以是112位或168位
        // AES算法可以是128、192、256位
        Key key = new SecretKeySpec(privateKey, "AES");
        //AES/CBC/PKCS7Padding
        Cipher cp = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC"); // 创建密码器
        if (mode == 0) {
            cp.init(Cipher.ENCRYPT_MODE, key, paramSpec); // 初始化
        } else {
            cp.init(Cipher.DECRYPT_MODE, key, paramSpec); // 初始化
        }
        byte[] data = cp.doFinal(src);
        /*KeyGenerator kgen = KeyGenerator.getInstance("AES");
         kgen.init(128, new SecureRandom(privateKey));
         AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
         SecretKey key = kgen.generateKey();
         Cipher cp = Cipher.getInstance("AES/CBC/NoPadding");
         if (mode == 0) {
         cp.init(Cipher.ENCRYPT_MODE, key, paramSpec); // 初始化
         } else {
         cp.init(Cipher.DECRYPT_MODE, key, paramSpec); // 初始化
         }
         byte[] data=cp.doFinal(src);*/
        return data;
    }
}
