package it.franco.arduinomisurazioni.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Version;

import javax.persistence.*;
import java.util.Date;

/*Questa è una semplice classe java che tramite le notazioni diventa un'entità all'interno del database
con le variabili di classe che rappresentano le colonne, e gli oggetti di questa classe rappresentano
le righe della tabella. Da queste classi viene generato tramite Hibernate che è l'ORM di Springboot, lo schema
all'interno del db con tutte le impostazioni. Le notazioni @Getter,@Setter,@EqualsAndHashCode,@ToString
sono un Decorator che uso che si chiama Lombok, che permette di creare alcuni metodi automaticamente.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "misurazione", schema = "dbMisurazioni")
public class Misurazione {
    @Id
    @Version
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Basic
    @Column(name="id", nullable = false,length = 50, unique = true)
    private long id;
    @Basic
    @Column(name = "umidita",nullable = false)
    private String umidita;
    @Basic
    @Column(name = "temperatura",nullable = false)
    private String temperatura;
    @Basic
    @Column(name = "tpercepita",nullable = false)
    private String tpercepita;
    @Basic
    @Column(name = "data", nullable = false)
    private Date data;

    public Misurazione(String umidita,String temperatura, String tpercepita,Date data){
        this.umidita=umidita;
        this.temperatura=temperatura;
        this.tpercepita=tpercepita;
        this.data=data;
    }

    public Misurazione() {
    }
}
