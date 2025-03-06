-- Insert users if they do not exist
INSERT INTO users (username, email)
SELECT 'john_doe', 'john@example.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'john_doe');

INSERT INTO users (username, email)
SELECT 'jane_smith', 'jane@example.com'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'jane_smith');

-- Insert products if they do not exist
INSERT INTO products (name, description)
SELECT 'Flour', 'Wheat flour for baking'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Flour');

INSERT INTO products (name, description)
SELECT 'Eggs', 'Fresh eggs'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Eggs');

INSERT INTO products (name, description)
SELECT 'Milk', 'Whole milk'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Milk');

INSERT INTO products (name, description)
SELECT 'Sugar', 'White granulated sugar'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Sugar');

INSERT INTO products (name, description)
SELECT 'Butter', 'Unsalted butter'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE name = 'Butter');

-- Insert recipes if they do not exist
INSERT INTO recipes (author_id, title, description, instructions)
SELECT 1, 'Pancakes', 'Delicious homemade pancakes', 'Mix ingredients and cook'
WHERE NOT EXISTS (SELECT 1 FROM recipes WHERE title = 'Pancakes');

-- Insert recipe-product relations if they do not exist
INSERT INTO recipe_products (recipe_id, product_id)
SELECT (SELECT id FROM recipes WHERE title = 'Pancakes'),
       (SELECT id FROM products WHERE name = 'Flour')
WHERE NOT EXISTS (
        SELECT 1 FROM recipe_products
        WHERE recipe_id = (SELECT id FROM recipes WHERE title = 'Pancakes')
          AND product_id = (SELECT id FROM products WHERE name = 'Flour')
    );

INSERT INTO recipe_products (recipe_id, product_id)
SELECT (SELECT id FROM recipes WHERE title = 'Pancakes'),
       (SELECT id FROM products WHERE name = 'Eggs')
WHERE NOT EXISTS (
        SELECT 1 FROM recipe_products
        WHERE recipe_id = (SELECT id FROM recipes WHERE title = 'Pancakes')
          AND product_id = (SELECT id FROM products WHERE name = 'Eggs')
    );

INSERT INTO recipe_products (recipe_id, product_id)
SELECT (SELECT id FROM recipes WHERE title = 'Pancakes'),
       (SELECT id FROM products WHERE name = 'Milk')
WHERE NOT EXISTS (
        SELECT 1 FROM recipe_products
        WHERE recipe_id = (SELECT id FROM recipes WHERE title = 'Pancakes')
          AND product_id = (SELECT id FROM products WHERE name = 'Milk')
    );

INSERT INTO recipe_products (recipe_id, product_id)
SELECT (SELECT id FROM recipes WHERE title = 'Pancakes'),
       (SELECT id FROM products WHERE name = 'Sugar')
WHERE NOT EXISTS (
        SELECT 1 FROM recipe_products
        WHERE recipe_id = (SELECT id FROM recipes WHERE title = 'Pancakes')
          AND product_id = (SELECT id FROM products WHERE name = 'Sugar')
    );

INSERT INTO recipe_products (recipe_id, product_id)
SELECT (SELECT id FROM recipes WHERE title = 'Pancakes'),
       (SELECT id FROM products WHERE name = 'Butter')
WHERE NOT EXISTS (
        SELECT 1 FROM recipe_products
        WHERE recipe_id = (SELECT id FROM recipes WHERE title = 'Pancakes')
          AND product_id = (SELECT id FROM products WHERE name = 'Butter')
    );
