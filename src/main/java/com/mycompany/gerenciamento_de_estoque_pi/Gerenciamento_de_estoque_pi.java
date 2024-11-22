/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.gerenciamento_de_estoque_pi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Pichau
 */
public class Gerenciamento_de_estoque_pi {

    public static void main(String[] args) throws SQLException {
        Connection conexao = CriarConexaoBanco();

        Menu(conexao);

        conexao.close();
    }
    
    public static void Menu(Connection conexao) throws SQLException {
    Scanner input = new Scanner(System.in);
    boolean loginValido = false;

    do {
        System.out.println("Olá, seja bem-vindo ao gerenciamento de estoque, digite o que deseja fazer : \n\n"
                + "1. Cadastrar Admin\n"
                + "2. Fazer Login");

        int escolha = input.nextInt();
        input.nextLine(); // Limpar o buffer após nextInt()

        while (escolha < 1 || escolha > 5) {
            System.out.println("Digite uma opção válida");
            escolha = input.nextInt();
            input.nextLine(); // Limpar o buffer após nextInt()
        }

        if (escolha == 1) {
            System.out.println("Você escolheu a opção Cadastrar Admin"
                    + "\nDigite seu nome: ");

            String nome = input.nextLine();

            System.out.println("\nDigite seu cpf: ");
            String cpf = input.nextLine();

            System.out.println("\nDigite seu e-mail: ");
            String email = input.nextLine();

            System.out.println("\n Crie uma senha para o seu login, lembrando que o seu acesso será por e-mail : ");
            String senha = input.nextLine();

            incluirAdmin(conexao, cpf, email, nome, senha);

        } else {

            System.out.println("Bem-vindo! Faça o login:");
            System.out.print("Digite seu e-mail: ");
            String email = input.nextLine();
            System.out.print("Digite sua senha: ");
            String senha = input.nextLine();

            loginValido = validarLogin(conexao, email, senha);
        }

    } while (!loginValido);

    int escolha;
    do {
        // Exibe o menu com as opções
        System.out.println("\nEscolha uma opção:");
        System.out.println("1 - Cadastrar novo produto");
        System.out.println("2 - Deletar produto");
        System.out.println("3 - Consultar produto por ID");
        System.out.println("4 - Consultar todos os produtos");
        System.out.println("5 - Sair");
        System.out.print("Digite a opção desejada: ");

        escolha = input.nextInt(); // Captura a escolha do usuário
        input.nextLine(); // Limpar o buffer após nextInt()

        while (escolha < 1 || escolha > 5) {
            System.out.println("Digite uma opção válida");
            escolha = input.nextInt();
            input.nextLine(); // Limpar o buffer após nextInt()
        }

        // Verifica qual ação o usuário escolheu
        if (escolha == 1) {
            // Chama a função para cadastrar um novo produto
            System.out.println("\nCadastro de novo produto...");

            System.out.println("Digite o nome do produto:");
            String nomeProduto = input.nextLine();

            System.out.println("Digite o preço do produto:");
            float precoProduto = input.nextFloat();
            input.nextLine(); // Limpar o buffer após nextFloat()

            System.out.println("Digite a quantidade em estoque do produto:");
            int qtdEstoque = input.nextInt();
            input.nextLine(); // Limpar o buffer após nextInt()

            System.out.println("Digite o tipo do produto (por exemplo: eletrônico, alimento, etc.):");
            String tipoProduto = input.nextLine();

            incluirProduto(conexao, nomeProduto, precoProduto, qtdEstoque, tipoProduto);
            System.out.println("Produto cadastrado com sucesso!");

        } else if (escolha == 2) {
            // Chama a função para deletar um produto
            System.out.println("\nDeletar produto...");
            System.out.println("Digite o ID do produto que deseja deletar:");
            int idProduto = input.nextInt();
            input.nextLine(); // Limpar o buffer após nextInt()

            deletarProduto(conexao, idProduto);

        } else if (escolha == 3) {
            // Chama a função para consultar produto por ID
            System.out.println("\nConsultar produto por ID...");
            System.out.println("Digite o ID do produto que deseja consultar:");
            int id_produto = input.nextInt();
            input.nextLine(); // Limpar o buffer após nextInt()

            ConsultarProdutoPorID(conexao, id_produto);

        } else if (escolha == 4) {
            // Chama a função para consultar todos os produtos
            System.out.println("\nConsultar todos os produtos...");
            consultarTodosProdutos(conexao);

        } else if (escolha == 5) {
            // Finaliza o programa
            System.out.println("\nSaindo... Até logo!");
            conexao.close();
        } else if (escolha <= 0 && escolha >= 6) {
            // Caso a opção seja inválida
            System.out.println("\nOpção inválida! Digite uma opção entre 1 e 5.");
        }

    } while (escolha != 5); // Enquanto a escolha não for a opção de sair
}


