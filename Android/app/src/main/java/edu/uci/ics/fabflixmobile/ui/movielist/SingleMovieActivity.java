package edu.uci.ics.fabflixmobile.ui.movielist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.model.Movie;
import edu.uci.ics.fabflixmobile.databinding.ActivityMovielistBinding;
import edu.uci.ics.fabflixmobile.databinding.ActivitySinglemovieBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SingleMovieActivity extends AppCompatActivity {
    private TextView movieTitle;
    private TextView year;

    private TextView director;

    private TextView genres;

    private TextView stars;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySinglemovieBinding binding = ActivitySinglemovieBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //final Button prevButton = binding.previous;
        //final Button nextButton = binding.next;
        movieTitle = binding.movieTitle;
        year = binding.year;
        director = binding.director;
        genres = binding.genres;
        stars = binding.stars;

        Intent intent = getIntent();
        String intentTitle = intent.getStringExtra("title");
        int intentYear = intent.getIntExtra("year", 0);
        String intentDirector = intent.getStringExtra("director");
        String intentGenres = intent.getStringExtra("genres");
        String intentStars = intent.getStringExtra("stars");

        movieTitle.setText("Title: "+intentTitle);
        year.setText("Year: "+Integer.toString(intentYear));
        director.setText("Director: "+intentDirector);
        genres.setText("Genres: "+intentGenres);
        stars.setText("Stars: "+intentStars);



    }

}