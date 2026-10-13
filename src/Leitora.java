import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

public class Leitora {

    private final Socket socketCliente;
    private final BlockingQueue<String> filaSaida;

    public Leitora(Socket socketCliente, BlockingQueue<String> filaSaida) {
        this.socketCliente = socketCliente;
        this.filaSaida = filaSaida;
    }

    public void executar() throws IOException {
        DataInputStream entrada =
                new DataInputStream(socketCliente.getInputStream());

        PrintStream saida = new PrintStream(
                new SaidaFila(filaSaida),
                true,
                StandardCharsets.UTF_8
        );

        String mensagemCompleta;

        while ((mensagemCompleta = lerLinha(entrada)) != null) {
            System.out.println(
                    "Cliente [" +
                    socketCliente.getInetAddress().getHostAddress() +
                    "] disse: " + mensagemCompleta
            );

            Protocolo.Mensagem mensagem =
                    Protocolo.separar(mensagemCompleta);

            if (mensagem == null) {
                saida.println("ERRO: Formato inválido.");
                continue;
            }

            String codigo = mensagem.codigo();
            String dados = mensagem.dados();

            switch (codigo) {
                case "1":
                    Operacoes.somar(dados, saida);
                    break;

                case "2":
                    Operacoes.subtrair(dados, saida);
                    break;

                case "3":
                    Operacoes.multiplicar(dados, saida);
                    break;

                case "4":
                    try {
                        String imagemBase64 =
                                ServicoImagem.obterImagemBase64();

                        saida.println("IMAGEM|" + imagemBase64);
                        System.out.println("Imagem enviada para o cliente.");

                    } catch (IOException e) {
                        saida.println(
                                "ERRO: Não foi possível carregar a imagem."
                        );
                        System.out.println(
                                "Erro ao carregar imagem: " + e.getMessage()
                        );
                    }
                    break;

                case "5":
                    receberAluno(dados, entrada, saida);
                    break;

                case "0":
                    saida.println("Conexão encerrada pelo cliente.");
                    return;

                default:
                    saida.println("Opção inválida.");
            }
        }

        System.out.println(
                "Cliente desconectado: " +
                socketCliente.getInetAddress().getHostAddress()
        );
    }

    private void receberAluno(
            String dados,
            DataInputStream entrada,
            PrintStream saida
    ) throws IOException {

        final int tamanho;

        try {
            tamanho = Integer.parseInt(dados.trim());

            if (tamanho <= 0 || tamanho > 1_000_000) {
                saida.println("ERRO: Tamanho do objeto inválido.");
                return;
            }
        } catch (NumberFormatException e) {
            saida.println("ERRO: Tamanho do objeto inválido.");
            return;
        }

        byte[] bytesObjeto = new byte[tamanho];

        // Lê exatamente a quantidade de bytes anunciada pelo cliente.
        entrada.readFully(bytesObjeto);

        try (ObjectInputStream objetoEntrada = new ObjectInputStream(
                new ByteArrayInputStream(bytesObjeto))) {

            Object objeto = objetoEntrada.readObject();

            if (!(objeto instanceof Aluno aluno)) {
                saida.println("ERRO: O objeto recebido não é um Aluno.");
                return;
            }

            System.out.println("Aluno recebido:");
            System.out.println("Nome: " + aluno.getNome());
            System.out.println("Matrícula: " + aluno.getMatricula());
            System.out.println("Curso: " + aluno.getCurso());

            saida.println("Aluno registrado com sucesso!");

        } catch (ClassNotFoundException | RuntimeException e) {
            System.err.println(
                    "Erro ao desserializar aluno: " + e.getMessage()
            );
            saida.println("ERRO: Não foi possível interpretar o aluno.");
        }
    }

    // Lê uma linha sem usar Scanner nem fazer leitura antecipada.
    private String lerLinha(DataInputStream entrada) throws IOException {
        StringBuilder linha = new StringBuilder();

        while (true) {
            int valor = entrada.read();

            if (valor == -1) {
                if (linha.isEmpty()) {
                    return null;
                }
                return linha.toString();
            }

            if (valor == '\n') {
                return linha.toString();
            }

            if (valor != '\r') {
                linha.append((char) valor);
            }
        }
    }
}
