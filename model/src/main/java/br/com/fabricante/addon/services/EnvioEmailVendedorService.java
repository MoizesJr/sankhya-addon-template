package br.com.fabricante.addon.services;

import br.com.fabricante.addon.config.EnvioEmailVendedorPocConfig;
import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnvioEmailVendedorService {

    private static final Logger log = Logger.getLogger(EnvioEmailVendedorService.class.getName());

    private final EmailVendedorGateway emailGateway;
    private final EmailVendedorIdempotenciaRepository idempotenciaRepository;

    public EnvioEmailVendedorService() {
        this(new EmailVendedorGateway(), new EmailVendedorIdempotenciaRepository());
    }

    EnvioEmailVendedorService(
            EmailVendedorGateway emailGateway,
            EmailVendedorIdempotenciaRepository idempotenciaRepository) {
        this.emailGateway = emailGateway;
        this.idempotenciaRepository = idempotenciaRepository;
    }

    public void processar(BigDecimal nunota) {
        try {
            EntityFacade facade = EntityFacadeFactory.getDWFFacade();
            DynamicVO nota = buscarVO(facade, "CabecalhoNota", nunota);
            if (nota == null) {
                log.warning("CabecalhoNota nao encontrado. NUNOTA=" + nunota);
                return;
            }

            if (!isTopVendaLocal(nota)) {
                log.info("Nota ignorada por TOP diferente da configurada no POC. NUNOTA=" + nunota
                        + ", CODTIPOPER=" + numero(nota, "CODTIPOPER")
                        + ", TOP_CONFIGURADA=" + EnvioEmailVendedorPocConfig.topVendaPermitida());
                return;
            }

            if (!isConfirmada(nota)) {
                log.info("Nota ignorada porque STATUSNOTA nao indica confirmacao. NUNOTA=" + nunota + ", STATUSNOTA=" + texto(nota, "STATUSNOTA"));
                return;
            }

            if (!isMovimentoVenda(nota)) {
                log.info("Nota ignorada porque TIPMOV nao e venda. NUNOTA=" + nunota + ", TIPMOV=" + texto(nota, "TIPMOV"));
                return;
            }

            BigDecimal codVend = numero(nota, "CODVEND");
            if (codVend == null || codVend.signum() <= 0) {
                log.warning("Nota confirmada sem vendedor valido. E-mail nao enviado. NUNOTA=" + nunota);
                return;
            }

            DynamicVO vendedor = buscarVO(facade, "Vendedor", codVend);
            if (vendedor == null) {
                log.warning("Vendedor nao encontrado. E-mail nao enviado. NUNOTA=" + nunota + ", CODVEND=" + codVend);
                return;
            }

            String emailVendedor = texto(vendedor, "EMAIL");
            if (isBlank(emailVendedor)) {
                log.warning("Vendedor sem EMAIL em TGFVEN. Confirmacao nao sera bloqueada. NUNOTA=" + nunota + ", CODVEND=" + codVend);
                return;
            }

            NotaVendaEmailRequest request = new NotaVendaEmailRequest(
                    nunota,
                    numero(nota, "NUMNOTA"),
                    localizarNomeParceiro(facade, nota),
                    numero(nota, "VLRNOTA"),
                    texto(vendedor, "APELIDO"),
                    emailVendedor);

            String assunto = request.getAssunto();
            if (idempotenciaRepository.jaExisteNaFila(facade, emailVendedor, assunto)) {
                log.info("Notificacao de vendedor ja existe na fila. E-mail nao sera duplicado. NUNOTA="
                        + nunota + ", CODVEND=" + codVend + ", EMAIL=" + emailVendedor
                        + ", ASSUNTO=" + assunto);
                return;
            }

            try {
                log.info("Nota validada como confirmada. Enfileirando e-mail do vendedor. NUNOTA=" + nunota + ", CODVEND=" + codVend);
                emailGateway.enfileirar(request);
                log.info("E-mail de confirmacao de nota enfileirado para vendedor. NUNOTA=" + nunota + ", CODVEND=" + codVend);
            } catch (Exception e) {
                log.log(Level.WARNING, "Falha ao enfileirar e-mail do vendedor. Confirmacao nao sera bloqueada. NUNOTA=" + nunota, e);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "Falha ao processar notificacao de vendedor. Confirmacao nao sera bloqueada. NUNOTA=" + nunota, e);
        }
    }

    private boolean isTopVendaLocal(DynamicVO nota) {
        BigDecimal codTipOper = numero(nota, "CODTIPOPER");
        return codTipOper != null && EnvioEmailVendedorPocConfig.topVendaPermitida().compareTo(codTipOper) == 0;
    }

    private boolean isConfirmada(DynamicVO nota) {
        return EnvioEmailVendedorPocConfig.STATUS_NOTA_CONFIRMADA.equalsIgnoreCase(texto(nota, "STATUSNOTA"));
    }

    private boolean isMovimentoVenda(DynamicVO nota) {
        return EnvioEmailVendedorPocConfig.TIPO_MOVIMENTO_VENDA.equalsIgnoreCase(texto(nota, "TIPMOV"));
    }

    private String localizarNomeParceiro(EntityFacade facade, DynamicVO nota) {
        try {
            DynamicVO parceiro = nota.asDymamicVO("Parceiro");
            if (parceiro == null) {
                BigDecimal codParc = numero(nota, "CODPARC");
                parceiro = codParc == null ? null : buscarVO(facade, "Parceiro", codParc);
            }
            return parceiro == null ? "" : texto(parceiro, "NOMEPARC");
        } catch (Exception e) {
            log.log(Level.WARNING, "Nao foi possivel localizar parceiro da nota.", e);
            return "";
        }
    }

    private DynamicVO buscarVO(EntityFacade facade, String entidade, Object pk) throws Exception {
        if (pk == null) {
            return null;
        }
        return (DynamicVO) facade.findEntityByPrimaryKeyAsVO(entidade, new Object[] { pk });
    }

    private BigDecimal numero(DynamicVO vo, String propriedade) {
        try {
            return vo == null ? null : vo.asBigDecimal(propriedade);
        } catch (Exception e) {
            log.log(Level.FINE, "Falha ao ler propriedade numerica " + propriedade + ".", e);
            return null;
        }
    }

    private String texto(DynamicVO vo, String propriedade) {
        try {
            String valor = vo == null ? null : vo.asString(propriedade);
            return valor == null ? "" : valor.trim();
        } catch (Exception e) {
            log.log(Level.FINE, "Falha ao ler propriedade texto " + propriedade + ".", e);
            return "";
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
