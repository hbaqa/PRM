/**
 * 
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import serveur.Defi;
import serveur.Resultat;

/**
 * @author Di�genes
 *
 */
public class Client1 implements MessageListener {

	/**
	 * @param args
	 * @throws NamingException 
	 * @throws JMSException 
	 */
	public static void main(String[] args) throws NamingException, JMSException {
		
		Client1 client1 = new Client1();
		
		Context context = Client1.getInitialContext();
		
		Queue queue01 = (Queue) context.lookup("Queue01"); // emission
		Queue queue02 = (Queue) context.lookup("Queue02"); // reception

		JMSContext jmsContext = ((ConnectionFactory) context.lookup("GFConnectionFactory")).createContext();
		jmsContext.createConsumer(queue02).setMessageListener(client1);

		JMSProducer jmsProducer = jmsContext.createProducer();
		
		// envoi du defi
		//Defi defi = new Defi();
		//defi.cote = 1;
		//jmsProducer.send(queue01, defi);
		
		// envoi du resulat
		Resultat Resultat = new Resultat();
		Resultat.cote = 1;
		jmsProducer.send(queue01, Resultat);
		
		while(true){}
	}
	
	public static Context getInitialContext() throws JMSException, NamingException {
		Properties properties = new Properties();
		properties.setProperty("java.naming.factory.initial", "com.sun.enterprise.naming.SerialInitContextFactory");
		properties.setProperty("java.naming.factory.url.pkgs", "com.sun.enterprise.naming");
		properties.setProperty("java.naming.provider.url", "iiop://localhost:3700");
		return new InitialContext(properties);
	}
	
	public void repondre() throws IOException, NamingException, JMSException{
		// initialisations
		Context context = Client1.getInitialContext();
		JMSContext jmsContext = ((ConnectionFactory) context.lookup("GFConnectionFactory")).createContext();
		JMSProducer jmsProducer = jmsContext.createProducer();
		Queue queue01 = (Queue) context.lookup("Queue01"); // emission
		
		// recuperer l'input
		BufferedReader bufferedReader = new java.io.BufferedReader(new InputStreamReader(System.in));
		String messageToSend = null;
		messageToSend = bufferedReader.readLine();
		// evoyer le message par rapport a l'input
		if (messageToSend.equalsIgnoreCase("exit")) {
			jmsContext.close();
			System.out.println("GoodBye");
			System.exit(0);
		} else if (messageToSend.contains("oui")) {
			// envoyer reponse positive
			Defi defi = new Defi();
			defi.cote = 1;
			defi.positive = true;
			defi.response = true;
			jmsProducer.send(queue01, defi);
		} else if (messageToSend.contains("non")) {
			// envoyer reponse negative
			Defi defi = new Defi();
			defi.cote = 1;
			defi.positive = false;
			defi.response = true;
			jmsProducer.send(queue01, defi);
		}
	}
	
	public void repondre_resultat() throws IOException, NamingException, JMSException{
		// initialisations
		Context context = Client1.getInitialContext();
		JMSContext jmsContext = ((ConnectionFactory) context.lookup("GFConnectionFactory")).createContext();
		JMSProducer jmsProducer = jmsContext.createProducer();
		Queue queue01 = (Queue) context.lookup("Queue01"); // emission
		
		// recuperer l'input
		BufferedReader bufferedReader = new java.io.BufferedReader(new InputStreamReader(System.in));
		String messageToSend = null;
		messageToSend = bufferedReader.readLine();
		// evoyer le message par rapport a l'input
		if (messageToSend.equalsIgnoreCase("exit")) {
			jmsContext.close();
			System.out.println("GoodBye");
			System.exit(0);
		} else if (messageToSend.contains("-")) {
			// envoyer reponse positive
			Resultat resultat = new Resultat();
			resultat.valide = true;
			jmsProducer.send(queue01, resultat);
		} else if (messageToSend.contains("oui")) {
			// envoyer reponse positive
			Resultat resultat = new Resultat();
			resultat.cote = 1;
			resultat.positive = true;
			resultat.response = true;
			resultat.valide = true;
			jmsProducer.send(queue01, resultat);
		} else if (messageToSend.contains("non")) {
			// envoyer reponse negative
			Resultat resultat = new Resultat();
			resultat.cote = 1;
			resultat.positive = false;
			resultat.response = true;
			resultat.valide = true;
			jmsProducer.send(queue01, resultat);
		}
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof ObjectMessage){
			ObjectMessage om = ((ObjectMessage) message);
			try {
				//Defi
				Class<?> c = om.getObject().getClass();
				if (c == Defi.class){
					Defi object = (Defi) om.getBody(c);
					// System.out.println("tada - defi" + object.cote);
					if (!object.response){
						try {
							repondre();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				//Resultat
				if (c == Resultat.class){
					Resultat object = (Resultat) om.getBody(c);
					// System.out.println("tada - Resultat" + object.cote);
					if (!object.response){
						try {
							repondre_resultat();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
