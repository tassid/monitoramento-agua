package monitoramento.agua.demo.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import monitoramento.agua.demo.models.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, UUID> {

}
