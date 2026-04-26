#import "./../cp3/cover.typ": semestralka

#show: semestralka.with(
  title: "CP4 - DBS - Transakce a trigger",
  author: "Michal Hrouda a Lukáš Hrubec",
  version: "v1.0",
  date: "25. 4. 2026",
  logo: image("./../cp3/logo_CVUT.jpg"),
)

#let sql(caption: none, breakable: false, query) = {
  figure(
    supplement: "SQL",
    kind: "sql",
    caption: caption,
    align(left)[
      #block(
        breakable: breakable,
        width: 100%,
        fill: rgb("#f6f8fa"),
        stroke: (left: 3pt + rgb("#0065bd")),
        radius: (right: 4pt),
        inset: (x: 14pt, y: 10pt),
        query,
      )],
  )
}

#show figure.where(kind: "sql"): set block(breakable: true)

#show outline.entry.where(
  level: 1,
): set block(above: 1.2em)

#outline()

#pagebreak()

= Transakce

Tato transakce řeší proces bezpečné výměny manažera v konkrétním oddělení. Operace zahrnuje odstranění stávajícího záznamu o manažerovi a okamžité jmenování nového zaměstnance do této role. Využíváme úroveň izolace SERIALIZABLE.
Konflikt: Pokud by dva administrátoři prováděli výměnu manažera ve stejném oddělení současně bez použití transakce, mohlo by dojít k situaci, kdy by se systém snažil vložit dva záznamy se stejným id_oddeleni, což by vyvolalo chybu.


#sql(
  ```sql
  BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE;

  DELETE FROM "Manazer"
  WHERE "id_oddeleni" = 2;

  INSERT INTO "Manazer" ("uroven_pravomoci", "id_zamestnanec", "id_oddeleni")
  VALUES (4, 24, 2);
  COMMIT;
  ```,
)

#figure(
  caption: "Využití transakce",
  image("./other/transaction.png"),
)

#pagebreak()
= Vytvoření a použití pohledu

Pohled v_kontaktni_seznam_manazeru slouží jako virtuální tabulka, která v reálném čase agreguje data z tabulek "Oddeleni", "Manazer", "Zamestnanec" a "Telefon".
Uživatel (např. recepční nebo ředitel) nemusí zadávat složité dozazy. Stačí mu zavolat jeden jednoduchý dotaz nad tímto pohledem.
Pohled neobsahuje citlivé údaje jako rodná čísla nebo data nástupu, které jsou uloženy v tabulce "Zamestnanec", čímž napomáhá dodržování zásad GDPR.


#sql(
  ```sql
  CREATE VIEW v_kontaktni_seznam_manazeru AS
  SELECT
      o.nazev AS oddeleni,
      z.jmeno || ' ' || z.prijmeni AS manazer,
      z.email,
      t.telefon
  FROM "Oddeleni" o
  JOIN "Manazer" m ON o.id_oddeleni = m.id_oddeleni
  JOIN "Zamestnanec" z ON m.id_zamestnanec = z.id_zamestnanec
  LEFT JOIN "Telefon" t ON z.id_zamestnanec = t.id_zamestnanec
  ORDER BY o.nazev;
  ```,
)

#figure(
  caption: "Ukázka výstupu view",
  image("./other/view.png"),
)

#pagebreak()
= Vytvoření a použití triggeru

Pro zajištění konzistence dat v modulu docházky jsme navrhli trigger trg_kontrola_dochazky. Tento spouštěč se aktivuje automaticky před každým vložením (BEFORE INSERT) nového řádku do tabulky "ZaznamDochazky".
Jeho úkolem je zkontrolovat, zda se zaměstnanec nesnaží přihlásit (zadat nový čas příchodu) v situaci, kdy ještě neuzavřel svůj předchozí záznam (tzn. v databázi existuje jeho předchozí záznam, kde je cas_odchodu roven NULL). Pokud takový neuzavřený záznam existuje, trigger vložení zablokuje a vyhodí chybovou hlášku. Zabráníme tím vzniku překrývajících se a nelogických docházkových bloků.

#sql(
  ```sql
  CREATE FUNCTION kontrola_nedokoncene_dochazky()
  RETURNS TRIGGER AS $$
  BEGIN
      IF EXISTS (
          SELECT 1 FROM "ZaznamDochazky"
          WHERE "id_zamestnanec" = NEW."id_zamestnanec"
            AND "cas_odchodu" IS NULL
      ) THEN
          RAISE EXCEPTION 'Chyba: Zaměstnanec (ID %) se nemůže znovu přihlásit, dokud neukončí předchozí docházku!', NEW."id_zamestnanec";
      END IF;

      RETURN NEW;
  END;
  $$ LANGUAGE plpgsql;
  ```,
)

#sql(
  caption: "Vytvoření triggeru",
  ```sql
  CREATE TRIGGER trg_kontrola_dochazky BEFORE INSERT ON "ZaznamDochazky"
  FOR EACH ROW EXECUTE FUNCTION kontrola_nedokoncene_dochazky();
  ```,
)

#figure(
  caption: "Vytvoření a ukázka funkčnosti triggeru",
  image("./other/trigger.png"),
)

#pagebreak()
= Index

V systému pro evidenci docházky je nejčastější operací vyhledávání historie záznamů pro jednoho konkrétního zaměstnance (např. při měsíčním zpracování mezd nebo kontrole absencí).
Před vytvořením indexu: Pokud personalista hledá docházku zaměstnance (např. s ID 5), databáze musí provést tzv. Sequential Scan. To znamená, že musí vzít tabulku, která může mít statisíce záznamů, a projít ji celou řádek po řádku, aby našla ty správné dny. S přibývajícími roky historie by se tento dotaz neustále zpomaloval.
Po vytvoření indexu: Díky indexu si databáze vytvoří logický stromový rejstřík. Při spuštění stejného dotazu plánovač použije Index Scan. Databáze přesně ví, na kterém místě na disku leží záznamy daného zaměstnance, a sáhne přímo pro ně. Čas spuštění (Execution Time) klesne z desítek či stovek milisekund na zlomky milisekundy, a to bez ohledu na to, jak moc tabulka v budoucnu naroste.

#sql(
  ```sql
  CREATE INDEX idx_dochazka_zamestnanec
  ON "ZaznamDochazky"("id_zamestnanec");
  ```,
)

#figure(
  caption: "Ukázka funkčnosti indexu",
  image("./other/index.png"),
)
