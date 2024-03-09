package com.priyajit.ecommerce.product.catalog.service.client;

import org.springframework.web.multipart.MultipartFile;

public interface ProductImageStore {

    /**
     *
     * @param key
     * @param file
     * @return url of the stored file
     */
    String store(String key, MultipartFile file);
}
