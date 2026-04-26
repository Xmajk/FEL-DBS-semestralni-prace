EXPLAIN ANALYZE
SELECT * FROM "ZaznamDochazky"
WHERE "id_zamestnanec" = 5;

CREATE INDEX idx_dochazka_zamestnanec
ON "ZaznamDochazky"("id_zamestnanec");

EXPLAIN ANALYZE
SELECT * FROM "ZaznamDochazky"
WHERE "id_zamestnanec" = 5;