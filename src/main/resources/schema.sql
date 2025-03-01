
CREATE TABLE IF NOT EXISTS users (id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(255) UNIQUE NOT NULL,
                    email VARCHAR(255) UNIQUE NOT NULL
);



CREATE TABLE IF NOT EXISTS recipes (id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      description TEXT,
                      instructions TEXT,
                      author_id BIGINT,
                      FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS products (id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) UNIQUE NOT NULL,
                       description TEXT
);

CREATE TABLE IF NOT EXISTS recipe_products (recipe_id BIGINT,
                              product_id BIGINT,
                              PRIMARY KEY (recipe_id, product_id),
                              FOREIGN KEY (recipe_id) REFERENCES recipes(id),
                              FOREIGN KEY (product_id) REFERENCES products(id)
);
