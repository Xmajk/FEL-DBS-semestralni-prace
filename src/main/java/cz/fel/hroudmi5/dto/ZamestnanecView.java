package cz.fel.hroudmi5.dto;

import cz.fel.hroudmi5.model.Zamestnanec;

import java.time.LocalDate;

public record ZamestnanecView(
        Integer idZamestnanec,
        String jmeno,
        String prijmeni,
        String email,
        String rodneCislo,
        LocalDate datumNastupu,
        Integer idOddeleni,
        String nazevOddeleni,
        Integer idPozice,
        String nazevPozice
) {
    public static ZamestnanecView from(Zamestnanec z) {
        return new ZamestnanecView(
                z.getIdZamestnanec(),
                z.getJmeno(),
                z.getPrijmeni(),
                z.getEmail(),
                z.getRodneCislo(),
                z.getDatumNastupu(),
                z.getOddeleni() != null ? z.getOddeleni().getIdOddeleni() : null,
                z.getOddeleni() != null ? z.getOddeleni().getNazev() : null,
                z.getPozice() != null ? z.getPozice().getIdPozice() : null,
                z.getPozice() != null ? z.getPozice().getNazev() : null
        );
    }
}
