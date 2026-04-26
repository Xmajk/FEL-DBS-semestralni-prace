package cz.fel.hroudmi5;

import cz.fel.hroudmi5.dto.*;
import cz.fel.hroudmi5.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private final ZamestnanecService zamestnanecService;
    private final DochazkaService dochazkaService;
    private final ProjektService projektService;
    private final ManazerService manazerService;
    private final OddeleniService oddeleniService;

    private final Scanner sc = new Scanner(System.in);

    public Application(ZamestnanecService zamestnanecService,
                       DochazkaService dochazkaService,
                       ProjektService projektService,
                       ManazerService manazerService,
                       OddeleniService oddeleniService) {
        this.zamestnanecService = zamestnanecService;
        this.dochazkaService = dochazkaService;
        this.projektService = projektService;
        this.manazerService = manazerService;
        this.oddeleniService = oddeleniService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        boolean running = true;
        while (running) {
            printMenu();
            String volba = sc.nextLine().trim();
            System.out.println();
            try {
                switch (volba) {
                    case "1" -> pridejZamestnance();
                    case "2" -> prevedNaOddeleni();
                    case "3" -> evidenceDochazky();
                    case "4" -> priradNaProjekt();
                    case "5" -> vymenManazera();
                    case "6" -> zobrazZamestnance();
                    case "7" -> hledejDleJmena();
                    case "8" -> zobrazProjekty();
                    case "9" -> zobrazManazery();
                    case "10" -> zobrazKontaktyManazeru();
                    case "11" -> smazZamestnance();
                    case "12" -> zmenRoliNaProjektu();
                    case "13" -> odeberZProjektu();
                    case "14" -> upravKontakt();
                    case "0" -> { running = false; print("Konec."); }
                    default  -> print("Neznámá volba.");
                }
            } catch (Exception e) {
                print("Chyba: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void pridejZamestnance() {
        zobrazOddeleni();
        NovyZamestnanecDto dto = new NovyZamestnanecDto();
        dto.setJmeno(prompt("Jméno: "));
        dto.setPrijmeni(prompt("Příjmení: "));
        dto.setEmail(prompt("E-mail: "));
        dto.setRodneCislo(prompt("Rodné číslo (xxxxxx/xxx): "));
        dto.setIdOddeleni(promptInt("ID oddělení: "));
        String pozice = prompt("ID pozice (Enter = bez pozice): ");
        if (!pozice.isBlank()) dto.setIdPozice(Integer.parseInt(pozice));
        dto.setDatumNastupu(LocalDate.now());

        ZamestnanecView v = zamestnanecService.vytvor(dto);
        print("Vytvořeno: " + formatZamestnanec(v));
    }

    private void prevedNaOddeleni() {
        zobrazZamestnance();
        int idZ = promptInt("ID zaměstnance: ");
        zobrazOddeleni();
        int idO = promptInt("ID cílového oddělení: ");

        ZamestnanecView v = zamestnanecService.prevedNaOddeleni(idZ, idO);
        print("Převedeno: " + formatZamestnanec(v));
    }

    private void evidenceDochazky() {
        zobrazZamestnance();
        int idZ = promptInt("ID zaměstnance: ");
        String op = prompt("Příchod (p) / odchod (o): ").toLowerCase();

        if (op.startsWith("p")) {
            String typ = prompt("Typ (kancelar/homeoffice/sluzebni): ");
            if (typ.isBlank()) typ = "kancelar";
            DochazkaView d = dochazkaService.prichod(idZ, typ);
            print("Příchod: " + formatDochazka(d));
        } else if (op.startsWith("o")) {
            DochazkaView d = dochazkaService.odchod(idZ);
            print("Odchod: " + formatDochazka(d));
        } else {
            print("Neznámá operace.");
        }
    }

    private void priradNaProjekt() {
        zobrazZamestnance();
        int idZ = promptInt("ID zaměstnance: ");
        zobrazProjekty();
        int idP = promptInt("ID projektu: ");
        String role = prompt("Role (Enter = clen): ");
        if (role.isBlank()) role = "clen";

        PriraditNaProjektDto dto = new PriraditNaProjektDto();
        dto.setIdZamestnanec(idZ);
        dto.setIdProjekt(idP);
        dto.setRole(role);

        UcastView u = projektService.priradZamestnance(dto);
        print("Účast: zaměstnanec " + u.idZamestnanec()
                + " -> projekt " + u.idProjekt() + " [" + u.role() + "]");
    }

    private void vymenManazera() {
        zobrazManazery();
        int idO = promptInt("ID oddělení: ");
        zobrazZamestnance();
        int idZ = promptInt("ID nového manažera (musí být zaměstnanec daného oddělení): ");
        int uroven = promptInt("Úroveň pravomoci (1-5): ");

        VymenaManazeraDto dto = new VymenaManazeraDto();
        dto.setIdOddeleni(idO);
        dto.setIdNovehoZamestnance(idZ);
        dto.setUrovenPravomoci(uroven);

        ManazerView m = manazerService.vymenManazera(dto);
        print("Manažer nastaven: " + formatManazer(m));
    }

    private void zobrazZamestnance() {
        List<ZamestnanecView> list = zamestnanecService.hledejDleJmena(null);
        print(String.format("%-4s %-20s %-30s %-20s", "ID", "Jméno", "E-mail", "Oddělení"));
        list.forEach(z -> print(String.format("%-4d %-20s %-30s %-20s",
                z.idZamestnanec(),
                z.jmeno() + " " + z.prijmeni(),
                z.email(),
                z.nazevOddeleni() != null ? z.nazevOddeleni() : "")));
    }

    private void zobrazOddeleni() {
        List<OddeleniView> list = oddeleniService.vsechna();
        print(String.format("%-4s %-8s %-30s %-20s", "ID", "Číslo", "Název", "Lokace"));
        list.forEach(o -> print(String.format("%-4d %-8s %-30s %-20s",
                o.idOddeleni(), o.cisloOddeleni(), o.nazev(), o.lokace())));
    }

    private void zobrazProjekty() {
        List<ProjektView> list = projektService.vsechny();
        print(String.format("%-4s %-30s %-12s %-12s", "ID", "Název", "Zahájení", "Ukončení"));
        list.forEach(p -> print(String.format("%-4d %-30s %-12s %-12s",
                p.idProjekt(), p.nazev(), p.terminZahajeni(),
                p.terminUkonceni() != null ? p.terminUkonceni() : "-")));
    }

    private void zobrazManazery() {
        List<ManazerView> list = manazerService.vsichni();
        print(String.format("%-4s %-20s %-25s %-5s", "ID", "Manažer", "Oddělení", "Úrov."));
        list.forEach(m -> print(String.format("%-4d %-20s %-25s %-5d",
                m.idManazer(), m.jmenoZamestnance(), m.nazevOddeleni(), m.urovenPravomoci())));
    }

    private void zobrazKontaktyManazeru() {
        String vzor = prompt("Filtr dle názvu oddělení (Enter = vše): ");
        List<KontaktManazeraView> list = manazerService.kontakty(vzor);
        if (list.isEmpty()) {
            print("Žádné výsledky.");
            return;
        }
        print(String.format("%-25s %-25s %-30s %-15s", "Oddělení", "Manažer", "E-mail", "Telefon"));
        list.forEach(k -> print(String.format("%-25s %-25s %-30s %-15s",
                k.oddeleni(),
                k.manazer(),
                k.email() != null ? k.email() : "-",
                k.telefon() != null ? k.telefon() : "-")));
    }

    private void zmenRoliNaProjektu() {
        zobrazZamestnance();
        int idZ = promptInt("ID zaměstnance: ");
        zobrazProjekty();
        int idP = promptInt("ID projektu: ");
        String novaRole = prompt("Nová role: ");
        if (novaRole.isBlank()) {
            print("Role nesmí být prázdná.");
            return;
        }
        UcastView u = projektService.zmenRoli(idZ, idP, novaRole);
        print("Role změněna: zaměstnanec " + u.idZamestnanec()
                + " -> projekt " + u.idProjekt() + " [" + u.role() + "]");
    }

    private void odeberZProjektu() {
        zobrazProjekty();
        int idP = promptInt("ID projektu: ");
        zobrazZamestnance();
        int idZ = promptInt("ID zaměstnance, který má z projektu odejít: ");
        String potvrzeni = prompt("Opravdu odebrat (a/n)? ").toLowerCase();
        if (!potvrzeni.startsWith("a")) {
            print("Zrušeno.");
            return;
        }
        projektService.odeberZamestnance(idZ, idP);
        print("Účast smazána: zaměstnanec " + idZ + " už nepatří na projekt " + idP + ".");
    }

    private void upravKontakt() {
        zobrazZamestnance();
        int idZ = promptInt("ID zaměstnance: ");
        print("Prázdná hodnota = ponechat původní.");
        String jmeno = prompt("Nové jméno: ");
        String prijmeni = prompt("Nové příjmení: ");
        String email = prompt("Nový e-mail: ");
        ZamestnanecView v = zamestnanecService.upravKontakt(idZ, jmeno, prijmeni, email);
        print("Aktualizováno: " + formatZamestnanec(v));
    }

    private void smazZamestnance() {
        zobrazZamestnance();
        int idZ = promptInt("ID zaměstnance ke smazání: ");
        String potvrzeni = prompt("Opravdu smazat (a/n)? ").toLowerCase();
        if (!potvrzeni.startsWith("a")) {
            print("Zrušeno.");
            return;
        }
        zamestnanecService.smaz(idZ);
        print("Zaměstnanec " + idZ + " smazán (kaskádově i jeho telefony, adresy, docházka a účasti na projektech).");
    }

    private void hledejDleJmena() {
        String vzor = prompt("Hledat (jméno/příjmení): ");
        List<ZamestnanecView> list = zamestnanecService.hledejDleJmena(vzor);
        if (list.isEmpty()) {
            print("Žádné výsledky.");
        } else {
            list.forEach(z -> print(formatZamestnanec(z)));
        }
    }

    private String formatZamestnanec(ZamestnanecView v) {
        return String.format("[%d] %s %s  e-mail:%s  oddělení:%s  pozice:%s",
                v.idZamestnanec(), v.jmeno(), v.prijmeni(), v.email(),
                v.nazevOddeleni() != null ? v.nazevOddeleni() : "-",
                v.nazevPozice() != null ? v.nazevPozice() : "-");
    }

    private String formatDochazka(DochazkaView d) {
        return String.format("[%d] příchod:%s  odchod:%s  typ:%s",
                d.idDochazka(), d.casPrichodu(), d.casOdchodu() != null ? d.casOdchodu() : "-", d.typZaznamu());
    }

    private String formatManazer(ManazerView m) {
        return String.format("[%d] %s -> oddělení:%s  úroveň:%d",
                m.idManazer(), m.jmenoZamestnance(), m.nazevOddeleni(), m.urovenPravomoci());
    }

    private void printMenu() {
        print("");
        print("  1. Přidat zaměstnance");
        print("  2. Převést zaměstnance na oddělení");
        print("  3. Evidence docházky");
        print("  4. Přiřadit na projekt");
        print("  5. Vyměnit manažera");
        print("  6. Seznam zaměstnanců");
        print("  7. Hledat zaměstnance");
        print("  8. Seznam projektů");
        print("  9. Seznam manažerů");
        print(" 10. Kontakty manažerů");
        print(" 11. Smazat zaměstnance");
        print(" 12. Změnit roli na projektu");
        print(" 13. Odebrat zaměstnance z projektu");
        print(" 14. Upravit kontakt zaměstnance");
        print("  0. Konec");
        System.out.print("Volba: ");
    }

    private String prompt(String msg) {
        System.out.print(msg);
        return sc.nextLine().trim();
    }

    private int promptInt(String msg) {
        return Integer.parseInt(prompt(msg));
    }

    private void print(String msg) {
        System.out.println(msg);
    }
}
