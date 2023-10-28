package com.jy.jyhy;

import com.unity3d.player.UnityPlayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.jy.callback.*;
import com.jy.callback.IOpenWebview;


public class WebViewPlugin {
	private static FrameLayout mLayout = null;
	private static WebView mWebView;
	
	
	public static ValueCallback<Uri> mUploadMessage;
	public static ValueCallback<Uri[]> mUploadCallbackAboveL;
	public final static int FILECHOOSER_RESULTCODE = 1;
	
	
	/**
	 * 打开一个WebView
	 * 
	 * @param url
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public static void openWebView(final String url, final int left,
			final int top, final int right, final int bottom,IOpenWebview callback)
	{
		
		closeWebView();
		
		final Activity activity = UnityPlayer.currentActivity;
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				mWebView = createWebView(activity);
				
				if (mLayout == null)
				{
					mLayout = new FrameLayout(activity);
					activity.addContentView(mLayout, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
					mLayout.setFocusable(true);
					mLayout.setFocusableInTouchMode(true);
				}
				
				mLayout.addView(mWebView, new FrameLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,Gravity.NO_GRAVITY));
				
				FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
						Gravity.NO_GRAVITY);
				params.setMargins(left, top, right, bottom);
				
				mWebView.setLayoutParams(params);
				mWebView.setVisibility(View.VISIBLE);
				mLayout.requestFocus();
				mWebView.requestFocus();
				mWebView.loadUrl(url);
			}
		});
	}
	
	
	/**
	 * 关闭webview
	 */
	public static void closeWebView() {
		Activity activity = UnityPlayer.currentActivity;
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (mWebView != null) {
					mLayout.removeView(mWebView);
					mWebView.clearFocus();
					mWebView = null;
				}
			}
		});
	}
	
	
	/**
	 * 创建WebView
	 * 
	 * @param activity
	 * @return
	 */
	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	private static WebView createWebView(final Activity activity) 
	{
		
		WebView webView = new WebView(activity);
		webView.setVisibility(View.GONE);
		webView.setFocusable(true);
		webView.setFocusableInTouchMode(true);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.setWebChromeClient(new WebChromeClient());
		
		WebSettings webSettings = webView.getSettings();
		webSettings.setSupportZoom(true); 
		webSettings.setBuiltInZoomControls(true);  
		webSettings.setUseWideViewPort(true);  
		webSettings.setLoadWithOverviewMode(true);  
		webSettings.setDefaultTextEncodingName("utf-8"); 
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		
		
		webSettings.setJavaScriptEnabled(true);
		webSettings.setUseWideViewPort(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setSupportZoom(true);
		webSettings.setDomStorageEnabled(true);
        String appCachePath = activity.getApplicationContext().getCacheDir().getAbsolutePath();
        webSettings.setAppCachePath(appCachePath);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        //webSettings.setVerticalScrollBarEnabled(true);
        //webSettings.setHorizontalScrollBarEnabled(true);
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		
		webView.setWebViewClient(new WebViewClient()
		{
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon)
			{
				
			}
			
			@Override
			public void onPageFinished(WebView view, String url) 
			{
				
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String desc, String url) 
			{

			}
			
			
		});
		
		
		
		
		webView.setWebChromeClient(new WebChromeClient() {
	            // For Android 3.0+
	            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
	            	Log.i("wwl", "wwwww  openFileChooser");
	                mUploadMessage = uploadMsg;
	                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
	                i.addCategory(Intent.CATEGORY_OPENABLE);
	                i.setType("*/*");
	                activity.startActivityForResult(Intent.createChooser(i, "File Chooser"),
	                        FILECHOOSER_RESULTCODE);
	            }

	            // For Android 3.0+
	            public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
	            	Log.i("wwl", "wwwww  openFileChooser");
	            	mUploadMessage = uploadMsg;
	                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
	                i.addCategory(Intent.CATEGORY_OPENABLE);
	                i.setType("*/*");
	                activity.startActivityForResult(Intent.createChooser(i, "File Browser"),
	                        FILECHOOSER_RESULTCODE);
	            }

	            // For Android 4.1
	            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
	            	Log.i("wwl", "wwwww  openFileChooser");
	            	mUploadMessage = uploadMsg;
	                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
	                i.addCategory(Intent.CATEGORY_OPENABLE);
	                i.setType("*/*");
	                activity.startActivityForResult(Intent.createChooser(i, "File Browser"),
	                        FILECHOOSER_RESULTCODE);

	            }

	            // For Android 5.0+
	            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback
	            		,WebChromeClient.FileChooserParams fileChooserParams) {
	            	Log.i("wwl", "wwwww  onShowFileChooser");
	                mUploadCallbackAboveL = filePathCallback;
	                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
	                i.addCategory(Intent.CATEGORY_OPENABLE);
	                i.setType("*/*");
	                activity.startActivityForResult(Intent.createChooser(i, "File Browser"),
	                        FILECHOOSER_RESULTCODE);
	                return true;
	            }
	        });
		
		
		
		return webView;
	}
}
