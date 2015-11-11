package cubegenerator;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;

public class CubePanel extends JFrame implements ActionListener, MouseListener, MouseMotionListener, Runnable {

    private CubeFont cubefont = null;
    protected int baseRadius = 20;
    protected int xp = 200;
    protected int yp = 5;
    protected int xw = 200;
    protected int yh = 160;
    protected int dv = 60;
    protected int panel_width = 250;
    protected Color bgcolor = new Color(0xd4, 0xd0, 0xc7);
    protected int count;
    protected LED leds[];
    protected Vector<String> data = new Vector<>();
    protected JLabel curImage, saveImage;
    protected String[] debugStrings = {"Debug nein", "Debug 1", "Debug 2", "Debug 3", "Debug 4", "Debug 5", "Debug 6"};
    protected JComboBox debug = new JComboBox(debugStrings);
    protected JTextField description = new JTextField();
    private JSlider value = new JSlider(0, 0, 0);
    private JRadioButton ln = new JRadioButton("nichts", true);
    private JRadioButton lv = new JRadioButton("vertikal", false);
    private JRadioButton lh = new JRadioButton("horizontal", false);
    private JRadioButton ld = new JRadioButton("diagonal", false);
    private final JButton btn_save_image;
    private final JButton btn_load_file;
    private final JButton btn_save_file;
    private JPanel panel_menu = new JPanel();
    private final JButton playCube;
    private final JSlider playTime;
    private String selectedFile = "";
    private boolean isthreadAlive = false;
    private Thread playThread;
    protected static CubePanel myInstance;
    protected JPanel panel_cube;
    protected JComboBox<Character> textcombo;
//    protected JList textin;
//    protected JScrollPane textpane;
    protected String confFileName;
    protected Properties conf = new Properties();
    protected int version = 0;
    protected String[] colorStrings = {"rot", "gelb", "grün", "blau", "weiß", "cyan", "pink"};
    protected JTextArea status;

    protected String blankLine = null;
    protected int valMap[] = null;
    protected int ind[] = null;

