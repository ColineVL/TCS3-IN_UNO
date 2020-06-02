package classes_partagees;

public enum Couleur {
	noir, jaune, bleu, rouge, vert;

	/**
	 * Verifie que la saisie decrit bien une couleur du jeu.
	 * 
	 * @param clientInput la saisie de l'utilisateur.
	 * @return true si et seulement si clientInput represente bien une couleur.
	 */
	public static boolean estBienUneCouleur(String clientInput) {
		String[] tab = { "jaune", "bleu", "rouge", "vert" };
		for (String str : tab) {
			if (str.equals(clientInput)) {
				return true;
			}
		}
		return false;
	}

}
