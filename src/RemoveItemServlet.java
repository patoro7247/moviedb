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

@WebServlet(name = "RemoveItemServlet", urlPatterns = "/api/shoppingcart/remove")
public class RemoveItemServlet extends HttpServlet {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String title = request.getParameter("title");

        PrintWriter out = response.getWriter();
        JsonObject responseJsonObject = new JsonObject();

        ArrayList<Item> cart = (ArrayList<Item>)request.getSession().getAttribute("cart");
        responseJsonObject.addProperty("cartLength", cart.size());


        for(int i = 0; i < cart.size(); i++){
            if (cart.get(i).getTitle().equals(title)){
                cart.remove(i);
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", title+" removed from shopping cart");

            }
        }
        /*
        responseJsonObject.addProperty("status", "fail");
        responseJsonObject.addProperty("message", "nothing happened?");
        */

        request.getSession().setAttribute("cart", cart);

        out.write(responseJsonObject.toString());
        response.setStatus(200);

        out.close();

    }
}
