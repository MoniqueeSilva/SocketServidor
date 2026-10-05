import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GerenciadorConexao implements Runnable {

    private final Socket socketCliente;
    private final BlockingQueue<Socket> filaConexoes;

    public GerenciadorConexao(Socket socketCliente, BlockingQueue<Socket> filaConexoes) {
        this.socketCliente = socketCliente;
        this.filaConexoes = filaConexoes;
    }

    @Override
    public void run() {
        BlockingQueue<String> filaSaida = new LinkedBlockingQueue<>();
        Thread threadEscritora = new Thread(new Escritora(socketCliente, filaSaida));
        threadEscritora.start();
        try {
            Leitora leitora = new Leitora(socketCliente, filaSaida);
            leitora.executar();
        } catch (IOException e) {
            System.out.println("Erro na conexão com o cliente: " + e.getMessage());
        } finally {
            filaSaida.add(Escritora.SENTINELA_FIM);
            try {
                threadEscritora.join(1000);
            } catch (InterruptedException e) {
            }
            filaConexoes.remove(socketCliente);
            System.out.println("Cliente removido. Conexões ativas: " + filaConexoes.size());
            try {
                socketCliente.close();
            } catch (IOException e) {
            }
        }
    }
}
