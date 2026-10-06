import java.io.PrintStream;

public class Operacoes {

    // Método auxiliar, lê, valida e retorna
    private static int[] lerDoisNumeros(String dados, PrintStream saida) {
        try {
            String[] numeros = dados.split(",");
            if (numeros.length != 2) {
                saida.println("ERRO: Formato inválido. Envie dois números separados por vírgula.");
                return null;
            }

            // Converte os números removendo os espaços e retorna
            int n1 = Integer.parseInt(numeros[0].trim());
            int n2 = Integer.parseInt(numeros[1].trim());
            return new int[] { n1, n2 };
        } catch (NumberFormatException e) {
            saida.println("ERRO: Entrada inválida. Digite apenas números inteiros.");
            return null;
        }
    }

    // Operação de soma 
    public static void somar(String dados, PrintStream saida) {
        int[] nums = lerDoisNumeros(dados, saida);
        if (nums == null)
            return;
        saida.println("Resultado: " + (nums[0] + nums[1]));
    }

    // Operação de subtração 
    public static void subtrair(String dados, PrintStream saida) {
        int[] nums = lerDoisNumeros(dados, saida);
        if (nums == null)
            return;
        saida.println("Resultado: " + (nums[0] - nums[1]));
    }

    // Operaçã de multiplicação
    public static void multiplicar(String dados, PrintStream saida) {
        int[] nums = lerDoisNumeros(dados, saida);
        if (nums == null)
            return;
        saida.println("Resultado: " + (nums[0] * nums[1]));
    }
}
