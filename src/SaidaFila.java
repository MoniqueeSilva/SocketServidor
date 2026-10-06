import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

public class SaidaFila extends OutputStream {

    private final BlockingQueue<String> fila; // Fila onde as mensagens completas serão colocadas
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(); // Buffer em memória para acumular bytes até encontrar

    // Recebe a fila onde as mensagens prontas serão depositadas
    public SaidaFila(BlockingQueue<String> fila) {
        this.fila = fila;
    }

    @Override
    public void write(int b) throws IOException {
        if (b == '\n') {
            fila.add(buffer.toString(StandardCharsets.UTF_8)); // Converte o buffer acumulado de bytes para string 
            buffer.reset();
        } else {
            buffer.write(b);
        }
    }
}
