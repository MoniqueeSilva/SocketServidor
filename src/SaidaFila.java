import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

public class SaidaFila extends OutputStream {

    private final BlockingQueue<String> fila;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public SaidaFila(BlockingQueue<String> fila) {
        this.fila = fila;
    }

    @Override
    public void write(int b) throws IOException {
        if (b == '\n') {
            fila.add(buffer.toString(StandardCharsets.UTF_8));
            buffer.reset();
        } else {
            buffer.write(b);
        }
    }
}
