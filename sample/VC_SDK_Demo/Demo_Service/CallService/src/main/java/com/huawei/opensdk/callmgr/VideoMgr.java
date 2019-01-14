package com.huawei.opensdk.callmgr;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.Handler;
import android.view.OrientationEventListener;
import android.view.SurfaceView;

import com.huawei.opensdk.commonservice.common.LocContext;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.sdkwrapper.manager.TupMgr;
import com.huawei.videoengine.ViERenderer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import common.TupCallParam;
import common.VideoWndType;
import object.TupDevice;
import object.VideoWndInfo;
import tupsdk.TupCall;
import tupsdk.TupCallManager;

public class VideoMgr {
    private static final String TAG = VideoMgr.class.getSimpleName();
    private static VideoMgr instance;
    private Context context;
    private Handler handler;

    private TupCallManager callManager;

    List<TupDevice> cameraList;

    private int currentCameraIndex = CallConstant.FRONT_CAMERA;
    private int currentCallId;


    /**
     * 本地隐藏窗口（只能创建一个）
     */
    private SurfaceView localHideView;
    /**
     * 本地窗口（只能创建一个）
     */
    private SurfaceView localVideoView;
    /**
     * 远端窗口（可以创建多个）
     */
    private SurfaceView remoteVideoView;
    /**
     * 辅流窗口（只能创建一个，创建方法和远端窗口一致）
     */
    private SurfaceView auxDataView;

    private OrientationDetector orientationDetector;

    private static final String BMP_FILE = "CameraBlack.BMP";

    public static final int LAYOUT_PORTRAIT = 1;

    public static final int LAYOUT_LANDSCAPE = 2;

    public VideoMgr() {
        context = LocContext.getContext();
        if (context == null) {
            throw new NullPointerException("BaseApp not initialized.");
        }

        handler = new Handler(context.getMainLooper());

        callManager = TupMgr.getInstance().getCallManagerIns();

        cameraList = callManager.tupGetDevices(TupCallParam.CALL_E_DEVICE_TYPE.CALL_E_CALL_DEVICE_VIDEO);
    }

    public static VideoMgr getInstance() {
        if (instance == null) {
            instance= new VideoMgr();
        }
        return instance;
    }

    /**
     * 创建视频Renderer
     * Create video renderer
     */
    private void createVideoRenderer()
    {
        // 创建本地视频窗口（本地窗口只能创建一个，底层可以直接获取到这个窗口）
        // 必须存在，否则远端视频无法显示
        localHideView = ViERenderer.createLocalRenderer(context);
        localHideView.setZOrderOnTop(false);

        // 本端小窗口显示
        localVideoView = ViERenderer.createRenderer(context,true);
        localVideoView.setZOrderMediaOverlay(true);

        // 创建远端视频窗口（可以创建多个）
        remoteVideoView = ViERenderer.createRenderer(context, true);
        remoteVideoView.setZOrderMediaOverlay(false);
    }



    public int switchCamera(int callId, int cameraIndex)
    {
        return setVideoOrient(callId, cameraIndex);
    }

    public int openCamera(Session session) {
        return controlLocalCameraMode1(session, true);
    }

    public int closeCamera(Session session) {
        return controlLocalCameraMode1(session, false);
    }

    private int controlLocalCameraMode1(Session session, boolean isOpen) {
        int result;

        if (isOpen) {
            //重新设置摄像头采集角度
            TupCall tupCall = session.getTupCall();
            result = tupCall.setCaptureRotation(CallConstant.FRONT_CAMERA, 3);
            if (result != 0) {
                LogUtil.e(TAG, "setCaptureRotation is failed, result -->" + result);
            }
            else {
                setCurrentCameraIndex(CallConstant.FRONT_CAMERA);
            }
        } else {
            //采用发送默认图版本方式，替代关闭摄相头动作
            String picturePath = Environment.getExternalStorageDirectory() + File.separator + BMP_FILE;
            result = callManager.setVideoCaptureFile(session.getCallID(), picturePath);
            if (result != 0) {
                LogUtil.e(TAG, "setVideoCaptureFile is failed, result -->" + result);
            }
            setCurrentCameraIndex(CallConstant.CAMERA_NON);
        }
        return result;
    }

