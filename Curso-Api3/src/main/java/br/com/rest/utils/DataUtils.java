package br.com.rest.utils;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataUtils {
	
	public static String getDataDiferencaDias(Integer qtdias) {
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, qtdias);
		return getDataFormatada(cal.getTime());
		
		//formatar em String 
		
	}
	
	public static String getDataFormatada(java.util.Date date) {
		
		DateFormat format = new SimpleDateFormat("dd/mm/yyyy");
		return format.format(date);
	}

}
