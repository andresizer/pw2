package com.finance.auth.resource;

import com.finance.auth.dto.*;
import com.finance.auth.entity.User;
import io.smallrye.jwt.build.Jwt;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Arrays;
import java.util.HashSet;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    
    /**
     * Registra um novo usuário
     */
    @POST
    @Path("/register")
    @Transactional
    public Response register(RegisterRequest request) {
        // Validações
        if (request.username == null || request.username.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Username é obrigatório"))
                    .build();
        }
        
        if (request.password == null || request.password.length() < 4) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Senha deve ter no mínimo 4 caracteres"))
                    .build();
        }
        
        // Verifica se usuário já existe
        if (User.findByUsername(request.username) != null) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse("Username já existe"))
                    .build();
        }
        
        // Cria o usuário
        User.add(request.username, request.password, request.role);
        
        return Response.status(Response.Status.CREATED)
                .entity(new ErrorResponse("Usuário criado com sucesso"))
                .build();
    }
    
    /**
     * Realiza o login e retorna JWT
     */
    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        // Busca o usuário
        User user = User.findByUsername(request.username);
        
        if (user == null || !user.validatePassword(request.password)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(new ErrorResponse("Credenciais inválidas"))
                    .build();
        }
        
        // Gera o JWT
        String token = Jwt.issuer("https://finance-auth-service")
                .upn(user.username)
                .groups(new HashSet<>(Arrays.asList(user.role)))
                .claim("userId", user.id)
                .sign();
        
        return Response.ok(new AuthResponse(token, user.username, user.role)).build();
    }
}