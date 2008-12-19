
DROP TABLE IF EXISTS springcommerce.BasketProduct;
DROP TABLE IF EXISTS springcommerce.TaxZoneGeographicalZone;
DROP TABLE IF EXISTS springcommerce.TaxRate;
DROP TABLE IF EXISTS springcommerce.ReviewDescription;
DROP TABLE IF EXISTS springcommerce.ProductNotification;
DROP TABLE IF EXISTS springcommerce.ProductDescription;
DROP TABLE IF EXISTS springcommerce.OrderProductDownload;
DROP TABLE IF EXISTS springcommerce.ProductDownload;
DROP TABLE IF EXISTS springcommerce.ProductProductOption;
DROP TABLE IF EXISTS springcommerce.OrderProductOption;
DROP TABLE IF EXISTS springcommerce.OrderStatusHistory;
DROP TABLE IF EXISTS springcommerce.ManufacturerInfo;
DROP TABLE IF EXISTS springcommerce.CustomerMetrics;
DROP TABLE IF EXISTS springcommerce.Review;
DROP TABLE IF EXISTS springcommerce.OrderProduct;
DROP TABLE IF EXISTS springcommerce.Product;
DROP TABLE IF EXISTS springcommerce.Order;
DROP TABLE IF EXISTS springcommerce.OrderStatus;
DROP TABLE IF EXISTS springcommerce.Basket;
DROP TABLE IF EXISTS springcommerce.Customer;
DROP TABLE IF EXISTS springcommerce.Address;
DROP TABLE IF EXISTS springcommerce.Category;
DROP TABLE IF EXISTS springcommerce.Country;
DROP TABLE IF EXISTS springcommerce.AddressFormat;
DROP TABLE IF EXISTS springcommerce.TaxZone;
DROP TABLE IF EXISTS springcommerce.GeographicalZone;
DROP TABLE IF EXISTS springcommerce.TaxClass;
DROP TABLE IF EXISTS springcommerce.ProductOption;
DROP TABLE IF EXISTS springcommerce.Manufacturer;
DROP TABLE IF EXISTS springcommerce.Language;
DROP TABLE IF EXISTS springcommerce.Currency;

CREATE TABLE springcommerce.Currency (
       CurrencyID BIGINT NOT NULL AUTO_INCREMENT
     , CurrencyName VARCHAR(32) NOT NULL
     , CurrencyCode CHAR(3) NOT NULL
     , LeftSymbol VARCHAR(12)
     , RightSymbol VARCHAR(12)
     , DecimalIndicator CHAR(1) NOT NULL
     , ThousandIndicator CHAR(1) NOT NULL
     , SignificantDigits CHAR(1) NOT NULL
     , ConversionFactor FLOAT(13, 8) NOT NULL
     , DateAdded DATETIME NOT NULL
     , DateUpdated DATETIME NOT NULL
     , PRIMARY KEY (CurrencyID)
);

CREATE TABLE springcommerce.Language (
       LanguageID BIGINT NOT NULL AUTO_INCREMENT
     , Name VARCHAR(32) NOT NULL
     , Code CHAR(2) NOT NULL
     , PRIMARY KEY (LanguageID)
);

CREATE TABLE springcommerce.Manufacturer (
       ManufacturerID BIGINT NOT NULL AUTO_INCREMENT
     , ManufacturerName VARCHAR(32) NOT NULL
     , DateAdded DATETIME NOT NULL
     , DateModified DATETIME NOT NULL
     , PRIMARY KEY (ManufacturerID)
);

CREATE TABLE springcommerce.ProductOption (
       ProductOptionID BIGINT NOT NULL AUTO_INCREMENT
     , Price DEC(15, 4) NOT NULL
     , ProductOptionName VARCHAR(32) NOT NULL
     , PRIMARY KEY (ProductOptionID)
);

CREATE TABLE springcommerce.TaxClass (
       TaxClassID BIGINT NOT NULL AUTO_INCREMENT
     , TaxClassTitle VARCHAR(32) NOT NULL
     , TaxClassDescription VARCHAR(255)
     , DateAdded DATETIME NOT NULL
     , DateModified DATETIME NOT NULL
     , PRIMARY KEY (TaxClassID)
);

