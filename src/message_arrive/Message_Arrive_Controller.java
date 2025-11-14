package message_arrive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.StyleClassDecoration;
import org.controlsfx.control.textfield.TextFields;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;

import beans.Beans_Message_Arrive;
import database.Query;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import util.Constants;
import util.Constants.Type_Message;
import util.Methodes;

public class Message_Arrive_Controller implements Initializable{

	@FXML
	private TextField text_num_arrive;

	@FXML
	private TextField text_expediteur_diplomail;

	@FXML
	private TextField text_numero_ordre_selon_l_expediteur;

	@FXML
	private DatePicker date_picker;

	@FXML
	private TextField text_nom_fichier;

	@FXML
	private TextArea area_objet_message;

	@FXML
	private ChoiceBox<String> choice_mention;

	@FXML
	private Pane paneau_drag_and_drop;

	@FXML
	private ChoiceBox<String> choice_type_message;

	@FXML
	private ChoiceBox<String> choice_systeme_cryptographique;

	//private String mdp_crypto = ""; 
	//private String nom_fichier_apres_dechiffrement;

	private SimpleStringProperty expediteur_observable;
	private SimpleStringProperty num_ordre_observable;
	private SimpleStringProperty objet_observable;
	private SimpleStringProperty reseau_observable;
	private SimpleStringProperty mention_observable; 
	private SimpleStringProperty nom_fichier_observable; 

	private Type_Message type_message = Type_Message.Officiel;
	//Lecture pour garder tracabilité
	private int num_dernier_message_arrive;
	private int num_dernier_message_arrive_officiel;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		choice_mention.getItems().addAll("Claire" ,"Confidentiel" , "Secret" , "Tres Secret");
		choice_mention.setValue("Claire");

		choice_type_message.getItems().addAll("Officiel","TAC","Service","Divers");
		choice_type_message.setValue("Officiel");



		date_picker.setValue(LocalDate.now());
		choice_systeme_cryptographique.getItems().addAll("CryptoForge" , "SecureIT" , "PGP" , "HEC");
		choice_systeme_cryptographique.setValue("CryptoForge");

		TextFields.bindAutoCompletion(
				text_expediteur_diplomail,
				Constants.liste_ambassade);

