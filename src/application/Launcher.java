package application;

import database.Creat_new_Table;

public class Launcher {
	

	public static void main(String[] args) {
		
		//parametre:  recupere donne delai 5s
		//depart: recupere numero ordre 1s
		
	//MOdifier table diver arriver conf arriver et secret
		Main.main(args);
		
		//Creat_new_Table.creatTable_Arrive_Message_50000();

	//	Creat_new_Table.creatTableNumero_Ordre_arrive();
	//	Creat_new_Table.populate_table_numero_ordre_arrive();
	
		
/*		
		
		// Table Depart
		Creat_new_Table.creatTableMessageDepart();
		Creat_new_Table.creatTable_Message_50000();
		Creat_new_Table.creatTable_Message_Depart_TAC();
		Creat_new_Table.creatTable_Message_Divers();
		Creat_new_Table.creatTable_destinataires_Message_50000();
		
		
		//Table Arrivee
		Creat_new_Table.creatTableMessageArrive();
		Creat_new_Table.creatTable_Arrive_Message_50000();
		Creat_new_Table.creatTable_Message_Arrivee_Divers();
		
		// Table Parametre
		Creat_new_Table.creatTableParametres();
		Creat_new_Table.populate_table_parametre();
		
		
		//CLASSIFIE
		Creat_new_Table.createTable_Conf_Depart();
		Creat_new_Table.createTable_Secret_Depart();
		Creat_new_Table.createTable_conf_Arrivee();
		Creat_new_Table.createTable_secret_Arrivee();
		
		Creat_new_Table.creatTableNumero_Ordre_depart();
		Creat_new_Table.populate_table_numero_ordre();
		Creat_new_Table.creatTableNumero_Ordre_arrive();
		Creat_new_Table.populate_table_numero_ordre_arrive();
		
		*/
		
	/*	//Temp add column
		String sql_divers_arrive = "alter table Message_Divers_Arrive add column expediteur TEXT NOT NULL DEFAULT 'MIAAESE';";
		String sql_conf_arrive = "alter table MessageArriveConf add column expediteur TEXT NOT NULL DEFAULT 'MIAAESE';";
		String sql_secret_arrive = "alter table MessageArriveSecret add column expediteur TEXT NOT NULL DEFAULT 'MIAAESE';";
		
		try {
			Query.insert(sql_divers_arrive);
			Query.insert(sql_conf_arrive);
			Query.insert(sql_secret_arrive);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		
	/*	 String sql_delete =  "DELETE FROM parametres WHERE libelle_parametre = 'email';";
		 String sql_delete_mdp =  "DELETE FROM parametres WHERE libelle_parametre = 'mdp_email';";
		 try {
				Query.insert(sql_delete);
				Query.insert(sql_delete_mdp);
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		 /* */
		
	/*	 try {
			 Creat_new_Table.creatTableParametres();
			 Creat_new_Table.populate_table_parametre();
		 } catch (Exception e) {
				e.printStackTrace();
			}*/
		 
		
	}

}
 