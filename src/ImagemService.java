import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class ImagemService {
    private static final String CAMINHO_IMAGEM = "imagens/imagem.jpg";

    public static String obterImagemBase64() throws IOException {
        Path caminho = Path.of(CAMINHO_IMAGEM);
        if (!Files.exists(caminho) || Files.size(caminho) == 0) {
            throw new IOException("Arquivo de imagem ausente ou vazio.");
        }
        byte[] imagem = Files.readAllBytes(caminho);
        return Base64.getEncoder().encodeToString(imagem);
    }

    // Para testar sem Socket
    public static void main(String[] args) {
        try {
            String base64 = obterImagemBase64();
            System.out.println("Imagem convertida com sucesso!");
            System.out.println("Tamanho Base64: " + base64.length());

        } catch (IOException e) {
            System.out.println("Erro ao ler imagem: " + e.getMessage());
        }
    }
}