INSERT INTO Institut(name) VALUES('Analogtechnick und Sensorik');
INSERT INTO Institut(name) VALUES('Digitale Signalverarbeitung');
INSERT INTO Institut(name) VALUES('Eingebettete Systeme und Medizintechnik');
INSERT INTO Institut(name) VALUES('Softwaretechnick und Datenkommunikation');
INSERT INTO Institut(name) VALUES('Fakultät');

INSERT INTO Professor(name, institut) VALUES('Barth', 4);
INSERT INTO Professor(name, institut) VALUES('Feldes', 2);
INSERT INTO Professor(name, institut) VALUES('Steglich', 1);
INSERT INTO Professor(name, institut) VALUES('Ackermann', 3);
INSERT INTO Professor(name, institut) VALUES('Dekan', 5);
INSERT INTO Professor(name, institut) VALUES('Kabulepa', 3);

INSERT INTO Investition(betrag, art, professor, kostenstelle) VALUES(1000.00, 'Hardware', 4, 3);
INSERT INTO Investition(betrag, art, professor, kostenstelle) VALUES(10000.00, 'Sonstiges', 5, 5);
INSERT INTO Investition(betrag, art, professor, kostenstelle) VALUES(-1000.00, 'Infrastruktur', 1, 4);
INSERT INTO Investition(betrag, art, professor, kostenstelle) VALUES(100.50, 'Software', 2, 2);
INSERT INTO Investition(betrag, art, professor, kostenstelle) VALUES(0.99, 'Hardware', 3, 1);