package cz.fel.hroudmi5.dao;

import cz.fel.hroudmi5.model.Telefon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelefonRepository extends JpaRepository<Telefon, Integer> {
}
