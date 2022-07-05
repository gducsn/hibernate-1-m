# Hibernate - one to many

La relazione una a molti è quella relazione in cui due entità sono 
collegate tra loro. La prima entità può avere molteplici istanze, mentre 
le molteplici istanze possono essere collegate ad una singola entità.

In questo esempio vediamo come gestire questo tipo di relazione tra un 
carrello e molteplici elementi collegati.

Abbiamo due entità, la prima ‘cart’ con la quale istanzieremo il carrello. 
La seconda ‘items’ in cui inseriremo le informazioni del carrello:

Struttura:

- pom.xml
- hibernate.cfg.xml
- Cart.java
- Items.java
- DAO.java
- App.java

Le prime due le ho già analizzate 
[qui](https://github.com/gducsn/hibernate-crud).

---

Prima iniziare con le entità analizziamo i due script che creano le nostre 
tabelle:

```sql
CREATE TABLE `Cart` (
  `cart_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `total` decimal(10,0) NOT NULL,
  `name` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`cart_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

CREATE TABLE `Items` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `cart_id` int(11) unsigned NOT NULL,
  `item_id` varchar(10) NOT NULL,
  `item_total` decimal(10,0) NOT NULL,
  `quantity` int(3) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cart_id` (`cart_id`),
  CONSTRAINT `items_ibfk_1` FOREIGN KEY (`cart_id`) REFERENCES `Cart` 
(`cart_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
```

Abbiamo due tabelle da creare, ‘Cart’ e ‘Items’. La prima è quella 
principale, la seconda entità prenderà una colonna come riferimento dalla 
principale. 

Cart ha tre colonne, la prima è di tipo primary key, non può essere null e 
si auto incrementa per ogni istanza. Inoltre ha altre colonne non null.

In una tabella possiamo avere una primary key e una colonna che riferisce 
ad una primary key esterna, quindi una ‘foreign key’. In questo tabella 
per ogni istanza ci sarà un id proprio e un id collegato all’entità Cart.

Le due tabelle sono già in comunicazione tra loro. Le due classi, le due 
entità utilizzano le annotazioni JPA per riproporre la stessa logica di 
relazione. Adeguatamente sistemate possiamo istanziare quello che vogliamo 
e avere una piena relazione tre le entità e il database.

---

Cart.java

```java
@Entity
@Table(name = "CART")
public class Cart {

	public Cart() {
	};

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cart_id")
	private long id;

	@Column(name = "total")
	private double total;

	@Column(name = "name")
	private String name;

	@OneToMany(mappedBy = "cart")
	private Set<Items> items1;

// get / set 

}
```

La classe principale è segnata con la annotazione ‘@Entity’ la quale la 
definisce, appunto, entità.

Definiamo il nome di riferimento del table nel database con l’apposita 
annotazione.

Ora definiamo le nostre colonne proprio come abbiamo fatto con lo script 
del db.

‘@Id’ definisce proprio che questa colonna deve essere la primary key, poi 
definiamo in che modo deve essere definita ogni qualvolta istanziamo 
l’entità. 

Inoltre nell’annotazione ‘@GeneratedValue’ decidiamo che tipo di strategia 
utilizzare. ‘IDENTITY’ fa in modo che il nostro ID abbia un incremento 
automatico ad ogni riga.

Infine diamo un nome ad ogni colonna.

Il tipo di relazione viene definita sulla reference dell’altra classe. 
Quella notazione, ‘@OneToMany’, definisce il tipo, mentre ‘mappedby’ 
definisce quale tra le due entità sia la principale, in questo caso 
l’entità Cart in reference nell’altra classe.

Per convenienza e per ottenere vantaggi dal [entity state 
transitions](https://vladmihalcea.com/a-beginners-guide-to-jpa-hibernate-entity-state-transitions/) 
e [dirty checking 
mechanism](https://vladmihalcea.com/the-anatomy-of-hibernate-dirty-checking/), 
scegliamo di mappare l’entità figlio come collezione nell’oggetto padre. 
Utilizziamo un Set. 
[Set](https://docs.oracle.com/javase/8/docs/api/java/util/Set.html) è una 
collezione che non contiene duplicati.

---

Items.java

```java
@Entity
@Table(name = "Items")
public class Items {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private long id;

	@Column(name = "item_id")
	private String itemId;

	@Column(name = "item_total")
	private double itemTotal;

	@Column(name = "quantity")
	private int quantity;

	@ManyToOne
	@JoinColumn(name = "cart_id", nullable = false)
	private Cart cart;

	public Items() {
	}

	public Items(String itemId, double total, int qty, Cart c) {
		this.itemId = itemId;
		this.itemTotal = total;
		this.quantity = qty;
		this.cart = c;
	}
```

Come la prima entità anche qui abbiamo le configurazioni specifiche per 
ogni colonna. La logica segue sempre lo script iniziale per creare i due 
table nel db.

Quello importante è la parte in cui abbiamo la reference dell’altra 
classe. Aggiungiamo la prima annotazione che ci permette di definire il 
tipo di relazione. Utilizziamo la annotazione ‘@JoinColumn’ con la quale 
diciamo: abbiamo lo stesso riferimento per due colonne, uniamo le righe. 

Dunque una colonna di questa entità fa riferimento ad un’altra colonna 
della reference. L’altra colonna è la primary key.

La nostra entità per poter funzionare deve avere un costruttore vuoto. 
Possiamo aggiungere quanti costruttori vogliamo, l’importante è avere un 
firma diversa.

---

DAO.java

```java
public static void saveData(Cart cartobj, Items itemsobj, Items itemsobj2) 
{

		Transaction tx = null;
		try (Session session = 
HibernateUtil.getSessionFactory().openSession()) {

			System.out.println("Session created using 
annotations configuration");
			// start transaction
			tx = session.beginTransaction();
			// Save the Model object
			session.save(cartobj);
			session.save(itemsobj);
			session.save(itemsobj2);
			tx.commit();

		} catch (HibernateException e) {
			System.out.println("Exception occured. " + 
e.getMessage());
			e.printStackTrace();
		}
	};
```

La nostra classe DAO contiene il metodo ‘saveData’ che ci permette di 
persistere le istanze del database.

Per poter avviare una connessione abbiamo bisogno di creare un nuovo 
oggetto di tipo Transaction nel quale inseriremo la nostra session con il 
metodo .beginTransaction(). Quest’ultimo ci permette di avviare la 
transazione.

Per ottenere una connessione ed evitare di essere prolissi utilizziamo il 
blocco ‘try with resource’. Aggiungiamo al blocco try un argomento, la 
nostra connessione. In questo modo quando il metodo avrà fatto il suo 
lavoro chiuderà in automatico la connessione.

All’interno del metodo persistiamo le nostre istanze con il metodo 
‘.save()’ ed infine il commit della nostra Transaction.

---

App.java

```java
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
```

La classe App con il suo metodo main ci permette di far partire il tutto. 
Per prima cosa istanziamo un nuovo oggetto di tipo Cart dandogli poi un 
nome. Successivamente creiamo dei nuovi elementi con vari attributi. 

Abbiamo bisogno di organizzare i nostri items in una raccolta, ne 
istanziamo una di tipo ‘Items’ e aggiungiamo i nostri items al suo 
interno.

Nell’instanza cart aggiungiamo i nostri items, definiamo il totale e con 
il metodo saveData dalla classe DAO, persistiamo le istanze.

---

[Utile](https://vladmihalcea.com/the-best-way-to-map-a-onetomany-association-with-jpa-and-hibernate/).
