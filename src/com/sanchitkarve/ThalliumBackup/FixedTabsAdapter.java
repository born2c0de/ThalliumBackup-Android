package com.sanchitkarve.ThalliumBackup;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;

import com.astuetz.viewpager.extensions.TabsAdapter;
import com.astuetz.viewpager.extensions.ViewPagerTabButton;


public class FixedTabsAdapter implements TabsAdapter
{
	private Activity mContext;
	
	private String[] mTitles = {
	    "BACKUP", "RESTORE", "CLOUD", "ACCOUNT"
	};
	
	public FixedTabsAdapter(Activity ctx) {
		this.mContext = ctx;
	}

	@Override
	public View getView(int position) {
		// TODO Auto-generated method stub
		ViewPagerTabButton tab;
		
		LayoutInflater inflater = mContext.getLayoutInflater();		
		tab = (ViewPagerTabButton) inflater.inflate(R.layout.tab_fixed, null);
		
		if (position < mTitles.length) tab.setText(mTitles[position]);
		
		return tab;
	}

}
