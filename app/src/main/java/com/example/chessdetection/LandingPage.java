package com.example.chessdetection;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.chessdetection.databinding.ActivityLandingPageBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.nullness.qual.NonNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class LandingPage extends AppCompatActivity {

    Toolbar toolbar1;
    DrawerLayout drawerLayout,drawerLayout2;
    MenuView.ItemView itemView;
    NavigationView navigationView;
    ActivityLandingPageBinding binding;
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    private FirebaseAuth firebaseAuth,authUser;
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

    ListItemAdapter listItemAdapter;


    FirebaseUser firebaseUser;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLandingPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Toast.makeText(this, "This is landing page", Toast.LENGTH_SHORT).show();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        nav = (NavigationView) findViewById(R.id.navigation_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);


        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();




        img = findViewById(R.id.profile_image);

        binding.Write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LandingPage.this, WritePost.class);
                startActivity(intent);

            }
        });





        nav.setNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId()) {
                case R.id.navi_android:
                    Toast.makeText(getApplicationContext(), "Let's detect chess piece", Toast.LENGTH_LONG).show();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    Intent in = new Intent(LandingPage.this, Classify.class);
                    startActivity(in);
                    break;
                case R.id.profileLink:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    startActivity(new Intent(LandingPage.this, ProfileActivity.class));
                    break;
                case R.id.location:
                    Toast.makeText(getApplicationContext(), "Location is open", Toast.LENGTH_LONG).show();
                    Intent in2 = new Intent(LandingPage.this, MapsActivity.class);
                    startActivity(in2);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;


                case R.id.logout:
                    FirebaseAuth.getInstance().signOut();
                    SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("isLoggedin");
                    editor.commit();
                    Toast.makeText(LandingPage.this, "Logged out", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LandingPage.this, MainActivity.class));
                    finish();
                    break;


                case R.id.rate:
                    startActivity(new Intent(LandingPage.this, RatingActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
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
        });





        binding.recyclerViewID.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<PostDetailsModel> options =
                new FirebaseRecyclerOptions.Builder<PostDetailsModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Posts"), PostDetailsModel.class)
                        .build();

        listItemAdapter = new ListItemAdapter(options, LandingPage.this);

        binding.recyclerViewID.setAdapter(listItemAdapter);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        switch (item.getItemId()) {

            //Toast.makeText(this, "menu clicked", Toast.LENGTH_SHORT).show();

            case R.id.gotoClassify:
                Intent intent = new Intent(LandingPage.this, WritePost.class);
                startActivity(intent);
                return true;


            case R.id.changePass:
                Intent intent1 = new Intent(LandingPage.this, ProfileActivity.class);
                startActivity(intent1);
                return true;

            case R.id.deleteAccount:
                Intent intent2 = new Intent(LandingPage.this, SettingsActivity.class);
                startActivity(intent2);
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }

    }


    protected void onStart() {
        super.onStart();
        authUser = FirebaseAuth.getInstance();
        if (authUser.getCurrentUser() == null) {
            Intent intent = new Intent(LandingPage.this, MainActivity.class);
            startActivity(intent);
        }
        listItemAdapter.startListening();
    }


}



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


// Working of post started from here


//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        switch (menuItem.getItemId()) {
//            case R.id.navi_android:
//                Toast.makeText(getApplicationContext(), "Let's detect chess piece", Toast.LENGTH_LONG).show();
//                drawerLayout.closeDrawer(GravityCompat.START);
//                Intent in = new Intent(LandingPage.this, Classify.class);
//                startActivity(in);
//                break;
//
//            case R.id.location:
//                Toast.makeText(getApplicationContext(), "Location is open", Toast.LENGTH_LONG).show();
//                Intent in2 = new Intent(LandingPage.this, MapsActivity.class);
//                startActivity(in2);
//                drawerLayout.closeDrawer(GravityCompat.START);
//                break;
//
//
////                    case R.id.gsap :
////                        Toast.makeText(getApplicationContext(),"Enjoy gsap animation.",Toast.LENGTH_LONG).show();
////                        drawerLayout.closeDrawer(GravityCompat.START);
////                        break;
//
//            case R.id.logout:
//                FirebaseAuth.getInstance().signOut();
//                Toast.makeText(LandingPage.this, "Logged out", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(LandingPage.this, MainActivity.class));
//                finish();
//                break;
//
//            case R.id.profileLink:
//                startActivity(new Intent(LandingPage.this, ProfileActivity.class));
//                break;
//            case R.id.youtube:
//                startActivity(new Intent(LandingPage.this, Youtube.class));
//                break;
//            case R.id.setting:
//                startActivity(new Intent(LandingPage.this, SettingsActivity.class));
//                break;
//            case R.id.phoneVerify:
//                startActivity(new Intent(LandingPage.this, PhoneVerification.class));
//                break;
//        }
//
//        return true;
//    }


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
