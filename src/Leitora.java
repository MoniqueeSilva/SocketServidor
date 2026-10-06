import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class Leitora {

    private final Socket socketCliente;
    private final BlockingQueue<String> filaSaida; // Fila de saída compartilhada com a Escritora para enviar respostas

    public Leitora(Socket socketCliente, BlockingQueue<String> filaSaida) {
        this.socketCliente = socketCliente;
        this.filaSaida = filaSaida;
    }

    public void executar() throws IOException {
        Scanner leDoSocket = new Scanner(socketCliente.getInputStream());
        PrintStream escreveNoSocket = new PrintStream(new SaidaFila(filaSaida), true, StandardCharsets.UTF_8); // Escreve na fila

        while (leDoSocket.hasNextLine()) {
            String mensagemCompleta = leDoSocket.nextLine();
            System.out.println("Cliente [" + socketCliente.getInetAddress().getHostAddress() + "] disse: " + mensagemCompleta);

            Protocolo.Mensagem mensagem = Protocolo.separar(mensagemCompleta); // Separa a mensagem no formato "codigo|dados" via classe Protocolo
            if (mensagem == null) {
                escreveNoSocket.println("ERRO: Formato inválido.");
                continue;
            }

            // Extrai operação e parâmetros da mensagem
            String codigo = mensagem.codigo();
            String dados = mensagem.dados();

            switch (codigo) {
                case "1":
                    Operacoes.somar(dados, escreveNoSocket);
                    break;
                case "2":
                    Operacoes.subtrair(dados, escreveNoSocket);
                    break;
                case "3":
                    Operacoes.multiplicar(dados, escreveNoSocket);
                    break;
                case "4":
                    try {
                        String imagemBase64 = ServicoImagem.obterImagemBase64();

                        escreveNoSocket.println("IMAGEM|" + imagemBase64);

                        System.out.println("Imagem enviada para o cliente.");

                    } catch (IOException e) {
                        escreveNoSocket.println("ERRO: Não foi possível carregar a imagem.");
                        System.out.println("Erro ao carregar imagem: " + e.getMessage());
                    }
                    break;
                case "0":
                    escreveNoSocket.println("Conexão encerrada pelo cliente.");
                    return;
                default:
                    escreveNoSocket.println("Opção inválida.");
            }
        }

        System.out.println("Cliente desconectado: " + socketCliente.getInetAddress().getHostAddress());
    }
}
