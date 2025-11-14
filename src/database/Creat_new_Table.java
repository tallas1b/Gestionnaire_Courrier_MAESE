package database;

import java.sql.SQLException;

import util.Constants;

public class Creat_new_Table {

	//Table Depart Diplomail
	public static void creatTableMessageDepart() {
		String sql =  "CREATE TABLE MessageDepart(ID INTEGER PRIMARY KEY AUTOINCREMENT,destinataire TEXT NOT NULL,"
				+ "mention TEXT NOT NULL,"
				+ "date TEXT NOT NULL,"
				+ "nom_fichier TEXT NOT NULL,"
				+ "objet TEXT NOT NULL,"
				+ "numero_depart INTEGER DEFAULT 1,"//anciennement id autoincrement
				+"reseau TEXT NOT NULL,"  // Officiel  divers ou TAC
				+"numero_ordre INTEGER DEFAULT 50000,"
				+ "crypto_systeme TEXT NOT NULL);";
		try {
			Query.insert(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	/*
	//Table Depart 50.000
		public static void creatTable_Message_50000() {
			String sql =  "CREATE TABLE MessageDepartOfficiel(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "date TEXT NOT NULL,"
					+ "objet TEXT NOT NULL,"
					+"destinataire TEXT NOT NULL,"
					+"numero_ordre INTEGER DEFAULT 50000);";
			try {
				Query.insert(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/

	//Table Depart 50.000
	public static void creatTable_destinataires_Message_50000() {

		try {

			for(String desti : Constants.liste_ambassade_base_donne) {
				
				String sql =  "CREATE TABLE "+desti+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
						+ "date TEXT NOT NULL,"
						+ "objet TEXT NOT NULL,"
						//		+"destinataire TEXT NOT NULL,"
						+"numero_ordre INTEGER DEFAULT 50000);";

				Query.insert(sql);
				
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//Table Depart Divers 70.000
	public static void creatTable_Message_Divers() {
		String sql =  "CREATE TABLE MessageDepartDivers(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "date TEXT NOT NULL,"
				+ "objet TEXT NOT NULL,"
				+ "destinataire TEXT NOT NULL,"
				+  "numero_ordre INTEGER DEFAULT 70000);";
		try {
			Query.insert(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//CLASSIFIE DEPART
	public static void createTable_Conf_Depart() {
		String sql =  "CREATE TABLE MessageDepartConf(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "date TEXT NOT NULL,"
				+ "objet TEXT NOT NULL,"
				+ "destinataire TEXT NOT NULL,"
				+  "numero_ordre INTEGER DEFAULT 50000);";
		try {
			Query.insert(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createTable_Secret_Depart() {
		String sql =  "CREATE TABLE MessageDepartSecret(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "date TEXT NOT NULL,"
				+ "objet TEXT NOT NULL,"
				+ "destinataire TEXT NOT NULL,"
				+  "numero_ordre INTEGER DEFAULT 50000);";
		try {
			Query.insert(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	//LES ARRIVES 

	public static void creatTableMessageArrive() {
		String sql =  "CREATE TABLE MessageArrive(ID INTEGER PRIMARY KEY AUTOINCREMENT,expediteur TEXT NOT NULL," //expediteur MIAAE par Defaut
				+ "mention TEXT NOT NULL,"
				+ "date TEXT NOT NULL,"
				+ "nom_fichier TEXT NOT NULL,"
				+ "objet TEXT NOT NULL,"
				+ "numero_arrive INTEGER DEFAULT 1,"//anciennement id autoincrement
				+ "numero_ordre INTEGER DEFAULT 50000," //numero d ordre selon initiateur genre paris 50.001
				+"reseau TEXT NOT NULL ,"   // Officiel(50.000)  divers ou TAC
				+ "crypto_systeme TEXT NOT NULL);";
		try {
			Query.insert(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//table arrivee Officiel (50.000)
	public static void creatTable_Message_Arrivee_Officiel() {
		String sql =  "CREATE TABLE Message_Officiel_Arrive(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "date TEXT NOT NULL,"
				+ "objet TEXT NOT NULL,"
				+ "numero_ordre INTEGER DEFAULT 50000 );";
		try {
			Query.insert(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//table arrivee TAC
	public static void creatTable_Message_Arrivee_TAC() {
		String sql =  "CREATE TABLE Message_TAC_Arrive(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "date TEXT NOT NULL,"
				+ "objet TEXT NOT NULL,"
				+ "numero_ordre INTEGER DEFAULT 40000 );";
		try {
			Query.insert(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//table arrivee Divers
	public static void creatTable_Message_Arrivee_Divers() {
		String sql =  "CREATE TABLE Message_Divers_Arrive(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "date TEXT NOT NULL,"
				+ "objet TEXT NOT NULL,"
				+ "numero_ordre INTEGER DEFAULT 70000,"
				+  "expediteur TEXT NOT NULL);";
		try {
			Query.insert(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//CLASSIFIE ARRIVE
	public static void createTable_conf_Arrivee() {
		String sql =  "CREATE TABLE MessageArriveConf(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "date TEXT NOT NULL,"
				+ "objet TEXT NOT NULL,"
				+ "numero_ordre INTEGER DEFAULT 50000,"
				+  "expediteur TEXT NOT NULL);";
		try {
			Query.insert(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createTable_secret_Arrivee() {
		String sql =  "CREATE TABLE MessageArriveSecret(ID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "date TEXT NOT NULL,"
				+ "objet TEXT NOT NULL,"
				+ "numero_ordre INTEGER DEFAULT 50000,"
				+  "expediteur TEXT NOT NULL);";
		try {
			Query.insert(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public static void creatTableNumero_Ordre_depart() {
		String sql =  "CREATE TABLE NumeroOrdreDepart(ID INTEGER PRIMARY KEY AUTOINCREMENT,poste_diplomatique TEXT NOT NULL,"
				+ "numero_ordre INTEGER DEFAULT 50000);";
		try {
			Query.insert(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void populate_table_numero_ordre() {
		for (String str : Constants.liste_ambassade) {
			String sql = "INSERT INTO NumeroOrdreDepart(poste_diplomatique,numero_ordre) VALUES ('"  
					+str+    "','" +50000+"');";	
			try {
				Query.insert(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}



	//parapetres
	public static void creatTableParametres() {
		String sql =  "CREATE TABLE parametres(ID INTEGER PRIMARY KEY AUTOINCREMENT,libelle_parametre TEXT NOT NULL,"
				+ "donnee TEXT );";
		try {
			Query.insert(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void populate_table_parametre() {

		String sql;
		try {

			sql = "INSERT INTO parametres(libelle_parametre,donnee) VALUES ('dossier_clair','Dossier');";	//1
			Query.insert(sql);

			sql = "INSERT INTO parametres(libelle_parametre,donnee) VALUES ('dossier_crypto','Dossier');";	//2
			Query.insert(sql);

			sql = "INSERT INTO parametres(libelle_parametre,donnee) VALUES ('imprimante_defaut','Imprimante');";	//3
			Query.insert(sql);	

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
