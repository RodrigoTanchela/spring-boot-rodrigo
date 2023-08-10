package br.com.rodrigo.utils;

public class MediaType {
	public static final String APPLICATION_JSON = "application/json";
	public static final String APPLICATION_XML = "application/xml";
	public static final String APPLICATION_YML = "application/x-yaml";
	
	/**
	 * Retorna os valores das constantes
	 * 
	 * @return []
	 */
	public static String[] getListaApllication() {
		String midiasDisponiveis[] = {APPLICATION_JSON, APPLICATION_XML, APPLICATION_YML};
		return midiasDisponiveis;	
	}
	
}
