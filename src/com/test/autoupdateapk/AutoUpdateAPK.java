package com.test.autoupdateapk;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.support.v4.app.NavUtils;

public class AutoUpdateAPK extends Activity {

	// String uriAPI = "http://192.168.10.46:81/apkinfos.json";
	String uriAPI = "http://192.168.1.7/apkinfos.json";
	// String jsonData = "{\"username\":\"arthinking\",\"userId\":001}";
	String jsonData = "[{\"username\":\"arthinking\",\"userId\":001},{\"username\":\"Jason\",\"userId\":002}]";
	String jsonData1 = "[{\"Id\":1,\"apkname\":\"Intentftp\",\"classname\":\"com.mfpad.intentftp\",\"description\":\"This apk can trigger Screencapture and upload picture(s) to FTP server\",\"level\":1,\"pubdate\":\"2012-09-05\",\"version\":\"1.1\"}]";
	public static final String INTENTMODE_AUTORUN = "autorun";
	
	private Button mButton1,mButton2;
	String  nouse_str;
	String  UPDATE_FOLDER="/sdcard/update/";
	List<apklist> my_apklist=new ArrayList<apklist>();    //建立一個 apklist的集合
	private static boolean mHaveRoot = false;   // 判斷機器Android是否已經root，即是否獲取root權限
	   
	List<ApkLauncherItem> lvalue; 
	PackageManager pkgMgt;
	PackageManager pm;
	PackageInfo pi;
	
	private ListView mListView;
    private ApkLauncherAdapter mlistAdapter; 
    ArrayList<String> apkArray = new ArrayList<String>();
    ArrayList<String> verArray = new ArrayList<String>();
    
    ArrayList<String> newverArray = new ArrayList<String>();
    ArrayList<String> downloadlinkArray = new ArrayList<String>();
    LinearLayout mlayout;
    boolean autorunmode, have_update;
    int  THISNewitem_MAX = 100;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        Add_superUser_item();
        
        autorunmode =false;
        if ( getIntent().hasExtra(INTENTMODE_AUTORUN)) 
        	autorunmode = true;
   
       // if(  autorunmode == false )
        	setContentView(R.layout.main);  // 非自動模式 , 顯示UI
     
      //  final Intent intent = new Intent();
	  //	intent.setClass(this, TimerService.class);
	  //  startService(intent);  
  
      //  mButton1 =(Button) findViewById(R.id.button1); 
          mButton2 =(Button) findViewById(R.id.button2); 
     
