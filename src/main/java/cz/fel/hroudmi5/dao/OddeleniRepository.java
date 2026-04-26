package cz.fel.hroudmi5.dao;

import cz.fel.hroudmi5.model.Oddeleni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OddeleniRepository extends JpaRepository<Oddeleni, Integer> {
}
