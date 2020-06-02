package serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * La classe qui lance le serveur et demarre la partie.
 * 
 * @author Coline van Leeuwen
 */
public class Serveur {
	// Attributs de jeu
	private int N;
	private Partie game;
	private ArrayList<LienAvecClient> listPlayers;
	private boolean gameRunning;
	// Attributs habituels de serveur TCP
	private ServerSocket serverSocket;

	/**
	 * Constructeur : allume le serveur et cree une partie.
	 * 
	 * @param argumentPartie Le score a atteindre pour gagner la partie, ou le
	 *                       nombre de manches.
	 * @param N              Le nombre de joueurs autorises.
	 * @param port           Le port du serveur.
	 * @throws IOException
	 */
	public Serveur(int typeDePartie, int argumentPartie, int N, int port) throws IOException {
		this.N = N;
		listPlayers = new ArrayList<LienAvecClient>();
		if (typeDePartie == 1) {
			game = new PartieTypeManches(N, argumentPartie, listPlayers);
		} else {
			game = new PartieTypeScore(N, argumentPartie, listPlayers);
		}
		gameRunning = true;
		serverSocket = new ServerSocket(port);
		System.out.println("En attente de connection de clients sur le port " + serverSocket.getLocalPort());
	}

	/**
	 * Accepte N joueurs, puis, en parallele, lance la partie et refuse les clients
	 * qui arrivent apres le debut de partie.
	 * 
	 * @throws IOException
	 */
	public void accepterNJoueursEtLancerPartie() throws IOException {

		// Accepter N joueurs
		while (listPlayers.size() < N) {
			Socket socket = serverSocket.accept();
			LienAvecClient LienAvecClient = new LienAvecClient(socket);
			LienAvecClient.sendString("bienvenue");
			listPlayers.add(LienAvecClient);
		}

		// Lancer la partie
		Thread partieThread = new Thread(game);
		partieThread.start();

		// Rejeter les clients retardataires
		RejetRetardataires rejet = new RejetRetardataires(serverSocket);
		Thread rejetThread = new Thread(rejet);
		rejetThread.start();

		// Je regarde si la partie est finie
		while (gameRunning) {
			if (partieThread.getState() == Thread.State.TERMINATED) {
				gameRunning = false;
			}
		}

		// Fermeture du serveur si la partie est terminee
		if (!gameRunning) {
			rejetThread.interrupt();
			System.out.println("Deconnection");
			serverSocket.close();
			return;
		}

	}

}