        //-----------------  Test purpose -----------------------
        //  readFileSdcard( "teacherpad01s.json");    // 來自 /sdcard/ 下的json測試檔案
        //  Gson gson = new Gson();
        //  decode_jsonDB( jsonData1 );
        //  User user = gson.fromJson(jsonData, User.class);
        //  System.out.println(user.getUsername());
        //  System.out.println(user.getUserId());
        
  		  
        mButton2.setOnClickListener(new Button.OnClickListener() 
        { 
          @Override 
          public void onClick(View v) 
          { 
            // TODO Auto-generated method stub 
        	
            //聲明網址字符串
           // String uriAPI = "http://www.dubblogs.cc:8751/Android/Test/API/Get/index.php?str=I+am+Get+String"; 
           // String uriAPI = "http://192.168.1.14:3000/teacherpad01s.json";
       	     
        
        	  /*
               *  check Json 格式的 apk list from Server
               *  
               */
       	      
            //建立HTTP Get聯機
            HttpGet httpRequest = new HttpGet(uriAPI); 
            try 
            { 
            	//HttpGet httpGet = new HttpGet(url);
            	HttpParams httpParameters = new BasicHttpParams();
            	// Set the timeout in milliseconds until a connection is established.
            	// The default value is zero, that means the timeout is not used. 
            	int timeoutConnection = 3000;
            	HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            	// Set the default socket timeout (SO_TIMEOUT) 
            	// in milliseconds which is the timeout for waiting for data.
            	int timeoutSocket = 5000;
            	HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            	DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            	HttpResponse response = httpClient.execute(httpRequest);

             // HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);  // 發出HTTP request
           if(response.getStatusLine().getStatusCode() == 200)   // 若狀態碼為200 ok
           { 
                String strResult = EntityUtils.toString(response.getEntity()); // 取出響應字符串
                decode_jsonDB( strResult );
             
            
             /*
              *  開始下載  Start download
              *  
              */
            have_update = false;
          	int total = apkArray.size() + THISNewitem_MAX;
          	for(int k=0;k < total;k++)
          	{ 
         		   String  dlinkstr = downloadlinkArray.get(k);
            
         		    if( dlinkstr != "" )
         		    {
         		    	have_update = true;
		       			//Toast.makeText(getBaseContext(), downloadlinkArray.get(k), Toast.LENGTH_SHORT).show();
		       			
		       			String  http_file_url = downloadlinkArray.get(k);
		       			int i = http_file_url.lastIndexOf("/");
		             	    String savefile = http_file_url.substring(i+1); 
		             	    httpdownload h = new httpdownload( );
		             	    
		             	    File target =  h.downLoadFile( http_file_url , savefile );
		             	    if( target != null )
		             	    {
		             	    	
		                  	  // Toast.makeText(getBaseContext(), "Download finish", Toast.LENGTH_SHORT).show();
		            		
		                  	    String str = "/update/"+savefile;
		                  	    String fileName = Environment.getExternalStorageDirectory() + str;
		                  	
		                  	//  if( ! syshaveRoot() )
		                  	//	Toast.makeText(getBaseContext(), "沒有系統權限,無法更新", Toast.LENGTH_LONG).show();
		                  	 
				                  	  // 靜默安裝APK , 如果發行者的 金key變更, 這升級重新安裝,將會無效
		                  	    	String result;
		                  	       if( k < lvalue.size() )
				                  	  result = execRootCmd( "pm uninstall "+ lvalue.get(k).package_name );
				                  	  result = execRootCmd( "pm install "+ fileName );
				                  	   Toast.makeText(getBaseContext(), "Install "+savefile+" --> "+result, Toast.LENGTH_LONG).show();
				                  
				                  	   //------------以下更新 Superuser的數據庫 -------------
				                  	   Intent intent = new Intent();
				                  	   intent.setComponent(new ComponentName("com.test.passsu", "com.test.passsu.MainActivity"));
									   startActivity(intent);
				                  	   
				                  	// 靜默安裝APK , 採用模擬按鍵的方法, 
				                  	 //  在Android 1.5當中會多彈出 1 對話盒,so,不能用, Android 2.1 -4.0可以用
				                  	 /* Intent intent = new Intent( );
						  			   String packageName1 = "com.test.autoupdateapk";
						  			   String className1 = "com.test.autoupdateapk.install";
					  			       ComponentName cn1 = new ComponentName(packageName1, className1);
						        	  intent.setComponent(cn1);
							  		  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							  		  intent.putExtra("APK", fileName );
							  		  startActivity(intent);
							  		*/
		                  	 
		             	    }  //if( target != null )
		             	    else
		             	    	Toast.makeText(getBaseContext(), http_file_url+ " not found !", Toast.LENGTH_SHORT).show();
		             	 
         		    } //if( dlinkstr != "" )
       		
         		} // for (int k=0;
          	
          	       // Connection over
          	
		          	if( have_update == false)
		        		Toast.makeText(getBaseContext(),"版本已是最新,無須更新", Toast.LENGTH_LONG).show();
		        	else
		        	{
		        		if( ! autorunmode  )
		        		{
			        		CollectApks();      //取得在機器內的所有 apk清單
			                ShowApkListViews(); // 顯示 自定義的列表
		        		}
		        	}
          	
              } //if(response...... == 200) 
           	else
           		Toast.makeText(getBaseContext(),"連接Server不成功, 請檢查網路", Toast.LENGTH_LONG).show();
           
              if( autorunmode  )
            	  finish(); 
              
            } 
            catch (ClientProtocolException e) 
            {  
            //  mTextView1.setText(e.getMessage().toString()); 
              e.printStackTrace(); 
            } 
            catch (IOException e) 
            {  
            //  mTextView1.setText(e.getMessage().toString()); 
            	// 連接到不正確 的WiFi 路由器會到此處
              e.printStackTrace();      // HttpResponse error,IP or port not exist
              Toast.makeText(getBaseContext(),e.getMessage(), Toast.LENGTH_LONG).show();
             // Toast.makeText(getBaseContext(), "" +
           	 //		"IP or port not exist! please check WiFi or Server.", Toast.LENGTH_SHORT).show();
              if( autorunmode  )
            	  finish(); 
            } 
            catch (Exception e) 
            {  
            //  mTextView1.setText(e.getMessage().toString()); 
              e.printStackTrace();  
            }  // try -- catch
          
      
          } //public void onClick(View v)
        });   // setOnClickListener
        
        
       /*
        mButton2.setOnClickListener(new Button.OnClickListener() 
        { 
          @Override 
          public void onClick(View v) 
          { 
        	 // lvalue.get(0).version_name  += "-->[New!]";
        	 // mlistAdapter.notifyDataSetChanged();   // 變更資料　
        	int total = apkArray.size();
        	 for(int k=0;k < total;k++)
        	 { 
   	   		   String  dlinkstr = downloadlinkArray.get(k);
             
   	   		    if( dlinkstr != "" )
        		{
        			
        			//Toast.makeText(getBaseContext(), downloadlinkArray.get(k), Toast.LENGTH_SHORT).show();
        			
        			String  http_file_url = downloadlinkArray.get(k);
        			int i = http_file_url.lastIndexOf("/");
              	    String savefile = http_file_url.substring(i+1); 
              	    httpdownload h = new httpdownload( );
              	    
              	    File target =  h.downLoadFile( http_file_url , savefile );
              	    if( target != null )
              	    {
              	    	
	              	  // Toast.makeText(getBaseContext(), "Download finish", Toast.LENGTH_SHORT).show();
	        		
	              	    String str = "/update/"+savefile;
	              	    String fileName = Environment.getExternalStorageDirectory() + str;
	              	  // 靜默安裝APK
	              	 // runRootCommand( "pm install -r "+ fileName );
	              	  String result = execRootCmd( "pm install -r "+ fileName );
	              	  Toast.makeText(getBaseContext(), "Install "+savefile+" --> "+result, Toast.LENGTH_LONG).show();
              	    }
              	    else
              	    	Toast.makeText(getBaseContext(), http_file_url+ " not found !", Toast.LENGTH_SHORT).show();
              	 
        		}
        		
   	   		}
      
          }
        }); 
      
        */
        
