package com.minlukj.audiolib;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 录音工具类
 * 使用Builder模式方便创建调用
 */
public class MediaRecorderUtils {

  private final String TAG = MediaRecorderUtils.class.getCanonicalName();
  //音频来源  ,  输出格式  ,  编码方式
  private final int AUDIO_SOURCE, OUTPUT_FORMAT, AUDIO_ENCODER;
  private MediaRecorderCallBack mMediaRecorderCallBack;
  //使用MediaRecorder录音
  private MediaRecorder mMediaRecorder;
  private boolean isRecording;
  //已完成录音路径
  private String path;
  //当前所有录音集合
  private ArrayList<String> mPathList;
  private Context mContext;
  //获取分贝的间隔
  private int SPACE;
  //输出文件
  private File mFile;
  //最大录制秒
  private int mMaximum;
  //当前录制了多少秒
  private int mSecond;
  private int BASE = 1;
  //设置录音最大时间，0为不设置
  private Handler mHandler = new Handler();
  private Runnable mDbRunnable = new Runnable() {
    @Override public void run() {
      getDecibel();
    }
  };
  private Runnable mRunnable = new Runnable() {
    @Override public void run() {
      if (mHandler != null) {
        //最大录制时间不等于0时就是已经设置了
        if (mSecond * 1000 >= mMaximum) {
          mHandler.removeCallbacks(mRunnable);
          stop();
        } else {
          mHandler.postDelayed(mRunnable, 1000);
          if (mMediaRecorderCallBack != null) {
            mMediaRecorderCallBack.process(++mSecond);
          }
        }
      }
    }
  };

  private MediaRecorderUtils(Builder mBuilder) {
    mPathList = new ArrayList<>();
    this.mContext = mBuilder.mContext;
    this.AUDIO_SOURCE = mBuilder.AUDIO_SOURCE;
    this.OUTPUT_FORMAT = mBuilder.OUTPUT_FORMAT;
    this.AUDIO_ENCODER = mBuilder.AUDIO_ENCODER;
    this.SPACE = mBuilder.SPACE;
    initMediaRecorder();
  }

  /**
   * 最大秒数限制 0为默认不设置
   *
   * @param second 秒
   */
  public void setMaximum(int second) {
    this.mMaximum = second * 1000;
  }

