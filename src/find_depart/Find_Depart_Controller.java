package find_depart;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.StyleClassDecoration;

import beans.Beans_Message_Depart;
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

public class Find_Depart_Controller implements Initializable{


	@FXML
	private Pane pane_criter_numero_depart;
	@FXML
	private TextField textfield_numero_depart;


	@FXML
	private Pane pane_criter_destinataire;	
	@FXML
	private TextField textfield_objet;

	@FXML
	private TableView<Beans_Message_Depart> table;

	@FXML
	private TableColumn<Beans_Message_Depart, Integer> colonne_numero_depart;
	
	@FXML
	private TableColumn<Beans_Message_Depart, Integer> colonne_numero_ordre;

	@FXML
	private TableColumn<Beans_Message_Depart, String> colonne_destinataire;

	@FXML
	private TableColumn<Beans_Message_Depart, String> colone_Mention;

	@FXML
	private TableColumn<Beans_Message_Depart, LocalDate> colone_date;

	@FXML
	private TableColumn<Beans_Message_Depart, String> colone_objet;

	@FXML
	private TableColumn<Beans_Message_Depart, String> crypto_systeme;
	
	@FXML
	private TableColumn<Beans_Message_Depart, String> colonne_nom_fichier;

	@FXML
	private ChoiceBox<String> choice_critere;

	private ObservableList<Beans_Message_Depart> obser;
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
					pane_criter_numero_depart.toFront();
				}else if(index == 1){
					pane_criter_destinataire.toFront();
				}else {
					System.out.println(choice_critere.getItems().get((Integer) number2));
				}
			}
		});

		obser = FXCollections.observableArrayList();

		colonne_numero_depart.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, Integer>("numero_depart"));
		colonne_destinataire.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, String>("destinataire"));
		colone_Mention.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, String>("mention"));
		colone_date.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, LocalDate>("date"));
		colone_objet.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, String>("objet_message"));
		crypto_systeme.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, String>("crypto_systeme"));
		colonne_numero_ordre.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, Integer>("numero_ordre") );
		colonne_nom_fichier.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, String>("nom_fichier") );
		
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

		String sql = "SELECT * FROM MessageDepart WHERE  objet LIKE '%"+objet+"%'";

		Connection conn = Connexion.connect();
		System.out.println(sql);

		try {
			System.out.println("ici");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while( rs.next()) {
				Beans_Message_Depart  bean = new Beans_Message_Depart();
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

		validation_vide_et_entier(textfield_numero_depart);
		String num_depart = textfield_numero_depart.getText();


		Beans_Message_Depart beans_select = new Beans_Message_Depart();
		String sql = "SELECT * FROM MessageDepart WHERE numero_ordre = "+Integer.parseInt(num_depart );
		System.out.println(sql);
		
		try {
			ResultSet rs = Query.select(sql);
			rs.next();
			beans_select.formatFromDatabase(rs);
			System.out.println(beans_select.toString());
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
		if(textfield_numero_depart.getText().length() <= 0 ) {
			Decorator.addDecoration(textfield_numero_depart, new StyleClassDecoration("warning"));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Champs Vide");
			alert.setHeaderText(null);
			alert.setContentText("Le champs Numero de départ est vide.\n veuillez le remplir avant de reessayer.");
			alert.show();
			return false;
		}

		//validation est un entier
		if(!Methodes.isStringInteger(textfield_numero_depart.getText())) {
			Decorator.addDecoration(textfield_numero_depart, new StyleClassDecoration("warning"));
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
