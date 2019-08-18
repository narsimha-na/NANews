package com.example.nanews;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.nanews.auth.Authentication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth maAuth;
    private ImageView img;
    private TextView name,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maAuth = FirebaseAuth.getInstance();

        name = (TextView) findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        img = (ImageView)findViewById(R.id.img);


        FirebaseUser fbUser = maAuth.getCurrentUser();

        try{
            Glide.with(this).load(fbUser.getPhotoUrl()).into(img);
            name.setText(fbUser.getDisplayName());
            email.setText(fbUser.getEmail());
        }catch (Exception e){
            Toast.makeText(MainActivity.this,"Failed Image",Toast.LENGTH_LONG).show();
        }


        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,Authentication.class));
            }
        });

    }

}
