package com.example.imco.mvp.main;

import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.App;
import com.example.imco.model.R;
import com.example.imco.model.SleepItem;
import com.example.imco.mvp.BaseActivity;
import com.example.imco.weight.AlarmInfoFragment;
import com.example.imco.weight.ListDialog;
import com.example.utils.DateUtils;
import com.example.utils.DialogUtils;
import com.example.utils.FileUtils;
import com.example.utils.LogUtils;
import com.imco.dfu.OtaCallback;
import com.imco.dfu.OtaProxy;
import com.imco.dfu.network.bean.AllFwResultBean;
import com.imco.interactivelayer.bean.AlarmClockBean;
import com.imco.interactivelayer.bean.FunctionsBean;
import com.imco.interactivelayer.manager.CommandManager;
import com.imco.interactivelayer.manager.Flags;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.imco.interactivelayer.manager.CommandManager.NotifyInfo.OTHER_NOTIFY_INFO_QQ;


/**
 * Created by mai on 17-6-21.
 */

public class MainActivity extends BaseActivity<MainPresenter> implements MainContract.MainView, LoaderManager.LoaderCallbacks<Cursor>
        , AlarmInfoFragment.OnSaveListener {
    public final static String TAG = "MainActivity";

    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 连接状态和查找手环　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/

    // 连接状态
    @BindView(R.id.tvContentStatus)
    TextView tvContentStatus;

    @Override
    public void getContentStatus(boolean contentStatus) {
        tvContentStatus.setText("" + contentStatus);
    }

    // 查找手环
    @OnClick(R.id.btn_find_band)
    public void findBand() {
        mPresenter.findBand();
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/




    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 通用消息提醒　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    @BindView(R.id.et_universal_message_info)
    public EditText mETUMInfo;

    @OnClick(R.id.btn_send_universal_message_info)
    public void sendUniversalMessageInfo() {
        String s = mETUMInfo.getText().toString().trim();
        mPresenter.sendUniversalMessageInfo(s.isEmpty() ? getString(R.string.universal_message_notify_info) : s);
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 当前固件支持功能查询　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    @OnClick(R.id.btn_query_functions)
    public void sendDisplayRequest() {
        mPresenter.queryFunctions();
    }

    @BindView(R.id.tv_functions)
    TextView tvFunctions;

    @Override
    public void onFunctions(FunctionsBean functions) {
        showToast("" + functions.isFakeBP());
        tvFunctions.setText("isFakeBP : " + functions.isFakeBP() +
                "\nisHeartRate : " + functions.isHeartRate() +
                "\nisPortrait_landscape:" + functions.isPortrait_landscape() +
                "\nisRealBP : " + functions.isRealBP() +
                "\nisSleep : " + functions.isSleep() +
                "\nisStepCounter : " + functions.isStepCounter());
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 连接和绑定手环的相关功能　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    @OnClick(R.id.btn_un_bond)
    public void onUnBindClick(View view) {
        mPresenter.unBind();
    }

    @OnClick(R.id.btn_connect)
    public void connect() {
        mPresenter.connect();
    }

    @OnClick(R.id.btn_disconnect)
    public void disconnect() {
        mPresenter.disconnect();
    }

    @OnClick(R.id.btn_start_auto_connect)
    public void startAutoConnect() {
        mPresenter.startAutoConnect();
    }

    @OnClick(R.id.btn_stop_auto_connect)
    public void stopAutoConnect() {
        mPresenter.stopAutoConnect();
    }

    @Override
    public void connecting(int statusCode) {
        switch (statusCode) {
            case -1:
                break;
            case CommandManager.LoginState.STATE_WRIST_LOGGING:
                DialogUtils.getInstance().cancelProgressBar();
                DialogUtils.getInstance().showProgressBar(R.string.login);
                break;
            case CommandManager.LoginState.STATE_WRIST_BONDING:
                DialogUtils.getInstance().cancelProgressBar();
                DialogUtils.getInstance().showProgressBar(R.string.bonding);
                break;
            case CommandManager.LoginState.STATE_WRIST_LOGIN:
                DialogUtils.getInstance().cancelProgressBar();
                break;
        }
    }

    @Override
    public void connectError(int errorCode) {
        DialogUtils.getInstance().cancelProgressBar();

        switch (errorCode) {
            case CommandManager.ErrorCode.ERROR_CODE_NO_LOGIN_RESPONSE_COME:
                showToast(R.string.error_code_no_login_response_come);
                break;
            case CommandManager.ErrorCode.ERROR_CODE_BOND_ERROR:
                showToast(R.string.connect_band_already_bond);
                break;
            case CommandManager.ErrorCode.ERROR_CODE_COMMAND_SEND_ERROR:
                showToast(R.string.error_code_command_send_error);
                break;
        }
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 手环名字配置　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    @BindView(R.id.et_device_name)
    EditText etDeviceName;

    @OnClick(R.id.btn_confirm)
    public void confirmDeviceName(View view) {
        if (etDeviceName.getText().toString().isEmpty()) {
            showToast(R.string.value_is_null);
            return;
        }
        mPresenter.setDeviceName(etDeviceName.getText().toString());
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 手环电量查询　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    @OnClick(R.id.btn_read_battery)
    public void readBattery() {
        mPresenter.readBatteryLevel();
    }

    @BindView(R.id.tv_battery)
    TextView tvBattery;

    @Override
    public void showBattery(int battery) {
        tvBattery.setText("" + battery);
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 步数和睡眠数据　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    // 步数
    @BindView(R.id.tv_step)
    TextView tvStepCount;

    @BindView(R.id.tv_calorie)
    TextView tvCalorie;

    @BindView(R.id.tv_distance)
    TextView tvDistance;

    @Override
    public void getCurrentStep(long step) {
        tvStepCount.setText("" + step);
    }

    @Override
    public void getCalorie(long calorie) {
        tvCalorie.setText("" + calorie);
    }

    @Override
    public void getDistance(long distance) {
        tvDistance.setText("" + distance);
    }

    @OnClick(R.id.sendNearlyOffsetStep)
    public void sendNearlyOffsetStep() {
        //　这个是用于发送步数，卡路里，距离到手环端的，默认各是1000，只在特殊情况下使用
        mPresenter.sendSyncTodayNearlyOffsetStepCommand();
    }

    // 睡眠
    @BindView(R.id.tv_sleep)
    TextView tvSleepHistory;

    @OnClick(R.id.btn_start_sync)
    public void onSyncClick(View view) {
        tvSleepHistory.setText("");
        mPresenter.startSync();
    }

    @Override
    public void sleepData(List<SleepItem> sleeps) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < sleeps.size(); i++) {
            buffer.append("Minutes : " + sleeps.get(i).mMinutes);
            buffer.append("  Mode : " + sleeps.get(i).mMode);
            buffer.append("\n");
        }
        tvSleepHistory.setText(buffer.toString());
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 心率测试　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    private boolean mReadHRing = true;

    @BindView(R.id.tv_heart_rate)
    TextView tvHeartRate;

    @OnClick(R.id.btn_heart_rate)
    public void onClick(View view) {
        if (mReadHRing) {
            mReadHRing = false;
            mPresenter.readHeartRate();
        } else {
            mReadHRing = true;
            mPresenter.stopReadHeartRate();
        }
    }

    // 周期性自动测试
    @BindView(R.id.et_measure_cycle)
    EditText etMeasureCycle;

    @OnClick(R.id.btn_cycle_measure)
    public void cycleMeasure(View view) {
        if (etMeasureCycle.getText().toString().isEmpty()) {
            showToast(R.string.value_is_null);
            return;
        }
        mPresenter.cycleMeasureHeartRate(true, Integer.valueOf(etMeasureCycle.getText().toString()));
    }
    @Override
    public void showHeartRate(int interval) {
        tvHeartRate.setText("" + interval);
        mReadHRing = false;
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 固件升级　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    private OtaProxy mOtaProxy = null;
    private String mFilePath;
    private static final int SELECT_FILE_REQ = 1;
    private FragmentManager fm;

    @OnClick(R.id.btn_get_fw_version)
    public void getFWVersion() {
        mPresenter.getFwVersion();
    }

    @OnClick(R.id.btn_select_from_net)
    public void getFWFromNet() {
        mPresenter.checkNewVersion("12312@qq.com", "iMCO");
    }

    @OnClick(R.id.btn_select_file)
    public void btnSelectFile(View view) {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // intent.setType("file/*.bin");
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // file browser has been found on the device
            startActivityForResult(intent, SELECT_FILE_REQ);
        } else {
            showToast(R.string.file_browser_not_found);
        }
    }

    @OnClick(R.id.btn_ota)
    public void clickUpdateFirmware() {
        int batteryLevel = mPresenter.getBatteryLevel();
        if (batteryLevel >= 0
                && batteryLevel < 40) {
            showToast(String.format(getString(R.string.dfu_battery_not_enough), batteryLevel));
            Log.e(TAG, "the battery level is too low. batteryLevel: " + batteryLevel);
            return;
        }
        LogUtils.d(""+mFilePath);
        mPresenter.ota(mOtaProxy, mFilePath);
    }

    @OnClick(R.id.btn_select_multi_file_from_net)
    public void selectMultiFileFromNet() {
        mPresenter.selectMultiFileFromNet("12312@qq.com", "iMCO");
    }


    @BindView(R.id.tv_ota)
    TextView tvOtaVersion;

    @Override
    public void newOtaFileVersion(int newFwVersion) {
        if (newFwVersion == -1) {
            showToast(R.string.dfu_file_status_invalid);
        }
        tvOtaVersion.setText(String.valueOf(newFwVersion));
    }

    OtaCallback cb = new OtaCallback() {
        @Override
        public void onServiceConnectionStateChange(boolean status, OtaProxy d) {
            Log.e(TAG, "onServiceConnectionStateChange status: " + status);
            if (status == true) {
                showToast("DFU Service connected");
                mOtaProxy = d;
            } else {
                showToast("DFU Service disconnected");
                mOtaProxy = null;
            }
        }

        @Override
        public void onError(int e) {
            Log.e(TAG, "onError: " + e);
            showToast(getString(R.string.dfu_status_error_msg, e));
        }

        @Override
        public void onSuccess(int s) {
            Log.e(TAG, "onSuccess: " + s);
            showToast(R.string.dfu_status_completed_msg);
            DialogUtils.getInstance().cancelProgressBar();
//            ControlManager.getInstance().close();
        }

        @Override
        public void onProcessStateChanged(int state) {
            Log.e(TAG, "onProcessStateChanged: " + state);
            showToast("onProcessStateChanged: " + state);
            switch (state) {
                case OtaProxy.ProcessState.STA_ORIGIN:
                    LogUtils.e(TAG, "onProcessStateChanged: STA_ORIGIN " + state);
                    break;
                case OtaProxy.ProcessState.STA_FIND_OTA_REMOTE:
                    LogUtils.e(TAG, "onProcessStateChanged: STA_FIND_OTA_REMOTE " + state);
                    break;

                case OtaProxy.ProcessState.STA_CONNECT_OTA_REMOTE:
                    LogUtils.e(TAG, "onProcessStateChanged: STA_CONNECT_OTA_REMOTE " + state);
                    break;

                case OtaProxy.ProcessState.STA_START_OTA_PROCESS:
                    LogUtils.e(TAG, "onProcessStateChanged: STA_START_OTA_PROCESS " + state);
                    DialogUtils.getInstance().showHProgressBar(" start ota ");
                    break;

                case OtaProxy.ProcessState.STA_OTA_UPGRADE_SUCCESS:
                    LogUtils.e(TAG, "onProcessStateChanged: STA_OTA_UPGRADE_SUCCESS " + state);
                    break;
            }
        }

        @Override
        public void onProgressChanged(int progress) {
            Log.e(TAG, "onProgressChanged: " + progress);
            DialogUtils.getInstance().setProgress(progress);
        }
    };

    @Override
    public void showAllFWVersion(ArrayList<AllFwResultBean.PayloadBean> list) {

        LogUtils.d(TAG, "isDestroyed : " + fm.isDestroyed());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final ListDialog mListDialog = ListDialog.getInstance(MainActivity.this);
                mListDialog.setData(list);
                mListDialog.show(fm, "alarm_fragment");
            }
        });
    }

    @Override
    public void fwItemOnClick() {

    }

    @Override
    public void firmwareVersion(String fv) {
        tvOtaVersion.setText(fv);
    }

    @Override
    public void startDownload() {
        DialogUtils.getInstance().showHProgressBar("Download...");
    }

    @Override
    public void downloadProgress(int value) {
        LogUtils.d(TAG, "Download Progress : " + value);
        DialogUtils.getInstance().setProgress(value);
    }

    @Override
    public void downloadComplete(String fp) {
        mFilePath = fp;
        showToast("Download Completed ! ");
        DialogUtils.getInstance().cancelProgressBar();
    }

    @Override
    public void downError(String errorMessage) {
        showToast(errorMessage);
        DialogUtils.getInstance().cancelProgressBar();
    }

    @Override
    public void getFwVersionTip() {
        showToast(R.string.get_fw_version_tips);
    }

    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 闹钟设置　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    private byte mDayAlarms = Flags.REPETITION_ALL;


    @BindView(R.id.s_alarm)
    Switch sAlarm;

    @BindView(R.id.tv_alarm_time)
    TextView tvAlarmTime;

    @BindView(R.id.tv_alarm_date)
    TextView tvAlarmDate;

    public void setOnCheckedChangeListenerForAlarm() {
        sAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showToast("" + sAlarm.isChecked());
            String timeString[] = tvAlarmTime.getText().toString().split(":");
            int hour = Integer.valueOf(timeString[0]);
            int minute = Integer.valueOf(timeString[1]);
            if (isChecked) {
                mPresenter.settingAlarm(hour, minute, mDayAlarms, 0);
            } else {
                mPresenter.closeAlarm();
            }
        });
    }

    @Override
    public void onAlarmInfoSaved(int position, int hour, int minute, byte dayFlag) {
        String hourStr = String.valueOf(hour).length() == 1
                ? "0" + String.valueOf(hour)
                : String.valueOf(hour);
        String minuteStr = String.valueOf(minute).length() == 1
                ? "0" + String.valueOf(minute)
                : String.valueOf(minute);
        final String timeStr = hourStr + ":" + minuteStr;

        if (position == 0) {
            tvAlarmTime.setText(timeStr);
            mDayAlarms = dayFlag;
            tvAlarmDate.setText(DateUtils.getDayFlagString(mDayAlarms));
            if (sAlarm.isChecked()) {
                mPresenter.settingAlarm(hour, minute, dayFlag, position);
            } else {
                sAlarm.setChecked(true);
            }
        } else if (position == 1) {
            // TODO: 17-6-26 The second alarm
        } // more alarm , Up to 8
    }

    @OnClick(R.id.tv_alarm_time)
    public void settingAlarm(View view) {
        String timeString[] = tvAlarmTime.getText().toString().split(":");
        int hour = Integer.valueOf(timeString[0]);
        int minute = Integer.valueOf(timeString[1]);
        // start le scan, with no filter
        final AlarmInfoFragment dialog = AlarmInfoFragment.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putInt(AlarmInfoFragment.EXTRAS_VALUE_POSITION, 0);
        bundle.putInt(AlarmInfoFragment.EXTRAS_DEFAULT_HOUR, hour);
        bundle.putInt(AlarmInfoFragment.EXTRAS_DEFAULT_MINUTE, minute);
        bundle.putByte(AlarmInfoFragment.EXTRAS_DEFAULT_DAY_FLAG, mDayAlarms);
        dialog.setArguments(bundle);
        LogUtils.d(TAG, "isDestroyed : " + fm.isDestroyed());

        dialog.show(fm, "alarm_fragment");
    }

    @Override
    public void setupAlarm(boolean result) {
        if (result) {
            showToast(R.string.settings_mydevice_setting_alarm_success);
        } else {
            showToast(R.string.settings_mydevice_setting_alarm_failed);
        }
    }

    @Override
    public void alarmList(ArrayList<AlarmClockBean> alarmList) {
        // TODO: 17-6-26 show alarm list in ui
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 用户个人资料　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    @BindView(R.id.et_age)
    EditText etAge;

    @BindView(R.id.et_height)
    EditText etHeight;

    @BindView(R.id.et_weight)
    EditText etWeight;

    @BindView(R.id.btn_profile)
    Button btnProfile;

    @BindView(R.id.rg_gender)
    RadioGroup rgGender;

    @BindView(R.id.rb_man)
    RadioButton rbMan;

    @BindView(R.id.rb_woman)
    RadioButton rbWoman;

    @OnClick(R.id.btn_profile)
    public void saveProfile(View view) {
        if (etAge.getText().toString().isEmpty() || etHeight.getText().toString().isEmpty() || etWeight.getText().toString().isEmpty()) {
            showToast(R.string.value_is_null);
            return;
        }

        mPresenter.saveUserProfile(rbMan.isChecked(), Integer.valueOf(etAge.getText().toString()),
                Integer.valueOf(etHeight.getText().toString()), Integer.valueOf(etWeight.getText().toString()));
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 步数目标　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    @BindView(R.id.et_step_target)
    EditText etStepTarget;

    @OnClick(R.id.btn_push_target)
    public void pushTarget(View view) {
        if (etStepTarget.getText().toString().isEmpty()) {
            showToast(R.string.value_is_null);
            return;
        }
        mPresenter.saveStepTarget(Integer.valueOf(etStepTarget.getText().toString()));
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 久坐提醒　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    @BindView(R.id.et_alarm_cycle)
    EditText etAlarmCycle;
    @BindView(R.id.s_sedentary_reminder)
    Switch sSedentaryReminder;

    public void setOnCheckedChangeListenerForSedentaryReminder() {
        sSedentaryReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (etAlarmCycle.getText().toString().isEmpty()) {
                showToast(R.string.value_is_null);
                return;
            }
            mPresenter.setSedentaryReminder(isChecked, Integer.valueOf(etAlarmCycle.getText().toString()));
        });
    }

    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 抬手亮屏　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    @BindView(R.id.s_turn_over_wrist)
    Switch sTurnOverWrist;

    public void setOnCheckedChangeListenerForTurnOverWrist() {
        sTurnOverWrist.setOnCheckedChangeListener((buttonView, isChecked) -> mPresenter.setTurnOverWrist(isChecked));
    }

    @OnClick(R.id.btn_query_status)
    public void queryTurnOverWristStatus() {
        mPresenter.queryTurnOverWristStatus();
    }

    @Override
    public void turnOverWristStatus(boolean status) {
        sTurnOverWrist.setChecked(status);
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 左右手切换(固件尚未支持)　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/

    @BindView(R.id.s_left_right_hand)
    Switch sLeftRightHand;

    public void setOnCheckedChangeListenerForLeftRightHand() {
        sLeftRightHand.setOnCheckedChangeListener((buttonView, isChecked) -> mPresenter.setLeftOrRightHand((byte) (isChecked ? 1 : 2)));
    }

    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 来电提醒　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    @BindView(R.id.s_enable_call_notify)
    Switch sEnableCallNotify;

    @BindView(R.id.et_call_notify_info)
    EditText etCallNotifyInfo;

    @OnClick(R.id.btn_accept_notify)
    public void acceptNotify() {
        mPresenter.setCallAcceptNotify();
    }

    @OnClick(R.id.btn_reject_notify)
    public void rejectNotify() {
        mPresenter.setCallRejectNotify();
    }

    @OnClick(R.id.btn_send_call_notify)
    public void sendCallNotify() {
        if (etCallNotifyInfo.getText().toString().isEmpty()) {
            showToast(R.string.value_is_null);
            return;
        }
        mPresenter.sendCallNotifyInfo(etCallNotifyInfo.getText().toString());
    }

    public void setOnCheckedChangeListenerForCallNotify() {
        sEnableCallNotify.setOnCheckedChangeListener((buttonView, isChecked) ->
                mPresenter.enableNotifyInfo(isChecked ? Flags.CALL_NOTIFY_MODE_ON : Flags.CALL_NOTIFY_MODE_OFF));
    }

    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ App消息提醒和短信消息提醒　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/

    @BindView(R.id.s_other_info)
    Switch sOtherInfo;

    public void setOnCheckedChangeListenerForOtherInfoNotify() {
        sOtherInfo.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (etOtherInfo.getText().toString().isEmpty()) {
                showToast(R.string.value_is_null);
                return;
            }
            // SPWristbandConfigInfo.setNotifyQQFlag(App.getAppContext(), isChecked);
            mPresenter.enableNotifyInfo((byte) (isChecked ? Flags.CALL_NOTIFY_MODE_ENABLE_QQ : Flags.CALL_NOTIFY_MODE_DISABLE_QQ));
        });
    }

    @BindView(R.id.et_other_info)
    EditText etOtherInfo;

    @OnClick(R.id.btn_other_info)
    public void sendOtherInfo() {
        if (etOtherInfo.getText().toString().isEmpty()) {
            showToast(R.string.value_is_null);
            return;
        }
        mPresenter.sendOtherNotifyInfo(OTHER_NOTIFY_INFO_QQ, etOtherInfo.getText().toString());
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 智能防丢(固件尚未支持)　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    @BindView(R.id.s_ant_lost)
    Switch sAntLost;

    public void setOnCheckedChangeListenerForOtherAntLost() {
        sAntLost.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mPresenter.setAntLost(isChecked);
        });
    }


    @Override
    public void showLinkLossValue(boolean status) {
        showToast("showLinkLossValue : " + status);
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 横竖屏切换　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    @BindView(R.id.s_display_setting)
    public Switch sDisplaySetting;

    public void setOnCheckedChangeListenerForOtherDisplaySetting() {
        sDisplaySetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LogUtils.d("isChecked : " + isChecked);
                mPresenter.setDisplaySwitchSetting(isChecked ? CommandManager.ScreenState.PORTRAIT : CommandManager.ScreenState.LANDSCAPE);
            }
        });
    }

    @Override
    public void displaySwitchStatus(int status) {
        showToast("status : " + status);
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ Activity的生命周期　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        ButterKnife.bind(this);

        fm = getFragmentManager();

        // get the Realsil OtaProxy proxy
        mPresenter.initOtaProxy(this, cb);
        mPresenter.isConnected();
        setOnCheckedChangeListenerForAlarm();
        setOnCheckedChangeListenerForSedentaryReminder();
        setOnCheckedChangeListenerForTurnOverWrist();
        setOnCheckedChangeListenerForLeftRightHand();
        setOnCheckedChangeListenerForCallNotify();
        setOnCheckedChangeListenerForOtherInfoNotify();
        setOnCheckedChangeListenerForOtherAntLost();
        setOnCheckedChangeListenerForOtherDisplaySetting();
        showToast("＞＞＞　　firmware version : " + CommandManager.getFirmwareVersion(App.getAppContext()));
    }
    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.setDataSync(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.setDataSync(false);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.disconnect();
        if (mOtaProxy != null) {
            mOtaProxy.finalize();
        }
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊ 结束　＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /* ＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊选择本地文件相关＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/

    private static final String EXTRA_URI = "uri";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SELECT_FILE_REQ:
                if (resultCode != RESULT_OK)
                    return;

                // clear previous data
                mFilePath = null;

                LogUtils.d(TAG, "getDataColumn : " + data.getData().getPath());

                // and read new one
                final Uri uri = data.getData();
                Log.d(TAG, "request Code : " + uri.getScheme());
                String path = FileUtils.getPathByUri4kitkat(App.getAppContext(), uri);

                mFilePath = path;
                Log.d(TAG, "request file path : " + path);

                // load the file
                mPresenter.loadOtaFileInfo(path);

                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = args.getParcelable(EXTRA_URI);
        String[] projection = new String[]{MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATA};
        return new CursorLoader(this, uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        if (data.moveToNext()) {
            String fileName = data.getString(0 /* DISPLAY_NAME */);
            int fileSize = data.getInt(1 /* SIZE */);
            String filePath = data.getString(2 /* DATA */);
            // load the file
            mFilePath = filePath;
            // load the file
            mPresenter.loadOtaFileInfo(filePath);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        tvOtaVersion.setText(null);
        mFilePath = null;
    }
    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊end＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/





    /*＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊命令发送状态＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/
    @Override
    public void cmdResult(boolean result) {
        showToast("Send command : " + result);
    }

    @Override
    public void cmdError(Throwable e) {
        showToast("Send command failed !");
    }

    @Override
    public void deviceNotConnect() {
        showToast(R.string.device_not_connect);
    }
    /* ＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊end＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊＊*/

}
