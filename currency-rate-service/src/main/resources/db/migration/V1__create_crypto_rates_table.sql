CREATE TABLE crypto_rates (
                              currency VARCHAR(255) NOT NULL UNIQUE ,
                              rate NUMERIC(19, 4) NOT NULL
);