package com.sanchitkarve.ThalliumBackup;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.R;
import com.stericson.RootTools.RootTools;

public class ThalliumBackupPagerAdapter extends PagerAdapter
{
protected transient Activity mContext;
boolean doIt = false;
	
	private int mLength = 0;
	private int mBackgroundColor = 0xFFFFFFFF;
	private int mTextColor = 0xFF000000;
	private int updatePosition = -1;
	ProgressDialog progDialog;
	EditText et;
	ListView lv1;
	EditText txtLoginEmail, txtLoginPassword;
	Button btnLogin;
	
	private boolean isLoggedIn = false;
	EditText txtLoggedInEmail;
	Button btnLogout;
	Spinner optDeviceIDList;
	
	Button btnBackupToCloud;	
	Button btnDownloadFromCloud;
	Button btnDeleteAllOnCloud;
	
	Button btnRestoreReset;
	Button btnBackupReset;
	
	SharedPreferences sharedPref;
	private String PREF_NAME = "THALLIUM_BACKUP";
	private String PREF_AUTHTOKEN = "AUTHTOKEN";
	private String PREF_LOGGEDINEMAIL = "LOGGEDINEMAIL";
	private String PREF_CURRENTDEVICEPROFILEID = "CURRENTDEVICEPROFILEID";
	private String PREF_TOTALDEVICEPROFILES = "TOTALDEVICEPROFILES";
	private String PREF_DEVICEPROFILENAMEX = "DEVICEPROFILENAME"; //Add value for x < PREF_TOTALDEVICEPROFILES manually
	private String PREF_DEVICEPROFILEIDX = "DEVICEPROFILEID"; //Add value for x < PREF_TOTALDEVICEPROFILES manually
	ArrayList<AppListViewItem> backupItemList;
	ArrayList<AppListViewItem> restoreItemList;
	
	private String[] mData = {
	    "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"
	};
	
	public ThalliumBackupPagerAdapter(Activity context, int length, int backgroundColor, int textColor) {
		mContext = context;
		mLength = length;
		mBackgroundColor = backgroundColor;
		mTextColor = textColor;		
		isLoggedIn = mContext.getSharedPreferences(PREF_NAME, 0).getString(PREF_AUTHTOKEN, "") == "" ? false : true;
		ThalliumUtils.setContext(mContext);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mLength;
	}
	
	 @Override
	    public int getItemPosition(Object object){
		 switch(updatePosition)
		 {
		 	case -1:
		 		return PagerAdapter.POSITION_NONE;
			 
		 	case 0:
		 		updatePosition = -1;
		 		return 0;
		 		
		 	case 1:
		 		updatePosition = -1;
		 		return 1;
		 		
		 	case 2:
		 		updatePosition = -1;
		 		return 2;
		 		
		 	case 3:
		 		updatePosition = -1;
		 		return 3;
		 
		 }
	        return PagerAdapter.POSITION_NONE;
	    }
	
