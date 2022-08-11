package co.gov.igac.snc.xtfajson.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import co.gov.igac.snc.xtfajson.dto.EstandarDeExcepcionesDTO;

/**
 * 
 * @author jdrodriguezo
 * @version 1.0
 */
@RestControllerAdvice
public class AplicacionEstandarDeExcepciones {
	
	private final Log log = LogFactory.getLog(getClass());

	@ExceptionHandler(Exception.class)
	public ResponseEntity<EstandarDeExcepcionesDTO> handleNoContentException(Exception ex){
		log.error(ex);
		EstandarDeExcepcionesDTO respuesta = new EstandarDeExcepcionesDTO.ExceptionBuilder()
				.tipo("Exception")
				.titulo("Error interno")
				.codigo("E100")
				.estado(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR))
				.detalle(ex.getMessage())
				.instancia("/convertir/xtfToJsonRdm")
				.builder();
		return new ResponseEntity<EstandarDeExcepcionesDTO>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(ExcepcionPropertiesNoExiste.class)
	public ResponseEntity<EstandarDeExcepcionesDTO> handleNoContentException(ExcepcionPropertiesNoExiste ex){
		log.error(ex);
		HttpStatus estado = ex.getEstado() == null ? HttpStatus.NOT_IMPLEMENTED : ex.getEstado();
		EstandarDeExcepcionesDTO respuesta = new EstandarDeExcepcionesDTO.ExceptionBuilder()
				.tipo("FileNotFoundException")
				.titulo("Error de archivo no encontrado")
				.codigo("E100")
				.estado(String.valueOf(estado.value()))
				.detalle(ex.getMessage())
				.instancia("/convertir/xtfToJsonRdm")
				.builder();
		return new ResponseEntity<EstandarDeExcepcionesDTO>(respuesta, estado);
	}
	
	@ExceptionHandler(ExcepcionLecturaDeArchivo.class)
	public ResponseEntity<EstandarDeExcepcionesDTO> handleNoContentException(ExcepcionLecturaDeArchivo ex){
		log.error(ex);
		HttpStatus estado = ex.getEstado() == null ? HttpStatus.NOT_IMPLEMENTED : ex.getEstado();
		EstandarDeExcepcionesDTO respuesta = new EstandarDeExcepcionesDTO.ExceptionBuilder()
				.tipo("IOException")
				.titulo("Error de lectura de archivo")
				.codigo("E200")
				.estado(String.valueOf(estado.value()))
				.detalle(ex.getMessage())
				.instancia("/convertir/xtfToJsonRdm")
				.builder();
		return new ResponseEntity<EstandarDeExcepcionesDTO>(respuesta, estado);
	}
	

	
	@ExceptionHandler(ExcepcionesDeNegocio.class)
	public ResponseEntity<EstandarDeExcepcionesDTO> handleNoContentException(ExcepcionesDeNegocio ex){
		log.error(ex);
		HttpStatus estado = ex.getEstado() == null ? HttpStatus.NOT_IMPLEMENTED : ex.getEstado();
		EstandarDeExcepcionesDTO respuesta = new EstandarDeExcepcionesDTO.ExceptionBuilder()
				.tipo("ExcepcionesDeNegocio")
				.titulo(ex.getTitulo())
				.codigo("E300")
				.estado(String.valueOf(estado.value()))
				.detalle(ex.getMessage())
				.instancia("/convertir/xtfToJsonRdm")
				.builder();
		return new ResponseEntity<EstandarDeExcepcionesDTO>(respuesta, estado);
	}
}
