package find_arrive;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.StyleClassDecoration;

import beans.Beans_Message_Arrive;
import database.Connexion;
import database.Query;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import util.Methodes;

public class Find_Arrive_Controller implements Initializable{


	@FXML
	private Pane pane_criter_numero_arrive;
	@FXML
	private TextField textfield_numero_arrive;


	@FXML
	private Pane pane_criter_objet;	
	@FXML
	private TextField textfield_objet;


	@FXML
	private TableView<Beans_Message_Arrive> table;

	@FXML
	private TableColumn<Beans_Message_Arrive, Integer> colonne_numero_arrive;
	
	@FXML
	private TableColumn<Beans_Message_Arrive, Integer> colonne_numero_ordre;

	@FXML
	private TableColumn<Beans_Message_Arrive, String> colonne_expediteur;

	@FXML
	private TableColumn<Beans_Message_Arrive, String> colone_Mention;

	@FXML
	private TableColumn<Beans_Message_Arrive, LocalDate> colone_date;

	@FXML
	private TableColumn<Beans_Message_Arrive, String> colone_objet;
	
	@FXML
	private TableColumn<Beans_Message_Arrive, String> crypto_systeme;
	
	@FXML
	private TableColumn<Beans_Message_Arrive, String> colonne_nom_fichier;

	@FXML
	private ChoiceBox<String> choice_critere;

	private ObservableList<Beans_Message_Arrive> obser;
	private int index = 0;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		choice_critere.getItems().addAll("Numero D'ordre","Objet");
		choice_critere.setValue("Numero D'ordre");
		choice_critere.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
				index = (Integer) number2;
				//number2 est la nouvel valeur selectionne et correspond a l index.
				if(index == 0) {
					pane_criter_numero_arrive.toFront();
				}else if(index == 1){
					pane_criter_objet.toFront();
				}else {
					System.out.println(choice_critere.getItems().get((Integer) number2));
				}
			}
		});

		obser = FXCollections.observableArrayList();

		colonne_numero_arrive.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, Integer>("numero_arrive"));
		colonne_expediteur.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, String>("expediteur"));
		colone_Mention.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, String>("mention"));
		colone_date.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, LocalDate>("date"));
		colone_objet.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, String>("objet_message"));
		crypto_systeme.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, String>("crypto_systeme"));
		colonne_numero_ordre.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, Integer>("numero_d_ordre_expediteur") );
		colonne_nom_fichier.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, String>("nom_fichier") );
		
		table.setItems(obser);

	}


	@FXML
	private void on_rechercher_clicked() {

		if(index == 0) {
			critere_numero_depart();
		}else if (index == 1) {
			critere_objet();
		}else {
			System.err.println("erreur dans recherche depart index bout fonction!!!!!!");
		}

	}


	private void critere_objet() {

		//on vide dabord la liste pour eviter que les anciennes recherches ne poluent
		obser.clear();

		//objet
		//on recherche un mot dans les objet sur les message de 1 mois pour eviter un relantissement trop important
		

		String objet = textfield_objet.getText();

		String sql = "SELECT * FROM MessageArrive WHERE  objet LIKE '%"+objet+"%'";

		Connection conn = Connexion.connect();
		System.out.println(sql);

		try {
			System.out.println("ici");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while( rs.next()) {
				Beans_Message_Arrive  bean = new Beans_Message_Arrive();
				bean.formatFromDatabase(rs);
				System.out.println(bean.toString());
				obser.add( bean);
			}
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	private void critere_numero_depart() {
		
		//on vide dabord la liste pour eviter que les anciennes recherches ne poluent
		obser.clear();

		validation_vide_et_entier(textfield_numero_arrive);
		String num_arrive = textfield_numero_arrive.getText();


		Beans_Message_Arrive beans_select = new Beans_Message_Arrive();
		String sql = "SELECT * FROM MessageArrive WHERE numero_ordre = "+ Integer.parseInt(num_arrive);
		
		try {
			ResultSet rs = Query.select(sql);
			rs.next();
			beans_select.formatFromDatabase(rs);
			obser.add(beans_select);
			Query.close_connection();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	private boolean validation_vide_et_entier(TextField textf) {

		//validation champ vide
		if(textfield_numero_arrive.getText().length() <= 0 ) {
			Decorator.addDecoration(textfield_numero_arrive, new StyleClassDecoration("warning"));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Champs Vide");
			alert.setHeaderText(null);
			alert.setContentText("Le champs Numero de depart est vide.\n veuillez le remplir avant de reessayer.");
			alert.show();
			return false;
		}

		//validation est un entier
		if(!Methodes.isStringInteger(textfield_numero_arrive.getText())) {
			Decorator.addDecoration(textfield_numero_arrive, new StyleClassDecoration("warning"));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Format Incorrect");
			alert.setHeaderText(null);
			alert.setContentText("Le champs Numero de depart doit etre un entier.\n veuillez corriger avant de reessayer.");
			alert.show();
			return false;
		}

		return true;
	}

}
