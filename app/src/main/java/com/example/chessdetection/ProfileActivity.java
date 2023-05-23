package com.example.chessdetection;

import static com.google.firebase.inappmessaging.internal.Logging.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.example.chessdetection.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;

    TextView name,profession,email,about,birthday,phone,country;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;
    TextView age;
    CircleImageView img;
    ActivityResultLauncher<String> launcher;
    FirebaseDatabase database;
    FirebaseStorage storage;
    public Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(ProfileActivity.this,MainActivity.class));
        }
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        name=findViewById(R.id.name);
        email= findViewById(R.id.email);
        profession = findViewById(R.id.prof);
        img = findViewById(R.id.image);
        birthday = findViewById(R.id.birthday);
        phone = findViewById(R.id.phone);
        about = findViewById(R.id.about);
        country = findViewById(R.id.country);
        logout = findViewById(R.id.logout);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        userId = firebaseAuth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();



        launcher = registerForActivityResult(new ActivityResultContracts.GetContent()
                , new ActivityResultCallback<Uri>() {

                    @Override
                    public void onActivityResult(Uri result) {
                        binding.image.setImageURI(result);

                        StorageReference reference = storage.getReference().child("User").child(firebaseAuth.getCurrentUser().getUid());

                        reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //  Toast.makeText(ProfileActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri result) {
                                                        database.getReference().child("User").child("Image").child(firebaseAuth.getCurrentUser().getUid())
                                                                .setValue(result.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(ProfileActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(ProfileActivity.this, "not uploaded", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ProfileActivity.this, "failed uploaded", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                    }
                });

        binding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launcher.launch("image/*");
            }
        });

        //Loading Image
        StorageReference dc = storage.getReference().child("User").child(firebaseAuth.getCurrentUser().getUid());

        dc.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {


                Glide
                        .with(ProfileActivity.this)
                        .load(uri) // the uri you got from Firebase
                        .into(img); //Your imageView variable
                //   Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        DocumentReference documentReference = firebaseFirestore.collection("User").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                name.setText(value.getString("name"));
                email.setText(value.getString("email"));
                phone.setText(value.getString("phone"));
                birthday.setText(value.getString("birthdate"));
                about.setText(value.getString("about"));
                profession.setText(value.getString("profess"));

            }
        });

        FirebaseUser userv = FirebaseAuth.getInstance().getCurrentUser();
        TextView statusTextView = findViewById(R.id.verify_email);
        user.reload();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user.isEmailVerified()) {
                    statusTextView.setText("Email is verified");
                    statusTextView.setTextColor(Color.GREEN);
                }
                else {
                    statusTextView.setText("Email is not verified");
                    statusTextView.setTextColor(Color.RED);
                }
                user.reload();
                handler.postDelayed(this, 1000); // check every 1 second
            }
        }, 1000); // start checking after 1 second



        TextView verifyEmailButton = findViewById(R.id.verify_email);



            verifyEmailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendVerificationEmail();
                }
            });


            //Phone verification checking
//        TextView phoneVerificationTextView = findViewById(R.id.phone_verification_text_view);
//
//        if (user != null) {
//            user.reload();
//            String phone = user.getPhoneNumber();
//            if (phone != null && user.isPhoneNumberVerified()) {
//                phoneVerificationTextView.setText("Phone is verified");
//                phoneVerificationTextView.setTextColor(Color.GREEN);
//            }
//        }
//
//        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
//                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<GetTokenResult> task) {
//                        if (task.isSuccessful()) {
//                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                            user.reload();
//                            String phone = user.getPhoneNumber();
//                            if (phone != null && user.isPhoneNumberVerified()) {
//                                phoneVerificationTextView.setText("Phone is verified");
//                                phoneVerificationTextView.setTextColor(Color.GREEN);
//                            }
//                        }
//                    }
//                });

//        binding.logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                FirebaseAuth.getInstance().signOut();
//                Toast.makeText(ProfileActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(ProfileActivity.this,MainActivity.class));
//                finish();
//            }
//        });


        binding.UpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.name.getText().toString();
                String profession = binding.prof.getText().toString();
                String about = binding.about.getText().toString();
                String birthday = binding.birthday.getText().toString();
                String country = binding.country.getText().toString();
                String email23 = binding.email.getText().toString();
                String phone = binding.phone.getText().toString();
                Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
                intent.putExtra("birthday", birthday);
                intent.putExtra("name", name);
                intent.putExtra("profession",profession);
                intent.putExtra("about",about);
                intent.putExtra("country",country);
              // intent.putExtra("email", profession);
                intent.putExtra("phone", phone);
                intent.putExtra("email", email23);

//Corresponding receiving paremeter
//                name = getIntent().getStringExtra("name");
//
//                country = getIntent().getStringExtra("country");
//                profession = getIntent().getStringExtra("profession");
//                birthday = getIntent().getStringExtra("birthday");
//                about = getIntent().getStringExtra("about");
//                phone = getIntent().getStringExtra("phone");




                startActivity(intent);
            }
        });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null&&!user.isEmailVerified()) {

            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email verification sent.");
                                Toast.makeText(ProfileActivity.this, "Email verification sent", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Log.e(TAG, "Error sending email verification", task.getException());
                                Toast.makeText(ProfileActivity.this, "Error sending email verification", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}