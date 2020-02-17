package com.ipartek.formacion.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ipartek.formacion.model.pojo.Habilidad;
import com.ipartek.formacion.model.pojo.Pokemon;

public class PokemonDAO implements IDAO<Pokemon>{
	
	private final static Logger LOG = LogManager.getLogger(PokemonDAO.class);
	//sentencias SQL
	private final static String SELECT_GET_ALL =  		"SELECT p.id AS id_pokemon ,p.nombre as nombre_pokemon , h.id AS id_habilidad, h.nombre AS nombre_habilidad "
														+ "FROM 	pokemon p,pokemon_has_habilidad ph,	habilidad h "
														+ "WHERE ph.id_pokemon = p.id AND ph.id_habilidad = h.id;";
	
	private final static String SELECT_GET_BY_ID =	 	"SELECT p.id 'id_pokemon', p.nombre 'nombre_pokemon', h.nombre 'nombre_habilidad' "
														+ "FROM (pokemon p LEFT JOIN  pokemon_has_habilidad ph ON p.id = ph.id_pokemon) LEFT JOIN habilidad h ON ph.id_habilidad = h.id "
														+ "WHERE p.id = ? ORDER BY p.id DESC LIMIT 500;";
	
	private final static String SELECT_GET_BY_NOMBRE =  "SELECT  p.id AS id_pokemon , p.nombre as nombre_pokemon ,h.id AS id_habilidad,h.nombre AS nombre_habilidad \r\n" + 
														"FROM 	pokemon p,	pokemon_has_habilidad ph,habilidad h \r\n" + 
														"WHERE ph.id_pokemon = p.id AND ph.id_habilidad = h.id AND p.nombre LIKE ? ;";
	
	private final static String SQL_UPDATE =  "UPDATE pokemon p SET nombre= ?  WHERE  id= ?;";
	
	private final static String SQL_DELETE =  "DELETE FROM pokemon WHERE id=?;";
	
	
	private static PokemonDAO INSTANCE;
	
	private PokemonDAO() {
		super();
	}
	
	public synchronized static PokemonDAO getInstance() {

		if (INSTANCE == null) {
			INSTANCE = new PokemonDAO();
		}

		return INSTANCE;
	}
	
	@Override
	public List<Pokemon> getAll()
	{
		//Queremos saber CUANTOS POKEMON tenemos y las HABILIDADES DE CADA UNO.
		//Creamos un hasMap<Integer(key),Pokemon(value)> donde Integer sera id_pokemon (key)
		// y Pokemon el correspondiente Pokemon (value). Hacemos esto por que la SQL devolvera 
		// mas de un registro por cada pokemon ya que tienen mas de una habilidad.
		HashMap<Integer, Pokemon> hm = new HashMap<Integer, Pokemon>();

		try (Connection con = ConnectionManager.getConnection();
				PreparedStatement pst = con.prepareStatement(SELECT_GET_ALL);
				ResultSet rs = pst.executeQuery() ) {
			
			while( rs.next() ) {

				int idPokemon = rs.getInt("id_pokemon");
				
				//buscamos con el id si el pokemon existe dentro del hasMap<>
				Pokemon p = hm.get(idPokemon);
				
				//si p == null quiere decir que no existe dentro del hasMap<> asi que lo crearemos nosotros.
				if ( p == null ) {
					p = new Pokemon();
					p.setId(idPokemon);
					p.setNombre(rs.getString("nombre_pokemon"));
				}
				
				//creamos un objeto de la clase Habilidad que utilizaremos para meter las habilidades.
				Habilidad h = new Habilidad();
				h.setId(rs.getInt("id_habilidad"));
				h.setNombre(rs.getString("nombre_habilidad"));
				
				//Metemos la habilidad en el objeto clase Pokemon
				p.getHabilidades().add(h);
				//Metemos en la posicion idPokemon (key) el pokemon p (value) 
				hm.put(idPokemon, p);
			}

		} catch (Exception e) {
			LOG.debug(e);
		}

		return new ArrayList<Pokemon>(hm.values());
	}

