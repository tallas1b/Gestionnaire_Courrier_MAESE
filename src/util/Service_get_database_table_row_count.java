package util;

import database.Query;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class Service_get_database_table_row_count extends Service<Integer>{
	
	private String table_name;
	public Service_get_database_table_row_count(String table_name) {
		super();
		this.table_name = table_name;
	}



	@Override
	protected Task<Integer> createTask() {
		// TODO Auto-generated method stub
		return new Task<Integer>() {

			@Override
			protected Integer call() throws Exception {
			
				int entier = Query.getRowCount(table_name);		
				Query.close_connection();
				return entier;
			}
		};
	}

}
