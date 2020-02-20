package com.ipartek.formacion.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ipartek.formacion.model.pojo.Habilidad;

public class HabilidadDAO implements IDAO<Habilidad>{
	
private final static Logger LOG = LogManager.getLogger(HabilidadDAO.class);


private final static String SELECT_GET_ALL = "SELECT h.id 'id_haibilad', h.nombre 'nombre_habilidad' "
										+ "FROM habilidad h ORDER BY id_haibilad LIMIT 500;";
		
	
private static HabilidadDAO INSTANCE;
	
	private HabilidadDAO() {
		super();
	}
	
	public synchronized static HabilidadDAO getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new HabilidadDAO();
		}

		return INSTANCE;
	}

	@Override
	public List<Habilidad> getAll() {
		
		ArrayList<Habilidad> lista = new ArrayList<Habilidad>();

		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(SELECT_GET_ALL);
				ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {

				lista.add(mapper(rs));
			}

		} catch (SQLException e) {
			LOG.debug(e);
		}

		return lista;
	}//getAll

	@Override
	public Habilidad getById(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Habilidad delete(int id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Habilidad update(Habilidad pojo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Habilidad create(Habilidad pojo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private Habilidad mapper(ResultSet rs) throws SQLException {

		Habilidad h = new Habilidad();
		h.setId(rs.getInt("id_haibilad"));
		h.setNombre(rs.getString("nombre_habilidad"));

		return h;
	}//mapper

}
