package controller;

import model.*;
import service.*;
import repository.*;

import java.util.*;

public class PedidoController {

    private PedidoService service = new PedidoService();
    private PedidoRepository repo = new PedidoRepository();

    public Pedido criarPedido(String nome, Map<String, Integer> itensQtd) {

        Cliente cliente = new Cliente(nome);
        Pedido pedido = new Pedido(cliente);

        List<ItemMenu> menu = new ArrayList<>();
        menu.add(new ItemMenu("Hamburguer", 25));
        menu.add(new ItemMenu("Pizza", 40));
        menu.add(new ItemMenu("Refrigerante", 8));
        menu.add(new ItemMenu("Batata Frita", 15));

        for (int i = 0; i < menu.size(); i++) {
            int qtd = itensQtd.getOrDefault(menu.get(i).getNome(), 0);
            if (qtd > 0) {
                service.adicionarItem(pedido, menu.get(i), qtd);
            }
        }

        pedido.setStatus("EM PREPARO");

        repo.salvar(pedido);

        return pedido;
    }

    public double calcularTotal(Pedido pedido) {
        return service.calcularTotal(pedido);
    }

    public List<String> listarPedidos() {
        return repo.listarPedidos();
    }
    public void atualizarStatus(int id, String status) {
        repo.atualizarStatus(id, status);
    }
    public Map<String, Object> gerarRelatorio() {

        List<String> linhas = repo.lerLinhasArquivo();

        double total = 0;
        int pedidos = 0;
        Map<String, Integer> contagemItens = new HashMap<>();

        for (String linha : linhas) {

            if (linha.equals("-----")) {
                pedidos++;
            }

            if (linha.contains(" - R$ ")) {
                String[] partes = linha.split(" - R\\$ ");
                String nome = partes[0];
                double valor = Double.parseDouble(partes[1]);

                total += valor;

                contagemItens.put(nome, contagemItens.getOrDefault(nome, 0) + 1);
            }
        }

        // 🔥 item mais vendido
        String maisVendido = "Nenhum";
        int max = 0;

        for (String item : contagemItens.keySet()) {
            if (contagemItens.get(item) > max) {
                max = contagemItens.get(item);
                maisVendido = item;
            }
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("total", total);
        resultado.put("pedidos", pedidos);
        resultado.put("maisVendido", maisVendido);

        return resultado;
    }
}

