package cubegenerator;

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;

/*
 Dirk Otto21:00
 DATA &B11111111, &B11111111, &B11111111, &B00111101
 DATA &B00000000, &B00000000, &B00000000, &B00111010
 DATA &B00000000, &B00000000, &B00000000, &B00110110
 DATA &B00000000, &B00000000, &B00000000, &B00101110
 DATA &B00000000, &B00000000, &B00000000, &B00011110
 und im prog 5x25 ist das dann so:
 1111111111111111111111111
 0000000000000000000000000
 0000000000000000000000000
 0000000000000000000000000
 0000000000000000000000000
 */
public class app5x25 extends CubePanel {

    // Mapping der LED's

    protected int ind[]
            = {
                100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124,
                75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99,
                50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74,
                25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,};

    // Mapping Value aus der Datenzeile aus der Datei zu der entsprechenden LED
    protected int valMap[]
            = {
                14, 13, 12, 11, 10, 9, 8, 7, 26, 25, 24, 23, 22, 21, 20, 19, 38, 37, 36, 35, 34, 33, 32, 31, 50,
                62, 61, 60, 59, 58, 57, 56, 55, 74, 73, 72, 71, 70, 69, 68, 67, 86, 85, 84, 83, 82, 81, 80, 79, 98,
                110, 109, 108, 107, 106, 105, 104, 103, 122, 121, 120, 119, 118, 117, 116, 115, 134, 133, 132, 131, 130, 129, 128, 127, 146,
                158, 157, 156, 155, 154, 153, 152, 151, 170, 169, 168, 167, 166, 165, 164, 163, 182, 181, 180, 179, 178, 177, 176, 175, 194,
                206, 205, 204, 203, 202, 201, 200, 199, 218, 217, 216, 215, 214, 213, 212, 211, 230, 229, 228, 227, 226, 225, 224, 223, 242
            };

    protected String blankLine = "DATA &B00000000, &B00000000, &B00000000, &B00111110, &B00000000, &B00000000, &B00000000, &B00111110, &B00000000, &B00000000, &B00000000, &B00111110, &B00000000, &B00000000, &B00000000, &B00111110, &B00000000, &B00000000, &B00000000, &B00111110";
    private JCheckBox checkCut;

    public app5x25(int _version, int _count, boolean directionASC) {
        super("Cube 5x25", _version, _count, directionASC);
        baseRadius = 10;
    }

    protected void init() {
        super.init(ind, valMap, blankLine);

        checkCut = new JCheckBox("<html><body>Lauflicht<br>aus</body></html>");
        this.panel_cube.add(checkCut);
        checkCut.setFont(new Font("Arial", Font.PLAIN, 10));
        checkCut.setBackground(bgcolor);
        checkCut.setBounds(180, 210, 70, 20);
    }

