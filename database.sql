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
@
insert into booksdb.books values (1, 'Clean Code: A Handbook of Agile Software Craftsmanship', 'Robert C. Martin')