package br.com.fabricante.addon.callbacks;

import br.com.fabricante.addon.config.EnvioEmailVendedorPocConfig;
import br.com.fabricante.addon.services.EnvioEmailVendedorService;
import br.com.sankhya.modelcore.custommodule.ICustomCallBack;
import br.com.sankhya.studio.annotations.hooks.Callback;
import br.com.sankhya.studio.annotations.hooks.CallbackEvent;
import br.com.sankhya.studio.annotations.hooks.CallbackWhen;

import java.math.BigDecimal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Callback(
        when = CallbackWhen.AFTER,
        event = CallbackEvent.PROCESS_CONFIRMATION,
        description = "Notificar vendedor apos confirmacao de venda local")
public class NotaVendaConfirmadaCallback implements ICustomCallBack {

    private static final Logger log = Logger.getLogger(NotaVendaConfirmadaCallback.class.getName());

    private final EnvioEmailVendedorService service = new EnvioEmailVendedorService();

    @Override
    public Object call(String id, Map<String, Object> data) {
        try {
            if (data == null) {
                log.warning("PROCESS_CONFIRMATION AFTER chamado sem dados.");
                return null;
            }

            if (data.get("error") != null) {
                if (!EnvioEmailVendedorPocConfig.PERMITIR_ENVIO_COM_ERRO_EVENTO_POC) {
                    log.warning("Evento retornou erro. E-mail do vendedor nao sera enviado porque a tolerancia POC esta desativada.");
                    return null;
                }
                log.warning("Evento retornou erro, mas POC local continuara porque a flag de tolerancia esta ativa e a nota sera validada pelo STATUSNOTA.");
            }

            BigDecimal nunota = paraBigDecimal(data.get("nunota"));
            if (nunota == null) {
                log.warning("Nao foi possivel converter a chave oficial nunota do callback.");
                return null;
            }

            service.processar(nunota);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Falha no callback de e-mail ao vendedor. Confirmacao nao sera bloqueada.", e);
        }
        return null;
    }

    private BigDecimal paraBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).longValue());
        }
        try {
            return new BigDecimal(String.valueOf(value).trim());
        } catch (Exception ignored) {
            return null;
        }
    }
}
