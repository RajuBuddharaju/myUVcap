package com.example.uvcap;


import static com.example.uvcap.App.CHANNEL_1_ID;
import static com.example.uvcap.App.CHANNEL_2_ID;
import static com.example.uvcap.App.SPF;
import static com.example.uvcap.App.UVDosage;
import static com.example.uvcap.App.UVIndex;
import static com.example.uvcap.App.indexNotifSent;
import static com.example.uvcap.App.eightyNotified;
import static com.example.uvcap.App.hunderedNotified;
import static com.example.uvcap.App.mytime;
import static com.example.uvcap.App.recUVDosage;
import static com.example.uvcap.App.spfSet;
import static com.example.uvcap.App.spfTimer;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.material.navigation.NavigationView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BluetoothAdapter bluetoothAdapter;
    public BluetoothGatt bluetoothGatt;
    public pGattCallback gattCallback;

    public static final String DEVICE_NAME = "ESP32_BLE";
    public static final String CHARACTERISTIC_UUID = "00002a19-0000-1000-8000-00805f9b34fb";
    public static final String SERVICE_UUID = "0000180f-0000-1000-8000-00805f9b34fb";

    //creating an instance of RCThread class
    RCThread thread;

    //for changing the text view
    TextView textView;

    //progress bar object
    ProgressBar progressBar;

    //Linechart Variables and Objects
    public LineChart linechart;
    static ArrayList<Entry> dataVals = new ArrayList<Entry>();


    //Navigation Menu Variables and Objects
    public static DrawerLayout drawerLayout;
    public static NavigationView navigationView;
    public static Toolbar toolbar;

    private NotificationManagerCompat notificationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gattCallback = new pGattCallback(this);

        notificationManager = NotificationManagerCompat.from(this);


        /*//clears all data values on create
        dataVals.clear();*/
        updateGraph();
        linechart.notifyDataSetChanged();

        // Initialize Bluetooth adapter
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.d("connection", "Bluetooth not enabled");
            //Toast.makeText(this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show();
            finish();
        }


        // Connect to the BLE device
        connectToDevice(this);

        //starting the thread
        thread = new RCThread(this);
        thread.start();
        boolean connected = false;
        //a mini thread that updates graph but could be used to update others based on RCThread
        final Handler handler = new Handler();
        Runnable refresh = new Runnable() {
            @Override
            public void run() {

                if (bluetoothGatt!=null) connectToDevice(thread.mainActivity);

                //updates the graph
                updateGraph();

                //updates UV Index value
                textView = findViewById(R.id.index_title);
                textView.setText("UV Index: "+ UVIndex);

                //updates suggestions
                textView = findViewById(R.id.index_text);
                if (UVIndex <= 2){
                    textView.setText("Low(1-2): No protection needed");
                    indexNotifSent = false;
                } else if(UVIndex <= 5){
                    textView.setText("Moderate(3-5): Some protection required");
                    indexNotifSent = false;
                } else if(UVIndex <= 7){
                    textView.setText("High(6-7): Protection essestial");
                } else if (UVIndex <= 10){
                    textView.setText("Very High(8-10): Extra protection is needed");
                } else if (UVIndex == 11){
                    textView.setText("Extreme(11): Stay inside");
                }

                //sends notification when UV Index is above a certain level
                if(UVIndex >= 8 && !(indexNotifSent)){
                    indexNotifSent = true;
                    uvNotif();
                }

                //updates the percentage of UV Dosage
                textView = findViewById(R.id.dosage_title);
                textView.setText("UV Dosage: "+((int)Math.round((UVDosage)/(recUVDosage)*100))+"%");

                progressBar = findViewById(R.id.dosage_progress);
                progressBar.setProgress((int)Math.round((UVDosage)/(recUVDosage)*100));

                //sends notification when UV Dosage is above a certain level
                if(((int)Math.round((UVDosage)/(recUVDosage)*100))>=80){
                    if(!eightyNotified && ((int)Math.round((UVDosage)/(recUVDosage)*100)) >= 80){
                        eightyNotified = true;
                        dosageNotif();
                    }
                    if(!hunderedNotified && (((int)Math.round((UVDosage)/(recUVDosage)*100)) >= 100)){
                        hunderedNotified = true;
                        dosageNotif();
                    }
                }

                if(spfTimer == 7200 && spfSet){
                    spfTimer = 0;
                    SPF = 1;
                    spfSet = false;
                    Log.d("SPFSPFSPF","SPF Reset after 20 seconds");
                    spfNotify();
                }

                spfTimer++;
                // data request
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(refresh, 1000);



        //idk why I gotta do this but it might not work other wise
        updateGraph();

        //Navigation bar shiz
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        //Toolbar shiz
        setSupportActionBar(toolbar);

        //Navigation Drawer Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigatio_drawer_open, R.string.navigatio_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this); //runs the function that listens to button press on navigation bar

        navigationView.setCheckedItem(R.id.nav_home);

    }

    public void dosageNotif(){
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle("UV Dosage Alert")
                .setContentText("Your daily dosage consumption has reached "+((int)Math.round((UVDosage)/(recUVDosage)*100))+"%")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        notificationManager.notify(1,notification);
    }

    public void uvNotif(){
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle("UV Index Alert")
                .setContentText("Warning High UV Detected. Please protect your skin from the sun.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        notificationManager.notify(2,notification);
    }

    public void spfNotify(){
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle("SPF value alert")
                .setContentText("It has been 2 hours since you last applied your sunscreen. Your SPF value has now been reset to 0. Please do apply sunscreen again.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .build();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        notificationManager.notify(3,notification);
    }

    private void connectToDevice(Context context) {
        BluetoothDevice device = findDevice(DEVICE_NAME);
        if (device != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Permissions are generally troublesome, check custom Gatt class
                return;
            }
            bluetoothGatt = device.connectGatt(this, false, gattCallback);
        } else {
            Log.d("connection", "Ruh roh raggy, device not found :((");
            finish();
        }
    }

    @Nullable
    private BluetoothDevice findDevice(String name) {
        if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 1);
            return null;
        }
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            if (name.equals(device.getName())) {
                return device;
            }
        }
        return null;
    }

    // for when people press back and other shiz
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        updateGraph();
        linechart.notifyDataSetChanged();
    }


    // on restart just clear the data and update graph
    @Override
    protected void onRestart() {
        super.onRestart();
        updateGraph();
        linechart.notifyDataSetChanged();
        connectToDevice(this);
    }

    @Override
    protected void onPause(){
        super.onPause();
        updateGraph();
        linechart.notifyDataSetChanged();
    }

    // on resume just update graph shiz
    @Override
    protected void onResume() {
        super.onResume();
        updateGraph();
        linechart.notifyDataSetChanged();
        connectToDevice(this);
    }
    // all the graph shiz
    public void updateGraph() {
        //creating LineChart object
        Log.d("Updated","blublub");
        linechart = (LineChart) findViewById(R.id.linechart);
        LineDataSet lineDataSet1 = new LineDataSet(dataVals, "Data Set 1");

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        //LineDataSet settings
        lineDataSet1.setHighLightColor(R.color.black);
        lineDataSet1.setColor(R.color.black);
        lineDataSet1.setCubicIntensity(0.2f);   //to enable the cubic density : if 1 then it will be sharp curve
        lineDataSet1.setDrawFilled(true);   //to fill the below of smooth line in graph
        lineDataSet1.setFillColor(R.color.black);
        lineDataSet1.setFillAlpha(80);  //set the transparency of the fill
        lineDataSet1.setMode(LineDataSet.Mode.CUBIC_BEZIER); //makes the thing curvy
        lineDataSet1.setDrawValues(false); //removes point values
        lineDataSet1.setLineWidth(2);
        lineDataSet1.setDrawCircles(false);

        //LineData settings
        LineData data = new LineData(dataSets);
        data.setValueTextColor(getResources().getColor(R.color.white));
        data.setValueTextSize(13f);


        //Description settings - the lil text area the end of the XAxis
        Description description = new Description();
        description.setText("Hours");
        description.setTextSize(15f);
        description.setTextColor(getResources().getColor(R.color.white));
        linechart.setDescription(description);
        description.setEnabled(false);

        //XAxis settings
        XAxis xAxis = linechart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setTextColor(getResources().getColor(R.color.black));
        xAxis.setAxisLineColor(getResources().getColor(R.color.black));
        xAxis.setAxisLineWidth(3f);
        xAxis.setDrawGridLines(false);

        //YAxis settings
        YAxis leftAxis = linechart.getAxisLeft();
        leftAxis.setAxisMaximum(11f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setTextColor(getResources().getColor(R.color.black));
        leftAxis.setAxisLineColor(getResources().getColor(R.color.black));
        leftAxis.setAxisLineWidth(3f);
        //leftAxis.setDrawGridLines(false);

        //setting the numbers displayed on the YAxis
        int[] numArr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        leftAxis.setLabelCount(numArr.length);

        //disabling the right YAxis
        linechart.getAxisRight().setEnabled(false);

        //disabling zoom on graph
        linechart.setPinchZoom(false);
        linechart.setDoubleTapToZoomEnabled(false);

        //disabling the legend
        linechart.getLegend().setEnabled(false);

        linechart.setData(data);
        linechart.invalidate(); //idk what this do
    }

    public static LocalDateTime localStartTime;
    // used to add data to the graph
    public static void addData(float x, float y) {
        dataVals.add(new Entry(x, y));
        Log.d("DataSize",""+dataVals.size());
    }

    public static void addData(float y) {
        LocalDateTime currentTime;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
             currentTime = LocalDateTime.now();
            if(localStartTime==null||currentTime.isBefore(localStartTime)){
                localStartTime = currentTime;
            }
        } else {
            return;
        }
        Duration duration = Duration.between(localStartTime,currentTime);
        float x = duration.getSeconds();
        addData(x,y);
    }

    // for the nav bar and switching between pages
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
        } else if (itemId == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}