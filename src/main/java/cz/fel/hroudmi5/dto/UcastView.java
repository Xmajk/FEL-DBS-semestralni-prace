package cz.fel.hroudmi5.dto;

import cz.fel.hroudmi5.model.UcastniSe;

public record UcastView(
        Integer idZamestnanec,
        String jmenoZamestnance,
        Integer idProjekt,
        String nazevProjektu,
        String role
) {
    public static UcastView from(UcastniSe u) {
        return new UcastView(
                u.getZamestnanec().getIdZamestnanec(),
                u.getZamestnanec().getCeleJmeno(),
                u.getProjekt().getIdProjekt(),
                u.getProjekt().getNazev(),
                u.getRole()
        );
    }
}
