package cz.fel.hroudmi5.service;

import cz.fel.hroudmi5.dao.PoziceRepository;
import cz.fel.hroudmi5.dto.PoziceView;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PoziceService {

    private final PoziceRepository poziceRepository;

    public PoziceService(PoziceRepository poziceRepository) {
        this.poziceRepository = poziceRepository;
    }

    @Transactional(readOnly = true)
    public List<PoziceView> vsechny() {
        return poziceRepository.findAll().stream().map(PoziceView::from).toList();
    }
}
