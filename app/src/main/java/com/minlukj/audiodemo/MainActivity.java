package com.minlukj.audiodemo;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.minlukj.audiolib.MediaRecorderUtils;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getCanonicalName();

  private TextView mSecond;
  private TextView mDB;

  private MediaRecorderUtils mMediaRecorderUtils;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mSecond = findViewById(R.id.tv_second);
    mDB = findViewById(R.id.tv_db);
    //初始化Utils
    mMediaRecorderUtils = new MediaRecorderUtils.Builder(this).setAudioSource(MediaRecorder.AudioSource.MIC)//麦克
        .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)//AMR
        .setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)//AMR
        .setDecibelSpace(100)//获取分贝的间隔
        .build();
    //最大10秒
    mMediaRecorderUtils.setMaximum(10);
    mMediaRecorderUtils.setMediaRecorderCallBack(new MediaRecorderUtils.MediaRecorderCallBack() {
      @Override public void start() {
        //开始录制
        Log.i(TAG, "开始录制");
      }

      @Override public void stop() {
        //结束录制
        Log.i(TAG, "结束录制");
      }

      @Override public void ioError(String ioError) {
        Log.i(TAG, "发生错误" + ioError);
      }

      @Override public void error(String error) {
        //错误
        Log.i(TAG, "发生错误" + error);
        Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
      }

      @Override public void process(int second) {
        //已录制秒数
        Log.i(TAG, "已经录制了：" + second);
        mSecond.setText(String.valueOf(second) + "s");
      }

      @Override public void decibel(int decibel) {
        Log.i(TAG, "分贝大小：" + decibel);
        mDB.setText("分贝：" + decibel);
      }
    });
  }

  //开始录音
  public void startOnClick(View v) {
    mMediaRecorderUtils.start();
  }

  //结束录音
  public void stopOnClick(View v) {
    mMediaRecorderUtils.stop();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    //页面销毁的时候会存储关闭前的录音
    mMediaRecorderUtils.onDestroy();
  }
}
