package com.finance.auth.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.persistence.*;

/**
 * Entidade User - Representa um usuário do sistema
 * Extende PanacheEntity para usar os métodos do Panache
 */
@Entity
@Table(name = "users")
public class User extends PanacheEntity {
    
    /**
     * Nome de usuário único
     */
    @Column(unique = true, nullable = false, length = 50)
    public String username;
    
    /**
     * Senha criptografada com BCrypt
     */
    @Column(nullable = false, length = 255)
    public String password;
    
    /**
     * Role do usuário (ex: USER, ADMIN)
     */
    @Column(nullable = false, length = 20)
    public String role = "USER";
    
    /**
     * Construtor padrão (necessário para JPA)
     */
    public User() {
    }
    
    /**
     * Construtor com parâmetros
     */
    public User(String username, String password, String role) {
        this.username = username;
        this.password = BcryptUtil.bcryptHash(password);
        this.role = role;
    }
    
    /**
     * Adiciona um novo usuário com senha criptografada
     * 
     * @param username Nome de usuário
     * @param password Senha em texto plano (será criptografada)
     * @param role Role do usuário
     */
    public static void add(String username, String password, String role) {
        User user = new User();
        user.username = username;
        user.password = BcryptUtil.bcryptHash(password);
        user.role = role != null ? role : "USER";
        user.persist();
    }
    
    /**
     * Busca usuário por username
     * 
     * @param username Nome de usuário
     * @return User encontrado ou null
     */
    public static User findByUsername(String username) {
        return find("username", username).firstResult();
    }
    
    /**
     * Valida a senha do usuário
     * 
     * @param password Senha em texto plano
     * @return true se a senha estiver correta
     */
    public boolean validatePassword(String password) {
        return BcryptUtil.matches(password, this.password);
    }
    
    /**
     * Verifica se o username já existe
     * 
     * @param username Nome de usuário
     * @return true se já existir
     */
    public static boolean usernameExists(String username) {
        return count("username", username) > 0;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}