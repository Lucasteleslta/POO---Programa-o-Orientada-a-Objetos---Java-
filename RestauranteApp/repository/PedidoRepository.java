package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.ItemPedido;
import model.Pedido;

public class PedidoRepository {

    public void salvar(Pedido pedido) {
        try {
            // Garante que a pasta existe
            File pasta = new File("data");
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            // Cria o arquivo
            File arquivo = new File("data/pedidos.txt");

            FileWriter writer = new FileWriter(arquivo, true);

            writer.write("ID: " + pedido.getId() + "\n");
            writer.write("Cliente: " + pedido.getCliente().getNome() + "\n");

            for (ItemPedido item : pedido.getItens()) {
                writer.write(item.getNome() + " - R$ " + item.getSubtotal() + "\n");
            }

            writer.write("Status: " + pedido.getStatus() + "\n");
            writer.write("-----\n");

            writer.close();

            System.out.println("✅ Pedido salvo em: " + arquivo.getAbsolutePath());

        } catch (IOException e) {
            System.out.println("❌ Erro real:");
            e.printStackTrace();
        }
    }

    public List<String> listarPedidos() {
        List<String> pedidos = new ArrayList<>();

        try {
            File arquivo = new File("data/pedidos.txt");

            if (!arquivo.exists()) return pedidos;

            BufferedReader reader = new BufferedReader(new FileReader(arquivo));

            String linha;
            StringBuilder pedidoAtual = new StringBuilder();

            while ((linha = reader.readLine()) != null) {

                if (linha.equals("-----")) {
                    pedidos.add(pedidoAtual.toString());
                    pedidoAtual = new StringBuilder();
                } else {
                    pedidoAtual.append(linha).append("<br>");
                }
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return pedidos;
    }

    public void atualizarStatus(int id, String novoStatus) {
        try {
            File arquivo = new File("data/pedidos.txt");
            List<String> linhas = new ArrayList<>();

            BufferedReader reader = new BufferedReader(new FileReader(arquivo));
            String linha;

            boolean encontrou = false;

            while ((linha = reader.readLine()) != null) {

                if (linha.startsWith("ID: " + id)) {
                    encontrou = true;
                }

                if (encontrou && linha.startsWith("Status:")) {
                    linha = "Status: " + novoStatus;
                    encontrou = false;
                }

                linhas.add(linha);
            }

            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo));
            for (String l : linhas) {
                writer.write(l + "\n");
            }
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<String> lerLinhasArquivo() {
        List<String> linhas = new ArrayList<>();

        try {
            File arquivo = new File("data/pedidos.txt");

            if (!arquivo.exists()) return linhas;

            BufferedReader reader = new BufferedReader(new FileReader(arquivo));
            String linha;

            while ((linha = reader.readLine()) != null) {
                linhas.add(linha);
            }

            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return linhas;
    }
}
