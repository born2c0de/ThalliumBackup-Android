package com.sanchitkarve.ThalliumBackup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.astuetz.viewpager.extensions.FixedTabsView;
import com.astuetz.viewpager.extensions.TabsAdapter;
import com.stericson.RootTools.RootTools;

public class ThalliumBackupActivity extends Activity {
	private ViewPager mPager;	
	private FixedTabsView mFixedTabs;
	private ProgressDialog progDialog;
	
	
	private PagerAdapter mPagerAdapter;	
	private TabsAdapter mFixedTabsAdapter;
	
	
	public static final String EXTRA_EXAMPLE_TYPE = "EXTRA_EXAMPLE_TYPE";
	
	public enum ExampleType {
		SwipeyTabs, IndicatorLine, FixedTabs, FixedIconTabs, ScrollingTabs, SanchitTest
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
		

    	boolean isPhoneConfigSupported = true;
    	boolean isRootAccessGiven = true;
    	try
        {
        	if(!RootTools.isRootAvailable())
        	{        		
        		isPhoneConfigSupported = false;
        	}
        	else if(!RootTools.isBusyboxAvailable())
        	{
        		isPhoneConfigSupported = false;
        	}
        	
        	if(!RootTools.isAccessGiven())
        	{
        		isRootAccessGiven = false;        		
        	}
        	
        }catch(Exception e){e.printStackTrace();}
    	
    	if(isPhoneConfigSupported && isRootAccessGiven)    	
    	{
    		setContentView(R.layout.activity_fixed_tabs);
			initViewPager(4, 0xFFFFFFFF, 0xFF000000);
			mFixedTabs = (FixedTabsView) findViewById(R.id.fixed_tabs);
			mFixedTabsAdapter = new FixedTabsAdapter(this);
			mFixedTabs.setAdapter(mFixedTabsAdapter);
			mPager.setOffscreenPageLimit(3);
			mFixedTabs.setViewPager(mPager);
    	}
    	else
    	{
    		setContentView(R.layout.root_status);
    		if(isPhoneConfigSupported)
    		{
    			TextView busyboxstatus = (TextView)findViewById(R.id.lblIsRooted);
    			busyboxstatus.setText("FOUND");
    			busyboxstatus.setTextColor(Color.GREEN);
    		}
    		
    	}

    }
    
	private void initViewPager(int pageCount, int backgroundColor, int textColor) {
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ThalliumBackupPagerAdapter(this, pageCount, backgroundColor, textColor);
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(0);
		mPager.setPageMargin(1);		
	}
}