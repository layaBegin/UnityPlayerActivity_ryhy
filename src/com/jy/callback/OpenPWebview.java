package com.jy.callback;

import android.R.string;

import com.jy.callback.IOpenWebview;
import com.jy.jyhy.MainGameActivity;

public class OpenPWebview implements IOpenWebview {
	private final String callUnityMethodName = "OpenPWebViewResutl";
	private MainGameActivity unity;
	
	public OpenPWebview(MainGameActivity unity) {
		this.unity = unity;
	}
	
	@Override
	public void OpenResult(boolean result) {		
		if(result){
			String json = "{\"mark\": \"android\",\"tip\": \"succ\"}";
			unity.CallUnity(callUnityMethodName,json);				
		}else{
			String json = "{\"mark\": \"android\",\"tip\": \"fail\"}";
			unity.CallUnity(callUnityMethodName,json);			
		}
		
	}
}
