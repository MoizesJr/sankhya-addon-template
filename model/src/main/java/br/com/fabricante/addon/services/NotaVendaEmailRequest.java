package br.com.fabricante.addon.services;

import java.math.BigDecimal;

public class NotaVendaEmailRequest {

    private final BigDecimal nunota;
    private final BigDecimal numeroNota;
    private final String nomeParceiro;
    private final BigDecimal valorNota;
    private final String apelidoVendedor;
    private final String emailVendedor;

    public NotaVendaEmailRequest(
            BigDecimal nunota,
            BigDecimal numeroNota,
            String nomeParceiro,
            BigDecimal valorNota,
            String apelidoVendedor,
            String emailVendedor) {
        this.nunota = nunota;
        this.numeroNota = numeroNota;
        this.nomeParceiro = nomeParceiro;
        this.valorNota = valorNota;
        this.apelidoVendedor = apelidoVendedor;
        this.emailVendedor = emailVendedor;
    }

    public String getEmailVendedor() {
        return emailVendedor;
    }

    public String getAssunto() {
        return "Nota fiscal confirmada - NUNOTA " + valor(nunota);
    }

    public String getMensagem() {
        return "Ol\u00e1, " + valor(apelidoVendedor) + ". A nota fiscal n\u00ba "
                + valor(numeroNota) + ", referente ao parceiro " + valor(nomeParceiro)
                + ", foi confirmada. Valor da nota: " + valor(valorNota)
                + ". Esta \u00e9 uma mensagem autom\u00e1tica.";
    }

    private String valor(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}