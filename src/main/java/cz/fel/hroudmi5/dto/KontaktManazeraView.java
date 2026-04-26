package cz.fel.hroudmi5.dto;

public record KontaktManazeraView(
        String oddeleni,
        String manazer,
        String email,
        String telefon
) {
    public static KontaktManazeraView from(Object[] row) {
        return new KontaktManazeraView(
                (String) row[0],
                (String) row[1],
                (String) row[2],
                (String) row[3]
        );
    }
}
