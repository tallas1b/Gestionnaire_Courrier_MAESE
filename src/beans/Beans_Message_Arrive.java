package beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import database.Query;
import util.DateFormater;

public class Beans_Message_Arrive {

	private Integer numero_arrive;
	private String expediteur;
	private String mention;
	private LocalDate date;
	private String objet_message;
	private String type_message;
	private Integer numero_d_ordre_expediteur;
	private String crypto_systeme;
	private String nom_fichier;


	public Beans_Message_Arrive() {
		super();
	}


	public Beans_Message_Arrive(Integer numero_arrive, String expediteur, String mention, LocalDate date,
			String objet_message, String type_message , Integer numero_d_ordre_expediteur , String crypto_systeme , String nom_fichier) {
		super();
		this.numero_arrive = numero_arrive;
		this.expediteur = expediteur;
		this.mention = mention;
		this.date = date;
		//on remplace les ' par * car sinon probléme insertion dans la base de donnéé. regarder bean message depart
		this.objet_message = objet_message.replace("'", "*");
		this.setReseau(type_message);
		this.numero_d_ordre_expediteur = numero_d_ordre_expediteur;
		this.crypto_systeme = crypto_systeme;
		this.nom_fichier = nom_fichier;
	}





	public Integer getNumero_arrive() {
		return numero_arrive;
	}


	public void setNumero_arrive(Integer numero_arrive) {
		this.numero_arrive = numero_arrive;
	}


	public String getExpediteur() {
		return expediteur;
	}


	public void setExpediteur(String expediteur) {
		this.expediteur = expediteur;
	}


	public String getMention() {
		return mention;
	}


	public void setMention(String mention) {
		this.mention = mention;
	}


	public LocalDate getDate() {
		return date;
	}


	public void setDate(LocalDate date) {
		this.date = date;
	}


	public String getObjet_message() {
		return objet_message;
	}


	public void setObjet_message(String objet_message) {
		this.objet_message = objet_message;
	}


	public Integer getNumero_d_ordre_expediteur() {
		return numero_d_ordre_expediteur;
	}


	public void setNumero_d_ordre_expediteur(Integer numero_d_ordre_expediteur) {
		this.numero_d_ordre_expediteur = numero_d_ordre_expediteur;
	}


	public String getCrypto_systeme() {
		return crypto_systeme;
	}


	public void setCrypto_systeme(String crypto_systeme) {
		this.crypto_systeme = crypto_systeme;
	}

	public String getReseau() {
		return type_message;
	}


	public void setReseau(String reseau) {
		this.type_message = reseau;
	}

	public String getType_message() {
		return type_message;
	}


	public void setType_message(String type_message) {
		this.type_message = type_message;
	}


	public String getNom_fichier() {
		return nom_fichier;
	}


	public void setNom_fichier(String nom_fichier) {
		this.nom_fichier = nom_fichier;
	}


	@Override
	public String toString() {
		return "Beans_Message_Arrive [numero_arrive=" + numero_arrive + ", expediteur=" + expediteur + ", mention="
				+ mention + ", date=" + date + ", objet_message=" + objet_message + ", type_message=" + type_message
				+ ", numero_d_ordre_expediteur=" + numero_d_ordre_expediteur + ", crypto_systeme=" + crypto_systeme
				+ ", nom_fichier=" + nom_fichier + "]";
	}


	//LES ARRIVEES
	public String formatToDatabase() {
		String sql = "INSERT INTO MessageArrive(expediteur,mention,date,nom_fichier,objet,numero_arrive,numero_ordre,reseau,crypto_systeme) VALUES "
				+ "('"  + expediteur +     "','"
				+ mention +          "','"
				+ DateFormater.DateToString_database(date)+"','"
				+nom_fichier + "','"
				+ objet_message+"','"
				+ numero_arrive+"','"
				+ numero_d_ordre_expediteur+"','"
				+ type_message+"','"
				+crypto_systeme+"');";
		return sql;
	}

	public String formatToDatabase_officiel() {
		String sql = "INSERT INTO Message_Officiel_Arrive(date,objet,numero_ordre) VALUES ('"
				+ DateFormater.DateToString_database(date)+ "','"
				+objet_message+ "',"
				+numero_d_ordre_expediteur + ");";
		return sql;
	}

	public String formatToDatabase_divers() {
		String sql = "INSERT INTO Message_Divers_Arrive(date,objet,numero_ordre,expediteur) VALUES ('"
				+ DateFormater.DateToString_database(date)+ "','"
				+objet_message+ "',"
				+numero_d_ordre_expediteur +",'"
				+expediteur +"');";
		return sql;
	}

	public String formatToDatabase_conf() {
		String sql = "INSERT INTO MessageArriveConf(date,objet,numero_ordre,expediteur) VALUES ('"
				+ DateFormater.DateToString_database(date)+ "','"
				+objet_message+ "',"
				+numero_d_ordre_expediteur +",'"
				+expediteur +"');";
		return sql;
	}

	public String formatToDatabase_secret() {
		String sql = "INSERT INTO MessageArriveSecret(date,objet,numero_ordre,expediteur) VALUES ('"
				+ DateFormater.DateToString_database(date)+ "','"
				+objet_message+ "',"
				+numero_d_ordre_expediteur +",'"
				+expediteur +"');";
		return sql;
	}



