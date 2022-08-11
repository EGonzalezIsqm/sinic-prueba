package co.gov.igac.snc.xtfajson.exception;

import java.io.FileNotFoundException;

import org.springframework.http.HttpStatus;
import lombok.Getter;


public class ExcepcionPropertiesNoExiste extends FileNotFoundException {
	
	private static final long serialVersionUID = 1L;
	
	@Getter
	private HttpStatus estado;

	public ExcepcionPropertiesNoExiste() {
		super();
	}	

	public ExcepcionPropertiesNoExiste(String mensaje) {
		super(mensaje);
	}	

	public ExcepcionPropertiesNoExiste(String mensaje, HttpStatus estado) {
		super(mensaje);
		this.estado = estado;
	}	
	
}
