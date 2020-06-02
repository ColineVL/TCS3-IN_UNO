package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import classes_partagees.*;

/**
 * Permet d'echanger des infos avec un client via la socket.
 * 
 * @author Coline van Leeuwen
 */
public class LienAvecClient implements Comparable<LienAvecClient> {
	// Attributs representant le joueur
	private String name;
	private int score;
	private PaquetDeCartes hand;
	// Attributs habituels pour un serveur TCP
	private BufferedReader txtIn;
	private PrintWriter txtOut;

	/**
	 * Constructeur
	 * 
	 * @param socket La socket vers le client.
	 * @throws IOException
	 */
	public LienAvecClient(Socket socket) throws IOException {
		// Attributs habituels pour un serveur TCP
		txtIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		txtOut = new PrintWriter(socket.getOutputStream(), true);
		// Attributs particuliers
		hand = new PaquetDeCartes("vide");
		score = 0;
		String demande = receiveString();
		if (demande.split(" ")[0].equals("je-suis")) {
			name = demande.split(" ")[1];
			System.out.printf("Nouveau client : %s, connecte au port %d\n", name, socket.getPort());
		} else {
			sendString("Erreur : message invalide");
			socket.close();
		}
		
	}

	/**
	 * Envoie un message au client.
	 * 
	 * @param str le message a envoyer.
	 */
	public void sendString(String str) {
		System.out.println("S -> " + name + " : " + str);
		try {
				txtOut.println(str);
		} catch (Exception e) {
			// Le client s'est deconnecte
			// Pas grave, on attrapera l'exception quand ce sera a son tour de jouer
		}
	
	}

	/**
	 * Recoit un message du client.
	 * 
	 * @return le message recu.
	 * @throws IOException
	 */
	public String receiveString() throws IOException {
		String message;
		try {
			message = txtIn.readLine();
		} catch (Exception e) {
			// Le client s'est deconnecte
			return ("Erreur : deconnection");
		}
		System.out.println(name + " -> S : " + message);
		return message;
	}

	/**
	 * Getter
	 * 
	 * @return le score du joueur.
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Getter
	 * 
	 * @return le nom du joueur.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter
	 * 
	 * @return la main du joueur.
	 */
	public PaquetDeCartes getHand() {
		return hand;
	}

	/**
	 * Met a jour le score du joueur.
	 * 
	 * @param n le nombre de points a ajouter.
	 */
	public void addScore(int n) {
		this.score += n;
	}

	/**
	 * Compare deux LienAvecCLient A et B : A est plus petit que B si son score est
	 * plus petit que celui de B.
	 * 
	 * @param B Le LienAvecCLient a comparer a A
	 * @return -1 si A<B, 0 si A=B, 1 si A>B.
	 */
	public int compareTo(LienAvecClient B) {
		if (this.score < B.score) {
			return -1;
		} else if (this.score > B.score) {
			return 1;
		} else {
			return 0;
		}
	}

}
