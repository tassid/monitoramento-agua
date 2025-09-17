package monitoramento.agua.demo.repositories;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import monitoramento.agua.demo.models.Property;

public interface PropertyRepository extends JpaRepository<Property, UUID> {

    Page<Property> findByPersonId(UUID personId, PageRequest pageable);
}
