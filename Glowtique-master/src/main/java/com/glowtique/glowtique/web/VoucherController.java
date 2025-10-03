package com.glowtique.glowtique.web;

import com.glowtique.glowtique.security.AuthenticationMetadata;
import com.glowtique.glowtique.user.model.User;
import com.glowtique.glowtique.user.service.UserService;
import com.glowtique.glowtique.voucher.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class VoucherController {
    private final VoucherService voucherService;
    private final UserService userService;

    @Autowired
    public VoucherController(VoucherService voucherService, UserService userService) {
        this.voucherService = voucherService;
        this.userService = userService;
    }

    @GetMapping("/vouchers")
    public ModelAndView getVouchers(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata, Model model) {
        if (authenticationMetadata == null) {
            return  new ModelAndView("redirect:/login");
        }
        User user = userService.getUserById(authenticationMetadata.getUserId());

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("vouchers");
        modelAndView.addObject("user", user);
        modelAndView.addObject("vouchers", user.getVouchers());
        return modelAndView;
    }
}
