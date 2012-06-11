package com.sanchitkarve.ThalliumBackup;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

public class ThalliumUtils
{
	private static Activity mContext;	
	
	public static void setContext(Activity context)
	{
		mContext = context;
	}
	
	public static List<ApplicationInfo> getInstalledApps()
	{	
		if(mContext != null)
		{
			final PackageManager pm = mContext.getPackageManager();			
			return pm.getInstalledApplications(PackageManager.GET_META_DATA);
		}
		else
			return null;
	}
	
	public static List<ApplicationInfo> getInstalledApps(boolean onlySupportedApps)
	{	
		if(mContext != null)
		{
			List<ApplicationInfo> ai = getInstalledApps();
			if(!onlySupportedApps) return ai;
			
			List<ApplicationInfo> res =new ArrayList<ApplicationInfo>();
			for(ApplicationInfo pi : ai)
			{
				if(isBackupSupportedForApp(pi.packageName))
				{
					res.add(pi);
				}
			}
			
			return res;			
		}
		else
			return null;
	}
	
	public static List<String> getInstalledAppNames(boolean onlySupportedApps)
	{
		List<ApplicationInfo> ai = getInstalledApps();
		if(ai != null)
		{
			List<String> packageNames = new ArrayList<String>();
			for(ApplicationInfo pi : ai)
			{				
				if(!onlySupportedApps || isBackupSupportedForApp(pi.packageName))
				{
					packageNames.add(pi.packageName);
				}				
			}
			return packageNames;
		}
		else
		{
			return null;
		}
	}	
	
	public static String[] getInstalledAppNamesAsArray(boolean onlySupportedApps)
	{
		List<String> isa = getInstalledAppNames(onlySupportedApps);
		if(isa == null) return null;
		String[] res = new String[isa.size()];
		isa.toArray(res);
		return res;
	}
	
	public static AppListViewItem[] getInstalledAppNamesAsAppListViewItem(boolean onlySupportedApps)
	{
		List<ApplicationInfo> isa = getInstalledApps(onlySupportedApps);
		if(isa == null) return null;
		AppListViewItem[] res = new AppListViewItem[isa.size()];
		for(int i=0; i < isa.size(); i++)
		{
			res[i] = new AppListViewItem(isa.get(i));
		}		
		return res;
	}
	
	public static ArrayList<AppListViewItem> getInstalledAppNamesAsAppListViewItemList(boolean onlySupportedApps)
	{
		List<ApplicationInfo> isa = getInstalledApps(onlySupportedApps);
		if(isa == null) return null;
		ArrayList<AppListViewItem> res = new ArrayList<AppListViewItem>();
		for(int i=0; i < isa.size(); i++)
		{
			AppListViewItem alvi = new AppListViewItem(isa.get(i));
			res.add(alvi);			
		}		
		return res;
	}
	
	public static Drawable getApplicationIcon(String packageName)
	{
		if(mContext == null) return null;
		
		try {
			return mContext.getPackageManager().getApplicationIcon(packageName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}
	
	public static String getApplicationLabel(ApplicationInfo aInfo)
	{
		if(mContext == null) return null;
		return mContext.getPackageManager().getApplicationLabel(aInfo).toString();
	}
	
	public static Drawable getApplicationIcon(ApplicationInfo ai)
	{
		if(mContext != null)
			return mContext.getPackageManager().getApplicationIcon(ai);
		else
			return null;
	}
	
	public static boolean isBackupSupportedForApp(String appPackageName)
	{
		boolean supported = true;
		if(appPackageName == null) return false;
		// UNTESTED APPS BUT PROBABLY BAD IDEA TO RESTORE DATA
		if(appPackageName.startsWith("com.android.") || appPackageName.startsWith("com.google.") || appPackageName.startsWith("android")) supported = false;
		if(appPackageName.startsWith("com.bel.android.") || appPackageName.startsWith("com.cyanogenmod.") || appPackageName.startsWith("com.koushikdutta.")) supported = false;
		if(appPackageName.startsWith("com.swype.") || appPackageName.startsWith("com.svox.") || appPackageName.startsWith("com.tmobile.theme")) supported = false;
		// UNTESTED APPS BUT PROBABLY USELESS OR USER UNLIKELY TO RESTORE DATA
		if(appPackageName.startsWith("jackpal.androidterm") || appPackageName.startsWith("com.noshufou.android.su")) supported = false;
		return supported;
	}

}
