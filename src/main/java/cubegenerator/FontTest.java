package cubegenerator;

public class FontTest {

    static Font5x5 cf = new Font5x5();

    public static void main(String[] args) {

        for (Character c : cf.getAvailableChars()) {
            displayN(c);
        }

    }

    public static void displayXY(char see) {
        System.out.println("-------- " + see + " ----------");

        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                System.out.print(cf.getForLED(see, x, y) ? "#" : ".");
            }
            System.out.println("");
        }
    }

    public static void displayN(char see) {
        System.out.println("-------- " + see + " ----------");

        for (int n = 0; n < 25; n++) {
            System.out.print(cf.getForLED(see, n) ? "#" : ".");
            if (n % 5 == 4) {
                System.out.println("");
            }
        }
    }
}
