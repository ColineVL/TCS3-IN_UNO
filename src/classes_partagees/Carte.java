package classes_partagees;

/**
 * Represente les cartes du UNO : une valeur et une couleur.
 * 
 * @author Coline van Leeuwen
 */
public abstract class Carte implements Comparable<Carte> {

	/** Description de la carte */
	private Couleur color;
	private String value;
	/** Le nombre de points que vaut la carte à la fin d'une manche. */
	private int score;

	/**
	 * Getter
	 * 
	 * @return la couleur de la carte.
	 */
	public Couleur getColor() {
		return color;
	}

	/**
	 * Setter
	 * 
	 * @param color la couleur voulue.
	 */
	public void setColor(Couleur color) {
		this.color = color;
	}

	/**
	 * Getter
	 * 
	 * @return la valeur de la carte.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Setter
	 * 
	 * @param value la valeur voulue.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Getter
	 * 
	 * @return le nombre de points que vaut la carte a la fin d'une manche.
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Setter
	 * 
	 * @param n le nombre de points voulu.
	 */
	public void setScore(int n) {
		score = n;
	}

	/**
	 * Fait office de constructeur. Cette methode est a modifier si l'on rajoute des
	 * types de cartes par sous-typage.
	 * 
	 * @param str Une instruction de carte a creer sous le format "Valeur-Couleur".
	 * @return La carte cree.
	 */
	public static Carte nouvelleCarte(String str) {
		Carte card;
		String[] tab = str.split("-");
		switch (tab[0].trim()) {
		case "+4":
			if (tab.length == 1) { // Par exemple "+4", sous-entendu noir
				card = new Plus4();
			} else { // Par exemple "+4-bleu", un joueur a choisi la couleur
				card = new Plus4(Couleur.valueOf(tab[1]));
			}
			break;
		case "Joker":
			if (tab.length == 1) {
				card = new Joker();
			} else {
				card = new Joker(Couleur.valueOf(tab[1]));
			}
			break;
		case "+2":
			card = new Plus2(Couleur.valueOf(tab[1]));
			break;
		case "Passer":
			card = new Passer(Couleur.valueOf(tab[1]));
			break;
		case "Inversion":
			card = new Inversion(Couleur.valueOf(tab[1]));
			break;
		default:
			card = new Normale(Couleur.valueOf(tab[1]), tab[0]);
			break;
		}
		return card;
	}

	/**
	 * Permet d'afficher une carte sous le format Valeur-couleur.
	 */
	public String toString() {
		return "" + value + "-" + color;
	}

	/**
	 * Verifie si deux cartes sont identiques.
	 * 
	 * @param carte la carte a comparer avec this.
	 * @return true si et seulement si les deux cartes sont identiques.
	 */
	public boolean equals(Carte carte) {
		// Si c'est un Joker ou un +4, pas besoin de verifier la couleur.
		if (value.equals(carte.getValue()) && ((value.equals("Joker") || (value.equals("+4"))))) {
			return true;
		}
		if (color != carte.color)
			return false;
		if (!value.equals(carte.getValue())) {
			return false;
		}
		return true;
	}

	/**
	 * Explique quels effets a la carte.
	 * 
	 * @return Un tableau sous la forme {saute, nbCartes, sens} : saute vaut true ou
	 *         false, nbCartes peut etre 0, 2 ou 4 et sens est -1 si le sens change
	 *         et 1 sinon.
	 */
	public abstract String[] quelsEffets();

	/**
	 * Decide si il est legal de jouer "this" sur "card".
	 * 
	 * @param card La carte sur laquelle on veut jouer.
	 * @return Un booleen selon si c'est legal ou pas.
	 */
	public boolean peutEtreJoueeSur(Carte card, Couleur choixCouleur) {
		if (this.getValue().equals("+4") || this.getValue().equals("Joker")) {
			return true;
		}
		if (card.getValue().equals("Joker") || card.getValue().equals("+4")) {
			if (this.getColor() == choixCouleur) {
				return true;
			}
			if (choixCouleur == Couleur.noir) {
				// Dans ce cas on est au tout debut de la partie et la premiere carte retournee
				// est un Joker.
				return true;
			}
			return false;
		}
		if (this.getColor() == card.getColor()) {
			return true;
		}
		if (this.getValue().equals(card.getValue())) {
			return true;
		}
		return false;
	}

	/**
	 * Verifie que l'instruction decrit bien une carte existante dans le jeu.
	 * 
	 * @param str Une instruction sous la forme "Valeur-couleur".
	 * @return true si et seulement si la carte demandee existe.
	 */
	public static boolean existe(String str) {
		String[] tab = str.split("-");
		// On verifie que la couleur est valide.
		boolean flagColor = false;
		String[] tableauCouleurs = { "bleu", "jaune", "vert", "rouge", "noir" };
		for (String c : tableauCouleurs) {
			if (tab[1].equals(c)) {
				flagColor = true;
			}
		}
		// On verifie que la valeur est valide.
		boolean flagValue = false;
		String[] tableauValeurs = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+2", "Inversion", "Passer" };
		for (String v : tableauValeurs) {
			if (tab[0].equals(v)) {
				flagValue = true;
			}
		}
		// On renvoie.
		return flagColor && flagValue;
	}

	/**
	 * Compare deux cartes, par couleur puis par valeur. Les cartes noires sont a la
	 * fin.
	 * 
	 * @return -1 si this<o, 0 si elles sont egales, 1 sinon.
	 */
	public int compareTo(Carte o) {
		if (this.getColor() == o.getColor()) {
			return this.getValue().compareTo(o.getValue());
		} else if (this.getColor() == Couleur.noir) {
			return 1;
		} else if (o.getColor() == Couleur.noir) {
			return -1;
		} else {
			return this.getColor().compareTo(o.getColor());
		}
	}

}
