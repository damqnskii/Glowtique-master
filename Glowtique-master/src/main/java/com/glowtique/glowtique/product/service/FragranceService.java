package com.glowtique.glowtique.product.service;

import com.glowtique.glowtique.exception.FragranceNotFound;
import com.glowtique.glowtique.product.model.Fragrance;
import com.glowtique.glowtique.product.model.FragranceType;
import com.glowtique.glowtique.product.repository.FragranceRepository;
import com.glowtique.glowtique.web.dto.FragranceEditRequest;
import com.glowtique.glowtique.web.dto.FragranceRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
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

        return fragranceRepository.save(fragrance);
    }

    public void deleteFragranceById(UUID id) {
        if (fragranceRepository.getFragranceById(id) == null) {
            throw new FragranceNotFound("Fragrance not found");
        }
        fragranceRepository.deleteById(id);
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
        return fragranceRepository.save(fragrance);
    }
}
