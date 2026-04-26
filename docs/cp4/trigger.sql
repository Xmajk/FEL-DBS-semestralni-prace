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

CREATE TRIGGER trg_kontrola_dochazky BEFORE INSERT ON "ZaznamDochazky"
FOR EACH ROW EXECUTE FUNCTION kontrola_nedokoncene_dochazky();