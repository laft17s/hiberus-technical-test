CREATE TABLE IF NOT EXISTS payment_orders (
    payment_order_id VARCHAR(50) PRIMARY KEY,
    external_reference VARCHAR(100) UNIQUE NOT NULL,
    debtor_iban VARCHAR(50) NOT NULL,
    creditor_iban VARCHAR(50) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    remittance_information VARCHAR(255),
    requested_execution_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    last_update TIMESTAMP NOT NULL
);
