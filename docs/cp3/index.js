const { Client } = require("pg");

const client = new Client({
  host: "slon.felk.cvut.cz",
  port: 5432,
  database: process.env["DB_NAME"],
  user: process.env["DB_USER"],
  password: process.env["DB_PASS"],
});

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

const jmena = [
  "Jan", "Petr", "Martin", "Lukas", "Tomas", "Pavel", "Jakub", "David",
  "Filip", "Ondrej", "Michal", "Vojtech", "Adam", "Matej", "Daniel",
  "Eva", "Anna", "Katerina", "Lucie", "Tereza", "Petra", "Jana",
  "Marie", "Barbora", "Veronika", "Marketa", "Klara", "Hana", "Lenka", "Simona",
];
const prijmeni = [
  "Novak", "Svoboda", "Novotny", "Dvorak", "Cerny", "Prochazka", "Kucera",
  "Vesely", "Horak", "Nemec", "Marek", "Pospisil", "Hajek", "Jelinek",
  "Kral", "Ruzicka", "Benes", "Fiala", "Sedlacek", "Kolar", "Vlcek",
  "Kopecky", "Bartos", "Stanek", "Maly", "Blaha", "Zeman", "Urban", "Kratky", "Holub",
];
const ulice = [
  "Hlavni 1", "Husova 12", "Namesti Miru 3", "Karlova 45", "Sokolska 8",
  "Masarykova 22", "Palackeho 7", "Jirasekova 15", "Nerudova 30", "Skolni 5",
  "Na Prikope 10", "Vinohradska 88", "Dejvicka 14", "Seifertova 3", "Jecna 19",
];
const mesta = ["Praha", "Brno", "Ostrava", "Plzen", "Liberec", "Olomouc"];
const psc   = ["110 00", "602 00", "702 00", "301 00", "460 01", "779 00"];
const agentury = [
  "ManpowerGroup", "Hays", "Grafton", "Randstad",
  "Adecco", "Robert Half", "PageGroup", "Kelly Services",
];
const typyZaznamu   = ["prace", "sluzebni_cesta", "skoleni", "home_office"];
const roleNaProjektu = [
  "vyvojar", "tester", "analytik", "projektovy_manazer",
  "architekt", "scrum_master", "designer", "konzultant",
];

