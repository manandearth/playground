CREATE TABLE register ( username VARCHAR(20), password VARCHAR(100), role VARCHAR(20) DEFAULT 'user');
CREATE TABLE users (id SERIAL, email VARCHAR(50), author VARCHAR(20));
