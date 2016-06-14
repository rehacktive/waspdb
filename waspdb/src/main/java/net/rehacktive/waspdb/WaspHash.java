package net.rehacktive.waspdb;

import net.rehacktive.waspdb.internals.collision.CollisionHash;
import net.rehacktive.waspdb.internals.collision.exceptions.KeyNotFoundException;
import net.rehacktive.waspdb.internals.collision.CipherManager;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class WaspHash extends WaspObservable {

	private String path;
    private CollisionHash hash;

    protected WaspHash() {}

    protected WaspHash(CipherManager cipherManager, String path) {
		super();
        this.hash = new CollisionHash(path, cipherManager);
		this.path = path;
	}

    /**
     * Store a key/value pair
     *
     * @param key the Object key
     * @param value the Object value
     */
	public Boolean put(Object key, Object value) {
		try {
			hash.updateObject(key, value);
            notifyObservers();
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

    /**
     * Retrieve a value associated to the specific key
     * @param key the key
     * @param <T> the value type
     * @return the object, casted automagically!
     */
	public <T> T get(Object key)  {
		try {
			return (T) hash.retrieveObject(key);
		}
		catch(KeyNotFoundException k) {
			k.printStackTrace();
			return null;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    /**
     * Remove the value associated to the specific key
     * @param key the key
     * @return true if all okay, false if error
     */
	public boolean remove(Object key) {
		try {
			Object obj = hash.removeObject(key);
			if(obj!=null) {
				notifyObservers();
				return true;
			}
			return false;
		}
		catch(KeyNotFoundException k) {
			k.printStackTrace();
			return false;
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

    /**
     * Retrieve the list of key for this WaspHash
     * @param <T> the key type
     * @return a list of keys, casted automagically
     */
	public <T> List<T> getAllKeys()  {
		try {
			return hash.getAllKeys(path);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    /**
     * Retrieve the list of values for this WaspHash
     * @param <T> the value type
     * @return a list of values, casted automagically
     */
	public <T> List<T> getAllValues()  {
		try {
			return hash.getAllValues(path);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    /**
     * Retrieve the list of key/value for this WaspHash
     * @param <K> the key type
     * @param <V> the value type
     * @return a Java HashMap containing all key/values
     */
	public <K,V> HashMap<K,V> getAllData() {
		try {
			return hash.getAllData(path);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    /**
     * Flush the current WaspHash - all the key/values will be removed
     */
	public void flush()  {
		try {
			File currentDir = new File(path);
			for(File f : currentDir.listFiles()) {
				if(f.isDirectory())
					FileUtils.deleteDirectory(f);
				else
					FileUtils.deleteQuietly(f);
			}

			notifyObservers();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

    /**
     * Return some information about this WaspHash
     * @return a string containing the infos
     */
	@Override
	public String toString() {
		return "WaspHash [path=" + path + "] total size: (K) " + FileUtils.sizeOf(new File(path));
	}


}
