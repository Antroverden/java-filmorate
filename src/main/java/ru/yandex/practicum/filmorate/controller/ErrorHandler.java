//package ru.yandex.practicum.filmorate.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import ru.yandex.practicum.filmorate.exception.NotFoundException;
//
//import javax.validation.ValidationException;
//
//@RestControllerAdvice
//public class ErrorHandler {
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public RuntimeException handleValidationException(final ValidationException e) {
//        return new RuntimeException(e.getMessage());
//    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public RuntimeException handleNotFoundException(final NotFoundException e) {
//        return new RuntimeException(e.getMessage());
//    }
//
//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public RuntimeException handleThrowable(final Exception e) {
//        return new RuntimeException("Произошла непредвиденная ошибка.");
//    }
//}
