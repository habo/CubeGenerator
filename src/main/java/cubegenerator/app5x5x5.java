package cubegenerator;

import java.util.*;
import java.io.*;

/*
 # Ebene 1 - alles aus
 DATA &B00000000, &B00000000, &B00000000, &B00111110, &B00000000, &B00000000, &B00000000, &B00111110, &B00000000, &B00000000, &B00000000, &B00111110, &B00000000, &B00000000, &B00000000, &B00111110, &B00000000, &B00000000, &B00000000, &B00111110, 
 # Ebene 1 - LED 1 an
 DATA &B00000001, &B00000000, &B00000000, &B00111100, &B00000000, &B00000000, &B00000000, &B00111110, &B00000000, &B00000000, &B00000000, &B00111110, &B00000000, &B00000000, &B00000000, &B00111110, &B00000000, &B00000000, &B00000000, &B00111110, 
 ->            x                                  x
 */
public class app5x5x5 extends CubePanel {
    // Mapping der LED's
    // Index 0 dieser bekommt nun den (virtuellen) Index 20
    // und hat, weil die Werte von LED[0] das so festlegen, die Koordinaten unten links

    protected int ind[]
            = {
                120, 121, 122, 123, 124, 95, 96, 97, 98, 99, 70, 71, 72, 73, 74, 45, 46, 47, 48, 49, 20, 21, 22, 23, 24,
                115, 116, 117, 118, 119, 90, 91, 92, 93, 94, 65, 66, 67, 68, 69, 40, 41, 42, 43, 44, 15, 16, 17, 18, 19,
                110, 111, 112, 113, 114, 85, 86, 87, 88, 89, 60, 61, 62, 63, 64, 35, 36, 37, 38, 39, 10, 11, 12, 13, 14,
                105, 106, 107, 108, 109, 80, 81, 82, 83, 84, 55, 56, 57, 58, 59, 30, 31, 32, 33, 34, 5, 6, 7, 8, 9,
                100, 101, 102, 103, 104, 75, 76, 77, 78, 79, 50, 51, 52, 53, 54, 25, 26, 27, 28, 29, 0, 1, 2, 3, 4
            };

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

    protected int levelRotateMap[][]
            = {
                // Level 0 - Dummy
                {},
                // Level 5
                {
                    // aussen, alle geraden nummern
                    120, 122,
                    122, 124,
                    124, 74,
                    74, 24,
                    24, 22,
                    22, 20,
                    20, 70,
                    70, 120,
                    // aussen, alle ungeraden nummern
                    121, 123,
                    123, 99,
                    99, 49,
                    49, 23,
                    23, 21,
                    21, 45,
                    45, 95,
                    95, 121,
                    // innen
                    96, 97,
                    97, 98,
                    98, 73,
                    73, 48,
                    48, 47,
                    47, 46,
                    46, 71,
                    71, 96,},
                // Level 4
                {
                    // aussen, alle geraden nummern
                    115, 117,
                    117, 119,
                    119, 69,
                    69, 19,
                    19, 17,
                    17, 15,
                    15, 65,
                    65, 115,
                    // aussen, alle ungeraden nummern
                    116, 118,
                    118, 94,
                    94, 44,
                    44, 18,
                    18, 16,
                    16, 40,
                    40, 90,
                    90, 116,
                    // innen
                    91, 92,
                    92, 93,
                    93, 68,
                    68, 43,
                    43, 42,
                    42, 41,
                    41, 66,
                    66, 91,},
                // Level 3
                {
                    // aussen, alle geraden nummern
                    110, 112,
                    112, 114,
                    114, 64,
                    64, 14,
                    14, 12,
                    12, 10,
                    10, 60,
                    60, 110,
                    // aussen, alle ungeraden nummern
                    111, 113,
                    113, 89,
                    89, 39,
                    39, 13,
                    13, 11,
                    11, 35,
                    35, 85,
                    85, 111,
                    // innen
                    86, 87,
                    87, 88,
                    88, 63,
                    63, 38,
                    38, 37,
                    37, 36,
                    36, 61,
                    61, 86,},
                // Level 2
                {
                    // aussen, alle geraden nummern
                    105, 107,
                    107, 109,
                    109, 59,
                    59, 9,
                    9, 7,
                    7, 5,
                    5, 55,
                    55, 105,
                    // aussen, alle ungeraden nummern
                    106, 108,
                    108, 84,
                    84, 34,
                    34, 8,
                    8, 6,
                    6, 30,
                    30, 80,
                    80, 106,
                    // innen, alle geraden nummern
                    81, 82,
                    82, 83,
                    83, 58,
                    58, 33,
                    33, 32,
                    32, 31,
                    31, 56,
                    56, 81,},
                // Level 1
                {
                    // aussen, alle geraden nummern
                    100, 102,
                    102, 104,
                    104, 54,
                    54, 4,
                    4, 2,
                    2, 0,
                    0, 50,
                    50, 100,
                    // aussen, alle ungeraden nummern
                    101, 103,
                    103, 79,
                    79, 29,
                    29, 3,
                    3, 1,
                    1, 25,
                    25, 75,
                    75, 101,
                    // innen
                    76, 77,
                    77, 78,
                    78, 53,
                    53, 28,
                    28, 27,
                    27, 26,
                    26, 51,
                    51, 76,},};