    public static void incluirAdmin(Connection conexao, String cpf, String email, String nome, String senha) throws SQLException {
    // A string que está armazenando a query para inserir o admin
    String sql = "INSERT INTO tb_admin (cpf, email, nome) VALUES (?, ?, ?)";

    PreparedStatement preparedStatement = conexao.prepareStatement(sql);
    preparedStatement.setString(1, cpf);
    preparedStatement.setString(2, email.trim());
    preparedStatement.setString(3, nome);
    preparedStatement.execute();

    // Buscando o ID do administrador para associar ao login
    String sqlIdAdmin = "SELECT id FROM tb_admin WHERE email = ?";
    preparedStatement = conexao.prepareStatement(sqlIdAdmin);
    preparedStatement.setString(1, email.trim());

    ResultSet rs = preparedStatement.executeQuery();

    if (rs.next()) {
        int idAdmin = rs.getInt("id"); // Obtendo o id do admin recém inserido
        incluirLogin(conexao, email, senha, idAdmin);  // Passando o id para o método login
    } else {
        System.out.println("Erro ao buscar ID do admin");
    }
}


    public static void incluirProduto(Connection conexao, String nome, float preco, int qtd_estoque, String tipo) throws SQLException {
        String sql = "INSERT INTO tb_produto (nome,preco,qtd_estoque,tipo) VALUES (?, ?, ?, ?)";

        PreparedStatement preparedstatement = conexao.prepareStatement(sql);

        preparedstatement.setString(1, nome);
        preparedstatement.setDouble(2, preco);
        preparedstatement.setInt(3, qtd_estoque);
        preparedstatement.setString(4, tipo);

        preparedstatement.execute();
    }

    public static void ConsultarProdutoPorID(Connection conexao, int id_produto) throws SQLException {
        // Validação do ID
        if (id_produto <= 0) {
            System.out.println("ID inválido. Por favor, insira um ID maior que zero.");
            return;
        }

        // Query para buscar o produto pelo ID
        String sql = "SELECT * FROM tb_produto WHERE id_produto = ?";
        PreparedStatement preparedStatement = conexao.prepareStatement(sql);
        preparedStatement.setInt(1, id_produto);

        // Executa a consulta
        ResultSet rs = preparedStatement.executeQuery();

        // Verifica se o produto foi encontrado
        if (rs.next()) {
            System.out.println("\nDetalhes do Produto:");
            System.out.println("ID: " + rs.getInt("id_produto"));
            System.out.println("Nome: " + rs.getString("nome"));
            System.out.println("Preço: " + rs.getFloat("preco"));
            System.out.println("Quantidade em Estoque: " + rs.getInt("qtd_estoque"));
            System.out.println("Tipo: " + rs.getString("tipo"));
        } else {
            System.out.println("\nProduto com ID " + id_produto + " não encontrado.");
        }
    }

    public static Connection CriarConexaoBanco() throws SQLException {
        String usuario = "root";
        String senha = "artur0706";
        String caminhoBanco = "jdbc:mysql://localhost:3306/bdpi";
        Connection conexao = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            //variavel que pega a conexão com o banco
            conexao = DriverManager.getConnection(caminhoBanco, usuario, senha);
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver do banco de dados não localizado");
        } catch (SQLException ex) {
            System.out.println("Ocorreu o erro ao acessar o banco" + ex.getMessage());
        }

        return conexao;
    }

   
