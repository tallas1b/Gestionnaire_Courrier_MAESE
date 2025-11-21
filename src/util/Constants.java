package util;

import java.util.HashMap;

public class Constants {
	
	// IMPORTANT
	//SI AJOUT DANS list_ambassade , alors il faudra mofifier liste_ambassade_base_donnee car il y a concordance
	
	
	public static final String[] liste_ambassade = {"AMBASENE ABIDJAN","AMBASENE ABU DHABI","AMBASENE ABUJA","AMBASENE ACCRA","AMBASENE ADDIS-ABEBA",
			"AMBASENE ALGER","AMBASENE ANKARA ","AMBASENE BAMAKO","AMBASENE BANJUL","AMBASENE BEIJING","AMBASENE BERLIN","AMBASENE BISSAU","AMBASENE BRASILIA",
			"AMBASENE BRAZZAVILLE","AMBASENE BRUXELLES","AMBASENE CONAKRY","AMBASENE CAIRE","AMBASENE DOHA","AMBASENE GENEVE","AMBASENE KIGALI","AMBASENE KINSHASA",
			"AMBASENE KOWEiT-CITY","AMBASENE KUALA LUMPUR","AMBASENE LA HAYE","AMBASENE LIBREVILLE","AMBASENE LISBONNE","AMBASENE LOME","AMBASENE LONDRES","AMBASENE MADRID",
			"AMBASENE MASCATE","AMBASENE MOSCOU","AMBASENE NAIROBI","AMBASENE NIAMEY","AMBASENE NEW DELHI","AMBASENE NEW YORK","AMBASENE NOUAKCHOTT","AMBASENE OTTAWA",
			"AMBASENE OUAGADOUGOU","AMBASENE PARIS","AMBASENE UNESCO_PARIS","AMBASENE PRAIA","AMBASENE PRETORIA","AMBASENE RABAT","AMBASENE RIYADH","AMBASENE ROME - SAINT-SIEGE",
			"AMBASENE ROME QUIRINAL","AMBASENE SEOUL","AMBASENE TEHERAN","AMBASENE TOKYO","AMBASENE TUNIS","AMBASENE VARSOVIE","AMBASENE WASHINGTON","AMBASENE YAOUNDE"};
	
	
	
	public static final String[] liste_ambassade_base_donne = {"ABIDJAN","ABU_DHABI","ABUJA","ACCRA","ADDIS_ABEBA",
			"ALGER","ANKARA ","BAMAKO","BANJUL","BEIJING","BERLIN","BISSAU","BRASILIA",
			"BRAZZAVILLE","BRUXELLES","CONAKRY","CAIRE","DOHA","GENEVE","KIGALI","KINSHASA",
			"KOWEiT_CITY","KUALA_LUMPUR","LA_HAYE","LIBREVILLE","LISBONNE","LOME","LONDRES","MADRID",
			"MASCATE","MOSCOU","NAIROBI","NIAMEY","NEW_DELHI","NEW_YORK","NOUAKCHOTT","OTTAWA",
			"OUAGADOUGOU","PARIS","UNESCO_PARIS","PRAIA","PRETORIA","RABAT","RIYADH","ROME_SAINT_SIEGE",
			"ROME_QUIRINAL","SEOUL","TEHERAN","TOKYO","TUNIS","VARSOVIE","WASHINGTON","YAOUNDE"};
	
	
	
	
	public static final String[] liste_month = {"Janvier","Fevrier","Mars","Avril","Mai","Juin",
			"Juillet","Aout","Septembre","Octobre","Novembre","Decembre"};
	
	public static enum Mention {Claire , Confidentiel , Secret , Tres_Secret };
	public static enum Type_Message { Officiel , T_A_C , Divers ,Service};
	
	public static String  DOC_CLAIR ="",DOC_CRYTO="";
	public static String Default_Printer_Name = "";
	public static boolean PARAMETRES_RECUPERER_AVEC_SUCCES = false;
	
	public static HashMap<String, Integer> hash_num_departs = new HashMap<String, Integer>();
	public static HashMap<String, Integer> hash_num_arrive = new HashMap<String, Integer>();

}
