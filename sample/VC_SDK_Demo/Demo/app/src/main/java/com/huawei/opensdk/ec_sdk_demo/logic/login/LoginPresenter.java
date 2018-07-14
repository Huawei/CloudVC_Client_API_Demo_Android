package com.huawei.opensdk.ec_sdk_demo.logic.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;

import com.huawei.opensdk.commonservice.util.DeviceManager;
import com.huawei.opensdk.commonservice.util.LogUtil;
import com.huawei.opensdk.ec_sdk_demo.R;
import com.huawei.opensdk.ec_sdk_demo.common.UIConstants;
import com.huawei.opensdk.ec_sdk_demo.ui.base.MVPBasePresenter;
import com.huawei.opensdk.ec_sdk_demo.util.FileUtil;
import com.huawei.opensdk.loginmgr.LoginConstant;
import com.huawei.opensdk.loginmgr.LoginMgr;
import com.huawei.opensdk.loginmgr.LoginParam;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;


/**
 * This class is about login logic
 */
public class LoginPresenter extends MVPBasePresenter<ILoginContract.LoginBaseView>
        implements ILoginContract.LoginBaserPresenter
{
    private static final String RINGING_FILE = "ringing.wav";
    private static final String RING_BACK_FILE = "ring_back.wav";
    private static final String BMP_FILE = "CameraBlack.BMP";
    private static final String ANNOT_FILE = "annotImages";
    private final Context mContext;
    private SharedPreferences sharedPreferences;
    private LoginModel mLoginModel;

    public LoginPresenter(Context context)
    {
        this.mContext = context;
        sharedPreferences = mContext.getSharedPreferences(LoginConstant.FILE_NAME, Activity.MODE_PRIVATE);
        mLoginModel = new LoginModel(sharedPreferences);
    }

    @Override
    public void onLoginParams()
    {
        String account = mLoginModel.getAccount();
        String password = mLoginModel.getPassword();
        getView().setEditText(account, password);
    }

    @Override
    public void doLogin(String userName, String password)
    {
        if (!DeviceManager.isNetworkAvailable(mContext))
        {
            LogUtil.e(UIConstants.DEMO_TAG, "network has been disconnected");
            getView().dismissLoginDialog();
            getView().showToast(R.string.network_be_disconnected);
            return;
        }

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password))
        {
            LogUtil.e(UIConstants.DEMO_TAG, mContext.getString(R.string.account_information_not_empty));
            getView().dismissLoginDialog();
            getView().showToast(R.string.account_information_not_empty);
            return;
        }



        String regServerAddress = mLoginModel.getRegServer();
        String serverPort = mLoginModel.getPort();

        if (TextUtils.isEmpty(regServerAddress))
        {
            getView().showToast(R.string.regServer_not_null);
            getView().dismissLoginDialog();
            return;
        }

        if (TextUtils.isEmpty(serverPort))
        {
            getView().showToast(R.string.ServerPort_not_null);
            getView().dismissLoginDialog();
            return;
        }

        if (null == Looper.myLooper())
        {
            Looper.prepare();
        }

        LoginParam loginParam = new LoginParam();

        loginParam.setServerPort(Integer.parseInt(serverPort));
        loginParam.setProxyPort(Integer.parseInt(serverPort));
        loginParam.setServerUrl(regServerAddress);
        loginParam.setProxyUrl(regServerAddress);
        loginParam.setUserName(userName);
        loginParam.setPassword(password);

        loginParam.setVPN(sharedPreferences.getBoolean(LoginConstant.TUP_VPN, false));

        loginParam.setSrtpMode(mLoginModel.getSrtpMode());
        loginParam.setSipTransportMode(mLoginModel.getSipTransport());
        loginParam.setServerType(mLoginModel.getLoginType());

        LoginMgr.getInstance().login(loginParam);

        mLoginModel.saveLoginParams(userName, password);
        importFile();
    }

    /**
     * import file.
     */
    private void importFile()
    {
        LogUtil.i(UIConstants.DEMO_TAG, "import media file!~");
        Executors.newFixedThreadPool(LoginConstant.FIXED_NUMBER).execute(new Runnable()
        {
            @Override
            public void run()
            {
                importMediaFile();
                importBmpFile();
                importAnnotFile();
            }
        });
    }

    private void importBmpFile()
    {
        if (FileUtil.isSdCardExist())
        {
            try
            {
                String bmpPath = Environment.getExternalStorageDirectory() + File.separator + BMP_FILE;
                InputStream bmpInputStream = mContext.getAssets().open(BMP_FILE);
                FileUtil.copyFile(bmpInputStream, bmpPath);
            }
            catch (IOException e)
            {
                LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            }
        }
    }

    private void importAnnotFile()
    {
        if (FileUtil.isSdCardExist())
        {
            try
            {
                String bmpPath = Environment.getExternalStorageDirectory() + File.separator + ANNOT_FILE;
                File file = new File(bmpPath);
                if (!file.exists())
                {
                    file.mkdir();
                }

                String[] bmpNames = new String[]{"check.bmp", "xcheck.bmp", "lpointer.bmp",
                        "rpointer.bmp", "upointer.bmp", "dpointer.bmp", "lp.bmp"};
                String[] paths = new String[bmpNames.length];

                for (int list = 0; list < paths.length; ++list)
                {
                    paths[list] = bmpPath + File.separator + bmpNames[list];
                    InputStream bmpInputStream = mContext.getAssets().open(bmpNames[list]);
                    FileUtil.copyFile(bmpInputStream, paths[list]);
                }

            }
            catch (IOException e)
            {
                LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            }
        }

    }

    private void importMediaFile()
    {
        if (FileUtil.isSdCardExist())
        {
            try
            {
                String mediaPath = Environment.getExternalStorageDirectory() + File.separator + RINGING_FILE;
                InputStream mediaInputStream = mContext.getAssets().open(RINGING_FILE);
                FileUtil.copyFile(mediaInputStream, mediaPath);

                String ringBackPath = Environment.getExternalStorageDirectory() + File.separator + RING_BACK_FILE;
                InputStream ringBackInputStream = mContext.getAssets().open(RING_BACK_FILE);
                FileUtil.copyFile(ringBackInputStream, ringBackPath);
            }
            catch (IOException e)
            {
                LogUtil.e(UIConstants.DEMO_TAG, e.getMessage());
            }
        }
    }


    @Override
    public void initServerData()
    {
        mLoginModel.initServerData();
    }
}
