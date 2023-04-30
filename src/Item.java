public class Item {
    public String itemTitle;
    public double price = 5.00;
    public int quantity;

    public Item(String itemTitle){
        this.itemTitle = itemTitle;
        this.price = 5.00;
        this.quantity = 1;
    }

    public void addQuantity(){
        quantity++;
    }

    public void decrementQuantity() {
        if(quantity > 1){
            quantity--;
        }

    }

    public String getTitle(){
        return itemTitle;
    }

    public int getQuantity(){
        return quantity;
    }

    public double getPrice(){
        return price;
    }
}