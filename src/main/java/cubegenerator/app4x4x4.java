package cubegenerator;

import java.util.*;
import java.io.*;

/*
Data &B00000000, &B00000000, &B10000000, &B00000000, &B00000000, &B01000000, &B00000000, &B00000000, &B00100000, &B00000001, &B00000000, &B00010000 ' LED 1 an
Data &B00000000, &B00000000, &B10000000, &B00000000, &B00000000, &B01000000, &B00000000, &B00000000, &B00100000, &B00010001, &B00000000, &B00010000 ' LED 1,2 an
Data &B00000000, &B00000000, &B10000000, &B00000000, &B00000000, &B01000000, &B00000000, &B00000000, &B00100000, &B00010001, &B00000100, &B00010000 ' LED 1,2,3 an
Data &B00000000, &B00000000, &B10000000, &B00000000, &B00000000, &B01000000, &B00000000, &B00000000, &B00100000, &B00010001, &B00000100, &B00010001 ' LED 1,2,3,4 an
*/

public class app4x4x4 extends CubePanel
{
  int ind[] =
  {
   60,61,62,63,  44,45,46,47,  28,29,30,31,  12,13,14,15, 
   56,57,58,59,  40,41,42,43,  24,25,26,27,   8, 9,10,11,
   52,53,54,55,  36,37,38,39,  20,21,22,23,   4, 5, 6, 7,
   48,49,50,51,  32,33,34,35,  16,17,18,19,   0, 1, 2, 3,
  };

  // Mapping Value aus der Datenzeile aus der Datei zu der entsprechenden LED
  protected int valMap2[] = 
  {
     14,  13,  12,  11,   10,   9,  26,  25,   24,  23,  22,  21,   38,  37,  36,  35,
     50,  49,  48,  47,   46,  45,  62,  61,   60,  59,  58,  57,   74,  73,  72,  71,
     86,  85,  84,  83,   82,  81,  98,  97,   96,  95,  94,  93,  110, 109, 108, 107,
    122, 121, 120, 119,  118, 117, 134, 133,  132, 131, 130, 129,  146, 145, 144, 143,
  };
  protected int valMap[] = 
  {
     14,  10,  24,  38,   13,   9,  23,  37,   12,  26,  22,  36,   11,  25,  21,  35,
     50,  46,  60,  74,   49,  45,  59,  73,   48,  62,  58,  72,   47,  61,  57,  71,
     86,  82,  96, 110,   85,  81,  95, 109,   84,  98,  94, 108,   83,  97,  93, 107,
    122, 118, 132, 146,  121, 117, 131, 145,  120, 134, 130, 144,  119, 133, 129, 143,
  };

  protected String blankLine="DATA &B00000000, &B00000000, &B10000000, &B00000000, &B00000000, &B01000000, &B00000000, &B00000000, &B00100000, &B00000000, &B00000000, &B00010000";

  public app4x4x4(int _version, int _count, boolean directionASC)
  {
    super("Cube 4x4x4", _version, _count, directionASC);
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
	      BufferedReader din = new BufferedReader(new FileReader(fileName));
	      String firstLine=din.readLine();
	      if(!firstLine.startsWith("Data "))
	      {
					System.err.println("Abbruch - Erste Zeile hat kein Element Data");
					return;
				}
				
	      firstLine = firstLine.substring(5);
	      String strImgCount = firstLine.substring(0,firstLine.indexOf("%"));
	      saveImage.setText(strImgCount);
	      int imgcount = Integer.parseInt(strImgCount);
	
	      String dataStr[] = {"","","",""};
	      String strDescription = "";
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
	        StringTokenizer tmpSt2 = new StringTokenizer(line, ",");
	        tmpSt2.nextToken();
	        tmpSt2.nextToken();
	        String strLevel = tmpSt2.nextToken().substring(3,7);
	
	        if(debug.getSelectedIndex() >= 2)
	        {
	          System.out.println("# call readFile readLine: "+line);
	          System.out.println("# call readFile strLevel: "+strLevel);
	        }
	
	        // die einzelnen Ebenen entspr. der Reihenfolge 1,2,3,4 sortieren
	        if(strLevel.equals("1000")) dataStr[0] = line.substring(5);
	        if(strLevel.equals("0100")) dataStr[1] = line.substring(5);
	        if(strLevel.equals("0010")) dataStr[2] = line.substring(5);
	        if(strLevel.equals("0001")) dataStr[3] = line.substring(5);
	
	        // check ob alles gesetzt ist
	        if(dataStr[0].length() == 0 ||
	           dataStr[1].length() == 0 ||
	           dataStr[2].length() == 0 ||
	           dataStr[3].length() == 0) continue;
	
	        if(debug.getSelectedIndex() >= 1)
	        {
	          System.out.println("# call readFile StringTokenizer dataStr[0]: "+dataStr[0]);
	          System.out.println("# call readFile StringTokenizer dataStr[0]: "+dataStr[1]);
	          System.out.println("# call readFile StringTokenizer dataStr[0]: "+dataStr[2]);
	          System.out.println("# call readFile StringTokenizer dataStr[0]: "+dataStr[3]);
	        }
	
	        // aus line die �berfl�ssigen Leerstrings entfernen
	        StringTokenizer tmpSt = new StringTokenizer(dataStr[0]+","+dataStr[1]+","+dataStr[2]+","+dataStr[3], ",");
	        String tmpLine="";
	        while(tmpSt.hasMoreTokens())
	        {
	          String entry = tmpSt.nextToken().trim();
	          if(tmpLine.length() == 0)
	          {
	            tmpLine = entry;
	          }
	          else
	          {
	            tmpLine += ", " + entry;
	          }
	        }
	        line = "DATA " + tmpLine+strDescription;
	
	        if(debug.getSelectedIndex() >= 1)
	        {
	          System.out.println("# call readFile addElement: "+line);
	        }
	        data.addElement(line);
	
	        // alle Werte wieder vorinitialisieren
	        dataStr[0]="";
	        dataStr[1]="";
	        dataStr[2]="";
	        dataStr[3]="";
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
    // abspeichern als 4x3 Zeilen Entry
    return line.substring(0,39) + "\r\nDATA " + line.substring(41,75) + "\r\nDATA " + line.substring(77,111) + "\r\nDATA " + line.substring(113) + "\r\n";
  }

  public static void main(String[] args)
  {
    try
    {
      new app4x4x4(1,4,true).init();
    }
    catch(Exception ex2)
    {
      ex2.printStackTrace();
    }
  }
}
