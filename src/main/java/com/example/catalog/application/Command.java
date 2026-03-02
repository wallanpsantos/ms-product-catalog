package com.example.catalog.application;

/**
 * Representa uma intenção de alterar o estado do sistema (WRITE).
 * <p>
 * <strong>Conceito CQS (Command Query Separation):</strong>
 * Um Comando é uma operação que produz efeitos colaterais (criação, atualização, exclusão)
 * mas não deve retornar dados complexos. Idealmente retorna {@code void} ou apenas o ID do recurso criado.
 * </p>
 * <p>
 * <strong>Por que usar?</strong>
 * Ao separar explicitamente Comandos de Consultas, garantimos que a lógica de negócio (regras, validações)
 * fique isolada da lógica de apresentação, permitindo otimizações independentes.
 * </p>
 *
 * @param <IN>  Dados necessários para executar a ação (ex: DTO de criação).
 * @param <OUT> Resultado mínimo da operação (ex: ID).
 */
public abstract class Command<IN, OUT> extends UseCase<IN, OUT> {
}
