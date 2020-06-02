package client;

import java.io.IOException;

/**
 * La classe a lancer pour jouer au UNO en tant qu'utilisateur.
 * 
 * @author Coline van Leeuwen
 */
public class JoueurMain {

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		// Rejoindre le serveur
		Joueur me;
		me = new Joueur();
		try {
			me.sendString("je-suis " + me.getName());
		} catch (Exception e) {
			return;
		}
		String reponse = me.receiveString();

		// Je suis refusee
		if (reponse.equals("Erreur : plus de place")) {
			System.out.println("Desolee, il n'y a plus de place. Vous etes deconnecte.");
			me.close();
		}
		// Je suis acceptee
		else if (reponse.equals("bienvenue")) {
			System.out.println(
					"Vous etes bien connecte. Nous attendons que tout le monde se connecte avant de lancer la partie...");
			// La partie se lance
			while (me.getGameRunning()) {
				me.gererMessage();
			}
			// La partie est finie, je me deconnecte
			me.close();
		}
	}

}
