package com.app.cogu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricPrompt;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNLogVerbosity;
import com.pubnub.api.enums.PNReconnectionPolicy;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.objects_api.channel.PNChannelMetadataResult;
import com.pubnub.api.models.consumer.objects_api.membership.PNMembershipResult;
import com.pubnub.api.models.consumer.objects_api.uuid.PNUUIDMetadataResult;
import com.pubnub.api.models.consumer.presence.PNHereNowChannelData;
import com.pubnub.api.models.consumer.presence.PNHereNowOccupantData;
import com.pubnub.api.models.consumer.presence.PNHereNowResult;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import com.pubnub.api.models.consumer.pubsub.PNSignalResult;
import com.pubnub.api.models.consumer.pubsub.files.PNFileEventResult;
import com.pubnub.api.models.consumer.pubsub.message_actions.PNMessageActionResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//import com.pubnub.api.models.consumer.pubsub.objects.PNMembershipResult;
//import com.pubnub.api.models.consumer.pubsub.objects.PNSpaceResult;
//import com.pubnub.api.models.consumer.pubsub.objects.PNUserResult;




public class MainActivity extends AppCompatActivity
{
    private static final String TAG = MainActivity.class.getName();
    private AutoCompleteTextView entryUpdateText;
    private TextView messagesText;

    private PubNub pubnub;
    private String theChannel;
    private String devide_name ;
    private ImageView connection_status;


    String app_uuid;
    String pubkey;
    String subkey;

    // contains the list of iot devices:-
    private List<String> iot_devices = new ArrayList<>(  );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // adding toolbar:
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String channel_name = intent.getStringExtra( "channel_name" );
        String system_name  = intent.getStringExtra( "deviceName" );
        String pubkey_local = intent.getStringExtra( "pubkey" );
        String subkey_local = intent.getStringExtra( "subkey" );
        String uuid_local   = intent.getStringExtra( "uuid" );
        theChannel = channel_name;
        devide_name   = system_name;
        pubkey     = pubkey_local;
        subkey     = subkey_local;
        app_uuid   = uuid_local;

        Log.d(TAG, "user subscribed to channel successfully");
        // to hide action bar    // this is to set custom tool baar for this activity only:-
//        Objects.requireNonNull( getSupportActionBar() ).hide();

        //Create a thread pool with a single thread//
        Executor newExecutor = Executors.newSingleThreadExecutor();

        final MainActivity activity = this;

        // To set the command options for autofill

