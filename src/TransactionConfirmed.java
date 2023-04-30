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


@WebServlet(name = "TransactionConfirmed", urlPatterns = "/api/transaction")
public class TransactionConfirmed extends HttpServlet {
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    private static final long serialVersionUID = 2L;
    private DataSource dataSource;


//    public class Item {
//        public String itemTitle;
//        public double price = 5.00;
//        public int quantity;
//
//        public Item(String itemTitle){
//            this.itemTitle = itemTitle;
//            this.price = 5.00;
//            this.quantity = 1;
//        }
//
//        public void addQuantity(){
//            quantity++;
//        }
//
//        public String getTitle(){
//            return itemTitle;
//        }
//
//        public int getQuantity(){
//            return quantity;
//        }
//
//        public double getPrice(){
//            return price;
//        }
//    }

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedbexample");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        //if we are getting this endpoint, consume user's cart and send the info as a response
        // to the webserver
        //JsonObject responseJsonObject = new JsonObject();
        PrintWriter out = response.getWriter();

        //send the user a JSON array of their cart as a response
        JsonArray cartArray = new JsonArray();

        ArrayList<Item> list = (ArrayList<Item>)httpRequest.getSession().getAttribute("cart");

        for(int i = 0; i < list.size(); i++){
            JsonObject cartItem = new JsonObject();
            cartItem.addProperty("title", list.get(i).getTitle());
            cartItem.addProperty("quantity", list.get(i).getQuantity());
            cartItem.addProperty("price", list.get(i).getPrice());


            cartArray.add(cartItem);
        }

        ArrayList<Item> newCart = new ArrayList<>();
        httpRequest.getSession().setAttribute("cart", newCart);

        out.write(cartArray.toString());
        response.setStatus(200);
        out.close();


    }

}
