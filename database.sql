delimiter @

create database if not exists booksdb
@
drop table if exists booksdb.books
@
create table booksdb.books (
    id INT NOT NULL AUTO_INCREMENT,
    name varchar(255) CHARACTER SET utf8 NOT NULL,
    author varchar(255) CHARACTER SET utf8 NOT NULL,
    PRIMARY KEY (id)
)