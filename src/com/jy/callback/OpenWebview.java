package com.jy.callback;

import com.jy.callback.IOpenWebview;
import com.jy.jyhy.MainGameActivity;

public class OpenWebview implements IOpenWebview {
	private final String callUnityMethodName = "OpenViewResult";
	private MainGameActivity unity;
	
	public OpenWebview(MainGameActivity unity){
		this.unity = unity;
	}
	
	@Override
	public void OpenResult(boolean result) {		
		if(result){
			unity.CallUnity(callUnityMethodName,"1");				
		}else{
			unity.CallUnity(callUnityMethodName,"0");			
		}
		
	}
}
