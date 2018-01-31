package com.example.cj.videoeditor.gpufilter;

import android.opengl.GLES20;
import android.view.MotionEvent;
import android.widget.Scroller;

import com.example.cj.videoeditor.MyApplication;
import com.example.cj.videoeditor.gpufilter.basefilter.GPUImageFilter;
import com.example.cj.videoeditor.gpufilter.helper.MagicFilterFactory;
import com.example.cj.videoeditor.gpufilter.helper.MagicFilterType;
import com.example.cj.videoeditor.utils.EasyGlUtils;


/**
 * Created by cj on 2017/7/20 0020.
 * 滑动切换滤镜的控制类
 */

public class TestSlideGpuFilterGroup {
    private MagicFilterType[] types = new MagicFilterType[]{
            MagicFilterType.NONE,
            MagicFilterType.WARM,
            MagicFilterType.ANTIQUE,
            MagicFilterType.INKWELL,
            MagicFilterType.BRANNAN,
            MagicFilterType.N1977,
            MagicFilterType.FREUD,
            MagicFilterType.HEFE,
            MagicFilterType.HUDSON,
            MagicFilterType.NASHVILLE,
            MagicFilterType.COOL
    };
    private GPUImageFilter curFilter;
    private int width, height;
    private int[] fFrame = new int[1];
    private int[] fTexture = new int[1];
    private int curIndex = 0;
    private OnFilterChangeListener mListener;

    public TestSlideGpuFilterGroup() {
        initFilter();
    }

    private void initFilter() {
        curFilter = getFilter(getCurIndex());
    }

    private GPUImageFilter getFilter(int index) {
        GPUImageFilter filter = MagicFilterFactory.initFilters(types[index]);
        if (filter == null) {
            filter = new GPUImageFilter();
        }
        return filter;
    }

    public void init() {
        curFilter.init();
    }

    public void onSizeChanged(int width, int height) {
        this.width = width;
        this.height = height;
        GLES20.glGenFramebuffers(1, fFrame, 0);
        EasyGlUtils.genTexturesWithParameter(1, fTexture, 0, GLES20.GL_RGBA, width, height);
        onFilterSizeChanged(width, height);
    }

    private void onFilterSizeChanged(int width, int height) {
        curFilter.onInputSizeChanged(width, height);
        curFilter.onDisplaySizeChanged(width, height);
    }

    public int getOutputTexture() {
        return fTexture[0];
    }

    public void onDrawFrame(int textureId) {
        EasyGlUtils.bindFrameTexture(fFrame[0], fTexture[0]);
        curFilter.onDrawFrame(textureId);
        /*if (direction == 0 && offset == 0) {
            curFilter.onDrawFrame(textureId);
        } else if (direction == 1) {
            onDrawSlideLeft(textureId);
        } else if (direction == -1) {
            onDrawSlideRight(textureId);
        }*/
        EasyGlUtils.unBindFrameBuffer();
    }


    public void destroy() {
        curFilter.destroy();
    }


    private int getCurIndex() {
        return curIndex;
    }



    public void setOnFilterChangeListener(OnFilterChangeListener listener) {
        this.mListener = listener;
    }

    public interface OnFilterChangeListener {
        void onFilterChange(MagicFilterType type);
    }

    public void onPageChange(int position) {
        curIndex = position;
        reCreateFilter();

    }

    private void reCreateFilter() {
        curFilter = getFilter(getCurIndex());
        curFilter.init();
        curFilter.onDisplaySizeChanged(width, height);
        curFilter.onInputSizeChanged(width, height);

        mListener.onFilterChange(types[curIndex]);
    }
}
