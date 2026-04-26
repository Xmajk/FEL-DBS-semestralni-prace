package cz.fel.hroudmi5.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class NovyZamestnanecDto {
    private String jmeno;
    private String prijmeni;
    private String email;
    private String rodneCislo;
    private LocalDate datumNastupu;
    private Integer idOddeleni;
    private Integer idPozice;
}
