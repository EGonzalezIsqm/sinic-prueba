package co.gov.igac.snc.xtfajson.service;


import org.springframework.http.ResponseEntity;

import co.gov.igac.snc.xtfajson.dto.PeticionDTO;
import co.gov.igac.snc.xtfajson.dto.RespuestaDTO;
import co.gov.igac.snc.xtfajson.exception.ExcepcionLecturaDeArchivo;
import co.gov.igac.snc.xtfajson.exception.ExcepcionPropertiesNoExiste;
import co.gov.igac.snc.xtfajson.exception.ExcepcionesDeNegocio;

public interface IxtfToJsonService {

	public ResponseEntity<RespuestaDTO> convertir(PeticionDTO peticion) 
			throws ExcepcionPropertiesNoExiste, ExcepcionLecturaDeArchivo, ExcepcionesDeNegocio;
}
