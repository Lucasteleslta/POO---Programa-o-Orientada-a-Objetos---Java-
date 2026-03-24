package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import controller.LoginController;
import controller.PedidoController;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Servidor {

    static String usuarioLogado = null;
    static boolean ehColaborador = false;

    // 🔥 DESIGN SYSTEM PREMIUM - Restaurante.App
    private static final String UI_STYLE = """
        <style>
            @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;600;700&display=swap');
            
            :root { 
                --primary: #fbbf24; 
                --primary-hover: #f59e0b;
                --bg: #0a0f1a; 
                --surface: #161e2d;
                --text-main: #f8fafc;
                --text-muted: #94a3b8;
                --border: #2d3748;
                --success: #10b981;
                --danger: #ef4444;
                --shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.5);
            }

            * { margin: 0; padding: 0; box-sizing: border-box; }

            body { 
                font-family: 'Plus Jakarta Sans', sans-serif; 
                background-color: var(--bg); 
                color: var(--text-main); 
                line-height: 1.6;
                display: flex; flex-direction: column; min-height: 100vh;
            }

            header {
                background: rgba(22, 30, 45, 0.8);
                backdrop-filter: blur(10px);
                padding: 1rem 5%;
                display: flex; justify-content: space-between; align-items: center;
                border-bottom: 1px solid var(--border);
                position: sticky; top: 0; z-index: 100;
                width: 100%;
            }

            .logo { font-weight: 800; font-size: 1.5rem; color: var(--primary); letter-spacing: -1px; text-decoration: none; }
            
            .user-info { display: flex; align-items: center; gap: 15px; font-weight: 600; color: var(--text-muted); }
            
            .btn-logout { 
                background: var(--danger); color: white; padding: 6px 14px; 
                border-radius: 8px; font-size: 0.75rem; text-decoration: none; transition: 0.3s;
                font-weight: 700;
            }
            .btn-logout:hover { filter: brightness(1.2); transform: translateY(-1px); }

            .main-content {
                flex: 1;
                width: 100%;
                max-width: 1200px;
                margin: 0 auto;
                padding: 40px 20px;
                display: flex; flex-direction: column;
                align-items: center; /* Centraliza horizontalmente */
                justify-content: center; /* Centraliza verticalmente */
            }

            .card {
                background: var(--surface);
                border: 1px solid var(--border);
                border-radius: 20px;
                padding: 2.5rem;
                box-shadow: var(--shadow);
                width: 100%;
            }

            /* Histórico: 3 por linha */
            .history-grid {
                display: grid;
                grid-template-columns: repeat(3, 1fr);
                gap: 1.5rem;
                width: 100%;
                margin-top: 1.5rem;
            }

            .menu-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 1.5rem; width: 100%;
            }

            .menu-card, .order-card {
                background: var(--surface);
                border: 1px solid var(--border);
                border-radius: 16px;
                padding: 1.5rem;
                transition: 0.3s;
                box-shadow: 0 4px 6px -1px rgba(0,0,0,0.2);
            }
            .menu-card:hover { border-color: var(--primary); transform: translateY(-4px); }

            .price-tag { color: var(--primary); font-weight: 800; font-size: 1.3rem; margin: 10px 0; display: block; }

            .btn {
                display: inline-flex; align-items: center; justify-content: center;
                padding: 0.9rem 2rem; border-radius: 12px;
                font-weight: 700; cursor: pointer; border: none;
                transition: all 0.3s; text-decoration: none;
                background: var(--primary); color: #000;
                width: 100%; text-transform: uppercase; letter-spacing: 0.5px;
            }
            .btn:hover { background: var(--primary-hover); transform: translateY(-2px); box-shadow: 0 4px 12px rgba(251, 191, 36, 0.3); }
            .btn-outline { background: transparent; border: 2px solid var(--border); color: var(--text-main); }
            .btn-outline:hover { background: var(--border); }

            .badge {
                padding: 4px 10px; border-radius: 6px; font-size: 0.65rem; font-weight: 800;
                text-transform: uppercase; background: var(--border); color: var(--primary);
            }

            input {
                width: 100%; padding: 0.8rem; border-radius: 10px; border: 1px solid var(--border);
                background: #0f172a; color: white; margin-bottom: 1rem;
            }

            @media (max-width: 1024px) { .history-grid { grid-template-columns: repeat(2, 1fr); } }
            @media (max-width: 650px) { .history-grid { grid-template-columns: 1fr; } }
        </style>
        """;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/", new HomeHandler());
        server.createContext("/login", new LoginHandler());
        server.createContext("/painel", new PainelHandler());
        server.createContext("/menu", new MenuHandler());
        server.createContext("/pedido", new PedidoHandler());
        server.createContext("/pedidos", new ListaPedidosHandler());
        server.createContext("/updateStatus", new UpdateStatusHandler());
        server.createContext("/relatorio", new RelatorioHandler());
        server.createContext("/logout", new LogoutHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("🚀 Restaurante.App pronto para produção: http://localhost:8080");
    }

    private static String getHeader() {
        if (usuarioLogado == null) return "<header><div class='logo'>Restaurante.App</div></header>";
        return String.format("""
            <header>
                <a href='/painel' class='logo'>Restaurante.App</a>
                <div class='user-info'>
                    <span>%s</span>
                    <a href='/logout' class='btn-logout'>SAIR</a>
                </div>
            </header>
            """, usuarioLogado);
    }

    // TELA INICIAL (LOGIN)
    static class HomeHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String html = "<html><head><title>Login | Restaurante.App</title>" + UI_STYLE + "</head><body>" + 
                getHeader() +
                "<div class='main-content'><div class='card' style='max-width:400px; text-align:center;'>" +
                "<h2 style='margin-bottom:2rem'>Acesso ao Sistema</h2>" +
                "<form action='/login' method='POST'>" +
                "<input type='text' name='email' placeholder='E-mail' required>" +
                "<input type='password' name='senha' placeholder='Senha' required>" +
                "<button type='submit' class='btn'>ENTRAR</button>" +
                "</form></div></div></body></html>";
            sendResponse(exchange, html);
        }
    }

    // LOGICA DE REDIRECIONAMENTO POS-LOGIN
    static class LoginHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parse(body);
                usuarioLogado = params.get("email");
                ehColaborador = new LoginController().isFuncionario(usuarioLogado, params.get("senha"));
                
                // Redireciona para o painel principal (onde se escolhe Menu ou Gestão)
                exchange.getResponseHeaders().set("Location", "/painel");
                exchange.sendResponseHeaders(303, -1);
            }
        }
    }

    // TELA "OLÁ COLABORADOR" OU "BEM-VINDO CLIENTE"
    static class PainelHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if (usuarioLogado == null) { redirect(exchange, "/"); return; }
            
            String content = ehColaborador ? """
                <h2>Olá Colaborador!</h2>
                <p style='color:var(--text-muted); margin-bottom: 2rem;'>O que deseja gerenciar hoje?</p>
                <div style='display: grid; gap: 1rem;'>
                    <a href="/pedidos" class="btn">GESTÃO DE PEDIDOS</a>
                    <a href="/relatorio" class="btn btn-outline">RELATÓRIOS</a>
                </div>
                """ : """
                <h2>Bem-vindo ao Restaurante.App</h2>
                <p style='color:var(--text-muted); margin-bottom: 2rem;'>Explore nosso cardápio exclusivo.</p>
                <a href="/menu" class="btn">FAZER MEU PEDIDO</a>
                """;

            String html = "<html><head>" + UI_STYLE + "</head><body>" + getHeader() +
                          "<div class='main-content'><div class='card' style='max-width:500px; text-align:center;'>" + content + "</div></div></body></html>";
            sendResponse(exchange, html);
        }
    }

    static class MenuHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if (usuarioLogado == null) { redirect(exchange, "/"); return; }
            String html = "<html><head><title>Cardápio</title>" + UI_STYLE + "</head><body>" + getHeader() +
                "<div class='main-content'>" +
                "<h1 style='margin-bottom: 2rem;'>Cardápio Digital</h1>" +
                "<form action='/pedido' method='POST' style='width:100%'>" +
                "<div class='menu-grid'>" +
                renderItem("Hamburguer Artesanal", "180g de carne angus.", 25, "hamburguer") +
                renderItem("Pizza Napolitana", "Molho de tomate pelado.", 40, "pizza") +
                renderItem("Refrigerante", "Lata 350ml.", 8, "refri") +
                renderItem("Batata Rústica", "Com alecrim.", 15, "batata") +
                "</div>" +
                "<div style='margin-top: 2rem; text-align: center;'><button type='submit' class='btn' style='width:auto; padding: 1rem 4rem;'>CONFIRMAR PEDIDO</button></div>" +
                "</form></div></body></html>";
            sendResponse(exchange, html);
        }
        private String renderItem(String n, String d, int p, String i) {
            return String.format("<div class='menu-card'><h3>%s</h3><p style='color:var(--text-muted)'>%s</p><span class='price-tag'>R$ %d,00</span><input type='number' name='%s' value='0' min='0'></div>", n, d, p, i);
        }
    }

    static class ListaPedidosHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if (usuarioLogado == null) { redirect(exchange, "/"); return; }
            List<String> pedidos = new PedidoController().listarPedidos();
            StringBuilder html = new StringBuilder("<html><head>" + UI_STYLE + "</head><body>" + getHeader() + "<div class='main-content'><h1>Gestão de Pedidos</h1><div class='history-grid'>");
            for (String p : pedidos) {
                String id = p.split("<br>")[0].replace("ID: ", "").trim();
                html.append("<div class='order-card'><div style='font-size:0.85rem; margin-bottom:1rem;'>").append(p).append("</div>")
                    .append("<div style='display:flex; gap:5px;'><a href='/updateStatus?id=").append(id).append("&status=PRONTO' class='btn' style='font-size:0.7rem; background:var(--success)'>PRONTO</a>")
                    .append("<a href='/updateStatus?id=").append(id).append("&status=ENTREGUE' class='btn' style='font-size:0.7rem; background:#3b82f6; color:white'>ENTREGUE</a></div></div>");
            }
            html.append("</div><a href='/painel' class='btn btn-outline' style='margin-top:2rem; width:auto;'>VOLTAR</a></div></body></html>");
            sendResponse(exchange, html.toString());
        }
    }

    static class RelatorioHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, Object> r = new PedidoController().gerarRelatorio();
            String html = "<html><head>" + UI_STYLE + "</head><body>" + getHeader() + "<div class='main-content'><h1>Relatórios</h1><div class='menu-grid'>" +
                "<div class='card' style='text-align:center'><h3>Faturamento</h3><span class='price-tag'>R$ " + r.get("total") + "</span></div>" +
                "</div><a href='/painel' class='btn btn-outline' style='margin-top:2rem; width:auto;'>VOLTAR</a></div></body></html>";
            sendResponse(exchange, html);
        }
    }

    static class LogoutHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            usuarioLogado = null;
            redirect(exchange, "/");
        }
    }

    static class PedidoHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> params = parse(body);
            Map<String, Integer> itens = new HashMap<>();
            itens.put("Hamburguer", Integer.parseInt(params.getOrDefault("hamburguer", "0")));
            itens.put("Pizza", Integer.parseInt(params.getOrDefault("pizza", "0")));
            itens.put("Refrigerante", Integer.parseInt(params.getOrDefault("refri", "0")));
            itens.put("Batata Frita", Integer.parseInt(params.getOrDefault("batata", "0")));
            new PedidoController().criarPedido(usuarioLogado, itens);
            
            String html = "<html><head>" + UI_STYLE + "</head><body>" + getHeader() + "<div class='main-content'><div class='card' style='text-align:center'><h2>Sucesso!</h2><p>Seu pedido foi enviado para a cozinha.</p><a href='/menu' class='btn' style='margin-top:2rem'>NOVO PEDIDO</a></div></div></body></html>";
            sendResponse(exchange, html);
        }
    }

    static class UpdateStatusHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            Map<String, String> params = new HashMap<>();
            for (String p : query.split("&")) { String[] kv = p.split("="); params.put(kv[0], kv[1]); }
            new PedidoController().atualizarStatus(Integer.parseInt(params.get("id")), params.get("status"));
            redirect(exchange, "/pedidos");
        }
    }

    private static void redirect(HttpExchange exchange, String url) throws IOException {
        exchange.getResponseHeaders().set("Location", url);
        exchange.sendResponseHeaders(303, -1);
    }

    private static void sendResponse(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    public static Map<String, String> parse(String body) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        if (body.isEmpty()) return map;
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=");
            map.put(URLDecoder.decode(kv[0], "UTF-8"), kv.length > 1 ? URLDecoder.decode(kv[1], "UTF-8") : "");
        }
        return map;
    }
}

