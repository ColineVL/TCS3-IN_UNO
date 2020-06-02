package classes_partagees;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Une classe pour representer une pile de cartes : les mains des joueurs, le
 * talon, la pioche...
 * 
 * @author Coline van Leeuwen
 */
public class PaquetDeCartes {

	/** La liste de cartes dans le paquet. */
	private ArrayList<Carte> deck;

	/**
	 * Constructeur
	 * 
	 * @param message "entier" formera une pioche, un autre message formera une main
	 *                ou un talon a remplir.
	 */
	public PaquetDeCartes(String message) {
		deck = new ArrayList<Carte>();

		if (message.equals("entier")) {
			// On cree toutes les cartes de la pioche.
			Couleur[] tab = { Couleur.bleu, Couleur.rouge, Couleur.vert, Couleur.jaune };
			for (Couleur color : tab) {
				for (int i = 0; i < 2; i++) {
					deck.add(new Normale(color, "1"));
					deck.add(new Normale(color, "2"));
					deck.add(new Normale(color, "3"));
					deck.add(new Normale(color, "4"));
					deck.add(new Normale(color, "5"));
					deck.add(new Normale(color, "6"));
					deck.add(new Normale(color, "7"));
					deck.add(new Normale(color, "8"));
					deck.add(new Normale(color, "9"));
					deck.add(new Plus2(color));
					deck.add(new Inversion(color));
					deck.add(new Passer(color));
				}
				deck.add(new Normale(color, "0"));
				deck.add(new Joker());
				deck.add(new Plus4());

				Collections.shuffle(deck);
			}
		}
	}

	/**
	 * Getter
	 * 
	 * @return la liste de cartes dans le paquet.
	 */
	public ArrayList<Carte> getDeck() {
		return deck;
	}
	
	/**
	 * Calcule le nombre de cartes dans le paquet.
	 * 
	 * @return la taille de deck.
	 */
	public int size() {
		return getDeck().size();
	}

	/**
	 * Retourne une carte precise.
	 * 
	 * @param index
	 * @return la carte situee a l'index demande.
	 */
	public Carte get(int index) {
		return getDeck().get(index);
	}

	/**
	 * Retourne la derniere carte du talon.
	 * 
	 * @return la carte la plus recente du talon.
	 */
	public Carte getCarteTalon() {
		return getDeck().get(getDeck().size() - 1);
	}

	/**
	 * Ajoute une carte dans le deck.
	 * 
	 * @param carte la carte a ajouter.
	 */
	public void add(Carte carte) {
		getDeck().add(carte);
	}
	
	/**
	 * Permet d'afficher la main d'un joueur.
	 */
	public void display() {
		for (int index = 0; index < this.size(); index++) {
			Carte card = this.get(index);
			System.out.println("		" + card);
		}
	}

	/**
	 * Permet de melanger la pioche.
	 */
	public void shuffle() {
		Collections.shuffle(getDeck());
	}

	/**
	 * Permet de retirer une carte du deck.
	 * 
	 * @param card La carte a retirer.
	 */
	public void remove(Carte card) {
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).equals(card)) {
				getDeck().remove(i);
				return;
			}
		}
	}

	/**
	 * Verifie si le deck contient la carte demandee.
	 * 
	 * @param card La carte demandee.
	 * @return true si et seulement si deck contient la carte demandee.
	 */
	public boolean contains(Carte card) {
		for (int i = 0; i < this.size(); i++) {
			if (this.get(i).equals(card)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Vide le deck.
	 */
	public void clear() {
		getDeck().clear();
	}

	/**
	 * Permet de refaire une pioche a partir du talon quand elle est vide.
	 * 
	 * @param talon
	 */
	public void addAll(PaquetDeCartes talon) {
		getDeck().addAll(talon.getDeck());
	}

	/**
	 * Trie deck par couleur puis par valeur, les cartes noires a la fin.
	 */
	public void sort() {
		Collections.sort(getDeck());
	}

}
