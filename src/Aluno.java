
import java.io.Serializable;

public class Aluno implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String matricula;
    private String curso;

    public Aluno() {
    }

    public String getNome() {
        return nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getCurso() {
        return curso;
    }

    @Override
    public String toString() {
        return "Aluno{\n" + nome + "\n" + matricula + "\n" + curso + "\n" + '}';
    }
}
