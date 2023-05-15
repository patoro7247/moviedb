import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/_dashboard/api/addmovie")
public class AddMovieServlet extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    private static final long serialVersionUID = 2L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbexample");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String movieTitle = request.getParameter("movieTitle");
        System.out.println(movieTitle);

        int movieYear = Integer.parseInt(request.getParameter("movieYear"));
        String director = request.getParameter("director");
        String starName = request.getParameter("starName");
        String genre = request.getParameter("genre");

        JsonObject responseJsonObject = new JsonObject();

        PrintWriter out = response.getWriter();
        //set up database connection and check credentials
        try (Connection conn = dataSource.getConnection()) {
            //check to see if movie already exists
            String checkMovieQuery = "select * from movies where title=? AND year=? AND director=?";
            PreparedStatement checkMovieStatement = conn.prepareStatement(checkMovieQuery);

            checkMovieStatement.setString(1, movieTitle);
            checkMovieStatement.setInt(2, movieYear);
            checkMovieStatement.setString(3, director);

            ResultSet checkMovieResults = checkMovieStatement.executeQuery();

            if(checkMovieResults.next() != false){
                responseJsonObject.addProperty("status", "failed");
                responseJsonObject.addProperty("message", "Movie already exists!");
                out.write(responseJsonObject.toString());
                response.setStatus(200);
                out.close();
            }


            String newId = "tt0";
            //get MAX id and increase it by 1
            Statement maxMovieStatement = conn.createStatement();
            String maxMovieIdQuery = "SELECT max(id) FROM movies WHERE id LIKE 'tt0%'";
            ResultSet rs = maxMovieStatement.executeQuery(maxMovieIdQuery);

            /*
            PreparedStatement maxMovieIdStatement = conn.prepareStatement(maxMovieIdQuery);
            ResultSet rs = maxMovieIdStatement.executeQuery();

             */

            while(rs.next()){
                String maxId = rs.getString("max(id)");
                //parse string, increase it by 1, and set it as newId
                int numberPortion = Integer.parseInt(maxId.substring(2, maxId.length()));
                numberPortion++;
                newId += String.valueOf(numberPortion);
            }

            if( newId.length() < 3 ){
                System.out.println("Something went wrong, no id was appended to nm(maxId and numberPortion didn't work)");
                response.setStatus(505);
                out.close();
            }
            System.out.println(newId);

            //insert into movies
            String updateQuery = "INSERT INTO movies VALUES (?, ?, ?, ?)";
            PreparedStatement updateMoviesStatement = conn.prepareStatement(updateQuery);

            updateMoviesStatement.setString(1, newId);
            updateMoviesStatement.setString(2, movieTitle);
            updateMoviesStatement.setInt(3, movieYear);
            updateMoviesStatement.setString(4, director);


            int row = updateMoviesStatement.executeUpdate();
            responseJsonObject.addProperty("rowsUpdated", row);
            responseJsonObject.addProperty("movieId", newId);


            String updateRatingQuery = "INSERT INTO ratings VALUES (?, 0, 0)";
            PreparedStatement updateRatingStatement = conn.prepareStatement(updateRatingQuery);
            updateRatingStatement.setString(1, newId);

            int ratingsUpdated = updateRatingStatement.executeUpdate();
            responseJsonObject.addProperty("added num ratings:", ratingsUpdated);


            //ADDING GENRES HERE
            String checkGenreQuery = "select id, name from genres where name=?";
            PreparedStatement checkGenreStatement = conn.prepareStatement(checkGenreQuery);
            checkGenreStatement.setString(1, genre);

            ResultSet checkGenreResults = checkGenreStatement.executeQuery();

            if(checkGenreResults.next() != false){
                System.out.println("Genre already exists");

                int genreId = checkGenreResults.getInt("id");

                String updateGenresInMoviesQuery = "INSERT INTO genres_in_movies VALUES (?, ?)";
                PreparedStatement updateGenresInMoviesStatement = conn.prepareStatement(updateGenresInMoviesQuery);
                updateGenresInMoviesStatement.setInt(1, genreId);
                updateGenresInMoviesStatement.setString(2, newId);

                int genresUpdated = updateGenresInMoviesStatement.executeUpdate();
                responseJsonObject.addProperty("existing genre added: ", genreId);
            }else{
                System.out.println("Genre does not exist");

                Statement maxGenreStatement = conn.createStatement();
                String maxGenreIdQuery = "SELECT max(id) FROM genres";
                ResultSet genreIdSet = maxGenreStatement.executeQuery(maxGenreIdQuery);

                int newMaxGenreId = 0;

                while(genreIdSet.next()){
                    int oldMaxGenreId = genreIdSet.getInt("max(id)");
                    newMaxGenreId = oldMaxGenreId + 1;
                }
                System.out.println("new genre id: "+ newMaxGenreId);

                String updateGenresQuery = "INSERT INTO genres VALUES (?, ?)";
                PreparedStatement updateGenresStatement = conn.prepareStatement(updateGenresQuery);
                updateGenresStatement.setInt(1, newMaxGenreId);
                updateGenresStatement.setString(2, genre);

                int newGenresAdded = updateGenresStatement.executeUpdate();
                responseJsonObject.addProperty("genres added", newGenresAdded);


                String updateGenresInMoviesQuery = "INSERT INTO genres_in_movies VALUES (?, ?)";
                PreparedStatement updateGenresInMoviesStatement = conn.prepareStatement(updateGenresInMoviesQuery);
                updateGenresInMoviesStatement.setInt(1, newMaxGenreId);
                updateGenresInMoviesStatement.setString(2, newId);

                int newGenresInMovies = updateGenresInMoviesStatement.executeUpdate();
                responseJsonObject.addProperty("new genre added, genres_in_movies updated: ", newGenresInMovies);

            }



            // check to see if star exists, if it does, add it to movies, else make a
            // new star and add it to movie
            String checkActorQuery = "select id, name from stars where name=?";
            PreparedStatement checkActorStatement = conn.prepareStatement(checkActorQuery);
            checkActorStatement.setString(1, starName);

            ResultSet checkActorResults = checkActorStatement.executeQuery();

            if(checkActorResults.next() != false){
                System.out.println("star already exists");
                responseJsonObject.addProperty("message2", "Star already exists, adding to movie...");

                String starId = checkActorResults.getString("id");

                //insert into stars_in_movies
                String updateStarsInMoviesQuery = "INSERT INTO stars_in_movies VALUES (?, ?)";
                PreparedStatement updateStarsInMoviesStatement = conn.prepareStatement(updateStarsInMoviesQuery);
                updateStarsInMoviesStatement.setString(1, starId);
                updateStarsInMoviesStatement.setString(2, newId);

                int starsUpdated = updateStarsInMoviesStatement.executeUpdate();
                responseJsonObject.addProperty("rowsUpdated", row);
                responseJsonObject.addProperty("stars added", starsUpdated);

            }else{
                responseJsonObject.addProperty("message3", "Star doesnt exist, creating and adding to movie...");
                //need to create a new star, with new id
                String newStarId = "nm";
                //get MAX id and increase it by 1
                Statement maxStarStatement = conn.createStatement();
                String maxStarIdQuery = "SELECT max(id) FROM stars WHERE id LIKE 'nm%'";
                ResultSet rs2 = maxStarStatement.executeQuery(maxStarIdQuery);

                while(rs2.next()){
                    String maxStarId = rs2.getString("max(id)");
                    //parse string, increase it by 1, and set it as newId
                    int numberPortion = Integer.parseInt(maxStarId.substring(2, maxStarId.length()));
                    numberPortion++;
                    newStarId += String.valueOf(numberPortion);
                }
                System.out.println("new star id: "+ newStarId);

                String updateStarsQuery = "INSERT INTO stars VALUES (?, ?, ?)";
                PreparedStatement updateStarsStatement = conn.prepareStatement(updateStarsQuery);

                updateStarsStatement.setString(1, newStarId);
                updateStarsStatement.setString(2, starName);
                updateStarsStatement.setInt(3, 0);


                int newStars = updateStarsStatement.executeUpdate();
                responseJsonObject.addProperty("newStarId", newStarId);
                responseJsonObject.addProperty("movieId", newId);


                String updateStarsInMoviesQuery = "INSERT INTO stars_in_movies VALUES (?, ?)";
                PreparedStatement updateStarsInMoviesStatement = conn.prepareStatement(updateStarsInMoviesQuery);
                updateStarsInMoviesStatement.setString(1, newStarId);
                updateStarsInMoviesStatement.setString(2, newId);

                int starsUpdated = updateStarsInMoviesStatement.executeUpdate();
                responseJsonObject.addProperty("rowsUpdated", row);
                responseJsonObject.addProperty("stars added", starsUpdated);

            }







            // Write JSON string to output
            out.write(responseJsonObject.toString());
            out.close();


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

    }
}
