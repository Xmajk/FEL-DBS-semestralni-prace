package cz.fel.hroudmi5.dao;

import cz.fel.hroudmi5.model.Projekt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjektRepository extends JpaRepository<Projekt, Integer> {
}
