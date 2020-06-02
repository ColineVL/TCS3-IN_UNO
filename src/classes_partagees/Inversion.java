package classes_partagees;

/**
 * Carte Inversion
 * 
 * @author Coline van Leeuwen
 */
public class Inversion extends Carte {

	/**
	 * Constructeur.
	 * 
	 * @param color la couleur de la carte.
	 */
	public Inversion(Couleur color) {
		super.setColor(color);
		super.setScore(20);
		super.setValue("Inversion");
	}

	/**
	 * Explique quels effets a la carte. Remplace la meme methode codee dans Carte.
	 * 
	 * @return Un tableau sous la forme {saute, nbCartes, sens} : saute vaut true ou
	 *         false, nbCartes peut etre 0, 2 ou 4 et sens est -1 si le sens change
	 *         et 1 sinon.
	 */
	public String[] quelsEffets() { // Le joueur suivant :
		String nbCartes = "0"; // ne pioche pas de cartes
		String saute = "false"; // ne saute pas son tour
		String sens = "-1"; // mais le sens de jeu change
		String[] tab = { saute, nbCartes, sens };
		return tab;
	}

}
