package co.gov.igac.snc.xtfajson.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import lombok.Getter;


public class ExcepcionLecturaDeArchivo extends IOException{
	
	private static final long serialVersionUID = 1L;
	
	@Getter
	private HttpStatus estado;
	
	public ExcepcionLecturaDeArchivo() {
		super();
	}
	
	public ExcepcionLecturaDeArchivo(String mensaje) {
		super(mensaje);
	}
	
	public ExcepcionLecturaDeArchivo(String mensaje, HttpStatus estado) {
		super(mensaje);
		this.estado = estado;
	}

}
