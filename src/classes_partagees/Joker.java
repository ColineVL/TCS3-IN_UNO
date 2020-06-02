package classes_partagees;

/**
 * Carte Joker
 * 
 * @author Coline van Leeuwen
 */
public class Joker extends Carte {

	/**
	 * Constructeur sans argument : par defaut la couleur est noire.
	 */
	public Joker() {
		super.setColor(Couleur.noir);
		super.setScore(50);
		super.setValue("Joker");
	}

	/**
	 * Constructeur.
	 * 
	 * @param c la couleur demandee par l'utilisateur.
	 */
	public Joker(Couleur c) {
		super.setColor(c);
		super.setScore(50);
		super.setValue("Joker");
	}

	/**
	 * Explique quels effets a la carte.
	 * 
	 * @return Un tableau sous la forme {saute, nbCartes, sens} : saute vaut true ou
	 *         false, nbCartes peut etre 0, 2 ou 4 et sens est -1 si le sens change
	 *         et 1 sinon.
	 */
	public String[] quelsEffets() { // Aucun effet !
		String nbCartes = "0";
		String saute = "false";
		String sens = "1";
		String[] tab = { saute, nbCartes, sens };
		return tab;
	}

	/**
	 * Permet de ne pas afficher la couleur de la carte quand c'est un Joker.
	 */
	public String toString() {
		return "" + getValue();
	}
}
