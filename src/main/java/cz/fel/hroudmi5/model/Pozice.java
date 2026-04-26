package cz.fel.hroudmi5.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Pozice")
@Getter
@Setter
@ToString
public class Pozice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pozice")
    private Integer idPozice;

    @Column(name = "nazev", nullable = false, length = 100, unique = true)
    private String nazev;

    @Column(name = "uroven", nullable = false)
    private Integer uroven;

    @Column(name = "popis", columnDefinition = "text")
    private String popis;
}
