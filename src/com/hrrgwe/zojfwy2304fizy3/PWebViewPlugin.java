package com.hrrgwe.zojfwy2304fizy3;

import com.unity3d.player.UnityPlayer;

import android.R.bool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.jy.callback.*;
import com.jy.callback.IOpenWebview;

public class PWebViewPlugin {
	private static FrameLayout mLayout = null;
	private static WebView mWebView;
	private static int urlIndex = 0;
	private static IOpenWebview _callback;
	private static String[] _signArr;
	
	/**
	 * 打开一个WebView
	 * 
	 * @param url
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public static void openWebView(final String url,String sign, final int left,
			final int top, final int right, final int bottom,IOpenWebview callback)
	{
		
		urlIndex = 0;
		if(sign.contains(",")){
			_signArr = sign.split(",");
		}else{
			_signArr = new String[]{sign};
		}		
		//Log.i("wwl", "sign="+sign);
		_callback = callback;
		
		//Log.i("wwl", " openWebView url="+url);
		
		
		startAlipayActivity(url);
		
		if(1==1){
			return;
		}
		
		
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
	
	private static void startAlipayActivity(String url) {
	    Intent intent;
	    try {
	        intent = Intent.parseUri(url,Intent.URI_INTENT_SCHEME);
	        intent.addCategory(Intent.CATEGORY_BROWSABLE);
	        intent.setComponent(null);
	        Activity activity = UnityPlayer.currentActivity;
	        activity.startActivity(intent);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
		
	/**
	 * 创建WebView
	 * 
	 * @param activity
	 * @return
	 */
	@SuppressLint("SetJavaScriptEnabled")
	@SuppressWarnings("deprecation")
	private static WebView createWebView(Activity activity) 
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
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				urlIndex++;
				Log.i("wwl", "jump url!!! ,url"+urlIndex+"="+url);
				Boolean isFialUrlBoolean = false;
				for(int i=0;i<_signArr.length;i++){
					if(url.startsWith(_signArr[i])){
						isFialUrlBoolean = true;
						break;
					}					
				}
				if (isFialUrlBoolean){
					 startAlipayActivity(url);
					 _callback.OpenResult(true);
				}else {
					view.loadUrl(url);	
				}
				view.loadUrl(url);	
				
				return true;
			}
			
			
		});
		
		
		return webView;
	}
}
