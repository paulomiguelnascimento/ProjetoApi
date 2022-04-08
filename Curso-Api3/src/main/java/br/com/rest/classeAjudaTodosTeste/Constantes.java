package br.com.rest.classeAjudaTodosTeste;

import io.restassured.http.ContentType;

public interface Constantes {
	
	String APP_BASE_URL = "https://barrigarest.wcaquino.me";
	Integer APP_PORT = 443; //http -> 80, https -> 443
	String APP_Base_Path = "";
	
	ContentType APP_CONTENT_TYPE = ContentType.JSON;
	
	Long Max_Timeout = 5000L;
	

}
