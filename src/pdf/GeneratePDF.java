package pdf;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import util.Constants.Mention;

public class GeneratePDF extends Service<Boolean>{

	private String num_depart ;
	private String destinataire ;
	private LocalDate date ;
	private String objet ;
	private Mention mention;
	private File file_a_concatener;

	private String nom_fichier;
	private PdfFont font;
	
	public static final String CANDARA_BOLD =
		    "assets/Candara_Bold.ttf";


	public GeneratePDF(String num_depart, String destinataire, LocalDate date, String objet, Mention mention,
			File file_a_concatener, String nom_fichier) {
		super();
		this.num_depart = num_depart;
		this.destinataire = destinataire;
		this.date = date;
		this.objet = objet;
		this.mention = mention;
		this.file_a_concatener = file_a_concatener;
		this.nom_fichier = nom_fichier;
		
		 try {
			FontProgram fontProgram =
				        FontProgramFactory.createFont(CANDARA_BOLD);
			font = PdfFontFactory.createFont(fontProgram);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	protected Task<Boolean> createTask() {
		// TODO Auto-generated method stub
		return new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				// TODO Auto-generated method stub
				return creation_pdf_file();
			}

		};
	}



	private boolean creation_pdf_file() {

		try {

			// Creating a PdfWriter   
			String pdf_path = "assets/generate.pdf";
			PdfWriter writer = new PdfWriter(pdf_path);
			String source = "assets/Model.pdf";

			// Creating a PdfDocument    
			PdfReader reader = new PdfReader(source);
			reader.setUnethicalReading(true);
			PdfDocument pdfDoc = new PdfDocument(reader, writer); 
			
			PdfReader reader_concatener = new PdfReader(file_a_concatener);
			reader_concatener.setUnethicalReading(true);
			PdfDocument pdf_doc_a_concatener = new PdfDocument(reader_concatener);

			// Creating a Document        
			Document document = new Document(pdfDoc); 

			//metadata
			PdfDocumentInfo info =  pdfDoc.getDocumentInfo();

			Map<String , String> info_a_ajouter = new HashMap<>();
			info_a_ajouter.put("expediteur" , destinataire);
			info_a_ajouter.put("num_depart", num_depart);
			info_a_ajouter.put("objet",objet);
			info_a_ajouter.put("mention",mention.name());
			info_a_ajouter.put("nom_fichier", nom_fichier);
			info_a_ajouter.put("talla","yes");
			info.setMoreInfo(info_a_ajouter);


			//date
			String str = "Dakar , le " + date.getDayOfMonth()+" "+date.getMonth().getDisplayName(TextStyle.FULL,Locale.FRENCH)+" "+date.getYear();
			Paragraph paragra = creat_paragraphe_model_concerne(str);
			paragra.setFixedPosition(400, 750, 220);
			paragra.setTextAlignment(TextAlignment.LEFT);
			paragra.setFont(font);
			document.add(paragra); 

			//mention de classification
			if(mention == Mention.Claire) {
				document.add(mention( "Claire" , 50 , 650 , ColorConstants.BLACK) );
			}
			
			
			if(mention != Mention.Claire) {
				document.add(mention( this.mention.name() , 50 , 650 , ColorConstants.RED) );
			}

			if(mention == Mention.Tres_Secret) {
				//securite chiffre
				document.add(mention( "Securite Chiffre" , 50 , 600 , ColorConstants.RED ));
			}


			setupNumero(document);
			setupOrigine(document);

			//destinataire
			setupDestinataire(document);

			//objet
			setupObjet(document);

			int nombre_de_page_fichier = pdf_doc_a_concatener.getNumberOfPages();
			//nombre de page
			nombre_de_page(document,nombre_de_page_fichier);

			//merge
			PdfMerger pdfcopy = new PdfMerger(pdfDoc);
			pdfcopy.merge(pdf_doc_a_concatener, 1,pdf_doc_a_concatener.getNumberOfPages());
			//ajout pagination
			for (int i = 1; i <= (nombre_de_page_fichier + 1 ); i++) {

				// Write aligned text to the specified by parameters point
				document.showTextAligned(new Paragraph(String.format(" %s / %s", i, (nombre_de_page_fichier + 1 ))),
						550, 50, i, TextAlignment.RIGHT, VerticalAlignment.TOP, 0);
			}
			
			// Closing the document  
			pdfcopy.close();
			
			//On recuper la page de garde pour pouvoir l imprimer trop compliké a partie de Imprimante car imprime tout le document
/*			String pdf_path_pdg = "assets/pageDeGarde.pdf";
			PdfWriter writer_pdg = new PdfWriter(pdf_path_pdg);     
			PdfDocument pdfDoc_page_de_garde = new PdfDocument(writer_pdg); 
			PdfMerger pdfcopy_pdg = new PdfMerger(pdfDoc_page_de_garde);
			pdfcopy_pdg.merge(pdfDoc, 1,1);//une seul page 1 debut 1 fin
			pdfcopy_pdg.close();*/
			
			System.out.println("generation pdf copy");
			pdf_doc_a_concatener.close();
			System.out.println("generation pdf a concatener");
			//	document.close();
			System.out.println("generation pdf termine");
			return true;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		} 
	}


