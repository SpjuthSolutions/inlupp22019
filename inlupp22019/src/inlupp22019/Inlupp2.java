 /*
  * To change this license header, choose License Headers in Project Properties.
  * To change this template file, choose Tools | Templates
  * and open the template in the editor.
  */
 package inlupp22019;
 import javax.swing.*;
 import javax.swing.border.LineBorder;
 import javax.swing.event.ListSelectionEvent;
 import javax.swing.event.ListSelectionListener;
 import javax.swing.filechooser.FileNameExtensionFilter;

 import java.awt.*;
 import java.awt.event.*;
 import java.io.*;
 import java.util.*;
 import java.util.List;
 import javax.swing.*;
 import java.awt.*;


 public class Inlupp2 extends JFrame {

     private JRadioButton namedButton, describedButton;
     private MapMouseListener nbmouseListener = new MapMouseListener();
     //--------------------------------collections----------------------------
     private Map<inlupp22019.Position, Place> places = new HashMap<Position, Place>();
     private Map<Integer, Collection<Place>> categoryMap = new HashMap<Integer, Collection<Place>>();
     private Map<Integer, Position> posMap = new HashMap<>();
     private Map<Integer, Place> coordinateMap = new HashMap<Integer, Place>();
     private Map<String, List<Place>> namedMap = new HashMap<>();
     private HashSet<Place> markedSet = new HashSet<>();
     //----------------------------------
     private MarkedListener getMarkedListener = new MarkedListener();

     private JScrollPane scrollPane = new JScrollPane();
     private JTextField searchField;
     private JMenuBar menuBar;
     private JMenuItem menuItem;
     private JMenu menu;
     private JFileChooser jfc = new JFileChooser();
     private MapHolder mh = null;
     private JScrollPane scroll = null;
     private JButton newButton;
     private JButton hideCatButton;
     private String[] categories = { "bus", "underground", "train" };
     private JList<String> categoriesList = new JList<String>(categories);
     private boolean changed;



     public Inlupp2() {

         changed = false;

         HashSet<Place> placesHash = new HashSet<Place>();

         JPanel south = new JPanel();
         add(south, BorderLayout.SOUTH);

         JPanel east = new JPanel();
         east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
         add(east, BorderLayout.EAST);

         menuBar = new JMenuBar();
         menu = new JMenu("Archive");
         add(menuBar, BorderLayout.NORTH);

         menu.setMnemonic(KeyEvent.VK_A);
         menu.getAccessibleContext().setAccessibleDescription("");
         menuBar.add(menu);

         menuItem = new JMenuItem(" New map", KeyEvent.VK_T);
         menuItem.addActionListener(new NewMapListener());
         menu.add(menuItem);

         menuItem = new JMenuItem(" Load Places");
         menuItem.setMnemonic(KeyEvent.VK_B);
         menu.add(menuItem);
         menuItem.addActionListener(new LoadListener());

         menuItem = new JMenuItem(" Save");
         menuItem.setMnemonic(KeyEvent.VK_B);
         menu.add(menuItem);
         menuItem.addActionListener(new SaveListener());

         menuItem = new JMenuItem(" Exit");
         menuItem.setMnemonic(KeyEvent.VK_B);
         menu.add(menuItem);
         menuItem.addActionListener(new ExitListener());

         scrollPane = new JScrollPane(categoriesList);

         east.add(scrollPane);
         categoriesList.addListSelectionListener(new ListListener());

         hideCatButton = new JButton("Hide category");
         hideCatButton.addActionListener(new HcListener());
         east.add(hideCatButton);

         newButton = new JButton("New");
         newButton.addActionListener(new NewButtonListener());
         south.add(newButton);

         namedButton = new JRadioButton("Named", true);
         south.add(namedButton);
         describedButton = new JRadioButton("Desrcibed");
         south.add(describedButton);
         ButtonGroup bg = new ButtonGroup();
         bg.add(namedButton);
         bg.add(describedButton);

         south.add(new JLabel("Search"));
         searchField = new JTextField(10);
         south.add(searchField);

         JButton searchButton = new JButton("Search");
         south.add(searchButton);
         searchButton.addActionListener(new SearchButtonListener());

         JButton hideButton = new JButton("Hide");
         south.add(hideButton);
         hideButton.addActionListener(new HideButtonListener());

         JButton removeButton = new JButton("Remove");
         south.add(removeButton);
         removeButton.addActionListener(new RemoveButtonListener());

         JButton cordinatesButton = new JButton("Cordinates");
         south.add(cordinatesButton);
         cordinatesButton.addActionListener(new CoListener());

         addWindowListener(new WindowAdapter() {
             public void windowClosing(WindowEvent e) {
                 if (changed) {
                     int exitSvar = JOptionPane.showConfirmDialog(Inlupp2.this,
                             "Unsaved changes, " + "do you want to quit anyway?", "Exit", JOptionPane.OK_CANCEL_OPTION);
                     if (exitSvar == JOptionPane.OK_OPTION)
                         System.exit(0);
                 } else {
                     System.exit(0);
                 }

             }
         });



         setSize(800, 600);
         setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
         setVisible(true);
         setLocationRelativeTo(null);
     }

     public class MapHolder extends JPanel {

         ImageIcon theMap;

         public MapHolder(String fileName) {
             theMap = new ImageIcon(fileName);
             int w = theMap.getIconWidth();
             int h = theMap.getIconHeight();

             setPreferredSize(new Dimension(w, h));
             setMaximumSize(new Dimension(w, h));
             setMinimumSize(new Dimension(w, h));

         }

         protected void paintComponent(Graphics g) {
             super.paintComponent(g);
             g.drawImage(theMap.getImage(), 0, 0, this);
         }

     }

     private void clearPlace() {
         places.clear();
         categoryMap.clear();
         namedMap.clear();
         coordinateMap.clear();
         posMap.clear();
         markedSet.clear();
         categoriesList.clearSelection();

     }


     class NamedPane extends JOptionPane {

         private JTextField nameField;

         JPanel namedPane;

         public NamedPane() {

             setLayout(new FlowLayout());

             nameField = new JTextField(15);
             namedPane = new JPanel();
             namedPane.setLayout(new BoxLayout(namedPane, BoxLayout.PAGE_AXIS));
             JPanel line1 = new JPanel();

             line1.add(nameField);
             namedPane.add(line1);

             setSize(400, 200);
             setDefaultCloseOperation(EXIT_ON_CLOSE);
             setVisible(true);
             setLocationRelativeTo(null);
             JOptionPane.showMessageDialog(null, namedPane, "Add named place", JOptionPane.QUESTION_MESSAGE);

         }

     }


     // Skriver ut formulâr fàr namgiven plats

     class DescribedPanel extends JPanel {

         private JTextField nameField = new JTextField(10);
         private JTextField describeField = new JTextField(5);

         JPanel describedPanel;
         JPanel line1;
         JPanel line2;

         public DescribedPanel() {

             describedPanel = new JPanel();
             line1 = new JPanel();
             line2 = new JPanel();

             line1.add(new JLabel("Name: "));
             line1.add(nameField);
             describedPanel.add(line1);

             line2.add(new JLabel("Description: "));
             line2.add(describeField);

             describedPanel.add(line2);

             setSize(400, 200);
             setDefaultCloseOperation(EXIT_ON_CLOSE);
             setVisible(true);
             setLocationRelativeTo(null);
             JOptionPane.showMessageDialog(null, describedPanel, "Add described place", JOptionPane.QUESTION_MESSAGE);



         }

     }

     private void addPlaceMaps(Place place) {
         mh.add(place);
         place.addMouseListener(new MarkedListener());
         places.put(place.getPos(), place);

         if (namedMap.containsKey(place.getName())) {
             namedMap.get(place.getName()).add(place);
         } else {
             namedMap.put(place.getName(), new ArrayList<>());
             namedMap.get(place.getName()).add(place);
         }

         if (categoryMap.containsKey(place.getCategory())) {
             categoryMap.get(place.getCategory()).add(place);
         } else {
             categoryMap.put(place.getCategory(), new ArrayList<>());
             categoryMap.get(place.getCategory()).add(place);
         }
     }

     private void addDescribedToMaps(DescribedPlace p) {
         places.put(p.getPos(), p);
         Integer mapKey = Integer.valueOf(p.getPos().hashCode());
         coordinateMap.put(mapKey, p);

         Collection<Place> sameCategory = categoryMap.get(p.getCategory());
         if (sameCategory == null) {
             sameCategory = new ArrayList<Place>();
             categoryMap.put(p.getCategory(), sameCategory);

         }

         sameCategory.add(p);

         List<Place> sameName = namedMap.get(p.getName());
         if (sameName == null) {
             sameName = new ArrayList<Place>();
             namedMap.put(p.getName(), sameName);

         }

         sameName.add(p);

     }

     private void addNamedToMaps(NamedPlace NamedPlace) {
         places.put(NamedPlace.getPos(), NamedPlace);

         Integer mapKey = Integer.valueOf(NamedPlace.getPos().hashCode());
         coordinateMap.put(mapKey, NamedPlace);

         Collection<Place> sameCategory = categoryMap.get(NamedPlace.getCategory());
         if (sameCategory == null) {
             sameCategory = new ArrayList<Place>();
             categoryMap.put(NamedPlace.getCategory(), sameCategory);
         }
         sameCategory.add(NamedPlace);

         List<Place> sameName = namedMap.get(NamedPlace.getName());
         if (sameName == null) {
             sameName = new ArrayList<Place>();
             namedMap.put(NamedPlace.getName(), sameName);
         }
         sameName.add(NamedPlace);

     }

     // Listeners------------------------------------------------------------->>>

     class ListListener implements ListSelectionListener {
         public void valueChanged(ListSelectionEvent lev) {
             if (!lev.getValueIsAdjusting()) {
                 int category = categoriesList.getSelectedIndex();
                 Collection<Place> sameCategory = categoryMap.get(category);
                 if (sameCategory != null)
                     for (Place p : sameCategory) {
                         p.setVisible(true);
                     }
             }
         }
     }

     class HcListener implements ActionListener {
         public void actionPerformed(ActionEvent ave) {

             int cat = categoriesList.getSelectedIndex();
             Collection<Place> sameCategory = categoryMap.get(cat);
             places.forEach((k, v) -> v.setVisible(true));
             if (sameCategory != null) {
                 if (cat != 3) {
                     for (Place p : sameCategory) {
                         p.setVisible(false);
                         categoriesList.clearSelection();
                     }
                 }
             }
         }
     }



     class MapMouseListener extends MouseAdapter { //Skapar ny plats
         @Override
         public void mouseClicked(MouseEvent mev) {
             if (mh != null) {
                 int x = mev.getX();

                 int y = mev.getY();

                 boolean whatPlace;
                 Position po = new Position(x, y);
                 System.out.println(po);

                 if (places.containsKey(po)) {
                     JOptionPane.showMessageDialog(null,"There is already a place listed on this position.",
                             "Place infomation ", JOptionPane.INFORMATION_MESSAGE);
                 } else {

                     int posX = x;
                     posMap.put(posX, po);
                     if (namedButton.isSelected() == true) {
                         NamedPane np = new NamedPane();
                         whatPlace = true;
                         Place p = new NamedPlace(np.nameField.getText(), categoriesList.getSelectedIndex(), x, y, whatPlace);
                         p.addMouseListener(getMarkedListener);
                         mh.add(p);
                         places.put(po, p);
                         addNamedToMaps((NamedPlace) p);
                         List<Place> placeName = namedMap.get(p.getName());
                         if (placeName == null) {
                             placeName = new ArrayList<Place>();

                         }
                         changed = true;

                     } else {
                         DescribedPanel dp = new DescribedPanel();
                         whatPlace = true;
                         Place p = new DescribedPlace(dp.nameField.getText(), categoriesList.getSelectedIndex(),
                                 dp.describeField.getText(), x, y, whatPlace);
                         mh.add(p);
                         p.addMouseListener(getMarkedListener);
                         places.put(po, p);
                         addDescribedToMaps((DescribedPlace) p);
                         changed = true;

                     }

                     mh.validate();
                     mh.repaint();
                     mh.removeMouseListener(nbmouseListener);
                     mh.setCursor(Cursor.getDefaultCursor());
                     newButton.setEnabled(true);

                 }

             } else {
                 return;

             }

         }

     }






     class NewMapListener implements ActionListener {
         public void actionPerformed(ActionEvent ave) {

             if (changed) {
                 int confirm = JOptionPane.showConfirmDialog(Inlupp2.this,
                         "Unsaved changes, " + "do you want to open a new map anyway?", "New map",
                         JOptionPane.OK_CANCEL_OPTION);

                 if (confirm != JOptionPane.OK_OPTION)
                     return;
             }

             int svar = jfc.showOpenDialog(Inlupp2.this);
             if (svar != JFileChooser.APPROVE_OPTION)
                 return;
             File fil = jfc.getSelectedFile();
             String path = fil.getAbsolutePath();



             if (mh != null)
                 remove(scroll);
             mh = new MapHolder(path);
             scroll = new JScrollPane(mh);
             add(scroll, BorderLayout.CENTER);
             pack();
             validate();
             repaint();


         }
     }



     class NewButtonListener implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent ave) {


             if (mh != null) {
                 mh.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                 mh.addMouseListener(nbmouseListener);
                 newButton.setEnabled(false);

                 // Aktiverar "ny" knappen med actionlistener

             } else {
                 JOptionPane.showInternalMessageDialog(Inlupp2.this,
                         "You need to load a new map before you can add places", "Error", HEIGHT);
             }
         }
     }

     class MarkedListener extends MouseAdapter {
         @Override
         public void mouseClicked(MouseEvent mev) {
             Place p = (Place) mev.getSource();

             if (p.isMarked == false) {
                 markedSet.add(p);
                 p.isMarked = true;
                 p.update(p.getGraphics());
                 repaint();
                 System.out.println(markedSet.toString());
             } else if (p.isMarked == true) {
                 p.isMarked = false;
                 repaint();
                 markedSet.remove(p);
                 System.out.println(markedSet.toString());
             }



             // Right clicked if statement
             if (mev.getButton() == MouseEvent.BUTTON3) {

                 for (Place pa : places.values()) {
                     if (pa.equals(p)) {

                         if (pa instanceof DescribedPlace) {

                             JOptionPane.showMessageDialog(null,
                                     "Name: " + pa.getName() + "{" + pa.getCoord() + "}. \n"
                                             + "Description: " + ((DescribedPlace) pa).getDescription(),
                                     "Place infomation ", JOptionPane.INFORMATION_MESSAGE);


                         } else if (pa instanceof NamedPlace) {

                             JOptionPane.showMessageDialog(null, pa.getName() + "{" + pa.getCoord() + "}",
                                     "Place infomation ", JOptionPane.INFORMATION_MESSAGE);


                         }
                     }
                 }
             }

         }
     }

     class SaveListener implements ActionListener { // Sparar platser
         @Override
         public void actionPerformed(ActionEvent ave) {
             try {
                 String str = System.getProperty(".");
                 JFileChooser fileChooser = new JFileChooser(str);
                 int file = fileChooser.showSaveDialog(Inlupp2.this);
                 if (file != JFileChooser.APPROVE_OPTION) {
                     return;
                 }
                 File selected = fileChooser.getSelectedFile();
                 FileWriter outFile = new FileWriter(selected + ".places");
                 PrintWriter out = new PrintWriter(outFile);
                 for (Place p : places.values()) {
                     if (p instanceof DescribedPlace) {
                         out.println("Described" + "," + p.getCatString() + "," + p.getX() + "," + p.getY() + ","
                                 + p.getName() + "," + ((DescribedPlace) p).getDescription());
                     } else {
                         out.println(
                                 "Named" + "," + p.getCatString() + "," + p.getX() + "," + p.getY() + "," + p.getName());
                     }
                 }
                 changed = false;
                 out.close();
                 outFile.close();
             } catch (FileNotFoundException e) {
                 JOptionPane.showMessageDialog(null, "Can't open the file.");
             } catch (IOException e) {
                 JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
             }
         }
     }

     class CoListener implements ActionListener {
         public void actionPerformed(ActionEvent ave) {


             try {
                 CoPane c = new CoPane();
                 int answer = JOptionPane.showConfirmDialog(Inlupp2.this, c, "Input Coordinates:",
                         JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                 if (answer != JOptionPane.OK_OPTION)
                     return;
                 else if (answer == JOptionPane.OK_OPTION) {

                 }


                 int x = c.getXCo();
                 int y = c.getYCo();

                 TestPosition tp = new TestPosition(x, y);

                 posMap.forEach((k,v)->{

                     if (tp.getX() == v.getX()){

                         if (tp.getY() == v.getY()) {

                             places.forEach((key,value)->{

                                 if (tp.toString().equals(key.toString()))

                                     markedSet.add(value);
                                 value.isMarked = true;
                                 value.repaint();

                                 if (value instanceof DescribedPlace) {

                                     JOptionPane.showMessageDialog(null,
                                             "Name: " + value.getName() + "{" + value.getCoord() + "}. \n"
                                                     + "Description: " + ((DescribedPlace) value).getDescription(),
                                             "Place infomation ", JOptionPane.INFORMATION_MESSAGE);


                                 } else if (value instanceof NamedPlace) {

                                     JOptionPane.showMessageDialog(null, value.getName() + "{" + value.getCoord() + "}",
                                             "Place infomation ", JOptionPane.INFORMATION_MESSAGE);
                                 }



                             });

                         }

                     } else {
                         JOptionPane.showMessageDialog(null, "There is no place on the given coordinates.",
                                 "Place infomation ", JOptionPane.INFORMATION_MESSAGE);
                     }

                 });




             } catch (NumberFormatException e) {
                 JOptionPane.showMessageDialog(Inlupp2.this, "Wrong input! Must be numerical values.", "Error!",
                         JOptionPane.ERROR_MESSAGE);

             }




         }

     }



     class CoPane extends JPanel {
         private JTextField xField = new JTextField(3);
         private JTextField yField = new JTextField(3);

         public CoPane() {
             setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
             JPanel r1 = new JPanel();
             r1.add(new JLabel("x: "));
             r1.add(xField);
             add(r1);
             JPanel r2 = new JPanel();
             r2.add(new JLabel("y:"));
             r2.add(yField);
             add(r2);
         }

         public int getXCo() {
             return Integer.parseInt(xField.getText());
         }

         public int getYCo() {
             return Integer.parseInt(yField.getText());
         }

     }



     class LoadListener implements ActionListener {
         public void actionPerformed(ActionEvent ave) {
             int answer = jfc.showOpenDialog(Inlupp2.this);
             if (answer != JFileChooser.APPROVE_OPTION)
                 return;
             File f = jfc.getSelectedFile();
             String filename = f.getAbsolutePath();
             if (changed) {
                 int confirm = JOptionPane.showConfirmDialog(Inlupp2.this,
                         "Unsaved changes, " + "do you really want to load new places?", "Load places",
                         JOptionPane.OK_CANCEL_OPTION);
                 if (confirm != JOptionPane.OK_OPTION)
                     return;
             }
             try {
                 mh.removeAll();
                 clearPlace();
                 FileReader fileIn = new FileReader(filename);
                 BufferedReader in = new BufferedReader(fileIn);
                 String line;
                 while ((line = in.readLine()) != null) {
                     boolean typeOfPlace;
                     String[] tokens = line.split(",");
                     String namedOrDescribed = tokens[0];
                     if (namedOrDescribed.equals("Named")) {
                         typeOfPlace = true;
                     } else {
                         typeOfPlace = false;
                     }
                     int category = categoryImport(tokens[1]);
                     int x = Integer.parseInt(tokens[2]);
                     int y = Integer.parseInt(tokens[3]);
                     Position pos = new Position(x, y);
                     int posKey = x;
                     posMap.put(posKey, pos);
                     String description = null;
                     String name = tokens[4];
                     if (!typeOfPlace) {
                         description = tokens[5];
                         Place p = new DescribedPlace(name, category, description, x, y, typeOfPlace);
                         p.addMouseListener(getMarkedListener);
                         mh.add(p);
                         mh.validate();
                         mh.repaint();
                         addDescribedToMaps((DescribedPlace) p);
                         p.isMarked = false;//-------------------------Ska det här va kvar?
                         changed = true;

                     } else {

                         NamedPlace np = new NamedPlace(name, category, x, y, typeOfPlace);
                         np.addMouseListener(getMarkedListener);
                         mh.add(np);
                         mh.validate();
                         mh.repaint();
                         addNamedToMaps(np);

                     }
                 }
                 in.close();
                 fileIn.close();
                 changed = false;
             } catch (FileNotFoundException e) {
                 JOptionPane.showMessageDialog(Inlupp2.this, "Can not open file!");
             } catch (IOException e) {
                 JOptionPane.showMessageDialog(Inlupp2.this, "Error!");
             }
         }

         private int categoryImport(String category) {
             if (category.equals("Bus")) {
                 return 0;
             } else if (category.equals("Underground")) {
                 return 1;
             } else if (category.equals("Train")) {
                 return 2;
             } else
                 return 3;
         }
     }

     class SearchButtonListener implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent ave) {
             if (!searchField.getText().equals("")) {

                 String name = searchField.getText();
                 Iterator<Place> itr = markedSet.iterator();
                 while (itr.hasNext()) {
                     Place p = (Place) itr.next();
                     p.setBorder(null);
                 }
                 markedSet.clear();
                 if (namedMap.get(name) != null) {
                     for (Place p : namedMap.get(name)) {

                         p.setVisible(true);
                         p.isMarked = true;
                         markedSet.add(p);
                     }
                 }
             }
         }
     }

     class RemoveButtonListener implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent ave) {
             Iterator<Place> itr = markedSet.iterator();

             while (itr.hasNext()) {
                 Place p = (Place) itr.next();
                 places.remove(p.getPos());
                 namedMap.get(p.getName()).remove(p);

                 if (namedMap.get(p.getName()).isEmpty()) {
                     namedMap.remove(p.getName());
                 }
                 categoryMap.get(p.getCategory()).remove(p);

                 if (categoryMap.get(p.getCategory()).isEmpty()) {
                     categoryMap.remove(p.getCategory());
                 }
                 mh.remove(p);
                 itr.remove();
             }


             changed = true;
             repaint();
             markedSet.clear();
             System.out.println(markedSet.toArray());

         }
     }

     class HideButtonListener implements ActionListener {
         public void actionPerformed(ActionEvent ave) {

             for (Iterator<Place> itr = markedSet.iterator(); itr.hasNext();) {
                 Place p = itr.next();
                 itr.remove();
                 p.setVisible(false);
                 p.off();
             }

             markedSet.clear();
             System.out.println(markedSet.toString());
             mh.validate();
             mh.repaint();
         }
     }

     class ExitListener implements ActionListener {
         public void actionPerformed(ActionEvent ave) {

             if (changed) {
                 int exitSvar = JOptionPane.showConfirmDialog(Inlupp2.this,
                         "Unsaved changes, " + "do you want to quit anyway?", "Exit", JOptionPane.OK_CANCEL_OPTION);
                 if (exitSvar == JOptionPane.OK_OPTION)
                     System.exit(0);
             } else {
                 System.out.println("Nu funkar det");
             }

         }
     }

     class HideCatLis implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent ave) {

             if (categoriesList.getSelectedValue() != null) {
                 for (Place p : categoryMap.get(categoriesList.getSelectedValue())) {

                     p.setMarked();
                     p.setVisible(false);
                 }
             }
             markedSet.clear();
             mh.validate();
             mh.repaint();
         }
     }

     class HideCatListener implements ActionListener {
         public void actionPerformed(ActionEvent ave) {
             HashSet<Place> selected = Place.getSelected();
             if (selected != null) {
                 for (Iterator<Place> itr = selected.iterator(); itr.hasNext();) {
                     Place p = itr.next();
                     itr.remove();
                     p.setVisible(false);
                     p.off();
                     changed = true;
                 }
             }
         }
     }

     public static void main(String[] args) {
         Inlupp2 in = new Inlupp2();
     }

 }