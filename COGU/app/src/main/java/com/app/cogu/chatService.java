package com.app.cogu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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

public class chatService extends AppCompatActivity {

    private static final String TAG = chatService.class.getName();

    private PubNub pubnub;
    private String theChannel;
    private String devide_name ;

    private EditText message_edit;
    private Button send;
    String app_uuid;
    String pubkey;
    String subkey;

    List<chat_messages_class> chats = new ArrayList<>(  );
    chat_adapter adapter;
    RecyclerView rvchats;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_chat_service );

//        // adding toolbar:
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

        devide_name = devide_name + "_chat";
        Log.d(TAG, "port name is" + devide_name);

        message_edit = (EditText) findViewById(R.id.message_edit);
        send = (Button) findViewById(R.id.message_send);

        // Lookup the recyclerview in activity layout
        rvchats = (RecyclerView) findViewById(R.id.rvchat);

//        chats.add(new chat_messages_class("something", true));

        // Create adapter passing in the sample user data
        adapter = new chat_adapter( chats );
        // Attach the adapter to the recyclerview to populate items
        rvchats.setAdapter(adapter);
        // Set layout manager to position the items
        rvchats.setLayoutManager(new LinearLayoutManager(this));

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
        ImageView chat_connection_status = (ImageView) findViewById(R.id.chat_connection_status);
        chat_connection_status.setImageResource( R.drawable.ic_offline_status );
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
                            for (PNHereNowOccupantData occupant : channelData.getOccupants()) {
//                                displayMessage( "UUID", occupant.getUuid() );
//                                displayMessage( "State", String.valueOf( occupant.getState() ) );
                                if (!occupant.getUuid().equals( devide_name )){
                                    chat_connection_status.setImageResource( R.drawable.ic_offline_status );
                                    Log.d(TAG, "System is offline" );
                                }else{
                                    chat_connection_status.setImageResource( R.drawable.ic_online_status );
                                    Log.d(TAG, "System is online" );
                                }
                            }
                        }
                    }
                });

        pubnub.addListener(new SubscribeCallback() {

            @Override
            public void message(PubNub pubnub, PNMessageResult event) {
                JsonObject message = event.getMessage().getAsJsonObject();
                try {
                    String text = message.get( "chat_message" ).getAsString();
                    boolean isme = message.get("isme").getAsBoolean();
                    if(!isme) {
                        chats.add(new chat_messages_class(text, false));
                        adapter.notifyDataSetChanged();
                        if(!isVisible()){
                            rvchats.smoothScrollToPosition(adapter.getItemCount() - 1 );
                        }
                    }

                }catch (Exception e) {
                    Log.e(TAG, "some error occured while recieving message-> " + e.toString());
                }
            }

            @Override
            public void status(PubNub pubnub, PNStatus event) {
                if (event.getCategory().equals( PNStatusCategory.PNUnexpectedDisconnectCategory )) {
                    // internet got lost, call reconnect when ready
                    Log.d(TAG, "Reconnected Successfully");
                    pubnub.reconnect();
                }else if (event.getCategory().equals( PNStatusCategory.PNTimeoutCategory )) {
                    // call reconnect when ready
                    Log.d(TAG, "Reconnected Successfully");
                    pubnub.reconnect();
                }else if (event.getCategory().equals( PNStatusCategory.PNReconnectedCategory )){
                    Log.d(TAG, "Reconnected Successfully");
                }else if (event.getCategory().equals(PNStatusCategory.PNConnectedCategory)){
                    Log.d(TAG, "Connected to channel successfully");
                }else if (event.getCategory().equals(PNStatusCategory.PNUnknownCategory)){
                    Log.e(TAG, "Unexpected error -> " + event.getCategory().toString());
                    Log.e( TAG, event.toString() );
                }else {
                    Log.e(TAG, "[STATUS: " + event.getCategory() + "]" + "connected to channels: " + event.getAffectedChannels());
                    Log.e(TAG, event.toString());
                }
            }

            @Override
            public void presence(PubNub pubnub, PNPresenceEventResult event) {
                Log.d(TAG,"[PRESENCE: " + event.getEvent() + "uuid: " + event.getUuid() + ", channel: " + event.getChannel() + "]");
                if(event.getUuid().equals( devide_name ) && (event.getEvent().equals( "leave" ) || event.getEvent().equals( "timeout" ))){
                    chat_connection_status.setImageResource( R.drawable.ic_offline_status );
                    Log.d(TAG, "System is offline" + " Pubnub event log" );
                }else if (event.getUuid().equals( devide_name ) && event.getEvent().equals( "join" )){
                    chat_connection_status.setImageResource( R.drawable.ic_online_status );
                    Log.d(TAG, "System is online and server is just joined" + " Pubnub event log" );
                }

            }
            // even if you don't need these handler, you still have include them
            // because we are extending an Abstract class
            @Override
            public void signal(PubNub pubnub, PNSignalResult event) { }

            @Override
            public void uuid(PubNub pubnub, PNUUIDMetadataResult pnUUIDMetadataResult) { }

            @Override
            public void channel(PubNub pubnub, PNChannelMetadataResult pnChannelMetadataResult) { }

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


        // Assign an onClickListener to the app’s “Authentication” button//
        message_edit.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if( i == EditorInfo.IME_ACTION_SEND){
                    send_message_class();
                }
                return true;
            }
        } );

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_message_class();
            }
        });
    }

    public void send_message_class(){
        JsonObject message_tosend = new JsonObject();
        String my_message = message_edit.getText().toString();
        chats.add(new chat_messages_class(my_message, true));
        message_tosend.addProperty("chat_message", my_message);
        message_tosend.addProperty("isme", true);
        adapter.notifyDataSetChanged();
        if(!isVisible()){
            rvchats.smoothScrollToPosition(adapter.getItemCount() - 1 );
        }
        pubnub.publish().channel(theChannel).message(message_tosend).async(
                new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        if (status.isError()) {
                            status.getErrorData().getThrowable().printStackTrace();
                        } else {
                            Log.d(TAG, "message sent successfully");
                        }
                    }
                });
        message_edit.setText("");

    }
    boolean isVisible(){
        LinearLayoutManager layoutManager = ((LinearLayoutManager) rvchats.getLayoutManager());
        int pos = layoutManager.findLastVisibleItemPosition();
        int numItems = rvchats.getAdapter().getItemCount();
        return (pos >= numItems);
    }
}
