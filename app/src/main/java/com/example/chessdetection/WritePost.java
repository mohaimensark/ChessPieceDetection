package com.example.chessdetection;

import static java.lang.Math.ceil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chessdetection.databinding.ActivityWritePostBinding;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.example.chessdetection.ml.ModelUnquant;
import com.squareup.picasso.Picasso;

import org.tensorflow.lite.DataType;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class WritePost extends AppCompatActivity {

    ActivityWritePostBinding activityWritePostBinding;

    private String userID, userName, predictionResult, uploadLocation;

    Bitmap bitmap;
    private Uri postUriImage;

    Button selectBtn, captureBtn;
    ImageView imageView;
    TextView result;
    Bitmap image;

    FirebaseStorage storage;
    TextView back;
    int imageSize = 224 ;
    String ProfileLink;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseUser firebaseUser2;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    private static final String TAG = "Write a post";
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        activityWritePostBinding = ActivityWritePostBinding.inflate(getLayoutInflater());
        setContentView(activityWritePostBinding.getRoot());
       // getSupportActionBar().setTitle("Write a post");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(WritePost.this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        storageReference = FirebaseStorage.getInstance().getReference("PostImages");
        databaseReference = FirebaseDatabase.getInstance().getReference("Posts");

        activityWritePostBinding.selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        activityWritePostBinding.captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 12);

            }
        });

        activityWritePostBinding.postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UploadImage();
            }
        });

        // Getting profile image
        String  userID = firebaseUser.getUid();

        //Extracting Profile from database for "Users"
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ImageCollector userDetails = snapshot.getValue(ImageCollector.class);
                if(snapshot != null){
                    ProfileLink = snapshot.getValue().toString();
         //           Toast.makeText(WritePost.this, ProfileLink, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(WritePost.this, "Null", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if(locationResult == null){
                return;
            }
            for(Location location: locationResult.getLocations()){
                Geocoder geocoder = new Geocoder(WritePost.this, Locale.getDefault());
                try {
                    List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);

                    /*
                    activityClassifyBinding.textViewLatLong.setText("Latitude : " + addressList.get(0).getLatitude()
                            + "\nLongitude : " + addressList.get(0).getLongitude());
                    */

                    uploadLocation = addressList.get(0).getAddressLine(0);
                    activityWritePostBinding.textViewLocationAddress.setText(addressList.get(0).getAddressLine(0));
                    stopLocationUpdates();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(WritePost.this, Manifest.permission
                .ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //checkSettingsAndStartLocationUpdates();
        }
        if (ActivityCompat.checkSelfPermission(WritePost.this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            askLocationPermission();
        }
        if (ActivityCompat.checkSelfPermission(WritePost.this, Manifest.permission
                .CAMERA) != PackageManager.PERMISSION_GRANTED) {
            askCameraPermission();
        }
    }

    void askCameraPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WritePost.this,
                    new String[]{Manifest.permission.CAMERA}, 11);
        }
    }
    private void askLocationPermission(){

        if (ActivityCompat.checkSelfPermission(WritePost.this, Manifest.permission
                .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WritePost.this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 10001);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();

        stopLocationUpdates();
    }

    private void stopLocationUpdates(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 10) {
            if (data != null) {
                Uri uri = data.getData();
                postUriImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    activityWritePostBinding.imageView.setImageBitmap(bitmap);
                    checkSettingsAndStartLocationUpdates();
                    stopLocationUpdates();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 12) {
            if (data != null) {

                bitmap = (Bitmap) data.getExtras().get("data");
                postUriImage = getImageUri(bitmap);

                activityWritePostBinding.imageView.setImageBitmap(bitmap);
               // predict();
                checkSettingsAndStartLocationUpdates();

                stopLocationUpdates();

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 10001){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission granted
                //checkSettingsAndStartLocationUpdates();
            }
            else{
                //Permission not granted
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private String getFileExtension(Uri uriImage) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uriImage));
    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(WritePost.this);
        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);

        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //Settings of device are satisfied and we can start location updates
                startLocationUpdates();
            }
        });
        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(WritePost.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

    }
    private void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }



    public void UploadImage() {

        if (postUriImage != null) {

            activityWritePostBinding.progressBarPostBtn.setVisibility(View.VISIBLE);

            userID = firebaseUser.getUid();
            //Extracting User reference from database for "Users_new"
            DatabaseReference databaseReferenceCurr = FirebaseDatabase.getInstance().getReference("Users_new");
            databaseReferenceCurr.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UserDetails userDetails = snapshot.getValue(UserDetails.class);
                    if(userDetails != null){
                        userName = userDetails.userName;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(WritePost.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            });



//            storage = FirebaseStorage.getInstance();
//            StorageReference dc = storage.getReference().child("User").child(firebaseAuth.getCurrentUser().getUid());

            // Get the current date and time
            String currDateTime = getCurrentDateTime();
            //Toast.makeText(this, ProfileLink, Toast.LENGTH_SHORT).show();
            // Upload post data
            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(postUriImage));
            storageReference2.putFile(postUriImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Get the download URL of the uploaded image
                            storageReference2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    String imageURL = uri.toString();

                                    Toast.makeText(getApplicationContext(), "Post Updated", Toast.LENGTH_LONG).show();

                                    String ImageUploadId = databaseReference.push().getKey();

                                    String postProfileImageUrl = ProfileLink;

                                    String postText = activityWritePostBinding.postTextMain.getText().toString();

                                    PostDetailsModel imageUploadInfo = new PostDetailsModel(postProfileImageUrl, imageURL, userID, userName, currDateTime,  uploadLocation, 0, 0,postText);

                                    databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                                    activityWritePostBinding.progressBarPostBtn.setVisibility(View.GONE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle any errors that occurred while retrieving the download URL
                                    Toast.makeText(WritePost.this, "Failed to retrieve image download URL", Toast.LENGTH_SHORT).show();
                                    activityWritePostBinding.progressBarPostBtn.setVisibility(View.GONE);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle any errors that occurred while uploading the image
                            Toast.makeText(WritePost.this, "Image upload failed", Toast.LENGTH_SHORT).show();
                            activityWritePostBinding.progressBarPostBtn.setVisibility(View.GONE);
                        }
                    });



        }
        else {

            Toast.makeText(WritePost.this, "Handle URI image error!", Toast.LENGTH_LONG).show();
        }
    }


    private String getCurrentDateTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }


}