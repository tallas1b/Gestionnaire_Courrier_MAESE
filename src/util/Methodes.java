package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

public class Methodes {


	public static boolean isStringInteger(String number ){

		try{

			Integer.parseInt(number);

		}catch(Exception e ){

			return false;
		}

		return true;
	}
	
	
	public static void mshowExceptionAlert(String headerText,
            String message, Throwable th) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        th.printStackTrace(pw);

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(headerText);

        if (message != null) {
            alert.setContentText(message);
        } else {
            alert.setContentText(th.getMessage());
        }

        TextArea textArea = new TextArea(sw.toString());
        textArea.setEditable(false);
        alert.getDialogPane().setExpandableContent(textArea);
        alert.showAndWait();
    }
	
	public static String ajout_point_50000(String string) {
		StringBuilder builder = new StringBuilder(string); //on ajoute le point pour decorer
		if(builder.length()<=2 ) {
			return string;
		}else {
			return builder.insert(2, '.').toString();
		}
	}
	
	public static String retirer_point_50000(String string) {
		StringBuilder builder = new StringBuilder(string); //on retire le point pour 
		if(builder.length()<=2 ) {
			return string;
		}else {
			return builder.deleteCharAt(2).toString();
		}
	}
	
	public static boolean save(String fichier , String value ) {
		try {
			//on prepare le fichier en mode ecriture
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(fichier));
			writer.write(value);
			writer.close();
			return true;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
	public static String load(String fichier) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fichier));
			String line = reader.readLine();
			reader.close();
			return line;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "-1";
		}
		
	}

	
	
}
