package message_depart.customListView;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

public class TaskCell extends ListCell<String> {


	@FXML
	private Label descriptionLabel;

	@FXML
	private ListCell<String> list_cell;

	public TaskCell() {
		loadFXML();
	}

	private void loadFXML() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Task_Cell.fxml"));
			loader.setController(this);
			loader.setRoot(this);
			loader.load();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);

		if(empty || item == null) {
			setText(null);
			setContentDisplay(ContentDisplay.TEXT_ONLY);
		}
		else {

			//on adapte la taille suivant la longueur du libelle
			if(item.length() > 95) {
				//recuperation nombre de eligne;
				String[] tab =  item.split("\n");
				int t = tab.length;
				
				list_cell.setPrefHeight(50 + t*10);
				descriptionLabel.setPrefHeight(50 + t*10);

			}else {
				list_cell.setPrefHeight(50);
				descriptionLabel.setPrefHeight(50);
			}
			descriptionLabel.setText(item);

			setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
		}
	}
}
