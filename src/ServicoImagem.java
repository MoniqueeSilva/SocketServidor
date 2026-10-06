import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class ServicoImagem {
    private static final String CAMINHO_IMAGEM = "imagens/imagem.jpg";

    public static String obterImagemBase64() throws IOException {
        Path caminho = Path.of(CAMINHO_IMAGEM); // Cria um Path a partir da string do caminho
        if (!Files.exists(caminho) || Files.size(caminho) == 0) {
            throw new IOException("Arquivo de imagem ausente ou vazio.");
        }
        byte[] imagem = Files.readAllBytes(caminho); // Lê todo o conteúdo do arquivo num array de bytes
        return Base64.getEncoder().encodeToString(imagem); // Codifica os bytes em Base64 e devolve como String
    }
}
