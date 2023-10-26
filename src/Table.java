import java.util.List;
import java.util.ArrayList;

public class Table {

    private List<String> Titles = new ArrayList<>();
    private List<List<String>> rows = new ArrayList<>();
    private List<Integer> maxLengths = new ArrayList<>();
    private int totalcolCount;

    public void addTitle(List<String> titles) {
        this.Titles = titles;
        totalcolCount = Math.max(titles.size(), totalcolCount);
    }

    public void addRow(List<String> row) {
        rows.add(row);
        totalcolCount = Math.max(row.size(), totalcolCount);
    }

    public List<Integer> calcMax() {

        maxLengths.clear();

        for (int colCount = 0; colCount < totalcolCount; colCount++) {
            maxLengths.add(0);
        }

        for (int colCount = 0; colCount < totalcolCount; colCount++) {

            int titleMaxLength = 0;
            if (colCount < this.Titles.size()) {
                titleMaxLength = Titles.get(colCount).length();
            }
            maxLengths.set(colCount, titleMaxLength);

            for (int rowCount = 0; rowCount < rows.size(); rowCount++) {

                String active = "";

                if (this.exists(rowCount, colCount) == true) {
                    active = rows.get(rowCount).get(colCount);
                }

                int activeLength = active.length();

                if (activeLength > maxLengths.get(colCount)) {
                    maxLengths.set(colCount, activeLength);
                }
            }
        }
        return maxLengths;
    }

    public boolean exists(int row, int col) {
        // if reihe existiert nicht dann, return false
        // if reihe existiert dann, prüfen existiert in dieser reihe die angefragte
        // spalte
        if (rows.size() <= row) {
            return false;
        } else {
            if (rows.get(row).size() <= col) {
                return false;
            } else {
                return true;
            }
        }
    }

    public String lineFill(int col, String item) {
        int spaceLeft;
        spaceLeft = maxLengths.get(col) - item.length();
        String result = "|" + " " + item + " " + " ".repeat(spaceLeft);
        return result;
    }

    public String title(int col) {

        String titleItem = "";
        if (col < this.Titles.size()) {
            titleItem = Titles.get(col);
        }
        String result = lineFill(col, titleItem);
        return result;
    }

    public String item(int col, int row) {

        String result = "";
        if (this.exists(row, col) == true) {
            result = lineFill(col, rows.get(row).get(col));
        } else {
            result = lineFill(col, "");
        }
        return result;
    }

    public void crossLine() {
        for (int colCount = 0; colCount < totalcolCount; colCount++) {
            System.out.print("+" + "-".repeat(maxLengths.get(colCount) + 2));

        }
        System.out.print("+");
    }

    public void print() {

        calcMax();
        crossLine();

        System.out.println();

        for (int colCount = 0; colCount < totalcolCount; colCount++) {
            System.out.print(title(colCount));
        }

        System.out.print("|");
        System.out.println();
        crossLine();
        System.out.println();

        for (int rowCount = 0; rowCount < rows.size(); rowCount++) {
            for (int colCount = 0; colCount < totalcolCount; colCount++) {
                System.out.print(item(colCount, rowCount));
            }
            System.out.print("|");
            System.out.println();

        }
        crossLine();
    }

    public void printTo(int row) {

        calcMax();
        crossLine();

        System.out.println();

        for (int colCount = 0; colCount < totalcolCount; colCount++) {
            System.out.print(title(colCount));
        }

        System.out.print("|");
        System.out.println();
        crossLine();
        System.out.println();

        for (int rowCount = 0; rowCount < row; rowCount++) {
            for (int colCount = 0; colCount < totalcolCount; colCount++) {
                System.out.print(item(colCount, rowCount));
            }
            System.out.print("|");
            System.out.println();

        }
        crossLine();
    }

    public void printFromTo(int startRow, int endRow) {

        calcMax();

        if (startRow == endRow) {
            return;
        }

        System.out.println();

        for (int rowCount = startRow; rowCount < endRow; rowCount++) {
            for (int colCount = 0; colCount < totalcolCount; colCount++) {
                System.out.print(item(colCount, rowCount));
            }
            System.out.print("|");
            System.out.println();

        }
        crossLine();
    }

    public void printFrom(int row) {

        calcMax();

        if (row == rows.size()) {
            return;
        }

        System.out.println();

        for (int rowCount = row; rowCount < rows.size(); rowCount++) {
            for (int colCount = 0; colCount < totalcolCount; colCount++) {
                System.out.print(item(colCount, rowCount));
            }
            System.out.print("|");
            System.out.println();

        }
        crossLine();
    }

}
