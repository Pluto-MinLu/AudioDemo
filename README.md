# 使用**MediaRecorder**进行音频录制
[![](https://jitpack.io/v/Chen-Xi-g/AudioDemo.svg)](https://jitpack.io/#Chen-Xi-g/AudioDemo)

### 手动停止录音，显示分贝和录音时长。
![显示时长和分贝，点击停止](https://github.com/Chen-Xi-g/AudioDemo/blob/master/audio1.gif)

### 最大时间自动停止，显示分贝和录音时长。
![显示时长和分贝，自动停止](https://github.com/Chen-Xi-g/AudioDemo/blob/master/audio2.gif)

 How to
--

##### To get a Git project into your build:
 
##### **Step 1. Add the JitPack repository to your build file**
 
##### Gradle

 ```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

##### **Step 2. Add the dependency**

 ```
dependencies {
       implementation 'com.github.Chen-Xi-g:AudioDemo:v1.0.3'
}
```

 ### 如何使用(How to use)
 
 #### 1.初始化(Initialization)
 
```java
/**
 *在需要进行音频录制的页面进行初始化
 *如果不设置setAudioSource()，setAudioEncoder()，setOutputFormat()，setDecibelSpace()会使用默认值进行输出
 */
MediaRecorderUtils mMediaRecorderUtils = new MediaRecorderUtils.Builder(this)
                   .setAudioSource(MediaRecorder.AudioSource.MIC)//麦克
                   .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)//AMR
                   .setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)//AMR
                   .setDecibelSpace(500)//获取分贝的间隔
                   .build();


```
 
 #### 2.使用(Use)
 
```java
//设置最大录制时间 秒，需要在start前调用
MediaRecorderUtils.setMaximum(int second);
//开始录制
MediaRecorderUtils.start();
//停止录制
MediaRecorderUtils.stop();
//判断当前是否正在进行录制
MediaRecorderUtils.isRecording();
//获取音频文件路径
MediaRecorderUtils.getPath();
//获取当前所有已录制音频文件路径
MediaRecorderUtils.getPathList();
//在Activity或Fragment的onDestory中调用，页面销毁时会保存音频
MediaRecorderUtils.onDestroy();
//音频录制监听回调
MediaRecorderUtils.setMediaRecorderCallBack(new MediaRecorderUtils.MediaRecorderCallBack() {
      @Override public void start() {
        //开始录制
        Log.i(TAG, "开始录制");
      }

      @Override public void stop() {
        //结束录制
        Log.i(TAG, "结束录制");
      }

      @Override public void error(String error) {
        //错误
        Log.i(TAG, "发生错误" + error);
      }

      @Override public void process(int second) {
        //已录制秒数
        Log.i(TAG, "已经录制了：" + second);
      }

      @Override public void decibel(int decibel) {
        Log.i(TAG, "分贝大小：" + decibel);
      }
    });

```

 ### 3.自定义文件路径
```java
  /**
   *如果你需要自定义录制文件保存的路径请下载lib中的MediaRecorderUtils代码。
   * 在MediaRecorderUtils的方法getExternalDir()里去设置自己的文件路径。
   */
  private String getExternalDir() {
    // 在这里修改你的路径
    String path = Environment.getExternalStorageDirectory().getPath() + "/" + mContext.getPackageName() + "/recorder";
    //...
  }
 /**
  *如果你需要额外的录制回调，你可以在MediaRecorderCallBack中添加。
  */
  public interface MediaRecorderCallBack {
      void start();//开始录制

      void stop();//停止录制

      void error(String error);

      //second 录制了几秒
      void process(int second);//录制中

      void decibel(int decibel);//分贝大小
  }
```

### 具体代码可以看Demo中示例

 ### 如果你感觉对你有用的话请点一下Star吧，而且你还可以打赏一波(If you feel useful to you, please click Star, or you can reward it.)
 
 <img src="http://r.photo.store.qq.com/psb?/V12LSg7n0Vj1Fg/JIE.r7vzYd0JdQV4.U8AFDF2wy5d*DXixdQZ2ZFiV6I!/r/dEYBAAAAAAAA" height = "400" width = "300">      <img src="http://r.photo.store.qq.com/psb?/V12LSg7n0Vj1Fg/64q8qbMEanfoAXbFWxrESl6QXS7ITX63kCabiSRL440!/r/dLYAAAAAAAAA" height = "400" width = "300">
 
 ### 如何联系我(How to contact me)
 
 **QQ:** 1217056667
 
 **邮箱(Email):** a912816369@gmail.com
 
 **小站:** www.minlukj.com
 
 
 
