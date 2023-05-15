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


            String newId = "tt";
            //get MAX id and increase it by 1
            Statement maxMovieStatement = conn.createStatement();

            String maxMovieIdQuery = "SELECT max(id) FROM movies";
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
