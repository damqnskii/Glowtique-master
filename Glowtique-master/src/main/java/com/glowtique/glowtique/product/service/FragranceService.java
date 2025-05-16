package com.glowtique.glowtique.product.service;

import com.glowtique.glowtique.exception.FragranceNotFound;
import com.glowtique.glowtique.product.model.Fragrance;
import com.glowtique.glowtique.product.model.FragranceType;
import com.glowtique.glowtique.product.repository.FragranceRepository;
import com.glowtique.glowtique.web.dto.FragranceEditRequest;
import com.glowtique.glowtique.web.dto.FragranceRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FragranceService {
    private FragranceRepository fragranceRepository;

    @Autowired
    public FragranceService(FragranceRepository fragranceRepository) {
        this.fragranceRepository = fragranceRepository;
    }


    public Fragrance getFragranceById(UUID id) {
        return fragranceRepository.getFragranceById(id);
    }

    public List<Fragrance> getAllFragrances() {
        return fragranceRepository.findAll();
    }

    public Fragrance createFragrance(FragranceRequest request) {
        Fragrance fragrance = Fragrance.builder()
                .baseNotes(request.getBaseNotes())
                .heartNotes(request.getHeartNotes())
                .topNotes(request.getTopNotes())
                .type(request.getTypes())
                .build();

        log.info("Fragrance was created at: {}", LocalDateTime.now());

        return fragranceRepository.save(fragrance);
    }

    public void deleteFragranceById(UUID id) {
        if (fragranceRepository.getFragranceById(id) == null) {
            throw new FragranceNotFound("Fragrance not found");
        }
        fragranceRepository.deleteById(id);
        log.info("Fragrance with id {} deleted", id);
        log.info("Fragrance was deleted at {}", LocalDateTime.now());
    }

    @Transactional
    public Fragrance updateFragrance(FragranceEditRequest request, UUID id) {
        if (fragranceRepository.getFragranceById(id) == null) {
            throw new FragranceNotFound("Fragrance not found");
        }
        Fragrance fragrance = fragranceRepository.getFragranceById(id);
        fragrance.setType(request.getTypes());
        fragrance.setBaseNotes(request.getBaseNotes());
        fragrance.setHeartNotes(request.getHeartNotes());
        fragrance.setTopNotes(request.getTopNotes());

        log.info("Updated fragrance: {}", fragrance);
        log.info("Updated fragrance with Id: {} ", fragrance.getId());
        log.info("Updated at: {}", LocalDateTime.now());
        return fragranceRepository.save(fragrance);
    }
}
