package com.example.test.exception;

public class ProcessingException extends RuntimeException {


	private static final long serialVersionUID = -3557751176469677049L;

	public ProcessingException(String message) {
        super(message);
    }
}