package com.ipartek.formacion.utilidades;

import org.apache.logging.log4j.LogManager;


import org.apache.logging.log4j.Logger;

public class Utilidades {
	private static final Logger LOG = LogManager.getLogger(Utilidades.class);

	/**
	 * Obtenemos el id del pathInfo (URI)
	 * 
	 * @param pathInfo : es una parte de la URL donde debemos buscar un numero
	 * @return numero id
	 * @throws Exception ejem:
	 *                   <ol>
	 *                   <li>/ url valida</li>
	 *                   <li>/2 url valida</li>
	 *                   <li>/2/ url vallido</li>
	 *                   <li>/2/2 esta url esta mal formada</li>
	 *                   <li>/2/otraCosa/34 esta url esta mal formada</li>
	 *                   </ol>
	 */
	public static int obtenerId(String pathInfo) throws Exception {

		boolean urlbien = false;
		int id = 0;
		String[] arrayUrl = null;

		if (pathInfo != null) {

			arrayUrl = pathInfo.split("/");
			switch (arrayUrl.length) {
			case 0:
			case 1:// Tanto si es 0 como si es 1 entra.
				id = -1;
				break;
			case 2:
				urlbien = arrayUrl[1].matches("^\\d+$");
				if (urlbien) {
					id = Integer.parseInt(arrayUrl[1]);
				} else {
					throw new Exception("URL mal formada. No es un id numerico " + pathInfo);
				}
				break;
			default:
				throw new Exception("");
			}
		} else {
			id = -1;
		}

		return id;

	};

	/**
	 * Este metodo coge un frase y cuenta las palabras qeu hay en ella.
	 * 
	 * @param String 
	 * @return int numero de palabras.
	 */
	public static int contarPlabrasTest(String frase) {

		int result;

		if (frase == null) {
			result = 0;
		} else {
			frase = frase.replaceAll("[\\_\\-]", " ");
			result = frase.split("([[a-z][A-Z][0-9][\\Q-\\E]]+)", -1).length
					+ (frase.replaceAll("([[a-z][A-Z][0-9][\\W]]*)", "")).length() - 1;
		}
		return result;
	};

}
