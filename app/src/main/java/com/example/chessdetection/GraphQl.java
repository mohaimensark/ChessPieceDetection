package com.example.chessdetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.firebase.database.annotations.NotNull;

import okhttp3.OkHttpClient;

public class GraphQl extends AppCompatActivity {

    private static final String GRAPHQL_ENDPOINT = "https://graphql-pokeapi.vercel.app/api/graphql";

    private TextView idTextView;
    private TextView nameTextView;
    private TextView heightTextView;
    private TextView weightTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_ql);

    }
}