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
import java.io.File;
import java.io.FileWriter;




// Declaring a WebServlet called ResultsServlet, which maps to url "/api/results"
@WebServlet(name = "ResultsServlet", urlPatterns = "/api/results")
public class ResultsServlet extends HttpServlet {
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
        //Start total time here
        long startTotalTime = System.nanoTime();

        String contextPath = request.getServletContext().getRealPath("/");

        String xmlFilePath=contextPath+"\\test.txt";

        System.out.println(xmlFilePath);
        File myfile = new File(xmlFilePath);
        System.out.println(myfile.getAbsolutePath());

        System.out.println("does myfile already exist?: "+myfile.exists());

        if(!myfile.exists()){
            System.out.println("Creating file now");
            myfile.createNewFile();
        }

        FileWriter writer = new FileWriter(xmlFilePath, true);



        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        //String pre = request.getParameter("prefix");
        //System.out.println(pre);
        //request.getServletContext().log("getting prefix: " + prefix);
//        if(request.getParameter("prefix") != null){
//            //the prefix parameter is set,
//            //return all movies with the prefix in sql
//            String pre = request.getParameter("prefix");
//            System.out.println(pre);
//
//        }
        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (Connection conn = dataSource.getConnection()) {
            //Start JDBC parts per time here
            long startJDBCTime = System.nanoTime();

            if(request.getParameter("prefix") != null){
                System.out.println("Using first if statement");
                //the prefix parameter is set,
                //return all movies with the prefix in sql
                String pre = request.getParameter("prefix");
                int searchOffset = Integer.parseInt(request.getParameter("offset"));
                int searchMax = Integer.parseInt(request.getParameter("max"));


                System.out.println(pre);

                //Statement prefixStatement = conn.createStatement();
                //String oldPrefixQuery = "SELECT * FROM movies where title LIKE ?%";
                //OG: String prefixQuery = "SELECT m.id, m.title, m.year, m.director, r.rating FROM movies as m, ratings as r WHERE m.id=r.movieId AND m.title LIKE ? ORDER BY m.title ASC LIMIT ? OFFSET ?";
                String prefixQuery = "SELECT m.id, m.title, m.year, m.director, r.rating FROM movies as m, ratings as r WHERE m.id=r.movieId AND m.title LIKE ? ORDER BY m.title ASC LIMIT ? OFFSET ?";

                PreparedStatement prefixStatement = conn.prepareStatement(prefixQuery);
                prefixStatement.setString(1, pre + "%");
                prefixStatement.setInt(2, searchMax);
                prefixStatement.setInt(3, searchOffset);

                ResultSet prefixResultSet = prefixStatement.executeQuery();

                JsonArray jsonArray = new JsonArray();

                while(prefixResultSet.next()) {
                    String id = prefixResultSet.getString("id");
                    String title = prefixResultSet.getString("title");
                    int year = prefixResultSet.getInt("year");
                    String director = prefixResultSet.getString("director");
                    float rating = prefixResultSet.getFloat("rating");

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
                    String genreQuery = "SELECT g.name FROM genres as g, genres_in_movies as gim, movies as m WHERE g.id=gim.genreId AND m.id=? AND m.id=gim.movieId ORDER BY g.name ASC LIMIT 3";
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

                    String starsQuery = "SELECT s.name, s.id FROM stars_in_movies as sim, stars as s, movies as m WHERE sim.movieId=m.id AND sim.starId=s.id AND m.id=? ORDER BY s.name ASC LIMIT 3";
                    PreparedStatement statement3 = conn.prepareStatement(starsQuery);
                    statement3.setString(1, id);

                    ResultSet rs3 = statement3.executeQuery();
                    while(rs3.next()) {
                        String name = rs3.getString("name");
                        String starId = rs3.getString("id");

                        JsonObject starName = new JsonObject();
                        starName.addProperty("name", name);
                        starName.addProperty("id", starId);
                        starList.add(starName);


                    }
                    jsonObject.add("stars", starList);

                    rs3.close();
                    statement3.close();


                    jsonArray.add(jsonObject);

                }

                prefixResultSet.close();
                prefixStatement.close();

                // Log to localhost log
                request.getServletContext().log("getting " + jsonArray.size() + " results");

                // Write JSON string to output
                out.write(jsonArray.toString());
                // Set response status to 200 (OK)
                response.setStatus(200);
            }

            if(request.getParameter("genre") != null) {
                System.out.println("Using genre if statement");

                //the prefix parameter is set,
                //return all movies with the prefix in sql

                String genreName = request.getParameter("genre");
                int searchOffset = Integer.parseInt(request.getParameter("offset"));
                int searchMax = Integer.parseInt(request.getParameter("max"));


                //Statement prefixStatement = conn.createStatement();
                //SELECT m.id, m.title, m.year, m.director, r.rating FROM movies as m, genres as g, genres_in_movies as gim, ratings as r WHERE gim.movieId=m.id AND gim.genreId=g.id AND r.movieId=m.id AND g.name='Action' ORDER BY r.rating DESC;
                String genreListQuery = "SELECT m.id, m.title, m.year, m.director, r.rating FROM movies as m, genres as g, genres_in_movies as gim, ratings as r WHERE gim.movieId=m.id AND gim.genreId=g.id AND r.movieId=m.id AND g.name=? ORDER BY r.rating DESC LIMIT ? OFFSET ?";

                PreparedStatement genreStatement = conn.prepareStatement(genreListQuery);
                genreStatement.setString(1, genreName);
                genreStatement.setInt(2, searchMax);
                genreStatement.setInt(3, searchOffset);


                ResultSet genreResultSet = genreStatement.executeQuery();

                JsonArray jsonArray = new JsonArray();

                while(genreResultSet.next()) {
                    String id = genreResultSet.getString("id");
                    String title = genreResultSet.getString("title");
                    int year = genreResultSet.getInt("year");
                    String director = genreResultSet.getString("director");
                    float rating = genreResultSet.getFloat("rating");

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
                    String genreQuery = "SELECT g.name FROM genres as g, genres_in_movies as gim, movies as m WHERE g.id=gim.genreId AND m.id=? AND m.id=gim.movieId ORDER BY g.name ASC LIMIT 3";
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

                    String starsQuery = "SELECT s.name, s.id FROM stars_in_movies as sim, stars as s, movies as m WHERE sim.movieId=m.id AND sim.starId=s.id AND m.id=? ORDER BY s.name ASC LIMIT 3";
                    PreparedStatement statement3 = conn.prepareStatement(starsQuery);
                    statement3.setString(1, id);

                    ResultSet rs3 = statement3.executeQuery();
                    while(rs3.next()) {
                        String name = rs3.getString("name");
                        String starId = rs3.getString("id");

                        JsonObject starName = new JsonObject();
                        starName.addProperty("name", name);
                        starName.addProperty("id", starId);
                        starList.add(starName);


                    }
                    jsonObject.add("stars", starList);

                    rs3.close();
                    statement3.close();


                    jsonArray.add(jsonObject);

                }

                genreResultSet.close();
                genreStatement.close();

                // Log to localhost log
                request.getServletContext().log("getting " + jsonArray.size() + " results");

                // Write JSON string to output
                out.write(jsonArray.toString());
                // Set response status to 200 (OK)
                response.setStatus(200);
            }

            if(request.getParameter("title") != null || request.getParameter("year") != null || request.getParameter("director") != null || request.getParameter("star") != null){
                System.out.println("Using the really long if statement");
                //the prefix parameter is set,
                //return all movies with the prefix in sql
                String searchTitle = request.getParameter("title");



                String searchYear = request.getParameter("year");
                String searchDirector = request.getParameter("director");
                String searchStar = request.getParameter("star");
                int searchOffset = Integer.parseInt(request.getParameter("offset"));
                System.out.println("requested query: " +searchTitle);
                int searchMax = Integer.parseInt(request.getParameter("max"));

                System.out.println("Search Offset: "+searchOffset+", Search Max: "+searchMax);

                if(searchStar != null && !searchStar.isEmpty()){
                    //processing here
                }

                //break down multiple keywords
                //ex: "good u" needs to mean WHERE MATCH(title) AGAINST ('+good* +t*' IN BOOLEAN MODE)
                //append + to beginning of word, and then *
                //Use setString with new string

                String fulltextParameter = '+'+searchTitle;
                fulltextParameter = fulltextParameter.replaceAll(" ", "* +");

                if(fulltextParameter.charAt(fulltextParameter.length()-1) != '*' ){
                    fulltextParameter += '*';
                }


                /*
                String searchQuery = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating FROM movies as m, ratings as r, stars_in_movies as sim, stars as s WHERE m.id=r.movieId AND m.title LIKE ? AND m.year LIKE ? AND m.director LIKE ? AND sim.movieId=m.id AND s.id=sim.starId AND s.name LIKE ? ORDER BY m.title ASC LIMIT ? OFFSET ?";


                String newSearchQuery = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating FROM movies as m, ratings as r, stars_in_movies as sim, stars as s WHERE MATCH(title) AGAINST (? IN BOOLEAN MODE) AND  m.id=r.movieId AND m.title LIKE ? AND m.year LIKE ? AND m.director LIKE ? AND sim.movieId=m.id AND s.id=sim.starId AND s.name LIKE ? ORDER BY m.title ASC LIMIT ? OFFSET ?";


                PreparedStatement searchStatement = conn.prepareStatement(searchQuery);
                searchStatement.setString(1, "%"+ searchTitle + "%");
                searchStatement.setString(2, searchYear + "%");
                searchStatement.setString(3, "%"+ searchDirector + "%");
                searchStatement.setString(4, "%"+ searchStar + "%");
                searchStatement.setInt(5, searchMax);
                searchStatement.setInt(6, searchOffset);

                ResultSet searchResultSet = searchStatement.executeQuery();

                 */
                String newSearchQuery = "SELECT DISTINCT m.id, m.title, m.year, m.director, r.rating FROM movies as m, ratings as r, stars_in_movies as sim, stars as s WHERE MATCH(title) AGAINST (? IN BOOLEAN MODE) AND  m.id=r.movieId AND m.year LIKE ? AND m.director LIKE ? AND sim.movieId=m.id AND s.id=sim.starId AND s.name LIKE ? ORDER BY m.title ASC LIMIT ? OFFSET ?";
                System.out.println(fulltextParameter);

                PreparedStatement searchStatement = conn.prepareStatement(newSearchQuery);
                searchStatement.setString(1, fulltextParameter);
                searchStatement.setString(2, searchYear + "%");
                searchStatement.setString(3, "%"+ searchDirector + "%");
                searchStatement.setString(4, "%"+ searchStar + "%");
                searchStatement.setInt(5, searchMax);
                searchStatement.setInt(6, searchOffset);

                ResultSet searchResultSet = searchStatement.executeQuery();

                JsonArray jsonArray = new JsonArray();

                while(searchResultSet.next()) {
                    String id = searchResultSet.getString("id");
                    String title = searchResultSet.getString("title");
                    int year = searchResultSet.getInt("year");
                    String director = searchResultSet.getString("director");
                    float rating = searchResultSet.getFloat("rating");

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
                    String genreQuery = "SELECT g.name FROM genres as g, genres_in_movies as gim, movies as m WHERE g.id=gim.genreId AND m.id=? AND m.id=gim.movieId ORDER BY g.name ASC LIMIT 3";
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

                    String starsQuery = "SELECT s.name, s.id FROM stars_in_movies as sim, stars as s, movies as m WHERE sim.movieId=m.id AND sim.starId=s.id AND m.id=? ORDER BY s.name ASC LIMIT 3";
                    PreparedStatement statement3 = conn.prepareStatement(starsQuery);
                    statement3.setString(1, id);

                    ResultSet rs3 = statement3.executeQuery();
                    while(rs3.next()) {
                        String name = rs3.getString("name");
                        String starId = rs3.getString("id");

                        JsonObject starName = new JsonObject();
                        starName.addProperty("name", name);
                        starName.addProperty("id", starId);
                        starList.add(starName);


                    }
                    jsonObject.add("stars", starList);

                    rs3.close();
                    statement3.close();


                    jsonArray.add(jsonObject);

                }

                searchResultSet.close();
                searchStatement.close();

                //end JDBC time here
                long endJDBCTime = System.nanoTime();
                long elapsedJDBCTime = endJDBCTime - startJDBCTime;

                writer.write(String.valueOf(elapsedJDBCTime)+", ");

                // Write JSON string to output
                out.write(jsonArray.toString());
                System.out.println(jsonArray.toString());
                // Set response status to 200 (OK)
                response.setStatus(200);

            }


            // Log to localhost log
            request.getServletContext().log("no prefix OR index!, this is from ResultsServlet.java");

            // Write JSON string to output
            //out.write(jsonArray.toString());
            // Set response status to 200 (OK)
            response.setStatus(200);

            long endTotalTime = System.nanoTime();
            long elapsedTotalTime = endTotalTime-startTotalTime;

            writer.write(String.valueOf(elapsedTotalTime));
            writer.write(System.getProperty( "line.separator" ));

            writer.close();


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