        CollectApks();      //取得在機器內的所有 apk清單
        ShowApkListViews(); // 顯示 自定義的列表
   
        if( autorunmode  )
        	mButton2.performClick();
        	
    }
    
    
    //-----------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //-----------------------------------------------------------------------
    /* 自定義字符串取代函數 */
    public String eregi_replace(String strFrom, String strTo, String strTarget)
    {
      String strPattern = "(?i)"+strFrom;
      Pattern p = Pattern.compile(strPattern);
      Matcher m = p.matcher(strTarget);
      if(m.find())
      {
        return strTarget.replaceAll(strFrom, strTo);
      }
      else
      {
        return strTarget;
      }
    }
    //-----------------------------------------------------------------------
    public String readFileSdcard(String fileName)
    {
        String res=""; 
        try{ 
         FileInputStream fin = new FileInputStream(fileName); 
         int length = fin.available(); 
         byte [] buffer = new byte[length]; 
         fin.read(buffer);     
         res = EncodingUtils.getString(buffer, "UTF-8"); 
         
         int i = res.indexOf("\n");
         if( i != -1)
         res = res.substring( 0, i);
         
         fin.close();     
        } 
        catch(Exception e){ 
         e.printStackTrace(); 
        } 
        return res; 
    }
    //-----------------------------------------------------------------------
    public void decode_jsonDB( String json_data )
    {
    	int sum=0;
    	int flag;
    	String  installed_status;
    	String  dev_vernum;
    	String  server_apkname;
    	String  link_address;
    	String  new_vernum;
    	long    lastversion;
    	int     this_apk_num = 0;
    	boolean find_old_item;
    	
    	 try{
         	JsonReader reader = new JsonReader(new StringReader( json_data ));
         	
         	// 標準的讀取方式 : JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
         	// 另一個參考  http://developer.android.com/reference/android/util/JsonReader.html
         	//
         	reader.beginArray();
         	while(reader.hasNext()){
         		
         	  	flag = read_one_record( reader ); 
         	  	if( flag==0 ) sum++;         // 傳回0,代表讀取成功
         	}	
         	reader.endArray();
         	//Toast.makeText(getBaseContext(), "APK sum= "+sum, Toast.LENGTH_SHORT).show();
         }
         catch(Exception e){
         	e.printStackTrace();
         	 Toast.makeText(getBaseContext(), "" +
         	 		"Data analysis error.", Toast.LENGTH_SHORT).show();
         }
    	 
    	 int k;
    	 for(k=0;k < apkArray.size()+THISNewitem_MAX ;k++)
    	 {
    		 downloadlinkArray.add("");
    		 newverArray.add("");
    	 }
    	 
    	 for( int i=0; i< sum; i++)   //網站上的清單 : apkinfos.json
    	 {
    		 server_apkname = my_apklist.get(i).getapkName();
    		 link_address = my_apklist.get(i).geturlLink();
    		 new_vernum = my_apklist.get(i).getversion();
    		 find_old_item = false;    // init 
    		 
 	   		for(k=0;k < apkArray.size();k++)    //check the all installed .apk
 	   		{

	 	   		if( server_apkname.equals( apkArray.get(k)) )   //Found apk name is same
		   		{  
	 	   				find_old_item = true;  // 是 舊的項目
	 	   				String lastversion_name = verArray.get(k); //原版本號
	 	   			
	 	   			if( lastversion_name != null &&  ! lastversion_name.equals(new_vernum)  )  //Version name different
	 	   			{
		 	   			downloadlinkArray.set(k, link_address);
		 	   			newverArray.set(k, new_vernum);
	 	   	
	 	   		    
	 	   		    //packagenameArray.set( k, lvalue.get(k).package_name );  // 原來的包名
	 	   		    
		   			//Toast.makeText(getBaseContext(), server_apkname+"/"+lastversion+ " : "+
		   			//verArray.get(k), Toast.LENGTH_SHORT).show();
		   			
		   			//mlayout.findViewById(k+1).setBackgroundColor(Color.GREEN);
		   		   // mlistAdapter.tv.setTextColor(Color.WHITE);   //改變文字color,但是沒作用
		   		    lvalue.get(k).version_name += "   ----->>   " + new_vernum;
	 	   		   
		   		    //mListView.getChildAt(k).setBackgroundColor(Color.GRAY);
		   		 
		   		   
		   		    break;
	 	   			}
		   			
		   		}
		
 	   		} //for(k=0;k < apkArray.size();k++) 
 	 
 	   		if( find_old_item == false )
 	   			downloadlinkArray.set(k, link_address);
 	   		/*	
    	   		for(int j=this_apk_num-1;j>=0;j--)
    	   		{ 
    	   		
    	   		    pi = pkgMgt.getInstalledPackages(0).get(j);
    	   		  
    	   		    ActivityInfo aif = pkgMgt.queryIntentActivities(it,0).get(j).activityInfo;
    		   		
    		   	//	Drawable icon = aif.loadIcon(pkgMgt);  //圖示
    		   		String label = aif.loadLabel(pkgMgt).toString(); //名稱
    		   		dev_vernum = pi.versionName;   //版本名, 另一個是 version code
    		   		lastversion = pi.lastUpdateTime;
    		   		
    		   		if( label.equals(server_apkname) )
    		   		{
    		   			Toast.makeText(getBaseContext(), label+" : "+lastversion, Toast.LENGTH_SHORT).show();
    		   		}
    		   		 
    		   			
    	   		}
    	   	 */
 	   		
    	 }  //for( int i=0; i< sum; i++)
    	 
    	 return;
    	/* ====================== >>> 下載的示範功能
    	if( sum >= 1)
    	{
    		 String http_file_url = my_apklist.get(0).geturlLink();
    		 String apkname = my_apklist.get(0).getapkName();
    		 
    	      httpdownload h = new httpdownload( );
  	          h.downLoadFile( http_file_url , apkname+".apk" );
  	   	   
	  	       if( syshaveRoot() )
	  	       {
	  	    	 installed_status = execRootCmd(   "pm install -r " + UPDATE_FOLDER+ apkname+".apk" ); 
	  	    	 Log.i( "autoupdate install status=", installed_status );
  	  	       }  
	  	     //  else  
	  	     //  先把 apk  filename 轉 File 格式  
	  	     //	 openFile( UPDATE_FOLDER+ apkname+".apk" );
  	      }
  	      */
  	    	
         
    }
    //-----------------------------------------------------------------------
    public int read_one_record( JsonReader reader) throws IOException {
   
        int  flag = -1;
 
       reader.beginObject();
        while (reader.hasNext()) {
        	
        	apklist new_apk = read_Record_APKINFO(reader);
  	        my_apklist.add( new_apk ); 
  	        flag = 0;
        	
        	/*   // 舊的解析, 最外圍有一個 Database table Name
             	String name = reader.nextName();
         	  if (name.equals("ebook")) {
        		
         		    apklist new_apk = read_Record_APKINFO(reader);
        	        my_apklist.add( new_apk ); 
        	        flag = 0;
        	        
        	  } else {
        		  reader.skipValue();
        	  }
        	*/
        	
        }
        reader.endObject();
        return flag;
      }
    
    public apklist read_Record_APKINFO(JsonReader reader) throws IOException {
        String apkname = null;
        String clsname = null;
        String pkgname = null;
        String urllink = null;
        String pdate = null;
        String ver = null;
        String desc = null;
        int key = -1;
        int id = -1;

     //   reader.beginObject();   //這一行適用於: 外圍有一個 Database table Name
        while (reader.hasNext()) {
          String name = reader.nextName();
          if (name.equals("apkname")) {
            apkname = reader.nextString();
          } else if (name.equals("pubdate")) {
        	  pdate = reader.nextString();
          } else if (name.equals("lastversion")) {
        	  ver = reader.nextString();
          } else if (name.equals("memo")) {
        	  desc = reader.nextString();
          } else if (name.equals("myclass")) {
        	  clsname = reader.nextString();
          } else if (name.equals("mypkg")) {
        	  pkgname = reader.nextString();	  
          } else if (name.equals("apklink")) {
        	  urllink = reader.nextString();
          } else if (name.equals("check")) {
            key = reader.nextInt();
          } else if (name.equals("id")) {
              id = reader.nextInt();
          }else {
            reader.skipValue();
          }
        }
     //   reader.endObject();   //這一行適用於: 外圍有一個 Database table Name
        
        apklist new_apk=new apklist( id, apkname,clsname, pkgname,key, urllink, pdate, ver, desc );
        
        return new_apk;
      }
    
     
    //------------------------------------------------------------------------------------
      public static boolean runRootCommand(String command) 
      { 
      	Process process = null; 
      	DataOutputStream os = null; 
      	  try 
      	  {  
      			process = Runtime.getRuntime().exec("su"); ///system/bin/sh");  
      		
      	    	os = new DataOutputStream(process.getOutputStream()); 
      	   	    os.writeBytes(command+"\n"); 
      		 //   os.flush();
      		    os.writeBytes("exit\n");  
      		    os.flush(); 
      		    process.waitFor(); 
      	   } catch (Exception e) 
      	   {  
              // 	Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "+e.getMessage()); 
      	   return false;  
      	   }  
      		finally 
      		{ 
      				try 
      				{
      						if (os != null) 
      						{  
      								os.close();  
      						}  
      						process.destroy(); 
      				} 
      				catch (Exception e) 
      				{ 
      					// nothing 
      				}  
      		}  
      		return true;

      	}  //runRootCommand(
    //------------------------------------------------------------------------------------
  
   // 判斷機器Android是否已經root，即是否獲取root權限
   public static boolean syshaveRoot() 
   { 
       if (!mHaveRoot) { 
              
    	      int ret = execRootCmdSilent("echo test");   // 通過執行測試命令來檢測
                
               if (ret != -1)  
	           { 
	               Log.i(  "RootCmd" , "have root!"); 
	               mHaveRoot = true; 
	           } 
	           else 
	           { 
	                 Log.i( "RootCmd", "not root!"); 
	           } 
       } 
       else { 
                 Log .i( "RootCmd", "mHaveRoot = true, have root!"); 
       } 
   return mHaveRoot; 
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
    
  //-------------------------------------------------------------  
 // 執行命令並且輸出結果
    public static String execRootCmd(String cmd) 
    {
        String result = ""; 
        DataOutputStream dos = null; 
        DataInputStream dis = null; 
        try { 
        	   Process p = Runtime.getRuntime().exec("su");// 經過Root處理的android系統即有su命令
        	   dos = new DataOutputStream(p.getOutputStream()); 
        	   dis = new DataInputStream( p.getInputStream()); 
			    Log.i( "RootCmd", cmd); 
			    dos.writeBytes(cmd + "\n"); 
			    dos.flush(); 
			    dos.writeBytes("exit\n"); 
			    dos.flush() ; 
			    String line = null; 
			    while ((line = dis.readLine()) != null) 
			    { 
			    	Log.d("result", line); 
			    	result += line; 
			    } 
			    p.waitFor(); 
		}
        catch (Exception e ) 
        { 
        	e.printStackTrace(); 
        } 
        finally { 
        	if (dos != null) 
        	{ 
        		try { dos.close(); } 
        		catch (IOException e) { e.printStackTrace(); } 
        	} 
        	if (dis != null) 
        	{ 
        		try { dis.close(); } 
        		catch (IOException e) { e.printStackTrace(); }
        	} 
        } 
        return result; 
    }
   
    //===============================================
    
    private void openFile(File f) 
    {
      Intent intent = new Intent();
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.setAction(android.content.Intent.ACTION_VIEW);
      String type = getMIMEType(f);
      intent.setDataAndType(Uri.fromFile(f),type);
      startActivity(intent); 
    }
    private String getMIMEType(File f) 
    { 
      String type="";
      String fName=f.getName();
      String end=fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase(); 
		      if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||end.equals("xmf")||end.equals("ogg")||end.equals("wav"))
		      {
		        type = "audio"; 
		      }
		      else if(end.equals("3gp")||end.equals("mp4"))
		      {
		        type = "video";
		      }
		      else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||end.equals("jpeg")||end.equals("bmp"))
		      {
		        type = "image";
		      }
		      else if(end.equals("apk")) 
		      { 
		        /* android.permission.INSTALL_PACKAGES */ 
		        type = "application/vnd.android.package-archive"; 
		      } 
		      else
		      {	   type="*";
		      }
     
		      if(end.equals("apk")) 
		      { 
		      } 
		      else 
		      { 
		        type += "/*";  
		      } 
		      return type;  
      } 
   //------------------------------------------
   //------------------------------------------
    public void CollectApks() { //throws Exception{ 
    	
    	String packageName1=null; 
    	String className1=null;
    	String versionName1=null;
    	
    	
   		lvalue = new ArrayList<ApkLauncherItem>(); 
   		pkgMgt = this.getPackageManager(); 
   		pm = this.getPackageManager();
    
   		//  取得所有 LAUNCHER　Category 的　MAIN清單 
   		Intent it = new Intent(Intent.ACTION_MAIN); 
   		it.addCategory(Intent.CATEGORY_LAUNCHER); 
   		List<ResolveInfo> ra =pkgMgt.queryIntentActivities(it,0); 
   		int t = ra.size();
   		for(int i=ra.size()-1;i>=0;i--)
   		{ 
   		//    pi = pkgMgt.getInstalledPackages(0).get(i);
   		    
   			ActivityInfo ai = ra.get(i).activityInfo; 
	   		//String ainfo = ai.toString(); 
   			Drawable icon = null;
	   	//	Drawable icon = ai.loadIcon(pkgMgt);  //圖示
	   		String label = ai.loadLabel(pkgMgt).toString(); //名稱
	   		
	   		apkArray.add(label);   //記錄到 array裡面, 以後可以用
	
	   		ComponentName c = new ComponentName(ai.applicationInfo.packageName,ai.name); 
	   		packageName1 = ai.applicationInfo.packageName;   // Get Package Name
	   		
	   		try {
				PackageInfo pinfo = pkgMgt.getPackageInfo(packageName1, 0);
				int versionNumber = pinfo.versionCode;  // 開發者用的內部版本號
				String versionName = pinfo.versionName; // 給使用者看的版本號
				
				versionName1 = versionName;
				verArray.add(versionName1);
				
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   		//pi = getPackageManager().getPackageInfo( ai.applicationInfo.packageName, i);
	   		List<ResolveInfo> apps = pm.queryIntentActivities(it, i);
	   		ResolveInfo ri = apps.listIterator(i).next(); 
	 	 	if (ri != null ) 
		   	 className1 =ri.activityInfo.name;     // Get Class Name
			    	 
		    ApkLauncherItem item = new ApkLauncherItem(icon,label,c,packageName1,className1,versionName1); 
			lvalue.add(item); 	
			
   		}
    }
   //-------------------------------------------------
	class ApkLauncherItem {
      	Drawable icon;
       	String name;
       	ComponentName component;
       	String package_name;
       	String class_name;
       	String version_name;
       	
      	ApkLauncherItem(Drawable d, String s,ComponentName cn,String pn, String clsn, 
      String vernum	){
	     	icon = d;
	    	name = s;
	     	component = cn;
	     	package_name = pn;
	     	class_name = clsn;
	     	version_name = vernum;
       	}
   	}
  //-------------------------------------------------
  //  自定義　內部的顯示的 class --->  有自已的 LinearLayout
	private class ApkLauncherAdapter extends BaseAdapter{
    
		Activity activity;
		public TextView tv;
    	public ApkLauncherAdapter(Activity a){
	   		activity = a;
   		}
        @Override  
        public int getCount() {  
        
        	return lvalue.size();
        }  
       @Override  
        public Object getItem(int arg0) {  
            return arg0;  
        }  
        @Override  
        public long getItemId(int position) {  
            return position;  
        } 
        @Override  
        public View getView(int position, View convertView, ViewGroup parent) {
        	
        	//LinearLayout layout = new LinearLayout(activity);
        	mlayout = new LinearLayout(activity);
	   		mlayout.setOrientation(LinearLayout.HORIZONTAL);
	   		ImageView iv = new ImageView(activity);
	   		iv.setImageDrawable(lvalue.get(position).icon);
	   		mlayout.addView(iv);
	   	
	   		 tv = new TextView(activity);
	   		tv.setText(lvalue.get(position).name+" : "+ lvalue.get(position).version_name);
	   		tv.setTextSize(24);
	   		//tv.setTextColor(Color.RED);
	     	tv.setPadding(10, 5, 0, 0);
	   		mlayout.addView(tv);
	   
	   		mlayout.setBackgroundColor(Color.YELLOW);
	   		
	   		
	   		
	   		//TextView tv2 = new TextView(activity);
	   		//tv2.setText(lvalue.get(position).package_name+"/"+lvalue.get(position).class_name);
	   		//tv.setTextColor(Color.RED);
	     	//tv2.setPadding(10, 35, 0, 0);
	   		//layout.addView(tv2);
	   			   		   		
	   		return mlayout;
        }
        
	} //class ApkLauncherAdapter
	
	//-------------------------------------------------
	public void ShowApkListViews(){
		
		
		mlistAdapter = new ApkLauncherAdapter(this); 
		mListView = (ListView)findViewById(R.id.listView1);  
        mListView.setAdapter( mlistAdapter );    //自定義　內部如何顯示
        
        mListView.setOnItemClickListener(new OnItemClickListener(){
      		@Override
      	   	public void onItemClick( AdapterView arg0, View arg1, int arg2, long arg3) 
      	   	{
      	   			// TODO Auto-generated method stub
      		  		//Intent intent =new Intent(Intent.ACTION_VIEW);
      		   		//intent.setComponent(lvalue.get(arg2).component);
      		   		//startActivity(intent);
      	 
      	   	}
    	});
	}
	//-------------------------------------------------
	void Add_superUser_item()
	{
		//  先 copy 到 /cache/ 或 /sdcard/ 
		//File srcDir=new File("/data/data/com.hoshufou.android.su/databases/permissions.sqlite");
		//File destDir=new File("/cache/permissions.sqlite"); 
		
		File srcDir=new File("/data/data/com.noshufou.android.su/databases/","permissions.sqlite");
		File destDir=new File("/sdcard/","permissions.sqlite");
		execRootCmdSilent("chmod 666 /data/data/com.noshufou.android.su/databases/permissions.sqlite");
	
		try {
			
			destDir.createNewFile();
			copyFile(srcDir,destDir);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.qihoo.root/databases/su.db", null, SQLiteDatabase.OPEN_READWRITE);
		//SQLiteDatabase db = SQLiteDatabase.openDatabase("/sdcard/permissions.sqlite", null, SQLiteDatabase.OPEN_READWRITE);
		
		//  查詢 使用方法 1 , 用 query
		//     參數說明	
			//	 Cursor query (String table, String[] columns, 
			//   String selection, String[] selectionArgs, 
			//	String groupBy, String having, String orderBy)
		//  示範	
		//	String[] tableColumns = new String[] {  "uid", "package" };
		//	String selection= "package=?";
		//	String[] selectionArgs = { "com.speedsoftware.sqleditor"};
		//	Cursor c = db.query( "apps", tableColumns, selection, selectionArgs,
		//		       null, null, null);
		//	if(c.getCount()>0)
		//	{
		//		c.moveToFirst();  // 如果讀取數據之前，沒有這一句，會有異常
		//		String ss0 = c.getString(0);
		//	}
	
			//	  查詢 使用方法 2 , 用 rawQuery
		//	String q1 = "com.speedsoftware.sqleditor";
		//	String q0 = "SELECT uid FROM apps WHERE package = ?";
		//	Cursor mc = db.rawQuery(q0,new String[]{ q1 });
			
		//	if( mc.getCount()>0)
		//	{
		//		mc.moveToFirst();  // 如果讀取數據之前，沒有這一句，會有異常
		//		String ss0 = mc.getString(0);
		//		ss0 = "";
		//	}
		
		
		/*
		//	 挿入新增 數據 , 用於 "/data/data/com.qihoo.root/databases/su.db"
			 ContentValues cv=new ContentValues();
			 cv.put("uid", "0");
			 cv.put("package", "com.speedsoftware.sqleditor2");
			 cv.put("name", "SQLite Editor");
			// cv.put("des_uid", "0");            //不能用在com.noshufou.android.su
			// cv.put("des_cmd", "/system/bin/sh "); //不能用在com.noshufou.android.su
			// cv.put("allow", "1");      //不能用在com.noshufou.android.su
			// cv.put("bak_date", "12345678090123"); //不能用在com.noshufou.android.su
			// cv.put("app_type", "0");   //not need, auto created
			// cv.put("creator", "2");    //not need, auto created
			// cv.put("diagnosis", "3"); //not need, auto created
			 long new_id =  db.insert("apps", null, cv);
       */
		/*
		//		 修改數據
		// 方法 update(table, ContentValues, whereClause, whereArgs); 
		String tablename = "apps";
		
		ContentValues new_values = new ContentValues(); 
		new_values.put("uid", "0"); 
		String target_name = "com.test.autoupdateapk";
		String selection= "package = '" + target_name + "'";  //前後加 ' '單引號
					
		update_status = db.update( tablename, new_values, selection , null );
		*/
		/*
		 * //	刪除數據
				// 方法 delete(table, whereClause, whereArgs); 
				String tablename = "apps";
				String target_name = "com.test.autoupdateapk";
				String selection= "package = '" + target_name + "'";  //前後加 ' '單引號
				del_status = db.delete(tablename,  selection , null );			
				del_status = 0;
		 */
		
		/*
		//	 挿入新增 數據 , 用於 "/data/data/com.noshufou.android.su/databases/permissions.sqlite"
				 ContentValues cv=new ContentValues();
				 cv.put("uid", "0");
				 cv.put("package", "com.test.autoupdateapk2");
				 cv.put("name", "autoUpdateAPK");
				 cv.put("exec_uid", "0");            
				 cv.put("exec_cmd", "/system/bin/sh "); 
				 cv.put("allow", "1");     
				 long new_id =  db.insert("apps", null, cv);
		*/
		
		//	  查詢 使用方法 2 , 用 rawQuery
		/*
			String q1 = "com.test.autoupdateapk";
			String q0 = "SELECT * FROM apps WHERE package = ?";
			Cursor mc = db.rawQuery(q0,new String[]{ q1 });
			int del_status;
			if( mc.getCount()>0)
			{
					
			}	
			 
			db.close();
		*/
		
	}
	
	//----------------------------------------------------------
	public static void copyFile(File source, File destination) throws IOException
	{
	  byte[] buffer = new byte[100000];

	  BufferedInputStream bufferedInputStream = null;
	  BufferedOutputStream bufferedOutputStream = null;
	  try
	  {
	    bufferedInputStream = new BufferedInputStream(new FileInputStream(source));
	    bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(destination));
	    int size;
	    while ((size = bufferedInputStream.read(buffer)) > -1)
	    {
	      bufferedOutputStream.write(buffer, 0, size);
	    }
	  }
	  catch (IOException e)
	  {
	    // TODO may want to do something more here
	    throw e;
	  }
	  finally
	  {
	    try
	    {
	      if (bufferedInputStream != null)
	      {
	        bufferedInputStream.close();
	      }
	      if (bufferedOutputStream != null)
	      {
	        bufferedOutputStream.flush();
	        bufferedOutputStream.close();
	      }
	    }
	    catch (IOException ioe)
	    {
	      // TODO may want to do something more here
	      throw ioe;
	    }
	  }
	}
	

	
	
	 //---------------------------------------------------------
    public String getRouterIpAddress() { 
    	
        WifiManager wim= (WifiManager) getSystemService(WIFI_SERVICE)  ;     
        List<WifiConfiguration> l=  wim.getConfiguredNetworks();
        if( l == null ) return null;
        if( l.isEmpty() ) return null;
        WifiConfiguration wc=l.get(0); 
        String this_ip = ""+ Formatter.formatIpAddress(wim.getConnectionInfo().getIpAddress());
      
        String  device_this_ip = this_ip;
        
       int n = device_this_ip.lastIndexOf(".");
 	   String router_ip = device_this_ip.substring(0,n) +".1";
 	   
        return router_ip;
       }
  //-----------------------------------------------------
    
}





 //  範例: 單集合的Json讀取方式
