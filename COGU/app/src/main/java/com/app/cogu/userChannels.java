package com.app.cogu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class userChannels extends AppCompatActivity {

    private static final String TAG = "userChannels";


    // initialinsing some global variables required:-
    private String pubkey;
    private String subkey;
    String app_uuid;
    String userid;

    // creating firebase variables:-
    FirebaseAuth fauth;
    FirebaseFirestore fstore;

    // instantiating recycleview:-
    RecyclerView rvchannel;

    // declaring variables used by firebase and recycleview adapter:-
    List<channel_list_Class> channelList = new ArrayList<>(  );
    Map<String, String> deviceMapping = new HashMap<>(  );


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_channels );

        // Enabling toolbar
        Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar_channel_list );
        setSupportActionBar( toolbar );


        // get the intent
        Intent intent = getIntent();

        // adding fab button:-
        FloatingActionButton fab = (FloatingActionButton) findViewById( R.id.fab_channel_add );

        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent register = new Intent( getBaseContext(), channel_register.class );
                startActivity( register );
                finish();

            }
        } );


        rvchannel = (RecyclerView) findViewById( R.id.channelRecycleview );



        // setting firebase instances:-
        fauth  = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        // getting document reference:-
        userid = fauth.getCurrentUser().getUid();

        final DocumentReference documentReference = fstore.collection( "cogu_users" ).document(userid);


        Log.d( TAG, "user id is here :-> " + userid );

        try {
            documentReference.get().addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    ArrayList<String> channellist = (ArrayList<String>) documentSnapshot.getData().get( "channels" );
                    Map<String, String> temp_deviceMapping = (Map<String, String>) documentSnapshot.getData().get( "deviceNamesMapping" );

                    String get_pubkey  = (String) documentSnapshot.getData().get( "pubkey" );
                    String get_subkey  = (String) documentSnapshot.getData().get( "subkey" );
                    String name_uuid   = (String) documentSnapshot.getData().get( "name" );

                    pubkey = get_pubkey;
                    subkey = get_subkey;
                    app_uuid = name_uuid;

                    Log.d( TAG, "you are searching for" + String.valueOf( documentSnapshot.getData().get( "pubkey" ) ) );

                    Log.d( TAG, "length of ist" + channellist.size() );

                    for (int i = 0 ; i<channellist.size();i++){

                        Log.d( TAG, "values are " + String.valueOf( channellist.get( i ) ) );
                        Log.d( TAG, "values are " + String.valueOf( channellist.get( i ) ) );
                        channelList.add( new channel_list_Class( channellist.get( i ) ));
                    }

                    for(Map.Entry m:temp_deviceMapping.entrySet()){
//                        System.out.println(m.getKey()+" "+m.getValue());
                        deviceMapping.put( m.getKey().toString(), m.getValue().toString() );
                    }

                    Log.d( TAG, "map is : " + deviceMapping );

                    Log.d( TAG, channelList.toString() + "is list" );


                    Log.d(TAG, "list updated");
                    refresh();
                    Log.d( TAG, channellist.toString() );


                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e( TAG, "Error while getting list of devices: " + e.getMessage() );
                }
            } );

        }catch (Exception e1){
            Toast.makeText( this, "Error occured, please restart your app or check network..!!", Toast.LENGTH_SHORT ).show();
            Log.e( TAG, e1.toString() );
        }



        Log.d( TAG, channelList.toString() + "is list" );
        channelNameAdapter channeladapter = new channelNameAdapter( channelList, deviceMapping, pubkey, subkey, app_uuid, userChannels.this);
        Log.d( TAG, "testing" );

        rvchannel.setAdapter( channeladapter );

        rvchannel.setLayoutManager( new LinearLayoutManager( userChannels.this ) );
    }

    private void refresh( ) {


        Log.d( TAG, channelList.toString() + "is list" );
        channelNameAdapter channeladapter = new channelNameAdapter( channelList, deviceMapping, pubkey, subkey, app_uuid, userChannels.this);
        Log.d( TAG, "testing" );

        rvchannel.setAdapter( channeladapter );

        rvchannel.setLayoutManager( new LinearLayoutManager( userChannels.this ) );

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_channel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                Toast.makeText( this, "Logging Out", Toast.LENGTH_SHORT ).show();
                FirebaseAuth.getInstance().signOut();
                startActivity( new Intent( userChannels.this, loginactivity.class ) );
                finish();
                return true;

            case R.id.refresh:
                Toast.makeText( this, "Refreshing", Toast.LENGTH_SHORT ).show();
                refresh( );
        }
        return super.onOptionsItemSelected( item );
    }
}