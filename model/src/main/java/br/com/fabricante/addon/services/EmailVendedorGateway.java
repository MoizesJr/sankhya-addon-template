package br.com.fabricante.addon.services;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.core.JapeSession.SessionHandle;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;
import br.com.sankhya.modelcore.util.email.FilaMsgUtil;

import java.util.logging.Logger;

public class EmailVendedorGateway {

    private static final Logger log = Logger.getLogger(EmailVendedorGateway.class.getName());

    public void enfileirar(NotaVendaEmailRequest request) throws Exception {
        FilaMsgUtil.Email email = new FilaMsgUtil.Email(
                request.getEmailVendedor(),
                request.getAssunto(),
                request.getMensagem());
        email.setMensagemUnica(true);

        log.info("Chamando FilaMsgUtil para enfileirar e-mail. Destinatario=" + request.getEmailVendedor()
                + ", assunto=" + request.getAssunto());

        SessionHandle hnd = null;
        try {
            hnd = JapeSession.open();
            hnd.execWithTX(new JapeSession.TXBlock() {
                @Override
                public void doWithTx() throws Exception {
                    EntityFacade facade = EntityFacadeFactory.getDWFFacade();
                    FilaMsgUtil.enviaEmail(facade, email);
                }
            });
        } finally {
            JapeSession.close(hnd);
        }

        log.info("E-mail enfileirado via FilaMsgUtil. Destinatario=" + request.getEmailVendedor()
                + ", assunto=" + request.getAssunto());
    }
}
