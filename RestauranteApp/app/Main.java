package app;

import controller.LoginController;
import controller.PedidoController;
import model.Pedido;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        LoginController login = new LoginController();
        PedidoController controller = new PedidoController();

        System.out.println("=== SISTEMA DE PEDIDOS ===");

        // 🔐 LOGIN
        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Senha: ");
        String senha = sc.nextLine();

        boolean isFuncionario = login.isFuncionario(email, senha);

        if (isFuncionario) {
            menuFuncionario(sc, controller);
        } else {
            menuCliente(sc, controller, email);
        }
    }

    // 👨‍🍳 MENU FUNCIONÁRIO
    public static void menuFuncionario(Scanner sc, PedidoController controller) {

        int opcao;

        do {
            System.out.println("\n=== MENU FUNCIONÁRIO ===");
            System.out.println("1 - Ver pedidos");
            System.out.println("2 - Atualizar status");
            System.out.println("3 - Ver relatório");
            System.out.println("0 - Sair");
            System.out.print("Escolha: ");

            opcao = sc.nextInt();
            sc.nextLine(); // limpar buffer

            switch (opcao) {

                case 1:
                    List<String> pedidos = controller.listarPedidos();

                    if (pedidos.isEmpty()) {
                        System.out.println("Nenhum pedido encontrado.");
                    } else {
                        for (String p : pedidos) {
                            System.out.println("\n----------------");
                            System.out.println(p.replace("<br>", "\n"));
                        }
                    }
                    break;

                case 2:
                    System.out.print("Digite o ID do pedido: ");
                    int id = sc.nextInt();
                    sc.nextLine();

                    System.out.println("Novo status:");
                    System.out.println("1 - EM PREPARO");
                    System.out.println("2 - PRONTO");
                    System.out.println("3 - ENTREGUE");

                    int s = sc.nextInt();
                    sc.nextLine();

                    String status = switch (s) {
                        case 1 -> "EM PREPARO";
                        case 2 -> "PRONTO";
                        case 3 -> "ENTREGUE";
                        default -> "EM PREPARO";
                    };

                    controller.atualizarStatus(id, status);
                    System.out.println("Status atualizado!");
                    break;

                case 3:
                    Map<String, Object> r = controller.gerarRelatorio();

                    System.out.println("\n=== RELATÓRIO ===");
                    System.out.println("Total vendido: R$ " + r.get("total"));
                    System.out.println("Pedidos: " + r.get("pedidos"));
                    System.out.println("Mais vendido: " + r.get("maisVendido"));
                    break;
            }

        } while (opcao != 0);
    }

    // 🍔 MENU CLIENTE
    public static void menuCliente(Scanner sc, PedidoController controller, String email) {

        Map<String, Integer> itens = new HashMap<>();

        System.out.println("\n=== CARDÁPIO ===");

        System.out.print("Hamburguer: ");
        itens.put("Hamburguer", sc.nextInt());

        System.out.print("Pizza: ");
        itens.put("Pizza", sc.nextInt());

        System.out.print("Refrigerante: ");
        itens.put("Refrigerante", sc.nextInt());

        System.out.print("Batata Frita: ");
        itens.put("Batata Frita", sc.nextInt());

        sc.nextLine(); // limpar buffer

        Pedido pedido = controller.criarPedido(email, itens);

        double total = controller.calcularTotal(pedido);

        System.out.println("\n=== RESUMO ===");
        System.out.println("Cliente: " + email);
        System.out.println("Status: " + pedido.getStatus());
        System.out.println("Total: R$ " + total);

        System.out.println("\nPedido realizado com sucesso!");
    }
}