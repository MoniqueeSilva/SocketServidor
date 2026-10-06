import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GerenciadorConexao implements Runnable {

    private final Socket socketCliente;
    private final BlockingQueue<Socket> filaConexoes; // Fila compartilhada com TODAS as conexões ativas do servidor

    // Recebe o socket do cliente e a fila global de conexões
    public GerenciadorConexao(Socket socketCliente, BlockingQueue<Socket> filaConexoes) {
        this.socketCliente = socketCliente;
        this.filaConexoes = filaConexoes;
    }

    @Override
    public void run() {
        BlockingQueue<String> filaSaida = new LinkedBlockingQueue<>(); // Cria uma fila só para as mensagens de saída
        Thread threadEscritora = new Thread(new Escritora(socketCliente, filaSaida)); // Cria a thread Escritora
        threadEscritora.start();
        try {
            Leitora leitora = new Leitora(socketCliente, filaSaida); // Cria a Leitora passando o socket e a fila de saída
            leitora.executar();
        } catch (IOException e) {
            System.out.println("Erro na conexão com o cliente: " + e.getMessage());
            
        } finally {
            filaSaida.add(Escritora.SENTINELA_FIM); // Coloca a sentinela na fila para a Escritora encerrar
            try {
                threadEscritora.join(1000);
            } catch (InterruptedException e) {}
            
            filaConexoes.remove(socketCliente);
            System.out.println("Cliente removido. Conexões ativas: " + filaConexoes.size());

            try {
                socketCliente.close();

            } catch (IOException e) {}
        }
    }
}