	@Override
	public Pokemon getById(int id) {
		String sql = SELECT_GET_BY_ID;

		LOG.debug(sql);

		HashMap<Integer, Pokemon> hm = new HashMap<Integer, Pokemon>();
		Pokemon p = null;

		try (Connection con = ConnectionManager.getConnection(); 
				PreparedStatement pst = con.prepareStatement(sql);) {

			pst.setInt(1, id);

			try (ResultSet rs = pst.executeQuery()) {

				while (rs.next()) {
					p = mapper(hm, rs);
				}
			}

		} catch (Exception e) {
			LOG.debug(e);
		}

		return p;
	}
	
	
	
	public List<Pokemon> getByNombre( String nombre) {
		
		String sql = SELECT_GET_BY_NOMBRE;

		LOG.debug(sql);

		HashMap<Integer, Pokemon> hm = new HashMap<Integer, Pokemon>();
		

		try (Connection con = ConnectionManager.getConnection(); PreparedStatement pst = con.prepareStatement(sql);) {
			
			//para buscar en la SELECT con la clausula LIKE le concatenamos el "nombre" entre %
			pst.setString(1, "%" + nombre + "%");

			try (ResultSet rs = pst.executeQuery()) {
				while (rs.next()) {
					mapper(hm, rs);					
				}
			}

		} catch (Exception e) {
			LOG.debug(e);
		}

		return new ArrayList<Pokemon>(hm.values());
	}
		

	@Override
	public Pokemon delete(int id) throws Exception {//SQL_DELETE
		
		Pokemon registro = null;
		try (Connection con = ConnectionManager.getConnection();
				CallableStatement pst = con.prepareCall(SQL_DELETE)) {

			pst.setInt(1, id);

			LOG.debug(pst);

			registro = this.getById(id); // recuperar

			int affectedRows = pst.executeUpdate(); // eliminar
			if (affectedRows != 1) {
				registro = null;
				throw new Exception("No se puede eliminar " + registro);
			}

		}
		return registro;
	}

	@Override
	public Pokemon update(Pokemon pojo) throws Exception {

			try (Connection con = ConnectionManager.getConnection();
					CallableStatement pst = con.prepareCall(SQL_UPDATE)) {

				pst.setString(1, pojo.getNombre());
				pst.setInt(2, pojo.getId());

				LOG.debug(pst);

				int affectedRows = pst.executeUpdate(); // lanza una excepcion si nombre repetido
				if (affectedRows == 1) {
					LOG.debug("Pokeomn actualizado correctamente");
				} else {
					throw new Exception("No se encontro registro para id=" + pojo.getId());
				}

			}
			return pojo;
		}

	@Override
	public Pokemon create(Pokemon pojo) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
private Pokemon mapper(HashMap<Integer, Pokemon> hm, ResultSet rs) throws Exception {
		
		int idPokemon = rs.getInt("id_pokemon");
		
		//buscamos con el id si el pokemon existe dentro del hasMap<>
		Pokemon p = hm.get(idPokemon);
		
		//si p == null quiere decir que no existe dentro del hasMap<> asi que lo crearemos nosotros.
		if ( p == null ) {
			p = new Pokemon();
			p.setId(idPokemon);
			p.setNombre(rs.getString("nombre_pokemon"));
		}
		
		//creamos un objeto de la clase Habilidad que utilizaremos para meter las habilidades.
		Habilidad h = new Habilidad();
		h.setId(rs.getInt("id_habilidad"));
		h.setNombre(rs.getString("nombre_habilidad"));
		
		//Metemos la habilidad en el objeto clase Pokemon
		p.getHabilidades().add(h);
		//Metemos en la posicion idPokemon (key) el pokemon p (value) 
		hm.put(idPokemon, p);	
		
		return p;
	}

}//class