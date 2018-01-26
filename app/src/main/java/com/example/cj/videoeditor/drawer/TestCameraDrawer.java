package com.example.cj.videoeditor.drawer;

import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.example.cj.videoeditor.filter.AFilter;
import com.example.cj.videoeditor.filter.OesFilter;
import com.example.cj.videoeditor.gpufilter.SlideGpuFilterGroup;
import com.example.cj.videoeditor.record.video.TextureMovieEncoder;
import com.example.cj.videoeditor.utils.MatrixUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cj on 2017/8/2.
 * desc 管理图像绘制的类
 * 主要用于管理各种滤镜、画面旋转、视频编码录制等
 */

public class TestCameraDrawer implements GLSurfaceView.Renderer {

    private float[] mMatrix = new float[16];
    private SurfaceTexture mSurfaceTextrue;
    /**
     * 预览数据的宽高
     */
    private int mPreviewWidth = 0, mPreviewHeight = 0;
    /**
     * 控件的宽高
     */
    private int width = 0, height = 0;
    private AFilter mOesFilter;
    private int mCameraId = 1;
    private int mTextureID;



    private TextureMovieEncoder videoEncoder;
    private boolean recordingEnabled;
    private int recordingStatus;
    private static final int RECORDING_OFF = 0;
    private static final int RECORDING_ON = 1;
    private static final int RECORDING_RESUMED = 2;
    private static final int RECORDING_PAUSE = 3;
    private static final int RECORDING_RESUME = 4;
    private static final int RECORDING_PAUSED = 5;
    private String savePath;
    private int[] fFrame = new int[2];
    private int[] fTexture = new int[2];


    public TestCameraDrawer(Resources res) {
        mOesFilter = new OesFilter(res);


        recordingEnabled = false;
    }


    public void setDataSize(int dataWidth, int dataHeight) {
        this.mPreviewWidth = dataWidth;
        this.mPreviewHeight = dataHeight;
        calculateMatrix();
    }

