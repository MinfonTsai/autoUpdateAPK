package com.test.autoupdateapk;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle; 
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

public class runservice extends Service {
	

    private static Timer timer = new Timer(); 
    private Context ctx;

    int hour;
	int min;
	int sec;
	boolean ping_status = false;
	boolean ip_status = false;
	String  device_this_ip;
	
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
    	/*
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss"); //先行定義時間格式
    	Date dt=new Date();  //取得現在時間
    	String dts=sdf.format(dt);
    	
    	String[] my =dts.split(":");

    	hour =Integer.parseInt(my[0]);
    	min =Integer.parseInt(my[1]);
    	sec =Integer.parseInt(my[2]);
		*/
    	
        Toast.makeText(getApplicationContext(), "WiFi Keep runner in ON", Toast.LENGTH_SHORT).show();
        
        timer.scheduleAtFixedRate(new mainTask(), 0, 120000);
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
        	int new_sec =Integer.parseInt(my[2]);
        	
        	//Toast.makeText(getApplicationContext(), "check wifi", Toast.LENGTH_SHORT).show();
        	if( new_sec < 30 )
        	{
        		  if( getThisIpAddress()== null )
        		  {
        			  execRootCmdSilent("svc wifi disable");
        			 // Toast.makeText(getApplicationContext(), "Set wifi disable", Toast.LENGTH_SHORT).show();
        		  }
        		  else if( ping_routerP80_status() == false )
        		  {
        			  execRootCmdSilent("svc wifi disable");
        		  }
        			  
        	}
        	else
        	{
        		  if( getThisIpAddress()== null )
        		  {
        			  execRootCmdSilent("svc wifi enable");
        			//  Toast.makeText(getApplicationContext(), "Set wifi enable", Toast.LENGTH_SHORT).show();
        		  }
        		  else if( ping_routerP80_status() == false )
        		  {
        			  execRootCmdSilent("svc wifi disable");
        		  }
        	}
        	
        }
    };    
    
    //---------------------------------------------------------
    public String getThisIpAddress() { 
    	ip_status = false;
        WifiManager wim= (WifiManager) getSystemService(WIFI_SERVICE)  ;     
        List<WifiConfiguration> l=  wim.getConfiguredNetworks();
        if( l == null ) return null;
        if( l.isEmpty() ) return null;
        WifiConfiguration wc=l.get(0); 
        String this_ip = ""+ Formatter.formatIpAddress(wim.getConnectionInfo().getIpAddress());
        ip_status = true;
        device_this_ip = this_ip;
        return this_ip;
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
    //--------------------------------------------------------- 
    private boolean ping_routerP80_status()
    {
    	 int router_port = 80; 
         Socket socket; 
         if( ip_status == false )
  		   return false; 
  	   
  	   int n = device_this_ip.lastIndexOf(".");
  	   String router_ip = device_this_ip.substring(0,n) +".1";
  			   
  	   Socket mySocket = null; 
         mySocket = new Socket();
         SocketAddress address = new InetSocketAddress( router_ip, router_port );
              
              int resp_time = 1000;
				  try {
					mySocket.connect(address, resp_time );
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}   // 50,100,150 is  TimeOut value
				
				if( mySocket != null )
				{
					if( mySocket.isConnected() )
					{
						ping_status = true;
						try {
							mySocket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return true;
					}
					else
					{
						ping_status = false;
					}
					try {
						mySocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						mySocket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else  
				{
					ping_status = false;
				}
				
				  return false; 
    }
    
    /*
    class ClientTask extends AsyncTask<Void, Void, Void>
    { 
         public int port = 80; 
         Socket socket; 
           @Override 
           protected Void doInBackground(Void... params) 
           {
        	   if( ip_status == false )
        		   return null; 
        	   
        	   int n = device_this_ip.lastIndexOf(".");
        	   String router_ip = device_this_ip.substring(0,n) +".1";
        			   
        	   Socket mySocket = null; 
	           mySocket = new Socket();
	           SocketAddress address = new InetSocketAddress( router_ip, port );
	                
	                int resp_time = 1000;
					  try {
						mySocket.connect(address, resp_time );
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}   // 50,100,150 is  TimeOut value
					
					if( mySocket != null )
					{
						if( mySocket.isConnected() )
						{
							ping_status = true;
						}
						else
						{
							ping_status = false;
						}
						try {
							mySocket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					else  
					{
						ping_status = false;
					}
					
					  return null;  
           }
   
    }
    */
    
}