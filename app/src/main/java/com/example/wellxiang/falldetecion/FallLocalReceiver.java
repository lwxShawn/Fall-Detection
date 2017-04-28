package com.example.wellxiang.falldetecion;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FallLocalReceiver extends BroadcastReceiver implements AMapLocationListener {

    private TextView countingView;
    private Dialog dialog;
    private Timer timer;
    private SharedPreferences sharedPreferences;
    private Vibrator vibrator;
    private boolean isVibrate;
    private MediaPlayer mediaPlayer;

    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationClientOption;
    public String locationAddress;
    public String locationTime;
    private Context context;
    private final String TAG = "liuweixiang";


    public FallLocalReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "FallLocalReceiver.onReceive()");
        this.context = context;
        showAlertDialog();

        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        isVibrate = sharedPreferences.getBoolean("pre_key_vibrate", true);
        if(isVibrate){
            startVibrate();
        }
        startAlarm();
        startLocation();


    }


    /*
    弹窗报警
     */
    private void showAlertDialog() {
        countingView = new TextView(context);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context.getApplicationContext());
        builder.setTitle("跌倒警报");
        builder.setView(countingView);
        builder.setMessage("检测到跌倒发生，是否发出警报？");
        builder.setIcon(R.drawable.ic_warning);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timer.cancel();
                dialog.dismiss();
                if(isVibrate){
                    stopVibrate();
                }
                stopAlarm();
                Intent startIntent = new Intent(context, FallDetectionService.class);
                context.startService(startIntent);
            }
        });
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        countDown();
        dialog.show();
        Log.d(TAG, "dialog.create()");
    }

    /*
    倒计时
     */
    private void countDown() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            int countTime = 10;
            @Override
            public void run() {
                if(countTime > 0){
                    countTime --;
                }
                Message msgTime = handler.obtainMessage();
                msgTime.arg1 = countTime;
                handler.sendMessage(msgTime);
            }
        };
        timer.schedule(timerTask, 50, 1000);
    }

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.arg1 > 0){
                //动态显示倒计时
                countingView.setText("                         "
                        + msg.arg1 + "秒后自动报警");
            }else{
                //倒计时结束自动关闭
                if(dialog != null){
                    dialog.dismiss();
                    if(isVibrate){
                        stopVibrate();
                    }
                    stopAlarm();
                    sendSMS(locationAddress, locationTime);
                }
                timer.cancel();
            }
        }
    };

    /*
    开始震动
     */
    private void startVibrate(){
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 500, 100, 500};
        vibrator.vibrate(pattern, 2);
    }
    /*
    停止震动
     */
    private void stopVibrate(){
        vibrator.cancel();
    }

    /*
    开始播放铃声
     */
    private void startAlarm(){
        String ringtone = sharedPreferences.getString("pre_key_alarm" , null);
        Log.d(TAG, ringtone + "");
        Uri ringtoneUri = Uri.parse(ringtone);

        mediaPlayer = MediaPlayer.create(context, ringtoneUri);
        mediaPlayer.setLooping(true);//设置循环
        mediaPlayer.start();
    }
    /*
    停止播放铃声
     */
    private void stopAlarm(){
        mediaPlayer.stop();
    }

    private void sendSMS(String address, String time){
        //获取短信管理器
        SmsManager smsManager = SmsManager.getDefault();

        String name = sharedPreferences.getString("pre_key_name", null);
        String phoneNum = sharedPreferences.getString("pre_key_phone", null);
        String smsContent = time + name + "在" + address + "发生跌倒了！";
        smsManager.sendTextMessage(phoneNum, null, smsContent ,null, null);
        Toast.makeText(context, "短信已经发出", Toast.LENGTH_SHORT).show();
    }

    private void startLocation(){
        Log.d(TAG, "FallLocalReceiver.startLocation()");
        locationClient = new AMapLocationClient(context);
        //初始化定位参数
        locationClientOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        locationClientOption.setOnceLocationLatest(true);

        //设置定位监听
        locationClient.setLocationListener(this);
        //启动定位
        locationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                locationAddress = amapLocation.getAddress();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                locationTime = df.format(date);//定位时间
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }


}
