package ru.practicum.shareit.exception.model;

public class InternalServerErrorException extends RuntimeException{
    InternalServerErrorException(String m){
        super(m);
    }
}
