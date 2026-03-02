package com.example.catalog.application;

/**
 * Representa uma intenção de recuperar dados do sistema (READ).
 * <p>
 * <strong>Conceito CQS (Command Query Separation):</strong>
 * Uma Consulta é uma operação idempotente: pode ser executada múltiplas vezes sem alterar o estado do sistema.
 * Sua única responsabilidade é retornar dados da forma mais eficiente possível.
 * </p>
 * <p>
 * <strong>Padrão CQRS (Arquitetura):</strong>
 * Consultas podem ignorar o modelo de domínio (Entidades ricas) e acessar diretamente
 * modelos de leitura (DTOs, Projeções) otimizados para a tela/cliente.
 * </p>
 *
 * @param <IN>  Critérios de filtro ou busca.
 * @param <OUT> Modelo de leitura (DTO) otimizado para o cliente.
 */
public abstract class Query<IN, OUT> extends UseCase<IN, OUT> {
}
