drop database if exists datademo;create database datademo;
CREATE TABLE datademo.Person (
       id BIGINT NOT NULL
     , name VARCHAR(50) NOT NULL
     , age INTEGER NOT NULL
     , PRIMARY KEY (id)
);

CREATE TABLE datademo.Catalog (
       id BIGINT NOT NULL
     , item_number INTEGER NOT NULL
     , color CHAR(10) NOT NULL
     , style CHAR(10) NOT NULL
     , PRIMARY KEY (id)
);

