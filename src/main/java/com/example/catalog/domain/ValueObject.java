package com.example.catalog.domain;

/**
 * Interface marcadora para todos os Objetos de Valor (Value Objects) do domínio.
 * <p>
 * Um <strong>Value Object</strong> é um objeto que não possui identidade própria; ele é definido
 * apenas pelos seus atributos. Eles devem ser imutáveis e substituíveis.
 * </p>
 * <p>
 * <strong>Características:</strong>
 * <ul>
 *   <li>Imutabilidade: Uma vez criado, não muda.</li>
 *   <li>Igualdade por valor: Dois VOs com os mesmos dados são iguais.</li>
 *   <li>Auto-validáveis: Não podem ser criados em estado inválido.</li>
 * </ul>
 * </p>
 */
public interface ValueObject {
}