CREATE TABLE springcommerce.GeographicalZone (
       GeographicalZoneID BIGINT NOT NULL AUTO_INCREMENT
     , GeographicalZoneName VARCHAR(32) NOT NULL
     , Description VARCHAR(255)
     , DateAdded DATETIME NOT NULL
     , DateModified DATETIME NOT NULL
     , PRIMARY KEY (GeographicalZoneID)
);

CREATE TABLE springcommerce.TaxZone (
       TaxZoneID BIGINT NOT NULL AUTO_INCREMENT
     , CountryID BIGINT NOT NULL
     , ZoneCode VARCHAR(32) NOT NULL
     , ZoneName VARCHAR(32) NOT NULL
     , PRIMARY KEY (TaxZoneID)
);

CREATE TABLE springcommerce.AddressFormat (
       AddressFormatID BIGINT NOT NULL AUTO_INCREMENT
     , AddressFormat VARCHAR(128) NOT NULL
     , FormatNotes VARCHAR(48)
     , PRIMARY KEY (AddressFormatID)
);

CREATE TABLE springcommerce.Country (
       CountryID BIGINT NOT NULL AUTO_INCREMENT
     , CountryName VARCHAR(64) NOT NULL
     , ISOCode2 CHAR(2) NOT NULL
     , ISOCode3 CHAR(3) NOT NULL
     , AddressFormatID BIGINT NOT NULL
     , PRIMARY KEY (CountryID)
     , INDEX (AddressFormatID)
     , CONSTRAINT FK_Country_1 FOREIGN KEY (AddressFormatID)
                  REFERENCES springcommerce.AddressFormat (AddressFormatID)
);

CREATE TABLE springcommerce.Category (
       CategoryID BIGINT NOT NULL AUTO_INCREMENT
     , Leftt BIGINT NOT NULL
     , Rightt BIGINT NOT NULL
     , DateAdded DATETIME NOT NULL
     , DateModified DATETIME NOT NULL
     , CategoryName VARCHAR(32) NOT NULL
     , CategoryDescription VARCHAR(128)
     , LanguageID BIGINT NOT NULL
     , PRIMARY KEY (CategoryID)
     , INDEX (LanguageID)
     , CONSTRAINT FK_Category_1 FOREIGN KEY (LanguageID)
                  REFERENCES springcommerce.Language (LanguageID)
);

CREATE TABLE springcommerce.Address (
       AddressID BIGINT NOT NULL AUTO_INCREMENT
     , CustomerID BIGINT NOT NULL
     , Gender CHAR(1)
     , Company VARCHAR(32)
     , FirstName VARCHAR(32) NOT NULL
     , LastName VARCHAR(32) NOT NULL
     , MiddleName VARCHAR(32)
     , StreetAddress1 VARCHAR(64) NOT NULL
     , StreeAddress2 VARCHAR(64)
     , PostalCode VARCHAR(10) NOT NULL
     , City VARCHAR(32) NOT NULL
     , StateProvince VARCHAR(32) NOT NULL
     , CountryID BIGINT NOT NULL
     , PRIMARY KEY (AddressID)
     , INDEX (CountryID)
     , CONSTRAINT FK_Address_1 FOREIGN KEY (CountryID)
                  REFERENCES springcommerce.Country (CountryID)
);

CREATE TABLE springcommerce.Customer (
       CustomerID BIGINT NOT NULL AUTO_INCREMENT
     , Gender CHAR(1)
     , FirstName VARCHAR(32) NOT NULL
     , LastName VARCHAR(32) NOT NULL
     , MiddleName VARCHAR(32) NOT NULL
     , DateOfBirth DATETIME
     , EmailAddress VARCHAR(96)
     , DefaultAddressID BIGINT NOT NULL
     , DefaultPhone VARCHAR(32)
     , Fax VARCHAR(32)
     , PRIMARY KEY (CustomerID)
     , INDEX (DefaultAddressID)
     , CONSTRAINT FK_Customer_1 FOREIGN KEY (DefaultAddressID)
                  REFERENCES springcommerce.Address (AddressID)
);

