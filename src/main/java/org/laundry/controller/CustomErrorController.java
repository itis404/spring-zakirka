package org.laundry.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("message", "Страница не найдена 404");
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("message", "У вас нет доступа к этой странице 403");
            } else {
                model.addAttribute("message", "Произошла ошибка на сервере");
            }
            model.addAttribute("code", statusCode);
        }
        
        return "error";
    }
}
