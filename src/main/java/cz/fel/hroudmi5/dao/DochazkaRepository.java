package cz.fel.hroudmi5.dao;

import cz.fel.hroudmi5.model.ZaznamDochazky;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DochazkaRepository extends JpaRepository<ZaznamDochazky, Integer> {

    @Query("SELECT d FROM ZaznamDochazky d WHERE d.zamestnanec.idZamestnanec = :idZam AND d.casOdchodu IS NULL")
    Optional<ZaznamDochazky> findOtevreny(@Param("idZam") Integer idZamestnanec);

    @Query("SELECT d FROM ZaznamDochazky d WHERE d.zamestnanec.idZamestnanec = :idZam ORDER BY d.casPrichodu DESC")
    List<ZaznamDochazky> findByZamestnanec(@Param("idZam") Integer idZamestnanec);
}
