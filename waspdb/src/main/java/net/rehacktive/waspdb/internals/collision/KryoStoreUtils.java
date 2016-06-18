package net.rehacktive.waspdb.internals.collision;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;


public class KryoStoreUtils {
	
	private static String TAG = "KRYOSTORE";
	private static Kryo kryoInstance;

	private static Kryo getKryoInstance() {
		if(kryoInstance==null) {
			kryoInstance = new Kryo();
			kryoInstance.register(WaspDataPage.class);
		}
		return kryoInstance;
	}

	// generic i/o

	public static void serializeToDisk(Object obj, String filename, CipherManager cipherManager) throws Exception {
		try {
			//Long start = System.currentTimeMillis();
			//Log.d(TAG,start+": starting serializeToDisk with password");

			Output output = new Output(new FileOutputStream(filename));
			WaspDataPage dataPage = new WaspDataPage();
            if(cipherManager!=null) {
                Cipher cipher = cipherManager.getEncCipher();
				dataPage.setIv(cipher.getIV());
				dataPage.setData(cipher.doFinal(serialize(obj)));
            } else {
				dataPage.setData(serialize(obj));
            }
			getKryoInstance().writeObject(output, dataPage);
			output.close();
			
			//Long end = System.currentTimeMillis();
			//Log.d(TAG,end+": starting serializeToDisk with password");
			//Log.d(TAG,"total time (ms): "+(end-start));
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new Exception("\nERROR on serializeToDisk:"+e.getMessage());
		}
	}

	public static Object readFromDisk(String filename, Class type, CipherManager cipherManager) throws Exception {
		try {
			//Long start = System.currentTimeMillis();
			//Log.d(TAG,start+": starting readFromDisk with password");

			File f = new File(filename);
            Object hash;
			if(f.exists()) {
				Input input = new Input(new FileInputStream(f));
				WaspDataPage dataPage = getKryoInstance().readObject(input, WaspDataPage.class);
                if(cipherManager!=null) {
                    Cipher decipher = cipherManager.getDecCipher(dataPage.getIv());
					hash = unserialize(decipher.doFinal(dataPage.getData()),type);
                }
                else {
                   hash = unserialize(dataPage.getData(),type);
                }
				input.close();
				
				//Long end = System.currentTimeMillis();
				//Log.d(TAG,end+": starting readFromDisk with password");
				//Log.d(TAG,"total time (ms): "+(end-start));
				
				return hash;
			}
			else {
				throw new Exception("\nERROR on readFromDisk: can't find "+filename);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new Exception("\nERROR on readFromDisk:"+e.getMessage());
		}
	}

	// serializer for keys
	public static byte[] serialize(Object o) {
		byte[] ret = new byte[CollisionHash.MAXFILESIZE*2];
		Output output = new Output(ret);
		getKryoInstance().writeObject(output, o);
		return output.toBytes();
	}

	public static Object unserialize(byte[] buffer, Class type) {
		Input input = new Input(buffer);
		return getKryoInstance().readObject(input, type);
	}
	
//	public static Object cloneObject(Object obj) {
//		return getKryoInstance().copy(obj);
//	}
}
