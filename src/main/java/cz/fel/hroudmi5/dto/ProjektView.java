package cz.fel.hroudmi5.dto;

import cz.fel.hroudmi5.model.Projekt;

import java.time.LocalDate;

public record ProjektView(
        Integer idProjekt,
        String nazev,
        LocalDate terminZahajeni,
        LocalDate terminUkonceni,
        String popis
) {
    public static ProjektView from(Projekt p) {
        return new ProjektView(
                p.getIdProjekt(),
                p.getNazev(),
                p.getTerminZahajeni(),
                p.getTerminUkonceni(),
                p.getPopis()
        );
    }
}
