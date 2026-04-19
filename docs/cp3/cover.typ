// ====================================================================
//  Template pro semestrální práci (ČVUT)
//  Použití:
//    #import "template.typ": semestralka
//    #show: semestralka.with(
//      title: "Název práce",
//      author: "Jméno Příjmení",
//      version: "v1.0",
//      date: "22. 3. 2026",
//      logo: image("cvut-logo.svg"),
//    )
// ====================================================================

#let semestralka(
  title: "Název semestrální práce",
  author: "Jméno Příjmení",
  version: "v1.0",
  date: datetime.today().display("[day].[month].[year]"),
  logo: none,
  // barva modrého polygonu – uprav podle potřeby
  accent: rgb("#0065bd"),
  body,
) = {
  // ---------- Globální nastavení dokumentu ----------
  set document(title: title, author: author)
  set page(
    paper: "a4",
    margin: (x: 2.5cm, y: 2.5cm),
    numbering: none,
  )
  set text(font: "New Computer Modern", size: 11pt, lang: "cs")
  set par(justify: true, leading: 0.7em)
  show heading: set block(above: 1.4em, below: 1em)

  // ---------- COVER PAGE ----------
  page(
    margin: 0pt,
    numbering: none,
    background: {
      // Šikmý modrý polygon přes horní ~polovinu stránky.
      // A4 = 210 × 297 mm. Polygon jde od levého horního rohu,
      // přes pravý okraj (cca ve výšce 45 %) a dolů vlevo (cca 65 %).
      place(
        top + left,
        polygon(
          fill: accent,
          (0mm, 0mm),
          (210mm, 0mm),
          (210mm, 125mm),
          (0mm, 185mm),
        ),
      )
    },
  )[
    // Titulek a autor – umístěné nahoře v modré ploše
    #place(
      top + left,
      dx: 20mm,
      dy: 35mm,
      box(width: 170mm)[
        #set par(justify: false)
        #text(fill: white, size: 28pt, weight: "bold", hyphenate: false)[#title]
        #v(-1.2em)
        #text(fill: white, size: 15pt)[#author]
      ],
    )

    // Logo vlevo dole
    #place(
      bottom + left,
      dx: 20mm,
      dy: -20mm,
      box(height: 25mm)[
        #if logo != none { logo } else [
          #text(size: 9pt, fill: gray)[(logo)]
        ]
      ],
    )

    // Verze a datum vpravo dole
    #place(
      bottom + right,
      dx: -20mm,
      dy: -20mm,
      box[
        #set text(size: 9pt)
        #set align(right)
        #version \
        #date
      ],
    )
  ]

  // ---------- OBSAH DOKUMENTU ----------
  counter(page).update(1)
  body
}
