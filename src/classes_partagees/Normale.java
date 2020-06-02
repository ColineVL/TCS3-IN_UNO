package classes_partagees;

/**
 * Carte classique entre 0 et 9
 * 
 * @author Coline van Leeuwen
 */
public class Normale extends Carte {

	/**
	 * Constructeur.
	 * 
	 * @param color la couleur de la carte.
	 * @param value la valeur de la carte : entre 0 et 9.
	 */
	public Normale(Couleur color, String value) {
		super.setColor(color);
		super.setValue(value);
		super.setScore(Integer.parseInt(value));
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
}
