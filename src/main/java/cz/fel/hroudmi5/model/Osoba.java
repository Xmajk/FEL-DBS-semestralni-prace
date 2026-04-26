package cz.fel.hroudmi5.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class Osoba {

    @Column(name = "jmeno", nullable = false, length = 100)
    private String jmeno;

    @Column(name = "prijmeni", nullable = false, length = 100)
    private String prijmeni;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    public String getCeleJmeno() {
        return jmeno + " " + prijmeni;
    }
}
