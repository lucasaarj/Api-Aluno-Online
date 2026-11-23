package br.com.alunoonline.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 254)
    private String login;

    @Column(nullable = false, length = 255)
    private String senha;

    @OneToOne
    @JoinColumn(name = "aluno_id", unique = true)
    private Aluno aluno;

    @OneToOne
    @JoinColumn(name = "professor_id", unique = true)
    private Professor professor;
}
