ALTER TABLE IF EXISTS HANDCRAFTED_SCHEMA.GIFT_SET
ALTER COLUMN PRICE DROP NOT NULL;

ALTER TABLE IF EXISTS HANDCRAFTED_SCHEMA.GIFT_SET_ITEM
ALTER COLUMN PRODUCT_COST DROP NOT NULL;
