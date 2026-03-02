package com.example.catalog.application;

import java.util.function.Function;

/**
 * Interface funcional para transformação de saída dos Casos de Uso.
 * <p>
 * Implementa o padrão <strong>Transformer/Strategy</strong> para a camada de apresentação.
 * Permite que o chamador (Controller, CLI, Teste) decida como quer receber os dados
 * do Caso de Uso, sem acoplar a lógica de negócio à interface gráfica/API.
 * </p>
 * <p>
 * <strong>Exemplo:</strong>
 * O UseCase retorna um `ProductOutput`. O Controller pode passar um Presenter que
 * converte esse Output em uma `ResponseEntity<ProductWebResponse>` ou em uma String JSON.
 * </p>
 *
 * @param <UC_OUT>  Tipo de saída original do Caso de Uso.
 * @param <NEW_OUT> Novo tipo de retorno desejado após a apresentação.
 */
public interface Presenter<UC_OUT, NEW_OUT> extends Function<UC_OUT, NEW_OUT> {
}
