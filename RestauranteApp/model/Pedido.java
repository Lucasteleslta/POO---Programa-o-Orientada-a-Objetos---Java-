package model;

import java.util.*;

public class Pedido {

    private static int contador = 1;

    private int id;
    private Cliente cliente;
    private List<ItemPedido> itens = new ArrayList<>();
    private String status = "CRIADO";

    public Pedido(Cliente cliente) {
        this.cliente = cliente;
        this.id = contador++;
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}