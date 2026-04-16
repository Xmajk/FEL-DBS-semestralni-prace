#import "cover.typ": semestralka

#show: semestralka.with(
  title: "CP3 - DBS - Vytvoření databáze",
  author: "Michal Hrouda a Lukáš Hrubec",
  version: "v1.0",
  date: "16. 4. 2026",
  logo: image("./logo_CVUT.jpg"),
)

#let sql(caption: none, query) = {
  figure(
    supplement: "SQL",
    kind: "sql",
    caption: caption,
    block(
      width: 100%,
      fill: rgb("#f6f8fa"),
      stroke: (left: 3pt + rgb("#0065bd")),
      radius: (right: 4pt),
      inset: (x: 14pt, y: 10pt),
      raw(lang: "sql", query),
    ),
  )
}

#show outline.entry.where(
  level: 1
): set block(above: 1.2em)

#outline()

#pagebreak()


#sql(
  caption: "Výběr všech zaměstnanců",
  "SELECT * FROM \"Zamestnanec\"
WHERE id_oddeleni = 1;",
)     