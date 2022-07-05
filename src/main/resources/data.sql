--insert into users(NAME, LOGIN, EMAIL, BIRTHDAY) VALUES ('Вася' , 'Vasya','vasya@mail.ru','2020-03-28');
--insert into FILMS(name, description, duration, releasedate) VALUES ('Матрица' , 'война людей и машин','120','2000-03-28');
delete from FRIENDS;
delete  from FILMS_GENRE;
delete from LIKES;
delete  from USERS;
delete  from FILMS;
delete  from GENRE;
delete  from RATING;
delete from REVIEWS;




insert into GENRE(GENRE_ID, NAME) VALUES ( '1','Комедия' );
insert into GENRE(GENRE_ID, NAME) VALUES ( '2','Драма' );
insert into GENRE(GENRE_ID, NAME) VALUES ( '3','Мультфильм' );
insert into GENRE(GENRE_ID, NAME) VALUES ( '4','Триллер' );
insert into GENRE(GENRE_ID, NAME) VALUES ( '5','Документальный' );
insert into GENRE(GENRE_ID, NAME) VALUES ( '6','Боевик' );

insert into RATING(RATING_ID, NAME) VALUES ( '1','G' );
insert into RATING(RATING_ID, NAME) VALUES ( '2','PG' );
insert into RATING(RATING_ID, NAME) VALUES ( '3','PG-13' );
insert into RATING(RATING_ID, NAME) VALUES ( '4','R' );
insert into RATING(RATING_ID, NAME) VALUES ( '5','NC-17' );
