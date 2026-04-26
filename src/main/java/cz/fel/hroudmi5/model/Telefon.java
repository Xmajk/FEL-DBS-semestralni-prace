package cz.fel.hroudmi5.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Telefon")
@Getter
@Setter
@ToString
public class Telefon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_telefon")
    private Integer idTelefon;

    @Column(name = "telefon", nullable = false)
    private String telefon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_zamestnanec", nullable = false)
    @ToString.Exclude
    private Zamestnanec zamestnanec;
}
