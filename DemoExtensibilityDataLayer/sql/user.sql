drop database if exists datademo;create database datademo;
CREATE TABLE datademo.SpecializedPerson (
       id BIGINT NOT NULL
     , name VARCHAR(50) NOT NULL
     , age INTEGER NOT NULL
     , age_factor INTEGER NOT NULL
     , PRIMARY KEY (id)
);

CREATE TABLE datademo.ProprietaryCatalog (
       id BIGINT NOT NULL
     , sku INTEGER NOT NULL
     , item_hue CHAR(10) NOT NULL
     , item_style CHAR(10) NOT NULL
     , item_popularity CHAR(10) NOT NULL
     , PRIMARY KEY (id)
);

