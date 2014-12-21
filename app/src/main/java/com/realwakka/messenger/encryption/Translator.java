package com.realwakka.messenger.encryption;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.NoSuchElementException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by realwakka on 14. 11. 28.
 */
public class Translator {
    public static final String ALGORITHM = "RSA";
    public static final String PRIVATE_KEY_FILE = "/keys/private.key";
    public static final String PUBLIC_KEY_FILE = "/keys/public.key";
    public static final String KEY_DIR="keys";

    public static PublicKey getPublicKey(Context context) throws Exception{
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(context.getFilesDir()+PUBLIC_KEY_FILE));
        return (PublicKey) inputStream.readObject();
    }

    public static PrivateKey getPrivateKey(Context context) throws Exception{
        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(context.getFilesDir()+PRIVATE_KEY_FILE));
        return (PrivateKey)inputStream.readObject();
    }


    public static byte[] encryptString(String text,PublicKey key)
            throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(text.getBytes());
    }


    public static String decryptString(byte[] text,PrivateKey key)
            throws Exception{
        final Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(text));

    }

    public static void generateKey(Context context) throws Exception {

        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyGen.initialize(1024);
        final KeyPair key = keyGen.generateKeyPair();


        File privateKeyFile = new File(context.getFilesDir()+PRIVATE_KEY_FILE);
        File publicKeyFile = new File(context.getFilesDir()+PUBLIC_KEY_FILE);

        if (privateKeyFile.getParentFile() != null) {
            File d = privateKeyFile.getParentFile();
            Log.d("RegisterActivity",d.getAbsolutePath());
            privateKeyFile.getParentFile().mkdirs();
        }
        privateKeyFile.createNewFile();

        if (publicKeyFile.getParentFile() != null) {
            publicKeyFile.getParentFile().mkdirs();
        }
        publicKeyFile.createNewFile();

        ObjectOutputStream publicKeyOS = new ObjectOutputStream(
                new FileOutputStream(publicKeyFile));
        publicKeyOS.writeObject(key.getPublic());
        publicKeyOS.close();

        ObjectOutputStream privateKeyOS = new ObjectOutputStream(
                new FileOutputStream(privateKeyFile));
        privateKeyOS.writeObject(key.getPrivate());
        privateKeyOS.close();

    }

    public static boolean areKeysPresent() {

        File privateKey = new File(PRIVATE_KEY_FILE);
        File publicKey = new File(PUBLIC_KEY_FILE);

        if (privateKey.exists() && publicKey.exists()) {
            return true;
        }
        return false;
    }

    public static boolean test(Context context) throws Exception {
        String str = "hello!";
        PublicKey publicKey = getPublicKey(context);
        PrivateKey privateKey = getPrivateKey(context);

        String encoded = URLEncoder.encode(str,"UTF-8");
        byte[] b_encrypted = encryptString(encoded,publicKey);

        String s_encrypted = new String(b_encrypted);

        return true;


    }
}