	@Override
	public Object instantiateItem(View container, int position) {
		
		
		View v = new View(mContext.getApplicationContext());
		container.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
		switch(position)
		{
			case 0:
				v = inflater.inflate(R.layout.backup, null, false);
				Button b = (Button)v.findViewById(R.id.btnBackup);
				b.setOnClickListener(new View.OnClickListener() {
					
					@SuppressWarnings("unchecked")
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						progDialog = ProgressDialog.show(mContext, "Backup in Progress...", "Backing Up Selected Applications");
						new BackupAppsTask().execute(backupItemList);
						
					}
				});
				
				btnBackupReset = (Button)v.findViewById(R.id.btnBackupReset);
				btnBackupReset.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						for(int i=0;i<backupItemList.size();i++)
						{
							backupItemList.get(i).setAppChecked(false);
							ViewPager x = (ViewPager)mContext.findViewById(R.id.pager);
							x.getAdapter().notifyDataSetChanged();
						}
						
					}
				});
				
				
				ListView lvBackup = (ListView)v.findViewById(R.id.lstViewBackupApps);
				
				if(backupItemList == null)
					backupItemList = ThalliumUtils.getInstalledAppNamesAsAppListViewItemList(true);
								
				AppListViewAdapter backupAdapter = new AppListViewAdapter(mContext, backupItemList);
				
				lvBackup.setAdapter(backupAdapter);
				break;
			    
			case 1:
							
				v = inflater.inflate(R.layout.restore, null, false);				

				ListView lvRestore = (ListView)v.findViewById(R.id.lstViewRestoreApps);
				Button r = (Button)v.findViewById(R.id.btnRestore);
				r.setOnClickListener(new View.OnClickListener() {
					
					@SuppressWarnings("unchecked")
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						progDialog = ProgressDialog.show(mContext, "Restore in Progress...", "Restoring Selected Applications");
						new RestoreAppsTask().execute(restoreItemList);
					}
				});
				btnRestoreReset = (Button)v.findViewById(R.id.btnRestoreReset);
				btnRestoreReset.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						for(int i=0;i<restoreItemList.size();i++)
						{
							restoreItemList.get(i).setAppChecked(false);
							ViewPager x = (ViewPager)mContext.findViewById(R.id.pager);
							x.getAdapter().notifyDataSetChanged();
						}
						
					}
				});
				
				restoreItemList = new ArrayList<AppListViewItem>();
				
				ArrayList<AppListViewItem> cloneBackupList = AppListViewItem.newCopy(backupItemList);
				
				for(int i=0;i<cloneBackupList.size();i++)
				{
					String sdCardDirectory = mContext.getString(R.string.sdCardDirectory);
					File sdCard = Environment.getExternalStorageDirectory();
					File appSdDir = new File(sdCard.getAbsolutePath() + "/" + sdCardDirectory);
					File checkFileExist = new File(appSdDir.getAbsolutePath() + "/" + cloneBackupList.get(i).getPackageName() + ".tar.gz");					
					if(checkFileExist.exists())
					{
						AppListViewItem item = cloneBackupList.get(i);
						item.setAppChecked(false);
						restoreItemList.add(item);
					}
				}
				AppListViewAdapter restoreAdapter = new AppListViewAdapter(mContext, restoreItemList);
				
				lvRestore.setAdapter(restoreAdapter);
				
			    break;
			    
			case 2:
				v = inflater.inflate(R.layout.cloud, null, false);
				Button delAllLocalBackups = (Button)v.findViewById(R.id.btnDeleteLocalContent);
				btnBackupToCloud = (Button)v.findViewById(R.id.btnBackupToCloud);
				btnBackupToCloud.setOnClickListener(btnBackupToCloudOnClick);			
				
				btnDownloadFromCloud = (Button)v.findViewById(R.id.btnDownloadFromCloud);
				btnDownloadFromCloud.setOnClickListener(btnDownloadFromCloudOnClick);
				btnDeleteAllOnCloud = (Button)v.findViewById(R.id.btnDeleteAllOnCloud);
				btnDeleteAllOnCloud.setOnClickListener(btnDeleteAllOnCloudOnClick);
				delAllLocalBackups.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//TODO: Ask if user is sure.
						//TODO: Then use AsyncTask and ProgressDialog to show progress as files are deleted.
						try
						{
							String dirToDel = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mContext.getString(R.string.sdCardDirectory);
							List<String> res = RootTools.sendShell("rm -r " + dirToDel + "/*", 15000);
							ViewPager x = (ViewPager)mContext.findViewById(R.id.pager);
							x.getAdapter().notifyDataSetChanged();
							Toast.makeText(mContext, "Local Backups deleted", Toast.LENGTH_LONG).show();
						}
						catch(Exception e){e.printStackTrace();}
						
					}
				});			
			    break;
			    
			case 3:				
				// Add code to see if authtoken is available from sharedpreferences
				if(!isLoggedIn)
				{
					v = inflater.inflate(R.layout.login,null,false);
					TextView link_to_register = (TextView)v.findViewById(R.id.link_to_register);
					link_to_register.setOnClickListener(linkToRegisterClick);
					txtLoginEmail = (EditText)v.findViewById(R.id.txtLoginEmail);
					txtLoginPassword = (EditText)v.findViewById(R.id.txtLoginPassword);
					btnLogin = (Button)v.findViewById(R.id.btnLogin);
					btnLogin.setOnClickListener(btnLoginOnClick);
					v.setTag(1);
				}
				else
				{
					v = inflater.inflate(R.layout.login_status,null,false);
					txtLoggedInEmail = (EditText)v.findViewById(R.id.txtLoggedInEmail);
					txtLoggedInEmail.setText(mContext.getSharedPreferences(PREF_NAME, 0).getString(PREF_LOGGEDINEMAIL, ""));
					btnLogout = (Button)v.findViewById(R.id.btnLogout);
					btnLogout.setOnClickListener(btnLogoutOnClick);
					optDeviceIDList = (Spinner)v.findViewById(R.id.optDeviceIDList);					
					ArrayList<String> savedDeviceProfiles = getDeviceProfilesFromPreferences();
					ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mContext.getBaseContext(), android.R.layout.simple_spinner_item, savedDeviceProfiles);
					optDeviceIDList.setAdapter(spinnerArrayAdapter);
					optDeviceIDList.setSelection(getCurrentDeviceIDLocationFromList(savedDeviceProfiles));
					v.setTag(2);
					
				}							
					break;
			default:
				
		}
		
	    
		
	    ((ViewPager) container).addView(v, 0);
	    return v;
	}
	
	private String getDeviceIDFromName(String devicename)
	{		
		int loc = -1;
		ArrayList<String> dps = getDeviceProfilesFromPreferences();
		for(int i=0;i<dps.size();i++)
		{
			if(dps.get(i).contentEquals(devicename))
			{
				loc = i;
				break;
			}
		}
		return mContext.getSharedPreferences(PREF_NAME, 0).getString(PREF_DEVICEPROFILEIDX + String.valueOf(loc+1), "");
	}
	
	private int getCurrentDeviceIDLocationFromList(
			ArrayList<String> savedDeviceProfiles) {
		// TODO Auto-generated method stub
		for(int i=0;i<savedDeviceProfiles.size();i++)
		{
			if(getDeviceIDFromName(savedDeviceProfiles.get(i)).contentEquals(android.os.Build.SERIAL))
				return i;
		}
		return -1;
	}

	private ArrayList<String> getDeviceProfilesFromPreferences() {
		// TODO Auto-generated method stub
		ArrayList<String> result = new ArrayList<String>();
		sharedPref = mContext.getSharedPreferences(PREF_NAME, 0);
		int len = sharedPref.getInt(PREF_TOTALDEVICEPROFILES, 0);
		if(len == 0) return null;
		for(int i=0;i<len;i++)
		{
			result.add(sharedPref.getString(PREF_DEVICEPROFILENAMEX + String.valueOf(i+1), ""));
		}
		return result;
		
	}
	
	View.OnClickListener btnDeleteAllOnCloudOnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String authToken = mContext.getSharedPreferences(PREF_NAME, 0).getString(PREF_AUTHTOKEN, "");
			if(!authToken.contentEquals(""))
			{
				progDialog = ProgressDialog.show(mContext, "Deleting backups on Cloud...", "Deleting backups on Cloud");
				new DeleteBackupsOnCloudTask().execute(authToken,android.os.Build.SERIAL);
			}
			else
				Toast.makeText(mContext.getBaseContext(), "Please login first.", Toast.LENGTH_LONG).show();
		}
	};
	
	View.OnClickListener btnDownloadFromCloudOnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String authToken = mContext.getSharedPreferences(PREF_NAME, 0).getString(PREF_AUTHTOKEN, "");
			if(!authToken.contentEquals(""))
			{
				progDialog = ProgressDialog.show(mContext, "Downloading backups from Cloud...", "Getting backup listings");
				new DownloadBackupsFromCloudTask().execute(authToken,android.os.Build.SERIAL);
			}
			else
				Toast.makeText(mContext.getBaseContext(), "Please login first.", Toast.LENGTH_LONG).show();
		}
	};
	
	View.OnClickListener btnBackupToCloudOnClick = new View.OnClickListener() {
		
		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ArrayList<AppListViewItem> cloneBackupList = restoreItemList;
			ArrayList<String> filesToBackup = new ArrayList<String>();
			for(int i=0;i<cloneBackupList.size();i++)
			{
				String sdCardDirectory = mContext.getString(R.string.sdCardDirectory);
				File sdCard = Environment.getExternalStorageDirectory();
				File appSdDir = new File(sdCard.getAbsolutePath() + "/" + sdCardDirectory);
				File checkFileExist = new File(appSdDir.getAbsolutePath() + "/" + cloneBackupList.get(i).getPackageName() + ".tar.gz");					
				if(checkFileExist.exists())
				{
					filesToBackup.add(checkFileExist.getAbsolutePath());
					
				}
			}
			progDialog = ProgressDialog.show(mContext, "Upload in Progress...", "Uploading Applications to Server");
			new UploadTask().execute(filesToBackup);
			
		}
	};
	
	View.OnClickListener btnLogoutOnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SharedPreferences.Editor ed = mContext.getSharedPreferences(PREF_NAME, 0).edit();
			ed.putString(PREF_AUTHTOKEN, "");
			ed.putString(PREF_LOGGEDINEMAIL, "");
			ed.putString(PREF_CURRENTDEVICEPROFILEID, "");
			ed.putInt(PREF_TOTALDEVICEPROFILES, 0);
			ed.commit();
			isLoggedIn = false;
			ViewPager x = (ViewPager)mContext.findViewById(R.id.pager);
			x.getAdapter().notifyDataSetChanged();
		}
	};

	View.OnClickListener btnLoginOnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			progDialog = ProgressDialog.show(mContext, "Login in Progress...", "Logging in to server");
			String username = txtLoginEmail.getText().toString();
			String password = null;
			try {
				password = SimpleSHA1.SHA1(txtLoginPassword.getText().toString());
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			new LoginTask().execute("http://people.oregonstate.edu/~karves/thalliumbackup/login.php",username,password);
		}
	};
	
	View.OnClickListener linkToRegisterClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent i = new Intent(mContext,RegisterActivity.class);
			mContext.startActivity(i);
		}
	};
	
	View.OnClickListener testAddDelPage = new View.OnClickListener() {
		
		@Override
		public void onClick(View vv) {
			// TODO Auto-generated method stub
			ViewPager x = (ViewPager)mContext.findViewById(R.id.pager);			
			View v = new View(mContext.getApplicationContext());			
		    final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    v = inflater.inflate(R.layout.login, null, false);
		    v.setTag(1);
		    x.setCurrentItem(0);
		    x.removeView((View)x.getTag(2));
		    //x.removeViewAt(3);
		    x.addView(v);		    
		    //x.addView(v, 3);
			doIt = true;		    
		    x.getAdapter().notifyDataSetChanged();		    
		    
		}
	};
	
	AdapterView.OnItemClickListener lv1OnItemClick = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			ListView v = (ListView)arg0;
			v.setItemChecked(arg2, v.isItemChecked(arg2));
			Toast.makeText(mContext, "selected", Toast.LENGTH_LONG).show();
		}
	};
	
	View.OnClickListener button1OnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			View vx = new View(mContext.getApplicationContext());
			final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vx = inflater.inflate(R.layout.test, null, false);
			//EditText et = (EditText)vx.findViewById(R.id.editText1);
			String s = et.getText().toString();
			Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
			et.setText("fuck off");
			
			
			
		}
	};

	@Override
	public boolean isViewFromObject(View view, Object object) {
		// TODO Auto-generated method stub
		return view == ((View) object);
	}
	
	@Override
	public void finishUpdate(View container) {}
	
	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {}
	
	@Override
	public Parcelable saveState() {
		return null;
	}
	
	@Override
	public void startUpdate(View container) {}

	@Override
	public void destroyItem(View container, int position, Object view) {
		// TODO Auto-generated method stub
		((ViewPager) container).removeView((View) view);
	}
	
	private String DeleteBackupsOnCloud(String urlString, String authToken, String deviceID) {
		 
		String response = "failure";
		DefaultHttpClient hc=new DefaultHttpClient();  
		ResponseHandler <String> res=new BasicResponseHandler();  
		HttpPost postMethod=new HttpPost(urlString);  
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();    
		nameValuePairs.add(new BasicNameValuePair("authToken", authToken));    
		nameValuePairs.add(new BasicNameValuePair("deviceID", deviceID));		
		try {
			postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
		try {
			response=hc.execute(postMethod,res);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response = e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	
	private class DeleteBackupsOnCloudTask extends AsyncTask<String, String, String>
	{


		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String response = "";
			response = DeleteBackupsOnCloud("http://people.oregonstate.edu/~karves/thalliumbackup/delete.php", params[0], params[1]);
				return response;
		}
		
		protected void onProgressUpdate(String... progress)
		{
			progDialog.setMessage(progress[0]);			
		}
		
		protected void onPreExecute()
		{
	
		}
		@Override
		protected void onPostExecute(String result)
		{
			//finish up ( or close the progressbar )

			progDialog.dismiss();
	
			super.onPostExecute(result);
			
			// separate authtoken and device profiles
			if(result.contains("Success"))
			{				
				ViewPager x = (ViewPager)mContext.findViewById(R.id.pager);						    
			    x.getAdapter().notifyDataSetChanged();	
				Toast.makeText(mContext.getBaseContext(), "All files on Cloud Deleted", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(mContext.getBaseContext(), "Could not delete files on Cloud.", Toast.LENGTH_LONG).show();
			}			
		}
			
	}
	
	private String DownloadLinksFromCloud(String urlString, String authToken, String deviceID) {
		 
		String response = "failure";
		DefaultHttpClient hc=new DefaultHttpClient();  
		ResponseHandler <String> res=new BasicResponseHandler();  
		HttpPost postMethod=new HttpPost(urlString);  
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();    
		nameValuePairs.add(new BasicNameValuePair("authToken", authToken));    
		nameValuePairs.add(new BasicNameValuePair("deviceID", deviceID));		
		try {
			postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
		try {
			response=hc.execute(postMethod,res);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response = e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
	}
	
	private String DownloadBackupToSD(String link, String fileName)
	{
		int TIMEOUT_CONNECTION = 5000;//5sec
		int TIMEOUT_SOCKET = 30000;//30sec


		            URL url = null;
					try {
						url = new URL(link);
					} catch (MalformedURLException e3) {
						// TODO Auto-generated catch block
						e3.printStackTrace();
						return "0";
					}


		            //Open a connection to that URL.
		            URLConnection ucon = null;
					try {
						ucon = url.openConnection();
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
						return "0";
					}

		            //this timeout affects how long it takes for the app to realize there's a connection problem
		            ucon.setReadTimeout(TIMEOUT_CONNECTION);
		            ucon.setConnectTimeout(TIMEOUT_SOCKET);


		            //Define InputStreams to read from the URLConnection.
		            // uses 3KB download buffer
		            InputStream is = null;
					try {
						is = ucon.getInputStream();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return "0";
					}
		            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
		            FileOutputStream outStream = null;
					try {
						outStream = new FileOutputStream(fileName);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return "0";
					}
		            byte[] buff = new byte[5 * 1024];

		            //Read bytes (and store them) until there is nothing more to read(-1)
		            int len;
		            try {
						while ((len = inStream.read(buff)) != -1)
						{
						    outStream.write(buff,0,len);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return "0";
					}

		            //clean up
		            try {
						outStream.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return "0";
					}
		            try {
						outStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return "0";
					}
		            try {
						inStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return "0";
					}
		            return "1";

	}
	
	
	private class DownloadBackupsFromCloudTask extends AsyncTask<String, String, String>
	{
		//ProgressDialog x;

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub			

			String response = "";
			String allLinks = DownloadLinksFromCloud("http://people.oregonstate.edu/~karves/thalliumbackup/download.php", params[0], params[1]);
			if(!allLinks.contains("NOFILES"))
			{
				String[] links = allLinks.split(" ");
				for(int i=0;i<links.length;i++)
				{
					//	strip off uid from filename (consider only link after _ )
					//put the actual name in filename with full path
					String sdCardDirectory = mContext.getString(R.string.sdCardDirectory);
					File sdCard = Environment.getExternalStorageDirectory();
					File appSdDir = new File(sdCard.getAbsolutePath() + "/" + sdCardDirectory);

					int fNameStart = links[i].indexOf("_");
					int fNameEnd = links[i].indexOf("?AWS");
					String filename = links[i].substring(fNameStart + 1,fNameEnd);//, fNameEnd - fNameStart + 7);
					Log.d("SANCHIT", filename);
					String fileNameWithPath = new File(appSdDir.getAbsolutePath() + "/" + filename).getAbsolutePath();
					Log.d("SANCHIT", fileNameWithPath);
					publishProgress("Downloading " + filename);
					response = DownloadBackupToSD(links[i], fileNameWithPath);
				}
				return response;
			}
			else
				return allLinks; //NOFILES
			
			
		}
		
		protected void onProgressUpdate(String... progress)
		{
			progDialog.setMessage(progress[0]);			
		}
		
		protected void onPreExecute()
		{
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			//finish up ( or close the progressbar )
		
			progDialog.dismiss();
			
			super.onPostExecute(result);
			
			// separate authtoken and device profiles
			if(!result.contains("0") && !result.contentEquals("NOFILES"))
			{				
				ViewPager x = (ViewPager)mContext.findViewById(R.id.pager);						    
			    x.getAdapter().notifyDataSetChanged();	
				Toast.makeText(mContext.getBaseContext(), "Download Success", Toast.LENGTH_LONG).show();
			}
			else if(result.contentEquals("NOFILES"))
			{
				Toast.makeText(mContext.getBaseContext(), "No files found on cloud.", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(mContext.getBaseContext(), "Download failed : " + result, Toast.LENGTH_LONG).show();
			}
			
		}
			
	}
	
	private String uploadFileToServer(String urlServer, String filePath, String fileName)
	{
		HttpURLConnection connection = null;
		DataOutputStream outputStream = null;
		DataInputStream inputStream = null;

		
		String pathToOurFile = filePath;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary =  "*****";
		String serverResponseMessage = "";

		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;

		try
		{
		FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );

		URL url = new URL(urlServer);
		connection = (HttpURLConnection) url.openConnection();

		// Allow Inputs & Outputs
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);

		// Enable POST method
		connection.setRequestMethod("POST");

		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

		outputStream = new DataOutputStream( connection.getOutputStream() );
		outputStream.writeBytes(twoHyphens + boundary + lineEnd);
		
		outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedFile\";filename=\"" + fileName +"\"" + lineEnd);
		outputStream.writeBytes(lineEnd);

		bytesAvailable = fileInputStream.available();
		bufferSize = Math.min(bytesAvailable, maxBufferSize);
		buffer = new byte[bufferSize];

		// Read file
		bytesRead = fileInputStream.read(buffer, 0, bufferSize);

		while (bytesRead > 0)
		{
		outputStream.write(buffer, 0, bufferSize);
		bytesAvailable = fileInputStream.available();
		bufferSize = Math.min(bytesAvailable, maxBufferSize);
		bytesRead = fileInputStream.read(buffer, 0, bufferSize);
		}

		outputStream.writeBytes(lineEnd);
		outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

		// Responses from the server (code and message)
				
		int serverResponseCode = connection.getResponseCode();		
		serverResponseMessage = connection.getResponseMessage();
		BufferedReader r = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
		    total.append(line);
		}
		serverResponseMessage = total.toString();
		fileInputStream.close();
		outputStream.flush();
		outputStream.close();
		}
		catch (Exception ex)
		{
		//Exception handling
		}
		return serverResponseMessage;
	}
	
	private class UploadTask extends AsyncTask<ArrayList<String>, String, String>
	{
		

		@Override
		protected String doInBackground(ArrayList<String>... params) {
			// TODO Auto-generated method stub			
			
			String response = "";
			String authToken = mContext.getSharedPreferences(PREF_NAME, 0).getString(PREF_AUTHTOKEN, "");
			if(authToken.contentEquals(""))
				return "authToken_empty";
			
			for(int i=0; i< params[0].size(); i++)
			{
				String fName = new File(params[0].get(i)).getName();
				publishProgress("Uploading : " + fName);
				response = uploadFileToServer("http://people.oregonstate.edu/~karves/thalliumbackup/upload.php?authToken=" + authToken + "&deviceID=" + android.os.Build.SERIAL, params[0].get(i), fName);
				if(response.startsWith("Success"))
				{
					publishProgress("Uploaded : " + fName);
				}
			}
			return response;			
		}
		
		protected void onProgressUpdate(String... progress)
		{
			progDialog.setMessage(progress[0]);			
		}
		
		protected void onPreExecute()
		{
				
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			//finish up ( or close the progressbar )

			progDialog.dismiss();
	
			super.onPostExecute(result);
			
			// separate authtoken and device profiles
			if(!result.startsWith("Error:"))
			{
				ViewPager x = (ViewPager)mContext.findViewById(R.id.pager);						    
			    x.getAdapter().notifyDataSetChanged();	
				Toast.makeText(mContext.getBaseContext(), "Upload Success", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(mContext.getBaseContext(), "Upload failed: " + result, Toast.LENGTH_LONG).show();
			}
			
		}
			
	}
	
	private String LoginToServer(String urlString, String email, String password) {
		 
		String response = "failure";
		DefaultHttpClient hc=new DefaultHttpClient();  
		ResponseHandler <String> res=new BasicResponseHandler();  
		HttpPost postMethod=new HttpPost(urlString);  
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();    
		nameValuePairs.add(new BasicNameValuePair("username", email));    
		nameValuePairs.add(new BasicNameValuePair("password", password));		
		try {
			postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
		try {
			response=hc.execute(postMethod,res);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response = e.getMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
}
	
	private class LoginTask extends AsyncTask<String, String, String>
	{


		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub			
			String response = LoginToServer(params[0], params[1], params[2]);
			return response;			
		}
		
		protected void onProgressUpdate(String... progress)
		{
			progDialog.setMessage(progress[0]);			
		}
		
		protected void onPreExecute()
		{
	
		}
		@Override
		protected void onPostExecute(String result)
		{
			//finish up ( or close the progressbar )

			progDialog.dismiss();
	
			super.onPostExecute(result);
			
			// separate authtoken and device profiles
			if(!result.startsWith("Error:") && !result.startsWith("failure"))
			{
				String[] resultTokens = result.split(":");
				SharedPreferences.Editor ed = mContext.getSharedPreferences(PREF_NAME, 0).edit();
				ed.putString(PREF_AUTHTOKEN, resultTokens[0]);
				ed.putString(PREF_LOGGEDINEMAIL, txtLoginEmail.getText().toString());
				int dpLength = (resultTokens.length - 1) / 2;
				ed.putInt(PREF_TOTALDEVICEPROFILES, dpLength);
				int ctr = 1;
				for(int i=0;i<dpLength;i++)
				{
					ed.putString(PREF_DEVICEPROFILEIDX + String.valueOf(i+1), resultTokens[ctr]);
					ed.putString(PREF_DEVICEPROFILENAMEX + String.valueOf(i+1), resultTokens[ctr+1]);
					ctr+=2;
				}				
				ed.commit();
				isLoggedIn = true;
				ViewPager x = (ViewPager)mContext.findViewById(R.id.pager);						    
			    x.getAdapter().notifyDataSetChanged();	
				Toast.makeText(mContext.getBaseContext(), "Login Success", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(mContext.getBaseContext(), "Login failed: " + result, Toast.LENGTH_LONG).show();
			}
			
		}
			
	}
	
	
	private class BackupAppsTask extends AsyncTask<ArrayList<AppListViewItem>, String, Integer>
	{


		@Override
		protected Integer doInBackground(ArrayList<AppListViewItem>... params) {
			// TODO Auto-generated method stub
			String sdCardDirectory = mContext.getString(R.string.sdCardDirectory);
			try {
				for(int i=0;i<params[0].size();i++)
				{					
					if(params[0].get(i).getAppChecked())
					{
						publishProgress(params[0].get(i).getPackageName());
						File sdCard = Environment.getExternalStorageDirectory();
						File x = new File(sdCard.getAbsolutePath() + "/" + sdCardDirectory);
						if(!x.exists())
						{
							boolean s = x.mkdirs();
						}
						else
						{

						}
						String xdir = params[0].get(i).getPackageName();
						File newDir = new File(x.getAbsolutePath() + "/" + xdir);
						if(!newDir.exists())
						{
							boolean s = newDir.mkdirs();
						}
						try
						{

							List<String> res = RootTools.sendShell("cp -r /data/data/" + xdir + "/!(cache) " + newDir.getAbsolutePath(), 15000);

							res = RootTools.sendShell("tar -zcvf " + x.getAbsolutePath() + "/" + xdir + ".tar.gz -C " + x.getAbsolutePath() + " " + xdir,15000);
							res = RootTools.sendShell("rm -r " + x.getAbsolutePath() + "/" + xdir,10000);
						}
						catch(Exception e){e.printStackTrace();}					
						
						
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onProgressUpdate(String... progress)
		{
			progDialog.setMessage(progress[0]);			
		}
		
		protected void onPreExecute()
		{
			
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			//finish up ( or close the progressbar )
		
			progDialog.dismiss();
			//updatePosition = 1;
			ViewPager x = (ViewPager)mContext.findViewById(R.id.pager);
		
			x.getAdapter().notifyDataSetChanged();	
			super.onPostExecute(result);
		}
	}
	
	private class RestoreAppsTask extends AsyncTask<ArrayList<AppListViewItem>, String, Integer>
	{
		

		@Override
		protected Integer doInBackground(ArrayList<AppListViewItem>... params) {
			// TODO Auto-generated method stub
			String sdCardDirectory = mContext.getString(R.string.sdCardDirectory);
			try {
				for(int i=0;i<params[0].size();i++)
				{					
					if(params[0].get(i).getAppChecked())
					{
						publishProgress(params[0].get(i).getPackageName());
						File sdCard = Environment.getExternalStorageDirectory();
						String xdir = params[0].get(i).getPackageName();
						File x = new File(sdCard.getAbsolutePath() + "/" + sdCardDirectory);
						String cmd = "tar zxvf " + x.getAbsolutePath() + "/" + xdir + ".tar.gz -C /data/data";
								
						List<String> res = RootTools.sendShell("rm -r " + "/data/data/" + xdir + "/*",10000);
						res = RootTools.sendShell(cmd, 10000);
						
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onProgressUpdate(String... progress)
		{
			progDialog.setMessage(progress[0]);			
		}
		
		protected void onPreExecute()
		{
			
		}
		
		@Override
		protected void onPostExecute(Integer result)
		{
			//finish up ( or close the progressbar )
		
			progDialog.dismiss();
		
			ViewPager x = (ViewPager)mContext.findViewById(R.id.pager);
		
			x.getAdapter().notifyDataSetChanged();	
			super.onPostExecute(result);
		}
	}
	

}
