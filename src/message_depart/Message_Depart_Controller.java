package message_depart;

import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.PageRanges;

import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.StyleClassDecoration;
import org.controlsfx.control.textfield.TextFields;

import beans.Beans_Message_Depart;
import database.Query;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import pdf.GeneratePDF;
import print.Imprimer;
import util.Constants;
import util.Constants.Mention;
import util.Constants.Type_Message;
import util.DateFormater;
import util.Methodes;

public class Message_Depart_Controller implements Initializable{

	@FXML
	private TextField text_num_depart;

	@FXML
	private TextField text_destinataire_diplomail;

	@FXML
	private TextField textfield_numero_d_ordre;

	@FXML
	private DatePicker date_picker;

	@FXML
	private TextArea area_objet_message;

	@FXML
	private ChoiceBox<String> choice_type_message;

	@FXML
	private ChoiceBox<String> choice_mention;

	@FXML
	private Label label_date;

	@FXML
	private Label label_numero;

	@FXML
	private Label label_poste_diplo;

	@FXML
	private Label label_objet;

	@FXML
	private Label label_reseau;

	@FXML
	private Label label_mention;

	@FXML
	private Label label_securite_chiffre;

	@FXML
	private Rectangle rectangle_securite_chiffre;

	@FXML
	private Rectangle rectangle_mention;

	@FXML
	private Rectangle rectangle_destinataire;

	@FXML
	private AnchorPane anchor_pane;

	@FXML
	private Pane panneau_destinataire;

	@FXML
	private Pane pane_new_libelle;

	@FXML
	private TextArea area_new_lebelle;

	@FXML
	private Button bouton;//bouton ok dans ajout

	@FXML
	private CheckBox check_print_pdg;

	@FXML
	private AnchorPane anchorPane_page_garde;

	@FXML
	private Pane panneau_steps;

	@FXML
	private ChoiceBox<String> choice_systeme_cryptographique;

	@FXML
	private Circle cercle_save_bd;

	@FXML
	private Circle cercle_generate_pdg;

	@FXML
	private Circle cercle_file_concatene;

	@FXML
	private Label label_save_bd;

	@FXML
	private Label label_generate_pdg;

	@FXML
	private Label label_file_concatene;

	@FXML
	private Label label_pane_steps;

	@FXML
	private Button bouton_valider;

	@FXML
	private Label origine;


	private Mention mention = Mention.Claire;
	private Type_Message type_message = Type_Message.Officiel;
	private String groupe_date;
	private File file_a_concatener;
	private String nom_fichier;
	private boolean bool_fichier_a_concatener_is_set = false;
	private File path_initial_fichier_a_concatener = null;
	//Lecture pour garder tracabilité
	private int num_dernier_message_depart;
	private int num_dernier_message_depart_officiel;
	private int num_dernier_message_depart_divers;

	private PrintService services;
	private PrintRequestAttributeSet attributeSet;
	private SimpleBooleanProperty update_gui_boolean;

	private String global_num_depart = "";
	private String global_destinataire = "";
	private LocalDate global_date;
	private String global_numero_ordre =  "";
	private String global_objet = "";


