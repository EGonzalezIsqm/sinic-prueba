package co.gov.igac.snc.xtfajson.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import co.gov.igac.snc.xtfajson.dto.MensajeKafkaDTO;
import co.gov.igac.snc.xtfajson.service.IKafkaProducerService;
import co.gov.igac.snc.xtfajson.util.Propiedades;

@Service
public class KafkaProducerServiceImpl implements IKafkaProducerService{
	
	private final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private Propiedades propiedades;
	
	@Autowired
    private KafkaTemplate<String, MensajeKafkaDTO> kafkaTemplate;

	@Override
	public void enviarMensaje(MensajeKafkaDTO json) {	
		if(Boolean.valueOf(propiedades.getUsaKafka())) {
			log.info("EnviarMensaje: " + propiedades.getKafkaTopic() + " - " + json);
			kafkaTemplate.send(propiedades.getKafkaTopic(),json);	
		}
	}

	
}
