package com.glowtique.glowtique.web;

import com.glowtique.glowtique.product.model.Fragrance;
import com.glowtique.glowtique.product.model.FragranceType;
import com.glowtique.glowtique.product.service.FragranceService;
import com.glowtique.glowtique.web.dto.FragranceEditRequest;
import com.glowtique.glowtique.web.dto.FragranceRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class FragranceController {
    private final FragranceService fragranceService;

    @Autowired
    public FragranceController(FragranceService fragranceService) {
        this.fragranceService = fragranceService;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/fragrance")
    public ModelAndView getFragranceOperations() {
        return new ModelAndView("admin-fragrance");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/fragrance-list")
    public ModelAndView getFragranceList() {
        ModelAndView modelAndView = new ModelAndView("fragrance-list");
        modelAndView.addObject("fragrances", fragranceService.getAllFragrances());
        return modelAndView;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/fragrance/creation")
    public ModelAndView getFragranceCreation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("fragrance-creation");
        modelAndView.addObject("fragranceRequest", new FragranceRequest());
        modelAndView.addObject("fragranceTypes", FragranceType.values());
        return modelAndView;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/admin-dashboard/fragrance/create")
    public ModelAndView createFragrance(@Valid @ModelAttribute FragranceRequest fragranceRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("fragrance-creation");
        }
        fragranceService.createFragrance(fragranceRequest);
        return new ModelAndView("fragrance-list");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/admin-dashboard/fragrance/delete/{id}")
    public ModelAndView deleteFragrance(@PathVariable UUID id) {
        fragranceService.deleteFragranceById(id);
        return new ModelAndView("fragrance-list");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/fragrance/edit/{id}")
    public ModelAndView getFragranceEdit(@PathVariable UUID id) {
        ModelAndView modelAndView = new ModelAndView("fragrance-edit");
        Fragrance fragrance = fragranceService.getFragranceById(id);
        modelAndView.addObject("fragrance", fragrance);
        modelAndView.addObject("fragranceTypes", FragranceType.values());
        modelAndView.addObject("fragranceEditRequest", new FragranceEditRequest(fragrance.getId(),
                fragrance.getBaseNotes(),
                fragrance.getHeartNotes(),
                fragrance.getTopNotes(),
                fragrance.getType()));
        return modelAndView;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/admin-dashboard/fragrance/edit/update")
    public ModelAndView updateFragrance(@Valid @ModelAttribute FragranceEditRequest fragranceEditRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("fragrance-edit");
        }
        fragranceService.updateFragrance(fragranceEditRequest, fragranceEditRequest.getId());
        return new ModelAndView("admin-fragrance");
    }

}
