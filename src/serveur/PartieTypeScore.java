package serveur;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Une classe qui gere une partie de UNO. On lance des manches tant que aucun
 * joueur n'a atteint un score suffisant, puis on affiche les scores finaux.
 * 
 * @author Coline van Leeuwen
 */
public class PartieTypeScore extends Partie implements Runnable {

	/** Score a atteindre pour gagner la partie */
	private int scoreToGet;

	/**
	 * Constructeur.
	 * 
	 * @param n               Le nombre de joueurs.
	 * @param argumentPartie  Le score a atteindre pour gagner la partie.
	 * @param listeJoueurs    La liste des sockets vers tous les clients connectes.
	 */
	public PartieTypeScore(int n, int argumentPartie, ArrayList<LienAvecClient> listeJoueurs) {
		super();
		this.scoreToGet = argumentPartie;
		this.listPlayers = listeJoueurs;
	}


	/**
	 * Determine si l'on doit relancer une manche ou terminer la partie.
	 * 
	 * @return true si on doit terminer la partie.
	 */
	private boolean unJoueurGagne() {
		if (listPlayers.size() == 0) {
			// Tous les clients sont deconnectes.
			return true;
		}
		for (LienAvecClient j : listPlayers) {
			if (j.getScore() > scoreToGet) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Lance la partie : enchaine les manches jusqu'a ce qu'un joueur depasse le
	 * score fixe.
	 * 
	 * @throws IOException
	 */
	public void lancerPartie() throws IOException {

		// Aucun joueur n'a atteint le score necessaire
		while (!unJoueurGagne()) {
			Manche manche = new Manche(listPlayers);
			manche.lancerManche();
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
			}
		}

		// Un joueur a atteint le score necessaire et gagne la partie
		etablirEnvoyerClassement();
	}


}