CREATE TABLE springcommerce.Basket (
       BasketID BIGINT NOT NULL AUTO_INCREMENT
     , CustomerID BIGINT NOT NULL
     , FinalPrice DECIMAL(15, 4)
     , DateUpdated DATETIME NOT NULL
     , DateAdded DATETIME NOT NULL
     , LanguageID BIGINT NOT NULL
     , PRIMARY KEY (BasketID)
     , INDEX (CustomerID)
     , CONSTRAINT FK_Basket_1 FOREIGN KEY (CustomerID)
                  REFERENCES springcommerce.Customer (CustomerID)
     , INDEX (LanguageID)
     , CONSTRAINT FK_Basket_2 FOREIGN KEY (LanguageID)
                  REFERENCES springcommerce.Language (LanguageID)
);

CREATE TABLE springcommerce.OrderStatus (
       OrderStatusID BIGINT NOT NULL AUTO_INCREMENT
     , LanguageID BIGINT NOT NULL
     , StatusName VARCHAR(32) NOT NULL
     , IsPublic BIT NOT NULL
     , IsDownload BIT NOT NULL
     , PRIMARY KEY (OrderStatusID)
     , INDEX (LanguageID)
     , CONSTRAINT FK_OrderStatus_1 FOREIGN KEY (LanguageID)
                  REFERENCES springcommerce.Language (LanguageID)
);

CREATE TABLE springcommerce.Order (
       OrderID BIGINT NOT NULL AUTO_INCREMENT
     , CustomerID BIGINT NOT NULL
     , AddressID BIGINT NOT NULL
     , PrimaryTelephone VARCHAR(32)
     , SecondaryTelephone VARCHAR(32)
     , Fax VARCHAR(32)
     , EmailAddress VARCHAR(96)
     , DeliveryAddressID BIGINT NOT NULL
     , BillingAddressID BIGINT NOT NULL
     , DateUpdated DATETIME NOT NULL
     , DateModified DATETIME NOT NULL
     , DateFinished DATETIME
     , OrderStatusID BIGINT NOT NULL
     , CurrencyID BIGINT NOT NULL
     , OrderTotal DEC(14, 6) NOT NULL
     , PRIMARY KEY (OrderID)
     , INDEX (AddressID)
     , CONSTRAINT FK_Order_1 FOREIGN KEY (AddressID)
                  REFERENCES springcommerce.Address (AddressID)
     , INDEX (DeliveryAddressID)
     , CONSTRAINT FK_Order_2 FOREIGN KEY (DeliveryAddressID)
                  REFERENCES springcommerce.Address (AddressID)
     , INDEX (BillingAddressID)
     , CONSTRAINT FK_Order_3 FOREIGN KEY (BillingAddressID)
                  REFERENCES springcommerce.Address (AddressID)
     , INDEX (AddressID)
     , CONSTRAINT FK_Order_4 FOREIGN KEY (AddressID)
                  REFERENCES springcommerce.Address (AddressID)
     , INDEX (CustomerID)
     , CONSTRAINT FK_Order_5 FOREIGN KEY (CustomerID)
                  REFERENCES springcommerce.Customer (CustomerID)
     , INDEX (CurrencyID)
     , CONSTRAINT FK_Order_6 FOREIGN KEY (CurrencyID)
                  REFERENCES springcommerce.Currency (CurrencyID)
     , INDEX (OrderStatusID)
     , CONSTRAINT FK_Order_7 FOREIGN KEY (OrderStatusID)
                  REFERENCES springcommerce.OrderStatus (OrderStatusID)
);

CREATE TABLE springcommerce.Product (
       ProductID BIGINT NOT NULL AUTO_INCREMENT
     , CategoryID BIGINT NOT NULL
     , InventoryQuantity INT(4) NOT NULL
     , Model VARCHAR(12) NOT NULL
     , Price DEC(15, 4) NOT NULL
     , DateAdded DATETIME NOT NULL
     , DateModified DATETIME NOT NULL
     , DateAvailable DATETIME NOT NULL
     , Weight DEC(5, 2) NOT NULL
     , Active BIT NOT NULL
     , ManufacturerID BIGINT NOT NULL
     , QuantityOrdered INT NOT NULL DEFAULT 0
     , IsDownload BIT NOT NULL DEFAULT 0
     , PRIMARY KEY (ProductID)
     , INDEX (ManufacturerID)
     , CONSTRAINT FK_Product_1 FOREIGN KEY (ManufacturerID)
                  REFERENCES springcommerce.Manufacturer (ManufacturerID)
     , INDEX (CategoryID)
     , CONSTRAINT FK_Product_2 FOREIGN KEY (CategoryID)
                  REFERENCES springcommerce.Category (CategoryID)
);

