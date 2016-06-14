package net.rehacktive.waspdb.internals.utils;

import net.rehacktive.waspdb.internals.collision.CipherManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;


public class Utils {

	public static String md5(String s) throws NoSuchAlgorithmException {
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(s.getBytes());
			byte[] resultByte = messageDigest.digest();
			String result = toHexString(resultByte);
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static String toHexString(byte[] bytes) {
	    char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
	    char[] hexChars = new char[bytes.length * 2];
	    int v;
	    for ( int j = 0; j < bytes.length; j++ ) {
	        v = bytes[j] & 0xFF;
	        hexChars[j*2] = hexArray[v/16];
	        hexChars[j*2 + 1] = hexArray[v%16];
	    }
	    return new String(hexChars);
	}

	public static boolean isEmpty(String s) {
		return s==null || s.trim().equals("");
	}

	public static boolean checkForCryptoAvailable() {
        try {
//			Security.addProvider(new BouncyCastleProvider());
//            for(String s : Security.getAlgorithms("Cipher"))
//                 System.out.println(s);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(CipherManager.key_algorithm);
			return true;
		} catch (NoSuchAlgorithmException e) {
			return false;
		}
	}

	public static boolean deleteRecursive(File path) throws FileNotFoundException {
		if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
		boolean ret = true;
		if (path.isDirectory()){
			for (File f : path.listFiles()){
				ret = ret && deleteRecursive(f);
			}
		}
		return ret && path.delete();
	}


    public static Salt generateSalt() {
		SecureRandom sr = new SecureRandom();
		byte[] output = new byte[256];
		sr.nextBytes(output);
        return new Salt(output);
    }
}
