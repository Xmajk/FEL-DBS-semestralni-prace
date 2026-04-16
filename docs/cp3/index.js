const { Client } = require("pg");

const client = new Client({
  host: "slon.felk.cvut.cz",
  port: 5432,
  database: "hroudmi5",
  user: "hroudmi5",
  password: "hroudmi5_DBS26",
});

// --- helpers ---
function randInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}
function pick(arr) {
  return arr[randInt(0, arr.length - 1)];
}
function pad(n, len = 2) {
  return String(n).padStart(len, "0");
}
function genRodneCislo(id) {
  const year = randInt(60, 99);
  const month = pad(randInt(1, 12));
  const day = pad(randInt(1, 28));
  const suffix = pad(randInt(0, 999), 3);
  return `${year}${month}${day}/${suffix}${id % 10}`;
}
function genPhone() {
  return `+420 ${randInt(600, 799)} ${randInt(100, 999)} ${randInt(100, 999)}`;
}
function dateStr(d) {
  return d.toISOString().slice(0, 10);
}

// --- static data ---
const jmena = [
  "Jan", "Petr", "Martin", "Lukas", "Tomas", "Pavel", "Jakub", "David",
  "Filip", "Ondrej", "Michal", "Vojtech", "Adam", "Matej", "Daniel",
  "Eva", "Anna", "Katerina", "Lucie", "Tereza", "Petra", "Jana", "Marie",
  "Barbora", "Veronika", "Marketa", "Klara", "Hana", "Lenka", "Simona",
];
const prijmeni = [
  "Novak", "Svoboda", "Novotny", "Dvorak", "Cerny", "Prochazka", "Kucera",
  "Vesely", "Horak", "Nemec", "Marek", "Pospisil", "Hajek", "Jelinek",
  "Kral", "Ruzicka", "Benes", "Fiala", "Sedlacek", "Kolar", "Vlcek",
  "Kopecky", "Bartos", "Stanek", "Maly", "Blaha", "Zeman", "Urban",
  "Kratky", "Holub",
];
const ulice = [
  "Hlavni 1", "Husova 12", "Namesti Miru 3", "Karlova 45", "Sokolska 8",
  "Masarykova 22", "Palackeho 7", "Jirasekova 15", "Nerudova 30",
  "Skolni 5", "Na Prikope 10", "Vinohradska 88", "Dejvicka 14",
  "Seifertova 3", "Jecna 19",
];
const mesta = ["Praha", "Brno", "Ostrava", "Plzen", "Liberec", "Olomouc"];
const psc = ["110 00", "602 00", "702 00", "301 00", "460 01", "779 00"];
const agentury = [
  "ManpowerGroup", "Hays", "Grafton", "Randstad", "Adecco",
  "Robert Half", "PageGroup", "Kelly Services",
];
const typyZaznamu = ["prace", "sluzebni_cesta", "skoleni", "home_office"];
const roleNaProjektu = [
  "vyvojar", "tester", "analytik", "projektovy_manazer", "architekt",
  "scrum_master", "designer", "konzultant",
];

