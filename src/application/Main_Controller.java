package application;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

public class Main_Controller implements Initializable{
	
	@FXML
	private AnchorPane pane_arrive;
	
	@FXML
	private AnchorPane pane_depart;
	
	@FXML
	private AnchorPane pane_find_depart;
	
	@FXML
	private AnchorPane pane_find_arrive;
	
	@FXML
	private AnchorPane rapport_depart;
	
	@FXML
	private AnchorPane rapport_arrive;
	
	@FXML
	private AnchorPane parametres;
	
	@FXML
	private AnchorPane pan_bouton;
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	
	@FXML
	private void on_bouton_arrive_clicked() {
		pane_arrive.toFront();
		pan_bouton.toFront();
	}

	@FXML
	private void on_bouton_depart_clicked() {
		pane_depart.toFront();
		pan_bouton.toFront();
	}
	
	@FXML
	private void on_bouton_rechercher_depart_clicked() {
		pane_find_depart.toFront();
		pan_bouton.toFront();
	}
	
	
	@FXML
	private void on_rapport_depart_clicked() {
		rapport_depart.toFront();
		pan_bouton.toFront();
	}
	
	
	@FXML
	private void on_rapport_arrive_clicked() {
		rapport_arrive.toFront();
		pan_bouton.toFront();
	}
	
	@FXML
	private void on_find_arrive_clicked() {
		pane_find_arrive.toFront();
		pan_bouton.toFront();
	}
	
	@FXML
	private void on_parametres_clicked() {
		parametres.toFront();
		pan_bouton.toFront();
	}
	
}
