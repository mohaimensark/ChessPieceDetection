package com.example.chessdetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.chessdetection.Post.WritePost;
import com.example.chessdetection.databinding.ActivityRatingBinding;
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
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class RatingActivity extends AppCompatActivity {

    ActivityRatingBinding activityRatingBinding;

    FirebaseUser firebaseUser;
    String ratingUserImage;
    DatabaseReference ratingRef, ratingUserRef, ratingDel, ratingRef2, ratingRef3, ratingRef4, ratingDetailsRef;
    RatingAdapter ratingListAdapter;;


    int userCount, newTot, curval;
    boolean gStar1, gStar2, gStar3, gStar4, gStar5;
    Boolean clickTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        activityRatingBinding = ActivityRatingBinding.inflate(getLayoutInflater());
        setContentView(activityRatingBinding.getRoot());

        //Storing into realtime database
        ratingUserRef = FirebaseDatabase.getInstance().getReference().child("Ratings").child("RatedUser");





        ratingUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int usrCount = (int) snapshot.getChildrenCount();
                userCount = usrCount;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // Retrieve the totalRating value
        ratingRef = FirebaseDatabase.getInstance().getReference().child("Ratings");
        ratingRef.child("totalRating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Integer totalRating = dataSnapshot.getValue(Integer.class);
                    if (totalRating != null) {
                        // Use the totalRating value
                        int rating = totalRating.intValue();

                        float appRatings = (float) rating / userCount;
                        String formatRating = String.format(Locale.getDefault(), "%.1f", appRatings);
                        activityRatingBinding.ratingAppId.setText("App rating: " + formatRating);

                    } else {
                        activityRatingBinding.ratingAppId.setText("App rating: 0.0");
                    }
                } else {
                    activityRatingBinding.ratingAppId.setText("App rating: 0.0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors
            }
        });
        //Set rating List adapter
        ratingDetailsRef = FirebaseDatabase.getInstance().getReference().child("Ratings").child("RatingDetails");
        activityRatingBinding.recyclerViewRating.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<RatingModel> options =
                new FirebaseRecyclerOptions.Builder<RatingModel>()
                        .setQuery(ratingDetailsRef, RatingModel.class)
                        .build();

        ratingListAdapter = new RatingAdapter(options);
        activityRatingBinding.recyclerViewRating.setAdapter(ratingListAdapter);
       // end others user rating

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = firebaseUser.getUid();

        String userId2 = firebaseUser.getUid();
        clickTest = true;

        ratingDel = FirebaseDatabase.getInstance().getReference().child("Ratings").child("RatedUser");
        ratingRef = FirebaseDatabase.getInstance().getReference().child("Ratings").child("RatedUser");


        gStar1 = false;
        gStar2 = false;
        gStar3 = false;
        gStar4 = false;
        gStar5 = false;

        activityRatingBinding.star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickTest) {
                    activityRatingBinding.star1.setImageResource(R.drawable.ic_baseline_star_rate);
                    activityRatingBinding.star2.setImageResource(R.drawable.ic_baseline_starbefore);
                    activityRatingBinding.star3.setImageResource(R.drawable.ic_baseline_starbefore);
                    activityRatingBinding.star4.setImageResource(R.drawable.ic_baseline_starbefore);
                    activityRatingBinding.star5.setImageResource(R.drawable.ic_baseline_starbefore);

                    activityRatingBinding.buttonSubmit.setEnabled(true);
                    gStar1 = true;
                    gStar2 = false;
                    gStar3 = false;
                    gStar4 = false;
                    gStar5 = false;
                }
            }
        });

        activityRatingBinding.star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickTest) {
                    activityRatingBinding.star1.setImageResource(R.drawable.ic_baseline_star_rate);
                    activityRatingBinding.star2.setImageResource(R.drawable.ic_baseline_star_rate);
                    activityRatingBinding.star3.setImageResource(R.drawable.ic_baseline_starbefore);
                    activityRatingBinding.star4.setImageResource(R.drawable.ic_baseline_starbefore);
                    activityRatingBinding.star5.setImageResource(R.drawable.ic_baseline_starbefore);

                    activityRatingBinding.buttonSubmit.setEnabled(true);
                    gStar1 = false;
                    gStar2 = true;
                    gStar3 = false;
                    gStar4 = false;
                    gStar5 = false;
                }

            }
        });

        activityRatingBinding.star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickTest) {
                    activityRatingBinding.star1.setImageResource(R.drawable.ic_baseline_star_rate);
                    activityRatingBinding.star2.setImageResource(R.drawable.ic_baseline_star_rate);
                    activityRatingBinding.star3.setImageResource(R.drawable.ic_baseline_star_rate);
                    activityRatingBinding.star4.setImageResource(R.drawable.ic_baseline_starbefore);
                    activityRatingBinding.star5.setImageResource(R.drawable.ic_baseline_starbefore);

                    activityRatingBinding.buttonSubmit.setEnabled(true);
                    gStar1 = false;
                    gStar2 = false;
                    gStar3 = true;
                    gStar4 = false;
                    gStar5 = false;
                }

            }
        });

        activityRatingBinding.star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (clickTest) {
                    activityRatingBinding.star1.setImageResource(R.drawable.ic_baseline_star_rate);
                    activityRatingBinding.star2.setImageResource(R.drawable.ic_baseline_star_rate);
                    activityRatingBinding.star3.setImageResource(R.drawable.ic_baseline_star_rate);
                    activityRatingBinding.star4.setImageResource(R.drawable.ic_baseline_star_rate);
                    activityRatingBinding.star5.setImageResource(R.drawable.ic_baseline_starbefore);

                    activityRatingBinding.buttonSubmit.setEnabled(true);
                    gStar1 = false;
                    gStar2 = false;
                    gStar3 = false;
                    gStar4 = true;
                    gStar5 = false;
                }

            }
        });

        activityRatingBinding.star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickTest) {
                    activityRatingBinding.star1.setImageResource(R.drawable.ic_baseline_star_rate);
                    activityRatingBinding.star2.setImageResource(R.drawable.ic_baseline_star_rate);
                    activityRatingBinding.star3.setImageResource(R.drawable.ic_baseline_star_rate);
                    activityRatingBinding.star4.setImageResource(R.drawable.ic_baseline_star_rate);
                    activityRatingBinding.star5.setImageResource(R.drawable.ic_baseline_star_rate);

                    activityRatingBinding.buttonSubmit.setEnabled(true);
                    gStar1 = false;
                    gStar2 = false;
                    gStar3 = false;
                    gStar4 = false;
                    gStar5 = true;
                }

            }
        });


        activityRatingBinding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gStar1) {
                    updateRating(userId, 1);
                } else if (gStar2) {
                    updateRating(userId, 2);
                } else if (gStar3) {
                    updateRating(userId, 3);
                } else if (gStar4) {
                    updateRating(userId, 4);
                } else if (gStar5) {
                    updateRating(userId, 5);
                }
            }
        }); // Getting profile image
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
                        ratingUserImage =  String.valueOf(task.getResult().getValue());
                  //      Toast.makeText(RatingActivity.this, ratingUserImage, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            // Handle any exceptions that occur
            ratingUserImage = "null";
        }
    }


    private void updateRating(String userId, int giRating) {


        ratingRef = FirebaseDatabase.getInstance().getReference().child("Ratings");
        ratingRef2 = FirebaseDatabase.getInstance().getReference().child("Ratings").child("RatedUser").child(userId);
        ratingRef3 = FirebaseDatabase.getInstance().getReference().child("Ratings").child("totalRating");


        ratingRef.child("RatedUser").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String num = snapshot.getValue().toString();
                    curval = Integer.parseInt(num);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ratingRef.child("RatedUser").child(userId).setValue(giRating).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RatingActivity.this, "Rating added", Toast.LENGTH_SHORT).show();
                    // Perform any additional actions here
                } else {
                    // Failed to add child node
                    // Handle the error
                }
            }
        });

        // new approach


        ratingRef2 = FirebaseDatabase.getInstance().getReference().child("Ratings").child("RatedUser");

        ratingRef2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                int tp = (int) snapshot.getChildrenCount();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String lol = ds.getValue().toString();
                    curval = Integer.parseInt(lol);

                    total += curval;
                }
               // Toast.makeText(RatingActivity.this, tp, Toast.LENGTH_SHORT).show();
                float appRatings = (float) total / tp;

                String formatRating = String.format(Locale.getDefault(), "%.1f", appRatings);
                // Update the UI with the current rating
                activityRatingBinding.ratingAppId.setText("App rating: " + formatRating);
         //       activityRatingBinding.buttonSubmit.setEnabled(false);

                activityRatingBinding.star1.setImageResource(R.drawable.ic_baseline_starbefore);
                activityRatingBinding.star2.setImageResource(R.drawable.ic_baseline_starbefore);
                activityRatingBinding.star3.setImageResource(R.drawable.ic_baseline_starbefore);
                activityRatingBinding.star4.setImageResource(R.drawable.ic_baseline_starbefore);
                activityRatingBinding.star5.setImageResource(R.drawable.ic_baseline_starbefore);

                ratingRef.child("totalRating").setValue(total)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Total rating updated successfully
                                    // Perform any additional actions here
                                } else {
                                    // Failed to update total rating
                                    // Handle the error
                                }
                            }
                        });
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
        ratingUserRef = FirebaseDatabase.getInstance().getReference().child("Users_new");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUserId = firebaseUser.getUid();

        ratingUserRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String ratingUserName = snapshot.child("userName").getValue().toString();



              //      Toast.makeText(RatingActivity.this, ratingUserImage, Toast.LENGTH_SHORT).show();
                        processRating(ratingUserName,ratingUserImage, currentUserId, giRating);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void processRating(String ratUserName, String ratUserImage, String currentUserId, int givenRat) {

        String ratingText = activityRatingBinding.ratingFeedback.getText().toString();
        String randomRatKey = currentUserId;

        HashMap ratingDetails=new HashMap();
        ratingDetails.put("Userid",currentUserId);
        ratingDetails.put("UserName",ratUserName);
        ratingDetails.put("UserImage",ratUserImage);
        ratingDetails.put("Text",ratingText);
        ratingDetails.put("givenRating", givenRat);

        ratingDetailsRef = FirebaseDatabase.getInstance().getReference().child("Ratings").child("RatingDetails");

        ratingDetailsRef.child(randomRatKey).updateChildren(ratingDetails)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Rating Added", Toast.LENGTH_LONG).show();
                            activityRatingBinding.ratingFeedback.setText("");
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
        ratingListAdapter.startListening();
    }

}

















