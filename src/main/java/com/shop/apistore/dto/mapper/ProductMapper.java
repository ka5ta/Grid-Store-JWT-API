package com.shop.apistore.dto.mapper;

import com.shop.apistore.dto.BasketProductDTO;
import com.shop.apistore.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProductMapper {

    ProductMapper MAPPER = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "productId", source = "id")
    @Mapping(target = "brand")
    @Mapping(target = "price")
    BasketProductDTO toBasketProductDTO(Product product);
}
