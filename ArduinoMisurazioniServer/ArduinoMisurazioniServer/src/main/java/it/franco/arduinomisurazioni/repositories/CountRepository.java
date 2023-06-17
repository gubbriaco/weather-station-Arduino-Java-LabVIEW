package it.franco.arduinomisurazioni.repositories;

import it.franco.arduinomisurazioni.entities.Count;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountRepository extends JpaRepository<Count,Long> {

}
