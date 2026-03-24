package model;

public class ItemPedido {
    private ItemMenu item;
    private int quantidade;

    public ItemPedido(ItemMenu item, int quantidade) {
        this.item = item;
        this.quantidade = quantidade;
    }

    public double getSubtotal() {
        return item.getPreco() * quantidade;
    }

    public String getNome() {
        return item.getNome();
    }
}