        entryUpdateText = findViewById( R.id.entry_update_text );
        entryUpdateText.setDropDownBackgroundResource( R.color.texting );
        // Get the string array
        String[] commands = getResources().getStringArray(R.array.commands);
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, commands);
        entryUpdateText.setAdapter(adapter);



        //Start listening for authentication events//

        final BiometricPrompt myBiometricPrompt = new BiometricPrompt(activity, newExecutor, new BiometricPrompt.AuthenticationCallback() {
            @Override

        //onAuthenticationError is called when a fatal error occurrs//

            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                } else {

        //Print a message to Logcat//

                    Log.d(TAG, "An unrecoverable error occurred");
                }
            }

        //onAuthenticationSucceeded is called when a fingerprint is matched successfully//

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

        //Print a message to Logcat//

                Log.d(TAG, "Fingerprint recognised successfully");
                activity.runOnUiThread( new Runnable() {
                    @Override
                    public void run() {
//                        entryUpdateText  = findViewById(R.id.entry_update_text);
                        submitUpdate(devide_name, entryUpdateText.getText().toString());
                        entryUpdateText.setText("");
                    }
                } );

            }
        //onAuthenticationFailed is called when the fingerprint doesn’t match//

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();

        //Print a message to Logcat//

                Log.d(TAG, "Fingerprint not recognised");
            }
        });

        //Create the BiometricPrompt instance//

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()

        //Add some text to the dialog//

                .setTitle("                      <COGU>")
                .setSubtitle( "  " )
                .setDescription("Verify with your registered Fingerprint to send current command...")
                .setNegativeButtonText("Cancel")

        //Build the dialog//

                .build();

        // Assign an onClickListener to the app’s “Authentication” button//
        entryUpdateText.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if( i == EditorInfo.IME_ACTION_SEND){
                    myBiometricPrompt.authenticate( promptInfo );
                }
                return true;
            }
        } );
        Button submitCommand = findViewById( R.id.btn_submit_message );
        submitCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBiometricPrompt.authenticate(promptInfo);


            }
        });

        // Initialisation:-
        PNConfiguration pnConfiguration = new PNConfiguration();
        // replace the key placeholders with your own PubNub publish and subscribe keys
        pnConfiguration.setPublishKey(pubkey);
        pnConfiguration.setSubscribeKey(subkey);
        pnConfiguration.setUuid(app_uuid);
        // to set the reconnection trigger when connection is lost to network.
        pnConfiguration.setReconnectionPolicy( PNReconnectionPolicy.LINEAR );
        // Enable Debugging
        pnConfiguration.setLogVerbosity( PNLogVerbosity.BODY);


        pubnub = new PubNub(pnConfiguration);

        // to check the current user is logged in or not when app is opened:-
        connection_status = (ImageView) findViewById( R.id.connection_status );
        connection_status.setImageResource( R.drawable.ic_offline_status );
        pubnub.hereNow()
                .channels(Arrays.asList(theChannel))
                .includeState(true)
                .async(new PNCallback<PNHereNowResult>() {
                    @Override
                    public void onResponse(PNHereNowResult result, PNStatus status) {
                        if (status.isError()) {
                            // handle error
                            Log.e(TAG, "Could not able to get users online now. You are dpenedent on precence listener, Restart your app to relaod"  );
                            return;
                        }

                        for (PNHereNowChannelData channelData : result.getChannels().values()) {

//                            System.out.println("---");
//                            System.out.println("channel:" + channelData.getChannelName());
//                            System.out.println("occupancy: " + channelData.getOccupancy());
//                            System.out.println("occupants:");

                            for (PNHereNowOccupantData occupant : channelData.getOccupants()) {
//                                displayMessage( "UUID", occupant.getUuid() );
//                                displayMessage( "State", String.valueOf( occupant.getState() ) );
                                if (!occupant.getUuid().equals( devide_name )){
                                    connection_status.setImageResource( R.drawable.ic_offline_status );
                                    Log.d(TAG, "System is offline" );
                                }else{
                                    connection_status.setImageResource( R.drawable.ic_online_status );
                                    Log.d(TAG, "System is online" );
                                }
//                                JsonObject jsonObject = occupant.getState().getAsJsonObject();
//                                String s = occupant.getState().getAsString();
//                                Log.d(TAG, "jason element testing" );
//                                System.out.println("uuid: " + occupant.getUuid()
//                                        + " state: " + occupant.getState());
                            }
                        }
                    }
                });


        pubnub.addListener(new SubscribeCallback() {

            @Override
            public void message(PubNub pubnub, PNMessageResult event) {
//                Log.d( "something", String.valueOf( event.getMessage() ) );
                JsonObject message = event.getMessage().getAsJsonObject();
                try {
//                  String entryVal = message.get("entry").getAsString();
//                  String identify = message.get("identifier").getAsString();
                    String updateVal1 = message.get( "comm" ).getAsString();

                    if (updateVal1.equals( "sysInfoPub" )) {
                        String Platform = message.get( "Platform" ).getAsString();
                        String arch = message.get( "arch" ).getAsString();
                        String mac = message.get( "mac-addr" ).getAsString();
                        String ram = message.get( "ram" ).getAsString();
                        String name = message.get( "name" ).getAsString();
                        String CPU = message.get( "CPU" ).getAsString();
                        String ramUsage = message.get( "RamUsage" ).getAsString();
                        String ip = message.get( "IP_addr" ).getAsString();
                        String pid = message.get( "pid" ).getAsString();
                        String ssid = message.get( "ssid" ).getAsString();
                        String battery = message.get( "bat_level" ).getAsString();
                        String batState = message.get( "state" ).getAsString();
                        displayMessage( "INFORMATION RECIEVED:",
                                "------------------------------------------------" );
                        sysViewer( Platform, arch, mac, ram, name, CPU, ramUsage, ip, pid, ssid, battery, batState );
                    } else if (updateVal1.equals( "culpritPhoto" )) {
                        String url = message.get( "url" ).getAsString();
                        String stamp = message.get( "name" ).getAsString();
                        photoActivity( url, stamp );

                    } else if (updateVal1.equals( "culprit" )) {
                        displayMessage( "CAPTURING IMAGE THROUGH WEB-CAM",
                                "===================================" );

                    } else if (updateVal1.equals( "requestSysActivity" )) {
                        displayMessage( "REQUESTING SYSTEM DETAILS",
                                "===========================" );
                    } else if (updateVal1.equals( "command_status" )) {
                        String msg = message.get( "message" ).getAsString();
                        displayMessage( "EXECUTED =>",
                                msg );

                    } else {
                        displayMessage( "Sent in channel",
                                                        updateVal1 );
                    }
                }catch (Exception e) {
                    try {
                        String updateVal1 = message.get( "iot" ).getAsString();
                        if (updateVal1.equals( "echo_reply" )){
                            String device_name = message.get( "iot_name" ).getAsString();
                            setIot_devices( device_name );
                        }else if (updateVal1.equals( "device_on" )){
                            String device_name = message.get( "iot_name" ).getAsString();
                            displayMessage( device_name, "ON" );
                        }else if (updateVal1.equals( "device_off" )){
                            String device_name = message.get( "iot_name" ).getAsString();
                            displayMessage( device_name, "OFF" );
                        }


                    }catch (Exception e2){
                        Log.e( TAG, String.format( "eeror occured while decoding json message for iot : %s", e2 ) );
                    }
                }
            }

            @Override
            public void status(PubNub pubnub, PNStatus event) {


                if (event.getCategory().equals( PNStatusCategory.PNUnexpectedDisconnectCategory )) {
                    // internet got lost, do some magic and call reconnect when ready
                    displayMessage( "UNEXPECTEDLY DISCONNECTED, Stop VPN if Running",
                                                    "Trying to Reconnect....");
                    pubnub.reconnect();

                }else if (event.getCategory().equals( PNStatusCategory.PNTimeoutCategory )) {
                    // do some magic and call reconnect when ready
                    displayMessage( "Connection is TIMEDOUT ",
                                                "Trying to reconnect...." );
                    pubnub.reconnect();

                }else if (event.getCategory().equals( PNStatusCategory.PNReconnectedCategory )){
                    displayMessage( "Connection is Restabilished..!!",
                                        "" );
                }else if (event.getCategory().equals(PNStatusCategory.PNConnectedCategory)){
                    displayMessage( "CONNECTED TO CHANNEL SUCCESSFULLY",
                            " ");
                }else if (event.getCategory().equals(PNStatusCategory.PNUnknownCategory)){
                    displayMessage( "UNKNOWN ERROR other than 200 Reponse",
                                            "See the log file to track");
                    Log.e( TAG, event.toString() );
                }else {
                    displayMessage("[STATUS: " + event.getCategory() + "]",
                            "connected to channels: " + event.getAffectedChannels());
                    Log.e(TAG, event.toString());
                }



            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult event) {
                displayMessage("[PRESENCE: " + event.getEvent() + ']',
                        "uuid: " + event.getUuid() + ", channel: " + event.getChannel());
                if(event.getUuid().equals( devide_name) && (event.getEvent().equals( "leave" ) || event.getEvent().equals( "timeout" ))){
                    connection_status.setImageResource( R.drawable.ic_offline_status );
                    Log.d(TAG, "System is offline" + " Pubnub event log" );
                }else if (event.getUuid().equals( devide_name) && event.getEvent().equals( "join" )){
                    connection_status.setImageResource( R.drawable.ic_online_status );
                    Log.d(TAG, "System is online and server is just joined" + " Pubnub event log" );
                }

            }
            // even if you don't need these handler, you still have include them
            // because we are extending an Abstract class
            @Override
            public void signal(PubNub pubnub, PNSignalResult event) { }

            @Override
            public void uuid(PubNub pubnub, PNUUIDMetadataResult pnUUIDMetadataResult) {

            }

            @Override
            public void channel(PubNub pubnub, PNChannelMetadataResult pnChannelMetadataResult) {

            }

//            @Override
//            public void user(PubNub pubnub, PNUserResult event) { }
//
//            @Override
//            public void space(PubNub pubnub, PNSpaceResult event) { }

            @Override
            public void membership(PubNub pubnub, PNMembershipResult event) { }

            @Override
            public void messageAction(PubNub pubnub, PNMessageActionResult event) { }

            @Override
            public void file(PubNub pubnub, PNFileEventResult pnFileEventResult) { }
        });


        pubnub.subscribe().channels(Arrays.asList(theChannel)).withPresence().execute();


        messagesText = findViewById(R.id.messages_text);
    }


    public void setIot_devices(String device_name){
        iot_devices.add( device_name );
    }



    protected void submitUpdate(String anEntry, String anUpdate) {
        JsonObject entryUpdate = new JsonObject();
//        entryUpdate.addProperty("entry", anEntry);
        String[] iot_command = anUpdate.split( " " );
        if (iot_command[0].equals( "iot" )){
            if (iot_command[1].equals( "echo" )){
                // firsts it clears the existing list to fill values with updated currently online devices:-
                iot_devices.clear();
                // used to command the devices to echo their names to channel:-
                entryUpdate.addProperty( "iot", "echo" );
            }else if(iot_command[1].equals( "list" )){
                // logs the devices names
                Log.d( TAG, "List of Conneccted IoT devices : " );


            }else{
                // getting all the parameters to command the particular iot device:-
                String iot_device_name = iot_command[1];
                String pin_no = iot_command[2];
                String status = iot_command[3];

                // adding the properties for iot:-
                entryUpdate.addProperty( "iot", status );
                entryUpdate.addProperty( "iot_name", iot_device_name );
                entryUpdate.addProperty( "iot_pin", pin_no );
            }
        }else {
            // to command the laptop or other devices other than iot:-
            entryUpdate.addProperty( "comm", anUpdate );
        }

        if (anUpdate.equals("sysInfoPub")){
            displayMessage("ERROR : ",
                    " == >  " + "THIS COMMAND IS NOT SUPPOSED TO BE USED");
        }else if (anUpdate.equals( "iot list" )){
            // handles that it must not send the request to channel as it is only accessing the internal data.
            // handles if the device list is empty:-
            if (iot_devices.isEmpty()){
                displayMessage( "There is no device ",
                        " name registered yet...");
            }else{
                // to display the currently online devices stored in a list:-
                for(String deviceName:iot_devices){
                    displayMessage( deviceName, "" );
                    Log.d( TAG, deviceName );
                }
                displayMessage(  "List of Conneccted IoT devices",
                        "" );
            }
        }else {

            pubnub.publish().channel(theChannel).message(entryUpdate).async(
                    new PNCallback<PNPublishResult>() {
                        @Override
                        public void onResponse(PNPublishResult result, PNStatus status) {
                            if (status.isError()) {
                                status.getErrorData().getThrowable().printStackTrace();
                            } else {
                                displayMessage("[ Commmand Sent ]",
//                                        "timetoken: " + result.getTimetoken());
                                        "");
                            }
                        }
                    });

        }
    }
    public void photoActivity(String Url, String stamp){
        Intent pic = new Intent( this, culpritPhoto.class );
        pic.putExtra( "url", Url );
        pic.putExtra( "timeStamp", stamp );
        startActivity( pic );
    }
    public void sysViewer(String Platform, String arch, String mac, String ram, String name, String CPU, String ramUsage, String ip, String PID, String SSID, String battery, String batState)
    {
        Intent intent = new Intent(this, Display_sys_info.class);
        intent.putExtra("Platform", Platform);
        intent.putExtra("arch", arch);
        intent.putExtra("mac", mac);
        intent.putExtra("ram", ram);
        intent.putExtra("name", name);
        intent.putExtra("CPU", CPU);
        intent.putExtra("ramUsage", ramUsage);
        intent.putExtra("ip", ip);
        intent.putExtra("PID", PID);
        intent.putExtra("SSID", SSID);
        intent.putExtra("BAT", battery);
        intent.putExtra("batstate", batState);
        startActivity(intent);

    }

    protected void displayMessage(String messageType, String aMessage) {
        String newLine = "\n";

        final StringBuilder textBuilder = new StringBuilder()
                .append(messageType)
                .append(newLine)
                .append(aMessage)
                .append(newLine).append(newLine)
                .append(messagesText.getText().toString());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messagesText.setText(textBuilder.toString());
            }
        });
    }


    public void sendrequest(View view){

        submitUpdate(devide_name, "requestSysActivity");

    }

    // making the menu item attached to the current activity:-
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            // logging out from current user account:-
            case R.id.menu_logout:
                Toast.makeText( this, "Logging Out", Toast.LENGTH_SHORT ).show();
                FirebaseAuth.getInstance().signOut();
                startActivity( new Intent( MainActivity.this, loginactivity.class ) );
                finish();
                return true;

            case R.id.menu_chat:
                Toast.makeText(MainActivity.this, "Openeing Chatting Interface", Toast.LENGTH_SHORT).show();
                Intent chatactivity = new Intent( this, chatService.class );
                chatactivity.putExtra( "channel_name", theChannel );
                chatactivity.putExtra( "deviceName", devide_name );
                chatactivity.putExtra( "pubkey", pubkey );
                chatactivity.putExtra( "subkey", subkey );
                chatactivity.putExtra( "uuid", app_uuid );
                startActivity( chatactivity );
                return true;

        }
        return super.onOptionsItemSelected( item );
    }
}
