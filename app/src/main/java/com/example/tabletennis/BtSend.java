package com.example.tabletennis;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class BtSend {

    private OutputStreamWriter outWrite;
    private BufferedReader inWriter;
    private static Set<BluetoothDevice> bondedDevices;
    private static List<String> devicesNameList = new ArrayList<>();
    private static List<String> macAddressList = new ArrayList<>();

    static List<String> pairedDevicesName(){
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if(blueAdapter!=null && blueAdapter.isEnabled()) {
            bondedDevices = blueAdapter.getBondedDevices();
            for (BluetoothDevice bt : bondedDevices) {
                devicesNameList.add(bt.getName());
            }
        }

        return  devicesNameList;
    }

    static List<String> pairedDevicesMac(){
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if(blueAdapter!=null && blueAdapter.isEnabled()) {
            bondedDevices = blueAdapter.getBondedDevices();
            for (BluetoothDevice bt : bondedDevices) {
                macAddressList.add(bt.getAddress());
            }
        }

        return  macAddressList;
    }

    void init(String macAddress) {
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                bondedDevices = blueAdapter.getBondedDevices();

                if(bondedDevices.size() > 0) {


                    BluetoothDevice device = null;

                    for (BluetoothDevice d : bondedDevices) {
                        if (d.getAddress().equals(macAddress))  //50:76:AF:DC:F3:BA -> my PC; A4:CF:12:44:EA:5E -> mx ESP
                            device = d;
                    }
                    try {
                        ParcelUuid[] uuids = device.getUuids();
                        BluetoothSocket socket =
                                device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                        socket.connect();
                        OutputStream outputStream = socket.getOutputStream();
                        InputStream inStream = socket.getInputStream();
                        inWriter = new BufferedReader(new InputStreamReader(inStream));
                        outWrite = (new OutputStreamWriter(outputStream));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                Log.e("error", "No appropriate paired devices.");
            } else {
                Log.e("error", "Bluetooth is disabled.");
            }
        }
    }
    void write(String msg)  {
        try {
            outWrite.write(msg);
            outWrite.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void run(MainActivity di, OnReceiveInterface ori) {
        Thread t = new Thread(() -> {
            while (true) {
                try {

                    if (inWriter != null) {
                        String a = inWriter.readLine();
                        ori.Action(a, di);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    public interface OnReceiveInterface {
        void Action(String s, MainActivity di);
    }

}


