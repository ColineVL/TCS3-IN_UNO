package serveur;

import java.io.IOException;
import java.util.Scanner;


/**
 * La classe principale a lancer pour demarrer un serveur de jeu de UNO
 * 
 * @author Coline van Leeuwen
 */
public class ServeurMain {

	private static Scanner inputScanner = new Scanner(System.in);
	/** Nombre de joueurs */
	private static int N;
	/** Le score a atteindre pour gagner la partie, ou le nombre de manches total */
	private static int argumentPartie;
	
	/**
	 * Methode utilitaire : permet d'etre surs que l'entree de l'utilisateur est correcte
	 * @param choixPartie l'entree de l'utilisateur
	 * @return true si et seulement si l'entree est correcte
	 */
	private static boolean inputCorrect(String choixPartie) {
		String[] tab = choixPartie.split(" ");
		if (tab.length != 2) {
			return false;
		}
		if (! ( tab[0].equals("1") || tab[0].equals("2") ) ) {
			return false;
		}
		try {
			argumentPartie = Integer.parseInt(tab[1]);
		} catch (Exception e) {
			return false;
		}
		if (argumentPartie < 1) {
			return false;
		}
		return true;
	}
	
	/**
	 * Fonction principale : lance le jeu de UNO
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		// Utile
		String choixPartie;
		boolean pasCorrect = true;

		// Selection du port par l'utilisateur
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
		
		// Combien de joueurs dans la partie ? 
		pasCorrect = true;
		while (pasCorrect) {
			System.out.print("Nombre de joueurs ? Entre 2 et 9 : ");
			String helpString = inputScanner.nextLine();
			try {
				N = Integer.parseInt(helpString);
				if (N<=9 && N>=2) {
					pasCorrect = false;
				}
			} catch (Exception e) {
				pasCorrect = true;
			}
		}

		// Quel score pour gagner la partie ?
		System.out.println();
		System.out.println("Vous pouvez jouer deux types de partie :");
		System.out.println("   -> (type 1) N manches, le joueur qui a le plus haut score gagne la partie");
		System.out.println("   -> (type 2) le joueur qui atteint un score de N le premier gagne la partie, autant de manches que necessaire");
		System.out.println("Pour jouer une partie de type 1, entrez : 1 nbManchesVoulues");
		System.out.println("Pour jouer une partie de type 2, entrez : 2 scoreAAtteindre");
		System.out.print("Faites votre choix : ");
		choixPartie = inputScanner.nextLine();
		while(! inputCorrect(choixPartie)) {
			System.out.print("Retapez, ce n'est pas correct : ");
			choixPartie = inputScanner.nextLine();
		}
		
		Serveur serveur;
		// Choix partie de type 1
		if (choixPartie.split(" ")[0].equals("1")) {
			serveur = new Serveur(1, argumentPartie, N, port);
		}
		// Choix partie de type 2
		else {
			serveur = new Serveur(2, argumentPartie, N, port);
		}
		// On lance la partie
		serveur.accepterNJoueursEtLancerPartie();

	}

	
}
