package PackTrabalhoED2;

import static PackTrabalhoED2.EncadeamentoExterior.TamanhoInt;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class CompartimentoHash {

    /**
     * Abre o arquivo hash e salva na posição da hash a posição em que foi
     * inserido na tabela
     *
     * @param T Tabela na qual será salva a tupla
     * @param codTupla A tupla que será apontada na tabela
     * @param ponteiroParaPosicaoArqDados Endereço da tupla no arquivo de dados
     * @throws IOException Pode gerar erros de IO
     */
    public static void salvaNaHash(Tabela T, int codTupla, int ponteiroParaPosicaoArqDados) throws IOException {

        RandomAccessFile Hash = new RandomAccessFile(new File("Hash" + T.nomeTabela + ".dat"), "rw");
        int posicao = codTupla % 7/*(int) (Hash.length() / T.tamanho_tupla)*/ /*(int) (Hash.length() / T.tamanho_tupla) significa 7*/;
        Hash.seek(TamanhoInt * posicao);
        Hash.writeInt(ponteiroParaPosicaoArqDados * T.tamanhoTupla);
        Hash.close();
    }

 
    
    /**Cria uma tabela hash vazia de tamanho tam, e salva no arquivo
     * nomeArquivoHash Como os compartimentos ainda não tem lista encadeada
     * associada deve ter valor igual a -1
     * 
     * @param nomeArquivoHash Nome do arquivo de Hash da tabela
     * @param tam Tamanho da tabela hash 
     * @throws FileNotFoundException Erro de Arquivo não encontrado
     * @throws IOException Erro de I.O.
     */
    public static void criaHash(String nomeArquivoHash, int tam) throws FileNotFoundException, IOException {
        RandomAccessFile arquivo = null;
        arquivo = new RandomAccessFile(new File(nomeArquivoHash), "rw");
        for (int k = 0; k < tam; k++) {
            arquivo.writeInt(-1);
        }
        arquivo.close();
    }

    
    /**Salva os dados do dadosTupla na tabela T
     * 
     * @param T Tabela onde serão salvo os dadosTupla
     * @param dadosTupla Dados da tupla que serão salvos
     * @param posicaoSalvar Posição onde os dados serão salvos
     * @throws IOException Erro de I.O.
     */
    public static void salva(Tabela T, List dadosTupla, int posicaoSalvar) throws IOException {
        try (RandomAccessFile out = new RandomAccessFile(T.nomeTabela + ".dat", "rw")) {
            out.seek(posicaoSalvar);
            for (int k = 0; k < T.atributosTabela.size(); k++) {
                //            System.out.println("Entrou no FOR e K= " + k);//apagar esta linha
                TipodeDados Tipo;
                Tipo = (TipodeDados) T.atributosTabela.get(k);
                switch (Tipo.tipoAtributo) {
                    case (TipodeDados.BOOLEAN): {
                        boolean B = (boolean) dadosTupla.get(k);
                        out.writeBoolean(B);
                        break;
                    }

                    case (TipodeDados.CHAR): {
                        char C = (char) dadosTupla.get(k);
                        out.writeChar(C);
                        break;
                    }

                    case (TipodeDados.SHORT): {
                        short S = (short) dadosTupla.get(k);
                        out.writeShort(S);
                        break;
                    }

                    case (TipodeDados.INTEIRO): {
                        int I = (int) dadosTupla.get(k);
                        out.writeInt(I);
                        break;
                    }

                    case (TipodeDados.LONGINT): {
                        long L = (long) dadosTupla.get(k);
                        out.writeLong(L);
                        break;
                    }

                    case (TipodeDados.FLOAT): {
                        float F = (float) dadosTupla.get(k);
                        out.writeFloat(F);
                        break;
                    }

                    case (TipodeDados.DOUBLE): {
                        double D = (double) dadosTupla.get(k);
                        out.writeDouble(D);
                        break;
                    }

                    case (TipodeDados.STRING): {
                        String Str = (String) dadosTupla.get(k);
                        out.writeUTF(Str);
                        break;
                    }
                }
            }
            out.close();
        }
    }

   
    
    /**Lê uma tupla do arquivo T.nomeTabela e retorna dentro de um arrayList
     * generico que contem os dados da tupla.
     * 
     * 
     * @param T A tabela que a leitura será realizada
     * @param posicaoLer Posição de leitura
     * @return ArrayList com os elementos da tupla lida
     * @throws IOException Erro de I.O.
     */
    public static ArrayList le(Tabela T, int posicaoLer) throws IOException {
        ArrayList retorno = new ArrayList();
        try (RandomAccessFile in = new RandomAccessFile(T.nomeTabela + ".dat", "r")) {
            in.seek(posicaoLer);
            TipodeDados Tipo;
            for (int k = 0; k < T.atributosTabela.size(); k++) {

                Tipo = (TipodeDados) T.atributosTabela.get(k);

                switch (Tipo.tipoAtributo) {
                    case (TipodeDados.BOOLEAN): {
                        retorno.add(in.readBoolean());
                        break;
                    }

                    case (TipodeDados.CHAR): {
                        retorno.add(in.readChar());
                        break;
                    }

                    case (TipodeDados.SHORT): {
                        retorno.add(in.readShort());
                        break;
                    }

                    case (TipodeDados.INTEIRO): {
                        retorno.add(in.readInt());
                        break;
                    }

                    case (TipodeDados.LONGINT): {
                        retorno.add(in.readLong());
                        break;
                    }

                    case (TipodeDados.FLOAT): {
                        retorno.add(in.readFloat());
                        break;
                    }

                    case (TipodeDados.DOUBLE): {
                        retorno.add(in.readDouble());
                        break;
                    }

                    case (TipodeDados.STRING): {
                        retorno.add(in.readUTF());
                        break;
                    }
                }
            }
            in.close();
        }
        return retorno;
    }

    /**
     *
     * @param T Tabela que você deseja imprimir o hash
     * @throws FileNotFoundException Erro de arquivo não encontrado
     * @throws IOException Erro de I.O.
     */
    public static void imprimeTodoHash(Tabela T) throws FileNotFoundException, IOException {
        RandomAccessFile Dados = new RandomAccessFile(new File("Hash" + T.nomeTabela + ".dat"), "r");
        for (int j = 0; j < 7; j++) {
            System.out.println(Dados.readInt());
        }
        Dados.close();
    }

}
