package message_depart.customListView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class Load_Destinataire_Libelle extends Service<Map<String,ArrayList<String>>>{

	private Map<String, ArrayList<String>> map;

	public Load_Destinataire_Libelle() {
		map = new HashMap<String, ArrayList<String>>();
	}

	@Override
	protected Task<Map<String, ArrayList<String>>> createTask() {
		// TODO Auto-generated method stub
		return new Task<Map<String,ArrayList<String>>>() {

			@Override
			protected Map<String,ArrayList<String>> call() throws Exception {
				loadFile();
				return map;
			}

		};
	}

	private void loadFile() {

		String path = "assets/libelle_destinataire.txt";
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line = br.readLine();

			while (line != null) {
				
				//on split pour recuperer le destinataire d abord
				String[] tab_destinataire  = line.split(":");

				String destinataire = tab_destinataire[0];
				String libelle = tab_destinataire[1].replace('|' , '\n');//on remet le lines separator
			
				//etape 2 on separe les libelle et on les met dans le map
				formatString(destinataire, libelle);
				line = br.readLine();
			}

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void formatString(String destinataire , String libelle) {
		//une ligne destinataire = key et l autre la value [ libele]
		String[] tab = libelle.split("#");
		map.put(destinataire, new ArrayList<>(Arrays.asList(tab)));


	}

}
