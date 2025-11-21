package rapport_depart;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.StyleClassDecoration;
import org.controlsfx.control.textfield.TextFields;

import beans.Beans_Message_Depart;
import database.Query;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import pdf.registreDepart.Generer_registre_depart;
import pdf.registreDepart.Generer_registre_depart_confidentiel;
import pdf.registreDepart.Generer_registre_depart_divers;
import pdf.registreDepart.Generer_registre_depart_officiel;
import pdf.registreDepart.Generer_registre_depart_secret;
import util.Constants;

public class Control_Rapport_Depart implements Initializable{

	@FXML
	private ChoiceBox<String> choice_registre;

	@FXML
	private ChoiceBox<String> choice_mois;
	
	@FXML 
	private TextField destinataire;
	
	@FXML
	private Label label_destinataire;

	@FXML
	private TableView<Beans_Message_Depart> tableview;

	@FXML
	private TableColumn<Beans_Message_Depart, Integer> colonne_numero_depart;

	@FXML
	private TableColumn<Beans_Message_Depart, String> colonne_destinataire;

	@FXML
	private TableColumn<Beans_Message_Depart, Integer> colonne_numero_ordre;

	@FXML
	private TableColumn<Beans_Message_Depart, String> colone_Mention;

	@FXML
	private TableColumn<Beans_Message_Depart, LocalDate> colone_date;

	@FXML
	private TableColumn<Beans_Message_Depart, String> colone_objet;

	@FXML
	private TableColumn<Beans_Message_Depart, String> colone_crypto_systeme;

	@FXML
	private TableColumn<Beans_Message_Depart, String> colonne_nom_fichier;

	@FXML
	private AnchorPane pane;

	@FXML
	private Button bouton_imprimer_rapport;


	private ObservableList<Beans_Message_Depart> obser;
	private boolean afficher_table = true;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		choice_mois.getItems().addAll(Constants.liste_month);
		choice_mois.setValue(Constants.liste_month[0]);
		choice_registre.getItems().addAll("Diplomail","Officiel", "TAC","Divers","Confidentiel","Secret");
		choice_registre.setValue("Diplomail");
		
		TextFields.bindAutoCompletion(
				destinataire ,
				Constants.liste_ambassade);

		obser = FXCollections.observableArrayList();

