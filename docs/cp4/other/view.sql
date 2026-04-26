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

SELECT * FROM v_kontaktni_seznam_manazeru