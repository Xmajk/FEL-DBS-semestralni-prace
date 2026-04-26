#import "cover.typ": semestralka

#show: semestralka.with(
  title: "CP3 - DBS - Vytvoření databáze a SQL dotazy",
  author: "Michal Hrouda a Lukáš Hrubec",
  version: "v1.0",
  date: "19. 4. 2026",
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

ON UPDATE CASCADE
Kde je to použito: U většiny vazeb.
Je to standardní a nejbezpečnější přístup. Pokud by se z nějakého velmi specifického důvodu změnilo ID zaměstnance, kaskádová aktualizace zajistí, že se toto nové ID bleskově přepíše do všech podřízených tabulek.

ON DELETE CASCADE
Kde je to použito: U tabulek Telefon, Adresa, ZaznamDochazky, Manazer, Externista a UcastniSe (vazby na Zamestnanec a Projekt).
Proč je to zvoleno: Tyto tabulky obsahují data, která bez svého "rodiče" nedávají absolutně žádný smysl (jsou na něm existenčně závislá). Pokud z databáze smažete zaměstnance, je nelogické si nadále uchovávat jeho telefonní číslo, adresu nebo historii jeho docházky.

ON DELETE RESTRICT
Kde je to použito: Vazba z tabulky Zamestnanec na Oddeleni.
Zabraňuje tomu, aby administrátor omylem smazal oddělení, ve kterém stále pracují nějací lidé. Pokud by se o to pokusil, databáze akci zablokuje a vyhodí chybu. Než se oddělení smaže, musí se jeho zaměstnanci nejprve ručně převést do jiného oddělení.

