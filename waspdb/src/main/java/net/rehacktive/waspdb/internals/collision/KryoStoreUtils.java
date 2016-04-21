package net.rehacktive.waspdb.internals.collision;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import net.rehacktive.waspdb.internals.cryptolayer.AESSerializer;
import net.rehacktive.waspdb.internals.cryptolayer.CipherManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class KryoStoreUtils {
	
	private static String TAG = "KRYOSTORE";
	private static Kryo kryoInstance;

	private static Kryo getKryoInstance() {
		if(kryoInstance==null)
			kryoInstance = new Kryo();

		return kryoInstance;
	}

	// generic i/o

	public static void serializeToDisk(Object obj, String filename, CipherManager cipherManager) throws Exception {
		try {
			//Long start = System.currentTimeMillis();
			//Log.d(TAG,start+": starting serializeToDisk with password");

			Output output = new Output(new FileOutputStream(filename));
            if(cipherManager!=null) {
                AESSerializer aes = new AESSerializer(getKryoInstance().getSerializer(obj.getClass()), cipherManager);
                aes.write(getKryoInstance(), output, obj);
            } else {
                getKryoInstance().writeObject(output, obj);
            }
			output.close();
			
			//Long end = System.currentTimeMillis();
			//Log.d(TAG,end+": starting serializeToDisk with password");
			//Log.d(TAG,"total time (ms): "+(end-start));
		}
		catch(Exception e) {
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
                if(cipherManager!=null) {
                    AESSerializer aes = new AESSerializer(getKryoInstance().getDefaultSerializer(type), cipherManager);
                    hash = aes.read(getKryoInstance(), input, type);
                }
                else {
                    hash = getKryoInstance().readObject(input, type);
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
			throw new Exception("\nERROR on readFromDisk:"+e.getMessage());
		}
	}

	// serializer for keys
	public static byte[] serialize(Object o) {
		byte[] ret = new byte[4096];
		Output output = new Output(ret);
		getKryoInstance().writeObject(output, o);
		return output.toBytes();
	}
	
//	public static Object cloneObject(Object obj) {
//		return getKryoInstance().copy(obj);
//	}
}