		//binding choice_type_message
		choice_type_message.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				if(newValue.equalsIgnoreCase("Officiel")) {			
					type_message = Type_Message.Officiel;
					//on recupere le dernier numero a chache changement de type de message uniquement
					text_numero_ordre_selon_l_expediteur.setText( (num_dernier_message_arrive_officiel + 1) +"" );

				}else if(newValue.equalsIgnoreCase("Service")) {	 
					//si on a un Service alors on affiche juste le Message Service
					type_message = Type_Message.Service;
					text_numero_ordre_selon_l_expediteur.setText("SERVICE");

				}else if(newValue.equalsIgnoreCase("TAC")) {	 
					//si on a un Service alors on affiche juste le Message Service
					type_message = Type_Message.T_A_C;
					text_numero_ordre_selon_l_expediteur.setText("40000");

				}else{//Divers
					type_message = Type_Message.Divers;
					//Divers on laisse le champ libre
					text_numero_ordre_selon_l_expediteur.setText("70000");
				}
			}
		});

		//observable car sinon probleme lors de la lecture des donne pdf apres dechiffrement car concurentiel
		//si on met timer on se retrouve a modifier un ui textfield dans un autre thread.
		expediteur_observable = new SimpleStringProperty();
		expediteur_observable.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				text_expediteur_diplomail.setText(newValue);
			}
		});

		//numero ordre
		num_ordre_observable = new SimpleStringProperty();
		num_ordre_observable.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				text_numero_ordre_selon_l_expediteur.setText(newValue);
			}
		});

		//objet
		objet_observable = new SimpleStringProperty();
		objet_observable.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				area_objet_message.setText(newValue);
			}
		});

		//reseau
		reseau_observable = new SimpleStringProperty();

		//mention
		mention_observable = new SimpleStringProperty();
		mention_observable.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				choice_mention.setValue(newValue);
			}
		});

		//nom fichier
		nom_fichier_observable = new SimpleStringProperty();
		nom_fichier_observable.addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				text_nom_fichier.setText(newValue);
			}
		});

		//recuperation du nombre de message officiel par defaut 
		num_dernier_message_arrive = Integer.parseInt( Methodes.load("assets/MessageArrive.txt") );
		num_dernier_message_arrive_officiel = Integer.parseInt( Methodes.load("assets/MessageArriveOfficiel.txt") );

		//Diplomail Arrive
		text_num_arrive.setText( (num_dernier_message_arrive + 1 ) +"");
		text_numero_ordre_selon_l_expediteur.setText( (num_dernier_message_arrive_officiel +1) + "" );

	}


	@FXML
	private void on_valider_clicked() {

		String num_arrive = text_num_arrive.getText();
		String expediteur;
		expediteur = text_expediteur_diplomail.getText();
		LocalDate date = date_picker.getValue();
		String objet = area_objet_message.getText();
		String mention = choice_mention.getValue();
		String type_message = choice_type_message.getValue();
		String numero_d_ordre = text_numero_ordre_selon_l_expediteur.getText();
		String crypto_systeme = choice_systeme_cryptographique.getSelectionModel().getSelectedItem();
		String nom_fichier = text_nom_fichier.getText();

		System.out.println("avant validation");

		if(!validation()) {
			return;
		}

		System.out.println("apre valid validation");

		try {

			boolean b = false;

			Beans_Message_Arrive beans;
			//DANS LE CAS DE SERVICE ON MET 0 POUR REPRESER CAR SINON ON AURA UN STRING
			//le this est important car variables differents String et enum
			if(this.type_message == Type_Message.Service) {

				if (mention.equalsIgnoreCase("Claire")) {
					beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, objet , type_message, 0,crypto_systeme,nom_fichier);
					b = Query.insert(beans.formatToDatabase());
				} else if (mention.equalsIgnoreCase("Confidentiel")) {
					beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, "Conf" , type_message, 0,crypto_systeme,nom_fichier);
					b = Query.insert(beans.formatToDatabase());
					//insert conf
					beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, objet , type_message, 0,crypto_systeme,nom_fichier);
					b = Query.insert(beans.formatToDatabase_conf());
				} else if (mention.equalsIgnoreCase("Secret") || mention.equalsIgnoreCase("Tres Secret")) {
					beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, "Secret" , type_message, 0,crypto_systeme,nom_fichier);
					b = Query.insert(beans.formatToDatabase());
					//insert Secret
					beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, objet , type_message, 0,crypto_systeme,nom_fichier);
					b = Query.insert(beans.formatToDatabase_secret());
				}

			}else {



				beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, objet , type_message, Integer.valueOf(numero_d_ordre),crypto_systeme , nom_fichier);


				// ON VERIFIE SI LE NUMERO DEPART EXISTE DEJA. SI OUI ON UPDATE SI NON ON INSERT
				boolean verification_existance_num_arrive_officiel = beans.verifie_existance_num_arrive_officiel();
				boolean verification_existance_num_arrive_TAC = beans.verifie_existance_num_arrive_TAC();

				//################################   UPDATE ###########################//

				//on verifie si le num depart correspond existe deja
				if(verification_existance_num_arrive_officiel || verification_existance_num_arrive_TAC) {
					//si il existe alors on update sauf dans le cas service car num ordre = tjrs 0

					System.out.println("dans update message diplomail numero ordre existe deja");

					//on informe l utilisateur si num existe deja
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setHeaderText("Numero d'ordre existe deja.");
					alert.setContentText("Le numero d'ordre existe deja, le precedent ligne dans la base de donnee va etre mise a jour si vous appuyez sur OUI. Veuillez "
							+ "appuyer sur NON si vous voulez annuler l'operation.");
					Optional<ButtonType> result = alert.showAndWait();

					if (result.isPresent() && result.get() == ButtonType.OK) {
						//ON ELIMINE LES DIVERS DANS LE CAS UPDATE ARRIVE CAR ON PEUT AVOIR PLUSIEURS EXPEDITEUR
						//AVEC LE MEME NUMERO. ( ROME 70.001 ET MADRID 70.001)

						if (mention.equalsIgnoreCase("Claire")) {

							b = Query.insert(beans.format_Update_Database());
							if(type_message.equalsIgnoreCase("Officiel")) {
								//update dans base de donnee 50000
								Query.insert(beans.format_Update_Arrive_officiel_Database());

							}else if(type_message.equalsIgnoreCase("TAC")) {
								//insertion dans base de donne TAC
								Query.insert(beans.format_Update_Arrive_TAC_Database());
							}

						} else if (mention.equalsIgnoreCase("Confidentiel")) {

							beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, "Conf" , type_message, Integer.valueOf(numero_d_ordre),crypto_systeme,nom_fichier);
							b = Query.insert(beans.format_Update_Database());

							if(type_message.equalsIgnoreCase("Officiel")) {
								//update dans base de donnee 50000
								Query.insert(beans.format_Update_Arrive_officiel_Database());

							}else if(type_message.equalsIgnoreCase("TAC")) {
								//insertion dans base de donne TAC
								Query.insert(beans.format_Update_Arrive_TAC_Database());
							}
							//insert conf
							beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, objet, type_message, Integer.valueOf(numero_d_ordre),crypto_systeme,nom_fichier);
							b = Query.insert(beans.format_Update_Arrive_conf_Database());

						} else if (mention.equalsIgnoreCase("Secret") || mention.equalsIgnoreCase("Tres Secret")) {

							beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, "Secret" , type_message, Integer.valueOf(numero_d_ordre),crypto_systeme,nom_fichier);
							b = Query.insert(beans.format_Update_Database());

							if(type_message.equalsIgnoreCase("Officiel")) {
								//update dans base de donnee 50000
								Query.insert(beans.format_Update_Arrive_officiel_Database());

							}else if(type_message.equalsIgnoreCase("TAC")) {
								//insertion dans base de donne TAC
								Query.insert(beans.format_Update_Arrive_TAC_Database());
							}
							//insert conf
							beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, objet, type_message, Integer.valueOf(numero_d_ordre),crypto_systeme,nom_fichier);
							b = Query.insert(beans.format_Update_Arrive_secret_Database());

						}
					}
					else {
						System.out.println("dans update message  ANNULATION EFFECTUE");
						return;
					}

				}
				else			//################################   INSERT ###########################//
				{

					if (mention.equalsIgnoreCase("Claire")) {
						beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, objet , type_message, Integer.valueOf(numero_d_ordre),crypto_systeme,nom_fichier);
						b = Query.insert(beans.formatToDatabase());
						// Update des numeros depart (PAS DE 50.000) ex 45
						Methodes.save("assets/MessageArrive.txt" , num_arrive) ;
						num_dernier_message_arrive += 1;


						if(type_message.equalsIgnoreCase("Officiel")) {
							//insertion dans base de donnee 50000
							Query.insert(beans.formatToDatabase_officiel());
							Methodes.save("assets/MessageArriveOfficiel.txt", numero_d_ordre );
							num_dernier_message_arrive_officiel += 1;


						}else if(type_message.equalsIgnoreCase("Divers")) {
							//insertion dans base de donne 70000
							Query.insert(beans.formatToDatabase_divers());
						}else if(type_message.equalsIgnoreCase("TAC")) {
							//insertion dans base de donne TAC
							Query.insert(beans.formatToDatabase_TAC());
						}

					} else if (mention.equalsIgnoreCase("Confidentiel")) {

						beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, "Conf" , type_message, Integer.valueOf(numero_d_ordre),crypto_systeme,nom_fichier);
						b = Query.insert(beans.formatToDatabase());

						if(type_message.equalsIgnoreCase("Officiel")) {
							//insertion dans base de donnee 50000
							Query.insert(beans.formatToDatabase_officiel());

						}else if(type_message.equalsIgnoreCase("Divers")) {
							//insertion dans base de donne 70000
							Query.insert(beans.formatToDatabase_divers());
						}else if(type_message.equalsIgnoreCase("TAC")) {
							//insertion dans base de donne TAC
							Query.insert(beans.formatToDatabase_TAC());
						}

						//insert conf
						beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, objet , type_message, Integer.valueOf(numero_d_ordre),crypto_systeme,nom_fichier);
						b = Query.insert(beans.formatToDatabase_conf());

					} else if (mention.equalsIgnoreCase("Secret") || mention.equalsIgnoreCase("Tres Secret")) {

						beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, "Secret" , type_message, Integer.valueOf(numero_d_ordre),crypto_systeme,nom_fichier);
						b = Query.insert(beans.formatToDatabase());

						if(type_message.equalsIgnoreCase("Officiel")) {
							//insertion dans base de donnee 50000
							Query.insert(beans.formatToDatabase_officiel());

						}else if(type_message.equalsIgnoreCase("Divers")) {
							//insertion dans base de donne 70000
							Query.insert(beans.formatToDatabase_divers());
						}else if(type_message.equalsIgnoreCase("TAC")) {
							//insertion dans base de donne TAC
							Query.insert(beans.formatToDatabase_TAC());
						}

						//insert Secret
						beans = new Beans_Message_Arrive(Integer.valueOf(num_arrive), expediteur, mention, date, objet , type_message, Integer.valueOf(numero_d_ordre),crypto_systeme,nom_fichier);
						b = Query.insert(beans.formatToDatabase_secret());

					}


				}
			}

			if(b) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setHeaderText(null);
				alert.setContentText("Succes!!!!! Insertion dans la base de donnee.");
				alert.show();
			}else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Erreur insertion base de donne");
				alert.setContentText("Erreur lors de l insertion dans la base de donne. Veuillez verifier le code");
				alert.show();
				return;
			}



			//clear des differents champs
			area_objet_message.clear();
			int ajout = Integer.parseInt( text_num_arrive.getText()) + 1;
			text_num_arrive.setText( ajout +"" );
			text_nom_fichier.clear();
			//LE CAS TYPE SERVICE sinon erreur car num est String 'SERVICE'
			if( !type_message.equals("Service")) {
				text_numero_ordre_selon_l_expediteur.setText( (Integer.valueOf(numero_d_ordre) + 1) +"" );
			}


		} catch (SQLException e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreur lors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		} catch (Exception e) {
			showExceptionAlert("Erreur insertion base de donne", "Erreurlors de l insertion dans la base de donne. Veuillez verifier le code", e);
			e.printStackTrace();
		} 

	}

	@FXML
	private void open_file(){

		//recuperation du document a concatener
		FileChooser chooser = new FileChooser();

		chooser.getExtensionFilters().add(new ExtensionFilter("Fichiers PDF (*.pdf)", "*.pdf"));
		//	chooser.getExtensionFilters().add(new ExtensionFilter("Fichiers CFG (*.cfe)", "*.cfe"));
		//	chooser.getExtensionFilters().add(new ExtensionFilter("Fichiers SIT (*.sitw)", "*.sitw"));
		chooser.setTitle("Choisir fichier a enregistrer");
		File fichier_a_dechiffrer = chooser.showOpenDialog(text_num_arrive.getScene().getWindow());

		//validation
		if(!fichier_a_dechiffrer.exists()) {
			return;
		}

		System.out.println(fichier_a_dechiffrer.getAbsolutePath());
		//a la reception on regarde l extensio et on agi suivant le cas.
		//		String nom_fichier  = fichier_a_dechiffrer.getName();  

		//on regarde si tous les parametres ont deja ete set
		if(!Constants.PARAMETRES_RECUPERER_AVEC_SUCCES) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Un parametre manquant");
			alert.setHeaderText(null);
			alert.setContentText("Un parametre est manquant veuillez revoir le panneau parametre avant de reessayer.");
			alert.show();
			return;
		}

		/*		boolean dechiffrement_bool = false;
		// le cas cryptoforge
		if(nom_fichier.endsWith(".cfe")) {

			choice_systeme_cryptographique.setValue("CryptoForge");
			//on regarde le spinner et on adapte le mdp
			if(spinner.getValue() == 1) {
				mdp_crypto = Constants.CFG1;
			}else if(spinner.getValue() == 2) {
				mdp_crypto = Constants.CFG2;
			}else if(spinner.getValue() == 3) {
				mdp_crypto = Constants.CFG3;
			}else { //cad  value  == 0 cad on a pas choisi ne value de spinner
				alert_cryptosysteme();
				return;
			}

			//on verifie ke le mdp selectionne n est pas vide
			if(mdp_crypto.length() <= 0) {
				alert_cryptosysteme();
			}else {
				dechiffrement_bool = dechiffrer_avec_cryptoforge(mdp_crypto, fichier_a_dechiffrer.getAbsolutePath());
				//4 pour .cfe
				nom_fichier_apres_dechiffrement = fichier_a_dechiffrer.getName().substring(0, fichier_a_dechiffrer.getName().length() - 4);
				System.out.println(nom_fichier_apres_dechiffrement);
			}

			// le cas secure IT
		}else if(nom_fichier.endsWith(".sitw")) {

			choice_systeme_cryptographique.setValue("SecureIT");
			//on regarde le spinner et on adapte le mdp
			if(spinner.getValue() == 1) {
				mdp_crypto = Constants.SIT1;
			}else if(spinner.getValue() == 2) {
				mdp_crypto = Constants.SIT2;
			}else if(spinner.getValue() == 3) {
				mdp_crypto = Constants.SIT3;
			}else { //cad  value  == 0 cad on a pas choisi ne value de spinner
				alert_cryptosysteme();
			}

			//on verifie ke le mdp selectionne n est pas vide
			if(mdp_crypto.length() <= 0) {
				alert_cryptosysteme();
			}else {
				dechiffrement_bool = dechiffrer_avec_secureIT(mdp_crypto, fichier_a_dechiffrer.getAbsolutePath());
				//9 pour _pdf.sitw 
				nom_fichier_apres_dechiffrement = fichier_a_dechiffrer.getName().substring(0, fichier_a_dechiffrer.getName().length() - 9) + ".pdf";
				System.out.println(nom_fichier_apres_dechiffrement);
			}
			//si on a un pdf 
		}else if(nom_fichier.endsWith(".pdf")) {*/
		//dans ce cas pas besoin d enlever les derniers caracteres sur la fin.
		readPdfMetadata(fichier_a_dechiffrer);
		/*	}

		if (dechiffrement_bool) {
			boolean b = readPdfMetadata(new File(fichier_a_dechiffrer.getParent() +"\\"+ nom_fichier_apres_dechiffrement));
			if(b) {
				//on informe le user que le chiffrement c est bien passe
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Dechiffrement");
				alert.setHeaderText(null);
				alert.setContentText("Dechiffrement effectue avec succes.");
				alert.show();
			}
		}	*/
	}



	private boolean validation() {

		//on enleve tout les decorations avant de mettre le mauvais
		Decorator.removeAllDecorations(text_num_arrive);
		Decorator.removeAllDecorations(text_expediteur_diplomail);
		Decorator.removeAllDecorations(area_objet_message);
		Decorator.removeAllDecorations(text_numero_ordre_selon_l_expediteur);
		Decorator.removeAllDecorations(choice_systeme_cryptographique);
		Decorator.removeAllDecorations(text_nom_fichier);

		//numero de depart
		if (text_num_arrive.getText().length() <= 0) {
			Decorator.addDecoration(text_num_arrive, new StyleClassDecoration("warning"));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Champs Vide");
			alert.setHeaderText(null);
			alert.setContentText("Le champs Numero de d�part est vide.\n veuillez le remplir avant de reessayer.");
			alert.show();
			return false;
		}



		//numero d ordre
		//cas specifique des services
		if(type_message != Type_Message.Service) {
			if (text_numero_ordre_selon_l_expediteur.getText().length() <= 0) {
				Decorator.addDecoration(text_numero_ordre_selon_l_expediteur, new StyleClassDecoration("warning"));
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Champs Vide");
				alert.setHeaderText(null);
				alert.setContentText("Le champs Numero d ordret est vide.\n veuillez le remplir avant de reessayer.");
				alert.show();
				return false;
			}
		}


		//numero d ordre
		//cas erreur de frappe 500.000 en lieu et place de 50.000
		//verification si integer deja effectue
		if(type_message != Type_Message.Service) {
			if(Integer.valueOf(text_numero_ordre_selon_l_expediteur.getText())  > 100000 ) {
				Decorator.addDecoration(text_numero_ordre_selon_l_expediteur, new StyleClassDecoration("warning"));
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Format Incorrect");
				alert.setHeaderText(null);
				alert.setContentText("Le champs Numero d odre ne peut depasser 100.000.\n veuillez verifier si il n'y a pas d'erreur de frappe avant de reessayer.");
				alert.show();
				return false;
			}
		}



		//expediteur
		//on verifie si le champ expediteur n est pas vide
		if ( text_expediteur_diplomail.getText().length() <= 0) {
			Decorator.addDecoration(text_expediteur_diplomail, new StyleClassDecoration("warning"));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Erreur!!!! Ambassade");
			alert.setHeaderText(null);
			alert.setContentText("Le champs destinataire du message est vide.\\n veuillez le remplir avant de reessayer.!!");
			alert.show();
			return false;
		}

		//Nom du fichier 
		if (text_nom_fichier.getText().length() <= 0) {
			Decorator.addDecoration(area_objet_message, new StyleClassDecoration("warning"));
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Champs Vide");
			alert.setHeaderText(null);
			alert.setContentText("Le champs Nom du fichier est vide.\n veuillez le remplir avant de reessayer.");
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


		/*		//mdp cryptosysteme
		int spinner_int = spinner.getValue();
		if(systeme_crypto == Systeme_cryptographique.CryptoForge && spinner_int == 1) {
			if(Constants.CFG1.length()<=0) {
				alert_cryptosysteme();
				return false;
			}
			mdp_crypto = Constants.CFG1;

		}else if(systeme_crypto == Systeme_cryptographique.CryptoForge && spinner_int == 2) {
			if(Constants.CFG2.length()<=0) {
				alert_cryptosysteme();
				return false;
			}
			mdp_crypto = Constants.CFG2;

		}else if(systeme_crypto == Systeme_cryptographique.CryptoForge && spinner_int == 3) {
			if(Constants.CFG3.length()<=0) {
				alert_cryptosysteme();
				return false;
			}
			mdp_crypto = Constants.CFG3;

			//secure IT
		}else if(systeme_crypto == Systeme_cryptographique.SecureIT && spinner_int == 1) {
			if(Constants.SIT1.length()<=0) {
				alert_cryptosysteme();
				return false;
			}
			mdp_crypto = Constants.SIT1;

		}
		else if(systeme_crypto == Systeme_cryptographique.SecureIT && spinner_int == 2) {
			if(Constants.SIT2.length()<=0) {
				alert_cryptosysteme();
				return false;
			}
			mdp_crypto = Constants.SIT2;
		}
		else if(systeme_crypto == Systeme_cryptographique.SecureIT && spinner_int == 3) {
			if(Constants.SIT3.length()<=0) {
				alert_cryptosysteme();
				return false;
			}
			mdp_crypto = Constants.SIT3;
		}*/


		return true;
	}


	private boolean readPdfMetadata(File source) { 

		try {

			// Creating a PdfDocument       
			PdfDocument pdfDoc = new PdfDocument(new PdfReader(source));                           

			//metadata
			PdfDocumentInfo info =  pdfDoc.getDocumentInfo();

			//on regarde si fichiers deja enregistre avec logiciel horus
			String key = info.getMoreInfo("talla");
			System.out.println("key === "+key);
			if( key == null) {
				//si NO alors on return
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Aucune donne preenregistre");
				alert.setHeaderText(null);
				alert.setContentText("Aucune donne n a ete preenregistre sur ce pdf. veuillez le faire remarquer a votre expediteur.\n"
						+"Pour une prochaine utilisation");
				alert.show();
				pdfDoc.close();
				return true;//true car il n y a rien mais y a pas d erreur!!!!!
			}


			String expediteur = info.getMoreInfo("expediteur");
			String numero_ordre = info.getMoreInfo("num_depart");
			String objet  = info.getMoreInfo("objet");
			String reseau = info.getMoreInfo("reseau");
			String mention = info.getMoreInfo("mention");
			String nom_fichier = info.getMoreInfo("nom_fichier");


			System.out.println("Auteur : "+expediteur+ "  - num depart : "+numero_ordre+"  - object : "+objet
					+ "  - reseau : "+reseau+ "  - mention : "+mention +"    - nom fichier : "+nom_fichier);

			expediteur_observable.setValue(expediteur);
			reseau_observable.setValue(reseau);

			area_objet_message.setText(objet);

			mention_observable.setValue(mention);
			num_ordre_observable.setValue(numero_ordre);
			nom_fichier_observable.setValue(nom_fichier);
			pdfDoc.close();
			return true;

		} catch (FileNotFoundException e) {
			//	showExceptionAlert("Erreur fichier", "Erreur fichier non trouv�. Veuillez verifier le code", e);
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			//	showExceptionAlert("Erreur fichier", "Erreur acces au fichier. Veuillez verifier le code", e);
			e.printStackTrace();
			return false;
		}
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


	/*	private void alert_cryptosysteme() {
		Decorator.addDecoration(choice_systeme_cryptographique, new StyleClassDecoration("warning"));
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Format Incorrect");
		alert.setHeaderText(null);
		alert.setContentText("Le mots de passe specifie n est pas deja configur�.\n Ceci peut etre due a l oublie de la valeur choisi pour la cl�.\n veuillez corriger cela dans l onglet parametre avant de reessayer.");
		alert.show();
	}


	private boolean dechiffrer_avec_cryptoforge(String password , String path_file) {
		try {

			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c" , "cd \""+Constants.PATH_CRYPTOFORGE+"\" && CFFiles /d  \"/passw:"+password+"\" "+path_file); ///sa:Shred /passes:6 a ajouter pour supprimer fichier original
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
			return true;

		} catch (IOException e) {
			showExceptionAlert("Erreur fichier", "Erreur fichier lors du dechiffrement. Veuillez verifier le code", e);
			e.printStackTrace();
			return false;
		}
	}



	private boolean dechiffrer_avec_secureIT(String password , String path_file) {
		try {

			File f = new File(path_file);
			String directory = f.getParent();
			System.out.println("chemin secure it "+directory);

			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c" , "cd \""+Constants.PATH_SECUREIT+"\" && secureit /decrypt /pw:\""+password+"\" /overwrite /tofolder:\""+ directory +"\" "+path_file);
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
			return true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			showExceptionAlert("Erreur fichier", "Erreur fichier lors du d�chiffrement. Veuillez verifier le code", e);
			e.printStackTrace();
			return false;
		}
	}
	 */

}
