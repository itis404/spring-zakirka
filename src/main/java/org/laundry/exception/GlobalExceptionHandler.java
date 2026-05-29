package org.laundry.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception ex, HttpServletRequest request) {
        log.error("An error occurred: ", ex);

        if (isAjaxRequest(request)) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "An error occurred on the server");
            response.put("message", ex.getMessage());
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            if (ex instanceof ResponseStatusException rse) {
                status = HttpStatus.valueOf(rse.getStatusCode().value());
            } else if (ex instanceof IllegalArgumentException) {
                status = HttpStatus.BAD_REQUEST;
            }
            return new ResponseEntity<>(response, status);
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", ex);
        mav.addObject("url", request.getRequestURL());
        mav.setViewName("error");
        return mav;
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWith)
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));
    }
}
