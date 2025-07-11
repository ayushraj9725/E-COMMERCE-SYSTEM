package org.inoventory.inventoryservice.Services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.inoventory.inventoryservice.DTOs.OrderRequestDto;
import org.inoventory.inventoryservice.DTOs.OrderRequestItemDto;
import org.inoventory.inventoryservice.DTOs.ProductDto;
import org.inoventory.inventoryservice.DTOs.ProductRequestDto;
import org.inoventory.inventoryservice.Models.Product;
import org.inoventory.inventoryservice.Repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Double reduceStocks(OrderRequestDto orderRequestDto) {
        log.info("Reducing the stocks");
        Double totalPrice = 0.0;

        for(OrderRequestItemDto orderRequestItemDto: orderRequestDto.getItems()){
            Long productId = orderRequestItemDto.getProductId();
            Integer quantity = orderRequestItemDto.getQuantity();

            if(isStockAvailable(productId,quantity)){
                Product product = productRepository.findById(productId)
                        .orElseThrow(()-> new RuntimeException("Product not found with id: "+productId));

                if(product.getStock() < quantity){
                    throw new RuntimeException("Product cannot be fulfilled for given quantity");
                }

                product.setStock(product.getStock() - quantity);
                productRepository.save(product);

                totalPrice += quantity*product.getPrice();

            }
        }

        return totalPrice;
    }

    public ProductDto addProduct(ProductRequestDto productRequestDto, String userEmail) {
        log.info("Adding the product for seller: {}", userEmail);
        // Map request to entity
        Product product = modelMapper.map(productRequestDto, Product.class);
        // Set the seller's email (from the Gateway-provided header)
        product.setUserEmail(userEmail);  // Make sure the Product has this field
        // Save product in DB
        product = productRepository.save(product);
        // Convert entity back to DTO
        return modelMapper.map(product, ProductDto.class);
    }


    public ProductDto updateProduct(Long id , ProductRequestDto productRequestDto,String userEmail){
        log.info("Updating the products ");
        Product olderProduct = productRepository.findByIdAndUserEmail(id,userEmail)
                .orElseThrow(()-> new RuntimeException("Product not found with id and email : "+id +" "+ userEmail));

        modelMapper.map(productRequestDto,olderProduct); // Map the new values from new request DTO into the existing entity

        // now we can save that updated value in the db
        productRepository.save(olderProduct);

        return modelMapper.map(olderProduct,ProductDto.class);
    }

    public boolean deleteProduct(Long id,String userEmail) {
        log.info("Deleting product with ID: {}", id);

        if (!productRepository.existsByIdAndUserEmail(id,userEmail)) {
            log.warn("Product with ID {} not found. Deletion skipped.", id);
            return false;
        }

        productRepository.deleteById(id);
        log.info("Product with ID {} deleted successfully.", id);
        return true;
    }


    public List<ProductDto> getAllProducts(String userEmail) {
        log.info("Fetching all products for seller: {}", userEmail);

        List<Product> inventories = productRepository.findAllByUserEmail(userEmail);

        return inventories.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
    }


    public ProductDto getProductById(Long id,String userEmail) {
        log.info("Fetching Product with Id: {}",id);
        Optional<Product> inventory = productRepository.findByIdAndUserEmail(id,userEmail);
        return inventory.map(item -> modelMapper.map(item,ProductDto.class))
                .orElseThrow(()->new RuntimeException("Inventory not found"));
    }


    public Page<ProductDto> searchProducts(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> result = productRepository.searchProducts(query,pageable);
        return result.map(product -> modelMapper.map(product, ProductDto.class));
    }


    public boolean isStockAvailable(Long productId, int requestedQuantity) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
        Product product = productOpt.get();
        return product.getStock() >= requestedQuantity;
    }

    public String ProductTitle(Long productId){
        System.out.println(11);
        String title = productRepository.findProductTitleById(productId);
        System.out.println(title);
        return title;
    }

}
