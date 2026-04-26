package cz.fel.hroudmi5.dto;

import cz.fel.hroudmi5.model.ZaznamDochazky;

import java.time.LocalDateTime;

public record DochazkaView(
        Integer idDochazka,
        Integer idZamestnanec,
        String jmenoZamestnance,
        LocalDateTime casPrichodu,
        LocalDateTime casOdchodu,
        String typZaznamu
) {
    public static DochazkaView from(ZaznamDochazky z) {
        return new DochazkaView(
                z.getIdDochazka(),
                z.getZamestnanec().getIdZamestnanec(),
                z.getZamestnanec().getCeleJmeno(),
                z.getCasPrichodu(),
                z.getCasOdchodu(),
                z.getTypZaznamu()
        );
    }
}
