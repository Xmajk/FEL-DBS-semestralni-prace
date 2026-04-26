package cz.fel.hroudmi5.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Adresa")
@Getter
@Setter
@ToString
public class Adresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adresa")
    private Integer idAdresa;

    @Column(name = "ulice", nullable = false)
    private String ulice;

    @Column(name = "mesto", nullable = false)
    private String mesto;

    @Column(name = "psc", nullable = false)
    private String psc;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_zamestnanec", nullable = false)
    @ToString.Exclude
    private Zamestnanec zamestnanec;
}
