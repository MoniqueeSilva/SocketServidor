import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/*
 * Thread responsável por consumir a fila de mensagens e escrever no socket.
 * Roda em paralelo com a thread leitora (ClienteHandler).
 */
public class Escritor implements Runnable { 

    private final Socket socket;
    private final BlockingQueue<String> fila;

    public Escritor(Socket socket, BlockingQueue<String> fila) {
        this.socket = socket;
        this.fila = fila;
    }

    @Override
    public void run() {
        try {
            PrintStream saida = new PrintStream(socket.getOutputStream());
            while (true) {
                String mensagem = fila.take();      // bloqueia até ter algo
                if ("__FIM__".equals(mensagem)) break;
                saida.println(mensagem);
            }
        } catch (IOException | InterruptedException e) {
            // thread encerrada silenciosamente
        }
    }
}