    private Rectangle converToRectangle(String s) {
        StringTokenizer st = new StringTokenizer(s, ",");
        return new Rectangle(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
    }

    private void createLayer(JPanel panel_cube, int i, int y) {
        createButton("Ebene " + i, 10, y, 78, 20, "Level" + i, panel_cube);
        createButton("L", 90, y, 50, 20, "<-" + i, panel_cube);
        createButton("R", 140, y, 50, 20, "->" + i, panel_cube);
        JComboBox colorList = new JComboBox(colorStrings);
        panel_cube.add(colorList);
        colorList.setFont(new Font("Arial", Font.PLAIN, 11));
        colorList.setBounds(190, y, 60, 20);
        colorList.addActionListener(this);
        colorList.setActionCommand("Color Ebene " + i);
    }

    public CubePanel(String title, int _version, int _count, boolean directionASC) {
        super(((title != null) ? title : "LED Bildmuster Generator"));

        myInstance = this;
        version = _version;
        count = _count;
        confFileName = getClass().getName() + ".conf";

        try {
            FileInputStream fin = new FileInputStream(confFileName);
            conf.load(fin);
            fin.close();
        } catch (java.io.FileNotFoundException ex) {
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setBackground(bgcolor);
        panel_menu.setLayout(null);
        panel_menu.setBackground(bgcolor);
        panel_menu.setBounds(0, 0, 200, 300);

        panel_cube = new JPanel();
        panel_menu.add(panel_cube);
        panel_cube.setLayout(null);
        panel_cube.setBackground(bgcolor);
        TitledBorder titleborder1 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Cube");
        titleborder1.setTitleJustification(TitledBorder.LEFT);
        titleborder1.setTitlePosition(TitledBorder.TOP);
        panel_cube.setBorder(titleborder1);
        int y = 20;
        createButton("aus", 10, y, 60, 20, "Cube off", panel_cube);
        createButton("an", 10, y + 20, 60, 20, "Cube on", panel_cube);

        y += 25;
        createLabel("Ebenen", 10, y, 60, 20, panel_cube);
        y += 15;
        createLabel("Invertieren", 10, y, 70, 20, panel_cube);
        createLabel("Drehen/Schieben", 90, y, 120, 20, panel_cube);
        createLabel("Farbe", 190, y, 70, 20, panel_cube);
        y += 2;
        if (directionASC) {
            for (int i = 1; i <= count; i++) {
                createLayer(panel_cube, i, y += 20);
            }
        } else {
            for (int i = count; i > 0; i--) {
                createLayer(panel_cube, i, y += 20);
            }
        }
        y += 20;
        createLabel("drehen/schieben:", 10, y, 100, 20, panel_cube);
        y += 20;
        createButton("Unten", 10, y, 80, 20, "moveD", panel_cube);
        createButton("Oben", 90, y, 80, 20, "moveU", panel_cube);
        // texteingabe
        textcombo = new JComboBox();
        panel_cube.add(textcombo);
        textcombo.setFont(new Font("Arial", Font.PLAIN, 11));
        textcombo.setBounds(170, y, 80, 20);
        textcombo.addActionListener(this);
        //textcombo.setActionCommand("Color Ebene " + i);

        // texteingabe ende
        y += 20;
        createButton("Rechts", 90, y, 80, 20, "moveR", panel_cube);
        createButton("Links", 10, y, 80, 20, "moveL", panel_cube);
        createButton("Text", 170, y, 80, 20, "TextByFont", panel_cube);
        y += 30;
        panel_cube.setBounds(1, 1, panel_width + 20, y);

        int y2 = panel_cube.getBounds().y + y + 8;
        JPanel panel_image = new JPanel();
        panel_menu.add(panel_image);
        panel_image.setLayout(null);
        panel_image.setBackground(bgcolor);
        TitledBorder titleborder2 = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED), "Bild");
        titleborder2.setTitleJustification(TitledBorder.LEFT);
        titleborder2.setTitlePosition(TitledBorder.TOP);
        panel_image.setBorder(titleborder2);
        y = 20;
        createButton("vorheriges anschauen", 5, y, 190, 20, "Show PrevImage", panel_image);
        y += 20;
        btn_save_image = createButton("<html><body>aktuelles <u>B</u>ild speichern</body></html>", 5, y, 190, 20, "Save Image", panel_image);
        y += 20;
        createLabel("aktuelles:", 10, y, 90, 20, panel_image);
        curImage = createLabel("", 100, y, 100, 20, panel_image);
        y += 20;
        createLabel("bisher gespeichert:", 10, y, 130, 20, panel_image);
        saveImage = createLabel("", 140, y, 60, 20, panel_image);
        y += 20;
        createLabel("Kommentar:", 10, y, 80, 20, panel_image);
        panel_image.add(description);
        description.setFont(new Font("Courier New", Font.PLAIN, 12));
        description.setText("");
        description.setBounds(80, y, 100, 20);
        y += 20;
        //createLabel("Einzelschritt:", 10, y, 80, 20, panel_image);
        //y += 22;
        createButton("<-", 10, y, 60, 20, "Step <-", panel_image);
        createButton("->", 70, y, 60, 20, "Step ->", panel_image);
        //y += 22;
        playCube = createButton("Play", 130, y, 60, 20, "Play Cube", panel_image);
        y += 22;
        createLabel("Speed:", 10, y, 60, 20, panel_image);
        playTime = new JSlider(JSlider.HORIZONTAL, 0, 500, 1);
        playTime.setBounds(60, y, 120, 30);
        y += 44;
        playTime.setMajorTickSpacing(20);
        playTime.setMinorTickSpacing(10);
        playTime.setValue(100);
        playTime.setPaintTicks(true);
        panel_image.add(playTime);
        panel_image.setBounds(0, y2, panel_width, y);

        y = y2 + y + 8;
        createLabel("Datei:", 10, y, 60, 20, panel_menu);
        btn_load_file = createButton("<html><body><u>l</u>aden</body></html>", 50, y, 80, 20, "Load File", panel_menu);
        //y += 22;
        btn_save_file = createButton("<html><body><u>s</u>peichern</body></html>", 140, y, 80, 20, "Save File", panel_menu);
        //y += 22;

        status = new JTextArea();
        panel_cube.add(status);
        status.setFont(new Font("Arial", Font.PLAIN, 12));
        status.setBounds(80, 14, 170, 50);
        status.setBackground(new Color(0xC8, 0xC8, 0xC8));

        ln.setBackground(bgcolor);
        lv.setBackground(bgcolor);
        lh.setBackground(bgcolor);
        ld.setBackground(bgcolor);
        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(ln);
        bgroup.add(lv);
        bgroup.add(lh);
        bgroup.add(ld);
        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new GridLayout(5, 1));
        radioPanel.add(ln);
        radioPanel.add(lv);
        radioPanel.add(lh);
        radioPanel.add(ld);
        radioPanel.setBackground(bgcolor);
        radioPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Linienabstände"));
        radioPanel.setBounds(0, 480, 200, 100);
        panel_menu.add(radioPanel);
        radioPanel.add(value);
        value.setPaintTicks(true);
        value.setMajorTickSpacing(10);

