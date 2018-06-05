package com.development.blackhole.ledcontroller;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class DeviceListActivity extends Activity {
	private ListView mListView;
	private DeviceListAdapter mAdapter;
	private ArrayList<BluetoothDevice> mDeviceList;
	public static String MAC_ADDRESS;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_paired_devices);

		
		mDeviceList		= getIntent().getExtras().getParcelableArrayList("device.list");
		
		mListView		= findViewById(R.id.lv_paired);
		
		mAdapter		= new DeviceListAdapter(this);
		
		mAdapter.setData(mDeviceList);
		mAdapter.setListener(new DeviceListAdapter.OnPairButtonClickListener() {			
			@Override
			public void onPairButtonClick(int position) {
				BluetoothDevice device = mDeviceList.get(position);
				String macAddress = device.getAddress();

				Intent returnMac = new Intent();
				returnMac.putExtra(MAC_ADDRESS, macAddress);

				setResult(RESULT_OK, returnMac);
				finish();

			}
		});
		
		mListView.setAdapter(mAdapter);
		
		registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(mPairReceiver);
		
		super.onDestroy();
	}
    
    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        
	        if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
	        	 final int state 		= intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
	        	 final int prevState	= intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
	        	 
	        	 mAdapter.notifyDataSetChanged();
	        }
	    }
	};
}