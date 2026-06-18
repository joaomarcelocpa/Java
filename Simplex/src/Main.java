public class Main {
    public static void main(String[] args) {

        double[] lucro = {-2,-3};
        double[][] restricoes = {{1,1}, {1,2}};
        double[] limites = {4,6};
        boolean minimizar = true;

        simplex(lucro, restricoes, limites, minimizar);
    }

    public static void simplex(double[] lucro, double[][] restricoes, double[] limites, boolean minimizar){
        int numVariaveis = lucro.length; // numero de variaveis
        int numRestricoes = limites.length; // numero de restricoes (variaveis de folga)
        int totalVariaveis = numVariaveis + numRestricoes; // total de variáveis = variáveis originais + variáveis de folga

        double[] coeficientes = new double[numVariaveis]; // copia do lucro, invertida se for minimizacao (min c.x = -max(-c.x))

        for (int coluna = 0; coluna < numVariaveis; coluna++){
            coeficientes[coluna] = minimizar ? -lucro[coluna] : lucro[coluna]; // inverte o sinal só quando for minimizar, sem alterar o array original
        }

        double[][] tabela = new double[numRestricoes][totalVariaveis + 1]; // tabela do tamanho total de variáveis + coluna do valor

        for (int linha = 0; linha < numRestricoes; linha++){ // loop para preencher os coeficientes das variáveis reais
            for (int coluna = 0; coluna < numVariaveis; coluna++){
                tabela[linha][coluna] = restricoes[linha][coluna];
            }
        }

        for (int linha = 0; linha < numRestricoes; linha++) {
            tabela[linha][numVariaveis + linha] = 1; // preenche as células das folgas com 1
        }

        for (int linha = 0; linha < numRestricoes; linha++){
            tabela[linha][totalVariaveis] = limites[linha]; // preenche a última coluna com os limites dos recursos
        }

        double[] linhaLucro = new double[totalVariaveis + 1]; // array do tamanho total de variáveis + coluna do valor

        for (int coluna = 0; coluna < numVariaveis; coluna++){
            linhaLucro[coluna] = -coeficientes[coluna]; // adiciona os coeficientes (já ajustados para min/max) com sinal trocado
        }

        int[] base = new int [numRestricoes]; // array para armazenar quais variáveis estão na base (inicialmente as variáveis de folga)

        for (int i = 0; i < numRestricoes; i++){ // inicializa a base com as variáveis de folga
            base[i] = numVariaveis + i;
        }

        exibirTabela(tabela, linhaLucro, base, totalVariaveis);

        while (true){
            int colunaEntra = -1; // índice da coluna que vai entrar na base
            double menorValor = 0; // menor valor negativo encontrado

            for (int coluna = 0; coluna < totalVariaveis; coluna++){ // loop para percorrer a cada elemento da linha lucro
                if (linhaLucro[coluna] < menorValor) { // se achou um valor menor
                    menorValor = linhaLucro[coluna]; // atualiza o menor valor
                    colunaEntra = coluna; // atualiza a coluna q vai entrar na base
                }
            }

            if (colunaEntra == -1){ // se não achou nenhum negativo sai
                break;
            }

            int linhaSai = -1;
            double menorRatio = Double.MAX_VALUE;

            for (int linha = 0; linha < numRestricoes; linha++){ // percorrer cada linha da tabela
                if (tabela[linha][colunaEntra] > 0) { // só entra no calculo se o coeficiente é positivo
                    double ratio = tabela[linha][totalVariaveis] / tabela[linha][colunaEntra]; // calcula o valor atual dividido pelo coeficiente da coluna que entra
                    if (ratio < menorRatio){ // se essa divisao deu resultado menor que o atual atualiza
                        menorRatio = ratio;
                        linhaSai = linha;
                    }
                }
            }

            if (linhaSai == -1){ // se nao achou nenhuma linha com coeficiente positivo
                System.out.println("Problema ilimitado!");
                return;
            }

            pivot(tabela, linhaLucro, linhaSai, colunaEntra, totalVariaveis); // chama a funcao pivot

            base[linhaSai] = colunaEntra; // atualiza a base, a linha que saiu agora tem a variável que entrou

            exibirTabela(tabela, linhaLucro, base, totalVariaveis);
        }

        System.out.println("Solução ótima encontrada!");

        for (int var = 0; var < numVariaveis; var++) {
            double valor = 0;

            for (int linha = 0; linha < numRestricoes; linha++) {
                if (base[linha] == var) {
                    valor = tabela[linha][totalVariaveis];
                }
            }

            System.out.println("x" + (var + 1) + " = " + valor);
        }

        double resultado = minimizar ? -linhaLucro[totalVariaveis] : linhaLucro[totalVariaveis]; // desfaz a inversão de sinal feita no início para mostrar o valor real
        resultado += 0.0; // corrige "-0.0" causado pela inversão de sinal quando o resultado é exatamente zero

        System.out.println((minimizar ? "Valor minimo" : "Lucro maximo") + " = " + resultado);
    }





    public static void pivot(double[][] tabela, double[] linhaLucro, int linhaPivo, int colunaPivo, int totalVariaveis){
        double elementoPivo = tabela[linhaPivo][colunaPivo]; // pegar qual o elemento é o pivo na tabela

        for (int coluna = 0; coluna <= totalVariaveis; coluna++){ // loop para percorrer os elementos da linha pivo em cada coluna
            tabela[linhaPivo][coluna] = tabela[linhaPivo][coluna] / elementoPivo; // dividir cada elemento da linha pivo pelo pivo
        }

        for (int linha = 0; linha < tabela.length; linha++){ // loop para percorrer e zerar a coluna pivô nas outras linhas
            if (linha != linhaPivo){ // pula a linha pivô, ela já foi atualizada
                double fator = tabela[linha][colunaPivo]; // pega o valor da célula na coluna pivô dessa linha

                for (int coluna = 0; coluna <= totalVariaveis; coluna++) { // Para cada linha que não é a pivô, percorre célula por célula e aplica
                    tabela[linha][coluna] = tabela[linha][coluna] - fator * tabela[linhaPivo][coluna]; // atualiza cada célula da linha percorrida subtraindo o fator multiplicado pela célula correspondente da linha pivô
                }
            }
        }

        double fatorLucro = linhaLucro[colunaPivo]; // pega o valor da célula na coluna pivô da linha do lucro

        for (int coluna = 0; coluna <= totalVariaveis; coluna++){
            linhaLucro[coluna] = linhaLucro[coluna] - fatorLucro * tabela[linhaPivo][coluna]; // novo elemento = elemento atual - fator × elemento da linha pivô
        }
    }




    public static void exibirTabela(double[][] tabela, double[] linhaLucro, int[] base, int totalVariaveis){
        System.out.print("BASE  |");
        for (int coluna = 0; coluna < totalVariaveis; coluna++){
            System.out.printf("  x%-4d", coluna + 1);
        }
        System.out.println("| VALOR");
        System.out.println("------+" + "-------".repeat(totalVariaveis) + "+-------");

        for (int linha = 0; linha < tabela.length; linha++){
            System.out.printf("  x%-3d |", base[linha] + 1);
            for (int coluna = 0; coluna < totalVariaveis; coluna++){
                System.out.printf("  %-5.2f", tabela[linha][coluna]);
            }
            System.out.printf("| %-6.2f\n", tabela[linha][totalVariaveis]);
        }

        System.out.println("------+" + "-------".repeat(totalVariaveis) + "+-------");
        System.out.print("  z   |");
        for (int coluna = 0; coluna < totalVariaveis; coluna++){
            System.out.printf("  %-5.2f", linhaLucro[coluna]);
        }
        System.out.printf("| %-6.2f\n\n", linhaLucro[totalVariaveis]);
    }
}