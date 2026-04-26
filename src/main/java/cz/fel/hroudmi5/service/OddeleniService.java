package cz.fel.hroudmi5.service;

import cz.fel.hroudmi5.dao.OddeleniRepository;
import cz.fel.hroudmi5.dto.OddeleniView;
import cz.fel.hroudmi5.model.Oddeleni;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OddeleniService {

    private final OddeleniRepository oddeleniRepository;

    public OddeleniService(OddeleniRepository oddeleniRepository) {
        this.oddeleniRepository = oddeleniRepository;
    }

    @Transactional(readOnly = true)
    public List<OddeleniView> vsechna() {
        return oddeleniRepository.findAll().stream().map(OddeleniView::from).toList();
    }

    @Transactional
    public Oddeleni uloz(Oddeleni o) {
        return oddeleniRepository.save(o);
    }
}
