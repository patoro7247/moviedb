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

@WebServlet(name = "AddStarServlet", urlPatterns = "/_dashboard/api/addstar")
public class AddStarServlet extends HttpServlet {
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
        String starName = request.getParameter("starName");
        System.out.println(starName);

        int birthYear = 0;
        String byear = request.getParameter("birthYear");

        if( byear != null && !byear.isEmpty()){
            birthYear = Integer.parseInt(request.getParameter("birthYear"));
            System.out.println(birthYear);
        }

        JsonObject responseJsonObject = new JsonObject();

        PrintWriter out = response.getWriter();
        //set up database connection and check credentials
        try (Connection conn = dataSource.getConnection()) {
            // Get a connection from dataSource
            String newId = "nm";
            //get MAX id and increase it by 1
            String maxStarIdQuery = "SELECT max(id) FROM stars";
            PreparedStatement maxStarIdStatement = conn.prepareStatement(maxStarIdQuery);
            ResultSet rs = maxStarIdStatement.executeQuery();

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

            //get cc info from ccid
            String updateQuery = "INSERT INTO stars VALUES (?, ?, ?)";
            PreparedStatement updateStarsStatement = conn.prepareStatement(updateQuery);
            updateStarsStatement.setString(1, newId);
            updateStarsStatement.setString(2, starName);
            updateStarsStatement.setNull(3, java.sql.Types.INTEGER);

            if(birthYear > 0){
                updateStarsStatement.setInt(3, birthYear);
            }

            int row = updateStarsStatement.executeUpdate();
            responseJsonObject.addProperty("rowsUpdated", row);
            responseJsonObject.addProperty("starId", newId);



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
