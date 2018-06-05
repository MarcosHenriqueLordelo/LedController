package com.development.blackhole.ledcontroller;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnBluetooth;
    private Switch swLed1, swLed2, swLed3;
    private TextView lblLed1State, lblLed2State, lblLed3State;

    private String MAC_ADDRESS;
    private static final int REQUEST_CONNECTION_BT = 2;
    private static final int MESSAGE_READ = 3;

    private Boolean connected = false;

    private BluetoothAdapter bluetoothAdapter = null;
    private BluetoothDevice bluetoothDevice = null;
    private BluetoothSocket bluetoothSocket = null;

    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();

    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    ConnectedThread connectedThread;

    Handler mHandler;
    StringBuilder bluetoothData = new StringBuilder();

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnBluetooth = findViewById(R.id.btnBluetooth);
        swLed1 = findViewById(R.id.swLed1);
        swLed2 = findViewById(R.id.swLed2);
        swLed3 = findViewById(R.id.swLed3);
        lblLed1State = findViewById(R.id.lblLed1State);
        lblLed2State = findViewById(R.id.lblLed2State);
        lblLed3State = findViewById(R.id.lblLed3State);

        swLed1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (connected) {
                    if (isChecked) {
                        lblLed1State.setText("On");
                        connectedThread.sendData("led1");
                    } else {
                        lblLed1State.setText("Off");
                        connectedThread.sendData("led1");
                    }
                }else{
                    showToast("bluetooth não conectado");
                    swLed1.setChecked(false);
                }
            }
        });

        swLed2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (connected) {
                    if (isChecked) {
                        lblLed2State.setText("On");
                        connectedThread.sendData("led2");
                    } else {
                        lblLed2State.setText("Off");
                        connectedThread.sendData("led2");
                    }
                }else{
                    showToast("bluetooth não conectado");
                    swLed2.setChecked(false);
                }
            }
        });

        swLed3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (connected) {
                    if (isChecked) {
                        lblLed3State.setText("On");
                        connectedThread.sendData("led3");
                    } else {
                        lblLed3State.setText("Off");
                        connectedThread.sendData("led3");
                    }
                }else{
                    showToast("bluetooth não conectado");
                    swLed3.setChecked(false);
                }
            }
        });

        btnBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter.isEnabled()) {
                    if (!connected) {
                        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                        Set<BluetoothDevice> unpairedDevices = new HashSet<BluetoothDevice>(mDeviceList);
                        unpairedDevices.removeAll(pairedDevices);

                        if ((unpairedDevices == null || unpairedDevices.size() == 0) && (pairedDevices == null || pairedDevices.size() == 0)) {
                            showToast("No Bluetooth Devices Found");
                        } else {
                            ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();

                            list.addAll(pairedDevices);

                            Intent intent = new Intent(MainActivity.this, DeviceListActivity.class);

                            intent.putParcelableArrayListExtra("device.list", list);

                            startActivityForResult(intent, REQUEST_CONNECTION_BT);
                        }
                    }else{
                        try {
                            bluetoothSocket.close();
                            connected = false;
                        } catch (IOException e) {

                        }
                    }
                }
            }
        });

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_READ){
                    String recived = (String)msg.obj;
                    bluetoothData.append(recived);

                    int informationEnds = bluetoothData.indexOf("}");
                    if (informationEnds > 0){
                        String allData = bluetoothData.substring(0,informationEnds);
                        int dataSize = allData.length();
                        if (bluetoothData.charAt(0) == '{'){
                            String finalData = bluetoothData.substring(1,dataSize);
                            showToast(finalData);
                            if (finalData.contains("l1on")){
                                swLed1.setChecked(true);
                                lblLed1State.setText("On");
                            }else{
                                swLed1.setChecked(false);
                                lblLed1State.setText("Off");
                            }
                            if (finalData.contains("l2on")){
                                swLed2.setChecked(true);
                                lblLed2State.setText("On");
                            }else{
                                swLed2.setChecked(false);
                                lblLed2State.setText("Off");
                            }
                            if (finalData.contains("l3on")){
                                swLed3.setChecked(true);
                                lblLed3State.setText("On");
                            }else{
                                swLed3.setChecked(false);
                                lblLed3State.setText("Off");
                            }
                        }
                        bluetoothData.delete(0,bluetoothData.length());
                    }

                }
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode){
            case REQUEST_CONNECTION_BT:
                if (resultCode == RESULT_OK){
                    MAC_ADDRESS = data.getExtras().getString(DeviceListActivity.MAC_ADDRESS);
                    bluetoothDevice = bluetoothAdapter.getRemoteDevice(MAC_ADDRESS);
                    try {
                        bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                        bluetoothSocket.connect();
                        connectedThread = new ConnectedThread(bluetoothSocket);
                        connectedThread.start();
                        showToast("Conectado a: " + MAC_ADDRESS);
                        connected = true;
                    }catch (IOException erro){
                        showToast("Erro ao Conectar-se");
                        connected = false;
                    }
                }
                break;
        }

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);

                    String btData = new String(buffer,0, bytes);

                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, btData)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void sendData(String data) {
            byte [] msgBuffer = data.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) { }
        }

    }
}
