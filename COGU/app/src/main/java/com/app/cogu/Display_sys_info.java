package com.app.cogu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Display_sys_info extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_sys_info);


        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String Platform = intent.getStringExtra("Platform");
        String arch = intent.getStringExtra("arch");
        String mac = intent.getStringExtra("mac");
        String ram = intent.getStringExtra("ram");
        String name = intent.getStringExtra("name");
        String CPU = intent.getStringExtra("CPU");
        String ramUsage = intent.getStringExtra("ramUsage");
        String ip = intent.getStringExtra("ip");
        String PID = intent.getStringExtra("PID");
        String ssid = intent.getStringExtra("SSID");
        String battery = intent.getStringExtra("BAT");
        String state = intent.getStringExtra("batstate");




        // Lookup the recyclerview in activity layout
        RecyclerView rvSysInfo = (RecyclerView) findViewById(R.id.rvInformation);

        // Initialize contacts
        List<sysInformations> full_info = new ArrayList<>(  );
        full_info.add( new sysInformations( "PLATFORM", Platform ) );
        full_info.add( new sysInformations( "ARCHITECTURE", arch ) );
        full_info.add( new sysInformations( "MAC-ADDRESS", mac ) );
        full_info.add( new sysInformations( "RAM", ram ) );
        full_info.add( new sysInformations( "SYSTEM NAME", name ) );
        full_info.add( new sysInformations( "CPU USAGE", CPU ) );
        full_info.add( new sysInformations( "RAM USAGE", ramUsage ) );
        full_info.add( new sysInformations( "IP-ADDRESS", ip ) );
        full_info.add( new sysInformations( "PID_COGU", PID ) );
        full_info.add( new sysInformations( "SSID", ssid ) );
        full_info.add( new sysInformations( "BAT PERCENT", battery ) );
        full_info.add( new sysInformations( "CHARGING STATUS", state ) );
        
        // Create adapter passing in the sample user data
        sysInfoAdapter adapter = new sysInfoAdapter( full_info );
        // Attach the adapter to the recyclerview to populate items
        rvSysInfo.setAdapter(adapter);
        // Set layout manager to position the items
        rvSysInfo.setLayoutManager(new LinearLayoutManager(this));



//        LinearLayout l1 = (LinearLayout) findViewById( R.id.testing );
//        ImageView img1 = new ImageView( getApplicationContext() );
//        img1.setImageResource( R.drawable.abhyam1 );
//        ImageView img2 = new ImageView( getApplicationContext() );
//        img2.setImageResource( R.drawable.abhyam2 );
//        ImageView img3 = new ImageView( getApplicationContext() );
//        img3.setImageResource( R.drawable.abhyam3 );
//        l1.addView( img1 );
//        l1.addView( img2 );
//        l1.addView( img3 );


        // Capture the layout's TextView and set the string as its text
//        TextView textView = findViewById(R.id.textView);

//        textView.setText("Platform : " + Platform +
//                "\n" +
//                "\nArchitecture : " + arch +
//                "\n" +
//                "\nRAM : " + ram +
//                "\n" +
//                "\nName of System : " + name +
//                "\n" +
//                "\nCPU Usage : " + CPU +
//                "\n" +
//                "\nRAM Usage : " + ramUsage +
//                "\n" +
//                "\nMAC Address : " + mac +
//                "\n" +
//                "\nIP Address : " + ip +
//                "\n" +
//                "\nPID of the running COGU Proccess : " + PID +
//                "\n" +
//                "\nSSID of connected wifi : " + ssid +
//                "\n" +
//                "\nBATTERY PERCENTAGE : " + battery +
//                "\n" +
//                "\nBattery State : " + state +
//                "\n");
//        textView.setText(value);
//        textView.setText(" Platform : " + Platform + "\n");
//
//        textView.setText("Architecture : " + arch + "\n");
//
//        textView.setText("RAM : " + ram.toString() + "\n" );
//
//        textView.setText("Name of System : " + name.toString() + "\n" );
//
//        textView.setText("CPU Usage : " + CPU.toString() + "\n" );
//
//        textView.setText("RAM Usage : " + ramUsage.toString() + "\n" );
//
//        textView.setText("MAC Address : " + mac.toString() + "\n" );
//
//        textView.setText("IP Address : " + ip.toString()  + "\n");
//
//        textView.setText("PID of the running COGU Proccess : " + PID.toString() + "\n" );
//
//        textView.setText("SSID of connected wifi : " + ssid.toString() + "\n" );
//
//        textView.setText("BATTERY PERCENTAGE : " + battery.toString() + "\n");
//
//        textView.setText("Battery State : " + state.toString() + "\n" );
//        textView.setText(value);

//        ImageView img = findViewById( R.id.testimage );
//
//        img.setImageResource( R.drawable.abhyam1 );
//        img.setImageResource( R.drawable.abhyam2 );
//        img.setImageResource( R.drawable.abhyam3 );

    }
}