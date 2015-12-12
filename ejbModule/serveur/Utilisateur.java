package serveur;

import java.io.Serializable;

public interface Utilisateur extends Serializable{
		public String getNom();
		public void setNom(String nom);
		public String getPrenom();
		public void setPrenom(String prenom);
		public String getAdresse () ;
		public void setAdresse ( String adresse ) ;
		public String getTelephone () ;
		public void setTelephone ( String telephone ) ;
		public String getEmail() ;
		public void setEmail( String email) ;
		public void setUserName(String user);
		public String getUserName();
}