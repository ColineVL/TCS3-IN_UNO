package serveur;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import classes_partagees.*;

/**
 * Joue une manche de UNO.
 * 
 * @author Coline van Leeuwen
 */
public class Manche {
	
	// Objets physiques
	private ArrayList<LienAvecClient> listPlayers;
	private PaquetDeCartes deck;
	private PaquetDeCartes discard;
	// Effets des cartes
	private int order;
	private boolean skip;
	private Couleur colorChoice;
	// Utile pour le code
	private boolean roundRunning;
	private LienAvecClient currentPlayer;
	private int indexCurrentPlayer;
	private Carte defaultCard;
	
	/**
	 * Constructeur : cree la pile, distribue des cartes aux joueurs, determine le
	 * premier joueur (au hasard).
	 * 
	 * @param listeJoueurs La liste des sockets des clients
	 * @throws IOException 
	 */
	public Manche(ArrayList<LienAvecClient> listeJoueurs) throws IOException {
		// Initialisation des attributs
		this.listPlayers = listeJoueurs;
		roundRunning = true;
		defaultCard = new Normale(Couleur.noir, "-1");
		// Effets des cartes : nuls pour l'instant
		order = 1;
		skip = false;
		colorChoice = Couleur.noir;
		// Premier joueur
		indexCurrentPlayer = randomInt(0, listeJoueurs.size() - 1);
		currentPlayer = listeJoueurs.get(indexCurrentPlayer);
		// Preparation de la pioche
		deck = new PaquetDeCartes("entier");
		deck.shuffle();
		// Distribution des cartes aux joueurs
		for (LienAvecClient j : listeJoueurs) {
			j.getHand().clear();
			j.sendString("debut-de-manche");
			for (int i = 0; i < 7; i++) {
				Carte carte = piocher();
				j.sendString("prends " + carte);
				j.getHand().add(carte);
			}
		}
		// Preparation du talon
		discard = new PaquetDeCartes("vide");
		Carte carteTalon = piocher();
		do {
			deck.add(carteTalon);
			deck.shuffle();
			carteTalon = piocher();
		} while (carteTalon.getValue().equals("+4"));
		discard.add(carteTalon);
		sendAllClients("nouveau-talon " + carteTalon + " yes");
		appliquerEffets(carteTalon);
	}

	/**
	 * Envoie a tous les clients le meme message.
	 * 
	 * @param message Le message a envoyer.
	 */
	private void sendAllClients(String message) {
		for (LienAvecClient j : listPlayers) {
			j.sendString(message);
		}
	}

	/**
	 * Prend la premiere carte de la pioche. Si la pioche est vide, melange le talon
	 * et forme une nouvelle pioche.
	 * 
	 * @return La premiere carte de la pioche.
	 */
	private Carte piocher() {
		if (deck.size() == 0) {
			Carte carteTalon = discard.getCarteTalon();
			discard.remove(carteTalon);
			if (discard.size() == 0) {
				sendAllClients("Erreur : toutes les cartes sont dans les mains des joueurs");
				return null;
			}
			deck.addAll(discard);
			discard.clear();
			discard.add(carteTalon);
			deck.shuffle();
		}
		Carte carte = deck.getDeck().get(0);
		deck.getDeck().remove(0);
		return carte;
	}

	/**
	 * Calcule quel est le joueur suivant.
	 * 
	 * @param indexJoueurEnCours L'index du joueur en train de jouer
	 * @return Le numero du joueur suivant.
	 */
	private int indexJoueurSuivant(int indexJoueurEnCours) {
		int indexNext = indexJoueurEnCours + order;
		indexNext = mod(indexNext, listPlayers.size());
		return indexNext;
	}

	/**
	 * Calcule x modulo y en ayant un resultat toujours positif.
	 * 
	 * @param x Le dividende.
	 * @param y Le diviseur.
	 * @return Le reste de la division euclidenne de x par y.
	 */
	private int mod(int x, int y) {
		int result = x % y;
		if (result < 0) {
			result += y;
		}
		return result;
	}

	/**
	 * Permet de determiner par le hasard qui est le premier joueur.
	 * 
	 * @param min L'index minimum.
	 * @param max L'index maximum.
	 * @return Un entier aleatoire entre min et max inclus.
	 */
	private int randomInt(int min, int max) {
		Random rand = new Random();
		return rand.nextInt((max - min) + 1) + min;
	}

