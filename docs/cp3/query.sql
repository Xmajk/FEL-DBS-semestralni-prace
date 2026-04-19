-- =============================================================================
-- SQL dotazy pro CP3 - DBS semestrální práce
-- Autoři: Michal Hrouda a Lukáš Hrubec
-- =============================================================================


-- -----------------------------------------------------------------------------
-- Dotaz 1: Vnitřní spojení tabulek + podmínka na data
-- Vypíše jméno, příjmení, email a název oddělení všech zaměstnanců,
-- kteří nastoupili po 1. 1. 2023. Demonstruje INNER JOIN a WHERE podmínku
-- na datovém typu DATE.
-- -----------------------------------------------------------------------------
SELECT z.jmeno, z.prijmeni, z.email, o.nazev AS oddeleni, z.datum_nastupu
FROM "Zamestnanec" z
INNER JOIN "Oddeleni" o ON z.id_oddeleni = o.id_oddeleni
WHERE z.datum_nastupu > DATE '2023-01-01'
ORDER BY z.datum_nastupu;


-- -----------------------------------------------------------------------------
-- Dotaz 2: Vnější spojení tabulek (LEFT OUTER JOIN)
-- Vypíše všechny zaměstnance i ty, kteří zatím nemají přiřazenou pozici.
-- U zaměstnanců bez přiřazené pozice se ve sloupcích "pozice" a "uroven"
-- zobrazí hodnota NULL.
-- -----------------------------------------------------------------------------
SELECT z.id_zamestnanec, z.jmeno, z.prijmeni, p.nazev AS pozice, p.uroven
FROM "Zamestnanec" z
LEFT OUTER JOIN "Pozice" p ON z.id_pozice = p.id_pozice
ORDER BY z.prijmeni, z.jmeno;


-- -----------------------------------------------------------------------------
-- Dotaz 3: Agregace + podmínka na hodnotu agregační funkce (HAVING)
-- Vypíše oddělení, ve kterých pracují více než 2 zaměstnanci, společně
-- s jejich počtem. Ukazuje použití GROUP BY, COUNT a HAVING.
-- -----------------------------------------------------------------------------
SELECT o.nazev AS oddeleni, o.lokace, COUNT(z.id_zamestnanec) AS pocet_zamestnancu
FROM "Oddeleni" o
INNER JOIN "Zamestnanec" z ON o.id_oddeleni = z.id_oddeleni
GROUP BY o.id_oddeleni, o.nazev, o.lokace
HAVING COUNT(z.id_zamestnanec) > 2
ORDER BY pocet_zamestnancu DESC;


-- -----------------------------------------------------------------------------
-- Dotaz 4: Řazení a stránkování (ORDER BY + LIMIT/OFFSET)
-- Vypíše druhou stránku (záznamy 6-10) zaměstnanců seřazených podle
-- příjmení a jména vzestupně. Typické použití pro stránkovaný výpis v UI.
-- -----------------------------------------------------------------------------
SELECT id_zamestnanec, jmeno, prijmeni, email, datum_nastupu
FROM "Zamestnanec"
ORDER BY prijmeni ASC, jmeno ASC
LIMIT 5 OFFSET 5;


-- -----------------------------------------------------------------------------
-- Dotaz 5: Množinová operace (UNION)
-- Vypíše sjednocení všech zaměstnanců, kteří jsou buď manažeři, nebo
-- externisté, s označením jejich typu. UNION automaticky odstraní duplicity.
-- -----------------------------------------------------------------------------
SELECT z.id_zamestnanec, z.jmeno, z.prijmeni, 'Manažer' AS typ
FROM "Zamestnanec" z
INNER JOIN "Manazer" m ON z.id_zamestnanec = m.id_zamestnanec
UNION
SELECT z.id_zamestnanec, z.jmeno, z.prijmeni, 'Externista' AS typ
FROM "Zamestnanec" z
INNER JOIN "Externista" e ON z.id_zamestnanec = e.id_zamestnanec
ORDER BY prijmeni, jmeno;


-- -----------------------------------------------------------------------------
-- Dotaz 6: Vnořený SELECT (korelovaný i nekorelovaný poddotaz)
-- Vypíše zaměstnance, kteří se účastní více projektů, než je průměrný
-- počet projektů na zaměstnance. Obsahuje poddotaz ve WHERE i SELECT klauzuli.
-- -----------------------------------------------------------------------------
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


-- -----------------------------------------------------------------------------
-- Dotaz 7: Definování oprávnění (GRANT)
-- Udělení čtecích i zápisových oprávnění nad všemi tabulkami databáze
-- druhému členovi týmu (Lukáš Hrubec - uživatel lhrubec).
-- -----------------------------------------------------------------------------
GRANT SELECT, INSERT, UPDATE, DELETE ON "Oddeleni"       TO lhrubec;
GRANT SELECT, INSERT, UPDATE, DELETE ON "Pozice"         TO lhrubec;
GRANT SELECT, INSERT, UPDATE, DELETE ON "Projekt"        TO lhrubec;
GRANT SELECT, INSERT, UPDATE, DELETE ON "Zamestnanec"    TO lhrubec;
GRANT SELECT, INSERT, UPDATE, DELETE ON "Telefon"        TO lhrubec;
GRANT SELECT, INSERT, UPDATE, DELETE ON "Adresa"         TO lhrubec;
GRANT SELECT, INSERT, UPDATE, DELETE ON "Manazer"        TO lhrubec;
GRANT SELECT, INSERT, UPDATE, DELETE ON "Externista"     TO lhrubec;
GRANT SELECT, INSERT, UPDATE, DELETE ON "ZaznamDochazky" TO lhrubec;
GRANT SELECT, INSERT, UPDATE, DELETE ON "UcastniSe"      TO lhrubec;
