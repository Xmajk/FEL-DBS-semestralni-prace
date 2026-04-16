#import "cover.typ": semestralka

#show: semestralka.with(
  title: "CP3 - DBS - Vytvoření databáze",
  author: "Michal Hrouda a Lukáš Hrubec",
  version: "v1.0",
  date: "16. 4. 2026",
  logo: image("./logo_CVUT.jpg"),
)

#let sql(caption: none, breakable: false, query) = {
  figure(
    supplement: "SQL",
    kind: "sql",
    caption: caption,
    block(
      breakable: breakable,
      width: 100%,
      fill: rgb("#f6f8fa"),
      stroke: (left: 3pt + rgb("#0065bd")),
      radius: (right: 4pt),
      inset: (x: 14pt, y: 10pt),
      query,
    ),
  )
}

#show figure.where(kind: "sql"): set block(breakable: true)

#show outline.entry.where(
  level: 1,
): set block(above: 1.2em)

#outline()

#pagebreak()

= DDL SQL pro vytvoření databáze

#sql(
  caption: "SQL DDL příkazy pro vytvoření databáze",
  breakable: true,
  ```sql
  DROP TABLE IF EXISTS "UcastniSe" CASCADE;
  DROP TABLE IF EXISTS "ZaznamDochazky" CASCADE;
  DROP TABLE IF EXISTS "Externista" CASCADE;
  DROP TABLE IF EXISTS "Manazer" CASCADE;
  DROP TABLE IF EXISTS "Adresa" CASCADE;
  DROP TABLE IF EXISTS "Telefon" CASCADE;
  DROP TABLE IF EXISTS "Zamestnanec" CASCADE;
  DROP TABLE IF EXISTS "Projekt" CASCADE;
  DROP TABLE IF EXISTS "Pozice" CASCADE;
  DROP TABLE IF EXISTS "Oddeleni" CASCADE;


  CREATE TABLE "Oddeleni" (
  	"id_oddeleni" int4 NOT NULL,
  	"cislo_oddeleni" varchar(6) NOT NULL,
  	"nazev" varchar(100) NOT NULL,
  	"lokace" varchar(100) NOT NULL,
  	"popis" text,
  	"id_nadrizene_oddeleni" int4,
  	PRIMARY KEY("id_oddeleni"),
  	CONSTRAINT "unique_cislo_oddeleni" UNIQUE("cislo_oddeleni"),
  	CONSTRAINT "unique_nazev_lokace" UNIQUE("nazev","lokace"),
  	CONSTRAINT "Ref_Oddeleni_to_Oddeleni" FOREIGN KEY ("id_nadrizene_oddeleni")
  		REFERENCES "Oddeleni"("id_oddeleni")
  		MATCH SIMPLE
  		ON DELETE SET NULL
  		ON UPDATE NO ACTION
  		NOT DEFERRABLE
  );

  CREATE TABLE "Pozice" (
  	"id_pozice" int4 NOT NULL,
  	"nazev" varchar(100) NOT NULL,
  	"uroven" int4 NOT NULL,
  	"popis" text,
  	PRIMARY KEY("id_pozice"),
  	CONSTRAINT "unique_nazev" UNIQUE("nazev"),
  	CONSTRAINT "check_validni_uroven" CHECK(uroven BETWEEN 1 AND 10)
  );

  CREATE TABLE "Projekt" (
  	"id_projekt" int4 NOT NULL,
  	"nazev" varchar(64) NOT NULL,
  	"termin_zahajeni" date NOT NULL,
  	"termin_ukonceni" date,
  	"popis" text,
  	PRIMARY KEY("id_projekt"),
  	CONSTRAINT "unique_nazev_termin_zahajeni" UNIQUE("nazev","termin_zahajeni"),
  	CONSTRAINT "check_termin_zahajeni_ukonceni" CHECK(termin_ukonceni >= termin_zahajeni)
  );

  CREATE TABLE "Zamestnanec" (
  	"id_zamestnanec" int4 NOT NULL,
  	"rodne_cislo" varchar(11) NOT NULL,
  	"email" varchar(100) NOT NULL,
  	"jmeno" varchar(100) NOT NULL,
  	"prijmeni" varchar(100) NOT NULL,
  	"datum_nastupu" date NOT NULL,
  	"id_oddeleni" int4 NOT NULL,
  	"id_pozice" int4,
  	PRIMARY KEY("id_zamestnanec"),
  	CONSTRAINT "unique_rodne_cislo" UNIQUE("rodne_cislo"),
  	CONSTRAINT "unique_email" UNIQUE("email"),
  	CONSTRAINT "unique_zamestnanec_oddeleni" UNIQUE("id_zamestnanec","id_oddeleni"),
  	CONSTRAINT "check_email_format" CHECK(email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
  	CONSTRAINT "check_rodne_cislo_format" CHECK(rodne_cislo ~ '^[0-9]{6}/[0-9]{3,4}$'),
  	CONSTRAINT "Ref_Zamestnanec_to_Oddeleni" FOREIGN KEY ("id_oddeleni")
  		REFERENCES "Oddeleni"("id_oddeleni")
  		MATCH SIMPLE
  		ON DELETE RESTRICT
  		ON UPDATE CASCADE
  		NOT DEFERRABLE,
  	CONSTRAINT "Ref_Zamestnanec_to_Pozice" FOREIGN KEY ("id_pozice")
  		REFERENCES "Pozice"("id_pozice")
  		MATCH SIMPLE
  		ON DELETE SET NULL
  		ON UPDATE CASCADE
  		NOT DEFERRABLE
  );

  CREATE TABLE "Telefon" (
  	"id_telefon" int4 NOT NULL,
  	"telefon" varchar NOT NULL,
  	"id_zamestnanec" int4 NOT NULL,
  	PRIMARY KEY("id_telefon"),
  	CONSTRAINT "unique_telefon_zamestnanec" UNIQUE("telefon","id_zamestnanec"),
  	CONSTRAINT "check_validni_telefon" CHECK(telefon ~ '^\+?[0-9\s]{9,15}$'),
  	CONSTRAINT "Ref_Telefon_to_Zamestnanec" FOREIGN KEY ("id_zamestnanec")
  		REFERENCES "Zamestnanec"("id_zamestnanec")
  		MATCH SIMPLE
  		ON DELETE CASCADE
  		ON UPDATE CASCADE
  		NOT DEFERRABLE
  );

  CREATE TABLE "Adresa" (
  	"id_adresa" int4 NOT NULL,
  	"ulice" varchar NOT NULL,
  	"mesto" varchar NOT NULL,
  	"psc" varchar NOT NULL,
  	"id_zamestnanec" int4 NOT NULL,
  	PRIMARY KEY("id_adresa"),
  	CONSTRAINT "unique_ulice_mesto_psc_zamestnanec" UNIQUE("ulice","mesto","psc","id_zamestnanec"),
  	CONSTRAINT "check_validni_psc" CHECK(psc ~ '^[0-9]{3}\s?[0-9]{2}$'),
  	CONSTRAINT "Ref_Adresa_to_Zamestnanec" FOREIGN KEY ("id_zamestnanec")
  		REFERENCES "Zamestnanec"("id_zamestnanec")
  		MATCH SIMPLE
  		ON DELETE CASCADE
  		ON UPDATE CASCADE
  		NOT DEFERRABLE
  );

  CREATE TABLE "Manazer" (
  	"id_manazer" int4 NOT NULL,
  	"uroven_pravomoci" int4 NOT NULL,
  	"id_zamestnanec" int4 NOT NULL,
  	"id_oddeleni" int4 NOT NULL,
  	PRIMARY KEY("id_manazer"),
  	CONSTRAINT "unique_manazer_zamestnanec" UNIQUE("id_zamestnanec"),
  	CONSTRAINT "check_validni_uroven_pravomoci" CHECK(uroven_pravomoci BETWEEN 1 AND 5),
  	CONSTRAINT "Ref_Manazer_to_Zamestnanec" FOREIGN KEY ("id_zamestnanec", "id_oddeleni")
  		REFERENCES "Zamestnanec"("id_zamestnanec", "id_oddeleni")
  		MATCH SIMPLE
  		ON DELETE CASCADE
  		ON UPDATE CASCADE
  		NOT DEFERRABLE
  );

  CREATE TABLE "Externista" (
  	"id_externista" int4 NOT NULL,
  	"nazev_agentury" varchar NOT NULL,
  	"konec_smlouvy" date NOT NULL,
  	"id_zamestnanec" int4 NOT NULL,
  	PRIMARY KEY("id_externista"),
  	CONSTRAINT "unique_externista_zamestnanec" UNIQUE("id_zamestnanec"),
  	CONSTRAINT "Ref_Externista_to_Zamestnanec" FOREIGN KEY ("id_zamestnanec")
  		REFERENCES "Zamestnanec"("id_zamestnanec")
  		MATCH SIMPLE
  		ON DELETE CASCADE
  		ON UPDATE CASCADE
  		NOT DEFERRABLE
  );

  CREATE TABLE "ZaznamDochazky" (
  	"id_dochazka" int4 NOT NULL,
  	"cas_prichodu" timestamp NOT NULL,
  	"cas_odchodu" timestamp,
  	"typ_zaznamu" varchar(100) NOT NULL,
  	"id_zamestnanec" int4 NOT NULL,
  	PRIMARY KEY("id_dochazka"),
  	CONSTRAINT "unique_cas_prichodu_zamestnanec" UNIQUE("cas_prichodu","id_zamestnanec"),
  	CONSTRAINT "check_cas_prichodu_cas_odchodu" CHECK(cas_odchodu > cas_prichodu),
  	CONSTRAINT "Ref_ZaznamDochazky_to_Zamestnanec" FOREIGN KEY ("id_zamestnanec")
  		REFERENCES "Zamestnanec"("id_zamestnanec")
  		MATCH SIMPLE
  		ON DELETE CASCADE
  		ON UPDATE CASCADE
  		NOT DEFERRABLE
  );

  CREATE TABLE "UcastniSe" (
  	"role" varchar NOT NULL,
  	"id_zamestnanec" int4 NOT NULL,
  	"id_projekt" int4 NOT NULL,
  	PRIMARY KEY("id_zamestnanec","id_projekt"),
  	CONSTRAINT "Ref_UcastniSe_to_Zamestnanec" FOREIGN KEY ("id_zamestnanec")
  		REFERENCES "Zamestnanec"("id_zamestnanec")
  		MATCH SIMPLE
  		ON DELETE CASCADE
  		ON UPDATE CASCADE
  		NOT DEFERRABLE,
  	CONSTRAINT "Ref_UcastniSe_to_Projekt" FOREIGN KEY ("id_projekt")
  		REFERENCES "Projekt"("id_projekt")
  		MATCH SIMPLE
  		ON DELETE CASCADE
  		ON UPDATE CASCADE
  		NOT DEFERRABLE
  );
  ```,
)

= Relační diagram

= ER diagram
#figure(
  image("er_model.png", width: 100%),
  caption: [
    ER diagram
  ],
)

= SQL query dotazy
