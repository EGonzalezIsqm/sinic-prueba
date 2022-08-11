package co.gov.igac.snc.xtfajson.service.impl;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import co.gov.igac.snc.xtfajson.dto.MensajeKafkaDTO;

@SpringBootTest
class KafkaProducerServiceImplTest {

	@MockBean
	KafkaProducerServiceImpl kafkaProducerService;

	@Test
	@DisplayName("Test de producer kafka")
	void testEnviarMensaje() {
		MensajeKafkaDTO mensajeKafkaDTO = new MensajeKafkaDTO("OK", Map.of("json","pruebaJson"));

		doNothing().when(kafkaProducerService).enviarMensaje(mensajeKafkaDTO);
		
		kafkaProducerService.enviarMensaje(mensajeKafkaDTO);
		
		verify(kafkaProducerService).enviarMensaje(mensajeKafkaDTO);
	}

}
