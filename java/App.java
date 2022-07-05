import java.util.HashSet;
import java.util.Set;

import model.Cart;
import model.Items;
import util.DAO;

public class App {

	public static void main(String[] args) {

		Cart cart = new Cart();
		cart.setName("cart");
		Items item1 = new Items("I10", 10, 1, cart);
		Items item2 = new Items("I20", 20, 2, cart);
		Set<Items> itemsSet = new HashSet<Items>();
		itemsSet.add(item1);
		itemsSet.add(item2);
		cart.setItems(itemsSet);
		cart.setTotal(10 * 1 + 20 * 2);
		DAO.saveData(cart, item1, item2);
	}
}
