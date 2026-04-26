package cz.fel.hroudmi5.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Oddeleni")
@Getter
@Setter
@ToString
public class Oddeleni {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_oddeleni")
    private Integer idOddeleni;

    @Column(name = "cislo_oddeleni", nullable = false, length = 6, unique = true)
    private String cisloOddeleni;

    @Column(name = "nazev", nullable = false, length = 100)
    private String nazev;

    @Column(name = "lokace", nullable = false, length = 100)
    private String lokace;

    @Column(name = "popis", columnDefinition = "text")
    private String popis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nadrizene_oddeleni")
    @ToString.Exclude
    private Oddeleni nadrizeneOddeleni;
}
