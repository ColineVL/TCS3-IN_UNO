package classes_partagees;

/**
 * Carte +2
 * 
 * @author Coline van Leeuwen
 */
public class Plus2 extends Carte {

	/**
	 * Constructeur.
	 * 
	 * @param color la couleur de la carte.
	 */
	public Plus2(Couleur color) {
		super.setColor(color);
		super.setScore(20);
		super.setValue("+2");
	}

	/**
	 * Explique quels effets a la carte.
	 * 
	 * @return Un tableau sous la forme {saute, nbCartes, sens} : saute vaut true ou
	 *         false, nbCartes peut etre 0, 2 ou 4 et sens est -1 si le sens change
	 *         et 1 sinon.
	 */
	public String[] quelsEffets() { // Le joueur suivant :
		String nbCartes = "2"; // pioche deux cartes
		String saute = "true"; // saute son tour
		String sens = "1"; // et le sens de jeu ne change pas
		String[] tab = { saute, nbCartes, sens };
		return tab;
	}
}
