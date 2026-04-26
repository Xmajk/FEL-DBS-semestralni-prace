package cz.fel.hroudmi5.dto;

import cz.fel.hroudmi5.model.Manazer;

public record ManazerView(
        Integer idManazer,
        Integer urovenPravomoci,
        Integer idZamestnanec,
        String jmenoZamestnance,
        Integer idOddeleni,
        String nazevOddeleni
) {
    public static ManazerView from(Manazer m) {
        return new ManazerView(
                m.getIdManazer(),
                m.getUrovenPravomoci(),
                m.getZamestnanec().getIdZamestnanec(),
                m.getZamestnanec().getCeleJmeno(),
                m.getOddeleni().getIdOddeleni(),
                m.getOddeleni().getNazev()
        );
    }
}
