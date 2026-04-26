# Plán oprav Java aplikace (CP-5)

## Kořenová příčina výjimek

`spring.jpa.open-in-view=false` — Hibernate session se uzavře hned po návratu ze servisní
metody. Servisní metody vracely entity (`Zamestnanec`, `Manazer`, …) s neinicializovanými
lazy proxy asociacemi. Kontrolery pak volaly `View::from(entity)` **mimo transakci**, čímž
přístup na proxy (např. `z.getOddeleni().getNazev()`) vyhodil `LazyInitializationException`.

## Řešení

Přesunout mapování entity → DTO **dovnitř** `@Transactional` servisních metod.
Session je v tu chvíli stále otevřená, lazy loading proběhne bez problémů.

## Přehled změn

### 1. Servisní vrstva — návratové typy entity → DTO

| Třída | Metoda | Před | Po |
|-------|--------|------|----|
| `ZamestnanecService` | `najdi` | `Zamestnanec` | `ZamestnanecView` |
| `ZamestnanecService` | `vytvor` | `Zamestnanec` | `ZamestnanecView` |
| `ZamestnanecService` | `prevedNaOddeleni` | `Zamestnanec` | `ZamestnanecView` |
| `ZamestnanecService` | `hledejDleJmena` | `List<Zamestnanec>` | `List<ZamestnanecView>` |
| `ZamestnanecService` | `nastoupeniPo` | `List<Zamestnanec>` | `List<ZamestnanecView>` |
| `DochazkaService` | `proZamestnance` | `List<ZaznamDochazky>` | `List<DochazkaView>` |
| `DochazkaService` | `prichod` | `ZaznamDochazky` | `DochazkaView` |
| `DochazkaService` | `odchod` | `ZaznamDochazky` | `DochazkaView` |
| `ProjektService` | `vsechny` | `List<Projekt>` | `List<ProjektView>` |
| `ProjektService` | `priradZamestnance` | `UcastniSe` | `UcastView` |
| `ProjektService` | `clenoveProjektu` | `List<UcastniSe>` | `List<UcastView>` |
| `ManazerService` | `vsichni` | `List<Manazer>` | `List<ManazerView>` |
| `ManazerService` | `vymenManazera` | `Manazer` | `ManazerView` |
| `OddeleniService` | `vsechna` | `List<Oddeleni>` | `List<OddeleniView>` |

### 2. Kontrolery — odstranit duplicitní mapování

Metody jako `service.vsichni().stream().map(View::from).toList()` → `service.vsichni()`.

### 3. Přidat endpoint `/api/pozice` (nové soubory)

Formulář pro vytvoření zaměstnance plnil select pozic prázdným `[]` (hack přes `/api/projekty`).
Přidat:
- `PoziceView.java` (DTO record)
- `PoziceService.java`
- `PoziceController.java` → `GET /api/pozice`

### 4. Opravit `zamestnanci.js`

Načítat pozice z `/api/pozice` místo `fetch('/api/projekty').then(() => [])`.

## Požadavky CP-5 (kontrolní seznam)

- [x] **Datový model** — pokrývá celou databázi (Oddeleni, Pozice, Projekt, Zamestnanec,
      Telefon, Adresa, Manazer, Externista, ZaznamDochazky, UcastniSe)
- [x] **Many-to-Many** — `UcastniSe` (`Zamestnanec` ↔ `Projekt`) s atributem `role`,
      composite PK přes `@EmbeddedId`
- [x] **Dědičnost** — `Zamestnanec extends Osoba` (`@MappedSuperclass`)
- [x] **DAO vrstva** — `ZamestnanecDao` s `EntityManager` a parametrizovanými JPQL/nativními dotazy;
      Spring Data JPA repositories pro všechny entity
- [x] **Servisní vrstva — 5 užití:**
  1. `ZamestnanecService.vytvor()` — zápis nového zaměstnance
  2. `ZamestnanecService.prevedNaOddeleni()` — UPDATE oddělení zaměstnance
  3. `DochazkaService.prichod()` + `odchod()` — zápis příchodu/odchodu
  4. `ProjektService.priradZamestnance()` — zápis do M:N vazby
  5. `ManazerService.vymenManazera()` — transakce z CP-4 (SERIALIZABLE, DELETE + INSERT)
