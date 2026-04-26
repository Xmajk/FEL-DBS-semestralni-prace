package cz.fel.hroudmi5.dao;

import cz.fel.hroudmi5.model.Zamestnanec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ZamestnanecRepository extends JpaRepository<Zamestnanec, Integer> {

    @Query("SELECT z FROM Zamestnanec z WHERE z.oddeleni.idOddeleni = :idOddeleni")
    List<Zamestnanec> findByOddeleni(@Param("idOddeleni") Integer idOddeleni);

    @Query("SELECT z FROM Zamestnanec z WHERE z.datumNastupu > :datum ORDER BY z.datumNastupu")
    List<Zamestnanec> findNastoupeniPo(@Param("datum") LocalDate datum);
}
