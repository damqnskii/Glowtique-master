package com.glowtique.glowtique.web;

import com.glowtique.glowtique.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AlreadyRegEmailException.class)
    public String handleAlreadyRegEmailException(RedirectAttributes redirectAttributes, final AlreadyRegEmailException e) {
        String message = e.getMessage();

        redirectAttributes.addFlashAttribute("alreadyRegisteredEmailMessage", message);
        return "redirect:/register";
    }
    @ExceptionHandler(CartNotExisting.class)
    public ModelAndView handleCartNotExisting(final CartNotExisting e) {
        return returnErrorPage(e.getMessage());
    }
    @ExceptionHandler(ProductNotfoundException.class)
    public ModelAndView handleProductNotfound(final ProductNotfoundException e) {
        return returnErrorPage(e.getMessage());
    }
    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorized(final UnauthorizedException e) {
        return "redirect:/login";
    }
    @ExceptionHandler(NotEnoughProductStock.class)
    public String handleNotEnoughProductStock(RedirectAttributes redirectAttributes,final NotEnoughProductStock e) {
        String message = e.getMessage();

        redirectAttributes.addFlashAttribute("notEnoughProductStockMessage", message);
        return "redirect:/checkout";
    }
    @ExceptionHandler(UserNotExisting.class)
    public String handleUserNotExisting(final UserNotExisting e) {
        return "redirect:/register";
    }
    @ExceptionHandler(ExistingPhoneNumber.class)
    public String handleExistingPhoneNumber(RedirectAttributes redirectAttributes,final ExistingPhoneNumber e) {
        String message = e.getMessage();
        redirectAttributes.addFlashAttribute("existingPhoneNumberMessage", message);
        return "redirect:/edit-profile";
    }
    @ExceptionHandler(UnchangeableEmailException.class)
    public String handelUnchangeableEmailException(RedirectAttributes redirectAttributes,final UnchangeableEmailException e) {
        String message = e.getMessage();

        redirectAttributes.addFlashAttribute("unchangeableEmailMessage", message);
        return "redirect:/edit-profile";
    }
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            AccessDeniedException.class,
            NoResourceFoundException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class
    })
    public ModelAndView handleNotFoundExceptions(Exception exception) {

        return new ModelAndView("error");
    }
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(Exception exception) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("internal-server-error");
        modelAndView.addObject("errorMessage", exception.getClass().getSimpleName());

        return modelAndView;
    }

    private ModelAndView returnErrorPage(String message) {

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", message);
        modelAndView.addObject("timestamp", LocalDateTime.now());
        return modelAndView;
    }

}
