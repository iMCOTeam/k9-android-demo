package com.example.imco.model;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

public class BleDevicesBean implements Comparable<BleDevicesBean>{

	private BluetoothDevice device;
	private Integer rssi;

	public BleDevicesBean() {
	}

	public BleDevicesBean(BluetoothDevice device, int rssi) {
		setDevice(device);
		setRssi(rssi);
	}

	public Integer getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public BluetoothDevice getDevice() {
		return device;
	}

	public void setDevice(BluetoothDevice device) {
		this.device = device;
	}

	@Override
	public int compareTo(@NonNull BleDevicesBean o) {
		return o.getRssi().compareTo(this.getRssi());
	}

}
