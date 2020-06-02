package classes_partagees;

/**
 * Carte Passer
 * 
 * @author Coline van Leeuwen
 */
public class Passer extends Carte {

	/**
	 * Constructeur.
	 * 
	 * @param color la couleur de la carte.
	 */
	public Passer(Couleur color) {
		super.setColor(color);
		super.setScore(20);
		super.setValue("Passer");
	}

	/**
	 * Explique quels effets a la carte.
	 * 
	 * @return Un tableau sous la forme {saute, nbCartes, sens} : saute vaut true ou
	 *         false, nbCartes peut etre 0, 2 ou 4 et sens est -1 si le sens change
	 *         et 1 sinon.
	 */
	public String[] quelsEffets() { // Le joueur suivant :
		String nbCartes = "0"; // ne pioche pas de carte
		String saute = "true"; // saute son tour
		String sens = "1"; // et le sens de jeu ne change pas
		String[] tab = { saute, nbCartes, sens };
		return tab;
	}

}
