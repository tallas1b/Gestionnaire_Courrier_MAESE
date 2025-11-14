package print;

import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;

import javax.print.PrintService;
import javax.print.attribute.PrintRequestAttributeSet;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

public class Imprimer {


	/// tres important regadrer les import car java awt printerjob =/= javafx printerjob

	public boolean printPDF(PrintService printingDevice , PrintRequestAttributeSet attributeSet) {

		try {
			//une seul page a imprimer
			PDDocument document = PDDocument.load(new File("assets/generate.pdf"));

			PDFPageable pageable = new PDFPageable(document);
			PrinterJob job = PrinterJob.getPrinterJob();
			job.setPageable( pageable );

			job.setPrintService(printingDevice);
			job.print(attributeSet);
		} catch (PrinterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
		return true;
	}
	
}
