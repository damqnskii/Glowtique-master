package com.glowtique.glowtique.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class InfoPageController {

    @GetMapping("/terms")
    public ModelAndView terms() {
        return new ModelAndView("terms");
    }

    @GetMapping("/delivery-terms")
    public ModelAndView deliveryTerms() {
        return new ModelAndView("delivery-terms");
    }

    @GetMapping("/return-policy")
    public ModelAndView returnPolicy() {
        return new ModelAndView("return-policy");
    }

    @GetMapping("/privacy-policy")
    public ModelAndView privacyPolicy() {
        return new ModelAndView("privacy-policy");
    }

    @GetMapping("/payment-methods")
    public ModelAndView paymentMethods() {
        return new ModelAndView("payment-methods");
    }
}
