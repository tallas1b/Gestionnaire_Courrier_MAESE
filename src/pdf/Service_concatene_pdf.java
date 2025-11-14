package pdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class Service_concatene_pdf extends Service<Integer>{
	
	private List<File> list_fichier_a_concatener;
	private String file_detination;
	

	public Service_concatene_pdf(List<File> list_fichier_a_concatener , String file_detination) {
		super();
		this.list_fichier_a_concatener = list_fichier_a_concatener;
		this.file_detination = file_detination;
	}

	@Override
	protected Task<Integer> createTask() {
		// TODO Auto-generated method stub
		return new Task<Integer>() {

			@Override
			protected Integer call() throws Exception {
				// TODO Auto-generated method stub
				return mergePDF();
			}

		};
	}

	private int mergePDF() {
		try {
			System.out.println("merge pdf");
			int nombre_de_page_fichier = 0;
			// Creating a PdfWriter   
			String pdf_path = file_detination; // "assets/combined_pdf_generate.pdf";
			PdfWriter writer;

			writer = new PdfWriter(pdf_path);

			String source = "assets/generate.pdf";

			// Creating a PdfDocument       
			PdfDocument pdfDoc = new PdfDocument(new PdfReader(source), writer);                           
			
			PdfMerger pdfcopy = new PdfMerger(pdfDoc);
			for (File file : list_fichier_a_concatener) {
				//set source PDF
				PdfDocument pdf_doc = new PdfDocument(new PdfReader(file));  
				//merge
				pdfcopy.merge(pdf_doc, 1,pdf_doc.getNumberOfPages()).setCloseSourceDocuments(true);
				pdf_doc.close();
			}
			
			nombre_de_page_fichier = pdfDoc.getNumberOfPages();
			Document doc = new Document(pdfDoc);
			//
			
			Paragraph par = new Paragraph();
			par.add(new Text("Nombre de pages :").setUnderline().setBold());
			par.setFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN));
			par.setFontSize(16);
			par.setFixedPosition(75,180, 200);
			
			Paragraph par2 = new Paragraph();
			par2.add(new Text("1 + "+(nombre_de_page_fichier - 1)+"  = "+nombre_de_page_fichier+"   (page de garde inclus)"));
			par2.setFont(PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN));
			par2.setFontSize(16);
			par2.setFixedPosition(200,180, 300);
			System.out.println("paragraphe");
			//
			doc.add(par);
			doc.add(par2);
			
			//fin ajout nombre de page dans page de garde
			
			
			
			System.out.println("merge doc add");
			pdfcopy.close();
			doc.close();
			System.out.println("doc close");
			pdfDoc.close();
			System.out.println("nombre de page "+nombre_de_page_fichier);
			return nombre_de_page_fichier;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

}
