package cubegenerator;

import java.awt.Color;

public class LED {

    boolean state = false;
    int level = -1;
    int nr = -1;
    int index = -1;
    Color c_on;
    Color c_off;

    public LED(int _l, int _n, int _i/*, int _x, int _y, int _w, int _h*/, Color _c_on, Color _c_off) {
        level = _l;
        nr = _n;
        index = _i;
        c_on = _c_on;
        c_off = _c_off;
    }

    public LED(int _l, int _n, int _i/*, int _x, int _y, int _w, int _h*/) {
        this(_l, _n, _i, Color.RED, Color.BLACK);
    }
}
