package com.example.chessdetection;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class RatingViewHolder extends RecyclerView.ViewHolder{
    CircleImageView UserImage;
    TextView UserName,Text, Cnt;
    public RatingViewHolder(@NonNull View itemView) {
        super(itemView);
        UserImage = itemView.findViewById(R.id.UserImage);
        UserName = itemView.findViewById(R.id.UserName);
        Text = itemView.findViewById(R.id.userFeedback);
        Cnt = itemView.findViewById(R.id.userOwnRating);
    }
}
