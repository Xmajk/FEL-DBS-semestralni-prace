package cz.fel.hroudmi5.dao;

import cz.fel.hroudmi5.model.UcastniSe;
import cz.fel.hroudmi5.model.UcastniSeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UcastniSeRepository extends JpaRepository<UcastniSe, UcastniSeId> {

    @Query("SELECT u FROM UcastniSe u WHERE u.projekt.idProjekt = :idProjekt")
    List<UcastniSe> findByProjekt(@Param("idProjekt") Integer idProjekt);

    @Query("SELECT u FROM UcastniSe u WHERE u.zamestnanec.idZamestnanec = :idZam")
    List<UcastniSe> findByZamestnanec(@Param("idZam") Integer idZam);
}
