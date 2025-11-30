package monitoramento.agua.demo.core.rabbitMq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import monitoramento.agua.demo.dtos.SensorDataDTO;

@Component
public class RabbitMQConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @RabbitListener(queues = "warning_queue")
    public void receiveMessage(String messageJson) {
        try {
            SensorDataDTO data = objectMapper.readValue(messageJson, SensorDataDTO.class);
            System.out.println("Alerta de : " + data.type());
            System.out.println("Mensagem: " + data.message());
            System.out.println("Valor medido: " + data.value());
        } catch (JsonProcessingException e) {
            System.err.println("Erro ao processar mensagem: " + e.getMessage());
        }
    }

}
