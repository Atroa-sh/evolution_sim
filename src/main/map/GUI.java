package map;

import javax.swing.*;
import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class GUI implements ActionListener, IGUI {
    private IEngine sim1;
    private IEngine sim2;
    private Vector2d position1;
    private Vector2d position2;
    private int width;
    private int height;
    private int startEnergy;
    private int moveEnergy;
    private int plantEnergy;
    private double jungleRatio;
    private int numberOfAnimals;
    private final JButton addParameters = new JButton("Load parameters");
    private final JButton startSim = new JButton("Start");
    private final JButton startTwo = new JButton("Start two sims at once");
    private final JButton reset = new JButton("Reset simulations");
    private final JButton getCurrentHistory1 = new JButton("Get stat of sim");
    private final JButton stopSimulation1 = new JButton("Stop sim");
    private final JTextField x1 = new JTextField(4);
    private final JTextField y1 = new JTextField(4);
    private final JButton getCurrentHistory2 = new JButton("Get stat of sim");
    private final JButton stopSimulation2 = new JButton("Stop sim");
    private final JButton trackButton1 = new JButton("Track animal");
    private final JButton trackButton2 = new JButton("Track animal");
    private final JButton animalsWithDominantGenome1 = new JButton("Animals with dominant genome");
    private final JButton animalsWithDominantGenome2 = new JButton("Animals with dominant genome");
    private final JTextField x2 = new JTextField(4);
    private final JTextField y2 = new JTextField(4);
    private final JLabel xLabel1 = new JLabel(":X");
    private final JLabel yLabel1 = new JLabel(":Y");
    private final JLabel xLabel2 = new JLabel(":X");
    private final JLabel yLabel2 = new JLabel(":Y");
    private final JLabel loadLabel = new JLabel("Load parameters to start simulation", SwingConstants.CENTER);
    private final JTextArea mapArea1 = new JTextArea("Here map will appear");
    private final JTextArea mapArea2 = new JTextArea("Here map will appear");
    private final JTextArea stat1 = new JTextArea("Here statistics will appear");
    private final JTextArea stat2 = new JTextArea("Here statistics will appear");
    private final JTextArea infoAboutTrackedAnimal1 = new JTextArea("Here info about tracked animal will appear");
    private final JTextArea infoAboutTrackedAnimal2 = new JTextArea("Here info about tracked animal will appear");
    private final JPanel mainPanel;
    private final JPanel mainPanel2;
    private final JFrame popup1 = new JFrame("Track animal for...");
    private final JPanel popupPanel1 = new JPanel();
    private final JLabel popupNrOfDays1 = new JLabel("Nr. of days");
    private final JButton popupAccept1 = new JButton("Accept");
    private final JTextField nrOfDays1 = new JTextField(4);
    private final JFrame popup2 = new JFrame("Track animal for...");
    private final JPanel popupPanel2 = new JPanel();
    private final JLabel popupNrOfDays2 = new JLabel("Nr. of days");
    private final JButton popupAccept2 = new JButton("Accept");
    private final JTextField nrOfDays2 = new JTextField(4);


    public GUI() {
        mapArea1.setFont(new Font("Consolas", Font.BOLD, 9));
        mapArea1.setEditable(false);
        mapArea2.setFont(new Font("Consolas", Font.BOLD, 9));
        mapArea2.setEditable(false);
        stat1.setFont(new Font("Consolas", Font.BOLD, 14));
        stat1.setEditable(false);
        stat2.setFont(new Font("Consolas", Font.BOLD, 14));
        stat2.setEditable(false);
        infoAboutTrackedAnimal1.setFont(new Font("Consolas", Font.BOLD, 14));
        infoAboutTrackedAnimal1.setEditable(false);
        infoAboutTrackedAnimal2.setFont(new Font("Consolas", Font.BOLD, 14));
        infoAboutTrackedAnimal2.setEditable(false);
        loadLabel.setFont(new Font("Consolas", Font.BOLD, 18));

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        popup1.add(popupPanel1);
        popupPanel1.setLayout(new BoxLayout(popupPanel1, BoxLayout.X_AXIS));
        popupPanel1.add(popupNrOfDays1);
        popupPanel1.add(nrOfDays1);
        popupPanel1.add(popupAccept1);
        popup1.setSize(350, 75);
        popup1.setLocation(dim.width / 2 - popup1.getSize().width / 2, dim.height / 2 - popup1.getSize().height / 2);

        popup2.add(popupPanel2);
        popupPanel2.setLayout(new BoxLayout(popupPanel2, BoxLayout.X_AXIS));
        popupPanel2.add(popupNrOfDays2);
        popupPanel2.add(nrOfDays2);
        popupPanel2.add(popupAccept2);
        popup2.setSize(350, 75);
        popup2.setLocation(dim.width / 2 - popup2.getSize().width / 2, dim.height / 2 - popup2.getSize().height / 2);


        addParameters.addActionListener(this);
        startSim.addActionListener(this);
        startTwo.addActionListener(this);
        getCurrentHistory1.addActionListener(this);
        getCurrentHistory2.addActionListener(this);
        stopSimulation1.addActionListener(this);
        stopSimulation2.addActionListener(this);
        trackButton1.addActionListener(this);
        trackButton2.addActionListener(this);
        animalsWithDominantGenome1.addActionListener(this);
        animalsWithDominantGenome2.addActionListener(this);
        popupAccept1.addActionListener(this);
        popupAccept2.addActionListener(this);
        reset.addActionListener(this);


        JFrame frame = new JFrame("Evolution generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel textPanel1 = new JPanel();
        textPanel1.setLayout(new BoxLayout(textPanel1, BoxLayout.Y_AXIS));

        JPanel textPanel2 = new JPanel();
        textPanel2.setLayout(new BoxLayout(textPanel2, BoxLayout.Y_AXIS));

        JPanel xPanel1 = new JPanel();

        JPanel yPanel1 = new JPanel();

        JPanel xPanel2 = new JPanel();

        JPanel yPanel2 = new JPanel();


        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        mainPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(mainPanel, BoxLayout.X_AXIS);
        mainPanel.setLayout(boxLayout);

        JPanel mainPanel1 = new JPanel();
        mainPanel1.setLayout(new BoxLayout(mainPanel1, BoxLayout.Y_AXIS));

        mainPanel2 = new JPanel();
        mainPanel2.setLayout(new BoxLayout(mainPanel2, BoxLayout.Y_AXIS));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        JPanel mapPanel1 = new JPanel();
        mapPanel1.setLayout(new BorderLayout());
        mapPanel1.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel mapPanel2 = new JPanel();
        mapPanel2.setLayout(new BorderLayout());
        mapPanel2.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel statPanel1 = new JPanel();
        statPanel1.setLayout(new BorderLayout());
        statPanel1.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel statPanel2 = new JPanel();
        statPanel2.setLayout(new BorderLayout());
        statPanel2.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel buttonsPanel1 = new JPanel();
        buttonsPanel1.setLayout(new BoxLayout(buttonsPanel1, BoxLayout.X_AXIS));

        JPanel buttonsPanel2 = new JPanel();
        buttonsPanel2.setLayout(new BoxLayout(buttonsPanel2, BoxLayout.X_AXIS));

        JPanel trackAnimal1 = new JPanel();
        trackAnimal1.setLayout(new BorderLayout());

        JPanel trackAnimal2 = new JPanel();
        trackAnimal2.setLayout(new BorderLayout());

        topPanel.add(addParameters);
        topPanel.add(startSim);
        topPanel.add(startTwo);
        topPanel.add(reset);

        textPanel1.add(xPanel1);
        textPanel1.add(yPanel1);

        textPanel2.add(xPanel2);
        textPanel2.add(yPanel2);

        xPanel1.add(x1);
        xPanel1.add(xLabel1);

        xPanel2.add(x2);
        xPanel2.add(xLabel2);

        yPanel1.add(y1);
        yPanel1.add(yLabel1);

        yPanel2.add(y2);
        yPanel2.add(yLabel2);

        buttonsPanel1.add(stopSimulation1);
        buttonsPanel1.add(getCurrentHistory1);
        buttonsPanel1.add(textPanel1);
        buttonsPanel1.add(trackButton1);
        buttonsPanel1.add(animalsWithDominantGenome1);


        buttonsPanel2.add(stopSimulation2);
        buttonsPanel2.add(getCurrentHistory2);
        buttonsPanel2.add(textPanel2);
        buttonsPanel2.add(trackButton2);
        buttonsPanel2.add(animalsWithDominantGenome2);


        mainPanel1.add(mapPanel1);
        mainPanel1.add(statPanel1);
        mainPanel1.add(buttonsPanel1);
        mainPanel1.add(trackAnimal1);

        mainPanel2.add(mapPanel2);
        mainPanel2.add(statPanel2);
        mainPanel2.add(buttonsPanel2);
        mainPanel2.add(trackAnimal2);

        mainPanel.add(mainPanel1);

        trackAnimal1.add(infoAboutTrackedAnimal1);
        trackAnimal2.add(infoAboutTrackedAnimal2);

        panel.add(topPanel);
        panel.add(loadLabel);
        panel.add(mainPanel);


        mapPanel1.add(mapArea1);
        mapPanel2.add(mapArea2);
        statPanel1.add(stat1);
        statPanel2.add(stat2);


        frame.add(panel);
        frame.pack();
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

    }

    public static void main(String[] args) {
        new GUI();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addParameters) {
            if (parser()) {
                loadLabel.setText("File was loaded successfully");
            } else loadLabel.setText("Something went wrong (Maybe file path is incorrect)");

        } else if (e.getSource() == startSim) {
            if (sim1 != null) {
                loadLabel.setText("One simulation already takes place");
            } else if (this.width != 0) {
                sim1 = new SimulationEngine(width, height, numberOfAnimals, startEnergy, moveEnergy, plantEnergy, jungleRatio, this, 1);
                loadLabel.setText("Simulation has started!");
                sim1.start(Integer.MAX_VALUE);
                if (width > 90 || height > 60) {
                    loadLabel.setText("Simulations have started but map is too big to be displayed (maxWidth==60, maxHeight==90)");
                }
            } else if (this.width == 0 || this.width < 0 || this.height == 0 || this.height < 0) {
                loadLabel.setText("width or height are incorrect");
            } else if (this.numberOfAnimals < 2) {
                loadLabel.setText("Add more life to this world! (less than 2 animals)");
            }
            else if(jungleRatio<=0||jungleRatio>=1){
                loadLabel.setText("Jungle is either to small or too big (can be 0<j<1)");
            }
            else if(moveEnergy<=0){
                loadLabel.setText("Animals don't lose energy with their moves");
            }
        } else if (e.getSource() == startTwo) {
            if (sim1 != null || sim2 != null) {
                loadLabel.setText("One simulation already takes place");
            } else if (this.width != 0) {
                mainPanel.add(mainPanel2);
                sim1 = new SimulationEngine(width, height, numberOfAnimals, startEnergy, moveEnergy, plantEnergy, jungleRatio, this, 1);
                sim2 = new SimulationEngine(width, height, numberOfAnimals, startEnergy, moveEnergy, plantEnergy, jungleRatio, this, 2);
                loadLabel.setText("Two simulations have started!");
                sim1.start(Integer.MAX_VALUE);
                sim2.start(Integer.MAX_VALUE);
                if (height > 60 || width > 90) {
                    loadLabel.setText("Simulations have started but map is too big to be displayed (maxWidth==90, maxHeight==60)");
                }
            } else if (this.width == 0 || this.width < 0 || this.height == 0 || this.height < 0) {
                loadLabel.setText("width or height are incorrect");
            } else if (this.numberOfAnimals < 2) {
                loadLabel.setText("Add more life to this world! (less than 2 animals)");
            }
            else if(jungleRatio<=0||jungleRatio>=1){
                loadLabel.setText("Jungle is either to small or too big (can be 0<j<1)");
            }
            else if(moveEnergy<=0){
                loadLabel.setText("Animals don't lose energy with their moves (moveEnergy<=0)");
            }
        } else if (e.getSource() == stopSimulation1) {
            if (stopSimulation1.getLabel() == "Stop sim") {
                stopSimulation1.setLabel("Resume");
                if (sim1 != null) sim1.notification(true);
            } else if (stopSimulation1.getLabel() == "Resume") {
                stopSimulation1.setLabel("Stop sim");
                if (sim1 != null) sim1.notification(false);
            }
        } else if (e.getSource() == stopSimulation2) {
            if (stopSimulation2.getLabel() == "Stop sim") {
                stopSimulation2.setLabel("Resume");
                if (sim2 != null) sim2.notification(true);
            } else if (stopSimulation2.getLabel() == "Resume") {
                stopSimulation2.setLabel("Stop sim");
                if (sim2 != null) sim2.notification(false);
            }
        } else if (e.getSource() == getCurrentHistory1) {
            if (sim1 == null) loadLabel.setText("Start sim in order to load it's history");
            else if (sim1.isPaused()) loadLabel.setText("Pause sim to access this function");
            else {
                loadLabel.setText("History of chosen simulation was loaded to file 'statistics1.txt'");
                writeStatistics(1);
            }
        } else if (e.getSource() == getCurrentHistory2) {
            if (sim2 == null) loadLabel.setText("Start sim in order to load it's history");
            else if (sim2.isPaused()) loadLabel.setText("Pause sim to access this function");
            else {
                loadLabel.setText("History of chosen simulation was loaded to file 'statistics2.txt'");
                writeStatistics(2);
            }
        } else if (e.getSource() == trackButton1) {
            int numberX;
            int numberY;
            if (sim1 == null) loadLabel.setText("Start sim in order access this function");
            else if (sim1.isPaused()) {
                loadLabel.setText("Pause sim to access this function");
                return;
            }
            try {
                numberX = Integer.parseInt(this.x1.getText());
                numberY = Integer.parseInt(this.y1.getText());
            } catch (NumberFormatException exception) {
                loadLabel.setText("One of arguments in text fields is invalid");
                return;
            }
            Vector2d position = new Vector2d(numberX, numberY);
            if (!sim1.isOccupied(position)) {
                loadLabel.setText("This place is empty");
                return;
            }
            this.position1 = position;
            popup1.setVisible(true);


        } else if (e.getSource() == trackButton2) {
            int numberX;
            int numberY;
            if (sim2 == null) loadLabel.setText("Start sim in order access this function");
            else if (sim2.isPaused()) {
                loadLabel.setText("Pause sim to access this function");
                return;
            }
            try {
                numberX = Integer.parseInt(this.x2.getText());
                numberY = Integer.parseInt(this.y2.getText());
            } catch (NumberFormatException exception) {
                loadLabel.setText("One of arguments in text fields is invalid");
                return;
            }
            Vector2d position = new Vector2d(numberX, numberY);
            if (!sim2.isOccupied(position)) {
                loadLabel.setText("This place is empty");
                return;
            }
            this.position2 = position;
            popup2.setVisible(true);


        } else if (e.getSource() == animalsWithDominantGenome1) {
            if (sim1 == null) loadLabel.setText("Start sim to see animals with dominant genome");
            else if (sim1.isPaused()) {
                loadLabel.setText("Pause sim to access this function");
                return;
            } else {
                loadLabel.setText("Those are animals with dominant genome");
                infoAboutTrackedAnimal1.setText(sim1.getAnimalsWithDominantGenome());
            }

        } else if (e.getSource() == animalsWithDominantGenome2) {
            if (sim2 == null) loadLabel.setText("Start sim to see animals with dominant genome");
            else if (sim2.isPaused()) {
                loadLabel.setText("Pause sim to access this function");
                return;
            } else {
                loadLabel.setText("Those are animals with dominant genome");
                infoAboutTrackedAnimal2.setText(sim2.getAnimalsWithDominantGenome());
            }

        } else if (e.getSource() == popupAccept1) {
            int nrOfDays;
            try {
                nrOfDays = Integer.parseInt(this.nrOfDays1.getText());
            } catch (NumberFormatException ex) {
                loadLabel.setText("Incorrect input");
                popup1.setVisible(false);
                return;
            }
            sim1.animalTracker(position1, nrOfDays);
            loadLabel.setText("Animal is being tracked");
            popup1.setVisible(false);

        } else if (e.getSource() == popupAccept2) {
            int nrOfDays;
            try {
                nrOfDays = Integer.parseInt(this.nrOfDays2.getText());
            } catch (NumberFormatException ex) {
                loadLabel.setText("Incorrect input");
                popup2.setVisible(false);
                return;
            }
            sim2.animalTracker(position2, nrOfDays);
            loadLabel.setText("Animal is being tracked");
            popup2.setVisible(false);

        } else if (e.getSource() == reset) {
            if (sim1 != null && sim1.isPaused()) {
                loadLabel.setText("Pause current sims before reset");
                return;
            }
            if (sim2 != null && sim2.isPaused()) {
                loadLabel.setText("Pause current sims before reset");
                return;
            }
            sim1 = null;
            sim2 = null;
            mainPanel.remove(mainPanel2);
            loadLabel.setText("Simulations were reset");
            stopSimulation1.setLabel("Stop sim");
            stopSimulation2.setLabel("Stop sim");
            mapArea1.setText("");
            mapArea2.setText("");
            stat1.setText("");
            stat2.setText("");
            infoAboutTrackedAnimal1.setText("");
            infoAboutTrackedAnimal2.setText("");
        }
    }

    public boolean parser() {

        Path path = Paths.get("parameters.json");
        File file = new File(String.valueOf(path));
        Scanner fScn;
        try {
            fScn = new Scanner(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }


        String data;

        while (fScn.hasNextLine()) {
            data = fScn.nextLine();
            if (data.contains("width")) {
                data = data.replaceAll("[^\\d.]", "");
                this.width = Integer.parseInt(data);
            } else if (data.contains("height")) {
                data = data.replaceAll("[^\\d.]", "");
                this.height = Integer.parseInt(data);
            } else if (data.contains("startEnergy")) {
                data = data.replaceAll("[^\\d.]", "");
                this.startEnergy = Integer.parseInt(data);
            } else if (data.contains("moveEnergy")) {
                data = data.replaceAll("[^\\d.]", "");
                this.moveEnergy = Integer.parseInt(data);
            } else if (data.contains("plantEnergy")) {
                data = data.replaceAll("[^\\d.]", "");
                this.plantEnergy = Integer.parseInt(data);
            } else if (data.contains("jungleRatio")) {
                data = data.replaceAll("[^\\d.]", "");
                this.jungleRatio = Double.parseDouble(data);
            } else if (data.contains("numberOfAnimals")) {
                data = data.replaceAll("[^\\d.]", "");
                this.numberOfAnimals = Integer.parseInt(data);
            }

        }

        fScn.close();
        return true;


    }

    public void changeMap(String newMap, int nrOfSim) {
        if (nrOfSim == 1) {
            this.mapArea1.setText(newMap);
        } else if (nrOfSim == 2) {
            this.mapArea2.setText(newMap);
        }
    }

    public void changeStatistics(String newStatistics, int nrOfSim) {
        if (nrOfSim == 1) {
            this.stat1.setText(newStatistics);
        } else if (nrOfSim == 2) {
            this.stat2.setText(newStatistics);
        }
    }

    public void changeTrackedAnimal(String newStatistics, int nrOfSim) {
        if (nrOfSim == 1) {
            this.infoAboutTrackedAnimal1.setText(newStatistics);
        } else if (nrOfSim == 2) {
            this.infoAboutTrackedAnimal2.setText(newStatistics);
        }
    }


    public void writeStatistics(int nrOfSim) {
        try {
            FileWriter myWriter = new FileWriter("statistics" + nrOfSim + ".txt");
            if (nrOfSim == 1) {
                myWriter.write(sim1.getHistoryStatistics());
            } else if (nrOfSim == 2) {
                myWriter.write(sim2.getHistoryStatistics());
            }
            myWriter.close();
        } catch (IOException e) {
            loadLabel.setText("Something went wrong during writing new file");
            e.printStackTrace();
        }

    }
}