CREATE TABLE springcommerce.OrderProduct (
       OrderProductID BIGINT NOT NULL AUTO_INCREMENT
     , OrderID BIGINT NOT NULL
     , ProductID BIGINT NOT NULL
     , FinalPrice DEC(15, 4) NOT NULL
     , Tax DEC(7, 4) NOT NULL
     , TaxClassID BIGINT NOT NULL
     , Quantity INT(2) NOT NULL
     , PRIMARY KEY (OrderProductID)
     , INDEX (OrderID)
     , CONSTRAINT FK_OrderProduct_1 FOREIGN KEY (OrderID)
                  REFERENCES springcommerce.Order (OrderID)
     , INDEX (ProductID)
     , CONSTRAINT FK_OrderProduct_2 FOREIGN KEY (ProductID)
                  REFERENCES springcommerce.Product (ProductID)
     , INDEX (TaxClassID)
     , CONSTRAINT FK_OrderProduct_3 FOREIGN KEY (TaxClassID)
                  REFERENCES springcommerce.TaxClass (TaxClassID)
);

CREATE TABLE springcommerce.Review (
       ReviewID BIGINT NOT NULL AUTO_INCREMENT
     , ProductID BIGINT NOT NULL
     , CustomerID BIGINT NOT NULL
     , Rating INT(1) NOT NULL
     , DateAdded DATETIME NOT NULL
     , DateModified DATETIME NOT NULL
     , TimesRead INT(5) NOT NULL DEFAULT 0
     , PRIMARY KEY (ReviewID)
     , INDEX (ProductID)
     , CONSTRAINT FK_Review_1 FOREIGN KEY (ProductID)
                  REFERENCES springcommerce.Product (ProductID)
     , INDEX (CustomerID)
     , CONSTRAINT FK_Review_2 FOREIGN KEY (CustomerID)
                  REFERENCES springcommerce.Customer (CustomerID)
);

CREATE TABLE springcommerce.CustomerMetrics (
       CustomerInfoID BIGINT NOT NULL AUTO_INCREMENT
     , LastLogin DATETIME
     , LoginQuantity INT(5)
     , DateAdded DATETIME NOT NULL
     , DateUpdated DATETIME NOT NULL
     , CustomerID BIGINT NOT NULL
     , PRIMARY KEY (CustomerInfoID)
     , INDEX (CustomerID)
     , CONSTRAINT FK_CustomerInfo_1 FOREIGN KEY (CustomerID)
                  REFERENCES springcommerce.Customer (CustomerID)
);

CREATE TABLE springcommerce.ManufacturerInfo (
       ManufacturerInfoID BIGINT NOT NULL AUTO_INCREMENT
     , ManufacturerID BIGINT NOT NULL
     , LanguageID BIGINT NOT NULL
     , ManufacturerURL VARCHAR(255)
     , ManufacturerURLClickQuantity INT(5)
     , DateURLLastClicked DATETIME
     , PRIMARY KEY (ManufacturerInfoID)
     , INDEX (ManufacturerID)
     , CONSTRAINT FK_ManufacturerInfo_1 FOREIGN KEY (ManufacturerID)
                  REFERENCES springcommerce.Manufacturer (ManufacturerID)
     , INDEX (LanguageID)
     , CONSTRAINT FK_ManufacturerInfo_2 FOREIGN KEY (LanguageID)
                  REFERENCES springcommerce.Language (LanguageID)
);

