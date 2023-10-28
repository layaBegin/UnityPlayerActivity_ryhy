package com.kola;

/**
 * Created by Administrator on 2017/9/27.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.unity3d.player.UnityPlayer;

public class BatteryReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (Intent.ACTION_BATTERY_OKAY.equals(intent.getAction())) {
//            Toast.makeText(context, "电量已恢复，可以使用!", Toast.LENGTH_LONG).show();
//        }
//        if (Intent.ACTION_BATTERY_LOW.equals(intent.getAction())) {
//            Toast.makeText(context, "电量过低，请尽快充电！", Toast.LENGTH_LONG).show();
//        }
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            // 获取当前电量
            int current = bundle.getInt("level");
            // 获取总电量
            int total = bundle.getInt("scale");
            UnityPlayer.UnitySendMessage("SDK","OnElectricity",String.valueOf(100*current/total));
        }

    }

}