        ln.addActionListener(this);
        lv.addActionListener(this);
        lh.addActionListener(this);
        ld.addActionListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        value.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (((JSlider) e.getSource()).getValueIsAdjusting()) {
                    if (lh.isSelected()) {
                        xw = value.getValue();
                        repaint();
                    }
                    if (lv.isSelected()) {
                        yh = value.getValue();
                        repaint();
                    }
                    if (ld.isSelected()) {
                        dv = value.getValue();
                        repaint();
                    }
                }
                if (debug.getSelectedIndex() >= 4) {
                    System.out.println("xp=" + xp + ",yp=" + yp + ",xw=" + xw + ",yh=" + yh + ",dv=" + dv);
                }
            }
        });
        debug.addActionListener(this);
        debug.setFont(new Font("Arial", Font.PLAIN, 11));
        debug.setBackground(bgcolor);
        debug.setBounds(2, 580, 100, 20);
        panel_menu.add(debug);

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.ipadx = getSize().width;
        c.weighty = 1.0;
        JPanel panel_content = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.setColor(getBackground());
                g.fillRect(0, 0, getSize().width, getSize().height);
                //TimesRoman
                g.setFont(new Font("Courier", Font.PLAIN, 10));
                handlePoints(g, null);
            }
        };
        panel_content.setBackground(/*Color.RED*/bgcolor); //joerg
        gridbag.setConstraints(panel_content, c);
        add(panel_content);

        c.gridwidth = GridBagConstraints.REMAINDER; //end row
        c.ipadx = 270;
        c.ipady = getSize().height;
        c.weightx = 0.0;                //reset to the default
        c.gridwidth = 2;                //reset to the default
        c.gridheight = 2;
        c.weighty = 2.0;

        gridbag.setConstraints(panel_menu, c);
        add(panel_menu);
        registerShortcutAll(this.getComponents());

        pack();
        setVisible(true);

        setBounds(converToRectangle(conf.getProperty("frameRect", "0,0,860,800")));
        xp = Integer.parseInt(conf.getProperty("xp", "200"));
        yp = Integer.parseInt(conf.getProperty("yp", "5"));
        xw = Integer.parseInt(conf.getProperty("xw", "200"));
        yh = Integer.parseInt(conf.getProperty("yh", "160"));
        dv = Integer.parseInt(conf.getProperty("dv", "60"));
        selectedFile = conf.getProperty("workDir", ".");
    }

    public void registerShortcutAll(Component[] coms) {
        for (Component com : coms) {
            if (com instanceof JComponent && com != description) {
                registerShortcut((JComponent) com);
            }
        }
        for (Component com : coms) {
            if (com instanceof JComponent && com != description) {
                registerShortcutAll(((JComponent) com).getComponents());
            }
        }
    }

    public void registerShortcut(JComponent c) {
        c.getInputMap().put(KeyStroke.getKeyStroke("B"), "KeyEvent.VK_B");
        c.getActionMap().put("KeyEvent.VK_B", new AbstractAction("KeyEvent.VK_B") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                myInstance.actionPerformed(new ActionEvent(btn_save_image, evt.getID(), "Save Image"));
            }
        });

        c.getInputMap().put(KeyStroke.getKeyStroke("S"), "KeyEvent.VK_S");
        c.getActionMap().put("KeyEvent.VK_S", new AbstractAction("KeyEvent.VK_S") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                myInstance.actionPerformed(new ActionEvent(btn_save_file, evt.getID(), "Save File"));
            }
        });

        c.getInputMap().put(KeyStroke.getKeyStroke("L"), "KeyEvent.VK_L");
        c.getActionMap().put("KeyEvent.VK_L", new AbstractAction("KeyEvent.VK_L") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                myInstance.actionPerformed(new ActionEvent(btn_load_file, evt.getID(), "Load File"));
            }
        });
    }

    private JLabel createLabel(String caption, int x, int y, int w, int h, Container parent) {
        JLabel label = new JLabel(caption);
        parent.add(label);
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        label.setBounds(x, y, w, h);
        return label;
    }

    private JButton createButton(String caption, int x, int y, int w, int h, String cmd, Container parent) {
        JButton button = new JButton();
        parent.add(button);
        button.setText(caption);
        button.setFont(new Font("Arial", Font.PLAIN, 10));
        button.setBounds(x, y, w, h);
        button.addActionListener(this);
        button.setActionCommand(cmd);
        return button;
    }

    private LED handlePoints(Graphics g, Point pos) {
        setTitle(getClass().getName() + " " + getSize().width + "x" + getSize().height + " - " + version);

        if (version == 1) {
            int xv1 = 0;
            int yv1 = 0;
            int i = 0;
            for (int p = 0; p < count; p++) {
                int y1 = (yp + dv);
                for (int i1 = 0; i1 < count; i1++) {
                    int x = xp - xv1;
                    for (int i2 = 0; i2 < count; i2++) {
                        int y = y1 + yv1;
                        int r = baseRadius + (p * 7);

                        if (g != null) // entweder malen -> g != null
                        {
                            if (leds != null && leds[i] != null) {
                                g.setColor(((leds[i].state) ? leds[i].c_on : leds[i].c_off));
                                if (leds[i].state) {
                                    g.fillOval(x, y, r, r);
                                } else {
                                    g.drawOval(x, y, r, r);
                                }
                            } else {
                                g.setColor(Color.YELLOW);
                                g.drawOval(x, y, r, r);
                            }
                            if (p + 1 < count) {
                                // diagonale Linien
                                g.setColor(ld.isSelected() ? Color.YELLOW : Color.LIGHT_GRAY);
                                g.drawLine(x, y + r, (x - dv) + dv / 2, (y + r + dv) - dv / 2);
                            }

                            if (i2 + 1 < count) {
                                // horizontale Linien
                                g.setColor(lh.isSelected() ? Color.YELLOW : Color.LIGHT_GRAY);
                                g.drawLine(x + r + 10, y + r / 2, (x + xw) - 10, y + r / 2);
                            }
                            if (i1 + 1 < count) {
                                // vertikale Linien
                                g.setColor(lv.isSelected() ? Color.YELLOW : Color.LIGHT_GRAY);
                                g.drawLine(x + r / 2, y + r + 10, x + r / 2, (y + yh) - 10);
                            }
                            if (debug.getSelectedIndex() >= 1 && leds[i] != null) {
                                g.setColor(Color.GRAY);
                                switch (debug.getSelectedIndex()) {
                                    case 1:
                                        g.drawRect(x, y + r, 40, 20);
                                        g.drawString("" + i, x, y + r + 16);
                                        break;
                                    case 2:
                                        g.drawRect(x, y + r, 40, 20);
                                        g.drawString("N=" + leds[i].nr, x, y + r + 16);
                                        break;
                                    case 3:
                                        g.drawRect(x, y + r, 80, 20);
                                        g.drawString("N=" + leds[i].nr + ",I=" + i, x, y + r + 16);
                                        break;
                                    default:
                                        g.drawRect(x - 20, y + r, 160, 20);
                                        g.drawString("N=" + leds[i].nr + ",L=" + leds[i].level + ",I1=" + leds[i].index + ",I2=" + i, x - 16, y + r + 16);
                                        g.drawRect(x, y, r, r);
                                        break;
                                }
                            }
                        } else // oder wir suchen eine bestimmte LED, dann ist/muss post gesetzt
                        {
                            if (pos.x >= x && pos.x - r <= x && pos.y >= y && pos.y - r <= y) {
                                return leds[i];
                            }
                        }
                        i++;
                        x += xw;
                    }
                    y1 += yh;
                }
                xv1 += dv;
                yv1 += dv;
            }
        }

        if (version == 2) {
            int ew = 20;
            int eh = 14;
            int ystart = 5;
            int xstart = ((ew / 2) * count) + (ew);
            int x = xstart;
            int y = ystart;
            for (int i4 = 0, i3 = 0, i2 = 1, i = 0; i < count * count * count; i++, i3++) {
                if (i > 0 && (i % (count * count)) == 0) {
                    if (g != null) {
                        g.setColor(Color.black);
                        g.drawLine(xstart - (ew / 2) * count, y + eh + eh / 2, xstart + ew * count + ew, y + eh + eh / 2);
                    }
                    i2 = 1;
                    y += 2;
                }
                if (i > 0 && (i % count) == 0) {
                    x = xstart - ((ew / 2) * i2);
                    y += (eh + 10);
                    i2++;
                    i3 = (count * count - count) + i3;
                }
                if (i > 0 && (i % (count * count)) == 0) {
                    i4++;
                    i3 = count * i4;
                }
                if (g != null) // entweder malen -> g != null
                {
                    if (leds != null && leds[i3] != null) {
                        g.setColor(((leds[i3].state) ? leds[i3].c_on : leds[i3].c_off));
                        if (leds[i3].state) {
                            g.fillRect(x, y, ew, eh);
                        } else {
                            g.drawRect(x, y, ew, eh);
                        }
                    } else {
                        g.setColor(Color.YELLOW);
                        g.drawRect(x, y, ew, eh);
                    }
                    if (debug.getSelectedIndex() >= 1) {
                        g.setColor(Color.BLACK);
                        g.drawString("Nr" + leds[i3].nr, x, y + 10);
                    }
                } else // oder wir suchen eine bestimmte LED, dann ist/muss post gesetzt
                {
                    if (pos.x >= x && pos.x - ew <= x && pos.y >= y && pos.y - eh <= y) {
                        return leds[i3];
                    }
                }
                x += (ew + 10);
            }
        }

        if (version == 3) {
            int x_start = 10;
            int y_start = 10;
            int x = x_start;
            int y = y_start;
            int ew = 20;
            int eh = 30;
            for (int i = 0; i < ind.length; i++, x += (ew + 5)) {
                if (i > 0 && i % 25 == 0) {
                    x = x_start;
                    y += (eh + 5);
                }
                if (g != null && leds != null && leds[i] != null) // entweder malen -> g != null
                {
                    g.setColor(((leds[i].state) ? leds[i].c_on : leds[i].c_off));
                    if (leds[i].state) {
                        g.fillRect(x, y, ew, eh);
                    } else {
                        g.drawRect(x, y, ew, eh);
                    }
                    if (debug.getSelectedIndex() >= 1) {
                        g.setColor(Color.BLUE);
                        g.drawString("" + leds[i].nr, x + 5, y + 10);
                        g.drawString("" + leds[i].level, x + 5, y + 20);
                        g.drawString("" + leds[i].index, x + 5, y + 30);
                    }
                } else {
                    if (pos.x >= x && pos.x - ew <= x && pos.y >= y && pos.y - eh <= y) {
                        return leds[i];
                    }
                }
            }
        }
        return null;
    }

    public int searchTail(int level, int nr) {
        for (int i = 0; i < leds.length; i++) {
            if (leds[i].level == level && leds[i].nr == nr) {
                return i;
            }
        }
        System.err.println("tail mit level=" + level + " und nr=" + nr + " konnte nicht gefunden werden.");
        return -1;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (debug.getSelectedIndex() >= 1) {
            int my = e.getY() - getInsets().top;
            LED led = handlePoints(null, new Point(e.getX(), my));
            if (led != null) {
                status.setText("Nummer=" + led.nr + "\n" + "Level=" + led.level + "\n" + "Index=" + led.index);
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int my = e.getY() - getInsets().top;
        LED led = handlePoints(null, new Point(e.getX(), my));
        if (led != null) {
            led.state = !led.state;
            repaint();
            getCube();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (debug.getSelectedIndex() >= 6) {
            System.out.println("actionPerformed: " + e);
        }
        // Ebene drehen
        if (e.getActionCommand().startsWith("move")) {
            String direction = e.getActionCommand().substring(4, 5);
            if (direction.equals("R")) // Cube nach rechts
            {
                moveRight();
            }
            if (direction.equals("L")) // Cube nach links
            {
                moveLeft();
            }
            if (direction.equals("U")) // Cube nach oben
            {
                moveUp();
            }
            if (direction.equals("D")) // Cube nach unten
            {
                moveDown();
            }
        }
        // Text setzen
        if (e.getActionCommand().equals("TextByFont") && cubefont!=null) {
            int n = 0;
            for (LED led : leds) {
                if (led.level == 1) {
                    char c = (char) textcombo.getSelectedObjects()[0];
                    led.state = cubefont.getForLED(c, n);
                    n++;
                }
            }
            repaint();
        }
        if (e.getActionCommand().startsWith("<-")) {
            // Ebene level nach links
            moveLeft(Integer.parseInt(e.getActionCommand().substring(2, 3)), false);
        }
        if (e.getActionCommand().startsWith("->")) {
            // Ebene level nach rechts
            moveRight(Integer.parseInt(e.getActionCommand().substring(2, 3)), false);
        }

        // wenn Step -> oder Step <- gedrückt wurde
        if (e.getActionCommand().startsWith("Step") && curImage.getText().length() > 0) {
            int curIndex = Integer.parseInt(curImage.getText());
            String step = e.getActionCommand().substring(5, 7);
            if (step.equals("->") && curIndex < data.size()) {
                setCube(curIndex);
            }
            if (step.equals("<-") && curIndex > 1) {
                setCube(curIndex - 2);
            }
            repaint();
        }

        // Farbe einer Ebene setzen
        if (e.getActionCommand().startsWith("Color Ebene")) {
            int level = Integer.parseInt(e.getActionCommand().substring(12));
            Color color;
            switch (((JComboBox) e.getSource()).getSelectedIndex()) {
                case 0:
                    color = Color.RED;
                    break;
                case 1:
                    color = Color.YELLOW;
                    break;
                case 2:
                    color = Color.GREEN;
                    break;
                case 3:
                    color = Color.BLUE;
                    break;
                case 4:
                    color = Color.WHITE;
                    break;
                case 5:
                    color = Color.CYAN;
                    break;
                case 6:
                    color = Color.PINK;
                    break;
                default:
                    color = Color.LIGHT_GRAY;
                    break;
            }

            for (LED led : leds) {
                if (led.level == level) {
                    led.c_on = color;
                }
            }
            repaint();
        }

        // wenn "ganzer Cube an" gedrückt wurde
        if (e.getActionCommand().equals("Cube on")) {
            for (LED led : leds) {
                if (led != null) {
                    led.state = true;
                }
            }
            repaint();
        }
        // wenn "ganzer Cube aus" gedrückt wurde
        if (e.getActionCommand().equals("Cube off")) {
            for (LED led : leds) {
                if (led != null) {
                    led.state = false;
                }
            }
            repaint();
        }
        // wenn "Level*" gedrückt wurde
        if (e.getActionCommand().startsWith("Level")) {
            int level = Integer.parseInt(e.getActionCommand().substring(5));
            if (debug.getSelectedIndex() >= 2) {
                System.out.println("Ebene " + level + " invertieren");
            }
            for (LED led : leds) {
                if (led != null && led.level == level) {
                    led.state= !led.state;
                }
            }
            repaint();
        }
        // wenn "Datei laden" gedrückt wurde
        if (e.getActionCommand().equals("Load File") && e.getSource() == btn_load_file) {
            showFileDialogOpen();
            repaint();
        }
        // wenn "Datei speichern" gedrückt wurde
        if (e.getActionCommand().equals("Save File") && e.getSource() == btn_save_file) {
            showFileDialogSave();
            repaint();
        }
        // wenn "aktuelles Bild speichern" gedrückt wurde
        if (e.getActionCommand().equals("Save Image") && e.getSource() == btn_save_image) {
            saveImage();
            repaint();
        }
        // wenn "vorheriges Bild anschauen" gedrückt wurde
        if (e.getActionCommand().equals("Show PrevImage")) {
            showPrevImage();
            repaint();
        }
        // wenn "Play Cube" gedrückt wurde
        if (e.getActionCommand().equals("Play Cube")) {
            if (data.size() > 0) {
                if (isthreadAlive) {
                    isthreadAlive = false;
                    ((JButton) e.getSource()).setText("Play Cube");
                } else {
                    isthreadAlive = true;
                    playThread = new Thread(myInstance);
                    playThread.start();
                    ((JButton) e.getSource()).setText("Stop Play");
                }
            }
        }
        if (e.getSource() == debug) {
            repaint();
        }
        if (e.getSource() == lh && lh.isSelected()) {
            value.setMinimum(60);
            value.setMinorTickSpacing(1);
            value.setMaximum(300);
            value.setValue(xw);
        }
        if (e.getSource() == lv && lv.isSelected()) {
            value.setMinimum(10);
            value.setMinorTickSpacing(1);
            value.setMaximum(300);
            value.setValue(yh);
        }
        if (e.getSource() == ld && ld.isSelected()) {
            value.setMinimum(10);
            value.setMinorTickSpacing(1);
            value.setMaximum(100);
            value.setValue(dv);
        }
        if (ln.isSelected()) {
            value.setEnabled(false);
        } else {
            value.setEnabled(true);
        }
        repaint();
    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.out.println("Einstellungen werden gespeichert");
            conf.setProperty("frameRect", "" + getBounds().x + "," + getBounds().y + "," + getBounds().width + "," + getSize().height);
            conf.setProperty("workDir", new File(selectedFile).getPath());
            conf.setProperty("xp", "" + xp);
            conf.setProperty("yp", "" + yp);
            conf.setProperty("xw", "" + xw);
            conf.setProperty("yh", "" + yh);
            conf.setProperty("dv", "" + dv);

            try {
                FileOutputStream fout = new FileOutputStream(confFileName);
                conf.store(fout, getClass().getName() + " Einstellungen");
                fout.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.println("Exit");
            System.exit(0);
        }
    }

    @Override
    public void run() {
        try {
            int i = 0;
            while (isthreadAlive) {
                setCube(i);
                i++;
                if (i >= data.size()) {
                    i = 0;
                }
                Thread.sleep(playTime.getMaximum() - playTime.getValue());
            }
        } catch (Exception iex) {
            iex.printStackTrace();
        }
    }

    private void showFileDialogOpen() {
        final JFileChooser chooser = new JFileChooser("Verzeichnis wählen");
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Text-Files(*.txt)";
            }
        });
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setCurrentDirectory(new File(selectedFile));
        chooser.setVisible(true);
        final int result = chooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            selectedFile = inputVerzFile.getPath();
            System.out.println("Lese Datei: " + selectedFile);
            readFile(selectedFile);
            playCube.setEnabled(true);
        }
        chooser.setVisible(false);
    }

    private void showFileDialogSave() {
        final JFileChooser chooser = new JFileChooser("Datei abspeichern");
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Text-Files(*.txt)";
            }
        });
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setCurrentDirectory(new File(selectedFile));
        chooser.setVisible(true);
        final int result = chooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File inputVerzFile = chooser.getSelectedFile();
            selectedFile = inputVerzFile.getPath();
            System.out.println("Schreibe Datei: " + selectedFile);
            saveFile(selectedFile);
        }
        chooser.setVisible(false);
    }

    protected char getValueAt(String line, int index, int[] valMap) {
        // gibt das Zeichen an der bestimmten Position "index" aus dem String "line" zur�ck
        // abh�ngig von der Mapping-Tabelle
        //
        int lineIndex = valMap[index];
        char ret = line.charAt(lineIndex);
        if (debug.getSelectedIndex() >= 3) {
            System.out.println("# call getValueAt: index=" + index + ", lineIndex=" + lineIndex + ":" + ret);
        }
        return ret;
    }

    protected void setCube(int index) {
        String line = (String) data.elementAt(index);
        if (debug.getSelectedIndex() >= 1) {
            System.out.println("# call setCube(" + index + "): " + line);
        }
        curImage.setText("" + (index + 1));
        if (line.contains("'")) {
            description.setText(line.substring(line.indexOf("'") + 1));
            line = line.substring(0, line.indexOf("'"));
        } else {
            description.setText("");
        }
        for (int i = 0; i < ind.length; i++) {
            leds[ind[i]].state = (getValueAt(line, i, valMap) == '1');
        }
        repaint();
    }

    protected String getSaveLine(int index) {
        System.err.println("Funktion <getSaveLine> nicht implementiert in " + getClass().getName());
        return null;
    }

    protected void saveFile(String fileName) {
        setTitle(getClass().getName() + " " + fileName);

        // aktuellen Zustand von Cube speichern
        //String dataStr = getCube();
        //int index = Integer.parseInt(curImage.getText())-1;
        //data.removeElementAt(index);
        //data.insertElementAt(dataStr,index);
        //saveImage.setText(""+data.size());
        int size = Integer.parseInt(saveImage.getText());
        if (debug.getSelectedIndex() >= 1) {
            System.out.println("# call saveFile: size(step 1)=" + size);
            System.out.println("# call saveFile: Datei " + fileName + ((new File(fileName).exists()) ? " ist schon vorhanden und wird �berschrieben" : " wird neu erstellt"));
        }
        if (data.isEmpty()) {
            System.err.println("Keine Daten zum Speichern da.");
            return;
        }
        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            fout.write(("Data " + size + "% ' " + size + " Bilder in der Animation\r\n\r\n").getBytes());
            for (int i = 0; i < size; i++) {
                String line = getSaveLine(i);
                if (debug.getSelectedIndex() >= 1) {
                    System.out.println("# call getSaveLine (" + i + ")=" + line);
                }
                fout.write(line.getBytes());
                fout.write(("\r\n").getBytes());
            }
            fout.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (debug.getSelectedIndex() >= 1) {
            System.out.println("# call saveFile: fileSize=" + new File(fileName).length());
        }
    }

    protected void readFile(String fileName) {
        setTitle(getClass().getName() + " " + fileName);
    }

    protected void init(int[] ind_, int[] valMap_, String blankLine_) {
        init(ind_, valMap_, blankLine_,null);
    }
    protected void init(int[] ind_, int[] valMap_, String blankLine_, CubeFont font) {
        cubefont=font;
        if (cubefont != null) {
            for (Character c : cubefont.getAvailableChars()) {
                textcombo.addItem(c);
            }
        }
        ind = ind_;
        valMap = valMap_;
        blankLine = blankLine_;

        leds = new LED[count * count * count];
        for (int i = 0, l = 1, nr = 1; i < ind.length; i++) {
            if (nr > count * count) {
                l++;
                nr = 1;
            }
            leds[ind[i]] = new LED(l, nr++, i);
        }
    }

    protected void showPrevImage() {
        if (curImage.getText().length() > 0 && Integer.parseInt(curImage.getText()) > 1) {
            setCube(Integer.parseInt(curImage.getText()) - 2);
        }
    }

    protected String getCube() {
    // die aktuelle DataLine, entsprechend den gesetzten LED's zurückgeben

        // die aktuelle (letzte) Zeile
        String curLine = blankLine;
        if (data.isEmpty()) {
            data.addElement(blankLine);
            curImage.setText("1");
        } else {
            int index = data.size() - 1;
            if (debug.getSelectedIndex() >= 1) {
                System.out.println("# call getCube: index=" + index);
            }
            curLine = (String) data.elementAt(index);
            if (debug.getSelectedIndex() >= 1) {
                System.out.println("# call getCube: curLine=" + curLine);
            }
        }

        // den String "curLine" nun ändern
        byte[] tmp = curLine.getBytes();
        for (int i = 0; i < ind.length; i++) {
            tmp[valMap[i]] = (byte) (leds[ind[i]].state ? '1' : '0');
        }

        String ret = new String(tmp);
        if (curLine.contains("'")) {
            ret += ("'" + curLine.substring(curLine.indexOf("'")));
        }
        if (debug.getSelectedIndex() >= 2) {
            System.out.println("# call getCube ret: " + ret);
        }

        return ret;
    }

    protected void saveImage() {
        if (debug.getSelectedIndex() >= 1) {
            System.out.println("# call saveImage");
            System.out.println("# call saveImage: data.size(step 1)=" + data.size());
        }
        // wenn daten mit curIndex da sind dann daten speichern und setCube mit curIndex+1 machen
        // sonst neues element im data vector erzeugen und daten dort ablegen dann setCube mit curIndex+1 machen
        String dataStr = getCube();
        if (debug.getSelectedIndex() >= 1) {
            System.out.println("# call saveImage: dataStr=" + dataStr);
        }
        if (dataStr.contains("'")) {
            dataStr = dataStr.substring(0, dataStr.indexOf("'"));
        }
        if (description.getText().length() > 0) {
            dataStr += ("'" + description.getText());
        }
        int index = 0;
        if (data.isEmpty()) {
            //description.setText(""); wird in setCube eh gemacht
            data.addElement(dataStr);
        } else {
            index = Integer.parseInt(curImage.getText()) - 1;
            data.removeElementAt(index);
            data.insertElementAt(dataStr, index);
        }
        if (dataStr.contains("'")) {
            dataStr = dataStr.substring(0, dataStr.indexOf("'"));
        }
        if ((index + 1) >= data.size()) {
            data.addElement(dataStr);
        }
        index++;

        setCube(index);
        curImage.setText("" + (index + 1));
        saveImage.setText("" + (data.size() - 1));
        if (debug.getSelectedIndex() >= 1) {
            System.out.println("# call saveImage: data.size(step 2)=" + data.size());
        }
    }

    protected void moveUp() {
        moveUp(false);
    }

    protected void moveUp(boolean clipping) {
        if (debug.getSelectedIndex() >= 1) {
            System.out.println("# call <moveUp>  in CubePanel");
        }
        for (int level = count - 1; level >= 1; level--) {
            for (LED led : leds) {
                if (led != null && led.level == level) {
                    int i2 = searchTail(led.level + 1, led.nr);
                    boolean state1 = led.state;
                    boolean state2 = leds[i2].state;
                    if (clipping && leds[i2].level == count && state2) {
                        state1 = false;
                        state2 = false;
                    }
                    leds[i2].state = state1;
                    led.state = state2;
                }
            }
        }
        repaint();
    }

    protected void moveDown() {
        moveDown(false);
    }

    protected void moveDown(boolean clipping) {
        if (debug.getSelectedIndex() >= 1) {
            System.out.println("# call <moveDown> in CubePanel");
        }
        for (int level = 2; level <= count; level++) {
            for (LED led : leds) {
                if (led != null && led.level == level) {
                    int i2 = searchTail(led.level - 1, led.nr);
                    boolean state1 = led.state;
                    boolean state2 = leds[i2].state;
                    if (clipping && led.level - 1 == 1 && state2) {
                        state1 = false;
                        state2 = false;
                    }
                    leds[i2].state = state1;
                    led.state = state2;
                }
            }
        }
        repaint();
    }

    protected void moveLeft(int level, boolean clipping) {
        for (int nr = 2; nr <= count * count; nr++) {
            int i1 = searchTail(level, nr);
            int i2 = searchTail(level, nr - 1);
            boolean state1 = leds[i1].state;
            boolean state2 = leds[i2].state;
            leds[i1].state = state2;
            leds[i2].state = state1;
        }
        if (clipping) {
            for (int l = 1; l <= count; l++) {
                int i = searchTail(l, count * count);
                if (i != -1) {
                    leds[i].state = false;
                }
            }
        }
        repaint();
    }

    protected void moveLeft(boolean clipping) {
        for (int level = 1; level <= count; level++) {
            moveLeft(level, clipping);
        }
    }

    protected void moveLeft() {
        moveLeft(false);
    }

    protected void moveRight(int level, boolean clipping) {
        for (int nr = count * count; nr > 1; nr--) {
            int i1 = searchTail(level, nr);
            int i2 = searchTail(level, nr - 1);
            boolean state1 = leds[i1].state;
            boolean state2 = leds[i2].state;
            leds[i1].state = state2;
            leds[i2].state = state1;
        }
        if (clipping) {
            for (int l = 1; l <= count; l++) {
                int i = searchTail(l, 1);
                if (i != -1) {
                    leds[i].state = false;
                }
            }
        }
        repaint();
    }

    protected void moveRight(boolean clipping) {
        for (int level = 1; level <= count; level++) {
            moveRight(level, clipping);
        }
    }

    protected void moveRight() {
        moveRight(false);
    }

    protected void hideComponent(String actionName) {
        hideComponent(null, actionName);
    }

    protected void hideComponent(Component c[], String actionName) {
        if (c == null) {
            c = this.getComponents();
        }
        JComponent ret = null;
        for (Component c1 : c) {
            if (c1 instanceof JButton) {
                if (((JButton) c1).getActionCommand().equals(actionName)) {
                    c1.setEnabled(false);
                    return;
                }
            }
            if (c1 instanceof JComponent) {
                hideComponent(((JComponent) c1).getComponents(), actionName);
            }
        }
    }

    protected void hideComponents(String s[]) {
        for (String item : s) {
            hideComponent(item);
        }
    }

}
