package com.glowtique.glowtique.product.service;

import com.glowtique.glowtique.brand.model.Brand;
import com.glowtique.glowtique.brand.repository.BrandRepository;
import com.glowtique.glowtique.brand.service.BrandService;
import com.glowtique.glowtique.category.model.Category;
import com.glowtique.glowtique.category.model.CategoryType;
import com.glowtique.glowtique.category.service.CategoryService;
import com.glowtique.glowtique.exception.ProductNotfoundException;
import com.glowtique.glowtique.product.model.Fragrance;
import com.glowtique.glowtique.product.model.FragranceType;
import com.glowtique.glowtique.product.model.Product;
import com.glowtique.glowtique.product.model.ProductGender;
import com.glowtique.glowtique.product.repository.ProductRepository;
import com.glowtique.glowtique.web.dto.FragranceRequest;
import com.glowtique.glowtique.web.dto.ProductEditRequest;
import com.glowtique.glowtique.web.dto.ProductImportRow;
import com.glowtique.glowtique.web.dto.ProductInsertionRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.unbescape.csv.CsvEscape.escapeCsv;

@Service
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final FragranceService fragranceService;


    @Autowired
    public ProductService(ProductRepository productRepository, BrandService brandService, CategoryService categoryService, FragranceService fragranceService) {
        this.productRepository = productRepository;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.fragranceService = fragranceService;
    }


    public Product getProductById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotfoundException("Product not found"));
    }

    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }


    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    public Page<Product> filterProducts(List<UUID> categories, List<String> genders,List<Integer> volumes, Double minPrice, Double maxPrice, Pageable pageable) {
        List<ProductGender> genderEnums = null;
        if (genders != null && !genders.isEmpty()) {
            genderEnums = genders.stream()
                    .map(String::toUpperCase)
                    .map(ProductGender::valueOf)
                    .toList();
        }
        return productRepository.filterProducts(categories, genderEnums, volumes, minPrice, maxPrice, pageable);
    }

    public Page<Product> filterBrandProducts(List<UUID> categories, List<String> genders, List<Integer> volume, Double minPrice, Double maxPrice, Brand brand , Pageable pageable) {
        List<ProductGender> genderEnums = null;
        if (genders != null && !genders.isEmpty()) {
            genderEnums = genders.stream()
                    .map(String::toUpperCase)
                    .map(ProductGender::valueOf)
                    .toList();
        }
        return productRepository.filterBrandProducts(categories, genderEnums, volume, minPrice, maxPrice, brand, pageable);
    }



    public List<Product> findTop10ByNameContainingIgnoreCase(String name) {
        return productRepository.findTop10ByNameContainingIgnoreCase(name);
    }
    public List<Integer> findAllVolumes() {
        return productRepository.findAllDistinctProductVolume();
    }



    @Transactional
    public Product createProduct(ProductInsertionRequest request) {
        Product product = new Product();
        product.setDescription(request.getDescription());
        product.setName(request.getProductName());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setImage(request.getImage());
        product.setProductGender(request.getProductGender());
        product.setQuantity(request.getQuantity());
        product.setSmallDescription(request.getSmallDescription());
        product.setIngredients(request.getIngredients());
        product.setVolume(request.getVolume());
        product.setBrand(brandService.getBrandByBrandName(request.getBrandName()));
        product.setCategory(categoryService.getCategoryByCategoryType(request.getType()));
        product.setFragrance(fragranceService.getFragranceById(request.getFragranceId()));
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);
        log.info("Created product: {}", product);
        log.info("Created at: {}", product.getCreatedAt());
        return product;
    }

    public BigDecimal maxPrice(Page<Product> products) {
        return productRepository.findTheExpensiveProductPrice(products);
    }

    @Transactional
    public Product updateProduct(ProductEditRequest request, UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ProductNotfoundException("Product wasn't found"));
        product.setName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setImage(request.getImage());
        product.setProductGender(request.getProductGender());
        product.setQuantity(request.getQuantity());
        product.setUpdatedAt(LocalDateTime.now());
        product.setBrand(brandService.getBrandByBrandName(request.getBrandName()));
        System.out.println("brand is correct");
        product.setCategory(categoryService.getCategoryByCategoryType(request.getType()));
        System.out.println("category is correct");
        product.setFragrance(fragranceService.getFragranceById(request.getFragranceId()));
        System.out.println("fragrance is correct");
        product.setSmallDescription(request.getSmallDescription());
        product.setIngredients(request.getIngredients());
        product.setVolume(request.getVolume());

        log.info("Updating product with ID: {}", id);
        log.info("Updating product with Name: {}", request.getProductName());

        return productRepository.save(product);
    }

    @Transactional
    public void importProducts(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            reader.readLine();

            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                ProductImportRow row = mapCsvLineToDto(line);

                Brand brand = brandService.getBrandByBrandName(row.getBrandName());
                Category category = categoryService.getCategoryByCategoryType(row.getCategoryType());

                Product product = productRepository
                        .findByNameIgnoreCaseAndBrand_NameIgnoreCaseAndVolume(
                                row.getName(),
                                row.getBrandName(),
                                row.getVolume()
                        )
                        .orElseGet(Product::new);

                boolean isNewProduct = product.getId() == null;

                Fragrance fragrance;

                if (isNewProduct || product.getFragrance() == null) {
                    FragranceRequest fragranceRequest = new FragranceRequest();
                    fragranceRequest.setTopNotes(row.getTopNotes());
                    fragranceRequest.setHeartNotes(row.getHeartNotes());
                    fragranceRequest.setBaseNotes(row.getBaseNotes());
                    fragranceRequest.setTypes(row.getFragranceTypes());

                    fragrance = fragranceService.createFragrance(fragranceRequest);
                } else {
                    fragrance = product.getFragrance();
                    fragrance.setTopNotes(row.getTopNotes());
                    fragrance.setHeartNotes(row.getHeartNotes());
                    fragrance.setBaseNotes(row.getBaseNotes());
                    fragrance.setType(row.getFragranceTypes());
                }

                product.setName(row.getName());
                product.setBrand(brand);
                product.setCategory(category);
                product.setProductGender(row.getProductGender());
                product.setPrice(row.getPrice());
                product.setDiscountPrice(row.getDiscountPrice());
                product.setQuantity(row.getQuantity());
                product.setVolume(row.getVolume());
                product.setImage(row.getImage());
                product.setDescription(row.getDescription());
                product.setSmallDescription(row.getSmallDescription());
                product.setIngredients(row.getIngredients());
                product.setFragrance(fragrance);

                if (isNewProduct) {
                    product.setCreatedAt(LocalDateTime.now());
                }

                product.setUpdatedAt(LocalDateTime.now());

                productRepository.save(product);
            }

        } catch (Exception e) {
            throw new RuntimeException("Product import failed: " + e.getMessage(), e);
        }
    }

    private ProductImportRow mapCsvLineToDto(String line) {
        String[] row = line.split(",", -1);

        ProductImportRow dto = new ProductImportRow();

        dto.setName(row[0].trim());
        dto.setBrandName(row[1].trim());
        dto.setCategoryType(CategoryType.valueOf(row[2].trim()));
        dto.setProductGender(ProductGender.valueOf(row[3].trim()));

        dto.setPrice(new BigDecimal(row[4].trim()));
        dto.setDiscountPrice(row[5].isBlank() ? null : new BigDecimal(row[5].trim()));

        dto.setQuantity(Integer.parseInt(row[6].trim()));
        dto.setVolume(Integer.parseInt(row[7].trim()));

        dto.setImage(row[8].trim());
        dto.setDescription(row[9].trim());
        dto.setSmallDescription(row[10].trim());
        dto.setIngredients(row[11].trim());

        dto.setTopNotes(row[12].trim());
        dto.setHeartNotes(row[13].trim());
        dto.setBaseNotes(row[14].trim());

        Set<FragranceType> types = Arrays.stream(row[15].split(";"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(FragranceType::valueOf).collect(Collectors.toSet());

        dto.setFragranceTypes(types);

        return dto;
    }

    public void exportProductsToCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=products-export.csv");

        List<Product> products = productRepository.findAll();

        try (PrintWriter writer = response.getWriter()) {
            writer.write('\uFEFF');
            writer.println("name,brandName,categoryType,productGender,price,discountPrice,quantity,volume,image,description,smallDescription,ingredients,topNotes,heartNotes,baseNotes,fragranceTypes");

            for (Product product : products) {
                Fragrance fragrance = product.getFragrance();

                String topNotes = fragrance == null ? "" : fragrance.getTopNotes();
                String heartNotes = fragrance == null ? "" : fragrance.getHeartNotes();
                String baseNotes = fragrance == null ? "" : fragrance.getBaseNotes();

                String fragranceTypes = fragrance == null
                        ? ""
                        : fragrance.getType()
                        .stream()
                        .map(Enum::name)
                        .sorted()
                        .collect(Collectors.joining(";"));

                writer.println(String.join(",",
                        escapeCsv(product.getName()),
                        escapeCsv(product.getBrand().getName()),
                        escapeCsv(product.getCategory().getCategoryType().name()),
                        escapeCsv(product.getProductGender().name()),
                        escapeCsv(product.getPrice().toString()),
                        product.getDiscountPrice() == null ? "" : escapeCsv(product.getDiscountPrice().toString()),
                        escapeCsv(String.valueOf(product.getQuantity())),
                        escapeCsv(String.valueOf(product.getVolume())),
                        escapeCsv(product.getImage()),
                        escapeCsv(product.getDescription()),
                        escapeCsv(product.getSmallDescription()),
                        escapeCsv(product.getIngredients()),
                        escapeCsv(topNotes),
                        escapeCsv(heartNotes),
                        escapeCsv(baseNotes),
                        escapeCsv(fragranceTypes)
                ));
            }
        }
    }
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        String escaped = value.replace("\"", "\"\"");

        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }

        return escaped;
    }
}
