package com.example.chessdetection;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SettingsActivity extends AppCompatActivity {


    TextView delete;

    //Location
    private static int MY_FINE_LOCATION_REQUEST = 99;
    private static int MY_BACKGROUND_LOCATION_REQUEST = 100;


    Intent mServiceIntent;
    TextView lat;
    Button startServiceBtn, stopServiceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(SettingsActivity.this,MainActivity.class));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        delete=findViewById(R.id.del_acc);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                user.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User account deleted.");
                                }
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(SettingsActivity.this, "Deleced Account", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(SettingsActivity.this,MainActivity.class));

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@androidx.annotation.NonNull Exception e) {
                                Toast.makeText(SettingsActivity.this, "Failed to delete"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }

}