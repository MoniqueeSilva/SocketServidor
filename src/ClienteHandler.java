import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// 1. Implementamos a interface Runnable.
public class ClienteHandler implements Runnable {

    private Socket socketCliente;
    private BlockingQueue<Socket> filaConexoes;
    private BlockingQueue<String> filaSaida;

    public ClienteHandler(Socket socketCliente, BlockingQueue<Socket> filaConexoes) {
        this.socketCliente = socketCliente;
        this.filaConexoes = filaConexoes;
        this.filaSaida = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        new Thread(new Escritor(socketCliente, filaSaida)).start();
        try {
            Scanner leDoSocket = new Scanner(socketCliente.getInputStream());
            PrintStream escreveNoSocket = new PrintStream(new FilaOutputStream(filaSaida));

            while (leDoSocket.hasNextLine()) {
                String mensagemCompleta = leDoSocket.nextLine();
                System.out.println(
                        "Cliente [" + socketCliente.getInetAddress().getHostAddress() + "] disse: " + mensagemCompleta);

                // 1. Separar o protocolo em CODIGO e DADOS
                String[] partes = mensagemCompleta.split("\\|");
                if (partes.length < 1 || partes[0].isEmpty()) {
                    escreveNoSocket.println("ERRO: Formato inválido.");
                    continue;
                }

                String codigo = partes[0];
                String dados = (partes.length > 1) ? partes[1] : "";

                // 2. Roteamento baseado no código
                switch (codigo) {
                    case "1":
                        realizarSoma(dados, escreveNoSocket);
                        break;
                    case "2":
                        realizarSubtracao(dados, escreveNoSocket);
                        break;
                    case "3":
                        realizarMultiplicacao(dados, escreveNoSocket);
                        break;
                    case "4":
                        try {
                            String imagemBase64 = ImagemService.obterImagemBase64();

                            escreveNoSocket.println("IMAGEM|" + imagemBase64);

                            System.out.println("Imagem enviada para o cliente.");

                        } catch (IOException e) {
                            escreveNoSocket.println("ERRO: Não foi possível carregar a imagem.");
                            System.out.println("Erro ao carregar imagem: " + e.getMessage());
                        }
                        break;
                    case "0":
                        escreveNoSocket.println("Conexão encerrada pelo cliente.");
                        leDoSocket.close();
                        escreveNoSocket.close();
                        socketCliente.close();
                        return;
                    default:
                        escreveNoSocket.println("Opção inválida.");
                }
            }

            System.out.println("Cliente desconectado: " + socketCliente.getInetAddress().getHostAddress());
            leDoSocket.close();
            escreveNoSocket.close();
            socketCliente.close();

        } catch (IOException e) {
            System.out.println("Erro na conexão com o cliente: " + e.getMessage());
        } finally {
            filaSaida.add("__FIM__");
            // Remove o socket da fila ao desconectar, liberando vaga
            filaConexoes.remove(socketCliente);
            System.out.println("Cliente removido. Conexões ativas: " + filaConexoes.size());
        }
    }

    // ==================== MÉTODO AUXILIAR ====================

    /*
     * Lê e valida os dois números enviados no formato "NUM1,NUM2".
     * Retorna um array com os dois inteiros, ou null se houver erro
     * (nesse caso, já envia a mensagem de erro ao cliente).
     */
    private int[] lerDoisNumeros(String dados, PrintStream saida) {
        try {
            String[] numeros = dados.split(",");
            if (numeros.length != 2) {
                saida.println("ERRO: Formato inválido. Envie dois números separados por vírgula.");
                return null;
            }
            int n1 = Integer.parseInt(numeros[0].trim());
            int n2 = Integer.parseInt(numeros[1].trim());
            return new int[] { n1, n2 };
        } catch (NumberFormatException e) {
            saida.println("ERRO: Entrada inválida. Digite apenas números inteiros.");
            return null;
        }
    }

    // ==================== OPERAÇÕES ====================

    private void realizarSoma(String dados, PrintStream saida) {
        int[] nums = lerDoisNumeros(dados, saida);
        if (nums == null)
            return;
        saida.println("Resultado: " + (nums[0] + nums[1]));
    }

    private void realizarSubtracao(String dados, PrintStream saida) {
        int[] nums = lerDoisNumeros(dados, saida);
        if (nums == null)
            return;
        saida.println("Resultado: " + (nums[0] - nums[1]));
    }

    private void realizarMultiplicacao(String dados, PrintStream saida) {
        int[] nums = lerDoisNumeros(dados, saida);
        if (nums == null)
            return;
        saida.println("Resultado: " + (nums[0] * nums[1]));
    }
}