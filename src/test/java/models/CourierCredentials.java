package models;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class CourierCredentials {
    private String login;
    private String password;

}