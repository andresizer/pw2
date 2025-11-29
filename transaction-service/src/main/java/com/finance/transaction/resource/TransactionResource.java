package com.finance.transaction.resource;

import com.finance.transaction.dto.*;
import com.finance.transaction.entity.Transaction;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.json.JsonNumber;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.List;
import java.util.stream.Collectors;

@Path("/transactions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class TransactionResource {
    
    @Inject
    JsonWebToken jwt;

    private Long getUserIdFromToken() {
        Object claim = jwt.getClaim("userId");

        if (claim == null) {
            throw new RuntimeException("userId não encontrado no token");
        }

        if (claim instanceof JsonNumber jsonNumber) {
            return jsonNumber.longValue();
        }

        if (claim instanceof String str) {
            return Long.valueOf(str);
        }

        throw new RuntimeException("Formato inesperado para userId: " + claim.getClass());
    }

    /**
     * Cria uma nova transação
     */
    @POST
    @RolesAllowed("USER")
    @Transactional
    public Response create(TransactionRequest request) {
        // Validações
        if (request.description == null || request.description.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Descrição é obrigatória"))
                    .build();
        }
        
        if (request.amount == null || request.amount.signum() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Valor deve ser positivo"))
                    .build();
        }
        
        if (request.type == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Tipo é obrigatório (INCOME ou EXPENSE)"))
                    .build();
        }
        
        // Cria a transação
        Transaction transaction = new Transaction();
        transaction.userId = getUserIdFromToken();
        transaction.description = request.description;
        transaction.amount = request.amount;
        transaction.type = request.type;
        transaction.date = request.date != null ? request.date : java.time.LocalDate.now();
        transaction.persist();
        
        return Response.status(Response.Status.CREATED)
                .entity(new TransactionResponse(transaction))
                .build();
    }
    
    /**
     * Lista todas as transações do usuário
     */
    @GET
    @RolesAllowed("USER")
    public Response list() {
        Long userId = getUserIdFromToken();
        List<Transaction> transactions = Transaction.findByUserId(userId);
        
        List<TransactionResponse> response = transactions.stream()
                .map(TransactionResponse::new)
                .collect(Collectors.toList());
        
        return Response.ok(response).build();
    }
    
    /**
     * Busca uma transação específica
     */
    @GET
    @Path("/{id}")
    @RolesAllowed("USER")
    public Response getById(@PathParam("id") Long id) {
        Long userId = getUserIdFromToken();
        Transaction transaction = Transaction.findByIdAndUserId(id, userId);
        
        if (transaction == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Transação não encontrada"))
                    .build();
        }
        
        return Response.ok(new TransactionResponse(transaction)).build();
    }
    
    /**
     * Atualiza uma transação
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed("USER")
    @Transactional
    public Response update(@PathParam("id") Long id, TransactionRequest request) {
        Long userId = getUserIdFromToken();
        Transaction transaction = Transaction.findByIdAndUserId(id, userId);
        
        if (transaction == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Transação não encontrada"))
                    .build();
        }
        
        // Atualiza os campos
        if (request.description != null) {
            transaction.description = request.description;
        }
        if (request.amount != null) {
            transaction.amount = request.amount;
        }
        if (request.type != null) {
            transaction.type = request.type;
        }
        if (request.date != null) {
            transaction.date = request.date;
        }
        
        return Response.ok(new TransactionResponse(transaction)).build();
    }
    
    /**
     * Deleta uma transação
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed("USER")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Long userId = getUserIdFromToken();
        Transaction transaction = Transaction.findByIdAndUserId(id, userId);
        
        if (transaction == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("Transação não encontrada"))
                    .build();
        }
        
        transaction.delete();
        
        return Response.noContent().build();
    }
    
    /**
     * Obtém o saldo do usuário
     */
    @GET
    @Path("/balance")
    @RolesAllowed("USER")
    public Response getBalance() {
        Long userId = getUserIdFromToken();
        java.math.BigDecimal balance = Transaction.calculateBalance(userId);
        
        return Response.ok(new BalanceResponse(balance, userId)).build();
    }
}