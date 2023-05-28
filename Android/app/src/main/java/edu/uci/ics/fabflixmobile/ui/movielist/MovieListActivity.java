package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;

import java.util.ArrayList;

import edu.uci.ics.fabflixmobile.databinding.ActivityMainBinding;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import edu.uci.ics.fabflixmobile.databinding.ActivityMovielistBinding;


public class MovieListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_movielist);
        // TODO: this should be retrieved from the backend server
        final ArrayList<Movie> movies = new ArrayList<>();

        ActivityMovielistBinding binding = ActivityMovielistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Button prevButton = binding.previous;
        final Button nextButton = binding.next;

        Intent intent = getIntent();
        String moviesString = intent.getStringExtra("movies");
        JSONArray moviesArray = new JSONArray();

        try {
            moviesArray = new JSONArray(moviesString);
            //System.out.println(moviesArray.toString(2));

            for(int i = 0; i < moviesArray.length(); i++){
                JSONObject mov = moviesArray.getJSONObject(i);
                String name = mov.getString("title");
                short year = (short) mov.getInt("year");
                String director = mov.getString("director");

                //concatenate all genres into a string
                String genreString = "";
                for(int j = 0; j < mov.getJSONArray("genres").length(); j++){
                    JSONObject genreObject = mov.getJSONArray("genres").getJSONObject(j);
                    genreString += genreObject.getString("genreName") + ", ";

                }
                //if(genreString.length() > 1) genreString = genreString.substring(0,genreString.length()-1);

                //concatenate all stars into string
                String starString = "";
                for(int k = 0; k < mov.getJSONArray("stars").length(); k++){
                    JSONObject starObject = mov.getJSONArray("stars").getJSONObject(k);
                    starString += starObject.getString("name") + ", ";
                }
                //if(starString.length() > 1) starString = starString.substring(0, genreString.length()-1);

                movies.add(new Movie(name, year, director, genreString, starString));
            }

            //movies.add(new Movie("The Terminal", (short) 2004, "", "", ""));
            //movies.add(new Movie("The Final Season", (short) 2007, "", "", ""));
            MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
            ListView listView = findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Movie movie = movies.get(position);
                @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                Intent SingleMoviePage = new Intent(MovieListActivity.this, SingleMovieActivity.class);
                SingleMoviePage.putExtra("title", movie.getName());
                SingleMoviePage.putExtra("year", movie.getYear());
                SingleMoviePage.putExtra("director", movie.getDirector());
                SingleMoviePage.putExtra("genres", movie.getGenreString());
                SingleMoviePage.putExtra("stars", movie.getStarString());

                startActivity(SingleMoviePage);
            });

            //prevButton.setOnClickListener(view -> previous());
            nextButton.setOnClickListener(view -> next());
            prevButton.setOnClickListener(view -> previous());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void next(){
        //should take searchQuery, add 10 to offset
        Intent intent = getIntent();

        // new Query holds the original URL from our search; add 10 to offset
        //http://10.0.2.2:8080/cs122b_project1_api_example_war/api/results?title=s lov&year=&director=&star=&offset=0&max=10
        String newQuery = intent.getStringExtra("searchQuery");
        int offset = intent.getIntExtra("offset", 0);
        offset += 10;

        String shortEnding = "";
        if( newQuery.contains("offset=0&")) {
            shortEnding = newQuery.substring(0, newQuery.length() - 8) + Integer.toString(offset) + "&max=10";
        }else{
            shortEnding = newQuery.substring(0, newQuery.length() - 9) + Integer.toString(offset) + "&max=10";
        }

        final String updatedQuery = shortEnding;

        System.out.println("Proper updated next query: "+updatedQuery);

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        //
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, updatedQuery, null, new Response.Listener<JSONArray>() {
                    //title=godfather&year=&director=&star=&offset=0&max=25
                    @Override
                    public void onResponse(JSONArray response) {
                        //newMessage.setText("Response: " + response.toString());
                        Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
                        MovieListPage.putExtra("movies", response.toString() );
                        MovieListPage.putExtra("searchQuery", updatedQuery);
                        int newOffset = intent.getIntExtra("offset", 0) + 10;
                        MovieListPage.putExtra("offset", newOffset);

                        startActivity(MovieListPage);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        System.out.println("Error: " + error.toString());
                    }
                });
        queue.add(jsonArrayRequest);
    }

    public void previous(){
        Intent intent = getIntent();
        int offset = intent.getIntExtra("offset", 0);
        if(offset == 0){
            System.out.println("Offset is 0!");
            return;
        }

        offset = offset - 10;

        String newQuery = intent.getStringExtra("searchQuery");
        final String prevQuery = newQuery.substring(0, newQuery.length() - 9) + Integer.toString(offset) + "&max=10";
        System.out.println("Proper updated previous query: "+prevQuery);

        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, prevQuery, null, new Response.Listener<JSONArray>() {
                    //title=godfather&year=&director=&star=&offset=0&max=25
                    @Override
                    public void onResponse(JSONArray response) {
                        //newMessage.setText("Response: " + response.toString());
                        Intent MovieListPage = new Intent(MovieListActivity.this, MovieListActivity.class);
                        MovieListPage.putExtra("movies", response.toString() );
                        MovieListPage.putExtra("searchQuery", prevQuery);
                        int newOffset = intent.getIntExtra("offset", 0) - 10;
                        MovieListPage.putExtra("offset", newOffset);

                        startActivity(MovieListPage);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        System.out.println("Error: " + error.toString());
                    }
                });
        queue.add(jsonArrayRequest);



    }

}