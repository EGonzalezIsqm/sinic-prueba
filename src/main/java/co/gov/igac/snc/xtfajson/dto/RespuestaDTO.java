package co.gov.igac.snc.xtfajson.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RespuestaDTO {

	private String rutaOrigen;
	private List<String> outJson;
	private String origen;
	private String observacion;
}
