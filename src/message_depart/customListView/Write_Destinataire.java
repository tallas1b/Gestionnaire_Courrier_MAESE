package message_depart.customListView;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class Write_Destinataire extends Service<Boolean>{

	private Map<String, ArrayList<String>> map;

	public Write_Destinataire(Map<String, ArrayList<String>> map) {
		super();
		this.map = map;
	}



	@Override
	protected Task<Boolean> createTask() {

		return new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {

				return writeFile();
			}

		};
	}

	private boolean writeFile() {
		
		try {
			FileWriter myWriter = new FileWriter("assets/libelle_destinataire.txt");
			
			for ( Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
			    String destinataire = entry.getKey();
			    ArrayList<String> libelle = entry.getValue();
			    
			    //on concate les libele
			    String concat_lebelle = formatLibelle(libelle);
			    
			    //puis on ajoute le destinataire devant
			    String str_final = destinataire + ":" + concat_lebelle;
			    
			    //on ecrit enfin
			    myWriter.write(str_final + "\n");//to line
			    
			}
			
			
			
			myWriter.close();
			
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
		return true;
	}
	
	private String formatLibelle( ArrayList<String> libelle) {
		//on met le premier avant d ajouter les #
		String result = libelle.get(0).replace('\n', '|');//on extrait les separateur de ligne
		
		for (int i = 1; i < libelle.size(); i++) {
			result = result + "#" +libelle.get(i).replace('\n', '|'); //on extrait les separateur de ligne
		}
		
		return result;
	}

}
