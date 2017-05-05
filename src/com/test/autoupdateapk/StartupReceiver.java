package com.test.autoupdateapk;


import android.content.BroadcastReceiver; 
import android.content.Context; 
import android.content.Intent;

public class StartupReceiver extends BroadcastReceiver {   
   
    @Override   
    public void onReceive(Context context, Intent intent) {   
        // TODO Auto-generated method stub  
    	
    	/*
        Intent i = new Intent(context,runservice.class);   
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
        context.startActivity(i);   
        */
   	
		intent.setClass(context, TimerService.class);
		context.startService(intent);
	    
        /*
         *  Start sequence is 
         *   (StartupReceiver)--> --> (TimerService)
         * 
         */
   
		intent.setClass(context, runservice.class);
		context.startService(intent);
		
    }   
   
}  
   

