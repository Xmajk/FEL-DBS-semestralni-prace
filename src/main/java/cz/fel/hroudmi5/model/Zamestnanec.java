package cz.fel.hroudmi5.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Zamestnanec")
@Getter
@Setter
@ToString(callSuper = true)
public class Zamestnanec extends Osoba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_zamestnanec")
    private Integer idZamestnanec;

    @Column(name = "rodne_cislo", nullable = false, length = 11, unique = true)
    private String rodneCislo;

    @Column(name = "datum_nastupu", nullable = false)
    private LocalDate datumNastupu;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_oddeleni", nullable = false)
    @ToString.Exclude
    private Oddeleni oddeleni;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pozice")
    @ToString.Exclude
    private Pozice pozice;

    @OneToMany(mappedBy = "zamestnanec", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Telefon> telefony = new ArrayList<>();

    @OneToMany(mappedBy = "zamestnanec", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Adresa> adresy = new ArrayList<>();

    @OneToMany(mappedBy = "zamestnanec", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<UcastniSe> ucasti = new ArrayList<>();
}
