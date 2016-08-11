package PackTrabalhoED2;

import static PackTrabalhoED2.EncadeamentoExterior.busca;
import static PackTrabalhoED2.EncadeamentoExterior.pegaTodosElementosDaTabela;
import com.sun.org.apache.xerces.internal.xni.XMLDTDHandler;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * O primeiro elemento da tupla é o código O ultimo elemento da tupla é o prox O
 * penultimo elemento da tupla é o flag de deletado
 */
public class Tabela {

    String nomeTabela;
    ArrayList atributosTabela = new ArrayList<>();
    int tamanhoTupla = 0;
    public int numDeRegistrosTotal;
    public int numDeRegistrosNaoDeletados;

    public static final String FINALARQUIVO = "final";

    public Tabela() {

    }

    /**
     * Construtir para ser chamado se A TABELA NÃO EXISTIR
     *
     * @param nome Nome da tabela
     * @param atributosTabela ArrayList contendo os atributos da tabela
     * @throws IOException Erro de I.O.
     */
    public Tabela(String nome, ArrayList<TipodeDados> atributosTabela) throws IOException {
        this.numDeRegistrosNaoDeletados = 0;
        this.nomeTabela = nome;
        this.numDeRegistrosTotal = 0;
        this.atributosTabela = atributosTabela;
        this.tamanhoTupla = EncadeamentoExterior.Calcula_Tamanho(atributosTabela);
        criaArquivoMetaDados();
        CompartimentoHash.criaHash("Hash" + this.nomeTabela + ".dat", 7);
        criaArquivoDados();
    }

    /**
     * Construtor para ser chamado quando A TABELA JÁ EXISTE
     *
     * @param nome Nome da tabela
     * @param atributosTabela ArrayList contendo os atributos da tabela
     * @param numDeRegistrosTotal Numero total de registros
     * @param numDeRegistrosNaoDeletados Numero total de registros que não foram
     * deletados da tabela
     * @throws FileNotFoundException Erro de Arquivo não encontrado
     * @throws IOException Erro de I.O.
     */
    public Tabela(String nome, ArrayList<TipodeDados> atributosTabela, int numDeRegistrosTotal, int numDeRegistrosNaoDeletados) throws FileNotFoundException, IOException {
        this.nomeTabela = nome;
        this.atributosTabela = atributosTabela;
        this.tamanhoTupla = EncadeamentoExterior.Calcula_Tamanho(atributosTabela);
        this.numDeRegistrosNaoDeletados = numDeRegistrosNaoDeletados;
        this.numDeRegistrosTotal = numDeRegistrosTotal;

    }

