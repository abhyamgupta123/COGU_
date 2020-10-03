package com.app.cogu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

public class culpritPhoto extends AppCompatActivity {
    ImageView culpritPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_culprit_photo );

        Intent pic = getIntent();
        String url = pic.getStringExtra( "url" );
        String text = pic.getStringExtra( "timeStamp" );

        TextView name = (TextView) findViewById( R.id.culpritName );
        name.setText( text );

        culpritPhoto = findViewById(R.id.imageView);
        loadimage(url);

    }

    private void loadimage (String Url){
        Picasso.get().load( Url ).placeholder( R.mipmap.ic_launcher )
                .error( R.mipmap.ic_launcher )
                .into( culpritPhoto );
    }



}