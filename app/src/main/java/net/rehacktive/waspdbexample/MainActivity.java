package net.rehacktive.waspdbexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import net.rehacktive.waspdb.WaspDb;
import net.rehacktive.waspdb.WaspFactory;
import net.rehacktive.waspdb.WaspHash;
import net.rehacktive.waspdb.WaspListener;
import net.rehacktive.waspdb.WaspObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * Edited by AndroidMarv
 * Removed ActionBarActivity
 * reason Deprecation
 */

public class MainActivity extends Activity {

	ProgressBar progressBar;
	// wasp objects
	WaspDb db;
	WaspHash hash;
	WaspObserver observer;

	UserAdapter adapter;

	@Override
	protected void onCreate (Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		ListView userList = (ListView) findViewById(R.id.userlist);
		adapter = new UserAdapter(this);
		userList.setAdapter(adapter);

		if ( db == null ) {
			progressBar.setVisibility(View.VISIBLE);
			WaspFactory.openOrCreateDatabase(getFilesDir().getPath(), "example", "Passw0rd", new WaspListener<WaspDb>() {
				@Override
				public void onDone (WaspDb waspDb) {
					db = waspDb;
					hash = db.openOrCreateHash("users");

					progressBar.setVisibility(View.INVISIBLE);

					getUsers();

					observer = new WaspObserver() {
						@Override
						public void onChange () {
							List<User> users = hash.getAllValues();
							adapter.setUsers(users);
							adapter.notifyDataSetChanged();
						}
					};

					hash.register(observer);
				}
			});
		}
	}

	private void getUsers () {
		List<User> users = hash.getAllValues();
		if ( users == null )
			users = new ArrayList<>();

		adapter.setUsers(users);
		adapter.notifyDataSetChanged();
	}

	private void addUser () {
		User user = new User("user " + System.currentTimeMillis(), "");
		hash.put(user.getUser_name(), user);
	}

	private void flushUsers () {
		hash.flush();
	}

	@Override
	protected void onDestroy () {
		super.onDestroy();
		hash.unregister(observer);
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if ( id == R.id.add_user ) {
			addUser();
			return true;
		}

		if ( id == R.id.flush_user ) {
			flushUsers();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
