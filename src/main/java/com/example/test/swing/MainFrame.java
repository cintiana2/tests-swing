package com.example.test.swing;


import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.test.service.SaudacaoService;

@Component
public class MainFrame extends JFrame {


	private static final long serialVersionUID = 519720434808907471L;

	private final SaudacaoService saudacaoService;

    private JTextField txtNome;
    private JLabel lblResultado;

    @Autowired
    public MainFrame(SaudacaoService saudacaoService) {
        this.saudacaoService = saudacaoService;
        initUI();
    }

    private void initUI() {
        setTitle("Exemplo Swing com Spring Boot");
        setSize(450, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel Principal
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Componentes Swing
        JLabel lblPrompt = new JLabel("Digite seu nome:");
        txtNome = new JTextField();
        JButton btnEnviar = new JButton("Chamar Serviço Spring");
        lblResultado = new JLabel(" ", SwingConstants.CENTER);

        // Ação do Botão chamando o Serviço do Spring
        btnEnviar.addActionListener(e -> {
            String nome = txtNome.getText();
            // Chamada direta ao Spring Service
            String mensagem = saudacaoService.gerarSaudacao(nome);
            lblResultado.setText(mensagem);
        });

        // Adicionando à interface
        panel.add(lblPrompt);
        panel.add(txtNome);
        panel.add(btnEnviar);
        panel.add(lblResultado);

        add(panel);
    }
}