
CREATE EXTENSION postgis;

CREATE TABLE "user" (
	"id" INTEGER PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	"name" VARCHAR(255),
	"email" VARCHAR(255) UNIQUE,
	"type" VARCHAR(9),
	"cellphone" VARCHAR(50),
	"birthday" DATE
);
CREATE INDEX "user_index_0" ON "user" ("email");

CREATE TABLE "driver" (
	"id" INTEGER PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	"plate" VARCHAR(255),
	"car_model" VARCHAR(255),
	"driver_license" VARCHAR(255),
	"curr_location" GEOGRAPHY(Point, 4326),
	"user_id" INTEGER,

	CONSTRAINT fk_user FOREIGN KEY("user_id") REFERENCES "user"(id)
);

CREATE TABLE "trip" (
	"id" INTEGER PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
	"user_id" INTEGER,
	"driver_id" INTEGER DEFAULT NULL,
    "status" VARCHAR(20),
	"price" MONEY,
	"created_at" DATE,
	"end_time" TIME,
	"origin" GEOGRAPHY(Point, 4326),
	"destiny" GEOGRAPHY(Point, 4326),

	CONSTRAINT fk_user_id FOREIGN KEY("user_id") REFERENCES "user"(id),
	CONSTRAINT fk_driver_id FOREIGN KEY("driver_id") REFERENCES "driver"(id)
);
CREATE INDEX "trip_index_0" ON "trip" ("user_id");