    /**
     * Cria o arquivo de Metadados da tabela
     *
     * @throws IOException
     */
    private void criaArquivoMetaDados() throws IOException {
        try (DataOutputStream saida = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("Metadados_" + this.nomeTabela + ".dat")))) {
            //Numero de registros
            saida.writeInt(0);
            //Numero de registros que não foram apagados
            saida.writeInt(0);
            for (Object atributosTabela1 : atributosTabela) {
                TipodeDados T = (TipodeDados) atributosTabela1;
                saida.writeUTF(T.nomeAtributo);
                switch (T.tipoAtributo) {
                    case (TipodeDados.BOOLEAN): {
                        saida.writeUTF((TipodeDados.BOOLEAN));
                        break;
                    }
                    case (TipodeDados.CHAR): {
                        saida.writeUTF((TipodeDados.CHAR));
                        break;
                    }
                    case (TipodeDados.SHORT): {
                        saida.writeUTF((TipodeDados.SHORT));
                        break;
                    }
                    case (TipodeDados.INTEIRO): {
                        saida.writeUTF((TipodeDados.INTEIRO));
                        break;
                    }
                    case (TipodeDados.LONGINT): {
                        saida.writeUTF((TipodeDados.LONGINT));
                        break;
                    }
                    case (TipodeDados.FLOAT): {
                        saida.writeUTF((TipodeDados.FLOAT));
                        break;
                    }
                    case (TipodeDados.DOUBLE): {
                        saida.writeUTF((TipodeDados.DOUBLE));
                        break;
                    }
                    case (TipodeDados.STRING): {
                        saida.writeUTF((TipodeDados.STRING));
                        break;
                    }
                }
            }
            saida.writeUTF(FINALARQUIVO);
            saida.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Tabela.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Cria o arquivo de Dados da tabela
     *
     */
    private void criaArquivoDados() {
        RandomAccessFile arquivo = null;
        try {
            arquivo = new RandomAccessFile(new File(this.nomeTabela + ".dat"), "rw");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Tabela.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                arquivo.close();
            } catch (IOException ex) {
                Logger.getLogger(Tabela.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Método utilizado para criar uma tabela Pergunta ao usuário o nome e os
     * atributos da tabela, e então constroi a mesma
     *
     * @return Retorna a tabela construída
     * @throws IOException Erro de I.O.
     */
    public static Tabela ConstroiTabela() throws IOException {
        String nome = null;
        Scanner entradaDados = new Scanner(System.in);
        int i = 0, b;
        boolean quiser;
        List<TipodeDados> atributosTabela = new ArrayList<>();
        do {
            System.out.println("Entre com o nome da Tabela");
            nome = entradaDados.nextLine().toUpperCase();
        } while (nome.isEmpty() && (nome.length() < 30));
        TipodeDados T;
        T = TipodeDados.LeTipoAtributoChavePrimaria();
        atributosTabela.add(T);
        System.out.println("Gostaria de entrar com mais um atributo?(1=sim/0=nao)");
        int opcao = entradaDados.nextInt();
        if (opcao == 1) {
            do {
                atributosTabela.add(TipodeDados.LeTipoAtributo());
                System.out.println("Gostaria de entrar com mais um atributo?(1=sim/0=nao)");
                b = entradaDados.nextInt();
                if (b == 1) {
                    quiser = true;
                } else {
                    quiser = false;
                }
            } while (quiser);
        }
        T = new TipodeDados("Removido", "BOOLEAN");
        atributosTabela.add(T);
        T = new TipodeDados("pont", "INT");
        atributosTabela.add(T);
        return new Tabela(nome, ((ArrayList<TipodeDados>) atributosTabela));
    }

    /**
     * Método utilizado para salvar uma tupla em uma tabela
     */
    public void SalvaTupla() {

        Scanner entradaDados = new Scanner(System.in);
        ArrayList Tupla = new ArrayList();
        int quantidadeAtributos = atributosTabela.size();

        for (int k = 0; k < quantidadeAtributos - 2; k++) {/*Menos 2 por causa dos campos FLAG e PONT*/

            TipodeDados A = (TipodeDados) atributosTabela.get(k);
            System.out.println("Entre com um " + A.tipoAtributo);
            System.out.println("Referente a coluna " + A.nomeAtributo);
            switch (A.tipoAtributo) {
                case (TipodeDados.BOOLEAN): {
                    Tupla.add(entradaDados.nextBoolean());
                    break;
                }
                case (TipodeDados.CHAR): {
                    try {
                        Tupla.add(((char) (System.in.read())));
                    } catch (IOException ex) {
                        Logger.getLogger(Tabela.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
                case (TipodeDados.SHORT): {
                    Tupla.add(entradaDados.nextShort());
                    break;
                }
                case (TipodeDados.INTEIRO): {
                    Tupla.add(entradaDados.nextInt());
                    break;
                }
                case (TipodeDados.LONGINT): {
                    Tupla.add(entradaDados.nextLong());
                    break;
                }
                case (TipodeDados.FLOAT): {
                    Tupla.add(entradaDados.nextFloat());
                    break;
                }
                case (TipodeDados.DOUBLE): {
                    Tupla.add(entradaDados.nextDouble());
                    break;
                }
                case (TipodeDados.STRING): {
                    Tupla.add(entradaDados.next());
                    break;
                }
            }
        }

        //Todos os elementos são inseridos por default como não removidos
        Tupla.add(false);

        //Todos os elementos são inseridos por default com ponteiro para o próximo como NULL, a nao ser que esse campo seja recalculado no metodo abaixo
        Tupla.add(-1);

        try {
            if (EncadeamentoExterior.insereTabela(this, Tupla) == false) {
                System.out.println("Erro: A tupla não foi inserida");
            } else {
                System.out.println("Tupla inserida com sucesso!");
            }

        } catch (Exception ex) {
            Logger.getLogger(Tabela.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Remove uma tupla da tabela
     *
     * @param chavePrim Chave primária que vai ser removida
     */
    public void removeTupla(int chavePrim) {
        try {
            if (EncadeamentoExterior.exclui(this, chavePrim) == true) {
                System.out.println("Remoção efetuada com suceso!");
            } else {
                System.out.println("Remoção não pode ser efetuada: A tupla digitada não foi encontrada na tabela!");
            }
        } catch (Exception ex) {
            Logger.getLogger(Tabela.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Imprime todos os atributos de uma tabela
     */
    public void imprimeAtributos() {

        for (int k = 0; k < this.atributosTabela.size() - 2; k++) {
            TipodeDados tipo = (TipodeDados) this.atributosTabela.get(k);
            System.out.println("Atributo: " + tipo.nomeAtributo + " do tipo " + tipo.tipoAtributo);
        }

    }

    /**
     * Imprime toda a tabela, ou seja, todos seus atributos e todas suas tuplas
     *
     */
    public void imprimeTodaTabela() {
        for (int k = 0; k < this.atributosTabela.size() - 2; k++) {
            TipodeDados tipo = (TipodeDados) this.atributosTabela.get(k);
            System.out.print("Atributo: " + tipo.nomeAtributo + " do tipo " + tipo.tipoAtributo);
            System.out.println();
        }
        try {
            RandomAccessFile dados = new RandomAccessFile(new File(this.nomeTabela + ".dat"), "r");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Tabela.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            int x = 0;
            while (true) {
                ArrayList retorno = CompartimentoHash.le(this, x);
                for (int k = 0; k < retorno.size(); k++) {
                    System.out.println(retorno.get(k));
                    x++;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Tabela.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Verifica se a entrada é um atributo da tabela
     *
     * @param nomeAtributo A string que vai ser verificada
     * @return Se não encontrou, retorna -1. Se encontrou, retorna o indice do
     * atributo no ArrayList
     */
    public int ehAtributoTabela(String nomeAtributo) {
        for (int k = 0; k < this.atributosTabela.size() - 2; k++) {
            TipodeDados tipo = (TipodeDados) this.atributosTabela.get(k);
            if (tipo.nomeAtributo.equalsIgnoreCase(nomeAtributo)) {
                return k;
            }
        }
        return -1;
    }

    /**
     * Realiza a busca com condições na tabela. Método recursivo.
     *
     * @param S String com a expressão de busca
     * @return Um ArrayList com todas as tuplas que foram localizadas pela busca
     */
    public ArrayList buscaComCondicoesVariadas(String S) {
        if (S.contains(" AND ") && S.contains(" OR ")) {
            ArrayList primeiro = new ArrayList();
            ArrayList segundo = new ArrayList();

            int posicaoAND;
            int posicaoOR;
            if ((posicaoAND = S.indexOf(" AND ")) < (posicaoOR = S.indexOf(" OR "))) {

                primeiro = buscaComCondicoesVariadas(S.substring(0, posicaoAND));
                segundo = buscaComCondicoesVariadas(S.substring(posicaoAND + 4, S.length()));
                for (int i = 0; i < primeiro.size(); i++) {
                    if (!segundo.contains(primeiro.get(i))) {
                        primeiro.remove(i);
                        i--;//Pra evitar problemas no size

                    }
                }
                return primeiro;
            } else {
                primeiro = buscaComCondicoesVariadas(S.substring(0, posicaoOR));
                segundo = buscaComCondicoesVariadas(S.substring(posicaoOR + 3, S.length()));
                for (int i = 0; i < segundo.size(); i++) {
                    if (!primeiro.contains(segundo.get(i))) {
                        primeiro.add(segundo.get(i));
                    }
                }
                return primeiro;
            }
        } else if (S.contains(
                " AND ")) {
            return EncadeamentoExterior.buscaComAND(S, this);
        } else if (S.contains(
                " OR ")) {
            return EncadeamentoExterior.buscaComOR(S, this);
        }
        try {
            //Se não contém nenhum operador lógico (AND / OR)

            return EncadeamentoExterior.buscaNaTabelaToda(this, S);
        } catch (IOException ex) {
            Logger.getLogger(Tabela.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        //      return /*null*/ new ArrayList();
    }

    /**
     * Realiza a busca com condição Faz a leitura da expressão de busca, vê como
     * esta expressão se classifica e faz chamadas para outros métodos
     * realizarem a busca. No final, pega o retorno dos outros métodos e imprime
     * as tuplas retornadas.
     *
     */
    public void buscaComCondicao() {

        Scanner entrada = new Scanner(System.in);
        String S;

        System.out.println("Atributos da tabela:");
        this.imprimeAtributos();
        System.out.println("---------------------");
        System.out.println("Entre com a expressão para a busca por campo");
        System.out.println("A expressão deve contar um atributo da tabela e um operador lógico");
        System.out.println("duas expressões podem ser unidas com um AND ou um OR");
        System.out.println("Exemplo de expressão: idade > 30 OR idade < 5");
        System.out.println("Digite TUDO para imprimir a tabela inteira");
        S = entrada.nextLine();
        S = S.toUpperCase();
        ArrayList Tuplas;
        ArrayList TuplasTratadas = new ArrayList();
        String[] condicaoDaBusca;
        String[] condicaoDaBuscaTratada;

        if (S.equals("TUDO")) {
            TuplasTratadas = EncadeamentoExterior.pegaTodosElementosDaTabela(this);
        } else {
            if (!S.contains(" AND ") && !S.contains(" OR ")) {
                try {
                    TuplasTratadas = EncadeamentoExterior.buscaNaTabelaToda(this, S);

                } catch (IOException ex) {
                    Logger.getLogger(Tabela.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (S.contains(" AND ") &&/*E*/ S.contains(" OR ")) {
                TuplasTratadas = buscaComCondicoesVariadas(S);

            } else if (S.contains(" AND ")) {
                TuplasTratadas = EncadeamentoExterior.buscaComAND(S, this);
            } else if (S.contains(" OR ")) {
                TuplasTratadas = EncadeamentoExterior.buscaComOR(S, this);
            }
        }

        ArrayList Tupla;
        TipodeDados TDD;
        for (Object Tupla1 : TuplasTratadas) {
            Tupla = (ArrayList) Tupla1;
            System.out.println("");
            for (int j = 0; j < Tupla.size() - 2; j++) {
                TDD = (TipodeDados) this.atributosTabela.get(j);
                System.out.print(TDD.nomeAtributo);
                System.out.print(" : ");
                System.out.print(Tupla.get(j));;
                System.out.print(" | ");
            }
            System.out.println();
        }
        System.out.println("");

    }

    /**
     * Realiza a busca por chave primária
     *
     * @param chave a chave primária que será buscada
     */
    public void buscaPorChavePrimaria(int chave) {
        int retorno_da_busca;
        try {
            retorno_da_busca = busca(this, chave);
            ArrayList TuplaLida = new ArrayList();
            if ((retorno_da_busca == -2) || (retorno_da_busca == -1)) { // nao existe nenhum dado naquela posicao hash
                System.out.println("O elemento buscado não existe na tabela!");
            } else {

                TuplaLida = (ArrayList) CompartimentoHash.le(this, retorno_da_busca);
                boolean flag = (boolean) TuplaLida.get(this.atributosTabela.size() - 2);
                if (flag) {//Se está liberado
                    System.out.println("O elemento buscado não existe na tabela!");
                } else {
                    TipodeDados TDD;
                    System.out.println();//Pula uma linha, para ajudar a visualização da tupla
                    for (int j = 0; j < TuplaLida.size() - 2; j++) {
                        TDD = (TipodeDados) this.atributosTabela.get(j);
                        System.out.print(TDD.nomeAtributo);
                        System.out.print(" : ");
                        System.out.print(TuplaLida.get(j));;
                        System.out.print(" | ");

                    }

                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Tabela.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Atualiza o numDeRegistrosTotal e o numDeRegistrosNaoDeletados arquivo de
     * Metadados
     *
     */
    public void atualizaMetadadosTabela() {
        try (RandomAccessFile metadados = new RandomAccessFile(new File("Metadados_" + this.nomeTabela + ".dat"), "rw");) {
            metadados.writeInt(this.numDeRegistrosTotal);
            metadados.writeInt(this.numDeRegistrosNaoDeletados);
            metadados.close();

        } catch (IOException ex) {
            Logger.getLogger(Tabela.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Método utilizado para atualizar uma tupla específica de uma tabela
     *
     * @param chavePrim A chave primária da chave que será atualizada
     */
    void atualizaTupla(int chavePrim) {
        try {
            Scanner entradaDados = new Scanner(System.in);
            int retorno_da_busca = busca(this, chavePrim);
            if ((retorno_da_busca == -2) || (retorno_da_busca == -1)) { // nao existe nenhum dado naquela posicao hash
                System.out.println("O elemento buscado não existe na tabela!");
            } else {
                ArrayList TuplaLida = new ArrayList();
                TuplaLida = (ArrayList) CompartimentoHash.le(this, retorno_da_busca);
                boolean flag = (boolean) TuplaLida.get(this.atributosTabela.size() - 2);
                if (flag) {//Se está liberado
                    System.out.println("O elemento buscado não existe na tabela!");
                } else {
                    TipodeDados TDD;
                    System.out.println();//Pula uma linha, para ajudar a visualização da tupla
                    System.out.println("Atributos da tabela: ");
                    this.imprimeAtributos();
                    System.out.println("----------");
                    System.out.println("Entre com o nome do atributo que deseja atualizar");
                    String nomeAtributo = entradaDados.next();
                    int indice;
                    if ((indice = this.ehAtributoTabela(nomeAtributo)) > 0) {//Caso a string que o usuário digitou seja um atributo da tabela
                        TipodeDados A = (TipodeDados) atributosTabela.get(indice);

                        System.out.println("Entre com um " + A.tipoAtributo);
                        System.out.println("Referente a coluna " + A.nomeAtributo);
                        switch (A.tipoAtributo) {
                            case (TipodeDados.BOOLEAN): {
                                TuplaLida.set(indice, entradaDados.nextBoolean());
                                break;
                            }
                            case (TipodeDados.CHAR): {
                                try {
                                    TuplaLida.set(indice, ((char) (System.in.read())));

                                } catch (IOException ex) {
                                    Logger.getLogger(Tabela.class
                                            .getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                            }
                            case (TipodeDados.SHORT): {
                                TuplaLida.set(indice, entradaDados.nextShort());
                                break;
                            }
                            case (TipodeDados.INTEIRO): {
                                TuplaLida.set(indice, entradaDados.nextInt());
                                break;
                            }
                            case (TipodeDados.LONGINT): {
                                TuplaLida.set(indice, entradaDados.nextLong());
                                break;
                            }
                            case (TipodeDados.FLOAT): {
                                TuplaLida.set(indice, entradaDados.nextFloat());
                                break;
                            }
                            case (TipodeDados.DOUBLE): {
                                TuplaLida.set(indice, entradaDados.nextDouble());
                                break;
                            }
                            case (TipodeDados.STRING): {
                                TuplaLida.set(indice, entradaDados.next());
                                break;
                            }
                        }

                        CompartimentoHash.salva(this, TuplaLida, retorno_da_busca);
                    } else {
                        System.out.println("O atributo digitado não existe, ou não pode ser modificado por se tratar de uma chave primária!");

                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Tabela.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Realiza a carga de dados de um arquivo binário .dat para uma tabela
     * existente.
     *
     *
     * @param caminho Caminho ou nome do arquivo binário onde estão os dados
     * @throws IOException Gera erros de arquivo não encontrado ou fim de
     * arquivo
     */
    public void cargaDeDados(String caminho) throws IOException {
        DataInputStream entradaDados;
        try {
            entradaDados = new DataInputStream(new BufferedInputStream(new FileInputStream(caminho)));
            int quantidadeAtributos = atributosTabela.size();
            ArrayList Tupla = new ArrayList();
            while (true) {
                for (int k = 0; k < quantidadeAtributos - 2; k++) {/*Menos 2 por causa dos campos FLAG e PONT*/


                    TipodeDados A = (TipodeDados) atributosTabela.get(k);

                    switch (A.tipoAtributo) {
                        case (TipodeDados.BOOLEAN): {
                            Tupla.add(entradaDados.readBoolean());
                            break;
                        }
                        case (TipodeDados.CHAR): {
                            Tupla.add((entradaDados.readChar()));
                            break;
                        }
                        case (TipodeDados.SHORT): {
                            Tupla.add(entradaDados.readShort());
                            break;
                        }
                        case (TipodeDados.INTEIRO): {
                            Tupla.add(entradaDados.readInt());
                            break;
                        }
                        case (TipodeDados.LONGINT): {
                            Tupla.add(entradaDados.readLong());
                            break;
                        }
                        case (TipodeDados.FLOAT): {
                            Tupla.add(entradaDados.readFloat());
                            break;
                        }
                        case (TipodeDados.DOUBLE): {
                            Tupla.add(entradaDados.readDouble());
                            break;
                        }
                        case (TipodeDados.STRING): {
                            String S = entradaDados.readUTF();
                            Tupla.add(S.replaceAll(" ", ""));
                            break;
                        }
                    }

                }
                //Todos os elementos são inseridos por default como não removidos
                Tupla.add(false);
                //Todos os elementos são inseridos por default com ponteiro para o próximo como NULL, a nao ser que esse campo seja recalculado no metodo abaixo
                Tupla.add(-1);

                try {
                    EncadeamentoExterior.insereTabela(this, Tupla);
                } catch (Exception ex) {
                    Logger.getLogger(Tabela.class.getName()).log(Level.SEVERE, null, ex);
                }
                Tupla = new ArrayList();
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Arquivo não encontrado");
            Logger.getLogger(Tabela.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
