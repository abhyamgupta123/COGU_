package com.app.cogu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class channel_register extends AppCompatActivity {

    private static final String TAG = "channel_register";

    FirebaseAuth fauth;
    FirebaseFirestore fstore;



    EditText channel_input;
    EditText systemname;
    Button register;
    Button cancel_button;
    String currentuserid;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_channel_register );

        channel_input = (EditText) findViewById( R.id.channel_name_input );
        systemname    = (EditText) findViewById( R.id.systemName );
        register      = (Button) findViewById( R.id.register );
        cancel_button = (Button) findViewById( R.id.cancel );
        progressBar   = (ProgressBar) findViewById( R.id.progressBar_register );




        register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegisterAction();
            }
        } );


        // Assign an onClickListener to the keyboard's action send button:-
        systemname.setOnEditorActionListener( new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if( i == EditorInfo.IME_ACTION_SEND){
                    RegisterAction();
                }
                return true;
            }
        } );

        cancel_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( channel_register.this, userChannels.class ) );
                finish();
            }
        } );

    }


    public void RegisterAction(){

        fauth  = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        currentuserid = fauth.getCurrentUser().getUid();


        // getting the data from firestore
        final DocumentReference documentReference = fstore.collection( "cogu_users" ).document(currentuserid);


        progressBar.setVisibility( View.VISIBLE );
        final String channelName = channel_input.getText().toString();
        final String deviceOnChannel   = systemname.getText().toString();


        try {
            documentReference.get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    ArrayList<String> channellist = (ArrayList<String>) documentSnapshot.getData().get( "channels" );
                    channellist.add(channelName);

                    Map<String, String> device_names = (Map<String, String>) documentSnapshot.getData().get( "deviceNamesMapping" );
                    device_names.put( channelName, deviceOnChannel );

                    documentReference.update( "channels", channellist ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility( View.GONE );
                            Log.d(TAG,"user data saved successfully");
                            startActivity( new Intent( channel_register.this, userChannels.class ) );
                            finish();
                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility( View.INVISIBLE );
                            Toast.makeText( channel_register.this, "Eroor occured try again...", Toast.LENGTH_SHORT ).show();
                            Log.d(TAG, "Error occured while saving in firestore : " + e.getMessage());
                        }
                    } );

                    Log.d( TAG, String.valueOf( channellist )  );
                    Log.d(TAG, String.valueOf( documentSnapshot.getData().get( "channels" ) ) );

                    documentReference.update( "deviceNamesMapping", device_names ).addOnSuccessListener( new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d( TAG, "user data saved successfully" );
                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText( channel_register.this, "Eroor occured try again...", Toast.LENGTH_SHORT ).show();
                            Log.d( TAG, "Error occured while saving in firestore : " + e.getMessage() );
                        }
                    } );
                }
            } );

        }catch (Exception e) {
            Log.e( TAG, e.toString() );
            Toast.makeText( channel_register.this, "Create Your First channel", Toast.LENGTH_SHORT ).show();

            // firebase document handling;
            ArrayList<String> channellist = new ArrayList<String>();
            channellist.add( channelName );

            Map<String, String> device_names = new HashMap<>(  );
            device_names.put( channelName, deviceOnChannel );

            documentReference.update( "channels", channellist ).addOnSuccessListener( new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressBar.setVisibility( View.GONE );
                    Log.d( TAG, "user data saved successfully" );
                    startActivity( new Intent( channel_register.this, userChannels.class ) );
                    finish();
                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility( View.INVISIBLE );
                    Toast.makeText( channel_register.this, "Eroor occured try again...", Toast.LENGTH_SHORT ).show();
                    Log.d( TAG, "Error occured while saving in firestore : " + e.getMessage() );
                }
            } );

            documentReference.update( "deviceNamesMapping", device_names ).addOnSuccessListener( new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d( TAG, "user data saved successfully" );
                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( channel_register.this, "Eroor occured try again...", Toast.LENGTH_SHORT ).show();
                    Log.d( TAG, "Error occured while saving in firestore : " + e.getMessage() );
                }
            } );
        }

    }
}