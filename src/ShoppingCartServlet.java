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


@WebServlet(name = "ShoppingCartServlet", urlPatterns = "/api/shoppingcart")
public class ShoppingCartServlet extends HttpServlet {
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

        if(request.getParameter("title") != null) {
            String title = request.getParameter("title");

            System.out.println("Movie Title: " + title);


            JsonObject responseJsonObject = new JsonObject();
            PrintWriter out = response.getWriter();

            if (httpRequest.getSession().getAttribute("cart") == null) {
                System.out.println("Cart is empty, creating one");
                ArrayList<Item> cart = new ArrayList<>();

                Item i = new Item(title);
                cart.add(i);

                httpRequest.getSession().setAttribute("cart", cart);

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success, cart was null, created and added");
                //request.getSession().setAttribute("user", new User(username));
            } else {
                System.out.println("Cart found, adding item");
                ArrayList<Item> cart = (ArrayList<Item>) httpRequest.getSession().getAttribute("cart");

                boolean itemAdded = false;
                for (int i = 0; i < cart.size(); i++) {
                    if (cart.get(i).getTitle().equals(title)) {
                        cart.get(i).addQuantity();
                        itemAdded = true;
                    }
                }

                if (!itemAdded) {
                    Item i = new Item(title);
                    cart.add(i);
                }


                httpRequest.getSession().setAttribute("cart", cart);

                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success, cart WAS NOT null, item added");
            }

            out.write(responseJsonObject.toString());
            response.setStatus(200);
            out.close();
        }
        if(request.getParameter("retrieve") != null){
            System.out.println("retrieve request received");

            JsonObject responseJsonObject = new JsonObject();
            PrintWriter out = response.getWriter();

            //send the user a JSON array of their cart as a response
            JsonArray cartArray = new JsonArray();

            if(httpRequest.getSession().getAttribute("cart") != null){
                ArrayList<Item> list = (ArrayList<Item>)httpRequest.getSession().getAttribute("cart");
                for(int i = 0; i < list.size(); i++){
                    JsonObject cartItem = new JsonObject();
                    cartItem.addProperty("title", list.get(i).getTitle());
                    cartItem.addProperty("quantity", list.get(i).getQuantity());
                    cartItem.addProperty("price", list.get(i).getPrice());


                    cartArray.add(cartItem);
                }

                out.write(cartArray.toString());
                response.setStatus(200);
                out.close();

            }else{
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "Cart Empty!");
                out.write(responseJsonObject.toString());
                response.setStatus(200);
                out.close();
            }

        }

        //HttpServletResponse httpResponse = (HttpServletResponse) response;
        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */



    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String title = request.getParameter("title");

        System.out.println("Movie Title: "+ title);


        JsonObject responseJsonObject = new JsonObject();
        PrintWriter out = response.getWriter();

        if (httpRequest.getSession().getAttribute("cart") == null) {
            System.out.println("Cart is empty, creating one");
            ArrayList<Item> cart = new ArrayList<>();

            Item i = new Item(title);
            cart.add(i);

            httpRequest.getSession().setAttribute("cart", cart);

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success, cart was null, created and added");
            //request.getSession().setAttribute("user", new User(username));
        } else {
            System.out.println("Cart found, adding item");
            ArrayList<Item> cart = (ArrayList<Item>)httpRequest.getSession().getAttribute("cart");

            boolean itemAdded = false;
            for(int i = 0; i < cart.size(); i++){
                if(cart.get(i).getTitle().equals(title)){
                    cart.get(i).addQuantity();
                    itemAdded = true;
                }
            }

            if(!itemAdded){
                Item i = new Item(title);
                cart.add(i);
            }


            httpRequest.getSession().setAttribute("cart", cart);

            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success, cart WAS NOT null, item added");
        }

        out.write(responseJsonObject.toString());
        response.setStatus(200);
        out.close();

        //HttpServletResponse httpResponse = (HttpServletResponse) response;
        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */

    }
}
