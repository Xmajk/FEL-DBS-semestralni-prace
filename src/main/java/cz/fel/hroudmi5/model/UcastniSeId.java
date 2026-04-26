package cz.fel.hroudmi5.model;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class UcastniSeId implements Serializable {

    private Integer idZamestnanec;
    private Integer idProjekt;

    public UcastniSeId(Integer idZamestnanec, Integer idProjekt) {
        this.idZamestnanec = idZamestnanec;
        this.idProjekt = idProjekt;
    }
}
