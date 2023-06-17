package it.franco.arduinomisurazioni.repositories;

import it.franco.arduinomisurazioni.entities.Misurazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
/*
L'interfaccia qui presente è il canale di comunicazione con il DB, che permette appunto di generare metodi
CRUD per la gestione delle entità all'interno del database. La notazione @Repository permette al db di capire
che è da qui che deve recuperare i dati dal db. L'interfaccia JpaRepository<T,Long>, permette di avere un'interfaccia
di base con alcuni metodi di default, come ad esempio i CRUD. Grazie alle JPA (Java Persistence API) è possibile
creare delle query, con le sole intestazioni dei metodi, e in questo caso sono riportate due instestazioni di metodi,
che rappresentanto due query;
*/

@Repository
public interface MisurazioneRepository extends JpaRepository<Misurazione,Long> {

    boolean existsByUmidita(int umidita);
    /*La query SQL che corrisponderà a questa intestazione sarà
    * SELECT *
    * FROM Misurazione m
    * WHERE m.umidita=${umidita}
    * NB= Chiaramente la notazione con il dollaro non esiste in SQL
    * ma serve solo a tenere presente che il valore che verrà messo
    * nella query è quello corrispondente al parametro "umidita", passato
    * al metodo.*/
    List<Misurazione> findByUmidita(int umidita);
    List<Misurazione> findByData(Date data);
    boolean existsByData(Date data);
}
