package co.gov.igac.snc.xtfajson.dto;

import java.util.List;

import lombok.Data;

@Data
public class MetadatoDTO {

	private String nombreArchivoXTF;
	private String fechaRecepcion;
	private String ruta;
	private String origen;
	private String municipio;
	private String usuario;
	private String estado;
	private List<ArchivosJsonDTO> archivosJson;
}
