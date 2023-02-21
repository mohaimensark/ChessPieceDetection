package com.example.chessdetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.chessdetection.databinding.ActivityUpdateProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateProfileActivity extends AppCompatActivity {
    ActivityUpdateProfileBinding binding;
    String name,age,profession,email,about,phone;
    EditText aboutUpdate,nameUpdate,birthUpdate,professUpdate,upphone,updatePhone;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(UpdateProfileActivity.this,MainActivity.class));
        }
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        nameUpdate = findViewById(R.id.nameUpdate);
        birthUpdate = findViewById(R.id.birthUpdate);
        aboutUpdate = findViewById(R.id.aboutUpdate);
        professUpdate = findViewById(R.id.professUpdate);


        name = getIntent().getStringExtra("name");
       // email = getIntent().getStringExtra("email");
     //   profession = getIntent().getStringExtra("birthday");
        age = getIntent().getStringExtra("birthday");
      //  about = getIntent().getStringExtra("about");
        phone = getIntent().getStringExtra("phone");

//        intent.putExtra("birthday", birthday);
//        intent.putExtra("name", name);
//        //  intent.putExtra("profession", profession);
//        intent.putExtra("phone", phone);
//        intent.putExtra("email", email23);


//       // aboutUpdate.setText(about);
//        birthUpdate.setText(age);
//        nameUpdate.setText(name);
//      //  professUpdate.setText(profession);
//        updatePhone.setText(phone);
//


//        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String upname = binding.nameUpdate.getText().toString();
//                String upage = binding.birthUpdate.getText().toString();
//                //String upemail = binding.emailUpdate.getText().toString();
//              //  String upprofess = binding.professUpdate.getText().toString();
//                String upphone = binding.updatePhone.getText().toString();
//
//              //  Toast.makeText(UpdateProfileActivity.this, email, Toast.LENGTH_SHORT).show();
//
//
//
//
//
//
//                firebaseAuth = FirebaseAuth.getInstance();
//                firebaseFirestore = FirebaseFirestore.getInstance();
//                firebaseDatabase = FirebaseDatabase.getInstance();
//
//
//
//
//
//                firebaseFirestore.collection("User")
//                        .document(firebaseAuth.getCurrentUser().getUid()).set(new UserModel(email,upname,upage,upphone))
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                Toast.makeText(UpdateProfileActivity.this, "Updated", Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(UpdateProfileActivity.this,ProfileActivity.class));
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//            }
//        });
    }
}