	/**
	 * Applique dans le jeu les effets d'une carte.
	 * 
	 * @param cartePosee La carte dont il faut appliquer les effets.
	 * @throws IOException 
	 */
	private void appliquerEffets(Carte cartePosee) throws IOException {
		String[] effets = cartePosee.quelsEffets();
		// effets est de la forme {saute(true/false), prends(0,2,4), sens(1=unchanged,
		// -1= changed)}
		
		// Effets saute et changement de sens
		
		skip = Boolean.valueOf(effets[0]);
		order = order * Integer.parseInt(effets[2]);
		// cas particulier de la partie a deux joueurs 
		if (effets[2].equals("-1") && listPlayers.size() == 2) {
			skip = true;
		}
		
		// Effet du +2 ou +4
		
		int nbCartesAPrendre = Integer.parseInt(effets[1]);
		int indexNext = indexJoueurSuivant(indexCurrentPlayer);
		LienAvecClient jNext = listPlayers.get(indexNext);
		LienAvecClient jQuiPioche = jNext;
		
		// Contestation du +4
		
		if (nbCartesAPrendre == 4) {
			// Demander au joueur suivant s'il veut contester le +4
			jNext.sendString("conteste");
			String rep = jNext.receiveString();
			if (rep.equals("Erreur : deconnection")) {
				deconnecter(jNext);
			}
			// Si il veut contester : verifier la main du joueur qui a joue +4
			if (rep.equalsIgnoreCase("oui")) {
				System.out.print(jNext.getName() + " conteste le +4");
				// Si le +4 etait bien legal 
				if (plus4Legal(discard.get(discard.size()-2).getColor(), currentPlayer.getHand())) {
					nbCartesAPrendre = 6;
					sendAllClients("joueur " + jNext.getName() + " conteste faux");
					System.out.println(" mais se trompe");
				}
				// Si le +4 etait illegal ou qu'il y a eu une erreur
				else {
					sendAllClients("joueur " + jNext.getName() + " conteste juste");
					System.out.println(" et a raison");
					jQuiPioche = currentPlayer;
					skip = false;
				}
			}
		}
				
		// Effets du +2 et du +4 : on fait piocher le bon joueur
		
		if (nbCartesAPrendre != 0) { 
			sendAllClients("joueur " + jQuiPioche.getName() + " pioche " + nbCartesAPrendre);
			for (int i = 0; i < nbCartesAPrendre; i++) {
				Carte cartePioche = piocher();
				if (!(cartePioche == null)) {
					jQuiPioche.sendString("prends " + cartePioche);
					jQuiPioche.getHand().add(cartePioche);
				}
				
			}	
		}
		
		// Calcul du joueur suivant
		
		if (!skip) {
			indexCurrentPlayer = indexNext;			
		}
		else {
			sendAllClients("joueur " + listPlayers.get(indexNext).getName() + " passe");
			indexCurrentPlayer = indexJoueurSuivant(indexNext);
			skip = false;
		}
	}

