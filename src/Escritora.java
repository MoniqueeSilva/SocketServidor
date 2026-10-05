import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class Escritora implements Runnable {

    public static final String SENTINELA_FIM = "__FIM__";

    private final Socket socket;
    private final BlockingQueue<String> fila;

    public Escritora(Socket socket, BlockingQueue<String> fila) {
        this.socket = socket;
        this.fila = fila;
    }

    @Override
    public void run() {
        try {
            PrintStream saida = new PrintStream(socket.getOutputStream());
            while (true) {
                String mensagem = fila.take();
                if (SENTINELA_FIM.equals(mensagem)) break;
                saida.println(mensagem);
            }
        } catch (IOException | InterruptedException e) {
        }
    }
}
