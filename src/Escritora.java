import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class Escritora implements Runnable {

    public static final String SENTINELA_FIM = "__FIM__"; // Mensagem especial que indica "encerre a thread"

    private final Socket socket;
    private final BlockingQueue<String> fila; // Fila compartilhada de mensagens a serem enviadas

    // Recebe socket e fila para trabalhar
    public Escritora(Socket socket, BlockingQueue<String> fila) {
        this.socket = socket;
        this.fila = fila;
    }

    @Override
    public void run() {
        try {
            PrintStream saida = new PrintStream(socket.getOutputStream()); // Para enviar bytes
            while (true) {
                String mensagem = fila.take(); // Bloqueia até haver mensagem na fila 
                if (SENTINELA_FIM.equals(mensagem)) break;
                saida.println(mensagem);
            }
        } catch (IOException | InterruptedException e) {
        }
    }
}
