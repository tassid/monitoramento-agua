package monitoramento.agua.demo.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import monitoramento.agua.demo.models.Person;

public interface PersonRepository extends JpaRepository<Person, UUID> {

}
