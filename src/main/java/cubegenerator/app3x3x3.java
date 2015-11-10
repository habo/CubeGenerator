package cubegenerator;
import java.io.*;

/*
Data &B01000000, &B00000000' Bild 1 - alles aus
Data &B00100000, &B00000000
Data &b00010000, &B00000000

Data &B01000001, &B11111111' Bild 2 - alles an
Data &B00100001, &B11111111
Data &b00010001, &B11111111

Data &B01000001, &B11111111' Bild 3 - Ebene 1 an
Data &B00100000, &B00000000
Data &b00010000, &B00000000

Data &B01000001, &B00000000' LED 1
Data &B00100000, &B00000000
Data &b00010000, &B00000000

Data &B01000001, &B10000000' LED 2
Data &B00100000, &B00000000
Data &b00010000, &B00000000

Data &B01000001, &B11000000' LED 3
Data &B00100000, &B00000000
Data &b00010000, &B00000000

Data &B01000001, &B11100000' LED 4
Data &B00100000, &B00000000
Data &b00010000, &B00000000

Data &B01000001, &B11110000' LED 5
Data &B00100000, &B00000000
Data &b00010000, &B00000000

Data &B01000001, &B11111000' LED 6
Data &B00100000, &B00000000
Data &b00010000, &B00000000

Data &B01000001, &B11111100' LED 7
Data &B00100000, &B00000000
Data &b00010000, &B00000000

Data &B01000001, &B11111110' LED 8
Data &B00100000, &B00000000
Data &b00010000, &B00000000

Data &B01000001, &B11111111' LED 9
Data &B00100000, &B00000000
Data &b00010000, &B00000000
*/


public class app3x3x3 extends CubePanel
{
  // Mapping der LED's
  // Index 0 dieser bekommt nun den (virtuellen) Index 20
  // und hat, weil die Werte von LED[0] das so festlegen, die Koordinaten unten links
  protected int ind[] =
  {
    6, 7, 8,  15, 16, 17,  24, 25, 26,
    3, 4, 5,  12, 13, 14,  21, 22, 23,
    0, 1, 2,   9, 10, 11,  18, 19, 20,
  };
  // Mapping Value aus der Datenzeile aus der Datei zu der entsprechenden LED
  protected int valMap[] = 
  {
    14, 19, 20,  21, 22, 23,  24, 25, 26,
    38, 43, 44,  45, 46, 47,  48, 49, 50,
    62, 67, 68,  69, 70, 71,  72, 73, 74
  };

  protected String blankLine="DATA &B01000000, &B00000000, &B00100000, &B00000000, &B00010000, &B00000000";

  public app3x3x3(int _version, int _count, boolean directionASC)
  {
    super("Cube 3x3x3", _version, _count, directionASC);
  }

  protected void init()
  {
    super.init(ind, valMap, blankLine);
    String[] s = {"->1", "->2", "->3", "->4", "->5", "<-1", "<-2", "<-3", "<-4", "<-5", "moveR", "moveL" };
    hideComponents(s);
  }

  protected void readFile(String fileName)
  {
    super.readFile(fileName);
    try
    {
	    File file = new File(fileName);
	    if(file.isFile() && file.getName().toLowerCase().endsWith(".txt"))
	    {
	      data.removeAllElements();
	      if(debug.getSelectedIndex() >= 2)
	      {
	        System.out.println("# call readFile openFile: "+fileName);
	      }
	      BufferedReader din = new BufferedReader(new FileReader(fileName));
	      String firstLine=din.readLine();
	      if(!firstLine.toUpperCase().startsWith("DATA "))
	      {
					System.err.println("Abbruch - Erste Zeile hat kein Element DATA");
					return;
				}
				
	      firstLine = firstLine.substring(5);
	      String strImgCount = firstLine.substring(0,firstLine.indexOf("%"));
	      saveImage.setText(strImgCount);
	      int imgcount = Integer.parseInt(strImgCount);
	
	      String strDescription="";
	      String[] dataStr = {"", "", ""};
	      while(true)
	      {
	        String line = din.readLine();
	        if(line == null) break; // Dateiende erreicht
	
	        // Leerzeilen ignorieren
	        if(line.length() == 0) continue;
	
	        // wenn die Zeile nicht mit "Data" beginnt steht Mist in der Datei drin
	        if(!line.toUpperCase().startsWith("DATA "))
	        {
						System.err.println("Abbruch - Element Data nicht gefunden");
						return;
					}
	
	        // check ob Description gesetzt ist und ablegen
	        if(line.indexOf("'") != -1)
	        {
	          strDescription = line.substring(line.indexOf("'"));
	          // da strDescription gesetzt ist muss die line1 nur bis zum "'" gehen
	          line = line.substring(0,line.indexOf("'"));
	        }
	
	        // auswerten f�r welche der String ist
	        String strLevel = line.substring(8,11);
	
	        if(debug.getSelectedIndex() >= 2)
	        {
	          System.out.println("# call readFile readLine: "+line);
	          System.out.println("# call readFile strLevel: "+strLevel);
	        }
	
	        // die einzelnen Ebenen entspr. der Reihenfolge 1,2,3 sortieren
	        if(strLevel.equals("100")) dataStr[0] = line;
	        if(strLevel.equals("010")) dataStr[1] = line;
	        if(strLevel.equals("001")) dataStr[2] = line;
	
	        // check ob alles gesetzt ist
	        if(dataStr[0].length() == 0 || dataStr[1].length() == 0 || dataStr[2].length() == 0) continue;
	
	        // und in data merken
	        line = dataStr[0]+", "+dataStr[1].substring(5,27)+", "+dataStr[2].substring(5,27)+strDescription;
	        if(debug.getSelectedIndex() >= 2)
	        {
	          System.out.println("# call readFile addElement: "+line);
	        }
	        data.addElement(line);
	
	        // alle Werte wieder vorinitialisieren
	        dataStr[0]="";
	        dataStr[1]="";
	        dataStr[2]="";
	        strDescription="";
	      }
	      din.close();
	      setCube(0);
	    }
	  }
	  catch(Exception ex)
	  {
			ex.printStackTrace();
		}
  }

  protected String getSaveLine(int index)
  {
    if(debug.getSelectedIndex() >= 2)
    {
      System.out.println("# call getSaveLine index="+index);
    }
    String line = data.elementAt(index);
    if(debug.getSelectedIndex() >= 2)
    {
      System.out.println("# call getSaveLine("+getClass().getName()+"): "+line);
    }
    // abspeichern als 3x2 Zeilen Entry
    return line.substring(0,27) + "\r\nDATA " + line.substring(29,51) + "\r\nDATA " + line.substring(53) + "\r\n";
  }

  public static void main(String[] args)
  {
    try
    {
      new app3x3x3(1,3,true).init();
    }
    catch(Exception ex2)
    {
      ex2.printStackTrace();
    }
  }
}
