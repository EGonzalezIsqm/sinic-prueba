package co.gov.igac.snc.xtfajson.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MensajeKafkaDTO {
	
	private String key;
	private Object json;
}
