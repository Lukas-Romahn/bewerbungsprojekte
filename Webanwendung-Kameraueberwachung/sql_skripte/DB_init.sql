CREATE DOMAIN KStatus text check (value in (
    'aktiv',
    'passiv',
    'wartung'
));

CREATE DOMAIN rolle text check( value in (
    'Admin',
    'Chef',
    'Standortleiter'
));


create Table Rollen (Rollenid Serial primary key,Rolle rolle );
CREATE TABLE Nutzer (userid Serial primary key,username text, pwdhash text, rollenid serial REFERENCES Rollen(Rollenid));
CREATE TABLE Standort (Standortid Serial Primary key, Adresse text);
CREATE TABLE Standortverteilung( userid Serial REFERENCES Nutzer(userid), Standortid Serial REFERENCES Standort(Standortid));



CREATE TABLE Kamera (kid serial PRIMARY KEY,name text,status KStatus,url text,date Date, Standortid Serial REFERENCES Standort (Standortid));
CREATE TABLE Bilder (bid Serial primary key, thumbnailUrl text,AblageUrl text,timestampBild timestamp);
