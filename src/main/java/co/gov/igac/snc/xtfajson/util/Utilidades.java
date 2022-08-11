package co.gov.igac.snc.xtfajson.util;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import co.gov.igac.snc.xtfajson.exception.ExcepcionesDeNegocio;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.core.publisher.Mono;
import reactor.netty.tcp.TcpClient;

@Component
public class Utilidades {

	public ResponseEntity<String> consumirApi(Map<String, String> peticion, String urlApi)
			throws ExcepcionesDeNegocio{
		TcpClient tcpClient = TcpClient
	            .create()
	            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
	            .doOnConnected(connection -> {
	                connection.addHandlerLast(new ReadTimeoutHandler(10000, TimeUnit.MILLISECONDS));
	                connection.addHandlerLast(new WriteTimeoutHandler(10000, TimeUnit.MILLISECONDS));
	            });
		WebClient webClient = WebClient.builder()
				//.clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
		
		ResponseEntity<String> respuesta = webClient.post()
		        .uri(urlApi)
		        .body(Mono.just(peticion),Map.class)
		        .retrieve()
		        .onStatus(HttpStatus::is4xxClientError, 
		        		clientResponse ->  clientResponse.bodyToMono(String.class)
		        		.map(body -> new ExcepcionesDeNegocio(body,"Error al consumir servicio " + urlApi, HttpStatus.CONFLICT)))
		        .onStatus(HttpStatus::is5xxServerError, 
		        		clientResponse ->  clientResponse.bodyToMono(String.class)
		        		.map(body -> new ExcepcionesDeNegocio(body,"Error al consumir servicio " + urlApi, HttpStatus.CONFLICT)))
		        .toEntity(String.class)
		        .block();
		
		return respuesta;
	}
	
}
