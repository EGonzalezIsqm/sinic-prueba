package co.gov.igac.snc.xtfajson.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

public class ExcepcionesDeNegocio extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	@Getter
	private HttpStatus estado;
	@Getter
	private String titulo;

	public ExcepcionesDeNegocio(String mensaje, String titulo, HttpStatus estado) {
		super(mensaje);
		this.titulo = titulo;
		this.estado = estado;
	}

}