    protected void readFile(String fileName) {
        super.readFile(fileName);
        try {
            File file = new File(fileName);
            if (file.isFile() && file.getName().toLowerCase().endsWith(".txt")) {
                data.removeAllElements();
                BufferedReader din = new BufferedReader(new FileReader(fileName));
                String firstLine = din.readLine();
                if (firstLine.indexOf("%") == -1) {
                    System.err.println("Erste Zeile hat kein % Eintrag");
                    din.close();
                    din = new BufferedReader(new FileReader(fileName));
                } else {
                    firstLine = firstLine.substring(5);
                    String strImgCount = firstLine.substring(0, firstLine.indexOf("%"));
                    saveImage.setText(strImgCount);
                    int imgcount = Integer.parseInt(strImgCount);
                }
                String strDescription = "";
                while (true) {
                    String line = din.readLine();
                    if (line == null) {
                        break; // Dateiende erreicht
                    }
                    // Leerzeilen ignorieren
                    if (line.length() == 0) {
                        continue;
                    }

                    // wenn die Zeile nicht mit "Data" beginnt steht Mist in der Datei drin
                    if (!line.toUpperCase().startsWith("DATA ")) {
                        System.err.println("Abbruch - Element Data nicht gefunden");
                        return;
                    }

                    // check ob Java- oder Exe- Tool
                    if (new StringTokenizer(line, ",").countTokens() == 4) {
                        if (debug.getSelectedIndex() >= 2) {
                            System.out.println("# call readFile: (kurze) line(1) gefunden: " + line);
                            System.out.println("# call readFile: (noch) 4 zeilen lesen und eine draus machen");
                        }

	          // Java Tool erzeugte Format , also umwandeln in Exe Tool erzeugte Format
                        // dazu 5 Zeilen lesen
                        String tmpLine = line;
                        for (int i = 0; i < 4; i++) {
                            line = din.readLine().substring(5).trim();
                            // check ob Description gesetzt ist und ablegen
                            if (line.indexOf("'") != -1) {
                                strDescription = line.substring(line.indexOf("'"));
                                // da strDescription gesetzt ist muss die line1 nur bis zum "'" gehen
                                line = line.substring(0, line.indexOf("'"));
                            }
                            if (debug.getSelectedIndex() >= 2) {
                                System.out.println("# call readFile: (kurze) line (" + (i + 2) + "): " + line);
                            }
                            tmpLine += "," + line;
                        }
                        // nun die Zeile noch passend aufbereiten
                        if (debug.getSelectedIndex() >= 2) {
                            System.out.println("# call readFile: neue line (vor): " + tmpLine);
                        }
                        StringTokenizer tmpSt = new StringTokenizer(tmpLine, ",");
                        line = "";
                        while (tmpSt.hasMoreTokens()) {
                            String entry = tmpSt.nextToken().trim();
                            if (line.length() == 0) {
                                line = entry;
                            } else {
                                line += ", " + entry;
                            }
                        }

                        if (debug.getSelectedIndex() >= 2) {
                            System.out.println("# call readFile: neue line(danach): " + line);
                        }

                    }
                    line += strDescription;
                    strDescription = "";
                    if (debug.getSelectedIndex() >= 2) {
                        System.out.println("# call readFile data.addElement: " + line);
                    }
                    data.addElement(line);
                }
                din.close();
                setCube(0);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected String getSaveLine(int index) {
        if (debug.getSelectedIndex() >= 2) {
            System.out.println("# call getSaveLine index=" + index);
        }
        String line = data.elementAt(index);
        if (debug.getSelectedIndex() >= 2) {
            System.out.println("# call getSaveLine(" + getClass().getName() + "): " + line);
        }
        // abspeichern als 5x4 Zeilen Entry oder wenn auskommteniert abspeichern wie Exe Tool
        line = line.substring(0, 51) + "\r\nDATA " + line.substring(53, 99) + "\r\nDATA " + line.substring(101, 147) + "\r\nDATA " + line.substring(149, 195) + "\r\nDATA " + line.substring(197) + "\r\n";

        byte[] tmp = line.getBytes();
        // ebene 1
        tmp[45] = '1';
        tmp[46] = '1';
        tmp[47] = '1';
        tmp[48] = '1';
        tmp[49] = '0';
        // ebene 2
        tmp[98] = '1';
        tmp[99] = '1';
        tmp[100] = '1';
        tmp[101] = '0';
        tmp[102] = '1';
        // ebene 3
        tmp[151] = '1';
        tmp[152] = '1';
        tmp[153] = '0';
        tmp[154] = '1';
        tmp[155] = '1';
        // ebene 4
        tmp[204] = '1';
        tmp[205] = '0';
        tmp[206] = '1';
        tmp[207] = '1';
        tmp[208] = '1';
        // ebene 5
        tmp[257] = '0';
        tmp[258] = '1';
        tmp[259] = '1';
        tmp[260] = '1';
        tmp[261] = '1';
        line = new String(tmp);

    //j=49,97,145,193,241
        // ist egal mit dem Check, es soll nur immer eine Ebene an sein
        // deswegen wird jetzt fest codiert dass Ebene 1 immer an (0) ist die anderen aus (1) sind
        tmp[49] = '1';
        tmp[97] = '1';
        tmp[145] = '1';
        tmp[193] = '1';
        tmp[241] = '0';

        return line;
    }

    protected void moveLeft() {
        super.moveLeft(checkCut.isSelected());
    }

    protected void moveRight() {
        super.moveRight(checkCut.isSelected());
    }

    protected void moveDown() {
        super.moveDown(checkCut.isSelected());
    }

    protected void moveUp() {
        super.moveUp(checkCut.isSelected());
    }

    public static void main(String[] args) {

        try {
            new app5x25(3, 5, false).init();
        } catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }
}
