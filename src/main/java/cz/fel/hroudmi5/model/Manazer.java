package cz.fel.hroudmi5.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Manazer")
@Getter
@Setter
@ToString
public class Manazer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_manazer")
    private Integer idManazer;

    @Column(name = "uroven_pravomoci", nullable = false)
    private Integer urovenPravomoci;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_zamestnanec", nullable = false, unique = true)
    @ToString.Exclude
    private Zamestnanec zamestnanec;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_oddeleni", nullable = false, unique = true)
    @ToString.Exclude
    private Oddeleni oddeleni;
}
