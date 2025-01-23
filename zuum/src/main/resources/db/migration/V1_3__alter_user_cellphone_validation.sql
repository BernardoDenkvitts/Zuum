ALTER TABLE "user"
ADD CONSTRAINT cellphone_number_check
CHECK (cellphone ~ '^[0-9]+$')