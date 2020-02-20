package com.ipartek.formacion.controller;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.ipartek.formacion.model.HabilidadDAO;
import com.ipartek.formacion.model.pojo.Habilidad;

/**
 * Servlet implementation class HabilidadesController
 */
@WebServlet("/api/habilidad/*")
public class HabilidadesController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final static Logger LOG = LogManager.getLogger(HabilidadesController.class);
       
	
	
	Object responseBody = null;
	int statusCode = HttpServletResponse.SC_OK;
	String pathinfo;
	
	
	private static HabilidadDAO dao;   

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		dao = HabilidadDAO.getInstance();
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
		
		//habilitar CORS
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		
		
		
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
			
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		
		LOG.debug("pathinfo= " + pathinfo );
		
		if ( pathinfo == null || "/".equals(pathinfo) )
			{
				responseBody = (ArrayList<Habilidad>)dao.getAll();
			}
		else {
				statusCode = HttpServletResponse.SC_NOT_FOUND;
			}
	}//doGet
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
