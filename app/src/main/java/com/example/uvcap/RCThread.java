package com.example.uvcap;

import android.util.Log;
import java.lang.Thread;
import java.util.Random;

public class RCThread extends Thread {


    public MainActivity mainActivity;

    public RCThread (MainActivity mainActivityobj){
        super();
        mainActivity = mainActivityobj;
    }

    public void run(){
        readValues();
    }
    public void readValues() {
        // Simulate the data if you want
        /*Random rand = new Random();
        float UV_Index;
        float time = 0;
        for (int i = 0; i < 10; i++){
            try {
                UV_Index = (float)((int)Math.floor(Math.random() * (11 - 1 + 1) + 1));
                mainActivity.UVIndex = UV_Index;
                mainActivity.addData(time, UV_Index);
                time += 1;
                Thread.sleep(1000);
            } catch(Exception ignored) {Log.e("Thread Error",ignored.getMessage());}
        }*/
        int i = 0;
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try{
                /*mainActivity.gattCallback.onServicesDiscovered(mainActivity.bluetoothGatt, 345);*/
                mainActivity.gattCallback.read(mainActivity.bluetoothGatt);
            } catch (NullPointerException e) {
                Log.d("blublub",e.getMessage());
            }
            i++;
        }
    }
}