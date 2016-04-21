package net.rehacktive.waspdb.internals.cryptolayer;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by stefano on 06/08/2014.
 */
public class CipherManager {

    private int ITERATIONS = 10000;
    private int KEYSIZE = 256;

    public static String algorithm = "PBEWITHSHA256AND256BITAES-CBC-BC";

    protected Key key;

    protected static CipherManager instance = null;

    private CipherManager() {
        // Exists only to defeat instantiation.
    }

    public static CipherManager getInstance(char[] p, byte[] s)  {
        if (instance == null) {
            instance = new CipherManager();
            try {
                instance.generateSK(p, s);
            }
            catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return instance;
    }

    private void generateSK(char[] passPhrase, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorithm);

        KeySpec spec = new PBEKeySpec(passPhrase,salt,ITERATIONS, KEYSIZE);
        SecretKey secretKey = secretKeyFactory.generateSecret(spec);

        key = new SecretKeySpec(secretKey.getEncoded(), algorithm);
    }

    protected Cipher getCipher(int mode) {
        try {
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(mode, key);

            return cipher;
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
