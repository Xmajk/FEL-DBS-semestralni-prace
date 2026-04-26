package cz.fel.hroudmi5.service;

import cz.fel.hroudmi5.dao.ProjektRepository;
import cz.fel.hroudmi5.dao.UcastniSeRepository;
import cz.fel.hroudmi5.dao.ZamestnanecRepository;
import cz.fel.hroudmi5.dto.PriraditNaProjektDto;
import cz.fel.hroudmi5.dto.ProjektView;
import cz.fel.hroudmi5.dto.UcastView;
import cz.fel.hroudmi5.model.Projekt;
import cz.fel.hroudmi5.model.UcastniSe;
import cz.fel.hroudmi5.model.UcastniSeId;
import cz.fel.hroudmi5.model.Zamestnanec;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjektService {

    private final ProjektRepository projektRepository;
    private final ZamestnanecRepository zamestnanecRepository;
    private final UcastniSeRepository ucastniSeRepository;

    public ProjektService(ProjektRepository projektRepository,
                          ZamestnanecRepository zamestnanecRepository,
                          UcastniSeRepository ucastniSeRepository) {
        this.projektRepository = projektRepository;
        this.zamestnanecRepository = zamestnanecRepository;
        this.ucastniSeRepository = ucastniSeRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjektView> vsechny() {
        return projektRepository.findAll().stream().map(ProjektView::from).toList();
    }

    @Transactional
    public UcastView priradZamestnance(PriraditNaProjektDto dto) {
        Zamestnanec z = zamestnanecRepository.findById(dto.getIdZamestnanec())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Zamestnanec " + dto.getIdZamestnanec() + " nenalezen"));
        Projekt p = projektRepository.findById(dto.getIdProjekt())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Projekt " + dto.getIdProjekt() + " nenalezen"));

        UcastniSe u = new UcastniSe();
        u.setId(new UcastniSeId(z.getIdZamestnanec(), p.getIdProjekt()));
        u.setZamestnanec(z);
        u.setProjekt(p);
        u.setRole(dto.getRole() != null ? dto.getRole() : "clen");
        return UcastView.from(ucastniSeRepository.save(u));
    }

    @Transactional
    public void odeberZamestnance(Integer idZamestnance, Integer idProjektu) {
        ucastniSeRepository.deleteById(new UcastniSeId(idZamestnance, idProjektu));
    }

    @Transactional
    public UcastView zmenRoli(Integer idZamestnance, Integer idProjektu, String novaRole) {
        UcastniSeId pk = new UcastniSeId(idZamestnance, idProjektu);
        UcastniSe u = ucastniSeRepository.findById(pk)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Zamestnanec " + idZamestnance + " nema ucast na projektu " + idProjektu));
        u.setRole(novaRole);
        return UcastView.from(ucastniSeRepository.save(u));
    }

    @Transactional(readOnly = true)
    public List<UcastView> clenoveProjektu(Integer idProjektu) {
        return ucastniSeRepository.findByProjekt(idProjektu)
                .stream().map(UcastView::from).toList();
    }
}
