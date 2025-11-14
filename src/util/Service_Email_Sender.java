package util;

import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class Service_Email_Sender extends Service<Boolean>{

	private String fromEmail; // on attends avant de supprimer!!!!
	private String password;
	private String toEmail; 
	private String objet; 
	private String corps_message;
	private String file_path;
	private String file_name;

	public Service_Email_Sender(String fromEmail, String password, String toEmail, String objet, String corps_message, String file_path , String file_name) {
		super();
		this.fromEmail = fromEmail;
		this.password = password;
		this.toEmail = toEmail;
		this.objet = objet;
		this.corps_message = corps_message;
		this.file_path = file_path;
		this.file_name = file_name;
	}


	@Override
	protected Task<Boolean> createTask() {
		// TODO Auto-generated method stub
		return new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {

				//on assaie de faire en sorte que le service essaice d anvoyer chaque minute
				do {
					boolean bool =  sendMessage();
					if(bool == true) {
						return true;
					}else {
						System.out.println("dans wait email");
						wait(20*1000);
						System.out.println("apres wait email");
					}


				}while(true);
			}

		};
	}


	private boolean sendMessage() {
		/**
		   Outgoing Mail (SMTP) Server
		   requires TLS or SSL: smtp.gmail.com (use authentication)
		   Use Authentication: Yes
		   Port for TLS/STARTTLS: 587
		 */

		//final String fromEmail = "myemailid@gmail.com"; //requires valid gmail id
		//	final String password = "mypassword"; // correct password for gmail id
		//	final String toEmail = "myemail@yahoo.com"; // can be any email id 

		System.out.println("TLSEmail Start");
		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.host", "smtp.office365.com");//"smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.pwd", "Tallalolo");//"cppudazthrarqngy");

		Session session = Session.getInstance(props, new Authenticator() {          
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail,password);   //"tallalos1b@gmail.com","cppudazthrarqngy");          
			}       
		});

		return sendEmail(session, toEmail,objet, corps_message);

	}

	/**
	 * Utility method to send simple HTML email
	 * @param session
	 * @param toEmail
	 * @param subject
	 * @param body
	 */
	private  boolean sendEmail(Session session, String toEmail, String subject, String body){
		try
		{
			MimeMessage msg = new MimeMessage(session);
			//set message headers
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");
			msg.setHeader("X-Unsent", "1");

			msg.setFrom(new InternetAddress(fromEmail,"Talla LO"));//"tallalos1b@gmail.com", "Talla LO"));

			msg.setReplyTo(InternetAddress.parse(fromEmail, false));

			msg.setSubject(subject, "UTF-8");

			msg.setText(body, "UTF-8");

			msg.setSentDate(new Date());
			msg.setFlag(Flag.DRAFT, true);

			//ajout d un fichier comme attachement
			// Create a multipar message
			Multipart multipart = new MimeMultipart();
			MimeBodyPart messageBodyPart = new MimeBodyPart();;
			DataSource source = new FileDataSource(file_path);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(file_name);
			multipart.addBodyPart(messageBodyPart);

			// Send the complete message parts
			msg.setContent(multipart);

			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
			System.out.println("Message is ready");
			Transport.send(msg);  

			System.out.println("EMail Sent Successfully!!");
			return true;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}



}
