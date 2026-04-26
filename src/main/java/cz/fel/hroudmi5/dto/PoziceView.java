package cz.fel.hroudmi5.dto;

import cz.fel.hroudmi5.model.Pozice;

public record PoziceView(Integer idPozice, String nazev, Integer uroven, String popis) {
    public static PoziceView from(Pozice p) {
        return new PoziceView(p.getIdPozice(), p.getNazev(), p.getUroven(), p.getPopis());
    }
}
