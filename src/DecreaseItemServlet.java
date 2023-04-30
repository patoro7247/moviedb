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

@WebServlet(name = "DecreaseItemServlet", urlPatterns = "/api/shoppingcart/decrease")
public class DecreaseItemServlet extends HttpServlet {
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
                if(cart.get(i).getQuantity() > 1){
                    cart.get(i).decrementQuantity();
                }
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", title+" quantity decreased, total quantity: "+cart.get(i).getQuantity());

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