  private void initMediaRecorder() {
    if (mMediaRecorder == null) {
      mMediaRecorder = new MediaRecorder();
    }
    // 设置音频来源     MIC == 麦克
    mMediaRecorder.setAudioSource(AUDIO_SOURCE == -2 ? MediaRecorder.AudioSource.MIC : AUDIO_SOURCE);
    // 设置默认音频输出格式   .amr 格式
    mMediaRecorder.setOutputFormat(OUTPUT_FORMAT == -2 ? MediaRecorder.OutputFormat.AMR_WB : OUTPUT_FORMAT);
    // 设置默认音频编码方式   .amr 编码
    mMediaRecorder.setAudioEncoder(AUDIO_ENCODER == -2 ? MediaRecorder.AudioEncoder.AMR_WB : AUDIO_ENCODER);
    if (mFile == null) {
      mFile = new File(getExternalDir(), System.currentTimeMillis() + ".amr");
      if (!mFile.exists()) {
        try {
          mFile.createNewFile();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    path = mFile.getAbsolutePath();
    mPathList.add(path);
    //指定音频输出文件路径
    mMediaRecorder.setOutputFile(mFile.getAbsolutePath());
  }

  //是否正在录音
  public boolean isRecording() {
    return isRecording;
  }

  //获取录音文件路径
  public String getPath() {
    return path;
  }

  //获取当前所有录音文件路径
  public ArrayList<String> getPathList() {
    return mPathList;
  }

  //获取分贝大小
  private void getDecibel() {
    if (mMediaRecorder == null) return;
    double ratio = (double) mMediaRecorder.getMaxAmplitude() / BASE;
    double decibel = 0;
    if (ratio > 0) {
      decibel = 20 * Math.log10(ratio);
    }
    mHandler.postDelayed(mDbRunnable, SPACE);
    if (mMediaRecorderCallBack != null) mMediaRecorderCallBack.decibel((int) decibel);
  }

  //开始录音
  public void start() {
    if (mMediaRecorder == null) {
      initMediaRecorder();
    }
    if (!isRecording) {
      try {
        isRecording = true;
        mMediaRecorder.prepare();
        mMediaRecorder.start();  //开始录制
        //大于0判断是否最大时间显示
        if (mMaximum > 0) {
          mHandler.postDelayed(mRunnable, 1000);
        }
        //开始录制
        if (mMediaRecorderCallBack != null) {
          mMediaRecorderCallBack.start();
          getDecibel();
        }
      } catch (IOException e) {
        if (mMediaRecorderCallBack != null) {
          mMediaRecorderCallBack.stop();
          mMediaRecorderCallBack.ioError(e.getMessage());
        }
        //e.printStackTrace();
        isRecording = false;
      }
    } else {
      Log.i(TAG, "音频录制中...");
    }
  }

  //停止录音
  public void stop() {
    if (mMediaRecorder != null && isRecording) {
      if (mMediaRecorderCallBack != null) {
        mMediaRecorderCallBack.stop();
      }
      //停止分贝获取
      if (mHandler != null) {
        mHandler.removeCallbacks(mDbRunnable);
        mHandler.removeCallbacks(mRunnable);
      }
      isRecording = false;
      try {
        mSecond = 0;
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mMediaRecorder = null;
        mFile = null;
      } catch (RuntimeException e) {
        if (mMediaRecorderCallBack != null) {
          mMediaRecorderCallBack.error("时间太短");
        }
      } finally {
        mMediaRecorder = null;
        mFile = null;
      }
    } else {
      Log.i(TAG, "录音未开始");
    }
  }

  //必须在onDestroy调用此方法，否则会消耗资源
  public void onDestroy() {
    if (mMediaRecorder != null && isRecording) {
      isRecording = false;
      mMediaRecorder.stop();
      mMediaRecorder.release();
      mPathList.clear();
    }
    mHandler.removeCallbacks(mRunnable);
    mHandler.removeCallbacks(mDbRunnable);
    mHandler = null;
    mRunnable = null;
    mDbRunnable = null;
    mMediaRecorder = null;
  }

  public void setMediaRecorderCallBack(MediaRecorderCallBack mediaRecorderCallBack) {
    this.mMediaRecorderCallBack = mediaRecorderCallBack;
  }

  /**
   * 获取外部存储目录.
   */
  private String getExternalDir() {
    //新建录音存储的文件夹
    String path = Environment.getExternalStorageDirectory().getPath() + "/" + mContext.getPackageName() + "/recorder";

    //如果目录不存在,创建目录.
    File file = new File(path);
    if ((file != null) && (!file.exists())) {
      file.mkdirs();
    }
    return path;
  }

  public interface MediaRecorderCallBack {
    void start();//开始录制

    void stop();//停止录制

    void ioError(String ioError);

    //这里提示错误
    void error(String error);

    //second 录制了几秒
    void process(int second);//录制中

    void decibel(int decibel);//分贝大小
  }

  //=====================Builder 开始===============================
  public static class Builder {
    private Context mContext;
    //可选字段，不传递参数为默认值
    private int AUDIO_SOURCE, OUTPUT_FORMAT, AUDIO_ENCODER = -2;
    private int SPACE = 500;

    //这里的File会替换掉重新录音前的文件，如果不想替换传递null
    public Builder(Context context) {
      this.mContext = context;
    }

    public Builder setDecibelSpace(int space) {
      this.SPACE = space;
      return this;
    }

    public Builder setAudioSource(int audioSource) {
      this.AUDIO_SOURCE = audioSource;
      return this;
    }

    //-------------------------------------------------------
    //            如果改变编码格式File的后缀也必须改变
    public Builder setOutputFormat(int outputFormat) {
      this.OUTPUT_FORMAT = outputFormat;
      return this;
    }

    public Builder setAudioEncoder(int audioEncoder) {
      this.AUDIO_ENCODER = audioEncoder;
      return this;
    }
    //----------------------------------------------------------

    public MediaRecorderUtils build() {
      return new MediaRecorderUtils(this);
    }
  }
  //=====================Builder 结束==============================================================
}
