package parametres;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;

import database.Query;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import util.Constants;
import util.Methodes;

public class Parametres_controller implements Initializable{

	@FXML
	private TextField text_clair;

	@FXML
	private TextField text_crypto;

	@FXML
	private TextField text_imprimante_par_defaut;

	
	@FXML
	private TextField text_num_depart;
	
	@FXML
	private TextField text_num_depart_officiel;
	
	@FXML
	private TextField text_num_depart_divers;
	
	@FXML
	private TextField text_num_arrive;
	
	@FXML
	private TextField text_num_arrive_officiel;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		TimerTask task = new TimerTask() {
			public void run() {
				recupere_donne();
			}
		};
		Timer timer = new Timer("Timer");

		long delay = 5000L;
		timer.schedule(task, delay);
	}

	private void recupere_donne() {
		text_clair.setText(Constants.DOC_CLAIR);
		text_crypto.setText(Constants.DOC_CRYTO);
		text_imprimante_par_defaut.setText(Constants.Default_Printer_Name);
	}



	@FXML
	private void on_valider_clicked(ActionEvent event) {


		try {
			//email et dossier

			if(text_clair.getText().length()>0) {
				Constants.DOC_CLAIR = text_clair.getText();
				String sql_update = "UPDATE parametres SET donnee = '"+Constants.DOC_CLAIR+ "' WHERE ID = 1 ;";
				Query.insert(sql_update);
			}

			if(text_crypto.getText().length()>0) {
				Constants.DOC_CRYTO = text_crypto.getText();
				String sql_update = "UPDATE parametres SET donnee = '"+Constants.DOC_CRYTO+ "' WHERE ID = 2 ;";
				Query.insert(sql_update);
			}

			//imprimante par defaut deja set dans methode
			if(text_imprimante_par_defaut.getText().length()>0) {
				//
				String sql_update = "UPDATE parametres SET donnee = '"+Constants.Default_Printer_Name+ "' WHERE ID = 3 ;";
				//System.out.println(sql_update);
				Query.insert(sql_update);
			}	
			
			
			//Numero
			if(text_num_depart.getText().length() > 0) {
				if(Methodes.isStringInteger(text_num_depart.getText())) {
					Methodes.save("assets/MessageDepart.txt" , text_num_depart.getText()) ;
				}
			}
			
			if(text_num_depart_officiel.getText().length() > 0) {
				if(Methodes.isStringInteger(text_num_depart_officiel.getText())) {
					Methodes.save("assets/MessageDepartOfficiel.txt", text_num_depart_officiel.getText() );
				}
			}
			
			if(text_num_depart_divers.getText().length() > 0) {
				if(Methodes.isStringInteger(text_num_depart_divers.getText())) {
					Methodes.save("assets/MessageDepartDivers.txt" , text_num_depart_divers.getText() );
				}
			}
			
			
			if(text_num_arrive.getText().length() > 0) {
				if(Methodes.isStringInteger(text_num_arrive.getText())) {
					Methodes.save("assets/MessageArrive.txt" , text_num_arrive.getText()) ;
				}
			}
			
			if(text_num_arrive_officiel.getText().length() > 0) {
				if(Methodes.isStringInteger(text_num_arrive_officiel.getText())) {
					Methodes.save("assets/MessageArriveOfficiel.txt", text_num_arrive_officiel.getText() );
				}
			}
			
			

			//on notifie l utilisateur de l insertion avec suuces
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Insertion dans la base de donne");
			alert.setHeaderText(null);
			alert.setContentText("Insertion dans la base de donne effectuee avec succes!!!!.");
			alert.show();


		} catch (SQLException e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		} catch (Exception e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		}

	}


	@FXML
	private void on_path_clair_clicked() {
		DirectoryChooser chooser = new DirectoryChooser();
		File f = chooser.showDialog(null);
		text_clair.setText(f.getAbsolutePath());
	}


	@FXML
	private void on_path_crypto_clicked() {
		DirectoryChooser chooser = new DirectoryChooser();
		File f = chooser.showDialog(null);
		text_crypto.setText(f.getAbsolutePath());
	}

	@FXML
	private void choose_default_printer() {
		PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
		JobName jobName = new JobName("Example Print", null);
		attributeSet.add(jobName);


		//***********************
		//on liste les imprimantes disponibles et on le met dans une liste que l ondisplay dans le ui
		PrintService[] services = PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, attributeSet);

		//on cree une arrayliste et on met les emp dedans
		ArrayList<String> array = new ArrayList<>();


		for(PrintService s : services) {
			System.out.println(s.getName());
			array.add(s.getName());
		}


		//on show le dialog avec la liste des imprimmantes
		String[] obj_to_string = new String[array.size()];
		for(int i=0; i < obj_to_string.length; i++)
			//Convertir les objets en int
			obj_to_string[i]= array.get(i);

		printer_Dialog(obj_to_string);//on attribut le nom a la constante
	}


	private boolean printer_Dialog( String[] choices ) {

		ChoiceDialog<String> cDial = new ChoiceDialog<>(choices[0], choices);
		cDial.setTitle("Liste des imprimantes");
		cDial.setHeaderText("Veuillez choisir une imprimante");
		cDial.setContentText("Imprimantes d�t�ct�s :");
		Optional<String> selection = cDial.showAndWait();

		if ( selection.isPresent() )
		{
			System.out.println( "button text = " + selection.get() );
			System.out.println( "choice = " + cDial.getSelectedItem());
			Constants.Default_Printer_Name = cDial.getSelectedItem();
			text_imprimante_par_defaut.setText(Constants.Default_Printer_Name);
			return true;
		} 

		return false;

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
