package com.test.autoupdateapk;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;


// 外部調用的使用方法: 
// adb shell am start -n com.test.autoupdateapk/.install -e APK /sdcard/xxx.apk

public class install extends Activity 
{
	
	public static final String INTENTMODE_ARG_APKNAME = "APK";
	String apk_pathfile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getIntent().hasExtra(INTENTMODE_ARG_APKNAME)) {
			apk_pathfile = getIntent().getStringExtra(INTENTMODE_ARG_APKNAME);
 		
		
	// ------------------------------測試用, 安裝 apk
			Intent install = new Intent(Intent.ACTION_VIEW);
			//Uri apkUri = Uri.fromFile(new File("/sdcard/M.apk"));
			Uri apkUri = Uri.fromFile(new File(apk_pathfile));
			install.setDataAndType(apkUri, "application/vnd.android.package-archive");
		 	startActivity(install);
			//------------------------------
		 
		 	//execRootCmdSilent("sendevent /dev/input/event2 3 0 50");
		 	//execRootCmdSilent("sendevent /dev/input/event2 3 1 785");
		 	
		 	new Handler().postDelayed(new Runnable(){  
		 	     public void run() {
		 	      //execute the task  
		 	     
		 	    	DisplayMetrics metrics = new DisplayMetrics();
		 		 	getWindowManager().getDefaultDisplay().getMetrics(metrics);
		 		 	int height=  metrics.heightPixels;
		 		 	int width = metrics.widthPixels;
		 		 	
		 	    	execRootCmdSilent("sendevent /dev/input/event2 3 0 "+(50));
		 		 	execRootCmdSilent("sendevent /dev/input/event2 3 1 "+(height-10));
		 		 	execRootCmdSilent("sendevent /dev/input/event2 1 330 1");
		 		 	execRootCmdSilent("sendevent /dev/input/event2 0 0 0");
		 		 	execRootCmdSilent("sendevent /dev/input/event2 1 330 0");
		 		 	execRootCmdSilent("sendevent /dev/input/event2 0 0 0");
		 		 	
		 	     }  
		 	  },1000);  //設定 1秒後 模擬點下 [INSTALL]
		 	
		 	new Handler().postDelayed(new Runnable(){  
		 	     public void run() {
		 	      //execute the task  
		 	     
		 	    	DisplayMetrics metrics = new DisplayMetrics();
		 		 	getWindowManager().getDefaultDisplay().getMetrics(metrics);
		 		 	int height=  metrics.heightPixels;
		 		 	int width = metrics.widthPixels;
		 		 	
		 	    	execRootCmdSilent("sendevent /dev/input/event2 3 0 "+(width-50));
		 		 	execRootCmdSilent("sendevent /dev/input/event2 3 1 "+(height-10));
		 		 	execRootCmdSilent("sendevent /dev/input/event2 1 330 1");
		 		 	execRootCmdSilent("sendevent /dev/input/event2 0 0 0");
		 		 	execRootCmdSilent("sendevent /dev/input/event2 1 330 0");
		 		 	execRootCmdSilent("sendevent /dev/input/event2 0 0 0");
		 		 	finish();
		 	     }  
		 	  },15000);//設定 15秒後 模擬點下 [DONE]	 	
		 	
		  } 
		
		finish();
	}
	
	//-------------------------------------------------------------     
    // 執行命令但不關注結果輸出
    public static  int execRootCmdSilent(String cmd) 
    { 
    	int result = - 1; 
    	DataOutputStream dos = null; 
    	try 
    	{ 
    		Process p = Runtime.getRuntime().exec("su"); 
    		dos = new DataOutputStream(p.getOutputStream()); 
    		Log.i( "RootCmd", cmd); 
    		dos.writeBytes( cmd + "\n"); 
    		dos.flush(); 
    		dos.writeBytes("exit\n"); 
    		dos.flush(); 
    		p.waitFor(); 
    		result = p.exitValue(); 
    		} 
	    	catch (Exception e ) 
	    	{ e.printStackTrace(); } 
    	    finally 
    	    { 
    	    	if (dos != null) 
    	    	{ 
    	    		try { dos.close(); } 
    	    	    catch (IOException e) { e.printStackTrace(); } 
    	     } 
    	 } 
    	return result; 
    } 
    
    
}