package com.app.cogu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class userRegistration extends AppCompatActivity {

    private static final String TAG = "userRegistration";

    EditText userName_edit;
    EditText userpasswd;
    EditText userEmail;
    EditText userPhone;
    EditText userPubkey;
    EditText userSubkey;


    Button register;
    TextView login;

    FirebaseAuth fauth;
    FirebaseFirestore fstore;
    ProgressBar progressBar;

    String userId;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_registration );

        // adding toolbar:
        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById( R.id.main_toolbar );
        setSupportActionBar( toolbar );


        // assigning ui elements:
        userName_edit  = (EditText) findViewById( R.id.user_name );
        userpasswd = (EditText) findViewById( R.id.user_passwd );
        userEmail  = (EditText) findViewById( R.id.user_email );
        userPhone  = (EditText) findViewById( R.id.user_phone );
        userPubkey = (EditText) findViewById( R.id.user_pubkey );
        userSubkey = (EditText) findViewById( R.id.user_subkey );


        register   = (Button) findViewById( R.id.user_register );
        login      = (TextView) findViewById( R.id.user_login );

        // firebase auth and progressbar:-
        fauth       = FirebaseAuth.getInstance();
        fstore      = FirebaseFirestore.getInstance();
        progressBar = (ProgressBar) findViewById( R.id.progressBar );


        if (fauth.getCurrentUser() != null){
            startActivity( new Intent( userRegistration.this, userChannels.class ) );
            finish();
        }



        register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email     = userEmail.getText().toString().trim();
                String password  = userpasswd.getText().toString().trim();
                final String phone     = userPhone.getText().toString().trim();
                final String pubkey    = userPubkey.getText().toString();
                final String subkey    = userSubkey.getText().toString();
                final String userName  = userName_edit.getText().toString();

                if (TextUtils.isEmpty( email )){
                     userEmail.setError( "Required" );
                     return;
                }
                if (TextUtils.isEmpty( password )){
                    userpasswd.setError( "required" );
                    return;
                }
                if (TextUtils.isEmpty( phone )){
                    userPhone.setError( "Required" );
                    return;
                }
                if (TextUtils.isEmpty( pubkey )){
                    userPubkey.setError( "required" );
                    return;
                }
                if (TextUtils.isEmpty( subkey )){
                    userSubkey.setError( "Required" );
                    return;
                }
                if (password.length() < 6 ){
                    userpasswd.setError( "MinimUm 6 charecters Required" );
                    return;
                }

                progressBar.setVisibility( View.VISIBLE );

                // register user to firebase:-
                fauth.createUserWithEmailAndPassword( email, password )
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    userId = fauth.getCurrentUser().getUid();
                                    Toast.makeText( userRegistration.this, "Account Created..!!", Toast.LENGTH_SHORT ).show();
                                    DocumentReference documentReference = fstore.collection( "cogu_users" ).document(userId);
                                    Map<String, Object> user_data = new HashMap<>();
                                    ArrayList<String> channellist = new ArrayList<String>(  );
                                    Map<String, String> device_names = new HashMap<>(  );
                                    user_data.put("name", userName);
                                    user_data.put("phone", phone);
                                    user_data.put("pubkey", pubkey);
                                    user_data.put("subkey", subkey);
                                    user_data.put("channels", channellist);
                                    user_data.put( "deviceNamesMapping", device_names );
                                    documentReference.set( user_data ).addOnSuccessListener( new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG,"user data saved successfully");
                                        }
                                    } ).addOnFailureListener( new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText( userRegistration.this, "Eroor occured try again...", Toast.LENGTH_SHORT ).show();
                                            Log.d(TAG, "Error occured while saving in firestore : " + e.getMessage());
                                        }
                                    } );

                                    startActivity( new Intent( userRegistration.this, userChannels.class ) );
                                    finish();
                                }else{
                                    Toast.makeText( userRegistration.this, "Eror Occured " + Objects.requireNonNull( task.getException() ).getMessage(), Toast.LENGTH_SHORT ).show();
                                    progressBar.setVisibility( View.GONE );
                                }
                            }
                        } );


            }
        } );

        login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( userRegistration.this, loginactivity.class ) );
                finish();
            }
        } );



    }
}