package tp2.net.nicole.gode.service;

public class AuditoriaImpl implements Auditoria {
    @Override
    public void registrarConsulta(String detalhes) {

        System.out.println("Auditoria registrada: " + detalhes);
    }
}