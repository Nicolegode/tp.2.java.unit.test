package tp2.net.nicole.gode.service;

public class ReembolsoNaoAutorizadoException extends RuntimeException {
    public ReembolsoNaoAutorizadoException(String mensagem) {
        super(mensagem);
    }
}