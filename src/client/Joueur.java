package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

import classes_partagees.*;

/**
 * Classe Joueur : fait le lien entre le serveur et l'utilisateur via la
 * console.
 * 
 * @author Coline van Leeuwen
 */
public class Joueur {
	// Attributs pour decrire le joueur
	private String name;
	private PaquetDeCartes hand;
	private Carte discardCard;
	private boolean autoSort;
	// Attributs utiles pour le code informatique.
	private boolean gameRunning;
	private boolean iJustDrew;
	private Carte lastCardPicked;
	// Attributs habituels de client TCP
	private Socket socket;
	private BufferedReader txtIn;
	private PrintWriter txtOut;
	private Scanner inputScanner;

	/**
	 * COnstructeur.
	 * 
	 * @param host L'adresse IP du serveur.
	 * @param port Le port du serveur.
	 * @throws IOException
	 */
	public Joueur() throws IOException {
		// Scanner
		inputScanner = new Scanner(System.in);
		// Attributs pour le jeu
		gameRunning = true;
		iJustDrew = false;
		this.hand = new PaquetDeCartes("vide");
	
		// Saisie de name sur la console par l'utilisateur
		System.out.println("Bienvenue dans ce jeu de UNO !");
		System.out.print("Comment vous appellez-vous ? ");
		name = inputScanner.nextLine();
		while (name.split(" ").length != 1 || name.equals("")) {
			System.out.print("Veuillez entrer un nom sans espace, merci ! ");
			name = inputScanner.nextLine();
		}
		
		// Saisie de l'adresseIP sur la console par l'utilisateur
		String host;
		System.out.print(
				"Tapez l'adresse IP du serveur de jeu (appuyez sur entrée pour l'adresse par défaut : 127.0.0.1) : ");
		host = inputScanner.nextLine();
		while (! estValideIP(host)) {
			System.out.println("Veuillez entrer une IP valide ! ");
			host = inputScanner.nextLine();
		}
		if (host.equals("")) {
			host = "127.0.0.1";
		}
		
		// Saisie du port sur la console par l'utilisateur
		boolean pasCorrect = true;
		int port = 0;
		while (pasCorrect) {
			System.out.print("Port du serveur (appuyez sur entrée pour le port par défaut : 6789) ? ");
			String helpString = inputScanner.nextLine();
			if (helpString.equals("")) {
				port = 6789;
				pasCorrect = false;
			} else {
				try {
					port = Integer.parseInt(helpString);
					if (port > 0) {
						pasCorrect = false;
					}
				} catch (Exception e) {
					pasCorrect = true;
				}
			}
		}
		
		// Attributs habituels de client TCP
		try {
			socket = new Socket(host, port);
		} catch (Exception e) {
			System.out.println("\nConnection au serveur impossible. Etes-vous sur que le port est correct ?");
			return;
		}
		txtIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		txtOut = new PrintWriter(socket.getOutputStream(), true);
		System.out.printf("Vous etes bien connecte au serveur, port local %d\n", socket.getLocalPort());
	
		// autoSort
		String input;
		do {
			System.out.print("Voulez-vous que je trie automatiquement vos cartes ? oui/non ");
			input = inputScanner.nextLine();
		} while (!(input.equalsIgnoreCase("oui") || input.equalsIgnoreCase("non")));
		if (input.equals("oui")) {
			autoSort = true;
		} else {
			autoSort = false;
		}
		
		// Fini !
		System.out.println("Tout est pret !");
	}

