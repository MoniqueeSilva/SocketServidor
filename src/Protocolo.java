public class Protocolo {

    public static Mensagem separar(String mensagemCompleta) {
        String[] partes = mensagemCompleta.split("\\|");
        if (partes.length < 1 || partes[0].isEmpty()) {
            return null;
        }

        String codigo = partes[0];
        String dados = (partes.length > 1) ? partes[1] : "";
        return new Mensagem(codigo, dados);
    }

    public record Mensagem(String codigo, String dados) {
    }
}
