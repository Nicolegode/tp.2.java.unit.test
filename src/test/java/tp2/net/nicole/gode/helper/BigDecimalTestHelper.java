package tp2.net.nicole.gode.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalTestHelper {

    private static final BigDecimal MARGEM_ERRO_PADRAO = new BigDecimal("0.01");


    public static boolean equalsComMargem(BigDecimal esperado, BigDecimal atual) {
        return equalsComMargem(esperado, atual, MARGEM_ERRO_PADRAO);
    }


    public static boolean equalsComMargem(BigDecimal esperado, BigDecimal atual, BigDecimal margemErro) {
        if (esperado == null && atual == null) {
            return true;
        }
        if (esperado == null || atual == null) {
            return false;
        }

        BigDecimal diferenca = esperado.subtract(atual).abs();
        return diferenca.compareTo(margemErro) <= 0;
    }


    public static void assertEqualsComMargem(BigDecimal esperado, BigDecimal atual) {
        assertEqualsComMargem(esperado, atual, MARGEM_ERRO_PADRAO);
    }


    public static void assertEqualsComMargem(BigDecimal esperado, BigDecimal atual, BigDecimal margemErro) {
        if (!equalsComMargem(esperado, atual, margemErro)) {
            throw new AssertionError(String.format(
                    "Valores não são iguais dentro da margem de erro %.2f. Esperado: %s, Atual: %s, Diferença: %s",
                    margemErro, esperado, atual,
                    esperado != null && atual != null ? esperado.subtract(atual).abs() : "N/A"
            ));
        }
    }


    public static void assertEqualsComMargem(BigDecimal esperado, BigDecimal atual, String mensagem) {
        if (!equalsComMargem(esperado, atual)) {
            throw new AssertionError(String.format(
                    "%s - Esperado: %s, Atual: %s, Diferença: %s",
                    mensagem, esperado, atual,
                    esperado != null && atual != null ? esperado.subtract(atual).abs() : "N/A"
            ));
        }
    }


    public static boolean estaDentroFaixa(BigDecimal valor, BigDecimal minimo, BigDecimal maximo) {
        return estaDentroFaixa(valor, minimo, maximo, MARGEM_ERRO_PADRAO);
    }


    public static boolean estaDentroFaixa(BigDecimal valor, BigDecimal minimo, BigDecimal maximo, BigDecimal margemErro) {
        if (valor == null) return false;

        BigDecimal minimoComMargem = minimo.subtract(margemErro);
        BigDecimal maximoComMargem = maximo.add(margemErro);

        return valor.compareTo(minimoComMargem) >= 0 && valor.compareTo(maximoComMargem) <= 0;
    }


    public static boolean equalsPercentualComMargem(BigDecimal esperado, BigDecimal atual) {
        return equalsComMargem(esperado, atual, new BigDecimal("0.1"));
    }


    public static BigDecimal arredondarParaDuasCasas(BigDecimal valor) {
        if (valor == null) return null;
        return valor.setScale(2, RoundingMode.HALF_UP);
    }


    public static BigDecimal criarValorMonetario(String valor) {
        return new BigDecimal(valor).setScale(2, RoundingMode.HALF_UP);
    }
}