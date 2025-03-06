CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(255) UNIQUE NOT NULL,
                                     email VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(255) UNIQUE NOT NULL,
                                        description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS recipes (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       author_id BIGINT NOT NULL,
                                       title VARCHAR(255) NOT NULL,
                                       description VARCHAR(255),
                                       instructions TEXT,
                                       FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS recipe_products (
                                               recipe_id BIGINT NOT NULL,
                                               product_id BIGINT NOT NULL,
                                               PRIMARY KEY (recipe_id, product_id),
                                               FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE,
                                               FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS user_recipes (
                                            user_id BIGINT NOT NULL,
                                            recipe_id BIGINT NOT NULL,
                                            PRIMARY KEY (user_id, recipe_id),
                                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                            FOREIGN KEY (recipe_id) REFERENCES recipes(id) ON DELETE CASCADE
);