package com.example.chessdetection;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.chessdetection.databinding.ActivityLandingPageBinding;
import com.example.chessdetection.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class LandingPage extends AppCompatActivity {

    Toolbar toolbar1;
    DrawerLayout drawerLayout;
    MenuView.ItemView itemView;
    NavigationView navigationView;
    ActivityLandingPageBinding binding;
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;
    TextView age;
    CircleImageView img;
    ActivityResultLauncher<String> launcher;
    FirebaseDatabase database;
    FirebaseStorage storage;


    private TextView responseTV;
    private TextView questionTV;
    private EditText queryEdt;

    private static final String API_URL = "https://api.openai.com/v1/engines/text-davinci-002/completions";
    private static final String API_KEY = "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";


    private String url = "https://api.openai.com/v1/engines/davinci-codex/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLandingPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        nav = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        img = findViewById(R.id.profile_image);

//        StorageReference dc = storage.getReference().child("User").child(firebaseAuth.getCurrentUser().getUid());
//
//        dc.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//
//                Glide
//                        .with(LandingPage.this)
//                        .load(uri) // the uri you got from Firebase
//                        .into(img); //Your imageView variable
//                //   Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@androidx.annotation.NonNull Exception e) {
//                Toast.makeText(LandingPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

        // Logout
//        binding.logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Toast.makeText(LandingPage.this, "Logged out", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(LandingPage.this,MainActivity.class));
//                finish();
//            }
//        });

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navi_android:
                        Toast.makeText(getApplicationContext(), "Let's detect chess piece", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        Intent in = new Intent(LandingPage.this, Classify.class);
                        startActivity(in);
                        break;


                    case R.id.location:
                        Toast.makeText(getApplicationContext(), "Location is open", Toast.LENGTH_LONG).show();
                        Intent in2 = new Intent(LandingPage.this, MapsActivity.class);
                        startActivity(in2);
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;


//                    case R.id.gsap :
//                        Toast.makeText(getApplicationContext(),"Enjoy gsap animation.",Toast.LENGTH_LONG).show();
//                        drawerLayout.closeDrawer(GravityCompat.START);
//                        break;

                    case R.id.logout:
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(LandingPage.this, "Logged out", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LandingPage.this, MainActivity.class));
                        finish();
                        break;

                    case R.id.profileLink:
                        startActivity(new Intent(LandingPage.this, ProfileActivity.class));
                        break;
                    case R.id.youtube:
                        startActivity(new Intent(LandingPage.this, Youtube.class));
                        break;
                    case R.id.setting:
                        startActivity(new Intent(LandingPage.this, SettingsActivity.class));
                        break;
                    case R.id.phoneVerify:
                        startActivity(new Intent(LandingPage.this, PhoneVerification.class));
                        break;
                }

                return true;
            }
        });


        queryEdt = findViewById(R.id.idEdtQuery);
        questionTV = findViewById(R.id.idTVQuestion);
        responseTV = findViewById(R.id.idTVResponse);

        // adding text watcher for edit text on below line
        queryEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // setting response tv on below line.
                responseTV.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // adding editor action listener for edit text on below line.
        queryEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    // setting response tv on below line.
                    responseTV.setText("Please wait...");
                    // validating text
                    if (queryEdt.getText().toString().trim().length() > 0) {
                        // calling get response to get the response.
                        getResponse(queryEdt.getText().toString().trim());
                    } else {
                        Toast.makeText(LandingPage.this, "Please enter your query...", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

//        toolbar1=findViewById(R.id.toolBar);
//        itemView = findViewById(R.id.navi_android);
//
//        setSupportActionBar(toolbar1);
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        drawerLayout=findViewById(R.id.drawerlayout);
//        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar1,R.string.detect,R.string.close);
//        drawerLayout.addDrawerListener(toggle);
//
//        toggle.syncState();
//
//        navigationView=findViewById(R.id.navigation_view);
//     //   navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
    }


    //    public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
//        if (menuitem.getItemId() == R.id.navi_android) {
//            Intent in =new Intent(LandingPage.this,Classify.class);
//            startActivity(in);
//        }else if (menuitem.getItemId()==R.id.navi_rate){
//
//            Toast.makeText(this, "new", Toast.LENGTH_SHORT).show();
//        }
//
//
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return false;
//    }
    private void getResponse(String query) {
        questionTV.setText(query);
        queryEdt.setText("");

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("model", "text-davinci-002");
            jsonObject.put("prompt", query);
            jsonObject.put("temperature", 0);
            jsonObject.put("max_tokens", 100);
            jsonObject.put("top_p", 1);
            jsonObject.put("frequency_penalty", 0.0);
            jsonObject.put("presence_penalty", 0.0);

            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> {
                        try {
                            String responseMsg = response.getJSONArray("choices").getJSONObject(0).getString("text");
                            responseTV.setText(responseMsg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Log.e("TAGAPI", "Error is : " + error.getMessage() + "\n" + error);
                        Toast.makeText(getApplicationContext(), "Something went wrong. Please try again later!", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "Bearer " + API_KEY);
                    return params;
                }
            };

            postRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {
                }
            });
            queue.add(postRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}