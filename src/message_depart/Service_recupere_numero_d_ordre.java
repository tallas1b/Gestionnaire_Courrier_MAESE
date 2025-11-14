package message_depart;

import database.Query;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class Service_recupere_numero_d_ordre extends Service<Integer>{
	//recupere le numero d ordre suivant le type de message (officiel tac ou divers) service non inclus
	private String nom_table;
	int numero_ordre;
	public Service_recupere_numero_d_ordre(String nom_table) {
		super();
		this.nom_table = nom_table;
	}

	@Override
	protected Task<Integer> createTask() {
		
		return new Task<Integer>() {

			@Override
			protected Integer call() throws Exception {
				
				numero_ordre = Query.getRowCount(nom_table);
				Query.close_connection();
				return numero_ordre;
			}
		};
	}

}
