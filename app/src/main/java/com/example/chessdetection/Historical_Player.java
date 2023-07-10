package com.example.chessdetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Historical_Player extends AppCompatActivity {

    private static final String API_URL = "https://api.coinlore.net/api/tickers/";
    private List<Coin> coinList;
    private CoinAdapter coinAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_player);

        ListView listView = findViewById(R.id.listView2);
        coinList = new ArrayList<>();
        coinAdapter = new CoinAdapter(this, coinList);
        listView.setAdapter(coinAdapter);

        fetchData();
    }

    private void fetchData() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray coinArray = response.getJSONArray("data");
                            for (int i = 0; i < 100; i++) {
                                JSONObject coinObject = coinArray.getJSONObject(i);
                                String symbol = coinObject.getString("symbol");
                                String name = coinObject.getString("name");
                                int rank = coinObject.getInt("rank");
                                double priceUSD = coinObject.getDouble("price_usd");

                                Coin coin = new Coin(symbol, name, rank, priceUSD);
                                coinList.add(coin);
                            }
                            coinAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(Historical_Player.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Historical_Player.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }


}