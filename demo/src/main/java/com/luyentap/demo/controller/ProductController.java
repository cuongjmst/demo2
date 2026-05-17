package com.luyentap.demo.controller;

import com.luyentap.demo.dto.ProductDTO;
import com.luyentap.demo.entity.Product;
import com.luyentap.demo.repository.ProductRepository;
import com.luyentap.demo.service.FileStorageService;
import com.luyentap.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final FileStorageService fileStorageService;
    private final ProductRepository productRepository;

    // Ai cũng xem được
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(productService.getAll(name, page, size, sortBy));
    }

    // Ai cũng xem được
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    // Chỉ ADMIN mới tạo được
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> create(@RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.create(dto));
    }

    // Chỉ ADMIN mới sửa được
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProductDTO dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    // Chỉ ADMIN mới xóa được
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok("Xóa thành công!");
    }
    @PostMapping("/{id}/upload")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> uploadImage(@PathVariable Long id,
                                         @RequestParam("file") MultipartFile file) throws IOException {
        Product product = productService.getById(id);
        String imageUrl = fileStorageService.saveFile(file);
        product.setImageUrl(imageUrl);
        productRepository.save(product);
        return ResponseEntity.ok(product);
    }
}