# Restaurante.App

Sistema de gerenciamento de pedidos desenvolvido em Java e orientado a objetos, com interface web simples utilizando servidor HTTP nativo.


## Sobre o Projeto

O **Restaurante.App** é uma aplicação completa que simula o funcionamento de um sistema de pedidos de restaurante, com:

* Interface web acessível via navegador
* Sistema de login com perfis diferentes
* Criação e gerenciamento de pedidos
* Controle de status em tempo real
* Relatórios de vendas


## Funcionalidades

### Cliente

* Login com qualquer e-mail e senha
* Acesso ao cardápio digital
* Criação de pedidos
* Visualização do status do pedido


### Funcionário

* Login com credenciais fixas
* Visualização de todos os pedidos
* Atualização de status:

  * EM PREPARO
  * PRONTO
  * ENTREGUE
* Acesso ao relatório de vendas


### Histórico de Pedidos

* Armazenamento em arquivo `.txt`
* Exibição no sistema web
* Cada pedido possui:

  * ID único
  * Cliente (email)
  * Itens
  * Status
  * Total


### 📊 Relatório

* Total vendido
* Quantidade de pedidos
* Item mais vendido


##  Arquitetura

O projeto foi estruturado seguindo boas práticas de organização:

```
RestauranteApp
 ┣  app           → Execução via terminal
 ┣  server        → Servidor web (HTTP)
 ┣  controller    → Regras de controle (login, pedidos)
 ┣  service       → Regras de negócio
 ┣  repository    → Persistência (arquivo .txt)
 ┣  model         → Entidades (Pedido, Cliente, etc)
 ┗  data          → Armazenamento dos pedidos
```

## Tecnologias Utilizadas

* Java 21+
* HTTP Server nativo (`com.sun.net.httpserver`)
* HTML + CSS (inline)
* Manipulação de arquivos (`FileWriter`, `BufferedReader`)


## Como Executar

### Compilar o projeto
```
javac -d . app\Main.java model\*.java service\*.java repository\*.java controller\*.java server\Servidor.java
```


### Rodar servidor web
```
java server.Servidor
```

### Acesse no navegador:
```
http://localhost:8080
```

---

### Rodar versão terminal
```
java app.Main
```


## Credenciais

### Funcionário
```
Email: adm@gmail.com
Senha: adm123
```

### Cliente
```
Pode usar qualquer e-mail e senha
```

## Armazenamento

Os pedidos são salvos em:
```
/data/pedidos.txt
```


---
