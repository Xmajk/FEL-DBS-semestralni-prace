package cz.fel.hroudmi5.dao;

import cz.fel.hroudmi5.model.Pozice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoziceRepository extends JpaRepository<Pozice, Integer> {
}
