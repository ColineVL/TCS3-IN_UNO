package serveur;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe utilitaire, permet de rejeter des connections de clients
 * retardataires.
 * 
 * @author Coline van Leeuwen
 *
 */
public class RejetRetardataires implements Runnable {

	/** Le serveur auquel on se connecte */
	private ServerSocket serverSocket;

	/**
	 * Constructeur.
	 * 
	 * @param serverSocket Le serveur auquel on se connecte.
	 */
	public RejetRetardataires(ServerSocket serverSocket) {
		super();
		this.serverSocket = serverSocket;
	}

	@Override
	/**
	 * Necessaire pour implementer Runnable. Accepte la connection d'un client
	 * retardataire, lui dit qu'il n'y a plus de place, et le deconnecte.
	 */
	public void run() {
		while (true) {
			Socket socket;
			try {
				socket = serverSocket.accept();
				LienAvecClient LienAvecClient = new LienAvecClient(socket);
				LienAvecClient.sendString("Erreur : plus de place");
				socket.close();
			} catch (IOException e) {
				return;

			}

		}

	}

}
