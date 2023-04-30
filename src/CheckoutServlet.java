import com.google.gson.JsonArray;
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
import java.util.ArrayList;

@WebServlet(name = "CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
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
        String firstName = request.getParameter("firstName");
        System.out.println(firstName);

        String lastName = request.getParameter("lastName");
        System.out.println(lastName);

        String ccid = request.getParameter("ccid");
        System.out.println(ccid);

        String exp = request.getParameter("exp");
        System.out.println(exp);




        JsonObject responseJsonObject = new JsonObject();

        PrintWriter out = response.getWriter();
        //set up database connection and check credentials
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource

            //get cc info from ccid

            // Construct a query with parameter represented by "?"
            String query = "select id, firstName, lastName, expiration from creditcards where id=?";

            // Declare our statement
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, ccid);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            if(!rs.isBeforeFirst()){
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Credentials do not match!");
                request.getServletContext().log("Login failed");
            }

            while (rs.next()) {

                String realFirstName = rs.getString("firstName");
                String realLastName = rs.getString("lastName");
                String realExp = rs.getString("expiration");


                if(firstName.equals(realFirstName) && lastName.equals(realLastName) && exp.equals(realExp)){
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "Credentials Match!");
                    //get user id
                    User u = (User)request.getSession().getAttribute("user");
                    int user_id = u.getId();

                    //get list of movies to add to sales
                    ArrayList<Item> cart = (ArrayList<Item>)request.getSession().getAttribute("cart");

                    for(int i = 0; i < cart.size(); i++){
                        String title = cart.get(i).getTitle();
                        String movieIdQuery = "select id from movies where title=?";
                        PreparedStatement movieIdStatement = conn.prepareStatement(movieIdQuery);
                        movieIdStatement.setString(1, title);

                        ResultSet movieRs = movieIdStatement.executeQuery();

                        while(movieRs.next()){
                            String movieId = movieRs.getString("id");

                            String insertSalesQuery = "INSERT INTO sales VALUES (NULL, ?, ?, DATE '2023-04-30')";
                            PreparedStatement salesInsertStatement = conn.prepareStatement(insertSalesQuery);

                            salesInsertStatement.setInt(1, user_id);
                            salesInsertStatement.setString(2, movieId);
                            int row = salesInsertStatement.executeUpdate();
                            responseJsonObject.addProperty("rowsUpdated", row);
                        }

                    }





                }else{
                    //credentials are false!
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "Credentials do not match!");


                }

            }



            rs.close();
            statement.close();


            // Write JSON string to output
            out.write(responseJsonObject.toString());
            out.close();

            //Write first query JSON string to output
            //String query1 = "SELECT title, year, director FROM movies where id = ?";
            //PreparedStatement statement1 = conn.prepareStatement(query1);


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
