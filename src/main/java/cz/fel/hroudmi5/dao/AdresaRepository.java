package cz.fel.hroudmi5.dao;

import cz.fel.hroudmi5.model.Adresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdresaRepository extends JpaRepository<Adresa, Integer> {
}