ON DELETE SET NULL
Kde je to použito: Vazba z tabulky Zamestnanec na Pozice a u hierarchie Oddeleni na nadrizene_oddeleni.
Použití SET NULL zajistí, že při smazání pozice v databázi zůstanou zaměstnaci, kteří tuto pozici měli, pouze se jim v kolonce "id_pozice" objeví prázdná hodnota (NULL).

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
      "id_oddeleni" SERIAL NOT NULL,
      "cislo_oddeleni" varchar(6) NOT NULL,
      "nazev" varchar(100) NOT NULL,
      "lokace" varchar(100) NOT NULL,
      "popis" text,
      "id_nadrizene_oddeleni" int4,
      PRIMARY KEY("id_oddeleni"),
      CONSTRAINT "unique_cislo_oddeleni" UNIQUE("cislo_oddeleni"),
      CONSTRAINT "unique_nazev_lokace" UNIQUE("nazev","lokace"),
      CONSTRAINT "Ref_Oddeleni_to_Oddeleni" FOREIGN KEY ("id_nadrizene_oddeleni") REFERENCES "Oddeleni"("id_oddeleni") MATCH SIMPLE ON DELETE SET NULL ON UPDATE NO ACTION NOT DEFERRABLE
  );

  CREATE TABLE "Pozice" (
      "id_pozice" SERIAL NOT NULL,
      "nazev" varchar(100) NOT NULL,
      "uroven" int4 NOT NULL,
      "popis" text,
      PRIMARY KEY("id_pozice"),
      CONSTRAINT "unique_nazev" UNIQUE("nazev"),
      CONSTRAINT "check_validni_uroven" CHECK(uroven BETWEEN 1 AND 10)
  );

  CREATE TABLE "Projekt" (
      "id_projekt" SERIAL NOT NULL,
      "nazev" varchar(64) NOT NULL,
      "termin_zahajeni" date NOT NULL,
      "termin_ukonceni" date,
      "popis" text,
      PRIMARY KEY("id_projekt"),
      CONSTRAINT "unique_nazev_termin_zahajeni" UNIQUE("nazev","termin_zahajeni"),
      CONSTRAINT "check_termin_zahajeni_ukonceni" CHECK(termin_ukonceni >= termin_zahajeni)
  );

  CREATE TABLE "Zamestnanec" (
      "id_zamestnanec" SERIAL NOT NULL,
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
      CONSTRAINT "Ref_Zamestnanec_to_Oddeleni" FOREIGN KEY ("id_oddeleni") REFERENCES "Oddeleni"("id_oddeleni") MATCH SIMPLE ON DELETE RESTRICT ON UPDATE CASCADE NOT DEFERRABLE,
      CONSTRAINT "Ref_Zamestnanec_to_Pozice" FOREIGN KEY ("id_pozice") REFERENCES "Pozice"("id_pozice") MATCH SIMPLE ON DELETE SET NULL ON UPDATE CASCADE NOT DEFERRABLE
  );

  CREATE TABLE "Telefon" (
      "id_telefon" SERIAL NOT NULL,
      "telefon" varchar NOT NULL,
      "id_zamestnanec" int4 NOT NULL,
      PRIMARY KEY("id_telefon"),
      CONSTRAINT "unique_telefon_zamestnanec" UNIQUE("telefon","id_zamestnanec"),
      CONSTRAINT "check_validni_telefon" CHECK(telefon ~ '^\+?[0-9\s]{9,15}$'),
      CONSTRAINT "Ref_Telefon_to_Zamestnanec" FOREIGN KEY ("id_zamestnanec") REFERENCES "Zamestnanec"("id_zamestnanec") MATCH SIMPLE ON DELETE CASCADE ON UPDATE CASCADE NOT DEFERRABLE
  );

  CREATE TABLE "Adresa" (
      "id_adresa" SERIAL NOT NULL,
      "ulice" varchar NOT NULL,
      "mesto" varchar NOT NULL,
      "psc" varchar NOT NULL,
      "id_zamestnanec" int4 NOT NULL,
      PRIMARY KEY("id_adresa"),
      CONSTRAINT "unique_ulice_mesto_psc_zamestnanec" UNIQUE("ulice","mesto","psc","id_zamestnanec"),
      CONSTRAINT "check_validni_psc" CHECK(psc ~ '^[0-9]{3}\s?[0-9]{2}$'),
      CONSTRAINT "Ref_Adresa_to_Zamestnanec" FOREIGN KEY ("id_zamestnanec") REFERENCES "Zamestnanec"("id_zamestnanec") MATCH SIMPLE ON DELETE CASCADE ON UPDATE CASCADE NOT DEFERRABLE
  );

  CREATE TABLE "Manazer" (
      "id_manazer" SERIAL NOT NULL,
      "uroven_pravomoci" int4 NOT NULL,
      "id_zamestnanec" int4 NOT NULL,
      "id_oddeleni" int4 NOT NULL,
      PRIMARY KEY("id_manazer"),
      CONSTRAINT "unique_manazer_zamestnanec" UNIQUE("id_zamestnanec"),
      CONSTRAINT "unique_oddeleni" UNIQUE("id_oddeleni"),
      CONSTRAINT "check_validni_uroven_pravomoci" CHECK(uroven_pravomoci BETWEEN 1 AND 5),
      CONSTRAINT "Ref_Manazer_to_Zamestnanec" FOREIGN KEY ("id_zamestnanec", "id_oddeleni") REFERENCES "Zamestnanec"("id_zamestnanec", "id_oddeleni") MATCH SIMPLE ON DELETE CASCADE ON UPDATE CASCADE NOT DEFERRABLE
  );

  CREATE TABLE "Externista" (
      "id_externista" SERIAL NOT NULL,
      "nazev_agentury" varchar NOT NULL,
      "konec_smlouvy" date NOT NULL,
      "id_zamestnanec" int4 NOT NULL,
      PRIMARY KEY("id_externista"),
      CONSTRAINT "unique_externista_zamestnanec" UNIQUE("id_zamestnanec"),
      CONSTRAINT "Ref_Externista_to_Zamestnanec" FOREIGN KEY ("id_zamestnanec") REFERENCES "Zamestnanec"("id_zamestnanec") MATCH SIMPLE ON DELETE CASCADE ON UPDATE CASCADE NOT DEFERRABLE
  );

  CREATE TABLE "ZaznamDochazky" (
      "id_dochazka" SERIAL NOT NULL,
      "cas_prichodu" timestamp NOT NULL,
      "cas_odchodu" timestamp,
      "typ_zaznamu" varchar(100) NOT NULL,
      "id_zamestnanec" int4 NOT NULL,
      PRIMARY KEY("id_dochazka"),
      CONSTRAINT "unique_cas_prichodu_zamestnanec" UNIQUE("cas_prichodu","id_zamestnanec"),
      CONSTRAINT "check_cas_prichodu_cas_odchodu" CHECK(cas_odchodu > cas_prichodu),
      CONSTRAINT "Ref_ZaznamDochazky_to_Zamestnanec" FOREIGN KEY ("id_zamestnanec") REFERENCES "Zamestnanec"("id_zamestnanec") MATCH SIMPLE ON DELETE CASCADE ON UPDATE CASCADE NOT DEFERRABLE
  );

  CREATE TABLE "UcastniSe" (
      "role" varchar NOT NULL,
      "id_zamestnanec" int4 NOT NULL,
      "id_projekt" int4 NOT NULL,
      PRIMARY KEY("id_zamestnanec","id_projekt"),
      CONSTRAINT "Ref_UcastniSe_to_Zamestnanec" FOREIGN KEY ("id_zamestnanec") REFERENCES "Zamestnanec"("id_zamestnanec") MATCH SIMPLE ON DELETE CASCADE ON UPDATE CASCADE NOT DEFERRABLE,
      CONSTRAINT "Ref_UcastniSe_to_Projekt" FOREIGN KEY ("id_projekt") REFERENCES "Projekt"("id_projekt") MATCH SIMPLE ON DELETE CASCADE ON UPDATE CASCADE NOT DEFERRABLE
  );
  ```,
)

= Relační model
#figure(
  image("./relationa_model.png", width: 100%),
  caption: [
    Relační model
  ],
)

= ER diagram
#figure(
  image("er_model.png", width: 100%),
  caption: [
    ER diagram
  ],
)

#pagebreak()

= SQL query dotazy

== Dotaz 1

Vypsání údajů o zaměstnancích a názvu jejich oddělení pro zaměstnance, kteří
nastoupili po 1. 1. 2023. Dotaz demonstruje vnitřní spojení tabulek
(`INNER JOIN`) a podmínku na data (`WHERE` s datovým typem `DATE`).

#sql(
  caption: none,
  breakable: false,
  ```sql
  SELECT z.jmeno, z.prijmeni, z.email, o.nazev AS oddeleni, z.datum_nastupu
  FROM "Zamestnanec" z
  INNER JOIN "Oddeleni" o ON z.id_oddeleni = o.id_oddeleni
  WHERE z.datum_nastupu > DATE '2023-01-01'
  ORDER BY z.datum_nastupu;
  ```,
)
#figure(
  image("query1.png", width: 100%),
  caption: none,
)

#pagebreak()

== Dotaz 2

Vypsání všech zaměstnanců včetně těch, kteří nemají přiřazenou pozici.
Dotaz demonstruje vnější spojení tabulek (`LEFT OUTER JOIN`) – u zaměstnanců
bez přiřazené pozice je ve sloupcích `pozice` a `uroven` hodnota `NULL`.

#sql(
  caption: none,
  breakable: false,
  ```sql
  SELECT z.id_zamestnanec, z.jmeno, z.prijmeni, p.nazev AS pozice, p.uroven
  FROM "Zamestnanec" z
  LEFT OUTER JOIN "Pozice" p ON z.id_pozice = p.id_pozice
  ORDER BY z.prijmeni, z.jmeno;
  ```,
)
#figure(
  image("query2.png", width: 100%),
  caption: none,
)

#pagebreak()

== Dotaz 3

Vypsání oddělení a počtu jejich zaměstnanců, kde je počet zaměstnanců
vyšší než 2. Dotaz demonstruje agregaci (`COUNT`, `GROUP BY`) a podmínku
na hodnotu agregační funkce pomocí klauzule `HAVING`.

#sql(
  caption: none,
  breakable: false,
  ```sql
  SELECT o.nazev AS oddeleni, o.lokace, COUNT(z.id_zamestnanec) AS pocet_zamestnancu
  FROM "Oddeleni" o
  INNER JOIN "Zamestnanec" z ON o.id_oddeleni = z.id_oddeleni
  GROUP BY o.id_oddeleni, o.nazev, o.lokace
  HAVING COUNT(z.id_zamestnanec) > 2
  ORDER BY pocet_zamestnancu DESC;
  ```,
)
#figure(
  image("query3.png", width: 100%),
  caption: none,
)

#pagebreak()

== Dotaz 4

Vypsání druhé stránky seznamu zaměstnanců (záznamy 6–10) seřazených podle
příjmení a jména vzestupně. Dotaz demonstruje řazení (`ORDER BY`) a
stránkování pomocí klauzulí `LIMIT` a `OFFSET`.

#sql(
  caption: none,
  breakable: false,
  ```sql
  SELECT id_zamestnanec, jmeno, prijmeni, email, datum_nastupu
  FROM "Zamestnanec"
  ORDER BY prijmeni ASC, jmeno ASC
  LIMIT 5 OFFSET 5;
  ```,
)
#figure(
  image("query4.png", width: 100%),
  caption: none,
)

#pagebreak()

== Dotaz 5

Výpis manažerů a externistů s pomocí klauzule `UNION`

#sql(
  caption: none,
  breakable: false,
  ```sql
  SELECT z.id_zamestnanec, z.jmeno, z.prijmeni, 'Manažer' AS typ
  FROM "Zamestnanec" z
  INNER JOIN "Manazer" m ON z.id_zamestnanec = m.id_zamestnanec
  UNION
  SELECT z.id_zamestnanec, z.jmeno, z.prijmeni, 'Externista' AS typ
  FROM "Zamestnanec" z
  INNER JOIN "Externista" e ON z.id_zamestnanec = e.id_zamestnanec
  ORDER BY prijmeni, jmeno;
  ```,
)
#figure(
  image("query5.png", width: 100%),
  caption: none,
)

#pagebreak()

== Dotaz 6

Výpis zaměstnanců, kteří se zůčastnili více než průměrného počtu projektů, pomocí využití `VNOŘENÉHO SELECTU`

#sql(
  caption: none,
  breakable: false,
  ```sql
  SELECT z.id_zamestnanec, z.jmeno, z.prijmeni,
         (SELECT COUNT(*)
          FROM "UcastniSe" u
          WHERE u.id_zamestnanec = z.id_zamestnanec) AS pocet_projektu
  FROM "Zamestnanec" z
  WHERE (SELECT COUNT(*)
         FROM "UcastniSe" u
         WHERE u.id_zamestnanec = z.id_zamestnanec) > (
      SELECT AVG(pocet)
      FROM (
          SELECT COUNT(*) AS pocet
          FROM "UcastniSe"
          GROUP BY id_zamestnanec
      ) AS prumer
  )
  ORDER BY pocet_projektu DESC;
  ```,
)
#figure(
  image("query6.png", width: 100%),
  caption: none,
)

#pagebreak()

== Dotaz 7

Udělení práv

#sql(
  caption: none,
  breakable: false,
  ```sql
  GRANT CONNECT ON DATABASE hroudmi5 TO hrubeluk;
  GRANT USAGE, CREATE ON SCHEMA public TO hrubeluk;
  GRANT SELECT, INSERT, UPDATE, DELETE ON "Oddeleni" TO hrubeluk;
  GRANT SELECT, INSERT, UPDATE, DELETE ON "Pozice" TO hrubeluk;
  GRANT SELECT, INSERT, UPDATE, DELETE ON "Projekt" TO hrubeluk;
  GRANT SELECT, INSERT, UPDATE, DELETE ON "Zamestnanec" TO hrubeluk;
  GRANT SELECT, INSERT, UPDATE, DELETE ON "Telefon" TO hrubeluk;
  GRANT SELECT, INSERT, UPDATE, DELETE ON "Adresa" TO hrubeluk;
  GRANT SELECT, INSERT, UPDATE, DELETE ON "Manazer" TO hrubeluk;
  GRANT SELECT, INSERT, UPDATE, DELETE ON "Externista" TO hrubeluk;
  GRANT SELECT, INSERT, UPDATE, DELETE ON "ZaznamDochazky" TO hrubeluk;
  GRANT SELECT, INSERT, UPDATE, DELETE ON "UcastniSe" TO hrubeluk;
  ```,
)

#figure(
  image("query7.png", width: 100%),
  caption: none,
)
