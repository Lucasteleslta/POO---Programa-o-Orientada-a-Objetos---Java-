package controller;

public class LoginController {

    // 🔐 login fixo de funcionário
    private final String EMAIL_FUNC = "adm@gmail.com";
    private final String SENHA_FUNC = "adm123";

    public boolean isFuncionario(String email, String senha) {
        return EMAIL_FUNC.equals(email) && SENHA_FUNC.equals(senha);
    }
}