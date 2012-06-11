package com.sanchitkarve.ThalliumBackup;

import java.util.ArrayList;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class AppListViewItem
{  
    String appName;  
    String pkgName;
    String duration;
    boolean appChecked;
    Drawable appIcon;
    
    
    public AppListViewItem()
    {
    	
    }
    
    public AppListViewItem(ApplicationInfo ai)
    {
    	appName = ThalliumUtils.getApplicationLabel(ai);
    	pkgName = ai.packageName;
    	boolean isSystemApp = false;    	
    	if((ai.flags & (ai.FLAG_SYSTEM | ai.FLAG_UPDATED_SYSTEM_APP)) != 0) isSystemApp = true;
    	duration = isSystemApp ? "MAY NOT BE SAFE TO BACKUP OR RESTORE" : "Safe to Backup and Restore";
    	appChecked = false;    	
    	appIcon = ThalliumUtils.getApplicationIcon(ai);
    }
  
    public String getAppName() {  
        return appName;  
    }  
  
    public void setAppName(String title) {  
        this.appName = title;  
    }  
  
    public String getPackageName() {  
        return pkgName;  
    }  
  
    public void setPackageName(String pName) {  
        this.pkgName = pName;  
    }  
    
    public String getDuration() {  
        return duration;  
    }  
  
    public void setDuration(String duration) {  
        this.duration = duration;  
    }
  
    public Drawable getAppIcon() {  
        return appIcon;  
    }  
  
    public void setAppIcon(Drawable appIcon) {  
        this.appIcon = appIcon;  
    }  
    
    public boolean getAppChecked() {  
        return appChecked;  
    }  
  
    public void setAppChecked(boolean checked) {  
        this.appChecked = checked;  
    }
    
    public static ArrayList<AppListViewItem> newCopy(ArrayList<AppListViewItem> ori)
    {
    	ArrayList<AppListViewItem> newList = new ArrayList<AppListViewItem>();
    	for(int i=0; i < ori.size(); i++)
    	{
    		AppListViewItem item = new AppListViewItem();
    		item.setAppChecked(ori.get(i).getAppChecked());
    		item.setAppIcon(ori.get(i).getAppIcon());
    		item.setAppName(ori.get(i).getAppName());
    		item.setDuration(ori.get(i).getDuration());
    		item.setPackageName(ori.get(i).getPackageName());
    		newList.add(item);
    	}
    	return newList;
    }
}  
