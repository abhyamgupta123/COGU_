package com.app.cogu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.nfc.Tag;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class loginactivity extends AppCompatActivity {

    private static final String TAG = loginactivity.class.getName();
    FirebaseAuth fauth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_loginactivity );


        // adding toolbar:
        Toolbar toolbar = (Toolbar) findViewById( R.id.login_toolbar );
        setSupportActionBar( toolbar );

        final EditText email    = (EditText) findViewById( R.id.username );
        final EditText pass     = (EditText) findViewById( R.id.password );
        final ProgressBar progressBar = (ProgressBar) findViewById( R.id.loading );
        TextView register_new_user    = (TextView) findViewById( R.id.register_new );
        TextView forgetPassword       = (TextView) findViewById( R.id.forget_pass );
        Button login            = (Button) findViewById( R.id.login );

        // get firebase instance
        fauth = FirebaseAuth.getInstance();

        if (fauth.getCurrentUser() != null){
            startActivity( new Intent( loginactivity.this, userChannels.class ) );
            finish();
        }

        login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Emailid  = email.getText().toString().trim();
                String password = pass.getText().toString().trim();

                if (TextUtils.isEmpty( Emailid )){
                    email.setError( "Required" );
                    return;
                }
                if (TextUtils.isEmpty( password )){
                    pass.setError( "required" );
                    return;
                }
                if (password.length() < 6 ){
                    pass.setError( "MinimUm 6 charecters Required" );
                    return;
                }

                progressBar.setVisibility( View.VISIBLE );

                // authenticate the user:-
                fauth.signInWithEmailAndPassword( Emailid, password )
                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText( loginactivity.this, "Logging In", Toast.LENGTH_SHORT ).show();
                                    startActivity( new Intent( loginactivity.this, userChannels.class ) );
                                    finish();
                                }else{
                                    Toast.makeText( loginactivity.this, "Eror Occured " + Objects.requireNonNull( task.getException() ).getMessage(), Toast.LENGTH_SHORT ).show();
                                    progressBar.setVisibility( View.GONE );
                                }

                            }
                        } );
            }
        } );

        register_new_user.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent( loginactivity.this, userRegistration.class ) );
                finish();
            }
        } );

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Emailid  = email.getText().toString().trim();

                if (TextUtils.isEmpty( Emailid )){
                    email.setError( "Required" );
                    return;
                }

                progressBar.setVisibility( View.VISIBLE );

                fauth.sendPasswordResetEmail( Emailid ).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.d(TAG, "reset link is sent on email id.");
                            Toast.makeText(loginactivity.this, "Password reset link sent on registered Email.", Toast.LENGTH_SHORT).show();
                        }else{
                            Log.d(TAG, "Link is not sent due to some error.");
                            Toast.makeText(loginactivity.this, "Try again, Some error occurred.", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility( View.INVISIBLE );
                        }
                    }
                });

            }
        });

    }
}