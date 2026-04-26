package cz.fel.hroudmi5.dao;

import cz.fel.hroudmi5.model.Externista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternistaRepository extends JpaRepository<Externista, Integer> {
}
