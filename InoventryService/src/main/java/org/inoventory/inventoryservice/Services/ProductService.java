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

    public ProductDto addProduct(ProductRequestDto productRequestDto){
        log.info("Adding the product");
        Product product = modelMapper.map(productRequestDto,Product.class);
        productRepository.save(product); // added the product in db
        return modelMapper.map(product,ProductDto.class);
    }

    public ProductDto updateProduct(Long id , ProductRequestDto productRequestDto){
        log.info("Updating the products ");
        Product olderProduct = productRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Product not found with id: "+id));

        modelMapper.map(productRequestDto,olderProduct); // Map the new values from new request DTO into the existing entity

        // now we can save that updated value in the db
        productRepository.save(olderProduct);

        return modelMapper.map(olderProduct,ProductDto.class);
    }

    public boolean deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);

        if (!productRepository.existsById(id)) {
            log.warn("Product with ID {} not found. Deletion skipped.", id);
            return false;
        }

        productRepository.deleteById(id);
        log.info("Product with ID {} deleted successfully.", id);
        return true;
    }



    public List<ProductDto> getAllProducts() {
        log.info("Fetching all inventory items");
        List<Product> inventories = productRepository.findAll();

        return inventories.stream()
                .map(products -> modelMapper.map(products,ProductDto.class))
                //.collect(Collectors.toList());
                .toList();
    }

    public ProductDto getProductById(Long id) {
        log.info("Fetching Product with Id: {}",id);
        Optional<Product> inventory = productRepository.findById(id);

        return inventory.map(item -> modelMapper.map(item,ProductDto.class))
                .orElseThrow(()->new RuntimeException("Inventory not found"));
    }


    public boolean isStockAvailable(Long productId, int requestedQuantity) {
        Optional<Product> productOpt = productRepository.findById(productId);

        if (productOpt.isEmpty()) {
            throw new RuntimeException("Product not found with ID: " + productId);
        }
        Product product = productOpt.get();
        return product.getStock() >= requestedQuantity;
    }

}
