package monitoramento.agua.demo.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import monitoramento.agua.demo.models.Property;

public interface PropertyRepository extends JpaRepository<Property, UUID> {

}
