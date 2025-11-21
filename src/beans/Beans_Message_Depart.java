package beans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import database.Query;
import util.Constants;
import util.DateFormater;

public class Beans_Message_Depart {

	private Integer numero_depart;
	private String destinataire;
	private String mention;
	private LocalDate date;
	private String objet_message;
	private String type_message;
	private  Integer numero_ordre; // pas dans le insert da la database 
	private String crypto_systeme;
	private String nom_fichier;
	
	private String desti = "";


	public Beans_Message_Depart() {
		super();
	}



	public Beans_Message_Depart(Integer numero_depart, String destinataire, String mention, LocalDate date,
			String objet_message, String type_message, Integer numero_ordre , String crypto_systeme , String nom_fichier) {
		super();
		this.numero_depart = numero_depart;
		this.destinataire = destinataire;
		this.mention = mention;
		this.date = date;
		//on remplace les ' par * car sinon probléme insertion dans la base de donnéé. regarder bean message depart
		this.objet_message = objet_message.replace("'", "*");
		this.type_message = type_message;
		this.numero_ordre = numero_ordre;
		this.crypto_systeme = crypto_systeme;
		this.nom_fichier = nom_fichier;
		
		int index = 0;
		for( String amb : Constants.liste_ambassade) {
			if(amb.equalsIgnoreCase(destinataire)) {
				desti = Constants.liste_ambassade_base_donne[index];
				break;
			}
			index++;
		}
		
	}




	public Integer getNumero_depart() {
		return numero_depart;
	}


	public void setNumero_depart(Integer numero_depart) {
		this.numero_depart = numero_depart;
	}


	public String getDestinataire() {
		return destinataire;
	}


