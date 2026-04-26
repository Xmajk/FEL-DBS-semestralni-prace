package cz.fel.hroudmi5.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "Projekt")
@Getter
@Setter
@ToString
public class Projekt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_projekt")
    private Integer idProjekt;

    @Column(name = "nazev", nullable = false, length = 64)
    private String nazev;

    @Column(name = "termin_zahajeni", nullable = false)
    private LocalDate terminZahajeni;

    @Column(name = "termin_ukonceni")
    private LocalDate terminUkonceni;

    @Column(name = "popis", columnDefinition = "text")
    private String popis;
}
