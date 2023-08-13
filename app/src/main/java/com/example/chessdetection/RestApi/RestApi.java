package com.example.chessdetection.RestApi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.chessdetection.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class RestApi extends AppCompatActivity {

    private static final String API_URL = "https://api.coinlore.net/api/tickers/";
    private List<Coin> coinList;
    private CoinAdapter coinAdapter;
    private int currentPage = 0; // Current page number for pagination

    // Views
    private Button currentPageButton;
    private Button nextPageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_api);

        ListView listView = findViewById(R.id.listView2);
        currentPageButton = findViewById(R.id.currentPageButton);
        nextPageButton = findViewById(R.id.nextPageButton);

        coinList = new ArrayList<>();
        coinAdapter = new CoinAdapter(this, coinList);
        listView.setAdapter(coinAdapter);

        // Implement pagination logic when scrolling to the end of the list
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0) {
                    // Load next page when scrolling to the end of the list
//                      currentPage++;
//                      fetchData();
                }
            }
        });

        // Handle "Previous" button click
        Button prevButton = findViewById(R.id.prevButton);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((currentPage-5) >= 0) {
                    currentPage-=5;
                    fetchData();
                }else{
                    if(currentPage>0){
                        currentPage=0;
                        fetchData();
                    }
                }

            }
        });

        // Handle "Next" button click
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage+=5;
                fetchData();
            }
        });

        // Handle "Show Current Page" button click
        currentPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RestApi.this, "Current Page: " + currentPage, Toast.LENGTH_SHORT).show();
            }
        });

        // Handle "Show Next Page" button click
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage >= 0) {
                    currentPage+=5;
                    fetchData();
                }
            }
        });

       if(currentPage==0)
       {
           fetchData();
       }
    }

    private void fetchData() {
        // Calculate the starting and ending page numbers to fetch data
        int startPage = currentPage;
        int endPage = currentPage + 5;

        // Ensure the starting and ending page numbers are valid
        if (startPage < 0) {
            startPage = 0;
        }

        // Construct the API URL with pagination parameters
        String urlWithPagination = API_URL + "?start=" + startPage + "&end=" + endPage;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlWithPagination, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray coinArray = response.getJSONArray("data");
                            coinList.clear(); // Clear the list before adding new data
                            for (int i = 0; i < coinArray.length(); i++) {
                                JSONObject coinObject = coinArray.getJSONObject(i);
                                String symbol = coinObject.getString("symbol");
                                String name = coinObject.getString("name");
                                int rank = coinObject.getInt("rank");
                                double priceUSD = coinObject.getDouble("price_usd");

                                Coin coin = new Coin(symbol, name, rank, priceUSD);
                                coinList.add(coin);
                            }
                            coinAdapter.notifyDataSetChanged();
                            updatePageTextViews();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RestApi.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RestApi.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }

    private void updatePageTextViews() {
        currentPageButton.setText("" + ((currentPage)/5));
        nextPageButton.setText("" + (((currentPage)/5)+1));
    }
}