    public app5x5x5(int _version, int _count, boolean directionASC) {
        super("Cube 5x5x5", _version, _count, directionASC);
        baseRadius = 10;
    }

    protected void init() {
        super.init(ind, valMap, blankLine,new Font5x5());
    }

    protected void readFile(String fileName) {
        super.readFile(fileName);
        /*
         Zeilen können so (mit Exe Tool):
         DATA &B11111111, &B11111111, &B11111111, &B00111101, &B11111111, &B11111111, &B11111111, &B00111011, &B11111111, &B11111111, &B11111111, &B00110111, &B11111111, &B11111111, &B11111111, &B00101111, &B11111111, &B11111111, &B11111111, &B00011111, 

         oder so (mit Java Tool) aufgebaut sein:
         Data &B11111111 , &B11111111 , &B11111111 , &B00111101
         Data &B00000000 , &B00000000 , &B00000000 , &B00111110
         Data &B11111111 , &B11111111 , &B11111111 , &B00111111
         Data &B00000000 , &B00000000 , &B00000000 , &B00111110
         Data &B11111111 , &B11111111 , &B11111111 , &B00111111

         Deswegen werden die eingelesenen Zeilen hier in ein einheitliches (Exe Tool) Format gebracht
         und als String im Vector "data" abgelegt
         */
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

        //dirk sagt - so soll der letzte block immer aussehen (# ist LED 25)
        //&Bxx11110#
        //&Bxx11101#
        //&Bxx11011#
        //&Bxx10111#
        //&Bxx01111#
        byte[] tmp = line.getBytes();
        // ebene 1
        tmp[45] = '1';
        tmp[46] = '1';
        tmp[47] = '1';
        tmp[48] = '1';
        //tmp[ 49] = (byte)((tmp[50] == '1') ? '0' : '1');
        tmp[49] = '0';
        // ebene 2
        tmp[98] = '1';
        tmp[99] = '1';
        tmp[100] = '1';
        //tmp[101] = (byte)((tmp[103] == '1') ? '0' : '1');
        tmp[101] = '0';
        tmp[102] = '1';
        // ebene 3
        tmp[151] = '1';
        tmp[152] = '1';
        //tmp[153] = (byte)((tmp[156] == '1') ? '0' : '1');
        tmp[153] = '0';
        tmp[154] = '1';
        tmp[155] = '1';
        // ebene 4
        tmp[204] = '1';
        //tmp[205] = (byte)((tmp[209] == '1') ? '0' : '1');
        tmp[205] = '0';
        tmp[206] = '1';
        tmp[207] = '1';
        tmp[208] = '1';
        // ebene 5
        //tmp[257] = (byte)((tmp[262] == '1') ? '0' : '1');
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

    protected void moveLeft(int level, boolean clipping) {
        if (debug.getSelectedIndex() >= 2) {
            System.out.println("call moveLeft app5x5x5");
        }
        if (levelRotateMap[level].length == 0) {
            return;
        }

        // hier kommen die Zustandswerte rein
        boolean[] tmp = new boolean[levelRotateMap[level].length / 2];
        // Zustände merken
        for (int i1 = 0, i2 = 0; i2 < levelRotateMap[level].length; i1++, i2 += 2) {
            tmp[i1] = leds[levelRotateMap[level][i2]].state;
        }
        // Zustände ändern/zurückschreiben
        for (int i1 = 0, i2 = 1; i2 < levelRotateMap[level].length - 1; i1++, i2 += 2) {
            leds[levelRotateMap[level][i2]].state = tmp[i1];
        }
        leds[levelRotateMap[level][levelRotateMap[level].length - 1]].state = tmp[tmp.length - 1];
        repaint();
    }

    protected void moveRight(int level, boolean clipping) {
        if (debug.getSelectedIndex() >= 2) {
            System.out.println("call moveRight app5x5x5");
        }
        if (levelRotateMap[level].length == 0) {
            return;
        }

        // hier kommen die Zustandswerte rein
        boolean[] tmp = new boolean[levelRotateMap[level].length / 2];
        // Zustände merken
        for (int i1 = 0, i2 = 1; i2 < levelRotateMap[level].length - 1; i1++, i2 += 2) {
            tmp[i1] = leds[levelRotateMap[level][i2]].state;
        }
        tmp[tmp.length - 1] = leds[levelRotateMap[level][levelRotateMap[level].length - 1]].state;
        // Zustände ändern/zurückschreiben
        for (int i1 = 0, i2 = 0; i2 < levelRotateMap[level].length; i1++, i2 += 2) {
            leds[levelRotateMap[level][i2]].state = tmp[i1];
        }
        repaint();
    }

    public static void main(String[] args) {
        try {
            new app5x5x5(2, 5, false).init();
        } catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }
}
