package com.jy.jyhy;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.unity3d.player.UnityPlayer;

import android.util.Log;

public class LoadFile {
	private static byte[] readtextbytes(InputStream inputStream) 
	{
		
	  ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	  //长度这里暂时先写成1024
	  byte buf[] = new byte [102400];
	  
	  int len;
 
	  try {
 
	   while ((len = inputStream.read(buf)) != -1) {
		   outputStream.write(buf, 0, len);
	   }
 
	   outputStream.close();
 
	   inputStream.close();
 
	  } catch (IOException e) {
 
	  }
	  return outputStream.toByteArray();
	}
	
	//读取assetbund并且返回字节数组
	public static byte[] loadFile(String path)
	{
	
		 InputStream inputStream = null ;
		 
		  try {

 
			   inputStream = UnityPlayer.currentActivity.getAssets().open(path);
			   String[] pathArr = UnityPlayer.currentActivity.getAssets().list("./");
			   Log.i("-------------------------","dasssssasdasdasdasddd");
			   for(int i=0;i<pathArr.length;i++){
				   Log.i("ab file name =", pathArr[i]);
				   
			   }
			   Log.i("-------------------------","dasssssasdasdasdasddd2");
			   
 
			  } catch (IOException e) 
			  {
 
				  Log.e("ihaiu.com", e.getMessage());
 
			  }
 
		  return readtextbytes(inputStream);
	}
}
