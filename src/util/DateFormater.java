package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public  class DateFormater {

	public static String DateToString(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
		//convert String to LocalDate
		return date.format(formatter);
	}


	//*** plus de recherche necessaire pas prioritaire!!
	public static String DateToString_database(LocalDate date) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
		//convert String to LocalDate
		return date.format(formatter);
	}


	public static LocalDate StringToDate(String str) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
		//convert String to LocalDate
		return LocalDate.parse(str, formatter);
	}



}
