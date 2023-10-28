package com.jy.jyhy;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.content.Intent;
import android.app.PendingIntent;
import android.net.Uri;
import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import com.jy.jyhy.GameLog;
import com.jy.jyhy.GameUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DownloadApk extends Thread {
	private String mDownloadUrl = "";
	private String mApkName = "";
	private Activity mMainActivity = null;
	
	// 下载apk进度条
	private ProgressDialog mProgressDialog;
	
	public DownloadApk(Activity mainActivity, String url, String apkName)
	{
		mMainActivity = mainActivity;
		mDownloadUrl = url;
		mApkName = apkName;
		
		if (mProgressDialog == null)
			mProgressDialog = new ProgressDialog(mMainActivity);
		mProgressDialog.setTitle(GameUtil.getText("snake_updateto"));
		mProgressDialog.setMessage(GameUtil.getText("snake_pleasewait"));
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();
	}
	
	
	@Override
	public void run()
	{
		if (mDownloadUrl == "" || mDownloadUrl == null)
			return;
		
		SSLSocketFactory.getSocketFactory().setHostnameVerifier(new AllowAllHostnameVerifier());
		
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(mDownloadUrl);
		HttpResponse response;
		try
		{
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			long length = entity.getContentLength();
			InputStream is = entity.getContent();
			FileOutputStream fos = null;
			if (is != null)
			{
				GameLog.logInfo("path:" + mMainActivity.getExternalCacheDir());
				
				File file = new File(mMainActivity.getExternalCacheDir()+"/", mApkName);
				fos = new FileOutputStream(file);
				
				byte[] buf = new byte[1024];
				int ch = -1;
				int count = 0;
				int progress = 0;
				while((ch = is.read(buf)) != -1)
				{
					fos.write(buf, 0, ch);
					count += ch;
					
					int curProgress = (int)(((float)count/length)*100);
                    if(progress != curProgress)
                    {
                    	progress = curProgress;
                    	updateProgressBar(curProgress);
                    }
				}
			}
			
			fos.flush();
			
			if (fos != null)
				fos.close();
			
			downloadCompleted();
		}
		catch(Exception e)
		{
			Bundle b = new Bundle();
			b.putString("msg", e.toString());
			
			GameUtil.sendMessageToUnity3D("DownloadFullPackageFailed", b);
			
			e.printStackTrace();
			
			updateProgressBar(-1);
		}
	}
	

	/**
	 * 更新进度条
	 */
	void updateProgressBar(final int progress)
	{
		this.mMainActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(mProgressDialog  == null)
	        	 {
					 GameLog.logInfo("progressDialog is null");
	        		 return;
	        	 }
	        	 
	        	 if(progress <= -1)
	        	 {
	        		 mProgressDialog.setMessage(GameUtil.getText("snake_downloaderror") + "\n" + GameUtil.getText("snake_checknet"));
	        		 mProgressDialog.dismiss();
	        		 mProgressDialog = null;
	            	 return;
	        	 }
	        	 
	        	 mProgressDialog.setProgress(progress);                   	 
	        	 
	        	 if(progress >= 100)
	        	 {   
	        		 mProgressDialog.cancel();
	        	 }
			}
		}); 
	}
	
	
	/**
	 * 下载完成
	 */
	void downloadCompleted() 
	{  
		this.mMainActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// 开始安装apk
				startInstallApk();
			}
		}); 
	}
	
	
	private void InstallAPkGreaterAndroid7(File apkFile){
		 Uri apkUri = FileProvider.getUriForFile(this.mMainActivity, "com.sharpknife.qmyl.fileprovider"
		            , apkFile);//在AndroidManifest中的android:authorities值
		            Intent install = new Intent(Intent.ACTION_VIEW);
		            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
		            this.mMainActivity.startActivity(install);
		
	}
	
	
	/**
	 * 开始安装apk
	 */
	void startInstallApk() 
	{	 
		/*Log.i("wwl", "准备安装的apk的名称mApkName="+mApkName);
	    Intent intent = new Intent(Intent.ACTION_VIEW);  
	    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.setDataAndType(Uri.fromFile(new File(mMainActivity.getExternalCacheDir()+"/", this.mApkName)),  
	        "application/vnd.android.package-archive");  
	    this.mMainActivity.startActivity(intent);  */
		
		Log.i("wwl", "准备安装的apk的名称mApkName="+mApkName);
		 File apkFile = new File(mMainActivity.getExternalCacheDir()+"/", this.mApkName); //这是我的文件路径，各自根据自己的写
		 Log.i("wwl", "准备安装的apk的名称Build.VERSION.SDK_INT="+Build.VERSION.SDK_INT);
		 
		 if(Build.VERSION.SDK_INT>=26){			//android8.0
			 
			 InstallAPkGreaterAndroid7(apkFile);
			 
			 
			 /*boolean b = this.mMainActivity.getPackageManager().canRequestPackageInstalls();
	            if (b) {
	            	InstallAPkGreaterAndroid7(apkFile);
	            } else {
	                //请求安装未知应用来源的权限
	                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, INSTALL_PACKAGES_REQUESTCODE);
	            }*/
		 }else if(Build.VERSION.SDK_INT>=24) {//判读版本是否在7.0以上			 	
			 InstallAPkGreaterAndroid7(apkFile);
        } else{
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.mMainActivity.startActivity(install);
        }
		
	}
}
