package PackTrabalhoED2;

import java.io.IOException;
import java.util.Scanner;

public class Main {

    /**
     * Método principal Gera o Menu e faz chamadas para outras classes
     *
     * @param args String[] args
     * @throws IOException Erro de I.O.
     */
    public static void main(String[] args) throws IOException {

        ControleTabelas CT = new ControleTabelas();

        Scanner entradaDados = new Scanner(System.in);
        int resp;
        do {
            System.out.println("MENU");
            System.out.println("1 -> Inserir tabela");
            System.out.println("2 -> Imprimir todas as tabelas");
            System.out.println("3 -> Listar os atributos de uma tabela");
            System.out.println("4 -> Remover tabela");
            System.out.println("5 -> Inserir tupla em tabela");
            System.out.println("6 -> Buscar tupla em tabela");
            System.out.println("7 -> Remover tupla de tabela");
            System.out.println("8 -> Atualizar tupla de tabela");
            System.out.println("9 -> Realizar carga de dados para uma tabela");
            System.out.println("0 -> Sair");
            resp = entradaDados.nextInt();

            switch (resp) {

                case 1: {
                    CT.inserirTabela();
                    break;
                }

                case 2: {
                    CT.imprimiTabelas();
                    break;
                }

                case 3: {
                    CT.listaAtributosDeUmaDasTabela();
                    break;
                }

                case 4: {
                    CT.removeTabela();
                    break;
                }

                case 5: {
                    CT.insereTuplaEmUmaDasTabelas();
                    break;
                }

                case 6: {
                    CT.buscaTuplaEmUmaDasTabelas();
                    break;
                }

                case 7: {
                    CT.removeTuplaEmUmaDasTabelas();
                    break;
                }

                case 8: {
                    CT.atualizaTuplaEmUmaDasTabelas();
                    break;
                }

                case 9: {
                    CT.cargaDeDadosEmUmaDasTabelas();
                    break;
                }

                case 0: {
                    CT.atualizaMetadados();
                    System.out.println("SEU SGBD FOI ENCERRADO COM SEGURANÇA");
                    break;
                }
            }
            CT.atualizaMetadados();
        } while (resp != 0);
    }
}
