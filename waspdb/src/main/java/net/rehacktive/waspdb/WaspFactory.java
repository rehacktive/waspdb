package net.rehacktive.waspdb;

import android.os.AsyncTask;

import net.rehacktive.waspdb.internals.collision.KryoStoreUtils;
import net.rehacktive.waspdb.internals.cryptolayer.CipherManager;
import net.rehacktive.waspdb.internals.utils.Salt;
import net.rehacktive.waspdb.internals.utils.Utils;

import java.io.File;

public class WaspFactory {

    private static final String DB_NAME = "data.db";
    private static final String SALT_NAME = "salt";

    /**
     * Asynchronously open/create a WaspDb instance
     * the operation requires some time, according to device CPU power
     * @param path the path for the database folder - use Context.getFilesDir().getPath()
     * @param name name of the database
     * @param password password - set as null if you don't need encryption / for better performances
     * @param listener a WaspListener instance, to get the database when is ready
     */
    public static void openOrCreateDatabase(final String path, final String name, final String password, final WaspListener<WaspDb> listener) {
        new AsyncTask<Void,Void,Void>() {

            WaspDb db = null;

            @Override
            protected Void doInBackground(Void... params) {
                if(WaspFactory.existsDatabase(path, name)) {
                    db = WaspFactory.loadDatabase(path, name, password);
                } else {
                    db = WaspFactory.createDatabase(path, name, password);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(db!=null) {
                    listener.onDone(db);
                } else {
                    listener.onError("error on openOrCreateDatabase");
                }
            }
        }.execute();

    }

    /**
     * Synchronous call to create the database - use outside of the main thread!
     * @param path the path for the database folder - use Context.getFilesDir().getPath()
     * @param name name of the database
     * @param password password - set as null if you don't need encryption / for better performances
     * @return
     */
    public static WaspDb openOrCreateDatabase(String path, String name, String password) {
        if(WaspFactory.existsDatabase(path, name)) {
            return WaspFactory.loadDatabase(path, name, password);
        } else {
            return WaspFactory.createDatabase(path, name, password);
        }
    }

    /**
     * Destroy the database and remove all the data
     * @param db the database object to destroy
     * @return
     */
    public static boolean destroyDatabase(WaspDb db) {
        try {
            String directory = db.getPath()+"/"+Utils.md5(db.getName())+"/";
            if(new File(directory).exists()) { // if exists
                // delete recursively
                try {
                    Utils.deleteRecursive(new File(directory));
                    return true;
                }
                catch(Exception e) {
                    return false;
                }
            } else {
                return true;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // PROTECTED

    protected static boolean existsDatabase(String path, String name) {
        try {
            WaspDb db = new WaspDb();
            db.setPath(path);
            db.setName(name);
            String directory;
            directory = db.getPath()+"/"+db.getName();

            return (new File(directory).exists());
        } catch (Exception e) {
            return false;
        }
    }

    protected static WaspDb createDatabase(final String path, final String name, final String password)  {
        if(password!=null && !Utils.checkForCryptoAvailable()) return null;
        Salt salt = Utils.generateSalt();
        WaspDb db = new WaspDb();
        db.setName(name);
        db.setPath(path);
        try {
            CipherManager cipherManager = null;
            if (!Utils.isEmpty(password)) {
                cipherManager = CipherManager.getInstance(password.toCharArray(), salt.getSalt());
            }

            boolean ret = storeDatabase(db, cipherManager);
            if (ret) {
                KryoStoreUtils.serializeToDisk(salt, db.getPath()+"/"+db.getName()+"/"+SALT_NAME, null);
                db.setCipherManager(cipherManager); // set the ciphermanager in the object
                return db;
            } else return null;
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    protected static boolean storeDatabase(WaspDb db, CipherManager cipherManager)  {
        try {
            String directory = db.getPath()+"/"+db.getName();
            //if((new File(directory).exists())) throw new WaspFatalException("database already exists");
            boolean success = true;
            if(!(new File(directory).exists())) success = (new File(directory)).mkdir();
            if(success) {
                // do not store the cipherManager
                db.clearCipherInformation();
                KryoStoreUtils.serializeToDisk(db, db.getPath() + "/" + db.getName() + "/" + DB_NAME, cipherManager);
                db.setCipherManager(cipherManager);

                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    protected static WaspDb loadDatabase(final String path, final String name, final String password) {
        if(password!=null && !Utils.checkForCryptoAvailable()) return null;
        CipherManager cipherManager = null;
        try {
            String realname = Utils.md5(name);
            WaspDb db;

            Salt salt = (Salt) KryoStoreUtils.readFromDisk(path + "/" + realname +"/"+SALT_NAME,Salt.class, null);
            if(!Utils.isEmpty(password))
                cipherManager = CipherManager.getInstance(password.toCharArray(),salt.getSalt());
            db = (WaspDb) KryoStoreUtils.readFromDisk(path + "/" + realname + "/" + DB_NAME, WaspDb.class, cipherManager);

            db.setPath(path); // refresh the path to the current one
            db.setCipherManager(cipherManager); // set the password in the object
            return db;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
