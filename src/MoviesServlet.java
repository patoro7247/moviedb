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
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/movies"
@WebServlet(name = "MoviesServlet", urlPatterns = "/api/movies")
public class MoviesServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbexample");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {

            // Declare our statement
            Statement statement = conn.createStatement();

            String query = "SELECT m.id, m.title, m.year, m.director, r.rating FROM movies as m, ratings as r WHERE m.id=r.movieId ORDER BY r.rating DESC LIMIT 20";
            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            JsonArray jsonArray = new JsonArray();

            // Iterate through each row of rs
            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                int year = rs.getInt("year");
                String director = rs.getString("director");
                float rating = rs.getFloat("rating");

                // Create a JsonObject based on the data we retrieve from rs
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("rating", rating);

                //Make genre list for each one

                JsonArray genreList = new JsonArray();

                //Statement statement2 = conn.createStatement();
                String genreQuery = "SELECT g.name FROM genres as g, genres_in_movies as gim, movies as m WHERE g.id=gim.genreId AND m.id=? AND m.id=gim.movieId LIMIT 3";
                PreparedStatement statement2 = conn.prepareStatement(genreQuery);
                statement2.setString(1, id);

                ResultSet rs2 = statement2.executeQuery();
                while(rs2.next()) {
                    String genreTitle = rs2.getString("name");
                    JsonObject genre = new JsonObject();
                    genre.addProperty("genreName", genreTitle);
                    genreList.add(genre);


                }
                jsonObject.add("genres",genreList);

                rs2.close();
                statement2.close();


                //Stars NOW
                JsonArray starList = new JsonArray();

                String starsQuery = "SELECT s.name FROM stars_in_movies as sim, stars as s, movies as m WHERE sim.movieId=m.id AND sim.starId=s.id AND m.id=? LIMIT 3 ";
                PreparedStatement statement3 = conn.prepareStatement(starsQuery);
                statement3.setString(1, id);

                ResultSet rs3 = statement3.executeQuery();
                while(rs3.next()) {
                    String name = rs3.getString("name");
                    JsonObject starName = new JsonObject();
                    starName.addProperty("name", name);
                    starList.add(starName);


                }
                jsonObject.add("stars", starList);

                rs3.close();
                statement3.close();


                jsonArray.add(jsonObject);
            }




            rs.close();
            statement.close();

            // Log to localhost log
            request.getServletContext().log("getting " + jsonArray.size() + " results");

            // Write JSON string to output
            out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

        } catch (Exception e) {

            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
