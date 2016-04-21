package net.rehacktive.waspdb;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

/**
 * Created by stefano on 30/07/2015.
 */
public class MainTest extends InstrumentationTestCase {

    String path;
    String dbName = "justAtestDb";
    String dbPwd = "passw0rd!";

    Context ctx;

    WaspDb db;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        ctx = getInstrumentation().getContext();
        path = ctx.getFilesDir().getAbsolutePath();

        db = WaspFactory.openOrCreateDatabase(path,dbName,dbPwd);
    }

    public void testDatabaseCreation() {
        WaspFactory.openOrCreateDatabase(path, dbName, dbPwd, new WaspListener<WaspDb>() {
            @Override
            public void onDone(WaspDb ret) {
                assertTrue("testDatabaseCreation", WaspFactory.existsDatabase(path, dbName));
            }
        });

    }

    public void testCreateHash() throws Exception {
        db.openOrCreateHash("hash");
        assertTrue("testCreateHash", db.existsHash("hash"));
    }

    public void testPut() throws Exception {
        int id = 1;
        UserWithNestedContent p = new UserWithNestedContent(id, "test", "123");
        WaspHash hash = db.openOrCreateHash("hash");
        hash.flush();

        hash.put(id, p);

        UserWithNestedContent newPerson = hash.get(id);
        assertTrue("testPut", newPerson.equals(p) && newPerson.getFriends().size()==UserWithNestedContent.NUMBER_OF_FRIENDS);
    }

    public void testMultiplePut() throws Exception {
        WaspHash hash = db.openOrCreateHash("test12");
        hash.flush();

        Long start = System.currentTimeMillis();
        int count = 100;
        for (int i = 0; i < count; i++) {
            UserWithNestedContent user = new UserWithNestedContent(i, "b" + i, "");
            hash.put(user.getUsername(), user);
        }
        Long end = System.currentTimeMillis();
        Log.d("WASPDEBUG MULTIPLE",(end-start)+" ms");
        List<String> result = hash.getAllKeys();
        Long end2 = System.currentTimeMillis();
        Log.d("WASPDEBUG MULTIPLE",(end2-end)+" ms for reading all");
        assertTrue("testMultiplePut " + result.size(), result.size() == count);
    }

    public void testGetAllKeys() throws Exception {
        WaspHash hash = db.openOrCreateHash("users");
        hash.flush();

        int count = 10;
        for(int i=0; i<count; i++ ) {
            User p = new User(i, "test", "123");
            hash.put(i, p);
        }

        List<Integer> result = hash.getAllKeys();
        assertTrue("testGetAllKeys", result.size() == count);
    }

    public void testGetAllValues() throws Exception {
        WaspHash hash = db.openOrCreateHash("users");
        hash.flush();

        int count = 10;
        for(int i=0; i<count; i++ ) {
            User p = new User(i, "test", "123");
            hash.put(i, p);
        }

        List<User> result = hash.getAllValues();
        assertTrue("testGetAllValues", result.size() == count);
    }

    public void testGetAllData() throws Exception {
        WaspHash hash = db.openOrCreateHash("users");
        hash.flush();

        int count = 10;
        for(int i=0; i<count; i++ ) {
            User p = new User(i, "test", "123");
            hash.put(i, p);
        }

        HashMap<Integer,User> result = hash.getAllData();
        assertTrue("testGetAllData", result.size() == count);
    }

    public void testRemove() throws Exception {
        WaspHash hash = db.openOrCreateHash("deleteHash");

        UserWithNestedContent p = new UserWithNestedContent(1,"test","123");

        hash.put(1,p);

        hash.remove(1);

        assertTrue("testDelete", hash.getAllKeys().size() == 0);
    }

    public void testHashFlush() throws Exception {
        WaspHash hash = db.openOrCreateHash("anotherHash");

        UserWithNestedContent p = new UserWithNestedContent(1,"test","123");

        hash.put(1,p);

        hash.flush();

        assertTrue("testHashFlush", hash.getAllKeys().size() == 0);
    }

    public void testHashDelete() throws Exception {
        WaspHash hash = db.openOrCreateHash("anotherHashToDelete");
        if(hash!=null) {
            db.removeHash("anotherHashToDelete");
            assertTrue(!db.existsHash("anotherHashToDelete"));
        } else {
            fail();
        }
    }

    public void testDatabaseDestroy() throws Exception {
        // TODO
    }

    public void testObserver() throws Exception {
        int id = 2;
        UserWithNestedContent p = new UserWithNestedContent(id,"test","123");

        final WaspHash hash = db.openOrCreateHash("another_hash");
        hash.flush();

        hash.register(new WaspObserver() {
            @Override
            public void onChange() {
                assertTrue("testObserver", hash.getAllKeys().size() == 1);
            }
        });

        hash.put(id, p);
    }


}
