package app.project.entity;

import lombok.*;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User {
    private Long id;
    private String phone;
    private String email;
    private String fio;
}
