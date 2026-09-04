package br.com.fabricante.addon.treinamento;

public class TreinamentoRequestDTO {

    private String mensagem;
    private Integer codigo;

    public TreinamentoRequestDTO() {
    }

    public TreinamentoRequestDTO(String mensagem, Integer codigo) {
        this.mensagem = mensagem;
        this.codigo = codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }
}
