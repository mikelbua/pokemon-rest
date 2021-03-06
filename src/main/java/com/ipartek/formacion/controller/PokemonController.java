package com.ipartek.formacion.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.ipartek.formacion.model.PokemonDAO;
import com.ipartek.formacion.model.pojo.Pokemon;
import com.ipartek.formacion.utilidades.Utilidades;

/**
 * Servlet implementation class PokemonController
 */
@WebServlet("/api/pokemon/*")
public class PokemonController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger LOG = LogManager.getLogger(PokemonController.class);
	
	
	private  static Object responseBody = null;
	private  static int statusCode = HttpServletResponse.SC_OK;
	private  static String pathinfo;
	private  static int idPokemon;
	
	
	private static PokemonDAO dao;   
	  

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		dao = PokemonDAO.getInstance();
	}

	/**
	 * @see Servlet#destroy()
	 */
	@Override
	public void destroy() {
		dao = null;
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		
		//habilitar CORS
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		
		
		pathinfo = request.getPathInfo();
		
		try {
			
			idPokemon = Utilidades.obtenerId(pathinfo);
			
		} catch (Exception e) {
			LOG.debug(e);
		}
	//---------------------------------------------------------------------------------------------
		super.service(request, response);
	//---------------------------------------------------------------------------------------------
		
		response.setStatus(statusCode);
		
		try( PrintWriter out = response.getWriter() ){
			
			if ( responseBody != null ) {
				statusCode = HttpServletResponse.SC_OK;
				Gson json = new Gson();
				out.print( json.toJson(responseBody) );
				out.flush();
			}else {
				statusCode = HttpServletResponse.SC_NOT_FOUND;
				String error = "Error 404 los datos que solicita no existen en nuestra base de datos!";
				Gson json = new Gson();
				out.print( json.toJson(error) );
				out.flush();
			}
			
		}//try (PrintWriter out....)
		
		
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		String nombre = request.getParameter("nombre");
		
		LOG.debug("pathinfo= " + pathinfo );
		LOG.debug("Parametro nombre= " + nombre );
		
		
		
		if ( nombre != null ) {
			ArrayList<Pokemon> pokemons = (ArrayList<Pokemon>) dao.getByNombre(nombre);
			
			if ( pokemons.isEmpty() ) {
				statusCode = HttpServletResponse.SC_NO_CONTENT;
			}
			
			responseBody = pokemons;
			
		}else if ( pathinfo == null || "/".equals(pathinfo) ){
			responseBody = (ArrayList<Pokemon>) dao.getAll();
			
		}else {
			try {
				int id = Integer.parseInt(pathinfo.split("/")[1]);
				responseBody = dao.getById(id);
			} catch (Exception e) {
				LOG.debug(e);
			}
			
			
			if ( responseBody == null ) {
				statusCode = HttpServletResponse.SC_NOT_FOUND;
			}
			
		}
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			//Convertir json del request body a Objeto
			BufferedReader reader = request.getReader();
			Gson gson = new Gson();
			Pokemon poNuevo = gson.fromJson(reader, Pokemon.class);
			LOG.debug(" Json convertido a Objeto: " + poNuevo);
			
			if (poNuevo == null) {
				statusCode = HttpServletResponse.SC_NO_CONTENT;
				
			} else if(poNuevo.getId() == 0) {
				
						try {
							dao.create(poNuevo);
						} catch (Exception e) {
							LOG.debug(e);
						}
						statusCode = HttpServletResponse.SC_CREATED;
						// response body
						responseBody = poNuevo;
			}
		} catch (Exception e) {
			LOG.debug(e);
		}
		
		
		
		

	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 * 
	 */
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		try {
			// convertir json del request body a Objeto
			BufferedReader reader = request.getReader();
			Gson gson = new Gson();
			Pokemon poNuevo = gson.fromJson(reader, Pokemon.class);
			LOG.debug(" Json convertido a Objeto: " + poNuevo);

			try {
				dao.update(poNuevo);
			} catch (Exception e) {
				LOG.debug(e);
			}
			

			if (poNuevo == null) {
				statusCode = HttpServletResponse.SC_NO_CONTENT;
			} else {
				statusCode = HttpServletResponse.SC_OK;
				// response body
				responseBody = poNuevo;
			}
		} catch (Exception e) {
			LOG.debug(e);
		}
		
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Pokemon po = null;
			try {
				if (idPokemon != -1) {
					po = dao.delete(idPokemon);
				}
			} catch (Exception e) {
				LOG.error(e);
			}

			if (po == null) {
				statusCode =  HttpServletResponse.SC_NO_CONTENT;
			} else {
				statusCode = HttpServletResponse.SC_OK;
				// response body
				responseBody = po;

			}

		
			
			
	}

}