async function seed() {
  await client.connect();
  console.log("Connected.");

  // =========== Oddeleni (10) ===========
  // Definice ve správném pořadí – rodiče vždy před dětmi.
  // parent je cislo_oddeleni rodiče (string), null = žádný nadřízený.
  const oddeleniDef = [
    { cislo: "ODD-01", nazev: "Vyvoj",          lokace: "Praha - budova A", popis: "Oddeleni vyvoje softwaru", parent: null     },
    { cislo: "ODD-04", nazev: "HR",              lokace: "Brno - budova C",  popis: "Lidske zdroje",            parent: null     },
    { cislo: "ODD-05", nazev: "Finance",         lokace: "Brno - budova C",  popis: "Financni oddeleni",        parent: null     },
    { cislo: "ODD-06", nazev: "Marketing",       lokace: "Ostrava - budova D", popis: "Marketingove oddeleni",  parent: null     },
    { cislo: "ODD-10", nazev: "Vedeni",          lokace: "Praha - budova A", popis: "Vedeni spolecnosti",       parent: null     },
    { cislo: "ODD-02", nazev: "Testovani",       lokace: "Praha - budova A", popis: "Oddeleni QA",              parent: "ODD-01" },
    { cislo: "ODD-03", nazev: "Infrastruktura",  lokace: "Praha - budova B", popis: "Sprava infrastruktury",    parent: "ODD-01" },
    { cislo: "ODD-09", nazev: "Analyza",         lokace: "Praha - budova A", popis: "Datova analyza",           parent: "ODD-01" },
    { cislo: "ODD-07", nazev: "Podpora",         lokace: "Praha - budova B", popis: "Zakaznicka podpora",       parent: "ODD-06" },
    { cislo: "ODD-08", nazev: "Obchod",          lokace: "Plzen - budova E", popis: "Obchodni oddeleni",        parent: "ODD-06" },
  ];

  const cisloToId = {};
  for (const def of oddeleniDef) {
    const parentId = def.parent ? cisloToId[def.parent] : null;
    const res = await client.query(
      `INSERT INTO "Oddeleni" ("cislo_oddeleni","nazev","lokace","popis","id_nadrizene_oddeleni")
       VALUES ($1,$2,$3,$4,$5) RETURNING "id_oddeleni"`,
      [def.cislo, def.nazev, def.lokace, def.popis, parentId]
    );
    cisloToId[def.cislo] = res.rows[0].id_oddeleni;
  }
  const oddeleniIds = Object.values(cisloToId);
  console.log("Oddeleni: 10");

  // =========== Pozice (10) ===========
  const poziceData = [
    ["Junior vyvojar",        2,  "Zacinajici vyvojar"],
    ["Senior vyvojar",        5,  "Zkuseny vyvojar"],
    ["Team leader",           6,  "Vedouci tymu"],
    ["Tester",                3,  "QA specialista"],
    ["Analytik",              4,  "Business analytik"],
    ["Projektovy manazer",    7,  "Rizeni projektu"],
    ["DevOps inzenyr",        5,  "Sprava CI/CD a infrastruktury"],
    ["UX designer",           4,  "Navrh uzivatelskeho rozhrani"],
    ["Databazovy specialista",5,  "Sprava databazi"],
    ["Reditel",               10, "Vedeni spolecnosti"],
  ];
  const poziceIds = [];
  for (const row of poziceData) {
    const res = await client.query(
      `INSERT INTO "Pozice" ("nazev","uroven","popis") VALUES ($1,$2,$3) RETURNING "id_pozice"`,
      row
    );
    poziceIds.push(res.rows[0].id_pozice);
  }
  console.log("Pozice: 10");

  // =========== Projekt (15) ===========
  const projektyData = [
    ["ERP System",       "2024-01-15", "2025-06-30", "Implementace ERP"],
    ["Mobilni aplikace", "2024-03-01", "2024-12-31", "iOS a Android aplikace"],
    ["Migrace do cloudu","2024-06-01", "2025-03-31", "AWS migrace"],
    ["Datovy sklad",     "2024-02-01", "2024-11-30", "BI a reporting"],
    ["Web portal",       "2023-09-01", "2024-08-31", "Zakaznicky portal"],
    ["API Gateway",      "2024-04-15", "2025-01-31", "Centralni API brana"],
    ["Security audit",   "2024-07-01", "2024-09-30", "Bezpecnostni audit"],
    ["CI/CD Pipeline",   "2024-05-01", "2024-10-31", "Automatizace deploymentu"],
    ["CRM System",       "2024-08-01", "2025-08-31", "Rizeni vztahu se zakazniky"],
    ["Chatbot",          "2024-10-01", null,          "AI chatbot pro zakazniky"],
    ["Monitoring",       "2024-01-01", "2024-06-30", "Systemovy monitoring"],
    ["GDPR Compliance",  "2024-03-15", "2024-12-15", "Soulad s GDPR"],
    ["Intranet",         "2024-09-01", "2025-04-30", "Firemni intranet"],
    ["ML Predikce",      "2025-01-01", null,          "Strojove uceni pro predikce"],
    ["Platebni brana",   "2024-11-01", "2025-07-31", "Online platby"],
  ];
  const projektyIds = [];
  for (const row of projektyData) {
    const res = await client.query(
      `INSERT INTO "Projekt" ("nazev","termin_zahajeni","termin_ukonceni","popis") VALUES ($1,$2,$3,$4) RETURNING "id_projekt"`,
      row
    );
    projektyIds.push(res.rows[0].id_projekt);
  }
  console.log("Projekt: 15");

  // =========== Zamestnanec (50) ===========
  // Prvních 10 zaměstnanců dostane každý jiné oddělení (1:1 s oddeleniIds),
  // aby FK v Manazer mohl odkazovat na (id_zamestnanec, id_oddeleni).
  const zamIds    = [];
  const zamOddIds = [];

  for (let i = 0; i < 50; i++) {
    const j     = pick(jmena);
    const p     = pick(prijmeni);
    const idOdd = i < 10 ? oddeleniIds[i] : pick(oddeleniIds);
    const idPoz = Math.random() < 0.9 ? pick(poziceIds) : null;
    const rok   = randInt(2015, 2024);
    const datum = `${rok}-${pad(randInt(1, 12))}-${pad(randInt(1, 28))}`;
    const email = `${j.toLowerCase()}.${p.toLowerCase()}${i + 1}@firma.cz`;

    const res = await client.query(
      `INSERT INTO "Zamestnanec" ("rodne_cislo","email","jmeno","prijmeni","datum_nastupu","id_oddeleni","id_pozice")
       VALUES ($1,$2,$3,$4,$5,$6,$7) RETURNING "id_zamestnanec"`,
      [genRodneCislo(i + 1), email, j, p, datum, idOdd, idPoz]
    );
    zamIds.push(res.rows[0].id_zamestnanec);
    zamOddIds.push(idOdd);
  }
  console.log("Zamestnanec: 50");

  // =========== Telefon (50) ===========
  for (const zamId of zamIds) {
    await client.query(
      `INSERT INTO "Telefon" ("telefon","id_zamestnanec") VALUES ($1,$2)`,
      [genPhone(), zamId]
    );
  }
  console.log("Telefon: 50");

  // =========== Adresa (50) ===========
  for (const zamId of zamIds) {
    const mi = randInt(0, mesta.length - 1);
    await client.query(
      `INSERT INTO "Adresa" ("ulice","mesto","psc","id_zamestnanec") VALUES ($1,$2,$3,$4)`,
      [pick(ulice), mesta[mi], psc[mi], zamId]
    );
  }
  console.log("Adresa: 50");

  // =========== Manazer (10) ===========
  // Prvních 10 zaměstnanců je každý v jiném oddělení (garantováno výše),
  // takže (zamIds[i], zamOddIds[i]) je platná dvojice z Zamestnanec.
  for (let i = 0; i < 10; i++) {
    await client.query(
      `INSERT INTO "Manazer" ("uroven_pravomoci","id_zamestnanec","id_oddeleni") VALUES ($1,$2,$3)`,
      [randInt(1, 5), zamIds[i], zamOddIds[i]]
    );
  }
  console.log("Manazer: 10");

  // =========== Externista (10) ===========
  // Zaměstnanci 40-49 nejsou manažeři (manažeři jsou 0-9).
  for (let i = 40; i < 50; i++) {
    const konec = `2025-${pad(randInt(1, 12))}-${pad(randInt(1, 28))}`;
    await client.query(
      `INSERT INTO "Externista" ("nazev_agentury","konec_smlouvy","id_zamestnanec") VALUES ($1,$2,$3)`,
      [pick(agentury), konec, zamIds[i]]
    );
  }
  console.log("Externista: 10");

  // =========== ZaznamDochazky (35 000) ===========
  console.log("ZaznamDochazky: generating 35000 rows (batch insert)...");

  const BATCH     = 1000;
  const startDate = new Date("2022-01-03");
  const rows      = [];

  for (let emp = 0; emp < zamIds.length; emp++) {
    for (let d = 0; d < 700; d++) {
      if (rows.length >= 35000) break;
      const day = new Date(startDate);
      day.setDate(day.getDate() + d);
      const dow = day.getDay();
      if (dow === 0 || dow === 6) continue;

      const hh      = randInt(6, 9);
      const prichod = `${dateStr(day)} ${pad(hh)}:${pad(randInt(0, 59))}:${pad(randInt(0, 59))}`;
      const odchod  = `${dateStr(day)} ${pad(hh + randInt(8, 10))}:${pad(randInt(0, 59))}:${pad(randInt(0, 59))}`;
      rows.push([prichod, odchod, pick(typyZaznamu), zamIds[emp]]);
    }
    if (rows.length >= 35000) break;
  }

  // Doplnění do 35000 pokud víkendy snížily počet
  let extraDay = 700;
  let extraIdx = 0;
  while (rows.length < 35000) {
    extraDay++;
    const day = new Date(startDate);
    day.setDate(day.getDate() + extraDay);
    if (day.getDay() === 0 || day.getDay() === 6) continue;
    const hh      = randInt(6, 9);
    const prichod = `${dateStr(day)} ${pad(hh)}:${pad(randInt(0, 59))}:${pad(randInt(0, 59))}`;
    const odchod  = `${dateStr(day)} ${pad(hh + randInt(8, 10))}:${pad(randInt(0, 59))}:${pad(randInt(0, 59))}`;
    rows.push([prichod, odchod, pick(typyZaznamu), zamIds[extraIdx % zamIds.length]]);
    extraIdx++;
  }

  for (let i = 0; i < rows.length; i += BATCH) {
    const batch  = rows.slice(i, i + BATCH);
    const vals   = [];
    const params = [];
    let idx = 1;
    for (const row of batch) {
      vals.push(`($${idx},$${idx + 1},$${idx + 2},$${idx + 3})`);
      params.push(...row);
      idx += 4;
    }
    await client.query(
      `INSERT INTO "ZaznamDochazky" ("cas_prichodu","cas_odchodu","typ_zaznamu","id_zamestnanec") VALUES ${vals.join(",")}`,
      params
    );
    if ((i / BATCH) % 5 === 0) process.stdout.write(`  ${i + batch.length} / 35000\r`);
  }
  console.log("\nZaznamDochazky: 35000");

  // =========== UcastniSe (40) ===========
  const ucastSet = new Set();
  let ucastCount = 0;
  while (ucastCount < 40) {
    const idZ = pick(zamIds);
    const idP = pick(projektyIds);
    const key = `${idZ}-${idP}`;
    if (ucastSet.has(key)) continue;
    ucastSet.add(key);
    await client.query(
      `INSERT INTO "UcastniSe" ("role","id_zamestnanec","id_projekt") VALUES ($1,$2,$3)`,
      [pick(roleNaProjektu), idZ, idP]
    );
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