	/**
	 * Permet de verifier que la saisie de l'utilisateur ne fera pas d'erreurs.
	 * @param host L'entree de l'utilisateur.
	 * @return true si et seulement si host represente bien une adresse IP.
	 */
	private boolean estValideIP(String host) {
		String[] tab = host.split(".");
		if (host.contentEquals("")) {
			return true;
		}
		if (tab.length != 4) {
			return false;
		}
		int nb;
		for (String str : tab) {
			try {
				nb = Integer.parseInt(str);
				if (nb<0 && nb>255) {
					return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Getter
	 * 
	 * @return le nom de l'utilisateur.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter
	 * 
	 * @return gameRunning.
	 */
	public boolean getGameRunning() {
		return gameRunning;
	}

	/**
	 * Envoie un message au serveur.
	 * 
	 * @param str le message a envoyer.
	 */
	public void sendString(String str) {
		txtOut.println(str);
	}

	/**
	 * Recoit un message du serveur.
	 * 
	 * @return le message recu.
	 * @throws IOException
	 */
	public String receiveString() throws IOException {
		String message = txtIn.readLine();
		return message;
	}

	/**
	 * Deconnecte le joueur.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		inputScanner.close();
		socket.close();
	}

	/**
	 * Ajoute la carte specifiee dans la main du joueur.
	 * 
	 * @param str Nom de carte sous la forme "prends Valeur-couleur".
	 */
	public void prendre(String str) {
		Carte carte = Carte.nouvelleCarte(str.split(" ")[1]);
		hand.add(carte);
		lastCardPicked = carte;
		System.out.println("	Nouvelle carte dans votre main : " + carte);
	}

	/**
	 * Verifie que l'utilisateur n'a pas fait une erreur de saisie.
	 * 
	 * @param clientInput Ce que saisit l'utilisateur dans la console.
	 * @return true si et seulement si la saisie est correcte.
	 */
	private boolean correct(String clientInput) {
		String[] tab = clientInput.split("-");
		boolean flag = true;
		// Cas 1 : l'utilisateur a voulu dire "piocher" ou "joker" ou "+4"
		if (tab.length == 1) {
			if (clientInput.equals("piocher")) {
				return true;
			}
			if (clientInput.equals("Joker") || clientInput.equals("+4")) {
				flag = hand.contains(Carte.nouvelleCarte(clientInput));
				if (!flag) {
					System.out.print("Cette carte n'est pas dans votre main ! ");
				}
				return flag;
			}
			return false;
		}
		// Cas 2 : l'utilisateur a voulu ecrire une carte sous la forme valeur-couleur
		else if (tab.length == 2) {
			// Si la carte existe bien...
			if (Carte.existe(clientInput)) {
				// On verifie qu'elle est bien dans la main du joueur
				flag = hand.contains(Carte.nouvelleCarte(clientInput));
				if (!flag) {
					System.out.print("Cette carte n'est pas dans votre main ! ");
				}
				return flag;
			}
			// Si elle n'existe pas...
			else {
				System.out.print("Cette carte n'existe pas ! ");
			}
		}
		// Cas 3 : l'utilisateur a fait n'importe quoi
		return false;
	}

	/**
	 * Gere le message envoye par le serveur.
	 * 
	 * @throws IOException
	 */
	public void gererMessage() throws IOException {
		String messageDuServeur = "";
		try {
			messageDuServeur = receiveString();
		}
		catch (Exception e) {
			// Le serveur est deconnecte.
			System.out.println();
			System.out.println("--------------");
			System.out.println("Erreur : connection au serveur impossible.");
			System.out.println("Je vous déconnecte.");
			System.out.println("--------------");
			gameRunning = false;
		}
		String[] fields = messageDuServeur.split(" ");
		switch (fields[0].trim()) {

		case "debut-de-manche":
			// On vide la main du joueur et on affiche le message sur la console.
			hand.clear();
			System.out.println();
			System.out.println();
			System.out.println("NOUVELLE MANCHE");
			System.out.println();
			System.out.println("Distribution des cartes...");
			break;

		case "fin-de-manche":
			// On affiche les scores.
			System.out.println();
			System.out.println();
			System.out.println("FIN DE MANCHE !");
			System.out.println();
			String[] scores = Arrays.copyOfRange(fields, 1, fields.length);
			String stringScores = String.join(" ", scores);
			System.out.println("Scores : " + stringScores);
			System.out.println();
			System.out.println();
			System.out.println("______________________________________________________________________");
			break;

		case "fin-de-partie":
			// On affiche les scores et on quitte le serveur.
			System.out.println();
			System.out.println();
			System.out.println("PARTIE TERMINEE ! Voici le classement :");
			for (int i = 1; i < fields.length - 1; i += 2) {
				System.out.println(((i + 1) / 2) + "eme place : " + fields[i] + " avec " + fields[i + 1] + " points !");
			}
			gameRunning = false;
			break;

		case "prends":
			// Le joueur prend une carte.
			prendre(messageDuServeur);
			break;

		case "nouveau-talon":
			// On modifie la derniere carte du talon (la seule que l'on retient en memoire).
			if (fields.length == 3) {
				if (fields[2].equals("yes")) {
					System.out.println("La carte sur le talon est : " + fields[1]);
				}
			}
			discardCard = Carte.nouvelleCarte(fields[1]);
			break;

		case "OK":
			// On ne fait rien.
			break;
			
		case "conteste":
			// Un +4 vient d'etre joue, le serveur me propose de le contester.
			String rep;
			do {
				System.out.print("	Voulez-vous contester le +4 ? oui/non ");
				rep = inputScanner.nextLine();
			} while (! (rep.equalsIgnoreCase("oui") || rep.equalsIgnoreCase("non") ));
			sendString(rep);
			break;

		case "joue":
			// Cas 1 : c'est le debut du tour du joueur.
			if (!iJustDrew) {
				// On demande a l'utilisateur ce qu'il veut jouer.
				System.out.println("	C'est a vous de jouer !");
				System.out.println("	Voici les cartes que vous avez dans votre main :");
				if (autoSort) {
					hand.sort();
				}
				hand.display();
				System.out.println("	La derniere carte du talon est : " + discardCard);
				System.out.print("	Quelle carte voulez-vous jouer ? Entrez valeur-couleur, ou 'piocher' : ");
				String clientInput = inputScanner.nextLine();
				// On verifie que la saisie est correcte.
				while (!correct(clientInput)) {
					System.out.print("Retapez : ");
					clientInput = inputScanner.nextLine();
				}
				// On demande la couleur si c'est un joker ou un +4.
				if (clientInput.equals("Joker") || clientInput.equals("+4")) {
					String couleur;
					do {
						System.out.print("	Quelle couleur choisissez-vous ? ");
						couleur = inputScanner.nextLine();
					} while (!(Couleur.estBienUneCouleur(couleur)));
					clientInput = clientInput + "-" + couleur;
				}
				// Si l'utilisateur a demande a piocher.
				if (clientInput.equals("piocher")) {
					sendString("je-pioche");
					iJustDrew = true;
				}
				// S'il a demande a poser une carte.
				else {
					sendString("je-pose " + clientInput);
					hand.remove(Carte.nouvelleCarte(clientInput));
				}
			}

			// Cas 2 : Le joueur vient de piocher.
			else {
				iJustDrew = false;
				System.out.print("	Voulez-vous la poser ? oui/non ");
				String clientInput = inputScanner.nextLine();
				// On verifie que la saisie est correcte.
				while (!(clientInput.equalsIgnoreCase("oui") || clientInput.equalsIgnoreCase("non"))) {
					System.out.print("Erreur : message invalide. Retapez : ");
					clientInput = inputScanner.nextLine();
				}
				
				// On envoie le bon message au serveur.
				if (clientInput.equals("oui")) {
					// On demande la couleur si c'est un joker ou un +4.
					if (lastCardPicked.getValue().equals("Joker") || lastCardPicked.getValue().equals("+4")) {
						String couleur;
						do {
							System.out.print("	Quelle couleur choisissez-vous ? ");
							couleur = inputScanner.nextLine();
						} while (!(Couleur.estBienUneCouleur(couleur)));
						sendString("je-pose " + lastCardPicked + "-" + couleur);
					} 
					// sinon on envoie juste je-joue derniereCarte
					else {
						sendString("je-pose " + lastCardPicked);
					}
					hand.remove(lastCardPicked);
				} else {
					sendString("je-passe");
					return;
				}
			}
			break;

		case "joueur":
			// Le serveur nous envoie des infos sur le jeu en cours. On affiche un message
			// adapté.
			String nom = fields[1];
			String action = fields[2];

			if (action.equals("joue")) {
				System.out.println();
				System.out.println(nom + " est en train de jouer...");
			}

			if (action.equals("passe")) {
				System.out.println(nom + " a saute son tour !");
			}
			
			if (action.equals("conteste")) {
				System.out.println(nom + " conteste le +4...");
				if (fields[3].equals("juste")) {
					System.out.println("Iel a raison ! Le +4 etait bien illegal !");
				} else {
					System.out.println("Mais iel se trompe... Le +4 etait legal.");
				}
			}

			if (action.equals("pose")) {
				String[] tab = fields[3].split("-");
				if (tab[0].equals("Joker") || tab[0].equals("+4")) {
					System.out.println(nom + " a pose la carte " + tab[0] + " sur le talon et a choisit la couleur "
							+ tab[1] + " ! -------");
				} else {
					System.out.println(nom + " a pose la carte " + fields[3] + " sur le talon !");
				}
			}

			if (action.equals("pioche")) {
				if (fields[3].equals("1")) {
					System.out.println(nom + " a pioche " + fields[3] + " carte !");
				} else {
					System.out.println(nom + " a pioche " + fields[3] + " cartes !");
				}
			}

			if (action.equals("triche")) {
				System.out.println(nom + " a essaye de tricher et prend 2 cartes !");
			}

			if (action.equals("aCartes")) {
				if (fields[3].equals("1")) {
					System.out.println(nom + " a 1 carte et dit UNO !");
				} else if (fields[3].equals("0")) {
					System.out.println(nom + " n'a plus de cartes !");
				} else {
					System.out.println(nom + " a " + fields[3] + " cartes.");
				}
			}
			
			if (action.equals("quitte")) {
				// Un joueur a quitte la partie en cours de jeu.
				System.out.println("** " + nom + " a quitte la partie... Au revoir ! **");
			}
			break;

		case "Erreur":
			// On affiche le message d'erreur.
			System.out.println("	" + messageDuServeur + "...");
			break;

		default:
			System.out.println(messageDuServeur);
			break;

		}

	}

}
