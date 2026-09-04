package br.com.fabricante.addon.treinamento;

import br.com.sankhya.studio.annotations.Service;

@Service(serviceName = "TreinamentoServiceSP")
public class TreinamentoService {

    public String responder(TreinamentoRequestDTO request) throws Exception {
        String mensagem = request == null ? "" : request.getMensagem();
        return "Treinamento Sankhya: " + (mensagem == null ? "" : mensagem);
    }
}