/*
try{
 	JsonReader reader = new JsonReader(new StringReader( json_data ));
reader.beginArray();
	while(reader.hasNext()){
		reader.beginObject();
		while(reader.hasNext()){
			
			String tagName = reader.nextName();
			
			if(tagName.equals("apkname")){
				String id_str = reader.nextString();
				System.out.println( id_str );
			}
			else
				nouse_str = reader.nextString();
				
		}
		reader.endObject();
	}
	reader.endArray();
}
catch(Exception e){
	e.printStackTrace();
}
*/
// //  範例: List 集合的加入方式
/*
apklist apk1=new apklist(1,"name1","ver1");  
apklist apk2=new apklist(2,"name2","ver2");  
List<apklist> my_apklist=new ArrayList<apklist>();  
my_apklist.add( apk1 ); 
my_apklist.add( apk2 ); 
*/


/*    靜默安裝APK
final String  http_file_url = "http://192.168.1.11:81/GScriptLite.apk";
int i = http_file_url.lastIndexOf("/");
String savefile = http_file_url.substring(i+1); 
httpdownload h = new httpdownload( );
 h.downLoadFile( http_file_url , savefile );
 
Toast.makeText(getBaseContext(), "Download finish", Toast.LENGTH_SHORT).show();

// String str = "/CanavaCancel.apk";
// String fileName = Environment.getExternalStorageDirectory() + str;
String fileName = UPDATE_FOLDER + savefile;
//  Intent intent = new Intent(Intent.ACTION_VIEW);
//  intent.setDataAndType(Uri.fromFile(new File(fileName)), "application/vnd.android.package-archive");
//  startActivity(intent);



// 靜默安裝APK
runRootCommand( "pm install -r "+ fileName );
Toast.makeText(getBaseContext(), "Install APK finish", Toast.LENGTH_SHORT).show();   


// 正常安裝,會出現安裝指示下一步的畫面
//  File file1 = new File( fileName);
// Intent intent = new Intent();
// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
// intent.setAction(android.content.Intent.ACTION_VIEW);
// intent.setDataAndType(Uri.fromFile(  file1 ),
// "application/vnd.android.package-archive");
// startActivity(intent);
*/