package com.sanchitkarve.ThalliumBackup;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class AppListViewAdapter extends BaseAdapter{  
	  
    ArrayList<AppListViewItem> itemList;  
  
    public Activity context;  
    public LayoutInflater inflater;  
  
    public AppListViewAdapter(Activity context,ArrayList<AppListViewItem> itemList) {  
        super();  
  
        this.context = context;  
        this.itemList = itemList;  
  
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
    }    
    
  
    @Override  
    public int getCount() {  
        // TODO Auto-generated method stub  
        return itemList.size();  
    }  
  
    @Override  
    public Object getItem(int position) {  
        // TODO Auto-generated method stub  
        return itemList.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
        // TODO Auto-generated method stub  
        return 0;  
    }  
  
    public static class ViewHolder  
    {  
        ImageView imgViewLogo;         
        TextView lblAppName;  
        TextView lblPkgName;
        TextView lblDuration;
        CheckBox chkAppChecked;
    }  
  
    @Override  
    public View getView(final int position, View convertView, ViewGroup parent) {  
        // TODO Auto-generated method stub  
  
        ViewHolder holder;  
        if(convertView==null)  
        {  
            holder = new ViewHolder();  
            convertView = inflater.inflate(R.layout.applist_row, null);  
  
            holder.imgViewLogo = (ImageView) convertView.findViewById(R.id.list_image);  
            holder.lblAppName = (TextView) convertView.findViewById(R.id.app_name);
            holder.lblPkgName = (TextView) convertView.findViewById(R.id.pkg_name);
            holder.lblDuration = (TextView) convertView.findViewById(R.id.duration);
            holder.chkAppChecked = (CheckBox) convertView.findViewById(R.id.chkAppSelected);
  
            convertView.setTag(holder);  
        }  
        else  
            holder=(ViewHolder)convertView.getTag();  
  
        AppListViewItem bean = (AppListViewItem) itemList.get(position);  
  
        holder.imgViewLogo.setImageDrawable(bean.getAppIcon());        
        holder.lblAppName.setText(bean.getAppName());
        holder.lblPkgName.setText(bean.getPackageName());
        holder.lblDuration.setText(bean.getDuration());
        holder.chkAppChecked.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckBox cb = (CheckBox) v.findViewById(R.id.chkAppSelected);
				itemList.get(position).setAppChecked(cb.isChecked());
			}
		});
        holder.chkAppChecked.setChecked(bean.getAppChecked());          
  
        return convertView;  
    }
    
    View.OnClickListener chkAppCheckedOnClick = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			   
		}
	};
  
}   