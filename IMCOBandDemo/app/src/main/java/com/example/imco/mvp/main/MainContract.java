package com.example.imco.mvp.main;

import android.content.Context;

import com.example.imco.model.SleepItem;
import com.example.imco.mvp.BasePresenter;
import com.example.imco.mvp.BaseView;
import com.imco.dfu.OtaCallback;
import com.imco.dfu.OtaProxy;
import com.imco.dfu.network.bean.AllFwResultBean;
import com.imco.interactivelayer.bean.AlarmClockBean;
import com.imco.interactivelayer.bean.FunctionsBean;
import com.imco.interactivelayer.manager.CommandManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mai on 17-6-22.
 */

public interface MainContract {
    interface MainView extends BaseView {
        void getContentStatus(boolean contentStatus);

        void getCurrentStep(long step);

        void getCalorie(long calorie);

        void getDistance(long distance);

        void showHeartRate(int interval);

        void newOtaFileVersion(int newFwVersion);

        void setupAlarm(boolean result);

        void alarmList(ArrayList<AlarmClockBean> alarmList);

        void cmdResult(boolean result);

        void cmdError(Throwable e);

        void deviceNotConnect();

        void firmwareVersion(String fv);

        void startDownload();

        void downloadProgress(int value);

        void downloadComplete(String fp);

        void downError(String errorMessage);

        void getFwVersionTip();

        void connecting(int statusCode);

        void connectError(int errorCode);

        void turnOverWristStatus(boolean status);

        void sleepData(List<SleepItem> sleeps);

        void showAllFWVersion(ArrayList<AllFwResultBean.PayloadBean> list);

        void fwItemOnClick();
        void  showLinkLossValue(boolean status);
        void displaySwitchStatus(@CommandManager.ScreenState int status);
        void onFunctions(FunctionsBean functions);
        void showBattery(int battery);
    }

    abstract class MainPresenter extends BasePresenter<MainView> {
        abstract void startSync();

        abstract void disconnect();

        abstract void connect();

        abstract void isConnected();

        abstract void unBind();

        abstract void startAutoConnect();

        abstract void stopAutoConnect();

        // heart rate
        abstract void readHeartRate();

        abstract void stopReadHeartRate();

        abstract void cycleMeasureHeartRate(boolean enable, int interval);

        // ota // TODO: 17-6-26 upload ota file from ftp when ftp service create
        abstract void initOtaProxy(Context context, OtaCallback listener);

        abstract boolean ota(OtaProxy dfu, String firmwarePath);

        abstract void readBatteryLevel();

        abstract int getBatteryLevel();

        abstract void checkNewVersion(String vendor, String deviceType);

        abstract void loadOtaFileInfo(String path);

        abstract void getFwVersion();

        abstract void selectMultiFileFromNet(String userId, String vendor);
        // alarm
        abstract void settingAlarm(int hour, int minute, byte daysFlag, int alarmId);

        abstract void closeAlarm();

        abstract void syncAlarmList();

        abstract void saveUserProfile(boolean gender, int age, int height, int weight);

        abstract void saveStepTarget(int stepTarget);

        abstract void findBand();

        abstract void setSedentaryReminder(boolean enable, int alarmCycle);

        abstract void setTurnOverWrist(boolean enable);

        //
        abstract void setLeftOrRightHand(byte mode);

        abstract void enableNotifyInfo(byte mode);

        abstract void sendCallNotifyInfo(String notifyInfo);

        abstract void setCallAcceptNotify();

        abstract void setCallRejectNotify();

        abstract void sendOtherNotifyInfo(byte mode, String notifyInfo);

        abstract void sendUniversalMessageInfo(String notifyInfo);

        abstract void setDeviceName(String deviceName);

        abstract void setDataSync(boolean enable);

        abstract void getSleepData();

        abstract void queryTurnOverWristStatus();

        abstract void sendSyncTodayNearlyOffsetStepCommand();

        abstract void setAntLost(boolean ant_lost);

        abstract void setDisplaySwitchSetting(@CommandManager.ScreenState int status);
        abstract void setDisplaySwitchRequest();

        abstract void queryFunctions();
    }
}
