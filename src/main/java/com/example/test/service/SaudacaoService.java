package com.example.test.service;

import org.springframework.stereotype.Service;

@Service
public class SaudacaoService {

    public String gerarSaudacao(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return "Olá! Bem-vindo ao app Swing + Spring Boot!";
        }
        return "Olá, " + nome + "! Processado com sucesso pelo Spring Boot.";
    }
}