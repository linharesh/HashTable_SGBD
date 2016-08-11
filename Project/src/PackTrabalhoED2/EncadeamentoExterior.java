package PackTrabalhoED2;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EncadeamentoExterior {

    public static final int TamanhoInt = 4;

    /**
     * Calcula o tamanho das tuplas da tabela
     *
     * @param atributosTabela ArrayList contendo os atributos da tabela e seus
     * respectivos tipos
     * @return retorna o tamano da tupla baseada nos TipodeDados de
     * atributosTabela
     */
    public static int Calcula_Tamanho(List<TipodeDados> atributosTabela) {
        int quantidadeAtributos = atributosTabela.size();
        int tamanho_tupla = 0;
        for (int k = 0; k < quantidadeAtributos; k++) {
            TipodeDados A = (TipodeDados) atributosTabela.get(k);
            switch (A.tipoAtributo) {
                case (TipodeDados.BOOLEAN): {
                    tamanho_tupla = tamanho_tupla + 1;
                    break;
                }
                case (TipodeDados.CHAR): {
                    tamanho_tupla = tamanho_tupla + 2;
                    break;
                }
                case (TipodeDados.SHORT): {
                    tamanho_tupla = tamanho_tupla + 2;
                    break;
                }
                case (TipodeDados.INTEIRO): {
                    tamanho_tupla = tamanho_tupla + 4;
                    break;
                }
                case (TipodeDados.LONGINT): {
                    tamanho_tupla = tamanho_tupla + 8;
                    break;
                }
                case (TipodeDados.FLOAT): {
                    tamanho_tupla = tamanho_tupla + 4;
                    break;
                }
                case (TipodeDados.DOUBLE): {
                    tamanho_tupla = tamanho_tupla + 8;
                    break;
                }
                case (TipodeDados.STRING): {
                    tamanho_tupla = tamanho_tupla + 512;   //Tamanho da string: 255
                    break;                                 //cada char possui 2 bytes, e 2 bytes do total estão reservados para armazenar o tamanho
                }
            }
        }
        return tamanho_tupla;
    }

    /**
     *
     * @param T Tabela em que a busca vai ser feita
     * @param S Expressão da busca (o manual de uso ensina como formular uma expressão de busca)
     * @return Retorna o ArrayList com as tuplas que o busca retornou
     * @throws IOException Erro de I.O.
     */
    public static ArrayList buscaPorCampo(Tabela T, String S) throws IOException {

        ArrayList Tuplas = new ArrayList();
        String[] condicaoBusca;
        String[] condicao;

        if (S.contains(" AND ")) {
            condicao = S.split(" AND ");
            ArrayList Primeiro = EncadeamentoExterior.buscaNaTabelaToda(T, condicao[0]);
            ArrayList Segundo = EncadeamentoExterior.buscaNaTabelaToda(T, condicao[1]);

            for (int i = 0; i < Primeiro.size(); i++) {
                for (int j = 0; j < Segundo.size(); j++) {
                    if (Primeiro.get(i).equals(Segundo.get(j))) {
                        Tuplas.add(Primeiro.get(i));
                    }
                }
            }

        } else if (S.contains(" OR ")) {
            condicao = S.split(" OR ");

            ArrayList Primeiro = EncadeamentoExterior.buscaNaTabelaToda(T, condicao[0]);
            ArrayList Segundo = EncadeamentoExterior.buscaNaTabelaToda(T, condicao[1]);

            //Elimina os repetidos
            for (int i = 0; i < Primeiro.size(); i++) {
                for (int j = 0; j < Segundo.size(); j++) {
                    if (Primeiro.get(i).equals(Segundo.get(j))) {
                        Segundo.remove(j);
                    }
                    Tuplas.add(Primeiro.get(i));
                }
            }

            for (int i = 0; i < Segundo.size(); i++) {
                Tuplas.add(Segundo.get(i));
            }

        } else if (S.contains("TUDO")) {
            Tuplas = EncadeamentoExterior.buscaNaTabelaToda(T, "TUDO");
        } else {
            Tuplas = EncadeamentoExterior.buscaNaTabelaToda(T, S);
        }

        return Tuplas;
    }

    /**
     *
     * @param T A tabela em que sera realizada a busca
     * @param codTupla O código que será buscado
     * @return O endereco onde o cliente foi encontrado, primeiro endereço vazio
     * ou -1 se não for encontrado retorna -2 se a posição na tabela hash
     * estiver vazia
     * @throws Exception Exceção
     */
    public static int busca(Tabela T, int codTupla) throws Exception {
        boolean pegouPrimeiroLiberado = false;
        int posicaoLiberada = 0;
        int posicao;

        try (RandomAccessFile Hash = new RandomAccessFile(new File("Hash" + T.nomeTabela + ".dat"), "r")) {
            posicao = (codTupla % 7/*(int) (Hash.length() / T.tamanho_tupla)*/ /*(int) (Hash.length() / T.tamanho_tupla) significa 7*/);
            Hash.seek(TamanhoInt * posicao);
            posicao = Hash.readInt();
            Hash.close();
            if (posicao == -1) {
                return -2;
            } else {
                RandomAccessFile Dados = new RandomAccessFile(new File(T.nomeTabela + ".dat"), "r");
                do {
                    Dados.seek(posicao);
                    ArrayList tuplaLida = CompartimentoHash.le(T, posicao);
                    if (((boolean) tuplaLida.get(tuplaLida.size() - 2)) && (!pegouPrimeiroLiberado)) { // pega o primeiro liberado
                        pegouPrimeiroLiberado = true;
                        posicaoLiberada = posicao;
                    } else {
                        if (((int) tuplaLida.get(0)) == codTupla) {//Se achou o elemento e ele não está liberado
                            Dados.close();
                            return posicao;
                        }
                        posicao = (int) tuplaLida.get(tuplaLida.size() - 1); // pega o ponteiro
                        if (posicao == -1) { // se nao tiver proximo
                            if (posicaoLiberada == 0) { // retorna codigo q indica q nao existe posicao liberada
                                Dados.close();
                                return -1;
                            }
                            Dados.close();
                            return posicaoLiberada;  // retorna a posicao liberada se nao existir proximo 
                        }
                    }
                } while (true);
            }

        }
    }

    /**
     * Método que lê a tabela e retorna um arraylist com todas as suas tuplas
     *
     * @param T Tabela em que a leitura será realizadas
     * @return ArrayList contendo todas as tuplas da tabela
     */
    public static ArrayList pegaTodosElementosDaTabela(Tabela T) {
        int posicaoAtualNoArquivo = 0;
        ArrayList Tuplas = new ArrayList();
        ArrayList Tupla = new ArrayList();
        do {
            try {
                Tupla = CompartimentoHash.le(T, T.tamanhoTupla * posicaoAtualNoArquivo++);
                if (((boolean) Tupla.get(Tupla.size() - 2)) == false) //&& (Tupla.get(posicaoAtributoNaTabela)))
                {
                    Tuplas.add(Tupla);
                }
            } catch (IOException ex) {
                Logger.getLogger(EncadeamentoExterior.class.getName()).log(Level.SEVERE, null, ex);
            }

        } while (T.numDeRegistrosTotal > posicaoAtualNoArquivo);
        return Tuplas;
    }

    /**
     * Método utilizado para realizar buscasde tuplas na tabela com o operador
     * lógico AND
     *
     * @param S String contendo a expressão de busca ((o manual de uso ensina como formular uma expressão de busca))
     * @param T Tabela em que a busca será realziada
     * @return Um ArrayList com as tuplas que foram localizadas pela busca
     */
    public static ArrayList buscaComAND(String S, Tabela T) {
        String[] condicaoDaBusca = S.split(" AND ");
        ArrayList Tuplas = new ArrayList();
        ArrayList TuplasTratadas = new ArrayList();
        S = S.replaceAll(" ", "");
        for (int k = 0; k < condicaoDaBusca.length; k++) {
            try {
                Tuplas = buscaNaTabelaToda(T, condicaoDaBusca[k]);
                if (TuplasTratadas.isEmpty()) {
                    TuplasTratadas = Tuplas;
                } else {

                    for (int i = 0; i < TuplasTratadas.size(); i++) {
                        if (!Tuplas.contains(TuplasTratadas.get(i))) {
                            TuplasTratadas.remove(i);
                            i--;//Pra evitar problemas no size

                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Tabela.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return TuplasTratadas;
    }

    /**
     * Método utilizado para realizar buscas de tuplas na tabela com o operador
     * lógico OR
     *
     * @param S String contendo a expressão de busca (o manual de uso ensina como formular uma expressão de busca)
     * @param T Tabela em que a busca será realziada
     * @return Um ArrayList com as tuplas que foram localizadas pela busca
     */
    public static ArrayList buscaComOR(String S, Tabela T) {
        String[] condicaoDaBusca = S.split(" OR ");
        ArrayList Tuplas = new ArrayList();
        ArrayList TuplasTratadas = new ArrayList();
        S = S.replaceAll(" ", "");
        int tamanho;

        for (int k = 0; k < condicaoDaBusca.length; k++) {
            try {
                Tuplas = EncadeamentoExterior.buscaNaTabelaToda(T, condicaoDaBusca[k]);
                if (TuplasTratadas.isEmpty()) {
                    TuplasTratadas = Tuplas;
                } else {
                    for (int i = 0; i < Tuplas.size(); i++) {
                        if (!TuplasTratadas.contains(Tuplas.get(i))) {
                            TuplasTratadas.add(Tuplas.get(i));
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(Tabela.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return TuplasTratadas;
    }

    /**
     * Realiza uma busca com uma expressão de busca.
     *
     * @param T A tabela em que a busca vai ser realizada
     * @param S A String que contém a expressão de busca ((o manual de uso ensina como formular uma expressão de busca))
     * @return O ArrayList que a busca retornou
     * @throws IOException Erro de I.O.
     */
    public static ArrayList buscaNaTabelaToda(Tabela T, String S) throws IOException {
        int posicaoAtualNoArquivo = 0;
        ArrayList Tuplas = new ArrayList();
        ArrayList Tupla = new ArrayList();
        String[] condicaoDaBusca = null;
        int retorno_ehAtributo;
        String[] condicaoDaBuscaTratada;
        S = S.replaceAll(" ", "");
        if (S.contains(">")) {
            condicaoDaBusca = S.split(">");
        } else if (S.contains("<")) {
            condicaoDaBusca = S.split("<");
        } else if (S.contains("=")) {
            condicaoDaBusca = S.split("=");
        } else if (S.contains("!=")) {
            condicaoDaBusca = S.split("!=");
        }

        if ((retorno_ehAtributo = T.ehAtributoTabela(condicaoDaBusca[0])) >= 0) {
            int posicaoAtributoNaTabela = retorno_ehAtributo;
            TipodeDados A = (TipodeDados) T.atributosTabela.get(retorno_ehAtributo);
            do {
                Tupla = CompartimentoHash.le(T, T.tamanhoTupla * posicaoAtualNoArquivo++);

                switch (A.tipoAtributo) {
                    case (TipodeDados.BOOLEAN): {
                        if (S.contains("=")) {
                            if ((boolean) Tupla.get(posicaoAtributoNaTabela) == Boolean.parseBoolean(condicaoDaBusca[1])) {
                                Tuplas.add(Tupla);
                            }
                        } else if (S.contains("!=")) {
                            if ((boolean) Tupla.get(posicaoAtributoNaTabela) != Boolean.parseBoolean(condicaoDaBusca[1])) {
                                Tuplas.add(Tupla);
                            }
                        } else {
                            System.out.println("Impossível realizar a busca com este operando");
                        }
                        break;
                    }
                    case (TipodeDados.CHAR): {
                        String s = condicaoDaBusca[1];
                        char c = s.charAt(0);

                        if (S.contains("=")) {

                            if (((char) Tupla.get(posicaoAtributoNaTabela)) == (c)) {
                                Tuplas.add(Tupla);
                            }
                        } else if (S.contains("!=")) {
                            if (((char) Tupla.get(posicaoAtributoNaTabela)) != (c)) {
                                Tuplas.add(Tupla);
                            }
                        } else {
                            System.out.println("Impossível realizar a busca com este operando");
                        }
                        break;
                    }
                    case (TipodeDados.SHORT): {
                        if (S.contains(">")) {
                            if (((short) Tupla.get(posicaoAtributoNaTabela) > Short.parseShort(condicaoDaBusca[1]))) {
                                Tuplas.add(Tupla);
                            }
                        } else {
                            if (S.contains("<")) {
                                if (((short) Tupla.get(posicaoAtributoNaTabela) < Short.parseShort(condicaoDaBusca[1]))) {
                                    Tuplas.add(Tupla);
                                }

                            } else if (S.contains("=")) {
                                if (((short) Tupla.get(posicaoAtributoNaTabela) == Short.parseShort(condicaoDaBusca[1]))) {
                                    Tuplas.add(Tupla);
                                }
                            } else if (S.contains("!=")) {
                                if (((short) Tupla.get(posicaoAtributoNaTabela) != Short.parseShort(condicaoDaBusca[1]))) {
                                    Tuplas.add(Tupla);
                                }
                            }
                        }
                        break;
                    }
                    case (TipodeDados.INTEIRO): {
                        if (S.contains(">")) {
                            if (((int) Tupla.get(posicaoAtributoNaTabela) > Integer.parseInt(condicaoDaBusca[1]))) {
                                Tuplas.add(Tupla);
                            }
                        } else {
                            if (S.contains("<")) {
                                if (((int) Tupla.get(posicaoAtributoNaTabela) < Integer.parseInt(condicaoDaBusca[1]))) {
                                    Tuplas.add(Tupla);
                                }

                            } else if (S.contains("=")) {
                                if (((int) Tupla.get(posicaoAtributoNaTabela) == Integer.parseInt(condicaoDaBusca[1]))) {
                                    Tuplas.add(Tupla);
                                }
                            } else if (S.contains("!=")) {
                                if (((int) Tupla.get(posicaoAtributoNaTabela) != Integer.parseInt(condicaoDaBusca[1]))) {
                                    Tuplas.add(Tupla);
                                }

                            }
                        }
                        break;
                    }
                    case (TipodeDados.LONGINT): {
                        if (S.contains(">")) {
                            if (((long) Tupla.get(posicaoAtributoNaTabela) > Long.parseLong(condicaoDaBusca[1]))) {
                                Tuplas.add(Tupla);
                            }
                        } else {
                            if (S.contains("<")) {
                                if (((long) Tupla.get(posicaoAtributoNaTabela) < Long.parseLong(condicaoDaBusca[1]))) {
                                    Tuplas.add(Tupla);
                                }

                            } else if (S.contains("=")) {
                                if (((long) Tupla.get(posicaoAtributoNaTabela) == Long.parseLong(condicaoDaBusca[1]))) {
                                    Tuplas.add(Tupla);
                                }
                            } else if (S.contains("!=")) {
                                if (((long) Tupla.get(posicaoAtributoNaTabela) != Long.parseLong(condicaoDaBusca[1]))) {
                                    Tuplas.add(Tupla);
                                }

                            }
                        }
                        break;
                    }
                    case (TipodeDados.FLOAT): {
                        if (S.contains(">")) {
                            if (((float) Tupla.get(posicaoAtributoNaTabela) > Float.parseFloat(condicaoDaBusca[1]))) {
                                Tuplas.add(Tupla);
                            }
                        } else {
                            if (S.contains("<")) {
                                if (((float) Tupla.get(posicaoAtributoNaTabela) < Float.parseFloat(condicaoDaBusca[1]))) {
                                    Tuplas.add(Tupla);
                                }

                            } else if (S.contains("=")) {
                                if (((float) Tupla.get(posicaoAtributoNaTabela) == Float.parseFloat(condicaoDaBusca[1]))) {
                                    Tuplas.add(Tupla);
                                }
                            } else if (S.contains("!=")) {
                                if (((float) Tupla.get(posicaoAtributoNaTabela) != Float.parseFloat(condicaoDaBusca[1]))) {
                                    Tuplas.add(Tupla);
                                }

                            }
                        }
                        break;
                    }
                    case (TipodeDados.DOUBLE): {
                        if (S.contains(">")) {
                            if (((double) Tupla.get(posicaoAtributoNaTabela) > Double.parseDouble(condicaoDaBusca[1]))) {
                                Tupla.add(Tupla);
                            }
                        } else {
                            if (S.contains("<")) {
                                if (((double) Tupla.get(posicaoAtributoNaTabela) < Double.parseDouble(condicaoDaBusca[1]))) {
                                    Tupla.add(Tupla);
                                }

                            } else if (S.contains("=")) {
                                if (((double) Tupla.get(posicaoAtributoNaTabela) == Double.parseDouble(condicaoDaBusca[1]))) {
                                    Tupla.add(Tupla);
                                }
                            } else if (S.contains("!=")) {
                                if (((double) Tupla.get(posicaoAtributoNaTabela) != Double.parseDouble(condicaoDaBusca[1]))) {
                                    Tupla.add(Tupla);
                                }

                            }
                        }
                        break;
                    }
                    case (TipodeDados.STRING): {
                        if (S.contains("=")) {
                            if (((String) Tupla.get(posicaoAtributoNaTabela)).equalsIgnoreCase(condicaoDaBusca[1])) {
                                Tuplas.add(Tupla);
                            }
                        } else if (S.contains("!=")) {
                            if (!(((String) Tupla.get(posicaoAtributoNaTabela)).equalsIgnoreCase(condicaoDaBusca[1]))) {
                                Tuplas.add(Tupla);
                            }
                        } else {
                            System.out.println("Impossível realizar a busca com este operando");
                        }
                        break;
                    }
                }

            } while (T.numDeRegistrosTotal > posicaoAtualNoArquivo);
        } else {
            System.out.println("Erro na expressão: Atributo não identificado");
        }

        return Tuplas;

    }

    /**
     * Pega o ultimo elemento da lista que existe após a tabela hash
     *
     * @param T Tabela em que o pegaUltimoElementoDaLista vai ser executado
     * @param posicaoHash Posição da tabela hash em que a lista buscada se
     * localiza
     * 
     * @return Posição do ultimo elemento
     * @throws Exception Exceção
     */
    public static int pegaUltimoElementoDaLista(Tabela T, int posicaoHash) throws Exception {
        int posicaoNoArquivoHash;
        try (RandomAccessFile Hash = new RandomAccessFile(new File("Hash" + T.nomeTabela + ".dat"), "r")) {
            posicaoNoArquivoHash = (posicaoHash % 7 /*(int) (Hash.length() / T.tamanho_tupla*/) /*(int) (Hash.length() / T.tamanho_tupla) significa 7)*/;
            Hash.seek(posicaoNoArquivoHash * TamanhoInt);
            int posicaoNoArquivoDados = Hash.readInt();
            do {
                ArrayList tuplaLida = CompartimentoHash.le(T, posicaoNoArquivoDados);
                if (((int) tuplaLida.get(tuplaLida.size() - 1)) == -1) {//Se o ponteiro para o próximo for igual a -1 então
                    Hash.close();
                    return posicaoNoArquivoDados;
                } else {
                    posicaoNoArquivoDados = (int) tuplaLida.get(tuplaLida.size() - 1);
                }
            } while (true);
        }
    }

    
    /**
     * 
     * @param T Tabela onde a inserção será realizada
     * @param dadosTupla Dados da tupla que será inserida
     * @return Endereço onde o cliente foi inserido, -1 se não conseguiu
     * inserir
     * @throws Exception Exceção
     */
    public static boolean insereTabela(Tabela T, ArrayList dadosTupla) throws Exception {
        int retorno_da_busca;
        int codTupla;
        codTupla = ((int) dadosTupla.get(0));
        retorno_da_busca = busca(T, codTupla);
        ArrayList TuplaLida = new ArrayList();
        if (retorno_da_busca == -2) { // nao existe nenhum dado naquela posicao hash
            CompartimentoHash.salva(T, dadosTupla, T.numDeRegistrosTotal * T.tamanhoTupla);
            CompartimentoHash.salvaNaHash(T, codTupla, T.numDeRegistrosTotal);
            T.numDeRegistrosTotal++;
            return true;

        } else {
            if (retorno_da_busca == -1) { // nao achou posicao liberada no hash e vai inserir no final da tabela
                int ultimoElementoLista = pegaUltimoElementoDaLista(T, (int) dadosTupla.get(0));
                TuplaLida = (ArrayList) CompartimentoHash.le(T, ultimoElementoLista);
                TuplaLida.set(TuplaLida.size() - 1, T.numDeRegistrosTotal * T.tamanhoTupla);
                CompartimentoHash.salva(T, TuplaLida, ultimoElementoLista);
                CompartimentoHash.salva(T, dadosTupla, T.numDeRegistrosTotal * T.tamanhoTupla);
                T.numDeRegistrosTotal++;
                return true;
            } else {

                TuplaLida = (ArrayList) CompartimentoHash.le(T, retorno_da_busca);
                dadosTupla.set(dadosTupla.size() - 1, TuplaLida.get(TuplaLida.size() - 1));//Para manter o ponteiro para o próximo
                boolean flag = (boolean) TuplaLida.get(T.atributosTabela.size() - 2);
                if (flag) {//Se está liberado
                    CompartimentoHash.salva(T, dadosTupla, retorno_da_busca);
                    T.numDeRegistrosNaoDeletados++;
                    return true;
                }
                return false;
            }
        }
    }

    /**
     *
     * @param T Tabela onde será realizada a exclusão
     * @param chaveTupla Chave a ser excluida
     * @return Endereço do cliente que foi excluido, ou -1 se cliente não existe
     * @throws IOException Erro de I.O.
     * @throws Exception Exceção
     */
    public static boolean exclui(Tabela T, int chaveTupla) throws IOException, Exception {
        int retornoDaBusca;
        retornoDaBusca = busca(T, chaveTupla);
        if ((retornoDaBusca == -1) || (retornoDaBusca == -2)) {
            return false;
        } else {
            RandomAccessFile tab = new RandomAccessFile(new File(T.nomeTabela + ".dat"), "rw");
            tab.seek(retornoDaBusca);
            ArrayList dadosTuplaLida = (ArrayList) CompartimentoHash.le(T, retornoDaBusca);
            if (((int) dadosTuplaLida.get(0)) == chaveTupla) {  // compara se o campo chave da dadosTuplaLida é igual a chave de dadosTupla
                dadosTuplaLida.set(dadosTuplaLida.size() - 2, true);// coloca a flag em liberada
                CompartimentoHash.salva(T, dadosTuplaLida, retornoDaBusca);
                tab.close();
                return true;
            }
            tab.close();
        }
        return false;
    }
}
