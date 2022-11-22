package edu.puj.popinapp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private String email;
    private String name;
    private String lastname;
    private String direction;
    private String date;
    private String gender;
    private long phoneNumber;
    private String tipoUsuario;
    private Double latitude;
    private Double longitude;
    private String photo;

}