public static boolean validarLogin(Connection conexao, String email, String senha) throws SQLException {
    if (email == null || email.isEmpty() || senha == null || senha.isEmpty()) {
        System.out.println("E-mail ou senha não podem estar vazios.");
        return false;
    }

    // Verificar e-mail na tabela tb_admin
    String sqlAdmin = "SELECT * FROM tb_admin WHERE email = ?";
    PreparedStatement preparedStatementAdmin = conexao.prepareStatement(sqlAdmin);
    preparedStatementAdmin.setString(1, email.trim());

    ResultSet rsAdmin = preparedStatementAdmin.executeQuery();

    if (!rsAdmin.next()) {
        System.out.println("E-mail não encontrado. Faça o cadastro para prosseguir.");
        rsAdmin.close();
        preparedStatementAdmin.close();
        return false;
    }

    // Obtém o ID do administrador associado ao e-mail
    int adminId = rsAdmin.getInt("id");
    rsAdmin.close();
    preparedStatementAdmin.close();

    // Verificar senha na tabela tb_login
    String sqlLogin = "SELECT * FROM tb_login WHERE id_admin = ? AND senha = ?";
    PreparedStatement preparedStatementLogin = conexao.prepareStatement(sqlLogin);
    preparedStatementLogin.setInt(1, adminId);
    preparedStatementLogin.setString(2, senha.trim());

    ResultSet rsLogin = preparedStatementLogin.executeQuery();

    if (rsLogin.next()) {
        System.out.println("Login efetuado com sucesso!");
        rsLogin.close();
        preparedStatementLogin.close();
        return true;
    } else {
        System.out.println("Senha incorreta. Tente novamente.");
        rsLogin.close();
        preparedStatementLogin.close();
        return false;
    }
}


    public static void incluirLogin(Connection conexao, String email, String senha, int id_admin) throws SQLException {
        
        String sql = "INSERT INTO tb_login (email,senha,id_admin) VALUES (?, ?, ?)";
        System.out.println("Passando pelo metodo login");
        PreparedStatement preparedstatement = conexao.prepareStatement(sql);

        preparedstatement.setString(1, email);
        preparedstatement.setString(2, senha);
        preparedstatement.setInt(3, id_admin);

        preparedstatement.executeUpdate();

    }

    public static void deletarProduto(Connection conexao, int id_produto) throws SQLException {
        String sql = "DELETE FROM tb_produto WHERE id_produto = ?";

        PreparedStatement preparedStatement = conexao.prepareStatement(sql);
        preparedStatement.setInt(1, id_produto);

        int linhasAfetadas = preparedStatement.executeUpdate();
        
        if (linhasAfetadas > 0) {
            System.out.println("Produto com ID " + id_produto + " deletado com sucesso!");
        } else {
            System.out.println("Nenhum produto encontrado com o ID informado.");
        }
    }

    public static void atualizarProduto(Connection conexao, int idProduto, String novoNome, float novoPreco, int novaQuantidade, String novoTipo) throws SQLException {
        String sql = "UPDATE tb_produto SET nome = ?, preco = ?, qtd_estoque = ?, tipo = ? WHERE id = ?";

        PreparedStatement preparedStatement = conexao.prepareStatement(sql);
        preparedStatement.setString(1, novoNome);
        preparedStatement.setFloat(2, novoPreco);
        preparedStatement.setInt(3, novaQuantidade);
        preparedStatement.setString(4, novoTipo);
        preparedStatement.setInt(5, idProduto);

        int linhasAfetadas = preparedStatement.executeUpdate();
        if (linhasAfetadas > 0) {
            System.out.println("Produto atualizado com sucesso!");
        } else {
            System.out.println("Nenhum produto encontrado com o ID informado.");
        }
    }

    public static void consultarTodosProdutos(Connection conexao) throws SQLException {
        String sql = "SELECT * FROM tb_produto";

        Statement statement = conexao.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        System.out.println("\n--- Lista de Produtos ---");
        while (rs.next()) {
            int id = rs.getInt("id_produto");
            String nome = rs.getString("nome");
            float preco = rs.getFloat("preco");
            int qtdEstoque = rs.getInt("qtd_estoque");
            String tipo = rs.getString("tipo");

            System.out.println("ID: " + id);
            System.out.println("Nome: " + nome);
            System.out.println("Preço: " + preco);
            System.out.println("Quantidade em Estoque: " + qtdEstoque);
            System.out.println("Tipo: " + tipo + "\n");
        }
    }
}
