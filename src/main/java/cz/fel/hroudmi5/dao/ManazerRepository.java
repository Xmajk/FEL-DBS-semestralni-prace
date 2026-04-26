package cz.fel.hroudmi5.dao;

import cz.fel.hroudmi5.model.Manazer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManazerRepository extends JpaRepository<Manazer, Integer> {

    Optional<Manazer> findByOddeleni_IdOddeleni(Integer idOddeleni);

    @Modifying
    @Query("DELETE FROM Manazer m WHERE m.oddeleni.idOddeleni = :idOddeleni")
    int deleteByOddeleni(@Param("idOddeleni") Integer idOddeleni);
}
