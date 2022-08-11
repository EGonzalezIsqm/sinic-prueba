package co.gov.igac.snc.xtfajson.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Configuration
@PropertySource("classpath:xtfToJson.properties")
@Getter
@NoArgsConstructor
@ToString
public class Propiedades {
	
	@Value("${uploadedFiles}")
	private String uploadedFiles;
	
	@Value("${iliDir}")
	private String iliDir;
	
	@Value("${ogrPath}")
	private String ogrPath;
	
	@Value("${workingDir}")
	private String workingDir;
	
	@Value("${tmpDirectoryPrefix}")
	private String tmpDirectoryPrefix;
	
	@Value("${uploads}")
	private String uploads;
	
	@Value("${rutaDescarga}")
	private String rutaDescarga;
	
	@Value("${utilsStorage.subirArchivo}")
	private String subirArchivo;

	@Value("${utilsStorage.existeArchivo}")
	private String existeArchivo;

	@Value("${utilsStorage.existeDirectorio}")
	private String existeDirectorio;

	@Value("${utilsStorage.crearDirectorioSiNoExiste}")
	private String crearDirectorioSiNoExiste;

	@Value("${utilsStorage.crearDirectorio}")
	private String crearDirectorio;

	@Value("${utilsStorage.descargarArchivo}")
	private String descargarArchivo;
	
	@Value("${kafka.bootstrap-servers}")
    private String kafkaServers;

	@Value("${kafka.topic}")
	private String kafkaTopic;

	@Value("${kafka.usaKafka}")
	private String usaKafka;


	@Value("${accountName}")
	private String accountName;

	@Value("${accountKey}")
	private String accountKey;

	@Value("${clientId}")
	private String clientId;

	@Value("${tenantId}")
	private String tenantId;

	@Value("${clientSecret}")
	private String clientSecret;

	@Value("${contenedor}")
	private String contenedor;

	@Value("${azure.storage.modelos}")
	private String azureModelos;

}
