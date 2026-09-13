package br.com.fabricante.addon.config;

import java.math.BigDecimal;

public final class EnvioEmailVendedorPocConfig {

    public static final boolean PERMITIR_ENVIO_COM_ERRO_EVENTO_POC = false;
    public static final BigDecimal TOP_VENDA_PERMITIDA_PADRAO = BigDecimal.valueOf(1101);
    public static final boolean ENVIAR_DANFE_NFE_AUTORIZADA = false;
    public static final String TIPO_MOVIMENTO_VENDA = "V";
    public static final String STATUS_NOTA_CONFIRMADA = "L";
    public static final String STATUS_NFE_AUTORIZADA = "A";

    private EnvioEmailVendedorPocConfig() {
    }

    public static BigDecimal topVendaPermitida() {
        return TOP_VENDA_PERMITIDA_PADRAO;
    }
}