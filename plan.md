# Plán: @Inheritance(JOINED) pro Manazer / Externista → Zamestnanec

## Zvolená strategie

JPA `@Inheritance(strategy = InheritanceType.JOINED)` + původní DDL beze změny.

Proč to funguje i bez úpravy DDL:
- `@PrimaryKeyJoinColumn(name = "id_zamestnanec")` říká Hibernate, ať pro JOIN používá
  sloupec `id_zamestnanec` (má UNIQUE constraint → chová se jako alternativní klíč).
- `id_manazer` / `id_externista` jsou `SERIAL` → mají DEFAULT (nextval). Pokud je JPA
  do INSERT nezahrne (`insertable = false`), PostgreSQL je doplní automaticky.
- Hibernate generuje SQL dotazy čistě podle JPA mapování, na skutečný DB PK se neptá.

Výsledná hierarchie:
```
Osoba (@MappedSuperclass)        jmeno, prijmeni, email, getCeleJmeno()
  └── Zamestnanec (@Entity)      id_zamestnanec (PK SERIAL)
         ├── Manazer (@Entity)   @PrimaryKeyJoinColumn("id_zamestnanec"), id_manazer read-only
         └── Externista (@Entity) @PrimaryKeyJoinColumn("id_zamestnanec"), id_externista read-only
```

---

## 1. DDL — beze změny

`docs/cp3/DDL.sql` zůstává jako je. Soubor `docs/cp4/newDDL.sql` není potřeba.

---

## 2. `Zamestnanec.java`

Přidat jedinou anotaci — JOINED nevyžaduje diskriminační sloupec:

```java
@Inheritance(strategy = InheritanceType.JOINED)
```

---

## 3. `Manazer.java` — přepis

```java
@Entity
@Table(name = "Manazer")
@PrimaryKeyJoinColumn(name = "id_zamestnanec")
@Getter @Setter @ToString(callSuper = true)
public class Manazer extends Zamestnanec {

    // DB generuje hodnotu z SERIAL sekvence; JPA nesmí sloupec zapisovat
    @Column(name = "id_manazer", insertable = false, updatable = false)
    private Integer idManazer;

    @Column(name = "uroven_pravomoci", nullable = false)
    private Integer urovenPravomoci;

    // id_oddeleni zděděno z Zamestnanec přes getOddeleni()
}
```

Odstraněna pole: `zamestnanec` (Manazer IS Zamestnanec), `oddeleni` (zděděno).

---

## 4. `Externista.java` — přepis

```java
@Entity
@Table(name = "Externista")
@PrimaryKeyJoinColumn(name = "id_zamestnanec")
@Getter @Setter @ToString(callSuper = true)
public class Externista extends Zamestnanec {

    // DB generuje hodnotu z SERIAL sekvence; JPA nesmí sloupec zapisovat
    @Column(name = "id_externista", insertable = false, updatable = false)
    private Integer idExternista;

    @Column(name = "nazev_agentury", nullable = false)
    private String nazevAgentury;

    @Column(name = "konec_smlouvy", nullable = false)
    private LocalDate konecSmlouvy;

    // zamestnanec zrušeno — Externista IS Zamestnanec
}
```

---

## 5. Repozitáře — beze změny typů

`ManazerRepository extends JpaRepository<Manazer, Integer>` — PK je stále Integer
(teď `id_zamestnanec`, ne `id_manazer`, ale typ se nemění).

`findByOddeleni_IdOddeleni` funguje — `oddeleni` je zděděno z `Zamestnanec`.

`deleteByOddeleni` JPQL bulk delete (`DELETE FROM Manazer WHERE ...`) s JOINED odstraní
pouze řádek z tabulky `Manazer`; `Zamestnanec` řádek přežije → degradace na řadového zaměstnance.

---

## 6. `ManazerView.java`

`m.getZamestnanec()` → přímé `m.getCeleJmeno()`, `m.getOddeleni()` (zděděné metody):

```java
public record ManazerView(
        Integer idManazer,
        Integer urovenPravomoci,
        Integer idZamestnanec,
        String jmenoZamestnance,
        Integer idOddeleni,
        String nazevOddeleni
) {
    public static ManazerView from(Manazer m) {
        return new ManazerView(
                m.getIdManazer(),
                m.getUrovenPravomoci(),
                m.getIdZamestnanec(),
                m.getCeleJmeno(),
                m.getOddeleni().getIdOddeleni(),
                m.getOddeleni().getNazev()
        );
    }
}
```

---

## 7. `ManazerService.vymenManazera`

Klíčový problém: `new Manazer()` + `em.persist()` = INSERT do obou tabulek
(`Zamestnanec` + `Manazer`) → vytvořil by nového zaměstnance, ne povýšil stávajícího.

Řešení: native INSERT pouze do tabulky `Manazer` pro existující `id_zamestnanec`:

```java
manazerRepository.deleteByOddeleni(oddeleni.getIdOddeleni());
em.flush(); // nutné kvůli unique_oddeleni

em.createNativeQuery("""
    INSERT INTO "Manazer" (id_zamestnanec, uroven_pravomoci)
    VALUES (:idZ, :uroven)
    """)
  .setParameter("idZ",    novy.getIdZamestnanec())
  .setParameter("uroven", urovenPravomoci)
  .executeUpdate();
em.flush();

Manazer novyManazer = em.find(Manazer.class, novy.getIdZamestnanec());
return ManazerView.from(novyManazer);
```

Native INSERT je zde odůvodnitelný: JPQL INSERT neexistuje a `em.persist(new Manazer())`
by insertoval nového zaměstnance, ne povýšil stávajícího.

---

## 8. Polymorfní dotazy — vedlejší efekt

`ZamestnanecRepository.findAll()` s JOINED vrátí instance správných typů:
manažeři jako `Manazer`, externisté jako `Externista`, ostatní jako `Zamestnanec`.
`ZamestnanecView.from(z)` to zvládne — pracuje jen s atributy z `Osoba`/`Zamestnanec`.

Pro dotaz pouze na řadové zaměstnance (bez podtříd):
```java
@Query("SELECT z FROM Zamestnanec z WHERE TYPE(z) = Zamestnanec")
```

---

## 9. Pořadí implementace

1. `Zamestnanec.java` — přidat `@Inheritance(JOINED)`
2. `Manazer.java` — přepsat jako `extends Zamestnanec`
3. `Externista.java` — přepsat jako `extends Zamestnanec`
4. `ManazerView.java` — opravit factory `from()`
5. `ManazerService.vymenManazera` — nahradit `new Manazer()` native INSERTem
6. Kompilace + test (`./start.sh`, menu volby 5, 9, 10)
