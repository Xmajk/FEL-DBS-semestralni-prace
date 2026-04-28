package cz.fel.hroudmi5.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "Externista")
@PrimaryKeyJoinColumn(name = "id_zamestnanec")
@Getter
@Setter
@ToString(callSuper = true)
public class Externista extends Zamestnanec {

    @Column(name = "id_externista", insertable = false, updatable = false)
    private Integer idExternista;

    @Column(name = "nazev_agentury", nullable = false)
    private String nazevAgentury;

    @Column(name = "konec_smlouvy", nullable = false)
    private LocalDate konecSmlouvy;
}