    private int controlLocalCameraMode2(Session session, boolean isOpen) {
        int result;

        /**
         * operation, value :open 0x01，close 0x02，start 0x04，stop 0x08, value can be linked by "|"
         * 操作，取值: open 0x01，close 0x02，start 0x04，stop 0x08，可以使用逻辑运算符"|"连接，open|start，close|stop
         */
        int operation;

        /**
         * module,value:0x01 display remote window,0x02 display local window,0x04 video,0x08 coder,0x10 decoder
         * 模式，取值: 0x01显示远端窗口 0x02显示本端窗口 0x04摄相头 0x08编码器  0x10解码器
         */
        int module;

        int callId = session.getCallID();

        if (isOpen) {
            module = 0x02 | 0x04;
            operation = 0x04;

            result = callManager.vedioControl(callId, operation, module);
            if (result != 0) {
                LogUtil.e(TAG, "vedioControl is failed, result --> " + result);
                return result;
            }
        } else {
            module = 0x02 | 0x04;
            operation = 0x08;

            result = callManager.vedioControl(callId, operation, module);
            if (result != 0) {
                LogUtil.e(TAG, "vedioControl is failed, result --> " + result);
                return result;
            }

            result = setVideoOrient(callId, CallConstant.FRONT_CAMERA);
            if (result != 0) {
                LogUtil.e(TAG, "setVideoOrient is failed, result --> " + result);
                return result;
            }
        }

        return 0;
    }

    public int setVideoOrient(int callId, int cameraIndex)
    {
        int orient;
        int portrait;
        int landscape;
        int seascape;

        Configuration configuration = LocContext.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            orient = 1;
        } else {
            orient = 2;
        }

        if (cameraIndex == CallConstant.FRONT_CAMERA) {
            portrait = 3;
            landscape = 0;
            seascape = 2;
        } else if (cameraIndex == CallConstant.BACK_CAMERA) {
            portrait = 1;
            landscape = 0;
            seascape = 2;
        } else {
            return -1;
        }

        /**
         * 横竖屏信息stOrient 设置标志位
         * @param int callId    0表示全局设置,不为0表示 会话中设置
         * @param int index     摄像头index
         * @param int orient    视频横竖屏情况 1：竖屏；2：横屏；3：反向横屏
         * @param int portrait  竖屏视频捕获（逆时针旋转）角度 0：0度；1：90度；2：180度；3：270度；
         * @param int landscape 横屏视频捕获（逆时针旋转）角度 0：0度；1：90度；2：180度；3：270度；
         * @param int seascape 反向横屏视频捕获（逆时针旋转）角度 0：0度；1：90度；2：180度；3：270度；
         * @return int result  视频角度
         */
        int result = callManager.setMboileVideoOrient(callId, cameraIndex, orient, portrait, landscape, seascape);
        if (result != 0) {
            LogUtil.e(TAG, "set video orient is failed. result --> " + result);
        } else {
            setCurrentCameraIndex(cameraIndex);
        }

        if (orientationDetector != null)
        {
            orientationDetector.updateRotation(true);
        }

