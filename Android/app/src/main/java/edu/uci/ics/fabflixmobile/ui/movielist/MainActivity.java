package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

import android.widget.TextView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;


import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.databinding.ActivityMainBinding;
import edu.uci.ics.fabflixmobile.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText searchQuery;
    private TextView message;

    private TextView newMessage;


    private final String host = "13.56.200.210";
    private final String port = "8443";
    //OG: private final String domain = "cs122b_project2_login_cart_example_war";
    private final String domain = "cs122b-project1-api-example";

    //This is the main app's server private final String domain = "cs122b_project1_api_example_war";
    //But we're just going to use the project2 login cart example server
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchQuery = binding.searchQuery;

        final Button searchButton = binding.search;
        message = binding.message;
        newMessage = binding.newMessage;

        searchButton.setOnClickListener(view -> search());
    }

    @SuppressLint("SetTextI18n")
    public void test() {
        newMessage.setText(searchQuery.getText().toString());
    }

    @SuppressLint("SetTextI18n")
    public void search(){
        //TODO: We were able to get the search query response successfully with
        // all id, title, director, rating, etc.
        // Now, pass this data to the MovieListActivity using the Intent.addPackage or whatever

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, baseURL+"/api/results?title="+searchQuery.getText().toString()+"&year=&director=&star=&offset=0&max=10", null, new Response.Listener<JSONArray>() {
                //title=godfather&year=&director=&star=&offset=0&max=25
                    @Override
                    public void onResponse(JSONArray response) {
                        newMessage.setText("Response: " + response.toString());
                        Intent MovieListPage = new Intent(MainActivity.this, MovieListActivity.class);
                        MovieListPage.putExtra("movies", response.toString() );
                        MovieListPage.putExtra("searchQuery", baseURL+"/api/results?title="+searchQuery.getText().toString()+"&year=&director=&star=&offset=0&max=10");
                        MovieListPage.putExtra("offset", 0);

                        startActivity(MovieListPage);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        newMessage.setText("Error: " + error.toString());

                    }
                });

        queue.add(jsonArrayRequest);

    }
//  use intent.putExtra("jsonArray", mJsonArray.toString()
//    @SuppressLint("SetTextI18n")
//    public void search() {
//        message.setText("Trying to search");
//        // use the same network queue across our application
//        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
//        // request type is POST
//        final StringRequest loginRequest = new StringRequest(
//                Request.Method.POST,
//
//                //TODO: REMEMBER I ALSO CHANGED URL TO BE MY PROJECT 1 AndroidLoginServlet.java endpoint
//                baseURL + "/api/androidlogin",
//                response -> {
//                    // TODO: should parse the json response to redirect to appropriate functions
//                    //  upon different response value.
//                    //RESPONSE "status" is "fail" if user doesn't exist
//
//                    message.setText(response.toString());
//
//
//                    if( response.toString().contains("fail")){
//                        message.setText(response.toString());
//                    }else{
//                        Log.d("login.success", response);
//                        Intent SearchPage = new Intent(LoginActivity.this, MainActivity.class);
//                        startActivity(SearchPage);
//                    }
//
//                    //OG inside else statement
//    //                    Log.d("login.success", response);
//    //                    Intent MovieListPage = new Intent(LoginActivity.this, MovieListActivity.class);
//    //                    startActivity(MovieListPage);
//
//                    //Log.d("login.success", response);
//                    //Complete and destroy login activity once successful
//                    //finish();
//                    // initialize the activity(page)/destination
//                    //Intent MovieListPage = new Intent(LoginActivity.this, MovieListActivity.class);
//                    // activate the list page.
//                    //startActivity(MovieListPage);
//                },
//                error -> {
//                    // error
//                    Log.d("login.error", error.toString());
//                }) {
//            @Override
//            protected Map<String, String> getParams() {
//                // POST request form data
//                final Map<String, String> params = new HashMap<>();
//                params.put("searchQuery", searchQuery.getText().toString());
//                return params;
//            }
//        };
//        // important: queue.add is where the login request is actually sent
//        queue.add(loginRequest);
//    }
}