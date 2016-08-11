package PackTrabalhoED2;

import java.util.Scanner;

public class TipodeDados {

    public String nomeAtributo;
    public String tipoAtributo;

    static final String BOOLEAN = "BOOLEAN";
    static final String CHAR = "CHAR";
    static final String SHORT = "SHORT";
    static final String INTEIRO = "INT";
    static final String LONGINT = "LONGINT";
    static final String FLOAT = "FLOAT";
    static final String DOUBLE = "DOUBLE";
    static final String STRING = "STRING";
    static final int TAM_STRING_ARQUIVO = 8;

    /**
     * Construtor de Tipo de Dados
     *
     * @param nomeAtributo Nome do atributo
     * @param tipoAtributo Tipo do atributo
     */
    public TipodeDados(String nomeAtributo, String tipoAtributo) {
        this.nomeAtributo = nomeAtributo;
        this.tipoAtributo = tipoAtributo;
    }

    /**
     * Ao ser invocado o metodo realiza as operaçoes de leitura e verificação
     * dos nomes das colunas e seus respectivos tipos
     *
     * @return TipodeDados um objeto do tipoDados preenchido
     */
    public static TipodeDados LeTipoAtributo() {
        Scanner entradaDados = new Scanner(System.in);
        String tipo = null;
        String nome = null;
        do {
            System.out.println("Entre com o nome da coluna");
            nome = entradaDados.nextLine();
        } while (nome.isEmpty());
        do {
            System.out.println("Entre com o tipo da coluna");
            tipo = entradaDados.nextLine().toUpperCase();
        } while (!tipo.equals(TipodeDados.BOOLEAN) && !tipo.equals(TipodeDados.CHAR) && !tipo.equals(TipodeDados.SHORT) && !tipo.equals(TipodeDados.INTEIRO) && !tipo.equals(TipodeDados.FLOAT) && !tipo.equals(TipodeDados.DOUBLE) && !tipo.equals(TipodeDados.STRING));
        return new TipodeDados(nome, tipo);
    }

    /**
     * Este metodo é chamado para realizar a leitura do nome da coluna que irá
     * armazenar a chave primária
     *
     * @return O nome da coluna e o tipo INTEIRO pré definido
     */
    public static TipodeDados LeTipoAtributoChavePrimaria() {
        Scanner entradaDados = new Scanner(System.in);
        String tipo = null;
        String nome = null;
        do {
            System.out.println("Entre com a primeira coluna da tabela");
            System.out.println("A primeira coluna da tabela será a chave primária, portanto seus atributos serão únicos");
            System.out.println("É necessário que o primeiro atributo seja do tipo INTEIRO");
            nome = entradaDados.nextLine();
        } while (nome.isEmpty());
        tipo = TipodeDados.INTEIRO;
        return new TipodeDados(nome, tipo);
    }

}
