package com.example.imco.mvp.scan;

import android.bluetooth.BluetoothDevice;

import com.example.imco.model.BleDevicesBean;
import com.example.imco.mvp.BasePresenter;
import com.example.imco.mvp.BaseView;

/**
 * Created by mai on 17-6-23.
 */

public interface ScanContract {

    interface ScanView extends BaseView {
        void scanDevices(BluetoothDevice device, int rssi);
        void startConnect();
        void connecting(int statusCode);
        void connectError(int errorCode);
//        void connectedStatus();
    }

    abstract class ScanPresenter extends BasePresenter<ScanView> {
        abstract void startScan();
        abstract void stopScan();
        abstract void connectDevice(BleDevicesBean message);

        abstract void registerCallback();
        abstract void unregisterCallback();
    }
}
