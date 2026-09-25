import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

// 1. Implementamos a interface Runnable. Isso diz ao Java que essa classe pode ser executada por uma Thread.
public class ClienteHandler implements Runnable {

    private Socket socketCliente;

    // 2. O construtor recebe o Socket do cliente que foi aceito pelo Servidor.
    public ClienteHandler(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

    // 3. O método run() é o que a Thread vai executar quando for iniciada.
    @Override
    public void run() {
        try {
            // A lógica de leitura e escrita que antes ficava no main agora fica aqui.
            Scanner leDoSocket = new Scanner(socketCliente.getInputStream());
            PrintStream escreveNoSocket = new PrintStream(socketCliente.getOutputStream());

            // Enquanto o cliente estiver conectado e enviando dados...
            while (leDoSocket.hasNextLine()) {
                String mensagem = leDoSocket.nextLine();
                System.out.println("Cliente [" + socketCliente.getInetAddress().getHostAddress() + "] disse: " + mensagem);

                // Responde para o cliente
                escreveNoSocket.println("Resposta da mensagem: " + mensagem + " = BLZ!!");
            }

            // Se sair do while, o cliente desconectou. Fechamos os recursos.
            System.out.println("Cliente desconectado: " + socketCliente.getInetAddress().getHostAddress());
            leDoSocket.close();
            escreveNoSocket.close();
            socketCliente.close();

        } catch (IOException e) {
            // Tratamento de erro caso a conexão caia de forma inesperada.
            System.out.println("Erro na conexão com o cliente: " + e.getMessage());
        }
    }
}