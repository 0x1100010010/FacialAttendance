package com.shahzadakhtar.attendancecam;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPrefs {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public MyPrefs(Context context) {

        sharedPreferences = context.getSharedPreferences("AttendencePrefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    public void setType(String type){
        editor.putString("type", type);
        editor.apply();
    }

    public String getType(){
        return sharedPreferences.getString("type", "null");
    }
    public void setGroupName(String type){
        editor.putString("GroupName", type);
        editor.apply();
    }

    public String getGroupName(){
        return sharedPreferences.getString("GroupName", "");
    }
}
