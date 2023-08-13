package com.example.chessdetection.Authentication_UserInfo_Sessions;

import static com.google.firebase.inappmessaging.internal.Logging.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chessdetection.R;
import com.example.chessdetection.databinding.ActivityUpdateProfileBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

public class UpdateProfileActivity extends AppCompatActivity {
    ActivityUpdateProfileBinding binding;
    String name,birthday,profession,email,country,about,phone;
    EditText aboutUpdate,nameUpdate,birthUpdate,professUpdate,upphone,updatePhone,countryUpdate;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseDatabase firebaseDatabase;
    String isEmailVerified,isPhoneVerified;

    private FirebaseFirestore db;

    String userId;
    TextView age;
    FirebaseDatabase database;
    FirebaseStorage storage;
    public Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(UpdateProfileActivity.this, MainActivity.class));
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
       countryUpdate = findViewById(R.id.updateCountry);
        upphone = findViewById(R.id.updatePhone);

        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        country = getIntent().getStringExtra("country");
        profession = getIntent().getStringExtra("profession");
        birthday = getIntent().getStringExtra("birthday");
        about = getIntent().getStringExtra("about");
        phone = getIntent().getStringExtra("phone");



        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();

          aboutUpdate.setText(about);
        birthUpdate.setText(birthday);
          nameUpdate.setText(name);
         professUpdate.setText(profession);
          upphone.setText(phone);
         countryUpdate.setText(country);



        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String upname = binding.nameUpdate.getText().toString();
                String upage = binding.birthUpdate.getText().toString();
                String upabout = binding.aboutUpdate.getText().toString();
                 String upprofess = binding.professUpdate.getText().toString();
                String upphone2 = binding.updatePhone.getText().toString();
                 String countryUpdate = binding.updateCountry.getText().toString();
               // Toast.makeText(UpdateProfileActivity.this, email, Toast.LENGTH_SHORT).show();






                firebaseAuth = FirebaseAuth.getInstance();
                firebaseFirestore = FirebaseFirestore.getInstance();
                firebaseDatabase = FirebaseDatabase.getInstance();


                db = FirebaseFirestore.getInstance();




                // Get the current user ID from Firebase Authentication
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

               // Updating into realtime database
                DatabaseReference valueRef = databaseReference.child("Users_new").child(userId).child("userName");

                Toast.makeText(UpdateProfileActivity.this, userId, Toast.LENGTH_SHORT).show();


                  //Use the setValue() method to update the value
                valueRef.setValue(upname)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Value updated successfully
                                Toast.makeText(UpdateProfileActivity.this, "Updated name", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Failed to update value
                                // Handle failure or log the error message
                            }
                        });

                // Query the "users" collection for the current user in Firebase Firestore
                db.collection("users")
                        .whereEqualTo("userId", userId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                // Loop through the result set and get the user data
                                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                    isEmailVerified = document.getString("emailVerified");
                                   isPhoneVerified= document.getString("phoneVerified");

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error getting user data", e);
                            }
                        });

                firebaseFirestore.collection("User")
                        .document(firebaseAuth.getCurrentUser().getUid()).set(new UserModel(email,upname,upage,upphone2,upabout,upprofess,countryUpdate,isEmailVerified,isPhoneVerified))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(UpdateProfileActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UpdateProfileActivity.this, ProfileActivity.class));
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}