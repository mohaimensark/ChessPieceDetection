package com.example.chessdetection.Comment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chessdetection.CommentItemAdapter;
import com.example.chessdetection.R;
import com.example.chessdetection.databinding.ActivityCommentBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class CommentActivity extends AppCompatActivity {
    ActivityCommentBinding activityCommentBinding;

    DatabaseReference userRef, commentRef;
    String postKey;
    String commentUserImage;
    CommentItemAdapter commentItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCommentBinding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(activityCommentBinding.getRoot());
        //getSupportActionBar().setTitle("Comment");


        postKey=getIntent().getStringExtra("postKey");

       // Toast.makeText(this, postKey, Toast.LENGTH_SHORT).show();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users_new");
        commentRef=FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("comments");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUserId = firebaseUser.getUid();

        activityCommentBinding.commentRecyclerviewId.setLayoutManager(new LinearLayoutManager(this));




        FirebaseRecyclerOptions<CommentModel> options =
                new FirebaseRecyclerOptions.Builder<CommentModel>()
                        .setQuery(commentRef, CommentModel.class)
                        .build();

        commentItemAdapter = new CommentItemAdapter(options);

        activityCommentBinding.commentRecyclerviewId.setAdapter(commentItemAdapter);


        String  userID = firebaseUser.getUid();
        DatabaseReference mDatabase;
// ...
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Extracting Profile from database for "Users"
        try {

            mDatabase.child("User").child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    }
                    else {
                        commentUserImage =  String.valueOf(task.getResult().getValue());
                        //   Toast.makeText(WritePost.this, ProfileLink, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            // Handle any exceptions that occur
            commentUserImage = "null";
        }

        activityCommentBinding.commentSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            String commentUserName = snapshot.child("userName").getValue().toString();



                            processComment(commentUserName,commentUserImage, currentUserId);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // Getting profile image
                String  userID = firebaseUser.getUid();

                //Extracting User reference from database for "Users"
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
                databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ImageCollector userDetails = snapshot.getValue(ImageCollector.class);
                        if(snapshot != null){
                            commentUserImage = snapshot.getValue().toString();
                            //           Toast.makeText(WritePost.this, ProfileLink, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(CommentActivity.this, "Null", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

    }

    private void processComment(String commentUserName, String commentUserImage, String currentUserId) {

        String commentText = activityCommentBinding.editTextComment.getText().toString();
        String randomPostKey = currentUserId +""+new Random().nextInt(1000);

        Calendar dateValue = Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-mm-yy");
        String currDate=dateFormat.format(dateValue.getTime());

        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm");
        String currTime=timeFormat.format(dateValue.getTime());

        HashMap commentDetails=new HashMap();
        commentDetails.put("commentUserid",currentUserId);
        commentDetails.put("commentUserName",commentUserName);
        commentDetails.put("commentUserImage",commentUserImage);
        commentDetails.put("commentText",commentText);
        commentDetails.put("commentDate",currDate);
        commentDetails.put("commentTime",currTime);

        commentRef.child(randomPostKey).updateChildren(commentDetails)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Comment Added", Toast.LENGTH_LONG).show();
                            activityCommentBinding.editTextComment.setText("");
                        }
                        else {
                            Toast.makeText(getApplicationContext(), task.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }


    @Override
    protected void onStart() {
        super.onStart();
        commentItemAdapter.startListening();
    }
}