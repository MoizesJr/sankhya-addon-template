package br.com.fabricante.addon.treinamento;

import br.com.sankhya.extensions.actionbutton.AcaoRotinaJava;
import br.com.sankhya.extensions.actionbutton.ContextoAcao;
import br.com.sankhya.studio.annotations.hooks.ActionButton;
import br.com.sankhya.studio.annotations.hooks.RefreshTypeEnum;
import br.com.sankhya.studio.annotations.hooks.TransactionType;

@ActionButton(
    description = "Executar treinamento",
    instanceName = "TMP_Atendimento",
    accessControlled = false,
    transactionType = TransactionType.AUTOMATIC,
    refreshType = RefreshTypeEnum.PARENT_ITEM)
public class TreinamentoActionButton implements AcaoRotinaJava {

    @Override
    public void doAction(ContextoAcao contexto) throws Exception {
        contexto.setMensagemRetorno("ActionButton de treinamento executado com sucesso.");
    }
}