		colonne_numero_depart.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, Integer>("numero_depart"));
		colonne_destinataire.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, String>("destinataire"));
		colonne_numero_ordre.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, Integer>("numero_ordre"));
		colone_Mention.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, String>("mention"));
		colone_date.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, LocalDate>("date"));
		colone_objet.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, String>("objet_message"));
		colone_crypto_systeme.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, String>("crypto_systeme"));

		colonne_nom_fichier.setCellValueFactory(new PropertyValueFactory<Beans_Message_Depart, String>("nom_fichier") );
		tableview.setItems(obser);
		
		//binding choice_type_message
		choice_registre.valueProperty().addListener(new ChangeListener<String>() {

					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

						if(newValue.equalsIgnoreCase("Officiel")) {			
							destinataire.setVisible(true);
							label_destinataire.setVisible(true);
						}else {	 
							destinataire.setVisible(false);
							label_destinataire.setVisible(false);
						}

					}
				});

	}



	@FXML
	private void on_valider_clicked() {
		obser.clear();

		if(choice_registre.getValue().equalsIgnoreCase("Diplomail")) {
			on_diplomail_selected();
		}else if(choice_registre.getValue().equalsIgnoreCase("Officiel")) {
			if(!validation()) {
				return;
			}
			on_officiel_selected();
		}else if(choice_registre.getValue().equalsIgnoreCase("TAC")) {
			on_TAC_selected();
		}else if(choice_registre.getValue().equalsIgnoreCase("Divers")) {
			on_divers_selected();
		}else if(choice_registre.getValue().equalsIgnoreCase("Confidentiel")) {
			on_confidentiel_selected();
		}else if(choice_registre.getValue().equalsIgnoreCase("Secret")) {
			on_secret_selected();
		}

	
		bouton_imprimer_rapport.setDisable(false);

	}

	private String corespondance_month_entier(String m) {
		for (int j = 0; j < Constants.liste_month.length; j++) {
			String s = Constants.liste_month[j];
			if(s.equalsIgnoreCase(m)) {

				DecimalFormat formatter = new DecimalFormat("00");//les mois sont en deux digits dans la database donc 01=1
				String entier = formatter.format(j+1);
				return entier; //car index donc commence par 0
			}

		}
		return "01";
	}

	private void on_diplomail_selected() {
		String sql = "SELECT * FROM MessageDepart WHERE date LIKE '%/" +corespondance_month_entier(choice_mois.getValue())+"/%'"; 

		if(!afficher_table) {//le tableau n est pas affiche
			colonne_numero_depart.setVisible(true);
			colone_Mention.setVisible(true);
			colone_crypto_systeme.setVisible(true);
			colonne_nom_fichier.setVisible(true);
			colonne_destinataire.setVisible(true);
			afficher_table = true;
		}
		
		try {	
			ResultSet rs = Query.select(sql);
			while( rs.next()) {
				Beans_Message_Depart  bean = new Beans_Message_Depart();
				bean.formatFromDatabase(rs);
				obser.add( bean);
			}
			Query.close_connection();
		} catch (SQLException e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		} catch (Exception e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		}
	}
	
	private void on_TAC_selected() {
		String sql = "SELECT * FROM Message_TAC_Depart ;'"; 

		if(afficher_table) {//le tableau est deja affiche on enleve les colonnes
			colonne_numero_depart.setVisible(false);
			colone_Mention.setVisible(false);
			colone_crypto_systeme.setVisible(false);
			colonne_nom_fichier.setVisible(false);
			colonne_destinataire.setVisible(false);
			afficher_table = false;
		}
		
		try {	
			ResultSet rs = Query.select(sql);
			while( rs.next()) {
				Beans_Message_Depart  bean = new Beans_Message_Depart();
				bean.formatFromDatabase_TAC(rs);
				obser.add( bean);
			}
			Query.close_connection();
		} catch (SQLException e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		} catch (Exception e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		}
	}


	private void on_divers_selected() {
		String sql = "SELECT * FROM MessageDepartDivers ;'"; 

		if(afficher_table) {//le tableau est deja affiche on enleve les colonnes
			colonne_numero_depart.setVisible(false);
			colone_Mention.setVisible(false);
			colone_crypto_systeme.setVisible(false);
			colonne_nom_fichier.setVisible(false);
			colonne_destinataire.setVisible(false);
			afficher_table = false;
		}
		
		try {	
			ResultSet rs = Query.select(sql);
			while( rs.next()) {
				Beans_Message_Depart  bean = new Beans_Message_Depart();
				bean.formatFromDatabase_divers(rs);
				obser.add( bean);
			}
			Query.close_connection();
		} catch (SQLException e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		} catch (Exception e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		}
	}


	private void on_officiel_selected() {
		
		int index = 0;
		String desti = "";
		for( String amb : Constants.liste_ambassade) {
			if(amb.equalsIgnoreCase(destinataire.getText())) {
				desti = Constants.liste_ambassade_base_donne[index];
				break;
			}
			index++;
		}
		
		String sql = "SELECT * FROM "+desti+" WHERE date LIKE '%/" +corespondance_month_entier(choice_mois.getValue())+"/%'"; 
		System.out.println("Le dest est dans controle depart: "+desti);

		if(afficher_table) {//le tableau est deja affiche on enleve les colonnes
			colonne_numero_depart.setVisible(false);
			colone_Mention.setVisible(false);
			colone_crypto_systeme.setVisible(false);
			colonne_nom_fichier.setVisible(false);
			colonne_destinataire.setVisible(false);
			afficher_table = false;
		}
		
		try {	
			ResultSet rs = Query.select(sql);
			while( rs.next()) {
				Beans_Message_Depart  bean = new Beans_Message_Depart();
				bean.formatFromDatabase_officiel(rs);
				obser.add( bean);
			}
			Query.close_connection();
		} catch (SQLException e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		} catch (Exception e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		}
	}
	
	private void on_confidentiel_selected() {
		String sql = "SELECT * FROM MessageDepartConf ;"; 

		if(afficher_table) {//le tableau est deja affiche on enleve les colonnes
			colonne_numero_depart.setVisible(false);
			colone_Mention.setVisible(false);
			colone_crypto_systeme.setVisible(false);
			colonne_nom_fichier.setVisible(false);
			colonne_destinataire.setVisible(false);
			afficher_table = false;
		}
		
		try {	
			ResultSet rs = Query.select(sql);
			while( rs.next()) {
				Beans_Message_Depart  bean = new Beans_Message_Depart();
				bean.formatFromDatabase_divers(rs);
				obser.add( bean);
			}
			Query.close_connection();
		} catch (SQLException e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		} catch (Exception e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		}
	}
	
	private void on_secret_selected() {
		String sql = "SELECT * FROM MessageDepartSecret ;"; 

		if(afficher_table) {//le tableau est deja affiche on enleve les colonnes
			colonne_numero_depart.setVisible(false);
			colone_Mention.setVisible(false);
			colone_crypto_systeme.setVisible(false);
			colonne_destinataire.setVisible(false);
			colonne_nom_fichier.setVisible(false);
			afficher_table = false;
		}
		
		try {	
			ResultSet rs = Query.select(sql);
			while( rs.next()) {
				Beans_Message_Depart  bean = new Beans_Message_Depart();
				bean.formatFromDatabase_divers(rs);
				obser.add( bean);
			}
			Query.close_connection();
		} catch (SQLException e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		} catch (Exception e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		}
	}


	@FXML
	private void on_print_clicked() {

		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters().add(new ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf"));
		File f = chooser.showSaveDialog(pane.getScene().getWindow());

		if(choice_registre.getValue().equalsIgnoreCase("Diplomail")) {
			generer_registre_diplomail(f);
		}else if(choice_registre.getValue().equalsIgnoreCase("Officiel")) {
			generer_registre_message_officiel(f);
		}else if(choice_registre.getValue().equalsIgnoreCase("Divers")) {
			generer_registre_message_Divers(f);
		}else if(choice_registre.getValue().equalsIgnoreCase("Confidentiel")) {
			generer_registre_message_Confidentiel(f);
		}else if(choice_registre.getValue().equalsIgnoreCase("Secret")) {
			generer_registre_message_Secret(f);
		}

		

	}
	
	private void generer_registre_diplomail(File f) {
		Generer_registre_depart gen = new Generer_registre_depart(obser , f);
		gen.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				boolean b = gen.getValue();
				if(b) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText(null);
					alert.setContentText("Succes!! Fichier PDF genere avec succes");
					alert.show();

				}else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText(null);
					alert.setContentText("Erreur lors de la generation du fichier PDF. Lancez l application en ligne de commande pour plus d information.");
					alert.show();
				}
			}
		});
		gen.start();
	}
	
	private void generer_registre_message_officiel(File f) {
		Generer_registre_depart_officiel gen = new Generer_registre_depart_officiel(obser , f);
		gen.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				boolean b = gen.getValue();
				if(b) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText(null);
					alert.setContentText("Succes!! Fichier PDF generee avec succes");
					alert.show();

				}else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText(null);
					alert.setContentText("Erreur lors de la generation du fichier PDF. Lancez l application en ligne de commande pour plus d information.");
					alert.show();
				}
			}
		});
		gen.start();
	}
	
	
	private void generer_registre_message_Secret(File f) {
		Generer_registre_depart_secret gen = new Generer_registre_depart_secret(obser , f);
		gen.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				boolean b = gen.getValue();
				if(b) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText(null);
					alert.setContentText("Succes!! Fichier PDF genere avec succes");
					alert.show();

				}else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText(null);
					alert.setContentText("Erreur lors de la generation du fichier PDF. Lancez l application en ligne de commande pour plus d information.");
					alert.show();
				}
			}
		});
		gen.start();
	}
	
	private void generer_registre_message_Confidentiel(File f) {
		Generer_registre_depart_confidentiel gen = new Generer_registre_depart_confidentiel(obser , f);
		gen.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				boolean b = gen.getValue();
				if(b) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText(null);
					alert.setContentText("Succes!! Fichier PDF genere avec succes");
					alert.show();

				}else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText(null);
					alert.setContentText("Erreur lors de la generation du fichier PDF. Lancez l application en ligne de commande pour plus d information.");
					alert.show();
				}
			}
		});
		gen.start();
	}
	
	
	private void generer_registre_message_Divers(File f) {
		Generer_registre_depart_divers gen = new Generer_registre_depart_divers(obser , f);
		gen.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				boolean b = gen.getValue();
				if(b) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText(null);
					alert.setContentText("Succees!! Fichier PDF generee avec succes");
					alert.show();

				}else {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText(null);
					alert.setContentText("Erreur lors de la generation du fichier PDF. Lancez l application en ligne de commande pour plus d information.");
					alert.show();
				}
			}
		});
		gen.start();
	}

	private void showExceptionAlert(String headerText,
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
	
	private boolean validation() {

		//on enleve tout les decorations avant de mettre le mauvais
		Decorator.removeAllDecorations(destinataire);


		//destinataire
		//on verifie si le champ destinataire n est pas vide
		if ( destinataire.getText().length() <= 0) {
			Decorator.addDecoration(destinataire, new StyleClassDecoration("warning"));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur!!!! Ambassade");
			alert.setHeaderText(null);
			alert.setContentText("Le champs destinataire du message est vide.\\n veuillez le remplir avant de reessayer.!!");
			alert.show();
			return false;
		}
		
		boolean b = false;
		String dest = destinataire.getText();
		for( String amb : Constants.liste_ambassade) {
			if(amb.equalsIgnoreCase(dest)) {
				b = true;
			}
		}
		
		if(b == false) {
			Decorator.addDecoration(destinataire, new StyleClassDecoration("warning"));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur!!!! Ambassade");
			alert.setHeaderText(null);
			alert.setContentText("Le destinataire du message n'est pas prédéfini.\\n veuillez revoir le champ avant de reessayer.!!");
			alert.show();
			return false;
		}

		return true;
	}

}
