package models;

public class CourierCredentialsWithoutLogin {
    private final String password;

    public CourierCredentialsWithoutLogin(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}