	public void setDestinataire(String destinataire) {
		this.destinataire = destinataire;
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


	public Integer getNumero_ordre() {
		return numero_ordre;
	}



	public void setNumero_ordre(Integer numero_ordre) {
		this.numero_ordre = numero_ordre;
	}


	public String getCrypto_systeme() {
		return crypto_systeme;
	}



	public void setCrypto_systeme(String crypto_systeme) {
		this.crypto_systeme = crypto_systeme;
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
		return "Beans_Message_Depart [numero_depart=" + numero_depart + ", destinataire=" + destinataire + ", mention="
				+ mention + ", date=" + date + ", objet_message=" + objet_message + ", type_message=" + type_message
				+ ", numero_ordre=" + numero_ordre + ", crypto_systeme=" + crypto_systeme + ", nom_fichier="
				+ nom_fichier + "]";
	}
	

	//LES DEPARTS

	public String formatToDatabase() {
		String sql = "INSERT INTO MessageDepart(destinataire,mention,date,nom_fichier,objet,numero_depart,reseau,numero_ordre,crypto_systeme) VALUES ('"  
				+ destinataire +     "','"
				+ mention +          "','"
				+ DateFormater.DateToString_database(date)+ "','"
				+nom_fichier + "','"
				+objet_message+ "',"
				+numero_depart+ ",'"
				+type_message +  "',"
				+numero_ordre + ",'"
				+crypto_systeme + "');";
		return sql;
	}

	public String formatToDatabase_officiel() {
		 
		String sql = "INSERT INTO "+desti+"(date,objet,numero_ordre) VALUES ('"  
			//	+ destinataire +     "','"
				+ DateFormater.DateToString_database(date)+ "','"
				+objet_message+ "',"
				+numero_ordre + ");";
		return sql;
	}
	
	public String formatToDatabase_TAC() {
		 
		String sql = "INSERT INTO Message_TAC_Depart(date,objet,numero_ordre) VALUES ('"  
			//	+ destinataire +     "','"
				+ DateFormater.DateToString_database(date)+ "','"
				+objet_message+ "',"
				+numero_ordre + ");";
		return sql;
	}

	public String formatToDatabase_divers() {
		String sql = "INSERT INTO MessageDepartDivers(destinataire,date,objet,numero_ordre) VALUES ('"  
				+ destinataire +     "','"
				+ DateFormater.DateToString_database(date)+ "','"
				+objet_message+ "',"
				+numero_ordre + ");";
		return sql;
	}
	
	public String formatToDatabase_conf() {
		String sql = "INSERT INTO MessageDepartConf(destinataire,date,objet,numero_ordre) VALUES ('"  
				+ destinataire +     "','"
				+ DateFormater.DateToString_database(date)+ "','"
				+objet_message+ "',"
				+numero_ordre + ");";
		return sql;
	}
	
	public String formatToDatabase_Secret() {
		String sql = "INSERT INTO MessageDepartSecret(destinataire,date,objet,numero_ordre) VALUES ('"  
				+ destinataire +     "','"
				+ DateFormater.DateToString_database(date)+ "','"
				+objet_message+ "',"
				+numero_ordre + ");";
		return sql;
	}


	//LES UPDATES
	public String format_Update_Database() {
		String sql_update = "UPDATE MessageDepart SET destinataire = " + "'" + destinataire + "'," +
				"date = "       + "'" + DateFormater.DateToString_database(date) +"'," +
				"mention = "+"'"+mention+"',"+
				"objet = "+"'"+objet_message+"',"+
				"reseau = "+"'"+type_message+"',"+
				"crypto_systeme = "+"'"+crypto_systeme+"'"+
				" WHERE numero_ordre = "+ numero_ordre +" ;";
		System.out.println(sql_update);
		return sql_update;
	}
	
	
	public String format_Update_Depart_officiel_Database() {
		String sql_update = "UPDATE "+desti+" SET " +
				"date = "       + "'" + DateFormater.DateToString_database(date) +"'," +
				"objet = "+"'"+objet_message+"'"+
				" WHERE numero_ordre = "+ numero_ordre +" ;";
		System.out.println(sql_update);
		return sql_update;
	}
	
	public String format_Update_Depart_TAC_Database() {
		String sql_update = "UPDATE Message_TAC_Depart SET " +
				"date = "       + "'" + DateFormater.DateToString_database(date) +"'," +
				"objet = "+"'"+objet_message+"'"+
				" WHERE numero_ordre = "+ numero_ordre +" ;";
		System.out.println(sql_update);
		return sql_update;
	}
	
	public String format_Update_Depart_divers_Database() {
		String sql_update = "UPDATE MessageDepartDivers SET destinataire = " + "'" + destinataire + "'," +
				"date = "       + "'" + DateFormater.DateToString_database(date) +"'," +
				"objet = "+"'"+objet_message+"'"+
				" WHERE numero_ordre = "+ numero_ordre +" ;";
		System.out.println(sql_update);
		return sql_update;
	}
	
	public String format_Update_Depart_conf_Database() {
		String sql_update = "UPDATE MessageDepartConf SET destinataire = " + "'" + destinataire + "'," +
				"date = "       + "'" + DateFormater.DateToString_database(date) +"'," +
				"objet = "+"'"+objet_message+"'"+
				" WHERE numero_ordre = "+ numero_ordre +" ;";
		System.out.println(sql_update);
		return sql_update;
	}
	
	public String format_Update_Depart_secret_Database() {
		String sql_update = "UPDATE MessageDepartSecret SET destinataire = " + "'" + destinataire + "'," +
				"date = "       + "'" + DateFormater.DateToString_database(date) +"'," +
				"objet = "+"'"+objet_message+"'"+
				" WHERE numero_ordre = "+ numero_ordre +" ;";
		System.out.println(sql_update);
		return sql_update;
	}
	
	
	public String format_Update_Numero_Depart() {
		String sql_update = "UPDATE NumeroOrdreDepart SET numero_ordre = " + numero_ordre +
				" WHERE poste_diplomatique = " + "'" + destinataire +"' ;";
		System.out.println(sql_update);
		return sql_update;
	}
	

	//LES LECTURES DEPART

	public boolean formatFromDatabase( ResultSet rs ) {

		try {

			// index commence par 1
			//	numero_depart = rs.getInt(1) + 10000;//on met les 10000 **************ID***********
			destinataire = rs.getString(2);
			mention = rs.getString(3);
			date = DateFormater.StringToDate(rs.getString(4));//a formater pour date
			nom_fichier = rs.getString(5);
			//on remplace les * par ' car sinon probléme insertion dans la base de donnéé. on fait le sens contraire. 
			objet_message = rs.getString(6).replace("*", "'");
			numero_depart = rs.getInt(7);
			type_message = rs.getString(8);
			numero_ordre = rs.getInt(9);
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
		//	destinataire = rs.getString(4);
			date = DateFormater.StringToDate(rs.getString(2));//a formater pour date
			objet_message = rs.getString(3).replace("*", "'");;
			numero_ordre = rs.getInt(4);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public boolean formatFromDatabase_TAC( ResultSet rs ) {

		try {

			//regarder dans create table
			//destinataire = rs.getString(4);
			date = DateFormater.StringToDate(rs.getString(2));//a formater pour date
			objet_message = rs.getString(3).replace("*", "'");;
			numero_ordre = rs.getInt(4);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}


	public boolean formatFromDatabase_divers( ResultSet rs ) {

		try {

			//regarder dans create table
			destinataire = rs.getString(4);
			date = DateFormater.StringToDate(rs.getString(2));//a formater pour date
			objet_message = rs.getString(3).replace("*", "'");;
			numero_ordre = rs.getInt(5);

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
			destinataire = rs.getString(4);
			date = DateFormater.StringToDate(rs.getString(2));//a formater pour date
			objet_message = rs.getString(3).replace("*", "'");;
			numero_ordre = rs.getInt(5);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public boolean formatFromDatabase_Secret( ResultSet rs ) {

		try {

			//regarder dans create table
			destinataire = rs.getString(4);
			date = DateFormater.StringToDate(rs.getString(2));//a formater pour date
			objet_message = rs.getString(3).replace("*", "'");;
			numero_ordre = rs.getInt(5);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}




	public String getTypeMessage() {
		return type_message;
	}


	public void setTypeMessage(String type_message) {
		this.type_message = type_message;
	}
	
	
	public boolean verifie_si_numero_depart_exist(){
		String sql = "SELECT * FROM "+desti+" WHERE numero_ordre = "+numero_ordre;
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
	
	
	public boolean verifie_si_numero_depart_TAC_exist(){
		String sql = "SELECT * FROM Message_TAC_Depart WHERE numero_ordre = "+numero_ordre;
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
