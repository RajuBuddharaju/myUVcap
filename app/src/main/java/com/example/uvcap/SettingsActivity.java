package com.example.uvcap;


import static com.example.uvcap.App.recUVDosage;
import static com.example.uvcap.App.SPF;
import static com.example.uvcap.App.spfSet;
import static com.example.uvcap.App.spfTimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;


public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Navigation Menu Variables and Objects
    public static DrawerLayout drawerLayout;
    public static NavigationView navigationView;
    public static Toolbar toolbar; //don't know why we need it like this?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Navigation bar shiz
        drawerLayout = findViewById(R.id.drawer_layout2);
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

        // shows that settings is selected by deafault on the nav bar
        navigationView.setCheckedItem(R.id.nav_settings);

        // function for button press
        changeTextViewOnButtonClick();

    }



    // to do stuff when you press the skin tone buttons
    private void changeTextViewOnButtonClick() {
        TextView changingText = (TextView) findViewById(R.id.changingView);
        Button skinI = (Button) findViewById(R.id.type1);
        Button skinII = (Button) findViewById(R.id.type2);
        Button skinIII = (Button) findViewById(R.id.type3);
        Button skinIV = (Button) findViewById(R.id.type4);
        Button skinV = (Button) findViewById(R.id.type5);
        Button skinVI = (Button) findViewById(R.id.type6);

        TextView changingText2 = (TextView) findViewById(R.id.changingView2);
        Button spf1 = (Button) findViewById(R.id.spf1);
        Button spf2 = (Button) findViewById(R.id.spf2);
        Button spf3 = (Button) findViewById(R.id.spf3);
        Button spf4 = (Button) findViewById(R.id.spf4);
        Button spf5 = (Button) findViewById(R.id.spf5);
        Button spf6 = (Button) findViewById(R.id.spf6);

        // pass value to the ESP32 after the OnClickListener in each thing
        // we also change the value of the displayed text here
        skinI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingText.setText(R.string.skin_I);
                recUVDosage = 50;
            }
        });
        skinII.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingText.setText(R.string.skin_II);
                recUVDosage = 62.5;
            }
        });
        skinIII.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingText.setText(R.string.skin_III);
                recUVDosage = 75;
            }
        });
        skinIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingText.setText(R.string.skin_IV);
                recUVDosage = 112.5;
            }
        });
        skinV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingText.setText(R.string.skin_V);
                recUVDosage = 150;
            }
        });
        skinVI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingText.setText(R.string.skin_VI);
                recUVDosage = 250;
            }
        });

        // pass value to the ESP32 after the OnClickListener in each thing
        // we also change the value of the displayed text here
        spf1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingText2.setText(R.string.spf_I);
                spfSet = true;
                spfTimer = 0;
                SPF = 0.067;
            }
        });
        spf2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingText2.setText(R.string.spf_II);
                spfSet = true;
                spfTimer = 0;
                SPF = 0.033;
            }
        });
        spf3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingText2.setText(R.string.spf_III);
                spfSet = true;
                spfTimer = 0;
                SPF = 0.025;
            }
        });
        spf4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingText2.setText(R.string.spf_IV);
                spfSet = true;
                spfTimer = 0;
                SPF = 0.022;
            }
        });
        spf5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingText2.setText(R.string.spf_V);
                spfSet = true;
                spfTimer = 0;
                SPF = 0.02;
            }
        });
        spf6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changingText2.setText(R.string.spf_VI);
                spfSet = true;
                spfTimer = 0;
                SPF = 0.017;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView changingText = (TextView) findViewById(R.id.changingView);
        if(recUVDosage == 50){
            changingText.setText(R.string.skin_I);
        } else if (recUVDosage == 62.5){
            changingText.setText(R.string.skin_II);
        } else if (recUVDosage == 75){
            changingText.setText(R.string.skin_III);
        } else if (recUVDosage == 112.5){
            changingText.setText(R.string.skin_IV);
        } else if (recUVDosage == 150){
            changingText.setText(R.string.skin_V);
        } else if (recUVDosage == 250){
            changingText.setText(R.string.skin_VI);
        }

        TextView changingText2 = (TextView) findViewById(R.id.changingView2);
        if(SPF == 0.067){
            changingText2.setText(R.string.spf_I);
        } else if (SPF == 0.033){
            changingText2.setText(R.string.spf_II);
        } else if (SPF == 0.025){
            changingText2.setText(R.string.spf_III);
        } else if (SPF == 0.022){
            changingText2.setText(R.string.spf_IV);
        } else if (SPF == 0.02){
            changingText2.setText(R.string.spf_V);
        } else if (SPF == 0.017){
            changingText2.setText(R.string.spf_VI);
        } else if (SPF == 1){
            changingText2.setText(R.string.default_spf);
        }
    }

    // going back shiz
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // nav bar shiz
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_settings) {
        } else if (itemId == R.id.nav_home) {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}