package com.glowtique.glowtique.product.service;

import com.glowtique.glowtique.product.model.Fragrance;
import com.glowtique.glowtique.product.model.FragranceType;
import com.glowtique.glowtique.product.repository.FragranceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FragranceService {
    private FragranceRepository fragranceRepository;

    @Autowired
    public FragranceService(FragranceRepository fragranceRepository) {
        this.fragranceRepository = fragranceRepository;
    }

    public List<Fragrance> getAllFragranceTypes() {
        return null;
    }
}
