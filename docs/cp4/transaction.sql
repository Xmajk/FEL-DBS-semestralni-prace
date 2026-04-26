BEGIN TRANSACTION ISOLATION LEVEL SERIALIZABLE;

DELETE FROM "Manazer"
WHERE "id_oddeleni" = 2;

INSERT INTO "Manazer" ("uroven_pravomoci", "id_zamestnanec", "id_oddeleni")
VALUES (4, 15, 2);
COMMIT;