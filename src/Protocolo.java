// Classe responsável por interpretar o protocolo de mensagens do sistema
public class Protocolo {

    // Método que SEPARA uma mensagem completa
    public static Mensagem separar(String mensagemCompleta) {
        String[] partes = mensagemCompleta.split("\\|");
        if (partes.length < 1 || partes[0].isEmpty()) {
            return null;
        }

        String codigo = partes[0]; // O código da operação é sempre a primeira parte
        String dados = (partes.length > 1) ? partes[1] : ""; // Os dados são a segunda parte
        return new Mensagem(codigo, dados);
    }

    public record Mensagem(String codigo, String dados) {
    }
}
