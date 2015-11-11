package cubegenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

abstract public class CubeFont extends HashMap<Character, Character[]> {
    private final int dimensionX;
    private final int dimensionY;
    public CubeFont(int x, int y) {
        dimensionX=x;
        dimensionY=y;
    }
    
    public boolean getForLED(char c, int x, int y) {
        char ychar = get(c)[x];
        return ((ychar >> (6 - y)) & 0x01) == 1;
    }
    public boolean getForLED(char c, int n) {
        char ychar = get(c)[n/dimensionX];
        return ((ychar >> (6 - n%dimensionY)) & 0x01) == 1;
    }

    public List<Character> getAvailableChars() {
        List list = new ArrayList(keySet());
        Collections.sort(list);
        return list;
    }
}
