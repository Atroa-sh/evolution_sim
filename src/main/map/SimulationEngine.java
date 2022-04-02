package map;

import java.util.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

public class SimulationEngine implements IEngine {
    private int currentDay;
    private int endOfTacking;
    private final int nrOfSim;
    private final IGUI GUI;
    private Thread thread;
    private AtomicBoolean paused = new AtomicBoolean();
    private final IWorldMap map;
    private final int startEnergy;
    private final ArrayList<Animal> animalsOnMap;
    private final int moveEnergy;
    private final int plantEnergy;
    private final double jungleRatio;
    private Animal trackedAnimal;

    public SimulationEngine(int width, int height, int nrOfAnimals, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio, IGUI GUI, int nrOfSim) throws IllegalArgumentException {
        this.nrOfSim = nrOfSim;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.jungleRatio = jungleRatio;
        this.map = new WorldMap( width, height, startEnergy, moveEnergy, plantEnergy, jungleRatio);
        animalsOnMap = randomAnimalGenerator(nrOfAnimals);
        this.GUI = GUI;
        paused.set(false);
    }

    private ArrayList<Animal> randomAnimalGenerator(int nrOfAnimals) {
        Random RNG = new Random();
        ArrayList<Animal> listOfAnimals = new ArrayList<>();
        for (int i = 0; i < nrOfAnimals; i++) {
            Vector2d animalPosition = new Vector2d(RNG.nextInt(map.getUpperBound().x + 1), RNG.nextInt(map.getUpperBound().y + 1));
            ;
            int[] newGenome = new int[32];
            int[] placesOfDivision = new int[7];
            for (int j = 0; j < placesOfDivision.length; j = j) {
                int randomPlaceOfDivision = RNG.nextInt(31);
                if (randomPlaceOfDivision != 0 && !(IntStream.of(placesOfDivision).anyMatch(x -> x == randomPlaceOfDivision))) {
                    placesOfDivision[j] = randomPlaceOfDivision;
                    j++;
                }
            }
            Arrays.sort(placesOfDivision);
            for (int j = 0; j < newGenome.length; j++) {
                int k;
                for (k = 0; k < placesOfDivision.length; k++) {
                    if (j < placesOfDivision[k]) break;
                }
                newGenome[j] = k;
            }
            listOfAnimals.add(new Animal(this.map, animalPosition, newGenome, startEnergy));


        }
        return listOfAnimals;
    }

    public String getAnimalsWithDominantGenome() {
        return map.getAnimalsWithDominantGenome();
    }

    public String getCurrentStatistics() {
        return map.showDailyStatistics();
    }

    public String getMapString() {
        return map.toString();
    }

    public String getTrackedAnimal() {
        return map.getInfoAboutTrackedAnimal(this.trackedAnimal);
    }

    public boolean isOccupied(Vector2d position) {
        return this.map.isOccupied(position);
    }

    public void animalTracker(Vector2d position, int nrOfDays) {
        this.trackedAnimal = map.strongestAnimals(position).get(0);
        this.endOfTacking = currentDay + nrOfDays;
    }

    public String getHistoryStatistics() {
        return map.showHistoryStatistics();
    }

    public void start(int nrOfDays) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() throws IllegalArgumentException {
                for (int day = 1; day <= nrOfDays; day++) {
                    SimulationEngine.this.currentDay = day;
                    if (paused.get()) {
                        synchronized (thread) {
                            try {
                                thread.wait();
                            } catch (InterruptedException e) {

                            }
                        }
                    }

                    map.dailyRoutineOnMap();
                    GUI.changeMap("Day: " + day + "\n" + getMapString(), nrOfSim);
                    GUI.changeStatistics(getCurrentStatistics(), nrOfSim);
                    if (trackedAnimal != null && day == SimulationEngine.this.endOfTacking) {
                        GUI.changeTrackedAnimal(getTrackedAnimal(), nrOfSim);
                    }

                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {

                    }
                }


            }
        };
        thread = new Thread(runnable);
        thread.start();
    }

    public void notification(boolean pause) {
        if (pause) {
            paused.set(true);
        } else {
            paused.set(false);
            synchronized (thread) {
                thread.notify();
            }
        }
    }

    public boolean isPaused() {
        if (!paused.get()) return true;
        else return false;
    }


}
