package co.gov.igac.snc.xtfajson.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.gov.igac.snc.xtfajson.dto.PeticionDTO;
import co.gov.igac.snc.xtfajson.dto.RespuestaDTO;
import co.gov.igac.snc.xtfajson.exception.ExcepcionLecturaDeArchivo;
import co.gov.igac.snc.xtfajson.exception.ExcepcionPropertiesNoExiste;
import co.gov.igac.snc.xtfajson.exception.ExcepcionesDeNegocio;
import co.gov.igac.snc.xtfajson.service.IKafkaProducerService;
import co.gov.igac.snc.xtfajson.service.IxtfToJsonService;

@WebMvcTest(XtfToJsonRestController.class)
class XtfToJsonRestControllerTest {
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	IxtfToJsonService service;
	
	@MockBean
	IKafkaProducerService kafkaProducerService;
	
	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("Test de controller convertir xtf a json /convertir/xtfToJsonRdm")
	void testXtfToJsonRdm() throws JsonProcessingException, Exception {
		PeticionDTO peticion = new PeticionDTO();
		peticion.setNombreArchivo("SNR_5001_prueba.xtf");
		peticion.setOrigen("SNR");
		peticion.setRutaArchivo("procesoRDM/XTF/SNR");
		RespuestaDTO respuestaDTO = RespuestaDTO.builder()
				.rutaOrigen("D:\\descargaXFT\\SNR_5001_prueba.xtf")
				.outJson(List.of("procesoRDM/XTF/SNR/5001/5001_prueba.json"))
				.origen("SNR")
				.build();
		
		when(service.convertir(peticion)).thenReturn(ResponseEntity.ok(respuestaDTO));
		
		mvc.perform(post("/convertir/xtfToJsonRdm")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(peticion)))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(content().json(objectMapper.writeValueAsString(respuestaDTO)));
		
		verify(service).convertir(peticion);
	}

}
