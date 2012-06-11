package com.sanchitkarve.ThalliumBackup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.astuetz.viewpager.extensions.R;

public class RegisterActivity extends Activity
{
	Button btnRegister;	
	EditText txtEmail, txtPassword, txtDeviceName;
	Spinner optRegions;
	ProgressDialog progDialog;
	transient Activity regActivity;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		btnRegister = (Button)findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(btnRegisterOnClick);		
		txtDeviceName = (EditText)findViewById(R.id.txtDeviceName);
		txtEmail = (EditText)findViewById(R.id.txtEmail);
		txtPassword = (EditText)findViewById(R.id.txtPassword);
		optRegions = (Spinner)findViewById(R.id.optRegions);
		txtDeviceName.setText(android.os.Build.PRODUCT);
		regActivity = this;
		
	}
	
	View.OnClickListener btnRegisterOnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// Add Registration Code.
			progDialog = ProgressDialog.show(regActivity, "Registration in Progress...", "Contacting Server");
			
			final String email = txtEmail.getText().toString();
			String password = null;
			try {
				password = SimpleSHA1.SHA1(txtPassword.getText().toString());
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String deviceName = txtDeviceName.getText().toString();
			String region = getRegionCode(optRegions.getSelectedItem().toString());
			

			//String urlString, String email, String password, String deviceID, String deviceName, String region
			new RegisterTask().execute("http://people.oregonstate.edu/~karves/thalliumbackup/register.php",email,password,android.os.Build.SERIAL,deviceName,region);
		}
	};
	
	private String getRegionCode(String regionString)
	{
		String result = "";		
		if(regionString.contentEquals("United States")) result = "US";
		else if(regionString.contentEquals("United States (Oregon)")) result = "US_OR";
		else if(regionString.contentEquals("United States (Northern California)")) result = "US_CA";
		else if(regionString.contentEquals("European Union (Ireland)")) result = "EU_IR";
		else if(regionString.contentEquals("Asia-Pacific (Singapore)")) result = "AP_SG";
		else if(regionString.contentEquals("Asia-Pacific (Tokyo)")) result = "AP_TK";
		else if(regionString.contentEquals("South America (Sao Paulo)")) result = "SA_SP";
		
		return result;
	}
	
	private String RegisterOnServer(String urlString, String email, String password, String deviceID, String deviceName, String region) {
		 
		String response = "failure";
		System.setProperty("http.keepAlive", "false");
		DefaultHttpClient hc=new DefaultHttpClient();  
		ResponseHandler <String> res=new BasicResponseHandler();  
		HttpPost postMethod=new HttpPost(urlString);  
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);    
		nameValuePairs.add(new BasicNameValuePair("username", email));    
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("deviceID", deviceID));
		nameValuePairs.add(new BasicNameValuePair("deviceName", deviceName));
		nameValuePairs.add(new BasicNameValuePair("region", region));
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
			response = e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;
}
	
	private class RegisterTask extends AsyncTask<String, String, String>
	{
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub			
			String response = RegisterOnServer(params[0], params[1], params[2], params[3], params[4], params[5]);
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
			if(result.contentEquals("Success"))
			{					
				finish();
				Toast.makeText(getBaseContext(), "Registration successful", Toast.LENGTH_LONG).show();
			}
			else
			{
				Toast.makeText(getBaseContext(), "Registration failed : " + result, Toast.LENGTH_LONG).show();
			}
		}
	}

}
