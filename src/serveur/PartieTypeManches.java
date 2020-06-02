package serveur;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Une classe qui gere une partie de UNO. On lance un nombre de manches defini au debut de la partie, puis on affiche les scores finaux : le nombre de manches gagnees par chaque joueur.
 * 
 * @author Coline van Leeuwen
 */
public class PartieTypeManches extends Partie implements Runnable {

		/** Nombre de manches pendant la partie */
		private int nbManches;

		/**
		 * Constructeur.
		 * 
		 * @param n               Le nombre de joueurs.
		 * @param argumentPartie  Le nombre de manches a jouer pendant la partie.
		 * @param listeJoueurs    La liste des sockets vers tous les clients connectes.
		 */
		public PartieTypeManches(int n, int argumentPartie, ArrayList<LienAvecClient> listeJoueurs) {
			super();
			this.nbManches = argumentPartie;
			this.listPlayers = listeJoueurs;
		}

		/**
		 * Lance la partie : enchaine nbManches manches.
		 * 
		 * @throws IOException
		 */
		public void lancerPartie() throws IOException {
			
			for (int i = 0; i < nbManches; i++) {
				if (listPlayers.size() != 0) {
					Manche manche = new Manche(listPlayers);
					manche.lancerManche();
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
				}
			}

			// On a joue toutes les manches, c'est la fin de la partie.
			etablirEnvoyerClassement();
		}		

	}

