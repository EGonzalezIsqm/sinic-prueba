package co.gov.igac.snc.xtfajson.service;

import co.gov.igac.snc.xtfajson.dto.MensajeKafkaDTO;

public interface IKafkaProducerService {

	public void enviarMensaje(MensajeKafkaDTO json);
}
