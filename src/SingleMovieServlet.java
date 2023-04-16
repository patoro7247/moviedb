import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbexample");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            // Construct a query with parameter represented by "?"
            String query1 = "SELECT * from stars as s, stars_in_movies as sim, movies as m " +
                    "where m.id = sim.movieId and sim.starId = s.id and s.id = ?";

            String query = "SELECT title, year, director, rating FROM movies as m, ratings as r WHERE m.id=? AND r.movieId=m.id";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, id);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {

                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");
                float rating = rs.getFloat("rating");


                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("rating", rating);
                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();


            //Write first query JSON string to output
            String query2 = "SELECT g.name FROM genres_in_movies as gim, genres as g WHERE gim.movieId=? AND gim.genreId=g.id";
            PreparedStatement statement2 = conn.prepareStatement(query2);
            statement2.setString(1, id);

            ResultSet rs2 = statement2.executeQuery();

            JsonObject jsonObject2 = new JsonObject();
            String genres = "";

            while(rs2.next()) {
                genres = genres + rs2.getString("name") + ", ";
            }
            if(genres.length() > 1) genres = genres.substring(0, genres.length()-2);


            jsonObject2.addProperty("genres", genres);
            rs2.close();
            statement2.close();

            jsonArray.add(jsonObject2);


            //STARS NOW
            String query3 = "SELECT s.id, s.name FROM stars as s, stars_in_movies as sim WHERE sim.movieId=? AND s.id=sim.starId";
            PreparedStatement statement3 = conn.prepareStatement(query3);
            statement3.setString(1, id);

            ResultSet rs3 = statement3.executeQuery();

            JsonArray jsonObject3 = new JsonArray();

            while(rs3.next()) {
                String starId = rs3.getString("id");
                String starName = rs3.getString("name");


                JsonObject actorItem = new JsonObject();
                actorItem.addProperty("id", starId);
                actorItem.addProperty("name", starName);

                jsonObject3.add(actorItem);
            }



            rs3.close();
            statement3.close();

            jsonArray.add(jsonObject3);

            // Write JSON string to output
            out.write(jsonArray.toString());

            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}
