package net.rehacktive.waspdb.internals.collision;

import android.util.Log;

import net.rehacktive.waspdb.internals.collision.exceptions.KeyAlreadyExistsException;
import net.rehacktive.waspdb.internals.collision.exceptions.KeyNotFoundException;
import net.rehacktive.waspdb.internals.utils.Utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CollisionHash {

    protected String path;
    protected String ext = ".cube";

    protected static int MAXFILESIZE = 65536;

    private CipherManager cipherManager;

	private static String TAG = "COLLISIONHASH";

	private static String[] cubes = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};

	// public methods

	public CollisionHash(String path, CipherManager cipherManager)  {
        this.path = path;
        this.cipherManager = cipherManager;
	}

    public void storeObject(Object key, Object value) throws Exception {
		storeObject(key, value,false);
	}

    public void updateObject(Object key, Object value) throws Exception {
		storeObject(key,value,true);
	}

    public void storeObject(Object key, Object value, boolean update) throws Exception {
		try {
			String searchKey = getSearchKey(key); // calculate searchKey for file to store
			HashMap hash = getHashFromKey(searchKey); // return the hash from the key - it's size is < MAXSIZE-1

			if(!hash.containsKey(key) || update) {
				hash.put(key, value); // add value
				storeHashByKey(hash,searchKey); // store hash
			}
			else {
				throw new KeyAlreadyExistsException("key already exists for value "+hash.get(key));
			}
		} catch(Exception e) {
			throw e;
		}
	}

	public Object retrieveObject(Object key) throws Exception {
		return retrieveObject(key, false);
	}


	public Object removeObject(Object key) throws Exception {
		return retrieveObject(key, true);
	}

	public HashMap getAllData(String p) throws Exception {
		if(p==null) p = path;
		HashMap ret = new HashMap();

		for(String s : cubes) {
			String currentPath = p+"/"+s;
			if(new File(currentPath).isDirectory()) {
				ret.putAll(getAllData(currentPath));
			}
			if(new File(currentPath+ext).exists()) {
				HashMap data;
				data = (HashMap) KryoStoreUtils.readFromDisk(currentPath+ext, HashMap.class, cipherManager);
				ret.putAll(data);
			}
		}
		return ret;
	}

	public List getAllKeys(String p) throws Exception {
		if(p==null) p = path;
		List<Object> ret = new ArrayList<Object>();

		for(String s : cubes) {
			String currentPath = p+"/"+s;
			if(new File(currentPath).isDirectory()) {
				ret.addAll(getAllKeys(currentPath));
			}
			if(new File(currentPath+ext).exists()) {
				HashMap data;
				data = (HashMap) KryoStoreUtils.readFromDisk(currentPath+ext, HashMap.class,cipherManager);
				ret.addAll(data.keySet());
			} 
		}
		return ret;
	}

	public List getAllValues(String p) throws Exception {
		if(p==null) p = path;
		List<Object> ret = new ArrayList<Object>();

		for(String s : cubes) {
			String currentPath = p+"/"+s;
			if(new File(currentPath).isDirectory()) {
				ret.addAll(getAllValues(currentPath));
			}
			if(new File(currentPath+ext).exists()) {
				HashMap data;
				data = (HashMap) KryoStoreUtils.readFromDisk(currentPath+ext, HashMap.class,cipherManager);
				ret.addAll(data.values());
			}
		}
		return ret;
	}

	// private methods

	private Object retrieveObject(Object key, boolean remove) throws Exception {
		Object ret = null;
		try {
			String searchKey = getSearchKey(key); // calculate searchKey for file to store
			HashMap hash = getHashFromKey(searchKey); // return the hash from the key - it's size is < MAXSIZE-1
			ret = hash.get(key);
			if(ret!=null) {
				Log.d(TAG, System.currentTimeMillis()+": object found with key "+key);
				if(remove) {
					hash.remove(key);
					storeHashByKey(hash,searchKey); // store  hash
					Log.d(TAG, System.currentTimeMillis()+": object removed with key "+key);
				}
			}
			else {
				throw new KeyNotFoundException("key not found for key "+key);
			}
		} catch(Exception e) {
			throw e;
		}
		return ret;	
	}

	protected void explodeCube(String file) throws Exception {
		Log.d(TAG,"cube full:"+file);
		// the cube is full 
		// get the working directory
		String tmpFile = file.substring(0,file.length()-6)+"tmp"+ext;
		// original file
		File file1 = new File(file);
		// tmp file
		File file2 = new File(tmpFile);
		// Rename file
		boolean success = file1.renameTo(file2);
		if(success) Log.d(TAG,"created temporary file");

		String newdirPath = file.substring(0,file.length()-5);
		File newdir = new File(newdirPath);;
		try {

			// create a directory with tmp name
			if(newdir.mkdir()) {
				Log.d(TAG,"created new directory:"+newdirPath);
				// read content of the file
				HashMap hash;
				hash = (HashMap) KryoStoreUtils.readFromDisk(tmpFile,HashMap.class,cipherManager);
				// and store it's content inside the new directory
				for(Object k : hash.keySet()) {
					//String key = (String) k;
					Object o = hash.get(k);
					storeObject(k, o);
				}
				// remove the tmp file
				file2.delete();
			} else {
				Log.d(TAG, "can't create "+newdirPath);
				throw new Exception();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			// if something goes wrong during split
			// remove the new directory
			deleteRecursive(newdir);
			// and restore original cube
			success = file2.renameTo(file1);
			if(success) {
				Log.d(TAG,"restored original cube");
				throw new Exception("Unable to add more data - no more space left?");
			}
			else throw new Exception("FATAL ON FILESYSTEM DURING ADDING MORE DATA - possible corrupted data!!!");
		}
		Log.d(TAG, "explosion done");
	}

    // previously on CollisionStructure

    public String getSearchKey(Object key) throws NoSuchAlgorithmException {
        // transform a key to a searchKey for the collision!
        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.reset();
        messageDigest.update(KryoStoreUtils.serialize(key));
        final byte[] resultByte = messageDigest.digest();
        final String result = Utils.toHexString(resultByte);
        String searchKey = result.substring(0,8);
        //Log.d("XXX","getSearchKey: "+searchKey+" for key "+key);
        return searchKey;
    }

    public String getFileFromSearchKey(String searchKey) {
        String filePath = "";
        for(int i=searchKey.length();i>0;i--) {
            filePath = path;
            for(int j=0;j<i;j++) {
                filePath += "/"+searchKey.charAt(j);
            }
            if(new File(filePath).isDirectory()) {
                String newfile = filePath+"/"+searchKey.charAt(i)+ext;
                return newfile;
            }
            filePath += ext;
            if(new File(filePath).exists()) {
                return filePath;
            }
        }
        return filePath;
    }

    // return always a not-full hash from a searchKey
    public HashMap getHashFromKey(String searchKey) throws Exception {
		HashMap hash;
        String file = getFileFromSearchKey(searchKey);
        // if file not exists, return a new hash (the file will be created on store)
        if(!new File(file).exists()) {
			hash = new HashMap();
		}
		else {
			// if file exists, read the hash inside
			hash = (HashMap) KryoStoreUtils.readFromDisk(file, HashMap.class, cipherManager);
			// if hash is full (maxsize) this file should "explode" to a new cube
			long size = FileUtils.sizeOf(new File(file));
			//System.out.println("file # " + size);
			if (size > MAXFILESIZE && hash.size() > 1) {
				explodeCube(file);
				// then recall this method again (recursive) to use the next byte of the key and find the correct file
				return getHashFromKey(searchKey);
			}
		}
        // return
        return hash;
    }

    protected boolean deleteRecursive(File path) throws FileNotFoundException {
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()){
            for (File f : path.listFiles()){
                ret = ret && deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }

    public void storeHashByKey(HashMap hash, String searchKey) throws Exception {
        // simply serialize the hash to the file addressed by the key
        String file = getFileFromSearchKey(searchKey);
        KryoStoreUtils.serializeToDisk(hash, file, cipherManager);
        // the file exists, according to the getHashFromKey feature, and it's not full
    }

}
