DROP TABLE IF EXISTS FILMS, USERS, FRIENDS, LIKES, RATING, GENRE, FILMS_GENRE;

create table if not exists USERS
(
    USER_ID  INTEGER auto_increment,
    NAME     CHARACTER VARYING(30) not null,
    EMAIL    CHARACTER VARYING(30) not null,
    LOGIN    CHARACTER VARYING(30) not null,
    BIRTHDAY DATE                  not null,
    constraint USERS_PK
        primary key (USER_ID)
);

create table if not exists FRIENDS
(
    FRIEND_ID INTEGER not null,
    USER_ID   INTEGER not null,
    STATUS    BOOLEAN not null,
  --  constraint FRIENDS_PK primary key (FRIEND_ID),
    constraint FRIENDS_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS
);

create table if not exists FILMS
(
    FILM_ID     INTEGER auto_increment,
    NAME        CHARACTER VARYING(30) not null,
    DESCRIPTION CHARACTER VARYING(30) not null,
    DURATION    INTEGER               not null,
    RELEASEDATE DATE                  not null,
    RATING      INTEGER,
    constraint FILMS_PK
        primary key (FILM_ID)
);

create table if not exists GENRE
(
    GENRE_ID INTEGER               not null,
    NAME     CHARACTER VARYING(20) not null,
    constraint GENRE_PK
        primary key (GENRE_ID)
);

create table if not exists FILMS_GENRE
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER,
    constraint FILMS_GENRE_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS (FILM_ID),
    constraint FILMS_GENRE_GENRE_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRE (GENRE_ID)
);


create table if not exists LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER,
    constraint LIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS (FILM_ID),
    constraint LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS (USER_ID)
);

create table if not exists RATING
(
    RATING_ID INTEGER               not null,
    NAME      CHARACTER VARYING(30) not null,
    constraint RATING_PK
        primary key (RATING_ID)
);


insert into USERS(NAME, EMAIL, LOGIN, BIRTHDAY) values ( 'vasya', '123@mail.ru','123','1990-01-01' );
select * from USERS