	/**
	 * Methode utilitaire pour la contestation du +4 : verifie dans la main du joueur s'il avait bien le droit de poser ce +4
	 * @param color 	La couleur sur laquelle il a joue le +4
	 * @param deck 		Sa main
	 * @return 			true si et seulement si il n'y a aucune carte de couleur color dans deck
	 */
	private boolean plus4Legal(Couleur color, PaquetDeCartes deck) {
		for (int i = 0; i < deck.size(); i++) {
			Carte carte = deck.get(i);
			if (carte.getColor() == color) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Donne deux cartes au joueur j.
	 */
	private void penalite(LienAvecClient j, int nbCartes) {
		for (int i = 0; i < nbCartes; i++) {
			Carte carte = piocher();
			if (!(carte == null)) {
				j.sendString("prends " + carte);
				j.getHand().add(carte);
			}
		}
	}

	/**
	 * Lance la manche, gere les tours des joueurs, echange avec les clients.
	 * 
	 * @throws IOException
	 */
	public void lancerManche() throws IOException {
		while (roundRunning) {
			// Qui joue ?
			currentPlayer = listPlayers.get(indexCurrentPlayer);
			sendAllClients("joueur " + currentPlayer.getName() + " joue");

			// Que veut-il faire ?
			currentPlayer.sendString("joue");
			String reponse = currentPlayer.receiveString();
			String[] tab = reponse.split(" ");

			// Il y a eu une erreur : deconnection ou message invalide
			if (reponse.equals("Erreur : deconnection")) {
				deconnecter(currentPlayer);
			}
			else if (!(tab[0].equals("je-pioche") || tab[0].equals("je-pose"))) {
				currentPlayer.sendString("Erreur : message invalide");
				penalite(currentPlayer, 1);
				appliquerEffets(defaultCard);
			}
			
			// Le joueur pioche
			if (tab[0].equals("je-pioche")) {
				Carte cartePioche = piocher();
				if (! (cartePioche == null) ) {
					currentPlayer.sendString("prends " + cartePioche);
					currentPlayer.getHand().add(cartePioche);
					currentPlayer.sendString("joue");
					String rep = currentPlayer.receiveString();
					tab = rep.split(" ");
					// En cas d'erreur
					if (rep.equals("Erreur : deconnection")) {
						deconnecter(currentPlayer);
					}
					if (!(tab[0].equals("je-passe") || tab[0].equals("je-pose") || (tab.length != 2))) {
						currentPlayer.sendString("Erreur : message invalide");
						penalite(currentPlayer, 1);
						appliquerEffets(defaultCard);
					}
					// Le joueur a triche et ne pose pas la carte qu'il vient de piocher
					if (! (tab[1].equals(cartePioche.toString())) ) {
						sendAllClients("joueur " + currentPlayer.getName() + " triche");
						penalite(currentPlayer, 2);
						appliquerEffets(defaultCard);
					}
					// Le joueur passe.
					if (tab[0].equals("je-passe")) {
						currentPlayer.sendString("OK");
						sendAllClients("joueur " + currentPlayer.getName() + " pioche 1");
						appliquerEffets(defaultCard);
					}
				}
				
			}
			
			// Le joueur pose une carte
			if (tab[0].equals("je-pose")) {
				Carte cartePosee = defaultCard;
				try {
					cartePosee = Carte.nouvelleCarte(tab[1]);
				} catch (Exception e) {
					// Le client a envoye un message incorrect
					currentPlayer.sendString("Erreur : message invalide");
					penalite(currentPlayer, 1);
					appliquerEffets(defaultCard);
				}
				// On verifie que le joueur a bien cette carte dans sa main
				if (!currentPlayer.getHand().contains(cartePosee)) {
					sendAllClients("joueur " + currentPlayer.getName() + " triche");
					penalite(currentPlayer, 2);
					appliquerEffets(defaultCard);
				}
				// On verifie que la carte peut etre jouee sur le talon
				else if (!cartePosee.peutEtreJoueeSur(discard.getCarteTalon(), colorChoice)) {
					sendAllClients("joueur " + currentPlayer.getName() + " triche");
					currentPlayer.sendString("prends " + cartePosee);
					penalite(currentPlayer, 2);
					appliquerEffets(defaultCard);
				}

				// La carte peut etre jouee
				else {
					currentPlayer.getHand().remove(cartePosee);
					if (cartePosee.getValue().equals("Joker") || cartePosee.getValue().equals("+4")) {
						System.out.println(
								currentPlayer.getName() + " pose la carte " + cartePosee + "-" + cartePosee.getColor());
						colorChoice = cartePosee.getColor();
						currentPlayer.sendString("OK");
						sendAllClients("joueur " + currentPlayer.getName() + " pose " + cartePosee + "-"
								+ cartePosee.getColor());
					} else {
						System.out.println(currentPlayer.getName() + " pose la carte " + cartePosee);
						currentPlayer.sendString("OK");
						sendAllClients("joueur " + currentPlayer.getName() + " pose " + cartePosee);
					}
					discard.add(cartePosee);
					sendAllClients("nouveau-talon " + cartePosee + " non");
					appliquerEffets(cartePosee);
				}
			}

			// Le serveur envoie a tous les clients le nombre de cartes de chaque joueur.
			for (LienAvecClient l : listPlayers) {
				sendAllClients("joueur " + l.getName() + " aCartes " + l.getHand().size());
			}

			// Cas ou c'est une fin de manche : un joueur n'a plus de carte
			if (currentPlayer.getHand().size() == 0) {
				roundRunning = false;
			}
		}
		
		// C'est la fin de la manche
		for (LienAvecClient j : listPlayers) {
			for (int i = 0; i < j.getHand().size(); i++) {
				currentPlayer.addScore(j.getHand().get(i).getScore());
			}
		}
		String txt = "fin-de-manche";
		for (LienAvecClient j : listPlayers) {
			txt += " " + j.getName() + " " + j.getScore();
		}
		sendAllClients(txt);
	}

	/**
	 * Si un joueur quitte le jeu en cours de partie, on fait en sorte que le jeu continue sans lui
	 * @param joueur Le joueur qui quitte la partie
	 */
	private void deconnecter(LienAvecClient joueur) {
		int indexNext = indexJoueurSuivant(indexCurrentPlayer);
		LienAvecClient jNext = listPlayers.get(indexNext);
		// On reprend ses cartes 
		deck.addAll(joueur.getHand());
		deck.shuffle();
		// On le retire de la partie
		listPlayers.remove(joueur);
		// On recalcule l'indice du joueur en cours
		indexCurrentPlayer = listPlayers.indexOf(jNext);
		// On previent tout le monde
		System.out.println(joueur.getName() + " s'est deconnecte");
		sendAllClients("joueur " + joueur.getName() + " quitte");
		if (listPlayers.size() == 0) {
			// Il n'y a plus de clients connectes
			System.out.println("Tous les clients sont deconnectes");
			roundRunning = false;
		}
	}

}