CREATE TABLE springcommerce.OrderStatusHistory (
       OrderStatusHistoryID BIGINT NOT NULL AUTO_INCREMENT
     , OrderID BIGINT NOT NULL
     , OrderStatusID BIGINT NOT NULL
     , DateAdded DATETIME NOT NULL
     , CustomerNotified BIT
     , Comments TEXT
     , PRIMARY KEY (OrderStatusHistoryID)
     , INDEX (OrderID)
     , CONSTRAINT FK_OrderStatusHistory_1 FOREIGN KEY (OrderID)
                  REFERENCES springcommerce.Order (OrderID)
     , INDEX (OrderStatusID)
     , CONSTRAINT FK_OrderStatusHistory_2 FOREIGN KEY (OrderStatusID)
                  REFERENCES springcommerce.OrderStatus (OrderStatusID)
);

CREATE TABLE springcommerce.OrderProductOption (
       OrderProductOption BIGINT NOT NULL AUTO_INCREMENT
     , OrderProductID BIGINT NOT NULL
     , FinalPrice DEC(15, 4) NOT NULL
     , Tax DEC(7, 4) NOT NULL
     , TaxClassID BIGINT NOT NULL
     , Quantity INT(2) NOT NULL
     , ProductOptionID BIGINT NOT NULL
     , PRIMARY KEY (OrderProductOption)
     , INDEX (OrderProductID)
     , CONSTRAINT FK_OrderProductOption_1 FOREIGN KEY (OrderProductID)
                  REFERENCES springcommerce.OrderProduct (OrderProductID)
     , INDEX (ProductOptionID)
     , CONSTRAINT FK_OrderProductOption_2 FOREIGN KEY (ProductOptionID)
                  REFERENCES springcommerce.ProductOption (ProductOptionID)
     , INDEX (TaxClassID)
     , CONSTRAINT FK_OrderProductOption_3 FOREIGN KEY (TaxClassID)
                  REFERENCES springcommerce.TaxClass (TaxClassID)
);

CREATE TABLE springcommerce.ProductProductOption (
       ProductProductOptionID BIGINT NOT NULL AUTO_INCREMENT
     , ProductID BIGINT NOT NULL
     , ProductOption BIGINT NOT NULL
     , ProductOptionID BIGINT NOT NULL
     , PRIMARY KEY (ProductProductOptionID)
     , INDEX (ProductID)
     , CONSTRAINT FK_ProductProductOption_1 FOREIGN KEY (ProductID)
                  REFERENCES springcommerce.Product (ProductID)
     , INDEX (ProductOptionID)
     , CONSTRAINT FK_ProductProductOption_2 FOREIGN KEY (ProductOptionID)
                  REFERENCES springcommerce.ProductOption (ProductOptionID)
);

CREATE TABLE springcommerce.ProductDownload (
       ProductDownloadID BIGINT NOT NULL AUTO_INCREMENT
     , ProductID BIGINT NOT NULL
     , FileName VARCHAR(255) NOT NULL
     , DownloadMaxDays INT(2)
     , DownloadMaxCount INT(2)
     , PRIMARY KEY (ProductDownloadID)
     , INDEX (ProductID)
     , CONSTRAINT FK_ProductDownload_1 FOREIGN KEY (ProductID)
                  REFERENCES springcommerce.Product (ProductID)
);

CREATE TABLE springcommerce.OrderProductDownload (
       OrderProductDownloadID BIGINT NOT NULL AUTO_INCREMENT
     , OrderProductID BIGINT NOT NULL
     , DownloadCount INT(2)
     , FileName VARCHAR(255)
     , DownloadMaxDays INT(2)
     , PRIMARY KEY (OrderProductDownloadID)
     , INDEX (OrderProductID)
     , CONSTRAINT FK_OrderProductDownload_1 FOREIGN KEY (OrderProductID)
                  REFERENCES springcommerce.OrderProduct (OrderProductID)
);

CREATE TABLE springcommerce.ProductDescription (
       ProductDescriptionID BIGINT NOT NULL AUTO_INCREMENT
     , ProductID BIGINT NOT NULL
     , LanguageID BIGINT NOT NULL
     , Description TEXT NOT NULL
     , URL VARCHAR(255)
     , PRIMARY KEY (ProductDescriptionID)
     , INDEX (ProductID)
     , CONSTRAINT FK_ProductDescription_1 FOREIGN KEY (ProductID)
                  REFERENCES springcommerce.Product (ProductID)
     , INDEX (LanguageID)
     , CONSTRAINT FK_ProductDescription_2 FOREIGN KEY (LanguageID)
                  REFERENCES springcommerce.Language (LanguageID)
);