	private Paragraph creat_paragraphe_model_concerne(String str) {

		Paragraph par = new Paragraph();
		par.add(str);
		par.setFont(font);
		par.setFontSize(14);
		par.setTextAlignment(TextAlignment.CENTER);

		return par;
	}

	private Table mention(String libele , int positionLeft , int positionBotom , Color color) throws IOException {
		Table table = new Table(1);
		table.addCell(libele);	
		table.setFont(font);
		table.setFontSize(18);
		table.setFontColor(color);
		table.setTextAlignment(TextAlignment.CENTER);
		table.setBorder(new SolidBorder(color , 1.4f));
		table.setFixedPosition(positionLeft, positionBotom, 160);
		return table;
	}

	private void setupObjet(Document doc) {

		Paragraph par = new Paragraph();
		par.add(new Text("OBJET:").setUnderline());
		par.setFont(font);
		par.setFontSize(16);
		par.setFixedPosition(75,130, 70);
		doc.add(par);

		Paragraph par2 = new Paragraph();
		par2.add(objet);
		par2.setFont(font);
		par2.setFontSize(16);
		par2.setFixedPosition(150, 130-70+ 30, 400);//120comme l autre 70 comme heigth 14 et plus comme font size
		par2.setHeight(70);
		doc.add(par2);

	}


	private void setupOrigine(Document doc) {

		Paragraph par = new Paragraph();
		par.add(new Text("ORIGINE:").setUnderline());
		par.setFont(font);
		par.setFontSize(18);
		par.setFixedPosition(90, 435 , 200);
		doc.add( par );


		Paragraph par2 = new Paragraph();
		par2.add(new Text("MIAAESE"));
		par2.setFont(font);
		par2.setFontSize(20);
		par2.setFixedPosition(210, 435 , 200);
		doc.add( par2 );

	}


	private void setupNumero(Document doc) {

		Paragraph par = new Paragraph();
		par.add(new Text("NUMERO:").setUnderline());
		par.setFont(font);
		par.setFontSize(18);
		par.setFixedPosition(90, 360, 200);
		doc.add( par );


		Paragraph par2 = new Paragraph();
		StringBuilder str_build_num_ordre = new StringBuilder(num_depart);
		str_build_num_ordre.insert(2, '.');
		par2.add(str_build_num_ordre.toString());
		par2.setFont(font);
		par2.setFontSize(20);
		par2.setFixedPosition(230, 360 , 200);
		doc.add( par2 );

	}



	private void setupDestinataire(Document doc) {

		Paragraph par = new Paragraph();
		par.add(new Text("DESTINATAIRE:").setUnderline());
		par.setFont(font);
		par.setFontSize(18);
		par.setFixedPosition(90, 395  , 200);
		doc.add(par);


		//poste
		Paragraph par3 = new Paragraph();
		par3.add(destinataire);
		par3.setFont(font);
		par3.setFontSize(20);
		//par3.setHeight(70);
		par3.setFixedPosition(240, 395 , 300);
		doc.add(par3);

	}

	private void nombre_de_page(Document doc , int nombre) {
		Paragraph par = new Paragraph();
		par.add(new Text("Nombre de pages :").setUnderline());
		par.setFont(font);
		par.setFontSize(18);
		par.setFixedPosition(75,250, 200);

		Paragraph par2 = new Paragraph();
		par2.add(new Text("1 + "+ nombre +"  = "+ (nombre +1)));
		par2.setFont(font);
		par2.setFontSize(20);
		par2.setFixedPosition(240,250, 300);

		doc.add(par);
		doc.add(par2);
	}
}
