import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) throws IOException {
        // 1. O ServidorSocket é criado uma única vez.
        ServerSocket servidor = new ServerSocket(12345);
        System.out.println("Servidor iniciado. Aguardando conexões...");

        // 2. O Loop Infinito: o servidor nunca para de aceitar clientes.
        while (true) {
            // 3. O accept() bloqueia e espera um cliente. Quando um chega, ele cria o Socket.
            Socket socketCliente = servidor.accept();
            System.out.println("Novo cliente conectado: " + socketCliente.getInetAddress().getHostAddress());

            // 4. A Mágica: Criamos uma nova Thread para este cliente específico.
            // Passamos o socket para o ClienteHandler e iniciamos a Thread.
            // O .start() faz a Thread rodar em paralelo, liberando o servidor para aceitar o próximo cliente.
            Thread threadCliente = new Thread(new ClienteHandler(socketCliente));
            threadCliente.start();
        }
    }
}