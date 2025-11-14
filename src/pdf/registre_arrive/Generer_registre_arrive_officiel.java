package pdf.registre_arrive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;

import beans.Beans_Message_Arrive;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import util.DateFormater;

public class Generer_registre_arrive_officiel extends Service<Boolean>{
		
	
		private List<Beans_Message_Arrive> list;
		private File file;
		

		public Generer_registre_arrive_officiel(List<Beans_Message_Arrive> list , File file) {
	
			this.list = list;
			this.file = file;
		}


		@Override
		protected Task<Boolean> createTask() {
			// TODO Auto-generated method stub
			return new Task<Boolean>() {

				@Override
				protected Boolean call() throws Exception {
					
					return create_file(file);
				}
				
			};
		}
		
		
		public boolean create_file(File f) {
		

			// Creating a PdfWriter             
			PdfWriter writer;
			try {
				
				if(f == null) {
					System.out.println("pas de fichier choisi");
					return false;
				}
				
				String path = f.getPath();
				
				if(!path.endsWith(".pdf")){
					path += ".pdf";
				}
				
				writer = new PdfWriter(path);
				String source = "assets/depart_rapport.pdf";
				PdfDocument pdfDoc = new PdfDocument(new PdfReader(source), writer);           

				// Creating a Document        
				Document document = new Document(pdfDoc);       
				
				//add model concerne
				Paragraph para_model = creat_paragraphe_model_concerne("MESSAGE ARRIVE 50.000");
				para_model.setMarginTop(20);
				document.add(para_model);
				
		
				// Adding Table to document        
				document.add(creat_table(list));

				// Closing the document    
				document.close();              
				return true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} 
		}

		private Table creat_table(List<Beans_Message_Arrive> list) {
			try {
				PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

				float [] pointColumnWidths = {70F,70F ,700F};
				Table table = new Table(pointColumnWidths);
				
				Cell cell_nbr_par_porte = new Cell().add(new Paragraph("Date").setFont(font).setFontSize(10));
				cell_nbr_par_porte.setTextAlignment(TextAlignment.CENTER);
				table.addHeaderCell(cell_nbr_par_porte);

				Cell cell_numero_ordre = new Cell().add(new Paragraph("N° Ordre").setFont(font).setFontSize(10));
				cell_numero_ordre.setTextAlignment(TextAlignment.CENTER);
				table.addHeaderCell(cell_numero_ordre);

				Cell cell_prix_unitaire = new Cell().add(new Paragraph("Objet").setFont(font).setFontSize(10));
				cell_prix_unitaire.setTextAlignment(TextAlignment.CENTER);
				table.addHeaderCell(cell_prix_unitaire);
				
				new_populate_table(table, list);

		
				table.setMarginTop(30);

				return table;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		private Paragraph creat_paragraphe_model_concerne(String str) {
			Paragraph par = new Paragraph();
			par.add(str);
			try {
				par.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD));
				par.setFontSize(14);
				par.setTextAlignment(TextAlignment.CENTER);
				return par;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		private void new_populate_table(Table table ,List<Beans_Message_Arrive> list) {
			
			for(int i=0; i< list.size(); i++) {
				
				//date
				Cell cell_date = new Cell().add(new Paragraph(DateFormater.DateToString_database(list.get(i).getDate())).setFontSize(8));
				cell_date.setTextAlignment(TextAlignment.CENTER);
				table.addCell(cell_date);
				
				//num ordre
				Cell num_ordre = new Cell().add(new Paragraph(list.get(i).getNumero_d_ordre_expediteur()+"").setFontSize(8));
				num_ordre.setTextAlignment(TextAlignment.CENTER);
				table.addCell(num_ordre);
				
				//objet
				Cell cell_objet = new Cell().add(new Paragraph(list.get(i).getObjet_message()).setFontSize(8));
				table.addCell(cell_objet);
			}
		}

}
