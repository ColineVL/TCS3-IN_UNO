package serveur;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Partie de UNO en plusieurs manches.
 * @author Coline van Leeuwen
 *
 */
public abstract class Partie implements Runnable {
	
	/** Contient les sockets vers tous les clients connectes */
	protected ArrayList<LienAvecClient> listPlayers;

	/**
	 * Lance la partie.
	 * @throws IOException
	 */
	public abstract void lancerPartie() throws IOException;

	
	/**
	 * Methode obligatoire pour realiser Runnable.
	 */
	@Override
	public void run() {
		try {
			lancerPartie();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Envoie un message a tous les clients
	 * 
	 * @param message Le message a envoyer.
	 */
	private void sendAllClients(String message) {
		for (LienAvecClient j : listPlayers) {
			j.sendString(message);
		}
	}
	
	/**
	 * Classe listeJoueurs en fonction des scores des joueurs, et envoie le
	 * classement a tous les clients.
	 */
	protected void etablirEnvoyerClassement() {
		String message = "fin-de-partie";
		ArrayList<LienAvecClient> classement = new ArrayList<LienAvecClient>();
		int pointDeComparaison;
		
		// Classe en fonction des scores
		
		if (listPlayers.size()==0) {
			return;
		}
		
		// Il y a au moins un joueur restant
		classement.add(listPlayers.get(0));
		for (int i = 1; i < listPlayers.size(); i++) {
			int index = 0;
			
			if (listPlayers.get(i).getScore() == 0) {
				classement.add(listPlayers.get(i));
			}
			
			else {
				pointDeComparaison = classement.get(index).getScore();
				while (pointDeComparaison > listPlayers.get(i).getScore()) {
					index += 1;
					pointDeComparaison = listPlayers.get(index).getScore();
				}
				classement.add(index, listPlayers.get(i));
			}	
		}
		
		// Envoie le classement a tous les joueurs
		for (LienAvecClient l : classement) {
			message += " " + l.getName() + " " + l.getScore();
		}
		sendAllClients(message);
	}


}
