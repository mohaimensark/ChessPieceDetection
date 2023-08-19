package com.example.chessdetection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class RatingAdapter extends FirebaseRecyclerAdapter<RatingModel, RatingViewHolder> {

    public RatingAdapter(@NonNull FirebaseRecyclerOptions<RatingModel> options) {
        super(options);
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ratinglistitem, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull RatingViewHolder holder, int position, @NonNull RatingModel model) {
        if(model.getUserImage()!="null"){
            Glide.with(holder.UserImage.getContext()).load(model.getUserImage()).into(holder.UserImage);
            holder.UserName.setText(model.getUserName());
            holder.Text.setText(String.valueOf(model.getText()));
            holder.Cnt.setText(String.valueOf(model.getGivenRating()));
        }else{
            Glide.with(holder.UserImage.getContext()).load(model.getUserImage()).placeholder(R.drawable.ic_person_24).into(holder.UserImage);
            holder.UserName.setText(model.getUserName());
            holder.Text.setText(String.valueOf(model.getText()));
            holder.Cnt.setText(String.valueOf(model.getGivenRating()));
        }

    }
}