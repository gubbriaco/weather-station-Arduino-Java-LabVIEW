package it.franco.arduinomisurazioni.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Version;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "count", schema = "dbMisurazioni")
public class Count {
    @Id
    @Version
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Basic
    @Column(name="id", nullable = false,length = 50, unique = true)
    private long id;

    @Basic
    @Column(name = "n",nullable = false)
    private int n;

    @Basic
    @Column(name = "count",nullable = false)
    private int count;

    public Count (long id, int n, int count){
        this.id=id;
        this.n=n;
        this.count=count;
    }

    public Count() {

    }
}
