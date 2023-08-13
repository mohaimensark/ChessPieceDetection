package com.example.chessdetection.Post;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chessdetection.Authentication_UserInfo_Sessions.UserDetails;
import com.example.chessdetection.R;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class WritePost extends AppCompatActivity {

    ActivityWritePostBinding activityWritePostBinding;

    private String userID, userName, predictionResult, uploadLocation,predictionResult1;

    Bitmap bitmap;
    private Uri postUriImage;

    Button selectBtn, captureBtn;
    ImageView imageView;
    TextView result;
    Bitmap image;

    FirebaseStorage storage;
    TextView back;
    int imageSize = 224 ;
    String[] infoArr = new String[] {"User", "Location", "Date", "Time"};
    String[] userInfo = new String[5];
    String fullName;

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
        activityWritePostBinding.GenerateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReport();
            }
        });
        // Getting profile image
        String  userID = firebaseUser.getUid();

        //Extracting Profile from database for "Users"
        try {

            if (firebaseUser.getPhotoUrl() != null) {
                ProfileLink = firebaseUser.getPhotoUrl().toString();
            } else {
                // Set the profile image from a drawable resource
                ProfileLink = "android.resources://" + getPackageName() + "/" + R.drawable.ic_image_24;
            }
        } catch (Exception e) {
            // Handle any exceptions that occur
            ProfileLink = "EMpTY";
        }
        enablePdfButton();
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
               //     activityWritePostBinding.textViewLocationAddress.setText(addressList.get(0).getAddressLine(0));
                    Toast.makeText(WritePost.this, "your current location is"+addressList.get(0).getAddressLine(0), Toast.LENGTH_SHORT).show();
                    stopLocationUpdates();
                   // enablePdfButton();
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
    private void enablePdfButton(){

        String userID = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users_new");
        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails = snapshot.getValue(UserDetails.class);
                if(userDetails != null){
                    fullName = userDetails.getUserName();
                    userInfo[0] = fullName;
                    activityWritePostBinding.GenerateBtn.setEnabled(true);
                };
                Toast.makeText(WritePost.this, "Enabled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WritePost.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void createReport() {
        // PDF generator start
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();


      //  Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
        // Create page
        float pageWidthInInches = 8.27f; // A4 page width in inches
        float pageHeightInInches = 11.69f; // A4 page height in inches
        int pageWidth = (int) (pageWidthInInches * 72); // Convert inches to points (1 inch = 72 points)
        int pageHeight = (int) (pageHeightInInches * 72); // Convert inches to points (1 inch = 72 points)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page page1 = pdfDocument.startPage(pageInfo);
        Canvas canvas = page1.getCanvas();

        //Get current Date time
        Calendar dateValue = Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yy");
        String currDate=dateFormat.format(dateValue.getTime());
        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
        String currTime=timeFormat.format(dateValue.getTime());



        userInfo[1] = uploadLocation;
        userInfo[2] = currDate;
        userInfo[3] = currTime;





        // Draw text and lines
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(22f);
        paint.setColor(Color.rgb(22, 30, 126));
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("Chess Detection", pageInfo.getPageWidth() / 2, 72, paint);

       // Toast.makeText(this, "C", Toast.LENGTH_SHORT).show();
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(16f);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.SANS_SERIF);

        int startXPos = 72, startYPos = 90, endXPos = pageInfo.getPageWidth() - 72;
        startYPos += 25;
        for (int i = 0; i < 4; i++) {
            startYPos += 10;
            if(i==0){
                paint.setColor(Color.rgb(22, 30, 126));
            }
            canvas.drawText(infoArr[i], startXPos + 5, startYPos, paint);
            canvas.drawText(": ", startXPos + 80, startYPos, paint);

            // Handle multiline text using StaticLayout
            String userInfoText = userInfo[i];
            int textWidth = pageInfo.getPageWidth() - (startXPos + 100); // Adjust the text width as needed

            // Create a TextPaint object for text rendering
            TextPaint textPaint = new TextPaint();
            textPaint.set(paint);

            // Create a StaticLayout instance for the multiline text
            StaticLayout staticLayout = new StaticLayout(userInfoText, textPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

            int textHeight = staticLayout.getHeight();
            canvas.save();
            canvas.translate(startXPos + 90, startYPos - 15); // Adjust the starting position as needed
            staticLayout.draw(canvas);
            canvas.restore();

            // Update the startYPos to account for the total height of the multiline text
            startYPos += textHeight;

            startYPos += 5;
        }

      //  Toast.makeText(this, "B", Toast.LENGTH_SHORT).show();
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(18f);
        paint.setColor(Color.rgb(22, 30, 126));
        startYPos += 20;
        canvas.drawText("Captured/Uploaded Image", pageInfo.getPageWidth()/2, startYPos, paint);

        // Draw image
        Rect imageRect = new Rect(100, startYPos + 10, pageInfo.getPageWidth() - 100, 650); // Define the position and size of the image
        canvas.drawBitmap(bitmap, null, imageRect, null);

        canvas.drawLine(startXPos, pageInfo.getPageHeight() -72, pageInfo.getPageWidth()-72, pageInfo.getPageHeight() -72, paint);

        startYPos = pageInfo.getPageHeight() - 72;

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(10f);
        paint.setColor(Color.BLACK);

        startYPos += 15;
        canvas.drawText("Mohaimen Sarker,", startXPos, startYPos, paint);
        canvas.drawText("CSE, University of Chittagong", startXPos, startYPos + 15, paint);
  //      Toast.makeText(this, "A", Toast.LENGTH_SHORT).show();
        // Finish page
        pdfDocument.finishPage(page1);

        // Save the PDF document
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "Information.pdf");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);

        try {
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            pdfDocument.writeTo(outputStream);
            pdfDocument.close();
            outputStream.close();
            Toast.makeText(WritePost.this, "PDF saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(WritePost.this, "Failed to save PDF", Toast.LENGTH_SHORT).show();
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
             //   predict();
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