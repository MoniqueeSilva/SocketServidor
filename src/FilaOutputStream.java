import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

/*
 * OutputStream customizado que, em vez de escrever em um arquivo ou socket,
 * coloca cada linha completa (terminada por \n) em uma fila.
 * Isso permite que os métodos de negócio continuem usando saida.println(...)
 * sem saber que a escrita real acontece em outra thread.
 */
public class FilaOutputStream extends OutputStream {

    private final BlockingQueue<String> fila;
    private final StringBuilder buffer = new StringBuilder();

    public FilaOutputStream(BlockingQueue<String> fila) {
        this.fila = fila;
    }

    @Override
    public void write(int b) throws IOException {
        char c = (char) b;
        if (c == '\n') {
            fila.add(buffer.toString());  // add() não lança InterruptedException
            buffer.setLength(0);
        } else {
            buffer.append(c);
        }
    }
}