	// LES UPDATES   ************************************************************************

	//LES UPDATES
	public String format_Update_Database() {
		String sql_update = "UPDATE MessageArrive SET  expediteur = " + "'" + expediteur + "'," +
				"date = "       + "'" + DateFormater.DateToString_database(date) +"'," +
				"mention = "+"'"+mention+"',"+
				"objet = "+"'"+objet_message+"',"+
				"reseau = "+"'"+type_message+"',"+
				"crypto_systeme = "+"'"+crypto_systeme+"'"+
				" WHERE numero_ordre = "+ numero_d_ordre_expediteur +" ;";
		System.out.println(sql_update);
		return sql_update;
	}


	public String format_Update_Arrive_officiel_Database() {
		String sql_update = "UPDATE Message_Officiel_Arrive SET " +
				"date = "       + "'" + DateFormater.DateToString_database(date) +"'," +
				"objet = "+"'"+objet_message+"'"+
				" WHERE numero_ordre = "+ numero_d_ordre_expediteur +" ;";
		System.out.println(sql_update);
		return sql_update;
	}

	public String format_Update_Arrive_divers_Database() {
		String sql_update = "UPDATE Message_Divers_Arrive SET expediteur = " + "'" + expediteur + "'," +
				"date = "       + "'" + DateFormater.DateToString_database(date) +"'," +
				"objet = "+"'"+objet_message+"'"+
				" WHERE numero_ordre = "+ numero_d_ordre_expediteur +" ;";
		System.out.println(sql_update);
		return sql_update;
	}

	public String format_Update_Arrive_conf_Database() {
		String sql_update = "UPDATE MessageArriveConf SET expediteur = " + "'" + expediteur + "'," +
				"date = "       + "'" + DateFormater.DateToString_database(date) +"'," +
				"objet = "+"'"+objet_message+"'"+
				" WHERE numero_ordre = "+ numero_d_ordre_expediteur +" ;";
		System.out.println(sql_update);
		return sql_update;
	}

	public String format_Update_Arrive_secret_Database() {
		String sql_update = "UPDATE MessageArriveSecret SET expediteur = " + "'" + expediteur + "'," +
				"date = "       + "'" + DateFormater.DateToString_database(date) +"'," +
				"objet = "+"'"+objet_message+"'"+
				" WHERE numero_ordre = "+ numero_d_ordre_expediteur +" ;";
		System.out.println(sql_update);
		return sql_update;
	}

	//****************************************************

	// LES LECTURES ARRIVEES

	public boolean formatFromDatabase( ResultSet rs ) {

		try {

			// index commence par 1
			//	numero_arrive = rs.getInt(1) + 10000; ********ID*****
			expediteur = rs.getString(2);
			mention = rs.getString(3);
			date = DateFormater.StringToDate(rs.getString(4));//a formater pour date
			nom_fichier = rs.getString(5);
			objet_message = rs.getString(6).replace("*", "'");;
			numero_arrive = rs.getInt(7);
			numero_d_ordre_expediteur = rs.getInt(8);
			type_message = rs.getString(9);
			crypto_systeme = rs.getString(10);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}


	public boolean formatFromDatabase_officiel( ResultSet rs ) {

		try {

			//regarder dans create table
			date = DateFormater.StringToDate(rs.getString(2));//a formater pour date
			objet_message = rs.getString(3).replace("*", "'");;
			numero_d_ordre_expediteur = rs.getInt(4);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean formatFromDatabase_Divers( ResultSet rs ) {

		try {

			//regarder dans create table
			date = DateFormater.StringToDate(rs.getString(2));//a formater pour date
			objet_message = rs.getString(3).replace("*", "'");
			numero_d_ordre_expediteur = rs.getInt(4);
			expediteur = rs.getString(5);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public boolean formatFromDatabase_Conf( ResultSet rs ) {

		try {

			//regarder dans create table
			expediteur = rs.getString(5);
			date = DateFormater.StringToDate(rs.getString(2));//a formater pour date
			objet_message = rs.getString(3).replace("*", "'");;
			numero_d_ordre_expediteur = rs.getInt(4);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public boolean formatFromDatabase_Secret( ResultSet rs ) {

		try {

			expediteur = rs.getString(5);
			date = DateFormater.StringToDate(rs.getString(2));//a formater pour date
			objet_message = rs.getString(3).replace("*", "'");;
			numero_d_ordre_expediteur = rs.getInt(4);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}


	public boolean verifie_existance_num_arrive_officiel(){
		//Contrairement au depart on peut avoir deux divers arrive avec le meme num ordre alors on utilise table 
		//officiel pour filtrer et eviter les duplicata en lieu et place de ArriveDiplomail
		String sql = "SELECT * FROM Message_Officiel_Arrive WHERE numero_ordre = "+numero_d_ordre_expediteur;
		boolean empty = false;
		try {
			ResultSet rs = Query.select(sql);
			
			while( rs.next() ) {
			    // ResultSet processing here
			    empty = true;
			}
			Query.close_connection();
			return empty;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return empty;
		
	}
}
