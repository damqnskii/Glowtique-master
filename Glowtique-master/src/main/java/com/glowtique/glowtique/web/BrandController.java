package com.glowtique.glowtique.web;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.brand.service.BrandService;
import com.glowtique.glowtique.web.dto.BrandEditRequest;
import com.glowtique.glowtique.web.dto.BrandRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
public class BrandController {
    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/brand-list")
    public ModelAndView getBrandList(Model model) {
        ModelAndView modelAndView = new ModelAndView("brand-list");
        model.addAttribute("brandList", brandService.getAllBrands());
        return modelAndView;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/brand")
    public ModelAndView getBrandOperations() {
        return new ModelAndView("admin-brand");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/brand/creation")
    public ModelAndView getBrandCreation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("brandRequest", new BrandRequest());
        modelAndView.setViewName("brand-creation");

        return modelAndView;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/admin-dashboard/brand/creation/create")
    public ModelAndView createBrand(@Valid BrandRequest brandRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("brand-creation");
        }
        brandService.createBrand(brandRequest);
        return new ModelAndView("admin-brand");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin-dashboard/brand/edit/{id}")
    public ModelAndView getBrandEdit(@PathVariable UUID id) {
        ModelAndView modelAndView = new ModelAndView("brand-edit");
        Brand brand = brandService.getBrandById(id);
        modelAndView.addObject("brand", brand);
        modelAndView.addObject("brandEditRequest", new BrandEditRequest(brand.getId(), brand.getName(), brand.getDescription(), brand.getLogo()));
        return modelAndView;
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/admin-dashboard/brand/edit/update")
    public ModelAndView updateBrand(@Valid @ModelAttribute BrandEditRequest brandRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("brand-edit");
        }
        brandService.updateBrand(brandRequest.getId(), brandRequest);
        return new ModelAndView("admin-brand");
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/admin-dashboard/brand/delete/{id}")
    public ModelAndView deleteBrand(@PathVariable UUID id) {
        brandService.deleteBrand(id);
        return new ModelAndView("admin-brand");
    }

    @GetMapping("/brands")
    public ModelAndView getBrandsPage() {

        List<Brand> brands = brandService.getAllBrands();

        ModelAndView modelAndView = new ModelAndView("brands");
        modelAndView.addObject("brands", brands);
        return modelAndView;
    }

    @GetMapping("/brand/{id}")
    public ModelAndView getBrandDetails(@PathVariable UUID id) {

        Brand currentBrand = brandService.getBrandById(id);

        ModelAndView modelAndView = new ModelAndView("brand-details");
        modelAndView.addObject("brand", currentBrand);

        return modelAndView;
    }

}
