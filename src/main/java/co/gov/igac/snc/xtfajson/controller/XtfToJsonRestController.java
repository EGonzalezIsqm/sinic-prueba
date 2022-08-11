package co.gov.igac.snc.xtfajson.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.gov.igac.snc.xtfajson.dto.PeticionDTO;
import co.gov.igac.snc.xtfajson.dto.RespuestaDTO;
import co.gov.igac.snc.xtfajson.exception.ExcepcionLecturaDeArchivo;
import co.gov.igac.snc.xtfajson.exception.ExcepcionPropertiesNoExiste;
import co.gov.igac.snc.xtfajson.exception.ExcepcionesDeNegocio;
import co.gov.igac.snc.xtfajson.service.IxtfToJsonService;

/**
 * 
 * @author jdrodriguezo
 * @version 1.0
 */
@RestController
@RequestMapping("/convertir")
public class XtfToJsonRestController {
	
	@Autowired
	IxtfToJsonService service;

	@PostMapping("/xtfToJsonRdm")
	public ResponseEntity<RespuestaDTO> xtfToJsonRdm(@RequestBody PeticionDTO peticion) 
			throws ExcepcionPropertiesNoExiste, ExcepcionLecturaDeArchivo, ExcepcionesDeNegocio {
		
		return service.convertir(peticion);
	}
}