        return result;
    }

    private boolean setVideoWindow(int callId, VideoWndType videoType, int videoIndex, int displayType)
    {
        int result;
        if (callId == 0)
        {
            result = callManager.createVideoWindow(videoType.getIndex(), videoIndex, displayType);
        }
        else {

            VideoWndInfo vInfo = new VideoWndInfo();
            vInfo.setUlRender(videoIndex);
            vInfo.setUlDisplayType(displayType);
            vInfo.setVideowndType(videoType);
            result = callManager.updateVideoWindow(vInfo, callId);
        }

        return (result == 0);
    }

    public void initVideoWindow(final int callId)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {

                createVideoRenderer();

                setCurrentCallId(callId);

                //设置视频窗口方向参数
                setVideoOrient(callId, CallConstant.FRONT_CAMERA);

                // 设置本地视频窗口与呼叫绑定
                // (0:拉伸模式 1:(不拉伸)黑边模式 2:(不拉伸)裁剪模式)
                int displayType = 2;
                int localVideoIndex = ViERenderer.getIndexOfSurface(localVideoView);
                setVideoWindow(callId, VideoWndType.local, localVideoIndex, displayType);

                // 设置远端视频窗口与呼叫绑定
                // (0:拉伸模式 1:(不拉伸)黑边模式 2:(不拉伸)裁剪模式)
                displayType = 1;
                int remoteVideoIndex = ViERenderer.getIndexOfSurface(remoteVideoView);
                setVideoWindow(callId, VideoWndType.remote, remoteVideoIndex, displayType);

//                onCreateHideView();
            }
        });
    }

    /**
     * Clear data
     */
    public void clearCallVideo()
    {
        LogUtil.i(TAG, "clearCallVideo() enter");

        handler.post(new Runnable()
        {
            @Override
            public void run()
            {

                ViERenderer.freeLocalRenderResource();
                if (localVideoView != null)
                {
                    ViERenderer.setSurfaceNull(localVideoView);
                    localVideoView = null;
                }

                if (remoteVideoView != null)
                {
                    ViERenderer.setSurfaceNull(remoteVideoView);
                    remoteVideoView = null;
                }

                if (auxDataView != null)
                {
                    ViERenderer.setSurfaceNull(auxDataView);
                    auxDataView = null;
                }

                if (localHideView != null)
                {
                    localHideView = null;
                }
            }
        });
    }


    /**
     * Gets local hide view.
     *
     * @return the local hide view
     */
    public SurfaceView getLocalHideView() {
        return localHideView;
    }

    /**
     * Gets local call view.
     *
     * @return the local call view
     */
    public SurfaceView getLocalVideoView() {
        return localVideoView;
    }

    /**
     * Gets remote call view.
     *
     * @return the remote call view
     */
    public SurfaceView getRemoteVideoView() {
        return remoteVideoView;
    }


    public int getCurrentCameraIndex() {
        return currentCameraIndex;
    }

    public void setCurrentCameraIndex(int currentCameraIndex) {
        this.currentCameraIndex = currentCameraIndex;
    }

    public int getCurrentCallId() {
        return currentCallId;
    }

    public void setCurrentCallId(int currentCallId) {
        this.currentCallId = currentCallId;
    }

    /**
     * is support video calls
     * @return the boolean
     */
    public boolean isSupportVideo()
    {
        if (cameraList != null) {
            if (cameraList.size() > 0)
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Set the video automatic rotation
     * 设置视频自动旋转
     * @param object 调用者对象
     * @param isOpen 是否开启方向调整
     */
    public void setAutoRotation(Object object, boolean isOpen, int layoutDirect) {
        if (orientationDetector == null) {
            orientationDetector = new OrientationDetector();
        }

        orientationDetector.setLayoutDirect(layoutDirect);

        orientationDetector.autoOrientationAdjust(object, isOpen);
    }

    /**
     * 视频角度探测类
     * 用于设备在旋转时，调整摄相头采集方向，以及视频窗口显示方向
     * 1.根据角度划4象限:竖向上【0-45、315-360】；横向右【45-135】；竖向下【135-225】；横向左【225-315】
     * 2.根据返回的页面布局方向分三种场景：横向布局、竖向布局、横向布局翻转
     * 3.根据摄像头情况分两种场景：前置摄像头、后置摄像头
     * 4.根据视频窗口情况分两种场景：远端窗口（对方看到自己的图像）、本端窗口（本端右下角小视频窗口）
     */
    private class OrientationDetector {
        /**
         * 未知角度，无象限所属
         */
        private static final int ORIENTATION_UNKNOWN = -1;

        /**
         * 竖屏象限（摄像头在上）
         */
        private static final int ORIENTATION_PORTRAIT_UP = 0;

        /**
         * 横屏象限（摄像头在左）
         */
        private static final int ORIENTATION_LANDSCAPE_LEFT = 1;

        /**
         * 横屏象限（摄像头在右）
         */
        private static final int ORIENTATION_LANDSCAPE_RIGHT = 2;

        /**
         * 竖屏象限（摄像头在下）
         */
        private static final int ORIENTATION_PORTRAIT_DOWN = 3;

        /**
         * 竖向布局
         */
        //private static final int LAYOUT_PORTRAIT = 1;

        //private static final int LAYOUT_LANDSCAPE = 2;

        /**
         * 布局方向，默认为竖向布局
         */
        private int layoutDirect = LAYOUT_PORTRAIT;

        /**
         * 摄相头采集旋转方向
         */
        private int cameraCaptureRotation;

        /**
         * 视频窗口显示旋转方向
         */
        private int windowsDisplayRotation;

        /**
         * 记录上一次的旋转方向
         * 默认unknown
         */
        private int lastOrientation = ORIENTATION_UNKNOWN;

        /**
         * 当前角度
         */
        private int curOriginalOrientation;

        /**
         * 设备摄像头旋转角度监听
         */
        private OrientationEventListener orientationEventListener;

        /**
         * 监听列表
         */
        private final List<Object> orientationEventListenerList = new ArrayList<>();

        /**
         * 构造方法
         */
        public OrientationDetector() {
            // 创建监听
            //createOrientationListener();
        }

        /**
         * 设置布局的方向
         *
         * @param layoutDirect 布局方向值
         */
        public void setLayoutDirect(int layoutDirect) {
            this.layoutDirect = layoutDirect;
        }

        /**
         * 设置视频自动方向调整
         *
         * @param object 调用者对象
         * @param isOpen 是否开启方向调整
         */
        public void autoOrientationAdjust(Object object, boolean isOpen) {
            if (isOpen) {
                if (orientationEventListenerList.size() == 0) {
                    // 创建监听
                    createOrientationListener();
                }
                // 添加调用者到监听列表
                if (!orientationEventListenerList.contains(object)) {
                    orientationEventListenerList.add(object);
                }

                updateRotation(true);
            } else {
                this.lastOrientation = ORIENTATION_UNKNOWN;

                // 去注册监听，移除摄像头旋转角度监听
                orientationEventListenerList.remove(object);
                if (orientationEventListenerList.size() == 0) {
                    destroyOrientationListener();
                }
            }
        }


        /**
         * 更新摄相头采集方向和视频窗口显示方向
         * 为解决平放手机无法监听设备角度问题，添加参数isForce，不管是否平放，先强制主动旋转一次
         *
         * @param isForce 是否是强制更新
         */
        public void updateRotation(boolean isForce) {
            int deviceOrientation = getOrientation(curOriginalOrientation);

            // 强制设置旋转，或与上一次不一样的区间，则进行更新设置旋转角度
            if (isForce || deviceOrientation != lastOrientation) {
                // 更新旋转角度, 包括摄相头和显示窗口
                updateRotation(deviceOrientation);

                // 根据旋转角度，调用TUP接口旋转视频方向
                setRotation(cameraCaptureRotation, windowsDisplayRotation);
            }
        }

        /**
         * 创建并启动设备旋转监听
         */
        private void createOrientationListener() {
            // 启一个新的监听，监听设备旋转角度
            orientationEventListener = new OrientationEventListener(LocContext.getContext()) {
                @Override
                public void onOrientationChanged(int orientation) {
                    curOriginalOrientation = orientation;

                    // 旋转处理，更新摄相头采集角度和视频窗口显示角度
                    if (!orientationEventListenerList.isEmpty()) {
                        updateRotation(false);
                    }
                }
            };

            // 启动监听
            orientationEventListener.enable();
        }

        /**
         * 停止并销毁设备旋转监听
         */
        private void destroyOrientationListener() {
            if (orientationEventListener != null) {
                orientationEventListener.disable();
            }
            orientationEventListener = null;
        }

        /**
         * 根据捕捉到的摄像头方向划分四个象限，不属于四象限则返回unknown不做处理
         * 四个象限分别是：竖向上【0-45、315-360】；横向右【45-135】；竖向下【135-225】；横向左【225-315】
         *
         * @param orientation 捕捉到的设备摄像头角度
         * @return 角度所属象限
         */
        private int getOrientation(int orientation) {
            if ((orientation < 45 && orientation >= 0) || (orientation >= 315 && orientation <= 360)) {

                return ORIENTATION_PORTRAIT_UP;
            } else if (orientation >= 45 && orientation < 135) {
                return ORIENTATION_LANDSCAPE_RIGHT;
            } else if (orientation >= 135 && orientation < 225) {
                return ORIENTATION_PORTRAIT_DOWN;
            } else if (orientation >= 225 && orientation < 315) {
                return ORIENTATION_LANDSCAPE_LEFT;
            } else {
                return ORIENTATION_UNKNOWN;
            }
        }

        /**
         * 根据不同的象限区间设置旋转角度
         *
         * @param deviceOrientation 象限区间
         */
        private void updateRotation(int deviceOrientation) {
            switch (deviceOrientation) {
                case ORIENTATION_LANDSCAPE_LEFT:
                    setOrientationLandscapeLeft();
                    break;
                case ORIENTATION_LANDSCAPE_RIGHT:
                    setOrientationLandscapeRight();
                    break;
                case ORIENTATION_PORTRAIT_UP:
                    setOrientationPortraitUp();
                    break;
                case ORIENTATION_PORTRAIT_DOWN:
                    setOrientationPortraitDown();
                    break;
                default:
                    break;
            }

            // 记录上一次的象限区间
            lastOrientation = deviceOrientation;
        }

        /**
         * 设备横向，摄像头在左时的旋转角度设置
         */
        private void setOrientationLandscapeLeft() {
            if (isLayoutPortrait()) {
                // 前置后置摄像头旋转角度一致
                cameraCaptureRotation = 0;
                windowsDisplayRotation = 1;
            } else {
                // 前置后置摄像头旋转角度一致
                cameraCaptureRotation = 0;
                windowsDisplayRotation = 0;
            }
        }

        /**
         * 设备横向，摄像头在右时的旋转角度设置
         */
        private void setOrientationLandscapeRight() {
            if (isLayoutPortrait()) {
                // 前置后置摄像头旋转角度一致
                cameraCaptureRotation = 2;
                windowsDisplayRotation = 3;
            } else {
                // 前置后置摄像头旋转角度一致
                cameraCaptureRotation = 2;
                windowsDisplayRotation = 2;
            }
        }

        /**
         * 设备竖向，摄像头在上时的旋转角度设置
         */
        private void setOrientationPortraitUp() {
            if (isLayoutPortrait()) {
                if (VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.FRONT_CAMERA) {
                    cameraCaptureRotation = 3;
                    windowsDisplayRotation = 0;
                } else {
                    cameraCaptureRotation = 1;
                    windowsDisplayRotation = 0;
                }
            } else {
                if (VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.FRONT_CAMERA) {
                    cameraCaptureRotation = 3;
                    windowsDisplayRotation = 3;
                } else {
                    cameraCaptureRotation = 1;
                    windowsDisplayRotation = 3;
                }
            }
        }

        /**
         * 设备竖向，摄像头在下时的旋转角度设置
         */
        private void setOrientationPortraitDown() {
            if (isLayoutPortrait()) {
                if (VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.FRONT_CAMERA) {
                    cameraCaptureRotation = 1;
                    windowsDisplayRotation = 2;
                } else {
                    cameraCaptureRotation = 3;
                    windowsDisplayRotation = 2;
                }
            } else {
                if (VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.FRONT_CAMERA) {
                    cameraCaptureRotation = 1;
                    windowsDisplayRotation = 1;
                } else {
                    cameraCaptureRotation = 3;
                    windowsDisplayRotation = 1;
                }
            }
        }

        /**
         * 判断是否是竖向布局
         *
         * @return true：是；false：否
         */
        private boolean isLayoutPortrait() {
            return layoutDirect == LAYOUT_PORTRAIT;
        }

        /**
         * 设置旋转角度
         *
         * @param cameraCaptureRotation  摄像头采集方向
         * @param windowsDisplayRotation 窗口显示方向
         */
        private void setRotation(int cameraCaptureRotation, int windowsDisplayRotation) {

            int currentCallId = VideoMgr.getInstance().getCurrentCallId();
            Session session = CallMgr.getInstance().getCallSessionByCallID(currentCallId);

            if (session != null) {
                if (VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.FRONT_CAMERA) {
                    // 1表示前置摄像头
                    session.setCaptureRotation(1, cameraCaptureRotation);
                } else if (VideoMgr.getInstance().getCurrentCameraIndex() == CallConstant.BACK_CAMERA) {
                    // 0表示后置摄像头
                    session.setCaptureRotation(0, cameraCaptureRotation);
                } else
                {
                    // -1表示摄相头关闭
                    // do nothing
                }

                session.setDisplayRotation(VideoWndType.local, windowsDisplayRotation);
                session.setDisplayRotation(VideoWndType.remote, windowsDisplayRotation);
            }
        }
    }

}
