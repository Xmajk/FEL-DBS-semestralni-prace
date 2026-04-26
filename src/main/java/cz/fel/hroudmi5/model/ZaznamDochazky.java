package cz.fel.hroudmi5.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "ZaznamDochazky")
@Getter
@Setter
@ToString
public class ZaznamDochazky {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dochazka")
    private Integer idDochazka;

    @Column(name = "cas_prichodu", nullable = false)
    private LocalDateTime casPrichodu;

    @Column(name = "cas_odchodu")
    private LocalDateTime casOdchodu;

    @Column(name = "typ_zaznamu", nullable = false, length = 100)
    private String typZaznamu;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_zamestnanec", nullable = false)
    @ToString.Exclude
    private Zamestnanec zamestnanec;
}
