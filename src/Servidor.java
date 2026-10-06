import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Servidor {

    private static final int MAX_CLIENTES = 3;

    public static void main(String[] args) throws IOException {
        ServerSocket servidor = new ServerSocket(12345);
        System.out.println("Servidor iniciado. Max de clientes: " + MAX_CLIENTES);

        // Cria fila thread e guarda os sockets dos clientes atualmente conectados
        BlockingQueue<Socket> filaConexoes = new ArrayBlockingQueue<>(MAX_CLIENTES);

        while (true) {
            Socket socketCliente = servidor.accept();
            System.out.println("Novo cliente conectado: " + socketCliente.getInetAddress().getHostAddress());

            // Tenta adicionar na fila sem bloquear
            if (filaConexoes.offer(socketCliente)) {
                System.out.println("Cliente adicionado. Conexões ativas: " + filaConexoes.size() + "/" + MAX_CLIENTES);
                socketCliente.getOutputStream().write("OK: Conectado ao servidor.\n".getBytes());

                // Cria uma thread dedicada para gerenciar este cliente, cada cliente tem seu próprio GerenciadorConexao
                Thread threadCliente = new Thread(new GerenciadorConexao(socketCliente, filaConexoes));
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