//
//        // Retrieve the totalRating value
//        ratingRef.child("totalRating").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    Integer totalRating = dataSnapshot.getValue(Integer.class);
//                    if (totalRating != null) {
//                        Toast.makeText(RatingActivity.this, curval, Toast.LENGTH_SHORT).show();
//                        // Use the totalRating value
//                        int rating = totalRating.intValue();
//                        newTot = rating + giRating-curval;
//
//                    }else{
//                        newTot = giRating;
//                    }
//                }else{
//                    newTot = giRating;
//                }
//
//                ratingUserRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        int usrCount = (int)snapshot.getChildrenCount();
//                        userCount = usrCount;
//
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//                float appRatings = (float) newTot / userCount;
//                String formatRating = String.format(Locale.getDefault(), "%.1f", appRatings);
//                // Update the UI with the current rating
//                activityRatingBinding.ratingAppId.setText("App rating: " + formatRating);
//         //       activityRatingBinding.buttonSubmit.setEnabled(false);
//
//                activityRatingBinding.star1.setImageResource(R.drawable.ic_baseline_starbefore);
//                activityRatingBinding.star2.setImageResource(R.drawable.ic_baseline_starbefore);
//                activityRatingBinding.star3.setImageResource(R.drawable.ic_baseline_starbefore);
//                activityRatingBinding.star4.setImageResource(R.drawable.ic_baseline_starbefore);
//                activityRatingBinding.star5.setImageResource(R.drawable.ic_baseline_starbefore);
//
//                ratingRef.child("totalRating").setValue(newTot)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    // Total rating updated successfully
//                                    // Perform any additional actions here
//                                } else {
//                                    // Failed to update total rating
//                                    // Handle the error
//                                }
//                            }
//                        });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // Handle any errors
//            }
//        });
//
//    }