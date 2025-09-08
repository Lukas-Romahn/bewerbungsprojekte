create domain investitionsArt varchar(80) check (value in('Hardware', 'Software', 'Infrastruktur', 'Sonstiges'));

create table Institut(
    institut_id serial primary key,
    name varchar(80),
    kostenstelle_id serial unique
);

create table Professor(
    professor_id serial primary key,
    name varchar(80),
    institut serial references Institut(institut_id)
);

create table Investition(
    investition_id serial primary key,
    betrag float,
    art investitionsArt,
    professor serial references Professor(professor_id),
    kostenstelle serial references Institut(kostenstelle_id)
);
