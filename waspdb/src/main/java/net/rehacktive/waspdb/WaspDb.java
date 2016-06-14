package net.rehacktive.waspdb;

import net.rehacktive.waspdb.internals.collision.CipherManager;
import net.rehacktive.waspdb.internals.utils.Utils;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class WaspDb {

	/*
	 * this object contains all main infos about database
	 * this file is "kryonized" under {path}/dbname/db.data
	 */

    private String dbName; // db name
    private String path; // db path
    private CipherManager cipherManager;

    private List<String> hashes; // all hashes used

    protected WaspDb() {
    }

    /**
     * Open/create a WaspHash instance
     * @param hashName name
     * @return
     */
    public WaspHash openOrCreateHash(String hashName) {
        WaspHash hash;
        try {
            if(existsHash(hashName)) {
                hash = getHash(hashName);
            } else {
                hash = createHash(hashName);
            }
            return hash;
        }
        catch(Exception wfe) {
            wfe.printStackTrace();
            return null;
        }
    }

    /**
     * Check if the WaspHash exists
     * @param hashName name
     * @return
     */
    public boolean existsHash(String hashName) {
        try {
            String realname = Utils.md5(hashName);
            String directory = path+"/"+Utils.md5(dbName)+"/"+realname;
            return new File(directory).exists();
        }
        catch(Exception e) {
            return false;
        }
    }

    protected WaspHash getHash(String hashName) {
        try {
            String realname = Utils.md5(hashName);
            String directory = path+"/"+Utils.md5(dbName)+"/"+realname;
            if(new File(directory).exists()) { // already exists
                WaspHash hash = new WaspHash(cipherManager,directory);
                return hash;
            }
            else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected WaspHash createHash(String hashName) {
        try {
            String realname = Utils.md5(hashName);
            String directory = path+"/"+Utils.md5(dbName)+"/"+realname;
            if(!new File(directory).exists()) { // if not exists
                // create a new one
                if(new File(directory).mkdir()) {
                    WaspHash hash = new WaspHash(cipherManager,directory);

                    if(hashes==null) hashes = new ArrayList<String>();
                    hashes.add(hashName);
                    persist(); // update db data on disk
                    return hash;
                }
                else {
                    return null;
                }
            } else {
                return getHash(hashName);
            }
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Delete the specified WaspHash
     * @param hashName name
     * @return
     */
    public boolean removeHash(String hashName) {
        try {
            String realname = Utils.md5(hashName);
            String directory = path+"/"+Utils.md5(dbName)+"/"+realname;
            if(new File(directory).exists()) { // if exists
                // delete recursively
                try {
                    Utils.deleteRecursive(new File(directory));

                    if(hashes!=null) hashes.remove(hashName);
                    persist(); // update db data on disk

                    return true;
                }
                catch(Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Return a list of all WaspHash names associated to this database
     * @return
     */
    public List<String> getAllHashes() {
        return hashes;
    }

    protected String getName() throws NoSuchAlgorithmException {
        return Utils.md5(dbName);
    }

    protected void setName(String name) {
        this.dbName = name;
    }

    protected String getPath() {
        return path;
    }

    protected void setPath(String path) {
        this.path = path;
    }

    /**
     * Get information about this instance
     * @return a string containing some information
     */
    @Override
    public String toString() {
        return "WaspDb [name=" + dbName + ", path=" + path + ", cipher enabled = "
                + (cipherManager!=null) + "]";
    }

    private void persist() {
        WaspFactory.storeDatabase(this, cipherManager);
    }

    protected CipherManager getCipherManager() {
        return cipherManager;
    }

    protected void setCipherManager(CipherManager cm) {
        this.cipherManager = cm;
    }

    protected void clearCipherInformation() {
        this.cipherManager = null;
    }


}
