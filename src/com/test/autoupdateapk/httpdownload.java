package com.test.autoupdateapk;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.widget.Toast;

public class httpdownload extends Activity {
	
	String  UPDATE_FOLDER="/sdcard/update/";
	
protected File downLoadFile( String httpUrl , String fileName ) 
{
// TODO Auto-generated method stub
//final String fileName = "updata.apk";
File tmpFile = new File( UPDATE_FOLDER );
if (!tmpFile.exists()) {
tmpFile.mkdir();
}
final File file = new File( UPDATE_FOLDER + fileName);

try {
URL url = new URL(httpUrl);
try {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		int status = conn.getResponseCode();
		if( status == 200 )  // success to find the target 
		{
			InputStream is = conn.getInputStream();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buf = new byte[256];
			conn.connect();
			double count = 0;
			if (conn.getResponseCode() >= 400) 
			{
				Toast.makeText(this, "连接超时", Toast.LENGTH_SHORT).show();
			} 
			else 
			{
				while (count <= 100) {
				
					if (is != null) 
					{
						int numRead = is.read(buf);
						if (numRead <= 0) 
						{
								break;
						} 
						else {
								fos.write(buf, 0, numRead);
							}
	
					} 
					else {
						break;
					}
	
				}
			}
			fos.close();
			is.close();
			conn.disconnect();
		}
		else
		{
			conn.disconnect();
			return null;
		}
		
		
		} catch (IOException e) {
		// TODO Auto-generated catch block
			Toast.makeText(this, "Http connect failure", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		
		e.printStackTrace();
		}

		
return file;
}

}