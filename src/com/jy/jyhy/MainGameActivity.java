package com.jy.jyhy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ContentHandler;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.clipimagedemo.MenuMainty;
import com.kola.BatteryReceiver;
import com.kola.GameGloableData;
import com.jy.callback.IOpenWebview;
import com.jy.callback.OpenPWebview;
import com.jy.callback.OpenWebview;
import com.jy.jyhy.DownloadApk;
import com.jy.jyhy.GameUtil;
import com.jy.jyhy.MacHandler;
import com.jy.jyhy.MainGameActivity;
import com.jy.jyhy.PWebViewPlugin;
import com.jy.jyhy.WebViewPlugin;
import com.comuse.ryhyzn.R;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import com.jy.DragFloatButton.*;

import com.cf.msc.sdk.AppVest;
import com.cf.msc.sdk.SecurityConnection;

import com.kiwi.sdk.Kiwi;

@SuppressLint("SetJavaScriptEnabled")
public class MainGameActivity extends UnityPlayerActivity {
	
	
	/**
	 * 广播接受者
	 */
	class BatteryReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//判断它是否是为电量变化的Broadcast Action
			if(Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){				
				int level = intent.getIntExtra("level", 0);//获取当前电量				
				int scale = intent.getIntExtra("scale", 100);//电量的总刻度
				
				// 当前手机使用的是哪里的电源
				String pluged = "";
                switch (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        // 电源是AC charger.[应该是指充电器]
                    	pluged="AC";
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        // 电源是USB port
                    	pluged="USB";
                        break;
                    default:
                        break;
                }
                String state = "";
                switch (intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)) {
                    case BatteryManager.BATTERY_STATUS_CHARGING:
                    	state="正在充电";
                        break;
                    case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    	state="BATTERY_STATUS_DISCHARGING";
                        break;
                    case BatteryManager.BATTERY_STATUS_FULL:
                    	state="充满";
                        break;
                    case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        // 没有充电
                    	state="没有充电";
                        break;
                    case BatteryManager.BATTERY_STATUS_UNKNOWN:
                        // 未知状态
                    	state="未知状态";
                        break;
                    default:
                        break;
                }			
				
				
				String json = "{\"level\": \""+level+"\",\"scale\": \""+scale+"\",\"pluged\": \""+pluged+"\",\"state\": \""+state+"\"}";
				
				Log.i("charge battery json=", json);
				
