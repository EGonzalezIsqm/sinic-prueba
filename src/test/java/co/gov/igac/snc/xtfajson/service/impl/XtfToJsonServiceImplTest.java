package co.gov.igac.snc.xtfajson.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.ResponseEntity;

import co.gov.igac.snc.xtfajson.dto.PeticionDTO;
import co.gov.igac.snc.xtfajson.dto.RespuestaDTO;
import co.gov.igac.snc.xtfajson.exception.ExcepcionLecturaDeArchivo;
import co.gov.igac.snc.xtfajson.exception.ExcepcionPropertiesNoExiste;
import co.gov.igac.snc.xtfajson.exception.ExcepcionesDeNegocio;
import co.gov.igac.snc.xtfajson.service.IKafkaProducerService;
import co.gov.igac.snc.xtfajson.util.Propiedades;
import co.gov.igac.snc.xtfajson.util.Utilidades;

@SpringBootTest
class XtfToJsonServiceImplTest {
	
	@MockBean
	XtfToJsonServiceImpl xtfToJsonService;
	

	@Test
	@DisplayName("Test de convertir xtf a json")
	void testConvertir() throws ExcepcionPropertiesNoExiste, ExcepcionLecturaDeArchivo, ExcepcionesDeNegocio {

		String rutaStorage = "procesoRDM/XTF/SNR";
		String nombreArchivo = "SNR_05001_prueba.xtf";
		String origen = "SNR";
		String urlApi = "http://localhost:18084/azureStorage/descargarArchivo";
		String rutaDescarga = "D:\\descargaXTF\\SNR_05001_prueba.xtf";
		List<String> outJson = List.of("D:\\descargarXTF\\SNR_05001_prueba_SNR_FuenteCabidaLinderos.json",
			    "D:\\descargarXTF\\SNR_05001_prueba_SNR_FuenteDerecho.json",
			    "D:\\descargarXTF\\SNR_05001_prueba_SNR_Titular.json",
			    "D:\\descargarXTF\\SNR_05001_prueba_SNR_PredioRegistro.json",
			    "D:\\descargarXTF\\SNR_05001_prueba_snr_titular_derecho.json",
			    "D:\\descargarXTF\\SNR_05001_prueba_SNR_Derecho.json");
		PeticionDTO peticion = new PeticionDTO();
		peticion.setNombreArchivo(nombreArchivo);
		peticion.setRutaArchivo(rutaStorage);
		peticion.setOrigen(origen);
		RespuestaDTO respuestaDTO = RespuestaDTO.builder()
				.rutaOrigen(rutaDescarga)
				.outJson(outJson)
				.origen(origen)
				.build();
		
		when(xtfToJsonService.convertir(peticion)).thenReturn(ResponseEntity.ok(respuestaDTO));
		
		assertEquals(xtfToJsonService.convertir(peticion), ResponseEntity.ok(respuestaDTO));
		
		verify(xtfToJsonService).convertir(peticion);
		
		
	}

}
