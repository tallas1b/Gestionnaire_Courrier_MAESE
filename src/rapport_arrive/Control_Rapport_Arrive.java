package rapport_arrive;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

import beans.Beans_Message_Arrive;
import beans.Beans_Message_Depart;
import database.Query;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import pdf.registre_arrive.Generer_registre_arrive;
import pdf.registre_arrive.Generer_registre_arrive_TAC;
import pdf.registre_arrive.Generer_registre_arrive_confidentiel;
import pdf.registre_arrive.Generer_registre_arrive_divers;
import pdf.registre_arrive.Generer_registre_arrive_officiel;
import pdf.registre_arrive.Generer_registre_arrive_secret;
import util.Constants;

public class Control_Rapport_Arrive implements Initializable{

	@FXML
	private ChoiceBox<String> choice_registre;

	@FXML
	private ChoiceBox<String> choice_mois;

	@FXML
	private TableView<Beans_Message_Arrive> tableview;

	@FXML
	private TableColumn<Beans_Message_Arrive, Integer> colonne_numero_arrive;

	@FXML
	private TableColumn<Beans_Message_Arrive, String> colonne_expediteur;
	
	@FXML
	private TableColumn<Beans_Message_Arrive, Integer> colonne_numero_ordre;

	@FXML
	private TableColumn<Beans_Message_Arrive, String> colone_Mention;

	@FXML
	private TableColumn<Beans_Message_Arrive, LocalDate> colone_date;
	
	@FXML
	private TableColumn<Beans_Message_Arrive, String> crypto_systeme;

	@FXML
	private TableColumn<Beans_Message_Arrive, String> colone_objet;

	@FXML
	private TableColumn<Beans_Message_Arrive, String> colonne_nom_fichier;

	@FXML
	private AnchorPane pane;

	@FXML
	private Button bouton_imprimer_rapport;


	private ObservableList<Beans_Message_Arrive> obser;
	private boolean afficher_table = true;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

		choice_mois.getItems().addAll(Constants.liste_month);
		choice_mois.setValue(Constants.liste_month[0]);
		choice_registre.getItems().addAll("Diplomail","Officiel","Divers","TAC","Confidentiel","Secret");
		choice_registre.setValue("Diplomail");

		obser = FXCollections.observableArrayList();