				CallUnity("SaveBatteryMessage", json);
				
				
			}
		}
		
	}
	
	
	static final String TAG = "Unity";
	private static MainGameActivity instance = null;

	public static MainGameActivity GetInstance() {
		return instance;
	}

	@SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		instance = this;
		super.onCreate(savedInstanceState);
		
		

		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);		//注册广播接受者
		BatteryReceiver batteryReceiver = new BatteryReceiver();		//创建广播接受者对象
		registerReceiver(batteryReceiver, intentFilter);		//注册receiver
		
	}

	static void Log(String msg) {
		Log.i(TAG, msg);
	}

	static void put(JSONObject obj, String key, Object value) {
		try {
			obj.put(key, value.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	static String formatPayParam(String p) {
		if (p == null || p.length() <= 0)
			return "无";
		return p;
	}

	static Bitmap CompressBitmap(Bitmap bmp, int width) {
		int height = width * bmp.getHeight() / bmp.getWidth();
		Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, width, height, true);

		// Bitmap compressedBmp = Bitmap.createBitmap(width, height,
		// Bitmap.Config.RGB_565);
		// Canvas localCanvas = new Canvas(compressedBmp);
		// localCanvas.drawBitmap(thumbBmp, new Rect(0, 0, width, height), new
		// Rect(0, 0, width, height), null);
		// thumbBmp.recycle();
		// return compressedBmp;

		return thumbBmp;
	}

	static void WriteBitmapToFile(Bitmap bitmap, String path) {
		try {
			FileOutputStream output = new FileOutputStream(new File(path));
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String mUnityMsgReceiver = "SDK";

	public void CallUnity(final String funcName, final String paramStr) {
		UnityPlayer.UnitySendMessage(mUnityMsgReceiver, funcName, paramStr);
	}

	// 获取手机电量
	public static void getElectricity() {
		
	}

	

	/**
	 * 打开一个WebView
	 * 
	 * @param url
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void openWebView(String url, int left, int top, int right, int bottom) {
		OpenWebview openWebview = new OpenWebview(this);

		WebViewPlugin.openWebView(url, left, top, right, bottom, openWebview);
	}

	/**
	 * 关闭webview
	 */
	public void closeWebView() {
		WebViewPlugin.closeWebView();
	}

	
	public void openPWebView(String url,String sign, int left, int top, int right, int bottom) {
		IOpenWebview openWebview = new OpenPWebview(this);

		PWebViewPlugin.openWebView(url,sign, left, top, right, bottom, openWebview);
	}

	/**
	 * 关闭PWebView
	 */
	public void closePWebView() {
		PWebViewPlugin.closeWebView();
	}
	
	
	/**
	 * 将文本复制剪切板
	 * 
	 * @param content
	 */
	public void copyToClipboard(final String content) {
		ClipboardManager cmb = (ClipboardManager) this
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setPrimaryClip(ClipData.newPlainText("Shared to RC", content));

		Bundle b = new Bundle();
		b.putString("success", String.valueOf(true));
		GameUtil.sendMessageToUnity3D("HandleCopyToClipboardMsg", b);
	}

	/**
	 * 整包下载
	 * 
	 * @param content
	 */
	public void updateApk(final String apkUrl,final String apkName) {

		Log.i("wwl", "apkUrl=");
		Log.i("wwl", apkUrl);

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				new DownloadApk(instance, apkUrl,apkName).start();
			}
		});
	}

	/**
	 * 获取app内置版本号
	 * 
	 * @param content
	 */
	public int getAppVersionCode() {
		PackageInfo pkg = null;
		try {
			pkg = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		if (pkg != null) {
			int versionCode = pkg.versionCode;
			return versionCode;
		}

		return 0;
	}

	/**
	 * 获取apk包的版本名
	 */
	public String getAppVersionName() {
		PackageInfo pkg = null;
		try {
			pkg = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		if (pkg != null) {
			String versionName = pkg.versionName;
			return versionName;
		}

		return "";
	}

	// -------------------------- 打开头像选择菜单------------------------------------
	public void OpenHeadSelectMenu() {
		Log("hahahaha");
		Intent intent = new Intent(MainGameActivity.this, MenuMainty.class);
		startActivity(intent);
	}

	// 设置玩家头像保存的文件信息
	// saveFolder:保存的目录
	// fileName:保存的文件名称
	public void SetPlayerHeadImgMsg(String saveFolder, String fileName) {
		GameGloableData.setHeadImagSaveFileName(fileName);
		GameGloableData.setHeadImagSaveFolderPath(saveFolder);
	}

	// --------------------------------------------------------------------------------

	/**
	 * 获取apk包的版本名
	 */
	public String getAppPackageName() {
		return getPackageName();
	}

	public String getChannelId() {
		return "哈哈哈， 这个是渠道号是假的！！！！！！";
	}

	// 获取剪贴板里的内容
	public String GetClipboardContent(int maxLen) {
		String text = "default";
		try {
			final ClipboardManager cm = (ClipboardManager) instance
					.getSystemService(CLIPBOARD_SERVICE);
			ClipData data = cm.getPrimaryClip();
			// ClipData 里保存了一个ArryList 的 Item 序列， 可以用 getItemCount() 来获取个数
			if (data.getItemCount() > 0) {
				ClipData.Item item = data.getItemAt(0);
				text = item.getText().toString();// 注意 item.getText 可能为空
				if (text.length() > maxLen) {
					text = text.substring(0, maxLen - 2);
				}
			}
			return text;
		} catch (Exception e) {
		}
		return text;

	}

	// 获取手机的mac地址
	public String GetMac() {
		Log.i("wwl", "GetPhoneMac in java");
		String resString = MacHandler.getAdresseMAC(instance);
		Log.i("wwl", "设备号");
		Log.i("wwl", "=" + resString);
		return resString;
	}

	
	public String GetSystemTime(String format){
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);//yyyy-MM-dd HH:mm E
			Date dt = new Date();
			String str_time = sdf.format(dt);
			return str_time;
		} catch (Exception e) {
			return "";
		}
		
	}
	
	
	
	
	//---------------------------
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        ValueCallback<Uri> mUploadMessage = WebViewPlugin.mUploadMessage;
    	ValueCallback<Uri[]> mUploadCallbackAboveL = WebViewPlugin.mUploadCallbackAboveL;
    	int FILECHOOSER_RESULTCODE = WebViewPlugin.FILECHOOSER_RESULTCODE;
        
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL)
                return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
    	
    	ValueCallback<Uri> mUploadMessage = WebViewPlugin.mUploadMessage;
    	ValueCallback<Uri[]> mUploadCallbackAboveL = WebViewPlugin.mUploadCallbackAboveL;
    	int FILECHOOSER_RESULTCODE = WebViewPlugin.FILECHOOSER_RESULTCODE;
    	
        if (requestCode != FILECHOOSER_RESULTCODE || mUploadCallbackAboveL == null) {
            return;
        }

        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {

            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();

                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }

                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
        return;
    }
	
	
    public byte[] LoadStreamingAssetsFile(String path){
    	return LoadFile.loadFile(path);
    	
    }
    
    public boolean CheckAppAvilible(String packageName) {    
    	try {
    		final PackageManager packageManager = getPackageManager();// 获取packagemanager
        	List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息    
        	if (pinfo != null) {        
        	  for (int i = 0; i < pinfo.size(); i++) {            
        	    String pn = pinfo.get(i).packageName;
        	     if (pn.equals(packageName)) //"com.tencent.mm"
        	    	 return true;
        	    }    
        	  }    
		} catch (Exception e) {			
		}    	
    	return false;
	}
    
    public void OpenApp(String pkg, String cls){
    	try {
    		Intent intent = new Intent();
        	ComponentName cmp=new ComponentName(pkg,cls); //"com.tencent.mm","com.tencent.mm.ui.LauncherUI"
        	intent.setAction(Intent.ACTION_MAIN);
        	intent.addCategory(Intent.CATEGORY_LAUNCHER);
        	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	intent.setComponent(cmp);
        	startActivity(intent);	
		} catch (Exception e) {			
		}
    	
    }
    
    
    public void CreatHttp(final int id,final String reqUrl) {
        //开启线程，发送请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                BufferedReader reader = null;
                try {
                    URL url = new URL(reqUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    //设置请求方法
                    connection.setRequestMethod("GET");
                    //设置连接超时时间（毫秒）
                    connection.setConnectTimeout(10000);
                    //设置读取超时时间（毫秒）
                    connection.setReadTimeout(10000);

                    //返回输入流
                    InputStream in = connection.getInputStream();

                    //读取输入流
                    reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    HttpSucc(id,result.toString());
                } catch (Exception e) {
                	HttpFail(id,e.toString());
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection != null) {//关闭连接
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }

    
    private void HttpFail(final int id,final String str) {
    	runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	String jsonString = "{" +    "\"id\":"+id +     ","+"\"HTTPRESULT\":"+str+             "}";
            	CallUnity("HttpFailFromSys", jsonString);
            }
        });    	
	}
    
    private void HttpSucc(final int id,final String str) {
    	runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	String finalStr = id +     "****||****||***||***||*****||***"+    str;
            	CallUnity("HttpSuccFromSys", finalStr);
            }
        });    	
    	
	}
    
    View dragFloatButtonView = null;
    String mTips = "";
	
	/**
	 * 显示可拖动的悬浮按钮
	 */
    public void ShowDragFloatButtonView(String tips)
    {
    	mTips = tips;
    	
    	runOnUiThread(new Runnable() {
            @Override
            public void run() {
            	RemoveDragFloatButtonView();
            	dragFloatButtonView = LayoutInflater.from(instance).inflate(R.layout.drag_float_button, null);
            	
            	addContentView(dragFloatButtonView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            	
            	DragFloatButton mBtn = dragFloatButtonView.findViewById(R.id.drag_float_button);
                mBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    	ShowNormalDialog();
                    }
                });
                
            }
        }); 
    }
    
	/**
	 * 移除可拖动的悬浮按钮
	 */
    private void RemoveDragFloatButtonView()
    {
        if (dragFloatButtonView != null) {
            ViewGroup parentViewGroup = (ViewGroup) dragFloatButtonView.getParent();
            if (parentViewGroup != null ) {
                parentViewGroup.removeView(dragFloatButtonView);
                dragFloatButtonView = null;
            }
        }
    }
    
    private void ShowNormalDialog(){
    	if(this.mTips.isEmpty() || mTips.length() <= 0)
    	{
        	RemoveDragFloatButtonView();
        	CallUnity("OnDragFloatButtonClicked", "");
        	return;
    	}
    	
        AlertDialog.Builder normalDialog = new AlertDialog.Builder(this);
        normalDialog.setTitle("提示");
        normalDialog.setMessage(mTips);

        //设置按钮
        normalDialog.setPositiveButton("确定"
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    	RemoveDragFloatButtonView();
                    	CallUnity("OnDragFloatButtonClicked", "");
                    }
                });
        normalDialog.setNegativeButton("取消"
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        normalDialog.create().show();
      }
    
    
    public void InitAppVestWithArg(String skey, String suuid)
    {
        //SDK初始化
        int res = AppVest.init(skey, suuid);
        if (res == -1) {
            Log.i("Unity", "高防初始化失败 ql");
            return;
        }
        Log.i("Unity", "高防初始化成功 ql");
    }

    public void InitAppVest()
    {
    	InitAppVestWithArg("553c12676eb80b82183e0944df97cd19",
    			"K8N/nk3cAxhGo7Oy0yoP/Ye5jcQUaMf+8+AutonLIoCXTbHXSHJ/D8W3/iIBHJq/4SABnAF7RvIeNRnF+x+wjB6cB1TEwIUviviQlofNvM4qJFLH55ON85n2pFBhSGW3qLF/"
    	);
    }
    
    public void InitAppVest2(String suuid, String skey)
    {
    	InitAppVestWithArg(skey,suuid);
    }
    
    public void InitAppVestKiwi(String appkey)
    {
    	int ret = Kiwi.Init(appkey);
    	Log.i("Unity", "kiwi高防初始化结果 " + ret);
    }
    
    public String GetServerIPAndPort(String domain, String port)
    {
    	SecurityConnection conn = AppVest.getServerIPAndPort(domain, Integer.parseInt(port));
    	if (conn.getServerPort() == -1) {
    		Log.i("Unity", "获取IP和端口失败");
    		return domain + "|" + port;
    	}
    	String newValue = String.format("%s|%s", conn.getServerIp(), conn.getServerPort());
    	return newValue;
    }
    
    public String GetServerIPAndPortKiwi(String rsname)
    {
    	final StringBuffer ip = new StringBuffer();
    	final StringBuffer port = new StringBuffer();
    	// 请替换真实rs标识
    	final int ret = Kiwi.ServerToLocal(rsname, ip, port);
    	if (ret < 0) {
    		return "127.0.0.1|8888";
    	}
    	return String.format("%s|%s", ip, port);
    }
    
    public void SendTextToApp(final String content)
    {
    	runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, content);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        }); 
    }
    
    public String GetMacAddress()
    {
        String mac = "";
        if (Build.VERSION.SDK_INT < 23) {
            mac = getMacBySystemInterface(getApplicationContext());
            //Log.v("Unity", "### 11 GetMacAddress:"+mac);
        } else {
            mac = getMacByJavaAPI();
            if (mac == null || mac.isEmpty()){
                mac = getMacBySystemInterface(getApplicationContext());
                //Log.v("Unity", "### 22 GetMacAddress:"+mac);
            }
            //Log.v("Unity", "### 33 GetMacAddress:"+mac);
        }
        return mac;
    }
    
    private String getMacByJavaAPI() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netInterface = interfaces.nextElement();
                if ("wlan0".equalsIgnoreCase(netInterface.getName()) || "eth0".equalsIgnoreCase(netInterface.getName())) {
                    byte[] addr = netInterface.getHardwareAddress();
                    if (addr == null || addr.length == 0) {
                        return null;
                    }
                    StringBuilder buf = new StringBuilder();
                    for (byte b : addr) {
                        buf.append(String.format("%02X:", b));
                    }
                    if (buf.length() > 0) {
                        buf.deleteCharAt(buf.length() - 1);
                    }
                    return buf.toString().toLowerCase(Locale.getDefault());
                }
            }
        } catch (Throwable e) {
        }
        return null;
    }

    private String getMacBySystemInterface(Context context) {
        try {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(true);
            wifi.setWifiEnabled(false);
            //一定需要Manifest.permission.ACCESS_WIFI_STATE权限
            WifiInfo info = wifi.getConnectionInfo();
            return info.getMacAddress();
        } catch (Throwable e) {
            return "";
        }
    }
    
	//分享内容到第三方APP
	// shareType 1 微信好友；2 微信朋友圈；3 QQ好友；4 QQ说说；5 微博
    public void ShareContentToOtherApps(int shareType, String content)
    {
    	if (shareType == 2){
    		ShareContent2WechatTimeline(instance.getApplicationContext(), content);
    	}
    }
    
    //微信的包名
    private static final String PACKAGE_WECHAT = "com.tencent.mm";
    
    // 判断是否安装指定app
    public boolean isInstallApp(Context context, String app_package)
    {
       final PackageManager packageManager = context.getPackageManager();
       List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
       if (pInfo != null) {
           for (int i = 0; i < pInfo.size(); i++) {
               String pn = pInfo.get(i).packageName;
               if (app_package.equals(pn)) {
                   return true;
               }
           }
       }
       return false;
    }

    /**
    * 直接分享文本到微信好友
    *
    * @param context 上下文
    */
    public void ShareContent2WechatTimeline(Context context, String content) {
       if (isInstallApp(context, MainGameActivity.PACKAGE_WECHAT)) {
           File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();
           String fileName = "share";
           File appDir = new File(file, fileName);
           if (!appDir.exists()) {
               appDir.mkdirs();
           }
           fileName = "ryhysharewechattimeline.jpg";
           File currentFile = new File(appDir, fileName);
           FileOutputStream fos = null;
           try {
               fos = new FileOutputStream(currentFile);
               BitmapDrawable bitmapDrawable = (BitmapDrawable)ContextCompat.getDrawable(context,R.drawable.app_icon);  
               Bitmap bitmap=bitmapDrawable.getBitmap();  
               bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
               fos.flush();
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           } finally {
               try {
                   if (fos != null) {
                       fos.close();
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
           
           Intent intent = new Intent();
           //分享精确到微信的页面，朋友圈页面，或者选择好友分享页面
           ComponentName comp = new ComponentName(PACKAGE_WECHAT, "com.tencent.mm.ui.tools.ShareToTimeLineUI");
           intent.setComponent(comp);
           intent.setAction(Intent.ACTION_SEND);
           intent.setType("image/*");
           //添加Uri图片地址
           Uri uri = null;
           try {
               ApplicationInfo applicationInfo = context.getApplicationInfo();
               int targetSDK = applicationInfo.targetSdkVersion;
               if (targetSDK >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                   uri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(), currentFile.getAbsolutePath(), "ryhyshare", null));
               } else {
                   uri = Uri.fromFile(currentFile);
               }
               intent.putExtra(Intent.EXTRA_STREAM, uri);
           } catch (FileNotFoundException e) {
               e.printStackTrace();
           }
           
           String newContent = "诚信大平台，注册立即送30！    " + content;
           intent.putExtra(Intent.EXTRA_TEXT, newContent);
           intent.putExtra("Kdescription", newContent);
           intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           context.startActivity(intent);
           
           instance.CallUnity("SuccessShareContentToOtherApps", "");
       } else {
           Toast.makeText(context, "您需要安装微信客户端", Toast.LENGTH_LONG).show();
       }
    }
}
