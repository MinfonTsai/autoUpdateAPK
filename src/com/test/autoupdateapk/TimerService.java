package com.test.autoupdateapk;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;



public class TimerService extends Service {
	
	    private static Timer timer = new Timer(); 
	    private Context ctx;

	    int hour;
    	int min;
    	int sec;
    	
	    public IBinder onBind(Intent arg0) 
	    {
	          return null;
	    }

	    public void onCreate() 
	    {
	          super.onCreate();
	          ctx = this; 
	          startService();
	    }

	    private void startService()
	    {   
	    	//SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //先行定義時間格式
        	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); //先行定義時間格式
        	Date dt=new Date();  //取得現在時間
        	String dts=sdf.format(dt);
        	
        	String[] my =dts.split(":");

        	hour =Integer.parseInt(my[0]);
        	min =Integer.parseInt(my[1]);
        	sec =Integer.parseInt(my[2]);

           // Toast.makeText(getApplicationContext(), "Now time is "+hour+":"+min+":"+sec, Toast.LENGTH_LONG).show();
            
	        timer.scheduleAtFixedRate(new mainTask(), 0, 60000);
	    }

	    private class mainTask extends TimerTask
	    { 
	        public void run() 
	        {
	            toastHandler.sendEmptyMessage(0);
	        }
	    }    

	    public void onDestroy() 
	    {
	          super.onDestroy();
	          Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
	    }

	    private final Handler toastHandler = new Handler()
	    {
	        @Override
	        public void handleMessage(Message msg)
	        {
	        	
	        	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); //先行定義時間格式
	        	Date dt=new Date();  //取得現在時間
	        	String dts=sdf.format(dt);
	        	
	        	String[] my =dts.split(":");

	        	int new_hour =Integer.parseInt(my[0]);
	        	int new_min =Integer.parseInt(my[1]);
	        	
	        	//if( new_hour==hour &&  new_min == (min+1) )
	        	if( new_min == (min+1) )
	        	{
	        		  Intent intent = new Intent( );
		  			   String packageName1 = "com.test.autoupdateapk";
		  			   String className1 = "com.test.autoupdateapk.AutoUpdateAPK";
	  			       ComponentName cn1 = new ComponentName(packageName1, className1);
		        	  intent.setComponent(cn1);
			  		  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			  		  intent.putExtra("autorun", "1");
			  		  startActivity(intent);
	        	}
	        }
	    };    

	
	/*
	private Timer myTimer;
	@Override
	 public void onCreate() {
	        // TODO Auto-generated method stub
		 
		
		 Toast.makeText(getBaseContext(), "Timer service.", Toast.LENGTH_SHORT).show();
		  
		
		    myTimer = new Timer();
		
		//    myTimer.schedule(new TimerTask() {          
		 //       @Override
		 //       public void run() {
		         //   TimerMethod();
		 //       	 Toast.makeText(getBaseContext(), "Timer service.", Toast.LENGTH_SHORT).show();
		  //      }

		   // }, 0, 1000);
		 		
		TimerTask updatetask = new UpdateTask();
		myTimer.scheduleAtFixedRate(updatetask, 0, 1000);
		
		 
	 }

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	class UpdateTask extends TimerTask {
		   public void run() {
		       //calculate the new position of myBall
			   Toast.makeText(getBaseContext(), "Timer N", Toast.LENGTH_SHORT).show();
		   }
		}
	
	private void TimerMethod()
	{
	    //This method is called directly by the timer
	    //and runs in the same thread as the timer.

	    //We call the method that will work with the UI
	    //through the runOnUiThread method.
	    this.runOnUiThread(Timer_Tick);
	}


	private Runnable Timer_Tick = new Runnable() {
	    public void run() {

	    //This method runs in the same thread as the UI.               
	    //Do something to the UI thread here
	    	Toast.makeText(getBaseContext(), "" +
           	 		"Run !", Toast.LENGTH_SHORT).show();
	    }
	};

	public final void runOnUiThread(Runnable action) {       
	  //  if (Thread.currentThread() != mUiThread) {       
	  //      mHandler.post(action);       
	  //  } else {       
	        action.run();       
	  //  }       
	}
	*/
}