async function seed() {
  await client.connect();
  console.log("Connected.");

  // =========== Oddeleni (10) ===========
  const oddeleni = [
    [1, "ODD-01", "Vyvoj", "Praha - budova A", "Oddeleni vyvoje softwaru", null],
    [2, "ODD-02", "Testovani", "Praha - budova A", "Oddeleni QA", 1],
    [3, "ODD-03", "Infrastruktura", "Praha - budova B", "Sprava infrastruktury", 1],
    [4, "ODD-04", "HR", "Brno - budova C", "Lidske zdroje", null],
    [5, "ODD-05", "Finance", "Brno - budova C", "Financni oddeleni", null],
    [6, "ODD-06", "Marketing", "Ostrava - budova D", "Marketingove oddeleni", null],
    [7, "ODD-07", "Podpora", "Praha - budova B", "Zakaznicka podpora", 6],
    [8, "ODD-08", "Obchod", "Plzen - budova E", "Obchodni oddeleni", 6],
    [9, "ODD-09", "Analyza", "Praha - budova A", "Datova analyza", 1],
    [10, "ODD-10", "Vedeni", "Praha - budova A", "Vedeni spolecnosti", null],
  ];
  for (const r of oddeleni) {
    await client.query(
      `INSERT INTO "Oddeleni" VALUES ($1,$2,$3,$4,$5,$6)`,
      r
    );
  }
  console.log("Oddeleni: 10");

  // =========== Pozice (10) ===========
  const pozice = [
    [1, "Junior vyvojar", 2, "Zacinajici vyvojar"],
    [2, "Senior vyvojar", 5, "Zkuseny vyvojar"],
    [3, "Team leader", 6, "Vedouci tymu"],
    [4, "Tester", 3, "QA specialista"],
    [5, "Analytik", 4, "Business analytik"],
    [6, "Projektovy manazer", 7, "Rizeni projektu"],
    [7, "DevOps inzenyr", 5, "Sprava CI/CD a infrastruktury"],
    [8, "UX designer", 4, "Navrh uzivatelskeho rozhrani"],
    [9, "Databazovy specialista", 5, "Sprava databazi"],
    [10, "Reditel", 10, "Vedeni spolecnosti"],
  ];
  for (const r of pozice) {
    await client.query(`INSERT INTO "Pozice" VALUES ($1,$2,$3,$4)`, r);
  }
  console.log("Pozice: 10");

  // =========== Projekt (15) ===========
  const projekty = [
    [1, "ERP System", "2024-01-15", "2025-06-30", "Implementace ERP"],
    [2, "Mobilni aplikace", "2024-03-01", "2024-12-31", "iOS a Android aplikace"],
    [3, "Migrace do cloudu", "2024-06-01", "2025-03-31", "AWS migrace"],
    [4, "Datovy sklad", "2024-02-01", "2024-11-30", "BI a reporting"],
    [5, "Web portal", "2023-09-01", "2024-08-31", "Zakaznicky portal"],
    [6, "API Gateway", "2024-04-15", "2025-01-31", "Centralni API brana"],
    [7, "Security audit", "2024-07-01", "2024-09-30", "Bezpecnostni audit"],
    [8, "CI/CD Pipeline", "2024-05-01", "2024-10-31", "Automatizace deploymentu"],
    [9, "CRM System", "2024-08-01", "2025-08-31", "Rizeni vztahu se zakazniky"],
    [10, "Chatbot", "2024-10-01", null, "AI chatbot pro zakazniky"],
    [11, "Monitoring", "2024-01-01", "2024-06-30", "Systemovy monitoring"],
    [12, "GDPR Compliance", "2024-03-15", "2024-12-15", "Soulad s GDPR"],
    [13, "Intranet", "2024-09-01", "2025-04-30", "Firemni intranet"],
    [14, "ML Predikce", "2025-01-01", null, "Strojove uceni pro predikce"],
    [15, "Platebni brana", "2024-11-01", "2025-07-31", "Online platby"],
  ];
  for (const r of projekty) {
    await client.query(`INSERT INTO "Projekt" VALUES ($1,$2,$3,$4,$5)`, r);
  }
  console.log("Projekt: 15");

  // =========== Zamestnanec (50) ===========
  const zamIds = [];
  const zamOddeleni = [];
  for (let i = 1; i <= 50; i++) {
    const j = pick(jmena);
    const p = pick(prijmeni);
    const idOdd = randInt(1, 10);
    const idPoz = Math.random() < 0.9 ? randInt(1, 10) : null;
    const rok = randInt(2015, 2024);
    const mesic = pad(randInt(1, 12));
    const den = pad(randInt(1, 28));
    const datum = `${rok}-${mesic}-${den}`;
    const email = `${j.toLowerCase()}.${p.toLowerCase()}${i}@firma.cz`;

    zamIds.push(i);
    zamOddeleni.push(idOdd);
    await client.query(
      `INSERT INTO "Zamestnanec" VALUES ($1,$2,$3,$4,$5,$6,$7,$8)`,
      [i, genRodneCislo(i), email, j, p, datum, idOdd, idPoz]
    );
  }
  console.log("Zamestnanec: 50");

  // =========== Telefon (50) ===========
  for (let i = 1; i <= 50; i++) {
    await client.query(`INSERT INTO "Telefon" VALUES ($1,$2,$3)`, [
      i,
      genPhone(),
      zamIds[i - 1],
    ]);
  }
  console.log("Telefon: 50");

  // =========== Adresa (50) ===========
  for (let i = 1; i <= 50; i++) {
    const mi = randInt(0, mesta.length - 1);
    await client.query(`INSERT INTO "Adresa" VALUES ($1,$2,$3,$4,$5)`, [
      i,
      pick(ulice),
      mesta[mi],
      psc[mi],
      zamIds[i - 1],
    ]);
  }
  console.log("Adresa: 50");

  // =========== Manazer (10) ===========
  const manazerIds = zamIds.slice(0, 10);
  for (let i = 0; i < 10; i++) {
    await client.query(`INSERT INTO "Manazer" VALUES ($1,$2,$3,$4)`, [
      i + 1,
      randInt(1, 5),
      manazerIds[i],
      zamOddeleni[i],
    ]);
  }
  console.log("Manazer: 10");

  // =========== Externista (10) ===========
  const extIds = zamIds.slice(40, 50);
  for (let i = 0; i < 10; i++) {
    const konec = `2025-${pad(randInt(1, 12))}-${pad(randInt(1, 28))}`;
    await client.query(`INSERT INTO "Externista" VALUES ($1,$2,$3,$4)`, [
      i + 1,
      pick(agentury),
      konec,
      extIds[i],
    ]);
  }
  console.log("Externista: 10");

  // =========== ZaznamDochazky (35 000) ===========
  console.log("ZaznamDochazky: generating 35000 rows (batch insert)...");

  const BATCH = 1000;
  let dochId = 1;

  // Pre-generate unique (cas_prichodu, id_zamestnanec) pairs.
  // 50 employees x 700 working days (2022-01-03 .. 2024-11-28) = 35000
  const startDate = new Date("2022-01-03");
  const rows = [];

  for (let emp = 1; emp <= 50; emp++) {
    for (let d = 0; d < 700; d++) {
      const day = new Date(startDate);
      day.setDate(day.getDate() + d);
      // skip weekends
      const dow = day.getDay();
      if (dow === 0 || dow === 6) continue;
      if (rows.length >= 35000) break;

      const hh = randInt(6, 9);
      const mm = randInt(0, 59);
      const ss = randInt(0, 59);
      const prichod = `${dateStr(day)} ${pad(hh)}:${pad(mm)}:${pad(ss)}`;
      const odchodHH = hh + randInt(8, 10);
      const odchod =
        Math.random() < 0.95
          ? `${dateStr(day)} ${pad(odchodHH)}:${pad(randInt(0, 59))}:${pad(randInt(0, 59))}`
          : null;

      rows.push([dochId++, prichod, odchod, pick(typyZaznamu), emp]);
    }
    if (rows.length >= 35000) break;
  }

  // Pad if needed (weekends may have reduced count)
  let extraDay = 700;
  let extraEmp = 1;
  while (rows.length < 35000) {
    extraDay++;
    const day = new Date(startDate);
    day.setDate(day.getDate() + extraDay);
    if (day.getDay() === 0 || day.getDay() === 6) continue;

    const hh = randInt(6, 9);
    const mm = randInt(0, 59);
    const ss = randInt(0, 59);
    const prichod = `${dateStr(day)} ${pad(hh)}:${pad(mm)}:${pad(ss)}`;
    const odchodHH = hh + randInt(8, 10);
    const odchod = `${dateStr(day)} ${pad(odchodHH)}:${pad(randInt(0, 59))}:${pad(randInt(0, 59))}`;
    rows.push([dochId++, prichod, odchod, pick(typyZaznamu), extraEmp]);
    extraEmp = (extraEmp % 50) + 1;
  }

  // Batch insert
  for (let i = 0; i < rows.length; i += BATCH) {
    const batch = rows.slice(i, i + BATCH);
    const values = [];
    const params = [];
    let idx = 1;
    for (const row of batch) {
      values.push(`($${idx},$${idx + 1},$${idx + 2},$${idx + 3},$${idx + 4})`);
      params.push(...row);
      idx += 5;
    }
    await client.query(
      `INSERT INTO "ZaznamDochazky" ("id_dochazka","cas_prichodu","cas_odchodu","typ_zaznamu","id_zamestnanec") VALUES ${values.join(",")}`,
      params
    );
    if ((i / BATCH) % 5 === 0) {
      process.stdout.write(`  ${i + batch.length} / 35000\r`);
    }
  }
  console.log("\nZaznamDochazky: 35000");

  // =========== UcastniSe (40) ===========
  const ucastSet = new Set();
  let ucastCount = 0;
  while (ucastCount < 40) {
    const idZ = randInt(1, 50);
    const idP = randInt(1, 15);
    const key = `${idZ}-${idP}`;
    if (ucastSet.has(key)) continue;
    ucastSet.add(key);
    await client.query(`INSERT INTO "UcastniSe" VALUES ($1,$2,$3)`, [
      pick(roleNaProjektu),
      idZ,
      idP,
    ]);
    ucastCount++;
  }
  console.log("UcastniSe: 40");

  console.log("Done.");
  await client.end();
}

seed().catch((err) => {
  console.error(err);
  process.exit(1);
});
