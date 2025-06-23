package models;

public class CourierCredentialsWithoutPassword {
    private final String login;

    public CourierCredentialsWithoutPassword(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }
}