package com.example.catalog.infrastructure.config;

import com.example.catalog.application.port.input.CreateProductBatchUseCase;
import com.example.catalog.application.port.input.CreateProductUseCase;
import com.example.catalog.application.port.input.DeactivateProductUseCase;
import com.example.catalog.application.port.input.GetProductByIdUseCase;
import com.example.catalog.application.port.input.ListActiveProductsUseCase;
import com.example.catalog.application.port.input.SearchProductsUseCase;
import com.example.catalog.application.port.input.UpdateProductBatchUseCase;
import com.example.catalog.application.port.input.UpdateProductUseCase;
import com.example.catalog.application.port.output.ProductCommandGateway;
import com.example.catalog.application.port.output.ProductQueryGateway;
import com.example.catalog.application.usecase.DefaultCreateProductBatchUseCase;
import com.example.catalog.application.usecase.DefaultCreateProductUseCase;
import com.example.catalog.application.usecase.DefaultDeactivateProductUseCase;
import com.example.catalog.application.usecase.DefaultGetProductByIdUseCase;
import com.example.catalog.application.usecase.DefaultListActiveProductsUseCase;
import com.example.catalog.application.usecase.DefaultSearchProductsUseCase;
import com.example.catalog.application.usecase.DefaultUpdateProductBatchUseCase;
import com.example.catalog.application.usecase.DefaultUpdateProductUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A classe de "cola" (Glue Code) da Arquitetura Hexagonal.
 * <p>
 * Responsável por conectar os Casos de Uso (POJOs puros) com suas dependências (Portas de Saída).
 * Os Casos de Uso NÃO são anotados com `@Service` ou `@Component` do Spring para manter a camada de aplicação
 * desacoplada do framework. Eles são instanciados explicitamente aqui como Spring Beans.
 * </p>
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public CreateProductUseCase createProductUseCase(final ProductCommandGateway gateway) {
        return new DefaultCreateProductUseCase(gateway);
    }

    @Bean
    public CreateProductBatchUseCase createProductBatchUseCase(final ProductCommandGateway gateway) {
        return new DefaultCreateProductBatchUseCase(gateway);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(final ProductCommandGateway gateway) {
        return new DefaultUpdateProductUseCase(gateway);
    }

    @Bean
    public UpdateProductBatchUseCase updateProductBatchUseCase(final ProductCommandGateway gateway) {
        return new DefaultUpdateProductBatchUseCase(gateway);
    }

    @Bean
    public GetProductByIdUseCase getProductByIdUseCase(final ProductQueryGateway gateway) {
        return new DefaultGetProductByIdUseCase(gateway);
    }

    @Bean
    public ListActiveProductsUseCase listActiveProductsUseCase(final ProductQueryGateway gateway) {
        return new DefaultListActiveProductsUseCase(gateway);
    }

    @Bean
    public SearchProductsUseCase searchProductsUseCase(final ProductQueryGateway gateway) {
        return new DefaultSearchProductsUseCase(gateway);
    }

    @Bean
    public DeactivateProductUseCase deactivateProductUseCase(final ProductCommandGateway gateway) {
        return new DefaultDeactivateProductUseCase(gateway);
    }
}
