package PackTrabalhoED2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControleTabelas {

    ArrayList tabelas = new ArrayList<>();

    /**
     * Construtor do ControleTabelas. É executado na inicialização do programa
     * Realiza uma leitura, procurando as tabelas existentes Caso existam
     * tabelas, as informações sobre elas são carregadas na memória
     */
    public ControleTabelas() {
        try {
            this.LeTabelasExistentes();
        } catch (IOException ex) {
            Logger.getLogger(ControleTabelas.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Método de controle para listar todos os atributos de uma tabela. Lê o
     * nome da tabela, e verifica se existe. Caso exista, a função
     * imprimeAtributos() da classe Tabela é chamada. Caso não exista, imprime
     * "Tabela Inexistente!"
     *
     */
    public void listaAtributosDeUmaDasTabela() {
        Scanner entradaDados = new Scanner(System.in);
        System.out.println("Listar os atributos de uma tabela!");
        System.out.println("Entre com o nome da tabela");
        String nomeTabela = entradaDados.next();
        int indice = this.BuscaTabela(nomeTabela);
        if (indice == -1) {
            System.out.println("Tabela Inexistente!");
        } else {
            Tabela T = (Tabela) this.tabelas.get(indice);
            T.imprimeAtributos();
        }

    }

    /**
     * Método de controle para remover tuplas de uma tabela. Lê o nome da
     * tabela, verifica se está correto. Continua se estiver correto e se a
     * quantidade de tuplas existentes na tabela for maior do que zero.
     * Satisfazendo estas condições, é lida a chave primária que o usuário
     * deseja remover e é feita uma chamada para o removeTupla() da classe
     * tabela
     */
    public void removeTuplaEmUmaDasTabelas() {
        Scanner entradaDados = new Scanner(System.in);
        System.out.println("Remover tupla!");
        System.out.println("Entre com o nome da tabela que deseja realizar a remoção");
        String nomeTabela = entradaDados.next();
        int indice = this.BuscaTabela(nomeTabela);
        Tabela T;
        if (indice == -1) {
            System.out.println("Tabela Inexistente!");
        } else {
            T = (Tabela) this.tabelas.get(indice);
            if (T.numDeRegistrosTotal > 0) {
                System.out.println("Digite a chave primária da tupla que deseja remover");
                int chavePrim = entradaDados.nextInt();
                T.removeTupla(chavePrim);
            } else {
                System.out.println("Não existe nenhuma tupla nesta tabela!");
            }
        }
    }

    /**
     * Método de controle para atualizar tuplas de uma tabela. Lê o nome da
     * tabela, verifica se está correto. Continua se estiver correto. Lê a chave
     * primária que o usuário deseja atualizar e é feita uma chamada para o
     * atualizaTupla() da classe tabela
     */
    public void atualizaTuplaEmUmaDasTabelas() {
        Scanner entradaDados = new Scanner(System.in);
        System.out.println("Atualizar tupla!");
        System.out.println("Entre com o nome da tabela que deseja realizar a atualização");
        String nomeTabela = entradaDados.next();
        int indice = this.BuscaTabela(nomeTabela);
        Tabela T;
        if (indice == -1) {
            System.out.println("Tabela Inexistente!");
        } else {
            T = (Tabela) this.tabelas.get(indice);
            System.out.println("Digite a chave primária da tupla que deseja atualizar");
            int chavePrim = entradaDados.nextInt();
            T.atualizaTupla(chavePrim);
            System.out.println("Atualização realizada com sucesso");
        }
    }

    /**
     * Método de controle para buscar tuplas de uma tabela. Lê o nome da tabela,
     * verifica se está correto e então verifica se o numero de registros é
     * maior que 0. Passando por estas condições, pergunta ao usuário se ele
     * deseja realizar busca por chave primária(1) ou por campo(2). Lê a
     * resposta. Caso 1-Chave primária: Lê a chave primária e passa ela como
     * parâmetro para o buscaPorChavePrimaria de tabela Caso 2-Busca por campo:
     * Chama o buscaComCondicao() de tabela, onde as próximas leituras e a busca
     * em si serão feitas.
     *
     */
    public void buscaTuplaEmUmaDasTabelas() {
        Scanner entradaDados = new Scanner(System.in);
        System.out.println("Busca!");
        System.out.println("Entre com o nome da tabela que deseja realizar a busca");
        String nomeTabela = entradaDados.next();
        int indice = this.BuscaTabela(nomeTabela);
        Tabela T;
        if (indice == -1) {
            System.out.println("Tabela Inexistente!");
        } else {
            T = (Tabela) this.tabelas.get(indice);

            if (T.numDeRegistrosTotal > 0) {

                System.out.println("1-Pesquisar por chave primária");
                System.out.println("2-Pesquisar por campo");
                int leitura = entradaDados.nextInt();

                if (leitura == 1) {//Se for chave primária

                    System.out.println("Digite a chave primária que deseja buscar");
                    T.buscaPorChavePrimaria(entradaDados.nextInt());

                } else if (leitura == 2) {//Se for por campo
                    T.buscaComCondicao();
                }
            } else {
                System.out.println("Não existe nenhuma tupla nesta tabela!");
            }
        }
    }

    /**
     * Método para realizar o controle da trasnferencia de dados de um arquivo
     * dat em disco para uma tabela deste programa. Pergunta a tabela que o
     * usuário deseja carregar e o nome do arquivo, que deve ser informado COM
     * SUA EXTENSÃO, e deve estar localziado na pasta do projeto deste programa.
     *
     * Exemplo de nome de arquivo: "dadosIniciais.dat"
     *
     * Após estas leituras, chama o método cargaDeDados, de tabela
     */
    public void cargaDeDadosEmUmaDasTabelas() {
        Scanner entradaDados = new Scanner(System.in);
        System.out.println("Carga de dados!");
        System.out.println("Entre com o nome da tabela que deseja realizar a Carga de Dados");
        String nomeTabela = entradaDados.next();
        int indice = this.BuscaTabela(nomeTabela);
        Tabela T;
        if (indice == -1) {
            System.out.println("Tabela Inexistente!");
        } else {
            T = (Tabela) this.tabelas.get(indice);
            System.out.println("Entre com o nome do arquivo");
            String nomeArquivo = entradaDados.next();
            try {
                T.cargaDeDados(nomeArquivo);
            } catch (IOException ex) {
                Logger.getLogger(ControleTabelas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Método para realizar o controle da inserção de uma tupla em uma tabela Lê
     * o nome da tabela e verifica se ela existe. Caso sim, chama o SalvaTupla
     * da classe Tabela
     */
    public void insereTuplaEmUmaDasTabelas() {
        Scanner entradaDados = new Scanner(System.in);
        System.out.println("Inserir tupla na tabela");
        System.out.println("Entre com o nome da tabela que deseja inserir uma tupla");
        String nomeTabela = entradaDados.next();
        int indice = this.BuscaTabela(nomeTabela);
        if (indice == -1) {
            System.out.println("Tabela Inexistente!");
        } else {
            Tabela T = (Tabela) this.tabelas.get(indice);
            try {
                T.SalvaTupla();
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * LeTabelasExistentes no arquivo Info_das_Tabelas
     *
     * @throws java.io.IOException Trata a exceção de fim de arquivo (Não ocorre
     * nunca pois é usado um marcador de fim de arquivo)
     * @throws FileNotFoundException Trata o caso do arquivo desejado não ser
     * encontrado
     */
    public void LeTabelasExistentes() throws FileNotFoundException, IOException {

        try (DataInputStream entrada = new DataInputStream(new BufferedInputStream(new FileInputStream("Info_das_Tabelas.dat")))) {

            Tabela T = null;
            String nome_atributo = null;
            String tipo_atributo = null;
            TipodeDados atributo = null;
            boolean continua = true;
            DataInputStream entradaMetaDados = null;
            ArrayList<TipodeDados> atributosTabela = new ArrayList<TipodeDados>();
            String nomeTabela = entrada.readUTF();
            while (!Tabela.FINALARQUIVO.equals(nomeTabela)) {
                entradaMetaDados = new DataInputStream(new BufferedInputStream(new FileInputStream("Metadados_" + nomeTabela + ".dat")));

                //Numero de registros total
                int numeroDeRegistrosTotal = entradaMetaDados.readInt();

                //Numero de registros que não foram apagados
                int numeroDeRegistrosNaoApagados = entradaMetaDados.readInt();

                do {
                    nome_atributo = entradaMetaDados.readUTF();
                    if (!nome_atributo.equals(Tabela.FINALARQUIVO)) {
                        tipo_atributo = entradaMetaDados.readUTF();
                        atributo = new TipodeDados(nome_atributo, tipo_atributo);
                        atributosTabela.add(atributo);
                    } else {
                        continua = false;
                    }
                } while (continua);
                T = new Tabela(nomeTabela, atributosTabela, numeroDeRegistrosTotal, numeroDeRegistrosNaoApagados);
                entradaMetaDados.close();
                tabelas.add(T);
                nomeTabela = entrada.readUTF();
            }
            entrada.close();
        } catch (IOException e) {
            //System.out.println("Arquivo Info_das_Tabelas.dat não foi localizado");
        }
    }

    /**
     * SalvaControleTabelas salva os nomes das tabelas que ja foram inseridas
     * para serem recuperadas na proxima vez que o programa for aberto
     *
     * @throws java.io.IOException Trata a exceção de fim de arquivo (Não ocorre
     * nunca pois é usado um marcador de fim de arquivo)
     * @throws FileNotFoundException Trata o caso do arquivo desejado não ser
     * encontrado
     */
    public void SalvaControleTabelas() throws FileNotFoundException, IOException {
        try (DataOutputStream saida = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("Info_das_Tabelas.dat")))) {
            for (Object tabela : tabelas) {
                Tabela T = (Tabela) tabela;
                saida.writeUTF(T.nomeTabela);
            }
            saida.writeUTF("final");
            saida.close();
        }
    }

    /*
     Verifica se o nome da tabela é uma tabela existente no arraylist de controletabelas
     @return -1 se a tabela não existe
     @return o indice onde a tabela se localiza no arraylist
     */
    public int BuscaTabela(String nomeTabela) {
        for (int k = 0; k < tabelas.size(); k++) {
            Tabela T = (Tabela) tabelas.get(k);
            if (T.nomeTabela.equalsIgnoreCase(nomeTabela)) { //Se o nome da tabela que o usuário digitou for igual ao nome de uma das tabelas do arraylist
                return k;
            }
        }
        return -1;
    }

    /**
     * Realiza o controle da inserção de tabela. Se já existir uma tabela com o
     * nome que o usuário está tentando realizar a inserção, a mensagem "Já
     * existe uma tabela com este nome!" será lançada no console, e a inserção
     * não será realizada. Este método garante que nunca vão existir duas
     * tabelas com o mesmo nome dentro do sistema.
     */
    public void inserirTabela() {
        System.out.println("Inserir tabela!");
        try {
            Tabela T;
            T = Tabela.ConstroiTabela();
            //Verifica se o nome de tabela que o usuário está tentando inserir já foi utilizado. O programa não irá permitir a existência de duas tabelas com o mesmo nome
            if (this.BuscaTabela(T.nomeTabela) == -1) { //Se não existe nenhuma tabela com este nome, então SALVA
                this.tabelas.add(T);
                System.out.println("Tabela gravada!");
                this.SalvaControleTabelas();
            } else {//Se já existe, a tabela não pode ser inserida
                System.out.println("Já existe uma tabela com este nome!");
            }

        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Método de controle da remoção de tabela Lê o nome da tabela e verifica se
     * ela existe. Caso não exista, volta ao menu e nada é excluido. Caso
     * exista, os arquivos de dados, metadados e hash da tabela são removidos, e
     * ela é deletada do sistema.
     */
    public void removeTabela() {
        Scanner entradaDados = new Scanner(System.in);
        System.out.println("Remover tabela");
        System.out.println("Entre com o nome da tabela");
        String nomeTabela = entradaDados.next();
        nomeTabela = nomeTabela.toUpperCase();
        int indice = this.BuscaTabela(nomeTabela);
        if (indice == -1) {
            System.out.println("Tabela Inexistente!");
        } else {
            Tabela T = (Tabela) this.tabelas.remove(indice);

            File dataFile = new File(nomeTabela + ".dat");
            dataFile.delete();

            File hashFile = new File("Hash" + nomeTabela + ".dat");
            hashFile.delete();

            File metadadosFile = new File("Metadados_" + nomeTabela + ".dat");
            metadadosFile.delete();

            System.out.println("Tabela Removida!");
            try {
                this.SalvaControleTabelas();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * Método que imprime todas as tabelas existentes
     *
     */
    public void imprimiTabelas() {
        System.out.println("Imprimir todas as tabelas!");
        for (Object tabela : this.tabelas) {
            Tabela T = (Tabela) tabela;
            System.out.println(T.nomeTabela);
        }
        System.out.println(" -- -- // -- -- ");
    }

    /**
     * Método que atualiza o arquivo de metadados de todas as tabelas
     *
     */
    public void atualizaMetadados() {
        Tabela T;
        for (Object tabela : this.tabelas) {
            T = (Tabela) tabela;
            T.atualizaMetadadosTabela();
        }
    }

}
