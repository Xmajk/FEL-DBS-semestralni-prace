package cz.fel.hroudmi5.service;

import cz.fel.hroudmi5.dao.OddeleniRepository;
import cz.fel.hroudmi5.dao.PoziceRepository;
import cz.fel.hroudmi5.dao.ZamestnanecDao;
import cz.fel.hroudmi5.dao.ZamestnanecRepository;
import cz.fel.hroudmi5.dto.NovyZamestnanecDto;
import cz.fel.hroudmi5.dto.ZamestnanecView;
import cz.fel.hroudmi5.model.Oddeleni;
import cz.fel.hroudmi5.model.Pozice;
import cz.fel.hroudmi5.model.Zamestnanec;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ZamestnanecService {

    private final ZamestnanecRepository zamestnanecRepository;
    private final OddeleniRepository oddeleniRepository;
    private final PoziceRepository poziceRepository;
    private final ZamestnanecDao zamestnanecDao;

    public ZamestnanecService(ZamestnanecRepository zamestnanecRepository,
                              OddeleniRepository oddeleniRepository,
                              PoziceRepository poziceRepository,
                              ZamestnanecDao zamestnanecDao) {
        this.zamestnanecRepository = zamestnanecRepository;
        this.oddeleniRepository = oddeleniRepository;
        this.poziceRepository = poziceRepository;
        this.zamestnanecDao = zamestnanecDao;
    }

    private Zamestnanec getEntity(Integer id) {
        return zamestnanecRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Zamestnanec " + id + " nenalezen"));
    }

    @Transactional(readOnly = true)
    public ZamestnanecView najdi(Integer id) {
        return ZamestnanecView.from(getEntity(id));
    }

    @Transactional
    public ZamestnanecView vytvor(NovyZamestnanecDto dto) {
        Oddeleni oddeleni = oddeleniRepository.findById(dto.getIdOddeleni())
                .orElseThrow(() -> new EntityNotFoundException("Oddeleni " + dto.getIdOddeleni() + " nenalezeno"));

        Zamestnanec z = new Zamestnanec();
        z.setJmeno(dto.getJmeno());
        z.setPrijmeni(dto.getPrijmeni());
        z.setEmail(dto.getEmail());
        z.setRodneCislo(dto.getRodneCislo());
        z.setDatumNastupu(dto.getDatumNastupu() != null ? dto.getDatumNastupu() : LocalDate.now());
        z.setOddeleni(oddeleni);
        if (dto.getIdPozice() != null) {
            Pozice pozice = poziceRepository.findById(dto.getIdPozice())
                    .orElseThrow(() -> new EntityNotFoundException("Pozice " + dto.getIdPozice() + " nenalezena"));
            z.setPozice(pozice);
        }
        return ZamestnanecView.from(zamestnanecRepository.save(z));
    }

    @Transactional
    public ZamestnanecView prevedNaOddeleni(Integer idZamestnance, Integer idOddeleni) {
        Zamestnanec z = getEntity(idZamestnance);
        Oddeleni cil = oddeleniRepository.findById(idOddeleni)
                .orElseThrow(() -> new EntityNotFoundException("Oddeleni " + idOddeleni + " nenalezeno"));
        z.setOddeleni(cil);
        return ZamestnanecView.from(zamestnanecRepository.save(z));
    }

    @Transactional
    public ZamestnanecView upravKontakt(Integer idZamestnance, String jmeno, String prijmeni, String email) {
        Zamestnanec z = getEntity(idZamestnance);
        if (jmeno != null && !jmeno.isBlank()) z.setJmeno(jmeno);
        if (prijmeni != null && !prijmeni.isBlank()) z.setPrijmeni(prijmeni);
        if (email != null && !email.isBlank()) z.setEmail(email);
        return ZamestnanecView.from(zamestnanecRepository.save(z));
    }

    @Transactional
    public void smaz(Integer id) {
        zamestnanecRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ZamestnanecView> hledejDleJmena(String vzor) {
        List<Zamestnanec> list;
        if (vzor == null || vzor.isBlank()) {
            list = zamestnanecRepository.findAll();
        } else {
            list = zamestnanecDao.vyhledejDleJmena(vzor);
        }
        return list.stream().map(ZamestnanecView::from).toList();
    }

    @Transactional(readOnly = true)
    public List<ZamestnanecView> nastoupeniPo(LocalDate datum, Integer idOddeleni) {
        return zamestnanecDao.najdiNastoupenePo(datum, idOddeleni)
                .stream().map(ZamestnanecView::from).toList();
    }
}
