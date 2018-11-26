[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-WaspDb-green.svg?style=true)](https://android-arsenal.com/details/1/3482)

last release: 1.1.1

(note: this version is NOT compatible with 1.0, read the release message)


# WaspDb 
WaspDB is a pure Java key/value (NoSQL) database library for Android. It supports AES256 encryption for all the disk storage. It's very small (the aar file is ~189 KB).

Keys and Values are simple Java Objects. Everything is automatically serialized using the [Kryo](https://github.com/EsotericSoftware/kryo/) serialization library.

Data is stored by an implementation of hashmaps on disk.


### QuickStart

To use it with gradle (using jitpack.io):

Add it in your root build.gradle at the end of repositories:

```groovy
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```
Add the dependency

```groovy
dependencies {
	...
	compile 'com.github.rehacktive:waspdb:1.1.1'
}
```

Ok, let's start.
	
Let's assume a POJO (even with nested object, like Address):
```java
class User {
	private String username;
	private String email;
	private String telephone;
	private Address address;
	
	public User() {
	}
	
	// getters and setters...
}
```

No need to be Serializable or Parcelable or annotations or extending/implementing from other classes/interfaces, the only important thing is to have an **empty constructor**.
	
**Create a database, a WaspHash and store a POJO**
```java
// create a database, using the default files dir as path, database name and a password
String path = getFilesDir().getPath();
String databaseName = "myDb";
String password = "passw0rd";
    
WaspDb db = WaspFactory.openOrCreateDatabase(path,databaseName,password);
	
// now create an WaspHash, it's like a sql table
WaspHash users = db.openOrCreateHash("users");

// now let's have a POJO
User p = new User();
... // do your stuff with your POJO!

// and simply store it!
users.put(p.getUsername(), p);
```

**To retrieve it**, it's just
```java
User p = users.get("username1");
```

**Need all your objects?**
```java
List<User> allUsers = users.getAllValues();
```

It returns all the users, in a standard java List.

Or you can **get all the keys** used
```java
List<String> keys = users.getAllKeys();
```
or the actual **key/value map** (it's again a standard java class)
```java
HashMap<String, User> usersMap = users.getAllData();
```
Please note that the process of creating an encrypted database is computationally expensive (10000 iterations to create the AES256 key), so also an async method is available:
```java	
WaspFactory.openOrCreateDatabase(path, databaseName, password, new WaspListener<WaspDb>() {
        @Override
        public void onDone(WaspDb waspDb) {
            ....
        }
    });
```
	
### Why WaspDb
I **hate** to store objects on sqlite! It's a lot of boiler code...for what?
Okay, let's say in the polite way :)

	The object-relational impedance mismatch is a set of conceptual and technical difficulties that are often encountered when a relational database management system (RDBMS) is being used by a program written in an object-oriented programming language or style; particularly when objects or class definitions are mapped in a straightforward way to database tables or relational schema. 
(from wikipedia [https://en.wikipedia.org/wiki/Object-relational_impedance_mismatch](https://en.wikipedia.org/wiki/Object-relational_impedance_mismatch))

### What WaspDb is NOT
This is not a SQL database. It does not have a relational data model, it does not support SQL queries, and it provides no support for indexes, nor transactions.

### Features
- it's pure java, it's standalone, no native stuff, it's not an ORM to SqlLite!

- the database addresses up to 4294967296 keys for WaspHash...enough? :)

### Limitations
- it's NOT transactional. So if you wanna store 10000, it will make 10000 actual write operations to disk in sequence. That means it will be slower (in this case) compared to transactional databases. (of course, if you store 100 items as a java List - so actually a single object - it will make a single(ish) write operation)

### Proguard 
If you use Proguard in your project, add this to your proguard rules in order to skip WaspDB/Kryo classes:
	
	-keep class net.rehacktive.waspdb.** { *; }
	-keep class com.esotericsoftware.kryo.** { *; }

### Performances

I've used this project [https://github.com/Raizlabs/AndroidDatabaseLibraryComparison](https://github.com/Raizlabs/AndroidDatabaseLibraryComparison) to make some benchmarking.

This is the "simple trial" (storing 50 objects) on a Nexus 5, using WaspDb's encryption feature:

![image](/images/wasp_comparison.png)

It's even faster if you don't need encryption.
