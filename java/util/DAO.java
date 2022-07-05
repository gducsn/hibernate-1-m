package util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import model.Cart;
import model.Items;

public class DAO {

	public static void saveData(Cart cartobj, Items itemsobj, Items itemsobj2) {

		Transaction tx = null;
		try (Session session = HibernateUtil.getSessionFactory().openSession()) {

			System.out.println("Session created using annotations configuration");
			// start transaction
			tx = session.beginTransaction();
			// Save the Model object
			session.save(cartobj);
			session.save(itemsobj);
			session.save(itemsobj2);
			tx.commit();

		} catch (HibernateException e) {
			System.out.println("Exception occured. " + e.getMessage());
			e.printStackTrace();
		}
	};

}
