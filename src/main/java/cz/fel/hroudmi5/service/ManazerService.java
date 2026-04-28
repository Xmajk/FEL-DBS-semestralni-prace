package cz.fel.hroudmi5.service;

import cz.fel.hroudmi5.dao.ManazerRepository;
import cz.fel.hroudmi5.dao.OddeleniRepository;
import cz.fel.hroudmi5.dao.ZamestnanecDao;
import cz.fel.hroudmi5.dao.ZamestnanecRepository;
import cz.fel.hroudmi5.dto.KontaktManazeraView;
import cz.fel.hroudmi5.dto.ManazerView;
import cz.fel.hroudmi5.dto.VymenaManazeraDto;
import cz.fel.hroudmi5.model.Manazer;
import cz.fel.hroudmi5.model.Oddeleni;
import cz.fel.hroudmi5.model.Zamestnanec;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ManazerService {

    private final ManazerRepository manazerRepository;
    private final OddeleniRepository oddeleniRepository;
    private final ZamestnanecRepository zamestnanecRepository;
    private final ZamestnanecDao zamestnanecDao;

    @PersistenceContext
    private EntityManager em;

    public ManazerService(ManazerRepository manazerRepository,
                          OddeleniRepository oddeleniRepository,
                          ZamestnanecRepository zamestnanecRepository,
                          ZamestnanecDao zamestnanecDao) {
        this.manazerRepository = manazerRepository;
        this.oddeleniRepository = oddeleniRepository;
        this.zamestnanecRepository = zamestnanecRepository;
        this.zamestnanecDao = zamestnanecDao;
    }

    @Transactional(readOnly = true)
    public List<ManazerView> vsichni() {
        return manazerRepository.findAll().stream().map(ManazerView::from).toList();
    }

    @Transactional(readOnly = true)
    public List<KontaktManazeraView> kontakty(String vzorOddeleni) {
        List<Object[]> rows = (vzorOddeleni == null || vzorOddeleni.isBlank())
                ? zamestnanecDao.kontaktyManazeru()
                : zamestnanecDao.kontaktyManazeruProOddeleni(vzorOddeleni);
        return rows.stream().map(KontaktManazeraView::from).toList();
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public ManazerView vymenManazera(VymenaManazeraDto dto) {
        Oddeleni oddeleni = oddeleniRepository.findById(dto.getIdOddeleni())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Oddeleni " + dto.getIdOddeleni() + " nenalezeno"));

        Zamestnanec novy = zamestnanecRepository.findById(dto.getIdNovehoZamestnance())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Zamestnanec " + dto.getIdNovehoZamestnance() + " nenalezen"));

        if (!novy.getOddeleni().getIdOddeleni().equals(oddeleni.getIdOddeleni())) {
            throw new IllegalArgumentException(
                    "Novy manazer musi byt zamestnancem oddeleni, ktere ma ridit");
        }

        manazerRepository.deleteByOddeleni(oddeleni.getIdOddeleni());
        // flush kvůli unique_oddeleni — jinak by INSERT spadl při kolizi v perzistentním kontextu
        em.flush();

        // persist(new Manazer()) by vytvořil nového zaměstnance; tady povyšujeme stávajícího
        em.createNativeQuery("""
                INSERT INTO "Manazer" (id_zamestnanec, uroven_pravomoci)
                VALUES (:idZ, :uroven)
                """)
          .setParameter("idZ",    novy.getIdZamestnanec())
          .setParameter("uroven", dto.getUrovenPravomoci() != null ? dto.getUrovenPravomoci() : 1)
          .executeUpdate();

        // novy je v PC jako Zamestnanec — clear zajistí, že em.find vrátí Manazer
        em.flush();
        em.clear();

        Manazer novyManazer = em.find(Manazer.class, novy.getIdZamestnanec());
        return ManazerView.from(novyManazer);
    }
}
