import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Servidor {

    // 1. Variável para controlar a quantidade máxima de clientes
    private static final int MAX_CLIENTES = 3; // Escalável até 1000

    public static void main(String[] args) throws IOException {
        ServerSocket servidor = new ServerSocket(12345);
        System.out.println("Servidor iniciado. Max de clientes: " + MAX_CLIENTES);

        // 2. Estrutura para armazenar cada conexão feita (capacidade limitada)
        BlockingQueue<Socket> filaConexoes = new ArrayBlockingQueue<>(MAX_CLIENTES);

        while (true) {
            Socket socketCliente = servidor.accept();
            System.out.println("Novo cliente conectado: " + socketCliente.getInetAddress().getHostAddress());

            // 3. Tenta adicionar à fila. offer() retorna false se a fila estiver cheia.
            if (filaConexoes.offer(socketCliente)) {
                System.out.println("Cliente adicionado. Conexões ativas: " + filaConexoes.size() + "/" + MAX_CLIENTES);
                // Envia mensagem de boas-vindas antes de criar a Thread
                socketCliente.getOutputStream().write("OK: Conectado ao servidor.\n".getBytes());

                Thread threadCliente = new Thread(new ClienteHandler(socketCliente, filaConexoes));
                threadCliente.start();
            } else {
                System.out.println(
                        "Servidor cheio! Recusando conexão de: " + socketCliente.getInetAddress().getHostAddress());
                socketCliente.getOutputStream().write("ERRO: Servidor cheio. Tente novamente mais tarde.\n".getBytes());
                socketCliente.close();
            }
        }
    }
}