    public void setViewSize(int width, int height) {
        this.width = width;
        this.height = height;
        calculateMatrix();
    }


    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTextrue;
    }

    public void setCameraId(int id) {
        this.mCameraId = id;
        calculateMatrix();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        mTextureID = createTextureID();
        mSurfaceTextrue = new SurfaceTexture(mTextureID);
        mOesFilter.create();
        mOesFilter.setTextureId(mTextureID);


        if (recordingEnabled) {
            recordingStatus = RECORDING_RESUMED;
        } else {
            recordingStatus = RECORDING_OFF;
        }
    }


    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        setViewSize(width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        if(mSurfaceTextrue!=null){
            mSurfaceTextrue.updateTexImage();
        }
        mOesFilter.draw();
        /**更新界面中的数据*//*
        mSurfaceTextrue.updateTexImage();

        EasyGlUtils.bindFrameTexture(fFrame[0], fTexture[0]);
        GLES20.glViewport(0, 0, mPreviewWidth, mPreviewHeight);
        EasyGlUtils.unBindFrameBuffer();


        if (recordingEnabled) {
            *//**说明是录制状态*//*
            switch (recordingStatus) {
                case RECORDING_OFF:
                    videoEncoder = new TextureMovieEncoder();
                    videoEncoder.setPreviewSize(mPreviewWidth, mPreviewHeight);
                    videoEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                            savePath, mPreviewWidth, mPreviewHeight,
                            3500000, EGL14.eglGetCurrentContext(),
                            null));
                    recordingStatus = RECORDING_ON;
                    break;
                case RECORDING_RESUMED:
                    videoEncoder.updateSharedContext(EGL14.eglGetCurrentContext());
                    videoEncoder.resumeRecording();
                    recordingStatus = RECORDING_ON;
                    break;
                case RECORDING_ON:
                case RECORDING_PAUSED:
                    break;
                case RECORDING_PAUSE:
                    videoEncoder.pauseRecording();
                    recordingStatus = RECORDING_PAUSED;
                    break;

                case RECORDING_RESUME:
                    videoEncoder.resumeRecording();
                    recordingStatus = RECORDING_ON;
                    break;

                default:
                    throw new RuntimeException("unknown recording status " + recordingStatus);
            }

        } else {
            switch (recordingStatus) {
                case RECORDING_ON:
                case RECORDING_RESUMED:
                case RECORDING_PAUSE:
                case RECORDING_RESUME:
                case RECORDING_PAUSED:
                    videoEncoder.stopRecording();
                    recordingStatus = RECORDING_OFF;
                    break;
                case RECORDING_OFF:
                    break;
                default:
                    throw new RuntimeException("unknown recording status " + recordingStatus);
            }
        }
        *//**绘制显示的filter*//*
        GLES20.glViewport(0, 0, width, height);
        if (videoEncoder != null && recordingEnabled && recordingStatus == RECORDING_ON) {
            videoEncoder.setTextureId(fTexture[1]);
            videoEncoder.frameAvailable(mSurfaceTextrue);
        }*/
    }


    //-----------------------------------------------------------------------------------------


    private void calculateMatrix() {
        MatrixUtils.getShowMatrix(mMatrix, this.mPreviewWidth, this.mPreviewHeight, this.width, this.height);
        if (mCameraId == 1) {
            MatrixUtils.flip(mMatrix, true, false);
            MatrixUtils.rotate(mMatrix, 90);
        } else {
            MatrixUtils.rotate(mMatrix, 270);
        }
        mOesFilter.setMatrix(mMatrix);
    }

    /**
     * 触摸事件的传递
     */
    public void onTouch(MotionEvent event) {
        //mSlideFilterGroup.onTouchEvent(event);
    }

    /**
     * 滤镜切换的事件监听
     */
    public void setOnFilterChangeListener(SlideGpuFilterGroup.OnFilterChangeListener listener) {
        //mSlideFilterGroup.setOnFilterChangeListener(listener);
    }

    /**
     * 设置预览效果的size
     */
    public void setPreviewSize(int width, int height) {
        if (mPreviewWidth != width || mPreviewHeight != height) {
            mPreviewWidth = width;
            mPreviewHeight = height;
        }
    }

    /**
     * 提供修改美白等级的接口
     */
    public void changeBeautyLevel(int level) {
        //mBeautyFilter.setBeautyLevel(level);
    }

    public int getBeautyLevel() {
        //return mBeautyFilter.getBeautyLevel();
        return 0;
    }

    /**
     * 根据摄像头设置纹理映射坐标
     */
   /* public void setCameraId(int id) {
        //drawFilter.setFlag(id);
    }*/
    public void startRecord() {
        recordingEnabled = true;
    }

    public void stopRecord() {
        recordingEnabled = false;
    }

    public void setSavePath(String path) {
        this.savePath = path;
    }

    public SurfaceTexture getTexture() {
        return mSurfaceTextrue;
    }

    public void onPause(boolean auto) {
        if (auto) {
            videoEncoder.pauseRecording();
            if (recordingStatus == RECORDING_ON) {
                recordingStatus = RECORDING_PAUSED;
            }
            return;
        }
        if (recordingStatus == RECORDING_ON) {
            recordingStatus = RECORDING_PAUSE;
        }
    }

    public void onResume(boolean auto) {
        if (auto) {
            if (recordingStatus == RECORDING_PAUSED) {
                recordingStatus = RECORDING_RESUME;
            }
            return;
        }
        if (recordingStatus == RECORDING_PAUSED) {
            recordingStatus = RECORDING_RESUME;
        }
    }

    /**
     * 创建显示的texture
     */
    private int createTextureID() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    public void useTexParameter() {
        //设置缩小过滤为使用纹理中坐标最接近的一个像素的颜色作为需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        //设置放大过滤为使用纹理中坐标最接近的若干个颜色，通过加权平均算法得到需要绘制的像素颜色
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        //设置环绕方向S，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        //设置环绕方向T，截取纹理坐标到[1/2n,1-1/2n]。将导致永远不会与border融合
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }

    public void changeRatio() {
        //clipFilter.verticalFlipVertexRatio();
    }
}