CREATE TABLE springcommerce.ProductNotification (
       ProductNotificationID BIGINT NOT NULL AUTO_INCREMENT
     , ProductID BIGINT NOT NULL
     , CustomerID BIGINT NOT NULL
     , PRIMARY KEY (ProductNotificationID)
     , INDEX (ProductID)
     , CONSTRAINT FK_ProductNotification_1 FOREIGN KEY (ProductID)
                  REFERENCES springcommerce.Product (ProductID)
     , INDEX (CustomerID)
     , CONSTRAINT FK_ProductNotification_2 FOREIGN KEY (CustomerID)
                  REFERENCES springcommerce.Customer (CustomerID)
);

CREATE TABLE springcommerce.ReviewDescription (
       ReviewDescriptionID BIGINT NOT NULL AUTO_INCREMENT
     , ReviewID BIGINT NOT NULL
     , LanguageID BIGINT NOT NULL
     , ReviewText TEXT NOT NULL
     , PRIMARY KEY (ReviewDescriptionID)
     , INDEX (ReviewID)
     , CONSTRAINT FK_ReviewDescription_1 FOREIGN KEY (ReviewID)
                  REFERENCES springcommerce.Review (ReviewID)
     , INDEX (LanguageID)
     , CONSTRAINT FK_ReviewDescription_2 FOREIGN KEY (LanguageID)
                  REFERENCES springcommerce.Language (LanguageID)
);

CREATE TABLE springcommerce.TaxRate (
       TaxRateID BIGINT NOT NULL AUTO_INCREMENT
     , TaxZoneID BIGINT NOT NULL
     , TaxClassID BIGINT NOT NULL
     , TaxPriority INT(5) NOT NULL DEFAULT 1
     , TaxRate DEC(7, 4) NOT NULL
     , Description VARCHAR(255)
     , DateModified DATETIME NOT NULL
     , DateAdded DATETIME NOT NULL
     , PRIMARY KEY (TaxRateID)
     , INDEX (TaxClassID)
     , CONSTRAINT FK_TaxRate_1 FOREIGN KEY (TaxClassID)
                  REFERENCES springcommerce.TaxClass (TaxClassID)
     , INDEX (TaxZoneID)
     , CONSTRAINT FK_TaxRate_2 FOREIGN KEY (TaxZoneID)
                  REFERENCES springcommerce.TaxZone (TaxZoneID)
);

CREATE TABLE springcommerce.TaxZoneGeographicalZone (
       TaxZoneGeographicalZoneID BIGINT NOT NULL AUTO_INCREMENT
     , TaxZoneID BIGINT NOT NULL
     , GeographicalZone BIGINT NOT NULL
     , GeographicalZoneID BIGINT NOT NULL
     , PRIMARY KEY (TaxZoneGeographicalZoneID)
     , INDEX (TaxZoneID)
     , CONSTRAINT FK_TaxZoneGeographicalZone_1 FOREIGN KEY (TaxZoneID)
                  REFERENCES springcommerce.TaxZone (TaxZoneID)
     , INDEX (GeographicalZoneID)
     , CONSTRAINT FK_TaxZoneGeographicalZone_2 FOREIGN KEY (GeographicalZoneID)
                  REFERENCES springcommerce.GeographicalZone (GeographicalZoneID)
);

CREATE TABLE springcommerce.BasketProduct (
       BasketProductID BIGINT NOT NULL AUTO_INCREMENT
     , BasketID BIGINT NOT NULL
     , ProductID BIGINT NOT NULL
     , PRIMARY KEY (BasketProductID)
     , INDEX (ProductID)
     , CONSTRAINT FK_BasketProduct_1 FOREIGN KEY (ProductID)
                  REFERENCES springcommerce.Product (ProductID)
     , INDEX (BasketID)
     , CONSTRAINT FK_BasketProduct_2 FOREIGN KEY (BasketID)
                  REFERENCES springcommerce.Basket (BasketID)
);

