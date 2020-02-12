package com.ipartek.formacion.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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
	
	private PokemonDAO pokemonDao;
	private String pathInfo;
	private ValidatorFactory factory;
	private Validator validator;
	Object responseBody = null;
	int statusCode = HttpServletResponse.SC_OK;
	String pathinfo;
	
	
	private static PokemonDAO dao;   
	  

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		dao = PokemonDAO.getInstance();
	}

	/**
	 * @see Servlet#destroy()
	 */
	public void destroy() {
		dao = null;
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");
		pathinfo = request.getPathInfo();
		
		super.service(request, response);
		
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
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
			int id = Integer.parseInt(pathinfo.split("/")[1]);
			responseBody = dao.getById(id);
			if ( responseBody == null ) {
				statusCode = HttpServletResponse.SC_NOT_FOUND;
			}
			
		}
		
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
			
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
	 */
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
