package br.com.fabricante.addon.services;

import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.comercial.nfe.ServicosNFeHelper2;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.logging.Logger;

public class EmailVendedorDanfeGateway {

    private static final Logger log = Logger.getLogger(EmailVendedorDanfeGateway.class.getName());

    public void enfileirar(DynamicVO notaVO, String destinatario, String assunto, String mensagem) throws Exception {
        if (notaVO == null) {
            throw new IllegalArgumentException("Nota obrigatoria para gerar anexos nativos da NF-e/DANFE.");
        }
        if (isBlank(destinatario)) {
            throw new IllegalArgumentException("Destinatario obrigatorio para enfileirar e-mail com DANFE.");
        }
        if (isBlank(assunto)) {
            throw new IllegalArgumentException("Assunto obrigatorio para enfileirar e-mail com DANFE.");
        }

        log.info("Chamando ServicosNFeHelper2 para gerar anexos nativos NF-e/DANFE. Destinatario="
                + destinatario + ", assunto=" + assunto);

        ServicosNFeHelper2 helper = ServicosNFeHelper2.build();
        Collection<BigDecimal> anexos = helper.buildAnexosEmailNota(notaVO);
        if (anexos == null || anexos.isEmpty()) {
            throw new IllegalStateException("Nenhum anexo nativo de NF-e/DANFE foi retornado para a nota.");
        }

        log.info("Anexos nativos NF-e/DANFE obtidos. Quantidade=" + anexos.size()
                + ". Enfileirando e-mail do vendedor via ServicosNFeHelper2.");

        helper.criaEmailNaFila(destinatario, assunto, mensagem, anexos, null);

        log.info("E-mail com anexos nativos NF-e/DANFE enfileirado. Destinatario="
                + destinatario + ", assunto=" + assunto);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
