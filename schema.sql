CREATE TABLE coffees (
    coffee_id BIGINT NOT NULL AUTO_INCREMENT, 

    coffee_name VARCHAR(128) NOT NULL, 
    coffee_price DECIMAL(38,2) NOT NULL, 
    coffee_stock BIGINT NOT NULL, 
    coffee_status ENUM ('DISCONTINUED','SELLING') NOT NULL,

    created_at DATETIME(6) NOT NULL, 
    modified_at DATETIME(6) NOT NULL, 

    CONSTRAINT `coffee_price_positive`
        CHECK (coffee_price >= 0),

    CONSTRAINT `coffee_stock_positive`
        CHECK (coffee_stock >= 0),

    PRIMARY KEY (coffee_id)
) ENGINE=InnoDB;

CREATE TABLE points (
    point_id BIGINT NOT NULL AUTO_INCREMENT, 

    point_amount DECIMAL(38,2) NOT NULL, 

    created_at DATETIME(6) NOT NULL, 
    modified_at DATETIME(6) NOT NULL, 

    CONSTRAINT `point_amount_positive`
        CHECK (point_amount >= 0),

    PRIMARY KEY (point_id)
) ENGINE=InnoDB;

CREATE TABLE point_audits (
    point_audit_id BIGINT NOT NULL AUTO_INCREMENT, 

    point_id BIGINT NOT NULL,

    point_audit_type ENUM (
        'POINT_SET', 
        'POINT_ADD',
        'POINT_SUB',
        'COFFEE_ORDER_ADDED',
        'COFFEE_ORDER_SUBTRACTED'
    ) NOT NULL,

    point_audit_amount DECIMAL(38,2) NOT NULL, 

    coffee_order_id BIGINT,

    customer_id VARCHAR(64),

    created_at DATETIME(6)  NOT NULL, 
    modified_at DATETIME(6)  NOT NULL, 

    CONSTRAINT `point_audit_amount_positive`
        CHECK (point_audit_amount >= 0),

    PRIMARY KEY (point_audit_id)
) ENGINE=InnoDB;

CREATE TABLE coffee_orders (
    coffee_order_id BIGINT NOT NULL AUTO_INCREMENT, 

    coffee_id BIGINT,

    coffee_snapshot_name VARCHAR(128)  NOT NULL,
    coffee_snapshot_price DECIMAL(38,2) NOT NULL,
    coffee_order_amount BIGINT NOT NULL,
    customer_id VARCHAR(64) NOT NULL,

    created_at DATETIME(6)  NOT NULL, 
    modified_at DATETIME(6)  NOT NULL, 

    CONSTRAINT `coffee_snapshot_price_positive`
        CHECK (coffee_snapshot_price >= 0),
    CONSTRAINT `coffee_order_amount_positive`
        CHECK (coffee_order_amount >= 0),

    PRIMARY KEY (coffee_order_id)
)  ENGINE=InnoDB;

CREATE TABLE idempotency_caches (
    idempotency_cache_id BIGINT NOT NULL AUTO_INCREMENT, 

    idempotency_cache_key UUID NOT NULL,

    idempotency_cache_value TEXT NOT NULL,

    created_at DATETIME(6)  NOT NULL, 
    modified_at DATETIME(6)  NOT NULL, 

    UNIQUE INDEX `uk_idempotency_cache_key` (idempotency_cache_key),

    PRIMARY KEY (idempotency_cache_id)
) ENGINE=InnoDB;

ALTER TABLE point_audits ADD CONSTRAINT `fk_point_id`
    FOREIGN KEY (point_id) REFERENCES points(point_id);
ALTER TABLE point_audits ADD  CONSTRAINT `fk_coffee_order_id`
    FOREIGN KEY (coffee_order_id) REFERENCES coffee_orders(coffee_order_id);

ALTER TABLE coffee_orders ADD CONSTRAINT `fk_coffee_id`
    FOREIGN KEY (coffee_id) REFERENCES coffees(coffee_id);

