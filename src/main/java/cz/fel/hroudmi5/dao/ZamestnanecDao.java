package cz.fel.hroudmi5.dao;

import cz.fel.hroudmi5.model.Zamestnanec;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ZamestnanecDao {

    @PersistenceContext
    private EntityManager em;

    public List<Zamestnanec> najdiNastoupenePo(LocalDate datum, Integer idOddeleni) {
        String jpql = "SELECT z FROM Zamestnanec z WHERE z.datumNastupu > :datum"
                + (idOddeleni != null ? " AND z.oddeleni.idOddeleni = :idOddeleni" : "")
                + " ORDER BY z.datumNastupu";
        TypedQuery<Zamestnanec> q = em.createQuery(jpql, Zamestnanec.class);
        q.setParameter("datum", datum);
        if (idOddeleni != null) {
            q.setParameter("idOddeleni", idOddeleni);
        }
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Zamestnanec> vyhledejDleJmena(String vzor) {
        Query q = em.createNativeQuery(
                "SELECT z.* FROM \"Zamestnanec\" z " +
                "WHERE z.jmeno ILIKE :vzor OR z.prijmeni ILIKE :vzor " +
                "ORDER BY z.prijmeni, z.jmeno",
                Zamestnanec.class);
        q.setParameter("vzor", "%" + vzor + "%");
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> kontaktyManazeru() {
        Query q = em.createNativeQuery(
                "SELECT oddeleni, manazer, email, telefon FROM v_kontaktni_seznam_manazeru");
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> kontaktyManazeruProOddeleni(String vzorOddeleni) {
        Query q = em.createNativeQuery(
                "SELECT oddeleni, manazer, email, telefon " +
                "FROM v_kontaktni_seznam_manazeru " +
                "WHERE oddeleni ILIKE ?1");
        q.setParameter(1, "%" + vzorOddeleni + "%");
        return q.getResultList();
    }
}
