package cz.fel.hroudmi5.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "Externista")
@Getter
@Setter
@ToString
public class Externista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_externista")
    private Integer idExternista;

    @Column(name = "nazev_agentury", nullable = false)
    private String nazevAgentury;

    @Column(name = "konec_smlouvy", nullable = false)
    private LocalDate konecSmlouvy;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_zamestnanec", nullable = false, unique = true)
    @ToString.Exclude
    private Zamestnanec zamestnanec;
}
