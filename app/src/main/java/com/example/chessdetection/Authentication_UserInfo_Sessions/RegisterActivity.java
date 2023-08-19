package com.example.chessdetection.Authentication_UserInfo_Sessions;

import static com.google.firebase.inappmessaging.internal.Logging.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chessdetection.R;
import com.example.chessdetection.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

//
//import com.google.i18n.phonenumbers.NumberParseException;
//import com.google.i18n.phonenumbers.PhoneNumberUtil;
//import com.google.i18n.phonenumbers.Phonenumber;
public class RegisterActivity extends AppCompatActivity {

    TextView already;
    ActivityRegisterBinding binding;
    FirebaseAuth firebaseAuth;
    Boolean uniUser;
    ProgressDialog progressDialog;
    View login_again;
    FirebaseFirestore firebaseFirestore;
    FirebaseStorage storage;
    ActivityResultLauncher<String> launcher;
    FirebaseDatabase firebaseDatabase;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);



       //  real time username checking
        EditText username = findViewById(R.id.nameReg);
        TextView errormsg = findViewById(R.id.errmsg3);

        Integer all_ok = 1  ;


        username.setOnFocusChangeListener((view, hasFocus) -> {     // to check the username is new or old
                                                            // if old then break else continue //
            if(!hasFocus) {
                checkUser();
            }
        });



        //realtime email checking
        EditText emailEditText2 = findViewById(R.id.emailReg);
        TextView emailErrorTextView2 = findViewById(R.id.errmsg2);

        emailEditText2.addTextChangedListener(new TextWatcher() {
            private final long DELAY = 100; // Delay time in milliseconds
            private Handler handler = new Handler(Looper.getMainLooper());
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(runnable); // Remove the previous runnable
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!s.toString().isEmpty()) { // Check if the email is not empty
                            // Check if email is already registered
                            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(s.toString())
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            List<String> signInMethods = task.getResult().getSignInMethods();
                                            if (signInMethods != null && signInMethods.size() > 0) {
                                                emailErrorTextView2.setText("Email is already registered.");
                                            } else {
                                                emailErrorTextView2.setText("");
                                            }
                                        } else {
                                            // Handle errors
                                            emailErrorTextView2.setText(task.getException().getMessage());
                                        }
                                    });
                        } else {
                            emailErrorTextView2.setText("");
                        }
                    }
                };
                handler.postDelayed(runnable, DELAY); // Schedule the new runnable
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });

        //real time email checking
        emailEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if the current text is a valid email address
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                    emailErrorTextView2.setText("");
                } else {
                    emailErrorTextView2.setText("Invalid email address");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });



        // Date Picker
        EditText enterAgeTextView = findViewById(R.id.enterAgeTextView);
        enterAgeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RegisterActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                // Do something with the date selected by the user
                                enterAgeTextView.setText(day+"/"+month+1+"/"+year);
                            }
                        },
                        year, month, day);
                datePickerDialog.show();
            }
        });


        binding.btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.nameReg.getText().toString();
                String email = binding.emailReg.getText().toString();
                String phone = binding.phoneNo.getText().toString();
                String birthday = binding.enterAgeTextView.getText().toString();
                String password = binding.passReg.getText().toString();
                String confpass = binding.passConf.getText().toString();
                int pvt = 1;

                progressDialog.show();
                firebaseAuth = FirebaseAuth.getInstance();
                EditText phoneNumberEditText = findViewById(R.id.phone_no);
                String phoneNumber = phoneNumberEditText.getText().toString().trim();

//
                 if(!mobileNumberValidation(phone))
                 {


                         progressDialog.cancel();
                         Toast.makeText(RegisterActivity.this, "Invalid Phone no!", Toast.LENGTH_SHORT).show();



                 }else if(!uniUser){
                     progressDialog.cancel();
                     Toast.makeText(RegisterActivity.this, "Username not available", Toast.LENGTH_SHORT).show();
                 }
               else if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)||TextUtils.isEmpty(confpass) || TextUtils.isEmpty(phone)||pvt==0) {
                    progressDialog.cancel();
                    Toast.makeText(RegisterActivity.this, "Empty credintials", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 6) {
                    progressDialog.cancel();
                    Toast.makeText(RegisterActivity.this, "Password is too short", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confpass)) {
                    progressDialog.cancel();
                    Toast.makeText(RegisterActivity.this, "Password Missmatched!", Toast.LENGTH_SHORT).show();
                }
                else {


                                        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                Toast.makeText(RegisterActivity.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                                progressDialog.cancel();
                                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                finish();
                                                progressDialog.cancel();



                                                firebaseAuth = FirebaseAuth.getInstance();

                                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser(); //n
                                                UserDetails userDetails = new UserDetails(name, email, birthday,phone);

                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users_new");


                                                databaseReference.child(firebaseUser.getUid()).setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()) {

                                                            firebaseFirestore = FirebaseFirestore.getInstance();
                                                            firebaseDatabase = FirebaseDatabase.getInstance();
                                                            sendVerificationEmail();
                                                            String about="Update your about";
                                                            String profession = "Update your profession";
                                                            String country = "Set up your country";



                                                            Toast.makeText(RegisterActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                                                            firebaseFirestore.collection("User")
                                                                    .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).set(new UserModel(email, name, birthday, phone,about,profession,country,"false","false"));


                                                        }else {
                                                            Toast.makeText(RegisterActivity.this, "User register failed. Please try again.",
                                                                    Toast.LENGTH_LONG).show();
                                                        }


                                                        //
                                                    }
                                                });






                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.cancel();
                                              Toast.makeText(RegisterActivity.this, e.getMessage() + "reg lppp", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                }
            }
        });


        already = findViewById(R.id.haveAcc);

        already.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,MainActivity.class));
            }
        });

    }

    private void checkUser() {
        int all_ok = 0 ;
        EditText username = findViewById(R.id.nameReg);
        TextView erroruser = findViewById(R.id.errmsg3);
        String shortName = username.getText().toString();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Users_new") ;


        if (!shortName.isEmpty()) {
            com.google.firebase.database.Query query = referenceProfile.orderByChild("userName").equalTo(shortName);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        username.setError("Username already exist");
                        uniUser=false;
                    //    erroruser.setText("Username already exist");
                     //   Toast.makeText(RegisterActivity.this,"LOL LOL  ", Toast.LENGTH_LONG).show();
                      //  all_ok ++ ;
                    } else {
                        username.setError(null);
                        uniUser=true;
                   //     username.setText("Username available");
                     //   Toast.makeText(RegisterActivity.this,"No user matched " + Integer.toString(all_ok), Toast.LENGTH_LONG).show();
                        //editTextUsername.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_username, 0, R.drawable.ic_ok, 0);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }
        else {
            username.setError("Username cannot be empty");
        }
    }
    public static boolean mobileNumberValidation(String number) {
        // Regular expression for a valid Bangladeshi mobile number
        String regex = "^01[3-9]\\d{8}$";

        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);

        // Check if the provided number matches the pattern
        return pattern.matcher(number).matches();
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email verification sent.");
                                Toast.makeText(RegisterActivity.this, "Email verification sent", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Log.e(TAG, "Error sending email verification", task.getException());
                                Toast.makeText(RegisterActivity.this, "Error sending email verification", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
}
}