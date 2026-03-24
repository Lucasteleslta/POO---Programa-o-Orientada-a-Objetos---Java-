package service;

import model.*;

public class PedidoService {

    public void adicionarItem(Pedido pedido, ItemMenu item, int qtd) {
        pedido.adicionarItem(new ItemPedido(item, qtd));
    }

    public double calcularTotal(Pedido pedido) {
        return pedido.getItens()
                .stream()
                .mapToDouble(ItemPedido::getSubtotal)
                .sum();
    }

    public void mostrarResumo(Pedido pedido) {
        System.out.println("\n=== RESUMO DO PEDIDO ===");

        for (ItemPedido item : pedido.getItens()) {
            System.out.println(item.getNome() + " - R$ " + item.getSubtotal());
        }

        System.out.println("Total: R$ " + calcularTotal(pedido));
    }
}