insert into Rollen values(1,'Chef');
insert into Rollen values(2,'Standortleiter');

insert into Nutzer values(1,'Max',md5('1234'),1);
insert into Nutzer values(2,'Peter',md5('1234'),2);


insert into Standort values(1,'Manneheim');
insert into Standort values(2,'Heidelberg');
insert into Standort values(3,'Oftersheim');

insert into Standortverteilung values(2,1);

insert into Kamera values(1,'Stegbild Karlsruhe Ruderklub','aktiv','https://www.rheinklub-alemannia.de/cgi-bin/camdownload.pl?0.8065891533257915','22-05-2025',1);