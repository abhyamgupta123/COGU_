package com.app.cogu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
        Button login            = (Button)   findViewById( R.id.login );

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

    }
}