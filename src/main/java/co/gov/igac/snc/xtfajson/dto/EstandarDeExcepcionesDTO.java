package co.gov.igac.snc.xtfajson.dto;

import lombok.Getter;

/**
 * 
 * @author jdrodriguezo
 * @version 1.0
 */
@Getter
public class EstandarDeExcepcionesDTO {

	private String tipo;
	private String titulo;
	private String codigo;
	private String estado;
	private String detalle;
	private String instancia;
	
	private EstandarDeExcepcionesDTO(ExceptionBuilder builder) {
		this.tipo = builder.tipo;
		this.titulo = builder.titulo;
		this.codigo = builder.codigo;
		this.estado = builder.estado;
		this.detalle = builder.detalle;
		this.instancia = builder.instancia; 
	}
	
	
	public static class ExceptionBuilder{
		private String tipo;
		private String titulo;
		private String codigo;
		private String estado;
		private String detalle;
		private String instancia;
		
		public ExceptionBuilder() {
			
		}
		
		public ExceptionBuilder tipo(String tipo) {
			this.tipo = tipo;
			return this;
		}
		
		public ExceptionBuilder titulo(String titulo) {
			this.titulo = titulo;
			return this;
		}
		
		public ExceptionBuilder codigo(String codigo) {
			this.codigo = codigo;
			return this;
		}
		
		public ExceptionBuilder estado(String estado) {
			this.estado = estado;
			return this;
		}
		
		public ExceptionBuilder detalle(String detalle) {
			this.detalle = detalle;
			return this;
		}
		
		public ExceptionBuilder instancia(String instancia) {
			this.instancia = instancia;
			return this;
		}
		
		public EstandarDeExcepcionesDTO builder() {
			return new EstandarDeExcepcionesDTO(this);
		}
	}
}
