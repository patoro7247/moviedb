package edu.uci.ics.fabflixmobile.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.MainActivity;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView message;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    //OG: private final String host = "10.0.2.2";
    private final String host = "13.56.200.210";
    //OG: private final String port = "8080";
    private final String port = "8443";

    //OG : private final String domain = "cs122b_project1_api_example_war";
    private final String domain = "cs122b-project1-api-example";

    //This is the main app's server private final String domain = "cs122b_project1_api_example_war";
    //But we're just going to use the project2 login cart example server
    private final String baseURL = "https://" + host + ":" + port + "/" + domain;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot());

        username = binding.username;
        password = binding.password;
        message = binding.message;
        final Button loginButton = binding.login;

        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(view -> login());
    }

    @SuppressLint("SetTextI18n")
    public void login() {
        message.setText("Trying to login");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.POST,

                //TODO: REMEMBER I ALSO CHANGED URL TO BE MY PROJECT 1 AndroidLoginServlet.java endpoint
                baseURL + "/api/androidlogin",
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.
                    //RESPONSE "status" is "fail" if user doesn't exist

                    message.setText(response.toString());


                    if( response.toString().contains("incorrect password")){
                        message.setText("Incorrect Password");
                    }else if( response.toString().contains("does not exist")){
                        message.setText("Account does not exist");
                    } else{
                        Log.d("login.success", response);
                        Intent SearchPage = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(SearchPage);
                    }

                    //OG inside else statement
//                    Log.d("login.success", response);
//                    Intent MovieListPage = new Intent(LoginActivity.this, MovieListActivity.class);
//                    startActivity(MovieListPage);

                    //Log.d("login.success", response);
                    //Complete and destroy login activity once successful
                    //finish();
                    // initialize the activity(page)/destination
                    //Intent MovieListPage = new Intent(LoginActivity.this, MovieListActivity.class);
                    // activate the list page.
                    //startActivity(MovieListPage);
                },
                error -> {
                    // error
                    Log.d("login.error", error.toString());
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }
}