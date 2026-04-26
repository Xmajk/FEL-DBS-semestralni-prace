package cz.fel.hroudmi5.dto;

import cz.fel.hroudmi5.model.Oddeleni;

public record OddeleniView(
        Integer idOddeleni,
        String cisloOddeleni,
        String nazev,
        String lokace,
        String popis,
        Integer idNadrizeneOddeleni
) {
    public static OddeleniView from(Oddeleni o) {
        return new OddeleniView(
                o.getIdOddeleni(),
                o.getCisloOddeleni(),
                o.getNazev(),
                o.getLokace(),
                o.getPopis(),
                o.getNadrizeneOddeleni() != null ? o.getNadrizeneOddeleni().getIdOddeleni() : null
        );
    }
}
