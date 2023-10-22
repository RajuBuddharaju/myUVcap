package com.example.uvcap;

import static com.example.uvcap.App.SPF;
import static com.example.uvcap.App.UVDosage;
import static com.example.uvcap.App.UVIndex;
import static com.example.uvcap.App.mytime;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.UUID;

public class pGattCallback extends BluetoothGattCallback {
    Context context;
    BluetoothGattService service;


    public pGattCallback(Context context) {
        super();
        this.context = context;
    }

    // TODO: This could break everything
    // Permission are generally troublesome, only the findDevice function
    // in the MainActivity actually caused an error

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        switch (newState) {
            case BluetoothGatt.STATE_CONNECTED:
                if (ActivityCompat.checkSelfPermission(this.context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                gatt.discoverServices();
                break;
            case BluetoothGatt.STATE_DISCONNECTED:
                gatt.close();
                break;
        }
    }

    // Honestly not sure what this is...
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        service = gatt.getService(UUID.fromString(MainActivity.SERVICE_UUID));
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(MainActivity.CHARACTERISTIC_UUID));
        if (ActivityCompat.checkSelfPermission(this.context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gatt.readCharacteristic(characteristic);
    }

    // Data receive function
    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] value, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            final String data = new String(characteristic.getValue());
            //formatting data received
            float Index = Float.parseFloat(data.substring(data.indexOf('B') + 1));
            double Intensity = Double.parseDouble(data.substring(data.indexOf('A') + 1, data.indexOf('B')));

            MainActivity.addData(mytime,Index);
            UVIndex = Index;
            UVDosage += Intensity * SPF;
            Log.d("data", data);
            mytime++;
        }
    }

    /*@Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        if (characteristic.getUuid().equals(MainActivity.CHARACTERISTIC_UUID)) {
            final String data = new String(characteristic.getValue());
            //formatting data received
            float Index = Float.parseFloat(data.substring(data.indexOf('B') + 1));
            double Intensity = Double.parseDouble(data.substring(data.indexOf('A') + 1, data.indexOf('B')));

            MainActivity.addData(mytime,Index);
            UVIndex = Index;
            UVDosage += Intensity * SPF;
            mytime++;
        }
    }*/

    public void read(BluetoothGatt gatt) {
        BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString(MainActivity.CHARACTERISTIC_UUID));
        if (ActivityCompat.checkSelfPermission(this.context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gatt.readCharacteristic(characteristic);
    }
}
