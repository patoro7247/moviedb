/**
 * This User class only has the username field in this example.
 * You can add more attributes such as the user's shopping cart items.
 */
public class User {

    private final String username;
    private int id;

    public User(String username) {
        this.username = username;
    }

    public User(String username, int id) {
        this.username = username;
        this.id = id;
    }


    public int getId(){
        return id;
    }
}
