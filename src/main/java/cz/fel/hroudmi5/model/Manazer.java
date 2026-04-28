package cz.fel.hroudmi5.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Manazer")
@PrimaryKeyJoinColumn(name = "id_zamestnanec")
@Getter
@Setter
@ToString(callSuper = true)
public class Manazer extends Zamestnanec {

    @Column(name = "id_manazer", insertable = false, updatable = false)
    private Integer idManazer;

    @Column(name = "uroven_pravomoci", nullable = false)
    private Integer urovenPravomoci;
}
