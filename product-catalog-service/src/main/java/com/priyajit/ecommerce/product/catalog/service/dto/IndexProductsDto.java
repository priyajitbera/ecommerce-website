package com.priyajit.ecommerce.product.catalog.service.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndexProductsDto {

    @NotEmpty
    private List<String> productIds;
}
