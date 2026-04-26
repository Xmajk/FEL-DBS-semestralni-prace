package cz.fel.hroudmi5.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "UcastniSe")
@Getter
@Setter
@ToString
public class UcastniSe {

    @EmbeddedId
    private UcastniSeId id = new UcastniSeId();

    @Column(name = "role", nullable = false)
    private String role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idZamestnanec")
    @JoinColumn(name = "id_zamestnanec")
    @ToString.Exclude
    private Zamestnanec zamestnanec;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idProjekt")
    @JoinColumn(name = "id_projekt")
    @ToString.Exclude
    private Projekt projekt;
}
