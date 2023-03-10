package com.example.chessdetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chessdetection.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.checkerframework.checker.nullness.qual.NonNull;

public class MainActivity extends AppCompatActivity {

     Button button;
     TextView reg;
     TextView forget;
    FirebaseAuth firebaseAuth;
    ActivityMainBinding binding;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        button = findViewById(R.id.buttonId);
        reg = findViewById(R.id.txtReg);
        forget = findViewById(R.id.txtFor);


        TextView emailErrorTextView;
        emailErrorTextView = findViewById(R.id.errmsg);
        EditText emailEditText;
        emailEditText = findViewById(R.id.emailSin);
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if the current text is a valid email address
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    emailErrorTextView.setText("");
                } else {
                    emailErrorTextView.setText("Invalid email address");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String email = binding.emailSin.getText().toString();
                    String password = binding.passSin.getText().toString();
                    progressDialog.show();
                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                        progressDialog.cancel();
                        Toast.makeText(MainActivity.this, "Empty credintials", Toast.LENGTH_SHORT).show();
                    } else if (password.length() < 6) {
                        progressDialog.cancel();
                        Toast.makeText(MainActivity.this, "Password is too short", Toast.LENGTH_SHORT).show();
                    } else {
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        progressDialog.cancel();

                                        Toast.makeText(MainActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this, LandingPage.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.cancel();
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
        });



       reg.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(MainActivity.this,RegisterActivity.class));
           }
       });

       forget.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                 startActivity(new Intent(MainActivity.this,ForgetPassword.class));
           }
       });
    }
}