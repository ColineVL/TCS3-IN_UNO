package classes_partagees;

/**
 * Carte +4
 * 
 * @author Coline van Leeuwen
 */
public class Plus4 extends Carte {

	/**
	 * Constructeur sans argument : par defaut la couleur est noire.
	 */
	public Plus4() {
		super.setColor(Couleur.noir);
		super.setScore(50);
		super.setValue("+4");
	}

	/**
	 * Constructeur.
	 * 
	 * @param c la couleur demandee par l'utilisateur.
	 */
	public Plus4(Couleur c) {
		super.setColor(c);
		super.setScore(50);
		super.setValue("+4");
	}

	/**
	 * Explique quels effets a la carte.
	 * 
	 * @return Un tableau sous la forme {saute, nbCartes, sens} : saute vaut true ou
	 *         false, nbCartes peut etre 0, 2 ou 4 et sens est -1 si le sens change
	 *         et 1 sinon.
	 */
	public String[] quelsEffets() { // Le joueur suivant :
		String nbCartes = "4"; // pioche 4 cartes
		String saute = "true"; // et saute son tour
		String sens = "1"; // mais le sens de jeu ne change pas
		String[] tab = { saute, nbCartes, sens };
		return tab;
	}

	/**
	 * Permet de ne pas afficher la couleur de la carte quand c'est un +4.
	 */
	public String toString() {
		return "" + getValue();
	}

}