	@Override
	public void initialize(URL location, ResourceBundle resources) {	

		choice_mention.getItems().addAll("CLAIR" , "CONFIDENTIEL" , "SECRET" , "TRES_SECRET");
		choice_mention.setValue("CLAIR");

		choice_type_message.getItems().addAll("Officiel","TAC","Service","Divers");
		choice_type_message.setValue("Officiel");

		choice_systeme_cryptographique.getItems().addAll("CryptoForge" , "SecureIT" , "PGP" , "HEC");
		choice_systeme_cryptographique.setValue("CryptoForge");


		date_picker.setValue(LocalDate.now());
		label_date.setText("Dakar , le " + DateFormater.DateToString(date_picker.getValue()));

		update_gui_boolean = new SimpleBooleanProperty();
		update_gui_boolean.setValue(true);
		update_gui_boolean.addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				//des que valeur change on cache peut import la valeur
				cercle_save_bd.setVisible(false);
				label_save_bd.setVisible(false);
				cercle_generate_pdg.setVisible(false);
				label_generate_pdg.setVisible(false);
				cercle_file_concatene.setVisible(false);
				label_file_concatene.setVisible(false);


			}
		});

		TextFields.bindAutoCompletion(
				text_destinataire_diplomail,
				Constants.liste_ambassade);


		text_destinataire_diplomail.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				label_poste_diplo.setText(newValue);
				for( String amb : Constants.liste_ambassade) {
					if(amb.equalsIgnoreCase(newValue)) {
						System.out.println("ambassade detecte");
						textfield_numero_d_ordre.setText(Integer.toString( Constants.hash_num_departs.get(newValue) ) );
					}
				}

			}
		});


		groupe_date = String.format("%02d", LocalDate.now().getDayOfMonth())+""+String.format("%02d", LocalDate.now().getMonthValue());

		//binding choice_type_message
		choice_type_message.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				if(newValue.equalsIgnoreCase("Officiel")) {			
					type_message = Type_Message.Officiel;
					//on recupere le dernier numero a chache changement de type de message uniquement
					textfield_numero_d_ordre.setText( (num_dernier_message_depart_officiel + 1) +"" );
					label_numero.setText(Methodes.ajout_point_50000( (num_dernier_message_depart_officiel + 1) +"" ));

				}else if(newValue.equalsIgnoreCase("Service")) {	 
					//si on a un Service alors on affiche juste le Message Service
					type_message = Type_Message.Service;
					textfield_numero_d_ordre.setText("SERVICE");
					label_numero.setText("SERVICE");

				}else{//Divers
					type_message = Type_Message.Divers;
					//on recupere le dernier numero a chache changement de type de message uniquement
					textfield_numero_d_ordre.setText( (num_dernier_message_depart_divers + 1) +"" );
					label_numero.setText(Methodes.ajout_point_50000( (num_dernier_message_depart_divers + 1) +"" ));
				}

			}
		});


		//binding mention
		choice_mention.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				if(oldValue.equalsIgnoreCase("TRES_SECRET")) {
					//on cache la mention securite chiffre
					rectangle_securite_chiffre.setVisible(false);
					label_securite_chiffre.setVisible(false);
				}

				if(oldValue.equalsIgnoreCase("CLAIR")) {
					//on affiche a nouveau la mention
					//	label_mention.setVisible(true);
					//	rectangle_mention.setVisible(true);
					label_mention.setTextFill(Color.RED);
					rectangle_mention.setStroke(Color.RED);
				}

				////////////////*************************************///////////////

				//puis on verifie le reste
				if(newValue.equalsIgnoreCase("CONFIDENTIEL")) {			

					label_mention.setText("CONFIDENTIEL");
					mention = Mention.Confidentiel;

				}else if(newValue.equalsIgnoreCase("SECRET")){

					label_mention.setText("SECRET");
					mention = Mention.Secret;

				}else if(newValue.equalsIgnoreCase("CLAIR")) {
					//cas claire alors on affiche rien
					mention = Mention.Claire;
					//label_mention.setVisible(false);
					//rectangle_mention.setVisible(false);
					label_mention.setText("CLAIR");
					label_mention.setTextFill(Color.BLACK);
					rectangle_mention.setStroke(Color.BLACK);

				}else{


					label_mention.setText("TRES SECRET");
					mention = Mention.Tres_Secret;
					//on affiche la mention securite chiffre
					rectangle_securite_chiffre.setVisible(true);
					label_securite_chiffre.setVisible(true);
				}
			}
		});

		date_picker.valueProperty().addListener(new ChangeListener<LocalDate>() {

			@Override
			public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
					LocalDate newValue) {
				label_date.setText("Dakar , le "+ DateFormater.DateToString(newValue));

			}
		});

		//recuperation du nombre de message officiel par defaut et sur la page de garde

		num_dernier_message_depart = Integer.parseInt( Methodes.load("assets/MessageDepart.txt") );
		num_dernier_message_depart_officiel = Integer.parseInt( Methodes.load("assets/MessageDepartOfficiel.txt") );
		num_dernier_message_depart_divers = Integer.parseInt( Methodes.load("assets/MessageDepartDivers.txt") );

		//Diplomail depart
		text_num_depart.setText( (num_dernier_message_depart + 1 ) +"");

		textfield_numero_d_ordre.setText( (num_dernier_message_depart_officiel +1) + "" );
		label_numero.setText( Methodes.ajout_point_50000( (num_dernier_message_depart_officiel +1) + "" ) );


		//timer pour decaler le demarage du logiciel et celui du service car sinon 
		//depart et arive demarrent en mem temp leur requete BD qui fait planter le logiciel
		recupere_donne();
		recupere_numero_depart();
		label_date.setText("Dakar , le " + DateFormater.DateToString(date_picker.getValue()));

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				System.out.println("Dans imprimante daprt controller");

				attributeSet = new HashPrintRequestAttributeSet();
				JobName jobName = new JobName("Example Print", null);
				attributeSet.add(jobName);
				attributeSet.add(new PageRanges(1));
				//***********************
				//On selectionne l imprimante par defaut deja defini sur l ordinateur pour des
				// IMPORTANT : Par defaut tres rapide   ,  si list alors tres lent.
				services = PrintServiceLookup.lookupDefaultPrintService();//PrintServiceLookup.lookupPrintServices(DocFlavor.SERVICE_FORMATTED.PAGEABLE, attributeSet);
			}
		}, 2000L);

	}

	/// binding des differents champs
	@FXML
	private void update_label_num_depart() {
		//on ajoute le point pour decorer
		label_numero.setText(Methodes.ajout_point_50000(text_num_depart.getText()));
	}

	@FXML
	private void update_label_objet() {
		label_objet.setText(area_objet_message.getText());
	}

	@FXML
	private void on_destinataire_focus_loose() {
		label_poste_diplo.setText(text_destinataire_diplomail.getText());
	}



	///------------------------------

	@FXML
	private void on_valider_clicked() {

		//on block bouton valider
		bouton_valider.setDisable(true);

		String num_depart = text_num_depart.getText();
		String destinataire = text_destinataire_diplomail.getText();
		LocalDate date = date_picker.getValue();
		String type_message = choice_type_message.getValue();
		String mention_string = choice_mention.getValue();
		String numero_ordre =  textfield_numero_d_ordre.getText();
		String crypto_systeme = choice_systeme_cryptographique.getSelectionModel().getSelectedItem();
		//on remplace les ' par * car sinon probléme insertion dans la base de donnéé. regarder bean message depart
		String objet = area_objet_message.getText();

		if(!validation()) {
			bouton_valider.setDisable(false);
			return;
		}

		global_num_depart = num_depart;
		global_destinataire = destinataire;
		global_date = date;
		global_numero_ordre =  numero_ordre;
		global_objet = objet;

		//FICHIER A CONCATENER EST VERIFIE DANS VALidATION
		DecimalFormat formatter = new DecimalFormat("000");
		nom_fichier = "mae"+groupe_date+"s" + formatter.format(Integer.parseInt(global_num_depart)) +".pdf";//si il y a modification par le user dans le save dialog


		//ouvertur conexion et insertion puis fermeture le tous dans query
		try {
			boolean b = false;
			// INSERTION DANS LA BASE DE DONNE

			Beans_Message_Depart beans;
			//DANS LE CAS DE SERVICE ON MET 0 POUR REPRESER CAR SINON ON AURA UN STRING
			//le this est important car variables differents String et enum

			////////////******************************** DEBUT SERVICE ***************************////////////////////////////////
			///
			if(this.type_message == Type_Message.Service) {


				if (mention == Mention.Claire) {
					beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, objet,type_message ,0,crypto_systeme , nom_fichier);
					b = Query.insert(beans.formatToDatabase());
				} else if (mention == Mention.Confidentiel) {
					beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, "Conf",type_message ,0,crypto_systeme, nom_fichier);
					b = Query.insert(beans.formatToDatabase());
					//insert conf
					beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, objet,type_message ,0,crypto_systeme, nom_fichier);
					b = Query.insert(beans.formatToDatabase_conf());
				} else if (mention == Mention.Secret || mention == Mention.Tres_Secret) {
					beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, "Secret",type_message ,0,crypto_systeme, nom_fichier);
					b = Query.insert(beans.formatToDatabase());
					//insert Secret
					beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, objet,type_message ,0,crypto_systeme, nom_fichier);
					b = Query.insert(beans.formatToDatabase_Secret());
				}


			}
			else   ////////////******************************** OFFICIEL et DIVERS  ***************************////////////////////////////////
			{

				beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, objet,type_message ,Integer.valueOf(numero_ordre),crypto_systeme, nom_fichier);

				// ON VERIFIE SI LE NUMERO DEPART EXISTE DEJA. SI OUI ON UPDATE SI NON ON INSERT
				boolean verification_existance_num_depart = beans.verifie_si_numero_depart_exist();

				//################################   UPDATE ###########################//

				//on verifie si le num depart correspond existe deja
				if(verification_existance_num_depart) {
					//si il existe alors on update sauf dans le cas service car num ordre = tjrs 0

					System.out.println("dans update message diplomail numero ordre existe deja");


					//on informe l utilisateur si num existe deja
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setHeaderText("Numero d'ordre existe déja.");
					alert.setContentText("Le numero d'ordre existe déja, le précédent ligne dans la base de donnée va étre mise a jour si vous appuyez sur OUI. Veuillez "
							+ "appuyer sur NON si vous voulez annuler l'opération.");
					Optional<ButtonType> result = alert.showAndWait();

					if (result.isPresent() && result.get() == ButtonType.OK) {


						if (mention == Mention.Claire) {

							b = Query.insert(beans.format_Update_Database());
							if(type_message.equalsIgnoreCase("Officiel")) {
								//update dans base de donnee 50000
								Query.insert(beans.format_Update_Depart_officiel_Database());

							}else if(type_message.equalsIgnoreCase("Divers")) {
								//insertion dans base de donne 70000
								Query.insert(beans.format_Update_Depart_divers_Database());
							}

						} else if (mention == Mention.Confidentiel) {

							beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, "Conf",type_message ,Integer.valueOf(numero_ordre),crypto_systeme, nom_fichier);
							b = Query.insert(beans.format_Update_Database());
							if(type_message.equalsIgnoreCase("Officiel")) {
								//insertion dans base de donnee 50000
								Query.insert(beans.format_Update_Depart_officiel_Database());

							}else if(type_message.equalsIgnoreCase("Divers")) {
								//insertion dans base de donne 70000
								Query.insert(beans.format_Update_Depart_divers_Database());
							}
							//insert conf
							beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, objet,type_message ,Integer.valueOf(numero_ordre),crypto_systeme, nom_fichier);
							b = Query.insert(beans.format_Update_Depart_conf_Database());

						} else if (mention == Mention.Secret  || mention == Mention.Tres_Secret) {

							beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, "Secret",type_message ,Integer.valueOf(numero_ordre),crypto_systeme, nom_fichier);
							b = Query.insert(beans.format_Update_Database());
							if(type_message.equalsIgnoreCase("Officiel")) {
								//insertion dans base de donnee 50000
								Query.insert(beans.format_Update_Depart_officiel_Database());

							}else if(type_message.equalsIgnoreCase("Divers")) {
								//insertion dans base de donne 70000
								Query.insert(beans.format_Update_Depart_divers_Database());
							}
							//insert conf
							beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, objet,type_message ,Integer.valueOf(numero_ordre),crypto_systeme, nom_fichier);
							b = Query.insert(beans.format_Update_Depart_secret_Database());

						}
					}
					else {
						System.out.println("dans update message  ANNULATION EFFECTUE");
						bouton_valider.setDisable(false);
						return;
					}

				}
				else			//################################   INSERT ###########################//
				{


					if (mention == Mention.Claire) {

						beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, objet,type_message ,Integer.valueOf(numero_ordre),crypto_systeme, nom_fichier);
						b = Query.insert(beans.formatToDatabase());
						// Update des numeros depart (PAS DE 50.000) ex 45
						Methodes.save("assets/MessageDepart.txt" , num_depart) ;
						num_dernier_message_depart += 1;

						if(type_message.equalsIgnoreCase("Officiel")) {
							//insertion dans base de donnee 50000
							Query.insert(beans.formatToDatabase_officiel());
							Methodes.save("assets/MessageDepartOfficiel.txt", numero_ordre );
							num_dernier_message_depart_officiel += 1;

						}else if(type_message.equalsIgnoreCase("Divers")) {
							//insertion dans base de donne 70000
							Query.insert(beans.formatToDatabase_divers());
							Methodes.save("assets/MessageDepartDivers.txt" , numero_ordre );
							num_dernier_message_depart_divers += 1;
						}

					} else if (mention == Mention.Confidentiel) {

						beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, "Conf",type_message ,Integer.valueOf(numero_ordre),crypto_systeme, nom_fichier);
						b = Query.insert(beans.formatToDatabase());
						Methodes.save("assets/MessageDepart.txt" , num_depart) ;
						num_dernier_message_depart += 1;

						if(type_message.equalsIgnoreCase("Officiel")) {
							//insertion dans base de donnee 50000
							Query.insert(beans.formatToDatabase_officiel());
							Methodes.save("assets/MessageDepartOfficiel.txt", numero_ordre );
							num_dernier_message_depart_officiel += 1;

						}else if(type_message.equalsIgnoreCase("Divers")) {
							//insertion dans base de donne 70000
							Query.insert(beans.formatToDatabase_divers());
							Methodes.save("assets/MessageDepartDivers.txt" , numero_ordre );
							num_dernier_message_depart_divers += 1;
						}
						//insert conf
						beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, objet,type_message ,Integer.valueOf(numero_ordre),crypto_systeme, nom_fichier);
						b = Query.insert(beans.formatToDatabase_conf());

					} else if (mention == Mention.Secret || mention == Mention.Tres_Secret) {

						beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, "Secret",type_message ,Integer.valueOf(numero_ordre),crypto_systeme, nom_fichier);
						b = Query.insert(beans.formatToDatabase());
						Methodes.save("assets/MessageDepart.txt" , num_depart) ;
						num_dernier_message_depart += 1;

						if(type_message.equalsIgnoreCase("Officiel")) {
							//insertion dans base de donnee 50000
							Query.insert(beans.formatToDatabase_officiel());
							Methodes.save("assets/MessageDepartOfficiel.txt", numero_ordre );
							num_dernier_message_depart_officiel += 1;

						}else if(type_message.equalsIgnoreCase("Divers")) {
							//insertion dans base de donne 70000
							Query.insert(beans.formatToDatabase_divers());
							Methodes.save("assets/MessageDepartDivers.txt" , numero_ordre );
							num_dernier_message_depart_divers += 1;
						}
						//insert Secret
						beans = new Beans_Message_Depart(Integer.valueOf(num_depart), destinataire, mention_string , date, objet,type_message ,Integer.valueOf(numero_ordre),crypto_systeme, nom_fichier);
						b = Query.insert(beans.formatToDatabase_Secret());

					}
				}
			}


			if(b) {
				cercle_save_bd.setFill(Color.LIGHTGREEN);
				cercle_save_bd.setVisible(true);
				label_save_bd.setVisible(true);
				label_pane_steps.setText("Sauvegarde dans la base de donne reussi.");
			}else {
				cercle_save_bd.setFill(Color.RED);
				cercle_save_bd.setVisible(true);
				label_save_bd.setVisible(true);
				label_pane_steps.setText("Sauvegarde dans la base de donne a echoue. Veuillez lire le rapport d erreur.");
			}

			//on deblock bouton valider
			bouton_valider.setDisable(false);
			//on reinitialise le bool pour le prochain
			bool_fichier_a_concatener_is_set = false;

			//clear des differents champs
			area_objet_message.clear();
			text_num_depart.setText((num_dernier_message_depart + 1) +"");
			label_objet.setText("Objet du Message");
			//LE CAS TYPE SERVICE sinon erreur car num est String 'SERVICE'
			if( !type_message.equals("Service")) {
				textfield_numero_d_ordre.setText((num_dernier_message_depart_officiel + 1) +"" );
				label_numero.setText((num_dernier_message_depart_officiel + 1) +"" );
			}


		} catch (Exception e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
			return;
		} 
	}



	private boolean validation() {

		//on enleve tout les decorations avant de mettre le mauvais
		Decorator.removeAllDecorations(text_num_depart);
		Decorator.removeAllDecorations(text_destinataire_diplomail);
		Decorator.removeAllDecorations(area_objet_message);
		Decorator.removeAllDecorations(textfield_numero_d_ordre);
		Decorator.removeAllDecorations(choice_systeme_cryptographique);

		//numero de depart
		if (text_num_depart.getText().length() <= 0) {
			Decorator.addDecoration(text_num_depart, new StyleClassDecoration("warning"));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Champs Vide");
			alert.setHeaderText(null);
			alert.setContentText("Le champs Numero de départ est vide.\n veuillez le remplir avant de reessayer.");
			alert.show();
			return false;
		}

		//numero de depart est un entier
		if(!Methodes.isStringInteger(text_num_depart.getText())) {
			Decorator.addDecoration(text_num_depart, new StyleClassDecoration("warning"));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Format Incorrect");
			alert.setHeaderText(null);
			alert.setContentText("Le champs Numero de départ doit etre un entier.\n veuillez corriger avant de reessayer.");
			alert.show();
			return false;
		}

		//numero d ordre
		//cas specifique des services
		if(type_message != Type_Message.Service) {
			if(!Methodes.isStringInteger(textfield_numero_d_ordre.getText())) {
				Decorator.addDecoration(textfield_numero_d_ordre, new StyleClassDecoration("warning"));
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Format Incorrect");
				alert.setHeaderText(null);
				alert.setContentText("Le champs Numero d odre doit etre un entier.\n veuillez corriger avant de reessayer.");
				alert.show();
				return false;
			}
		}

		//numero d ordre
		//cas erreur de frappe 500.000 en lieu et place de 50.000
		//verification si integer deja effectue
		if(type_message != Type_Message.Service) {
			if(Integer.valueOf(textfield_numero_d_ordre.getText())  > 100000 ) {
				Decorator.addDecoration(textfield_numero_d_ordre, new StyleClassDecoration("warning"));
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Format Incorrect");
				alert.setHeaderText(null);
				alert.setContentText("Le champs Numero d odre ne peut depasser 100.000.\n veuillez verifier si il n'y a pas d'erreur de frappe avant de reessayer.");
				alert.show();
				return false;
			}
		}


		//destinataire
		//on verifie si le champ destinataire n est pas vide
		if ( text_destinataire_diplomail.getText().length() <= 0) {
			Decorator.addDecoration(text_destinataire_diplomail, new StyleClassDecoration("warning"));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur!!!! Ambassade");
			alert.setHeaderText(null);
			alert.setContentText("Le champs destinataire du message est vide.\\n veuillez le remplir avant de reessayer.!!");
			alert.show();
			return false;
		}
		
		boolean b = false;
		String dest = text_destinataire_diplomail.getText();
		for( String amb : Constants.liste_ambassade) {
			if(amb.equalsIgnoreCase(dest)) {
				b = true;
			}
		}
		
		if(b == false) {
			Decorator.addDecoration(text_destinataire_diplomail, new StyleClassDecoration("warning"));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur!!!! Ambassade");
			alert.setHeaderText(null);
			alert.setContentText("Le destinataire du message n'est pas prédéfini.\\n veuillez revoir le champ avant de reessayer.!!");
			alert.show();
			return false;
		}


		//objet message

		if (area_objet_message.getText().length() <= 0) {
			Decorator.addDecoration(area_objet_message, new StyleClassDecoration("warning"));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Champs Vide");
			alert.setHeaderText(null);
			alert.setContentText("Le champs Objet du message est vide.\n veuillez le remplir avant de reessayer.");
			alert.show();
			return false;
		}


		//CAS DU SET FICHIER A CONCATENER
		if(!bool_fichier_a_concatener_is_set) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Fichier a concatener non defini.");
			alert.setHeaderText(null);
			alert.setContentText("Le Fichier a concatener est non defini..\n veuillez le selectionner avant de reessayer.");
			alert.show();
			return false;
		}

		return true;
	}


	@FXML
	private void open_file(){

		//recuperation du document a concatener
		FileChooser chooser = new FileChooser();
		if(path_initial_fichier_a_concatener != null) {
			chooser.setInitialDirectory(path_initial_fichier_a_concatener);
		}
		chooser.getExtensionFilters().add(new ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf"));
		chooser.setTitle("Choisir fichier a concatener");
		file_a_concatener = chooser.showOpenDialog(anchorPane_page_garde.getScene().getWindow());

		//SI LE FICHIER EST VIDE ON RETOURNE
		if(file_a_concatener == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Pas de fichier séléctionné.");
			alert.setContentText("Erreur pas de fichier séléctionné. Aucune insertion dans la base de donnée éfféctué. Veuillez reprendre la procédure.");
			alert.show();
			System.out.println("Fichier non séléctionné retour");
			bouton_valider.setDisable(false);

			cercle_file_concatene.setFill(Color.RED);
			cercle_file_concatene.setVisible(true);
			label_file_concatene.setVisible(true);
			label_pane_steps.setText("Ajout Fichier a echoue. Veuillez lire le rapport d erreur.");

		}else {
			bool_fichier_a_concatener_is_set = true;
			path_initial_fichier_a_concatener = file_a_concatener.getParentFile();

			cercle_file_concatene.setFill(Color.LIGHTGREEN);
			cercle_file_concatene.setVisible(true);
			label_file_concatene.setVisible(true);
			label_pane_steps.setText("Ajout Fichier reussi.");
		}

	}



	@FXML
	private void generer_PDG() {
		System.out.println("Dans generate pdf avant generation pdf");
		DecimalFormat formatter = new DecimalFormat("000");
		nom_fichier = "mae"+groupe_date+"s" + formatter.format(Integer.parseInt(global_num_depart)) +".pdf";//si il y a modification par le user dans le save dialog

		System.out.println("Dans generate pdf 1");

		GeneratePDF pdf = new GeneratePDF(global_numero_ordre, global_destinataire, global_date, global_objet, mention,file_a_concatener , nom_fichier);
		pdf.setOnSucceeded(new EventHandler<WorkerStateEvent>() {



			@Override
			public void handle(WorkerStateEvent event) {
				boolean b = pdf.getValue();
				System.out.println("b value = "+b);
				if(b) {

					File f;

					if(Constants.DOC_CLAIR.length()>0) {
						//chooser.setInitialDirectory(new File(Constants.DOC_CLAIR));
						// **** dan sce cas on sait deja le directory. alors on ajoute juste le separator et le nom du fichier
						f = new File(Constants.DOC_CLAIR+"//"+nom_fichier);
						System.out.println(f.getAbsolutePath());
					}else {
						//si le directory n est pas choisi, alors il demande de le choisir
						FileChooser chooser = new FileChooser();
						chooser.getExtensionFilters().add(new ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf"));
						chooser.setInitialFileName(nom_fichier);//mois en deux digit 05 au lieu 5
						f = chooser.showSaveDialog(anchorPane_page_garde.getScene().getWindow());
					}
					//SI LE FICHIER EST VIDE ON RETOURNE
					System.out.println("Dans generate pdf avant verification si fichier vide");
					if(f == null) {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setHeaderText("Pas de fichier séléctionné.");
						alert.setContentText("Erreur pas de fichier séléctionné. Aucune insertion dans la base de donnée éfféctué. Veuillez reprendre la procédure.");
						alert.show();
						System.out.println("Fichier non séléctionné retour");
						bouton_valider.setDisable(false);
						return;
					}
					nom_fichier = f.getName();
					//	path_file_concatene = f.getAbsolutePath();
					try {
						byte [] buffer = new byte[2048];

						FileInputStream in = new FileInputStream("assets/generate.pdf");//lecture
						//ecriture
						BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
						int count = 0;	

						while((count = in.read(buffer))!=-1){
							out.write(buffer, 0, count);
						}

						Desktop.getDesktop().open(f);

						in.close();
						out.close();



						cercle_generate_pdg.setFill(Color.LIGHTGREEN);
						label_generate_pdg.setVisible(true);
						cercle_generate_pdg.setVisible(true);
						label_pane_steps.setText("Generation de la page de garde reussi.");

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						cercle_generate_pdg.setFill(Color.RED);
						label_generate_pdg.setVisible(true);
						cercle_generate_pdg.setVisible(true);
						e.printStackTrace();
						showExceptionAlert("Erreur fichier", "Erreur fichier non trouvé. Veuillez verifier le code", e);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						cercle_generate_pdg.setFill(Color.RED);
						label_generate_pdg.setVisible(true);
						cercle_generate_pdg.setVisible(true);
						e.printStackTrace();
						showExceptionAlert("Erreur fichier", "Erreur acces au fichier. Veuillez verifier le code", e);
					}

					//apres generation on verifie si case impimer coche et on agit suiv	nt le cas
					if(check_print_pdg.isSelected()) {
						Imprimer imp = new Imprimer();				
						boolean bool = imp.printPDF(services , attributeSet);
						if(bool) {
							label_pane_steps.setText("Veuillez glisser-deposer le fichier a concatener pour pousuivre les operations.");
						}else {
							Alert ale = new Alert(AlertType.ERROR);
							ale.setHeaderText(null);
							ale.setContentText("Erreur!!!!! \nEcheque de l Impression. Veuiller verifier si l imprmante est connecté!!!!");
							ale.show();
						}
					}else {//si pas d impression , on informe l utilisateur qu il doit concatener le fichier
						label_pane_steps.setText("Veuillez glisser-deposer le fichier a concatener pour pousuivre les operations.");
					}

					//chiffrement
					//	chiffrement();


					Timer timer = new Timer();
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							update_gui_boolean.setValue( !update_gui_boolean.getValue() ); ;
						}
					}, 5000L);

				}else {
					area_objet_message.setText(pdf.getException().getMessage());					
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText(null);
					alert.setContentText("Erreur dans la generation du fichier PDF. Veuillez regarder le log pour plus d information");
					alert.show();
					System.out.println(pdf.getException().getMessage());
				}
			}
		});

		pdf.setOnFailed( new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent event) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText(null);
				alert.setContentText("Erreur dans la generation du fichier PDF. Veuillez regarder le log pour plus d information");
				alert.show();
				System.out.println(pdf.getException().getMessage());
			}
		});
		pdf.start();

	}



	//	/*******************SURTOUT NE PAS SUPPRIMER JUSTE DECOMMENTER****************/
	//		private void sendEmail() {
	//	
	//			//test pour eviter erreur // penser a envoyer retour de succes ou d echec a user
	//			//System.out.println("mail sended");
	//	
	//	
	//			String from = Constants.EMAIL;  //"tallalos1b@outlook.com";//  "tallalos1b@gmail.com";
	//			String password = Constants.MDP_EMAIL; //"Tallalolo"; //"cppudazthrarqngy";
	//			String to = "tallalos1b@gmail.com"; //je n ai pas d autre destinataire
	//			String group_heur = String.format("%02d", LocalTime.now().getHour())+""+String.format("%02d", LocalTime.now().getMinute()); 
	//			String objet = "NR : "+ numero_ordre +" - UN FICHIER JOINT - "+ groupe_date+"/"+group_heur;
	//			//.cfe et .sitw ajoute dans xhiffrer avec  dans handle drop 
	//			Service_Email_Sender email = new Service_Email_Sender(from, password, to , objet , "Ceci est un body de test" , path_file_concatene, nom_fichier );
	//			email.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
	//	
	//				@Override
	//				public void handle(WorkerStateEvent event) {
	//					boolean b = email.getValue();
	//					System.out.println("valeur recu de send mail "+b);
	//					if(b) {
	//						cercle_send_mail.setFill(Color.LIGHTGREEN);
	//						label_send_mail.setVisible(true);
	//						cercle_send_mail.setVisible(true);
	//					}else {
	//						cercle_send_mail.setFill(Color.RED);
	//						label_send_mail.setVisible(true);
	//						cercle_send_mail.setVisible(true);
	//					}
	//				}
	//			});
	//			email.start();
	//		}




	/*	private void chiffrement(){

		if(systeme_crypto == Systeme_cryptographique.CryptoForge) {

			chiffrer_avec_cryptoforge(mdp_crypto, path_file_concatene);
			path_file_concatene += ".cfe";
			nom_fichier += ".cfe";
			//
			System.out.println(path_file_concatene);
			System.out.println(nom_fichier);
		}else if(systeme_crypto == Systeme_cryptographique.SecureIT){

			chiffrer_avec_secureIT(mdp_crypto, path_file_concatene);
			path_file_concatene = path_file_concatene.subSequence(0, path_file_concatene.length() -4)+ "_pdf.sitw";;
			nom_fichier = nom_fichier.subSequence(0, nom_fichier.length() -4)+ "_pdf.sitw";
			//
			System.out.println(path_file_concatene);
			System.out.println(nom_fichier);
		}else if(systeme_crypto == Systeme_cryptographique.None) {
			//dans ce cas on  ne chiffre pass
			System.out.println("Aucun cryptosysteme choisi");
		}


		//****************************************IMPORTANT************************************************************************

		//PROBLEME de delai a envisager genre le chifffrement n a pas fini et il essae de l envoyer

		//on regarde< si case envoyer mail coche et on envoie si oui
		//		/*******************SURTOUT NE PAS SUPPRIMER JUSTE DECOMMENTER****************/
	//				if(check_send_mail.isSelected()) {
	//					sendEmail();
	//				}

	/*		label_pane_steps.setText("Chifrement effectue avec succes");
		//on deblock bouton valider
		bouton_valider.setDisable(false);



		//timer pour decaler l a ffichage des differents elements
		TimerTask task = new TimerTask() {
			public void run() {
				//fin des operation et reprise a zero
				cercle_save_bd.setVisible(false);
				cercle_generate_pdg.setVisible(false);
				cercle_chiffrement.setVisible(false);
				cercle_send_mail.setVisible(false);

				label_save_bd.setVisible(false);
				label_generate_pdg.setVisible(false);
				label_chiffrement.setVisible(false);
				label_send_mail.setVisible(false);
			}
		};
		Timer timer = new Timer("Timer");

		long delay = 8000L;
		timer.schedule(task, delay);
	}


	private void chiffrer_avec_cryptoforge(String password , String path_file) {
		try {

			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c" , "cd \""+Constants.PATH_CRYPTOFORGE+"\" && CFFiles /e  \"/passw:"+password+"\" "+path_file); ///sa:Shred /passes:6 a ajouter pour supprimer fichier original
			builder.redirectErrorStream(true);
			Process pro = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			String line;
			while(true) {
				line = reader.readLine();
				if(line == null) {
					break;
				}
				System.out.println(line);
			}
			cercle_chiffrement.setFill(Color.LIGHTGREEN);
			label_chiffrement.setVisible(true);
			cercle_chiffrement.setVisible(true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			cercle_chiffrement.setFill(Color.RED);
			label_chiffrement.setVisible(true);
			cercle_chiffrement.setVisible(true);
			showExceptionAlert("Erreur fichier", "Erreur fichier lors du chiffrement. Veuillez verifier le code", e);
			e.printStackTrace();
		}
	}



	private void chiffrer_avec_secureIT(String password , String path_file) {
		try {

			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c" , "cd \""+ Constants.PATH_SECUREIT +"\" && secureit /encrypt /pw:\""+password+"\" "+path_file);
			builder.redirectErrorStream(true);
			Process pro = builder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			String line;
			while(true) {
				line = reader.readLine();
				if(line == null) {
					break;
				}
				System.out.println(line);
			}
			cercle_chiffrement.setFill(Color.LIGHTGREEN);
			label_chiffrement.setVisible(true);
			cercle_chiffrement.setVisible(true);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			showExceptionAlert("Erreur fichier", "Erreur fichier lors du chiffrement. Veuillez verifier le code", e);
			cercle_chiffrement.setFill(Color.RED);
			label_chiffrement.setVisible(true);
			cercle_chiffrement.setVisible(true);
			e.printStackTrace();
		}
	}



	private void alert_cryptosysteme() {
		Decorator.addDecoration(choice_systeme_cryptographique, new StyleClassDecoration("warning"));
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Format Incorrect");
		alert.setHeaderText(null);
		alert.setContentText("Le mots de passe specifie n est pas deja configuré.\n veuillez corriger cela dans l onglet parametre avant de reessayer.");
		alert.show();
	}*/

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


	private void recupere_donne() {
		int count = 0;
		//attention je recupere les donnes dans le meme ordre que insee dans la DB. regarder creer table
		String sql = "SELECT * FROM parametres ;";

		try {
			ResultSet rs = Query.select(sql);
			//3. donnees   :colonnes 

			//ligne dossier clair
			rs.next();
			String clair = rs.getString(3);
			if(clair.length()>0) {
				count += 1;
			}


			//ligne dossier crypto
			rs.next();
			String crypto = rs.getString(3);
			if(crypto.length()>0) {
				count += 1;
			}

			//ligne imprimante
			rs.next();
			String imprimante = rs.getString(3);
			if(imprimante.length()>0) {
				count += 1;
			}


			//fermeture
			Query.close_connection();
			if(count <= 3) {
				Constants.PARAMETRES_RECUPERER_AVEC_SUCCES = true;
				Constants.DOC_CLAIR = clair;
				Constants.DOC_CRYTO = crypto;
				Constants.Default_Printer_Name = imprimante;
			}else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Un parametre manquant");
				alert.setHeaderText(null);
				alert.setContentText("Un parametre est manquant veuillez revoir le panneau parametre avant de reessayer.");
				alert.show();
			}

		} catch (SQLException e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void recupere_numero_depart() {
		//attention je recupere les donnes dans le meme ordre que insee dans la DB. regarder creer table
		String sql = "SELECT * FROM NumeroOrdreDepart ;";

		try {
			ResultSet rs = Query.select(sql);

			while (rs.next()) {
				Constants.hash_num_departs.put(rs.getString(2), rs.getInt(3));
				System.out.println("nom : "+ rs.getString(2) + "    /  num : "+rs.getString(3));
			}
			
			Query.close_connection();

		} catch (SQLException e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
