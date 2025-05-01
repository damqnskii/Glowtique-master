package com.glowtique.glowtique.web;

import com.glowtique.glowtique.product.service.FragranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FragranceController {
    private final FragranceService fragranceService;

    @Autowired
    public FragranceController(FragranceService fragranceService) {
        this.fragranceService = fragranceService;
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/fragrances")
    public ModelAndView getFragranceEdit() {

        return null;
    }
}