		colonne_numero_arrive.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, Integer>("numero_arrive"));
		colonne_expediteur.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, String>("expediteur"));
		colonne_numero_ordre.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, Integer>("numero_d_ordre_expediteur"));
		colone_Mention.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, String>("mention"));
		colone_date.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, LocalDate>("date"));
		colone_objet.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, String>("objet_message"));
		crypto_systeme.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, String>("crypto_systeme"));
		colonne_nom_fichier.setCellValueFactory(new PropertyValueFactory<Beans_Message_Arrive, String>("nom_fichier") );
		tableview.setItems(obser);

	}



	@FXML
	private void on_valider_clicked() {
		obser.clear();

		
		if(choice_registre.getValue().equalsIgnoreCase("Diplomail")) {
			on_diplomail_selected();
		}else if(choice_registre.getValue().equalsIgnoreCase("Officiel")) {
			on_officiel_selected();
		}else if(choice_registre.getValue().equalsIgnoreCase("Divers")) {
			on_divers_selected();
		}else if(choice_registre.getValue().equalsIgnoreCase("TAC")) {
			on_TAC_selected();
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
		String sql = "SELECT * FROM MessageArrive WHERE date LIKE '%/" +corespondance_month_entier(choice_mois.getValue())+"/%'"; 

		if(!afficher_table) {//le tableau n est pas affiche
			colonne_numero_arrive.setVisible(true);
			colonne_expediteur.setVisible(true);
			colone_Mention.setVisible(true);
			crypto_systeme.setVisible(true);
			colonne_nom_fichier.setVisible(true);
			afficher_table = true;
		}
		
		try {	
			ResultSet rs = Query.select(sql);
			while( rs.next()) {
				Beans_Message_Arrive  bean = new Beans_Message_Arrive();
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


	private void on_divers_selected() {
		String sql = "SELECT * FROM Message_Divers_Arrive WHERE date LIKE '%/" +corespondance_month_entier(choice_mois.getValue())+"/%'"; 

		if(afficher_table) {//le tableau est deja affiche on enleve les colonnes
		colonne_numero_arrive.setVisible(false);
		//colonne_expediteur.setVisible(false);
		colone_Mention.setVisible(false);
		crypto_systeme.setVisible(false);
		colonne_nom_fichier.setVisible(false);
		afficher_table = false;
		}
		
		try {	
			ResultSet rs = Query.select(sql);
			while( rs.next()) {
				Beans_Message_Arrive  bean = new Beans_Message_Arrive();
				bean.formatFromDatabase_Divers(rs);
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
		String sql = "SELECT * FROM Message_Officiel_Arrive WHERE date LIKE '%/" +corespondance_month_entier(choice_mois.getValue())+"/%'"; 

		if(afficher_table) {//le tableau est deja affiche on enleve les colonnes
			colonne_numero_arrive.setVisible(false);
			colonne_expediteur.setVisible(false);
			colone_Mention.setVisible(false);
			crypto_systeme.setVisible(false);
			colonne_nom_fichier.setVisible(false);
			afficher_table = false;
			}
		
		try {	
			ResultSet rs = Query.select(sql);
			while( rs.next()) {
				Beans_Message_Arrive  bean = new Beans_Message_Arrive();
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
	
	private void on_TAC_selected() {
		String sql = "SELECT * FROM Message_TAC_Arrive WHERE date LIKE '%/" +corespondance_month_entier(choice_mois.getValue())+"/%'"; 

		if(afficher_table) {//le tableau est deja affiche on enleve les colonnes
			colonne_numero_arrive.setVisible(false);
			colonne_expediteur.setVisible(false);
			colone_Mention.setVisible(false);
			crypto_systeme.setVisible(false);
			colonne_nom_fichier.setVisible(false);
			afficher_table = false;
			}
		
		try {	
			ResultSet rs = Query.select(sql);
			while( rs.next()) {
				Beans_Message_Arrive  bean = new Beans_Message_Arrive();
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
		String sql = "SELECT * FROM MessageArriveConf WHERE date LIKE '%/" +corespondance_month_entier(choice_mois.getValue())+"/%'"; 

		if(afficher_table) {//le tableau est deja affiche on enleve les colonnes
			colonne_numero_arrive.setVisible(false);
			colone_Mention.setVisible(false);
			crypto_systeme.setVisible(false);
			colonne_nom_fichier.setVisible(false);
			afficher_table = false;
		}
		
		try {	
			ResultSet rs = Query.select(sql);
			while( rs.next()) {
				Beans_Message_Arrive  bean = new Beans_Message_Arrive();
				bean.formatFromDatabase_Conf(rs);
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
		String sql = "SELECT * FROM MessageArriveSecret WHERE date LIKE '%/" +corespondance_month_entier(choice_mois.getValue())+"/%'"; 

		if(afficher_table) {//le tableau est deja affiche on enleve les colonnes
			colonne_numero_arrive.setVisible(false);
			colone_Mention.setVisible(false);
			crypto_systeme.setVisible(false);
			colonne_nom_fichier.setVisible(false);
			afficher_table = false;
		}
		
		try {	
			ResultSet rs = Query.select(sql);
			while( rs.next()) {
				Beans_Message_Arrive  bean = new Beans_Message_Arrive();
				bean.formatFromDatabase_Secret(rs);
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
		}else if(choice_registre.getValue().equalsIgnoreCase("TAC")) {
			generer_registre_message_TAC(f);
		}

		

	}
	
	private void generer_registre_diplomail(File f) {
		Generer_registre_arrive gen = new Generer_registre_arrive(obser , f);
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
		Generer_registre_arrive_officiel gen = new Generer_registre_arrive_officiel(obser , f);
		gen.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				boolean b = gen.getValue();
				if(b) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText(null);
					alert.setContentText("Succés!! Fichier PDF généré avec succes");
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
		Generer_registre_arrive_secret gen = new Generer_registre_arrive_secret(obser , f);
		gen.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				boolean b = gen.getValue();
				if(b) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setHeaderText(null);
					alert.setContentText("\"Succes!! Fichier PDF genere avec succes");
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
		Generer_registre_arrive_confidentiel gen = new Generer_registre_arrive_confidentiel(obser , f);
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
		Generer_registre_arrive_divers gen = new Generer_registre_arrive_divers(obser , f);
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
	
	private void generer_registre_message_TAC(File f) {
		Generer_registre_arrive_TAC gen = new Generer_registre_arrive_TAC(obser , f);
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

}
