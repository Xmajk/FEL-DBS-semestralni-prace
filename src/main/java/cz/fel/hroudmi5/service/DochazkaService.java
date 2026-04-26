package cz.fel.hroudmi5.service;

import cz.fel.hroudmi5.dao.DochazkaRepository;
import cz.fel.hroudmi5.dao.ZamestnanecRepository;
import cz.fel.hroudmi5.dto.DochazkaView;
import cz.fel.hroudmi5.model.Zamestnanec;
import cz.fel.hroudmi5.model.ZaznamDochazky;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DochazkaService {

    private final DochazkaRepository dochazkaRepository;
    private final ZamestnanecRepository zamestnanecRepository;

    public DochazkaService(DochazkaRepository dochazkaRepository,
                           ZamestnanecRepository zamestnanecRepository) {
        this.dochazkaRepository = dochazkaRepository;
        this.zamestnanecRepository = zamestnanecRepository;
    }

    @Transactional(readOnly = true)
    public List<DochazkaView> proZamestnance(Integer idZamestnanec) {
        return dochazkaRepository.findByZamestnanec(idZamestnanec)
                .stream().map(DochazkaView::from).toList();
    }

    @Transactional
    public DochazkaView prichod(Integer idZamestnanec, String typ) {
        Zamestnanec z = zamestnanecRepository.findById(idZamestnanec)
                .orElseThrow(() -> new EntityNotFoundException("Zamestnanec " + idZamestnanec + " nenalezen"));

        ZaznamDochazky zd = new ZaznamDochazky();
        zd.setZamestnanec(z);
        zd.setCasPrichodu(LocalDateTime.now());
        zd.setTypZaznamu(typ != null ? typ : "kancelar");
        return DochazkaView.from(dochazkaRepository.save(zd));
    }

    @Transactional
    public DochazkaView odchod(Integer idZamestnanec) {
        ZaznamDochazky otevreny = dochazkaRepository.findOtevreny(idZamestnanec)
                .orElseThrow(() -> new IllegalStateException(
                        "Zamestnanec " + idZamestnanec + " nema otevreny zaznam dochazky"));
        otevreny.setCasOdchodu(LocalDateTime.now());
        return DochazkaView.from(dochazkaRepository.save(otevreny));
    }
}
