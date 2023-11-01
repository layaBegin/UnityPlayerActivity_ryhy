package com.hrrgwe.zojfwy2304fizy3;

import java.util.Set;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.hrrgwe.zojfwy2304fizy3.GameLog;
import com.unity3d.player.UnityPlayer;

public class GameUtil {
	
	/**
	 * 发送消息给Unity3D
	 * @param methodName 对象上脚本的方法名
	 */
	public static void sendMessageToUnity3D(String methodName, Bundle b) 
	{
		if (b == null) {
			UnityPlayer.UnitySendMessage("GameManager", methodName, "");
		} else {
			StringBuilder sb = new StringBuilder();
			Set<String> keySet = b.keySet();
			for(String key : keySet)
			{ 
				if (sb.length() == 0)
				{
					sb.append(String.format("%s=%s", key, b.get(key)));
				}
				else
				{
					sb.append(String.format("&%s=%s", key, b.get(key)));
				}
			}
			UnityPlayer.UnitySendMessage("Main", methodName, sb.toString());
		}
	}
	
	
	/**
	 * 根据指定id获取string.xml中的文字
	 * @param stringID
	 * @return
	 */
	public static String getText(String key)
	{
		int stringID = UnityPlayer.currentActivity.getResources().getIdentifier(
				key, "string", UnityPlayer.currentActivity.getPackageName());
		
		return UnityPlayer.currentActivity.getResources().getString(stringID);
	}
	
	
	/**
	 * 获取资源id
	 * @param defType
	 * @param name
	 * @return
	 */
	public static int getResourcesId(String defType, String name)
	{
	    int id = UnityPlayer.currentActivity.getResources().getIdentifier(
	    		name, defType, UnityPlayer.currentActivity.getPackageName());
	    
	    if (id == 0) {
	      	GameLog.logError("找不到" + defType + "." + name);
	    }
	    return id;
	}
	
	
    /**
     * 获取当前游戏版本信息
     */
    public static String getCurrectGameVersion(Context context) {
    	
    	String gameVersion = getStringValueFromSharedPrefs(context, "snake_version");
    	GameLog.logInfo("获取当前版本：" + gameVersion);
    	return gameVersion;
    }
    
    
    /**
     * 从shared_prefs文件中读取某个字段对应的int值
     */
    public static String getStringValueFromSharedPrefs(Context context, String key)
    {
    	SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    	return sp.getString("Snake__" + key, "");
    }
    
    
    /**
     * 给shared_prefs文件中写入某个string值
     */
    public static void setStringValueToSharedPrefs(Context context, String key, String value)
    {
    	SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    	Editor editor = sp.edit();
    	editor.putString("Snake__" + key, value);
    	editor.commit();
    }
    
    
    /**
     * 从shared_prefs文件中读取某个字段对应的int值
     */
    public static int getIntegerValueFromSharedPrefs(Context context, String key)
    {
    	SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    	return sp.getInt("Snake__" + key, 0);
    }
    
    
    /**
     * 给shared_prefs文件中写入某个int值
     */
    public static void setIntegerValueToSharedPrefs(Context context, String key, int value)
    {
    	SharedPreferences sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    	Editor editor = sp.edit();
    	editor.putInt("Snake__" + key, value);
    	editor.commit();
    }
}
