DROP TABLE IF EXISTS ADDRESSES;
DROP TABLE IF EXISTS USERS;

CREATE TABLE USERS (
    ID INTEGER AUTO_INCREMENT PRIMARY KEY,
    ACTIVE BOOLEAN,
    USERNAME VARCHAR(30),
    EMAIL VARCHAR(30),
    LEVEL INTEGER,
    REGISTRATION_DATE DATE
);

CREATE TABLE ADDRESSES (
    USER_ID INTEGER AUTO_INCREMENT PRIMARY KEY,
    STREET VARCHAR(30) NOT NULL,
    CITY VARCHAR(20) NOT NULL
);
