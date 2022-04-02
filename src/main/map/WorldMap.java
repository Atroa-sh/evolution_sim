package map;

import java.util.*;

public class WorldMap implements IWorldMap, IObserver {
    private MapVisualizer visualizer = new MapVisualizer(this);
    private final LinkedHashMap<Vector2d, ArrayList<Animal>> hashMap = new LinkedHashMap<Vector2d, ArrayList<Animal>>();
    private final Vector2d upperBound;
    private final Vector2d lowerBound;
    private int numOfDeadAnimals = 0;
    private int sumOfDaysAlive = 0;
    private int numOfAnimals;
    private int[] dominantGenome;
    private int currentDay = 1;
    private int avgEnergyLevel;
    private int numOfChildren;
    private int sumOfNumOfAnimals = 0;
    private int sumOfAvgEnergyLevel = 0;
    private double sumOfAvgNumOfChildren = 0;
    private final ArrayList<int[]> dominantGenomesOverDays = new ArrayList<>();
    private final ArrayList<Integer> dominantGenomesCounter = new ArrayList<>();
    private final LinkedHashMap<Vector2d, Grass> hashMapGrass = new LinkedHashMap<Vector2d, Grass>();
    public final int startEnergy;
    public final int moveEnergy;
    public final int plantEnergy;
    private final double jungleRatio;
    private double sumOfAvgDaysAlive = 0;
    private int currentNrOfGrass = 0;
    private int sumOfNrOfGrass = 0;
    private final Random RNG = new Random();
    private final Vector2d jungleLowerBound;
    private final Vector2d jungleUpperBound;


    public WorldMap(int width, int height, int startEnergy, int moveEnergy, int plantEnergy, double jungleRatio) {
        this.upperBound = new Vector2d(width - 1, height - 1);
        this.lowerBound = new Vector2d(0, 0);
        this.currentNrOfGrass = 0;
        this.startEnergy = startEnergy;
        this.moveEnergy = moveEnergy;
        this.plantEnergy = plantEnergy;
        this.jungleRatio = jungleRatio;
        ArrayList<Vector2d> bounds = getJungleBounds();
        this.jungleLowerBound = bounds.get(0);
        this.jungleUpperBound = bounds.get(1);
    }

    private boolean ifGrassAt(Vector2d position) {
        if (hashMapGrass.get(position) == null) return false;
        else return true;
    }


    public boolean place(Animal object) {
        Vector2d position = object.getPosition();
        if (hashMap.get(position) == null) this.hashMap.put(position, new ArrayList<Animal>());
        this.hashMap.get(position).add(object);
        object.addObserver(this);
        return true;
    }

    private void eatingGrass() {
        for (Map.Entry<Vector2d, ArrayList<Animal>> entry : hashMap.entrySet()) {
            Vector2d currentPosition = entry.getKey();
            ArrayList<Animal> currentAnimals = entry.getValue();
            if (ifGrassAt(currentPosition) && currentAnimals.size() != 0) {
                ArrayList<Animal> strongestAnimals = strongestAnimals(currentPosition);
                int energyGain = plantEnergy / strongestAnimals.size();
                for (int i = 0; i < strongestAnimals.size(); i++) {
                    strongestAnimals.get(i).changeEnergy(energyGain);
                }
                hashMapGrass.remove(currentPosition);
                currentNrOfGrass -= 1;
            }
        }
    }

    private void spawnGrass() {
        ArrayList<Vector2d> possiblePositionsAtSavanna = new ArrayList<>();
        for (int i = lowerBound.x; i <= upperBound.x; i++) {
            for (int j = lowerBound.y; j <= upperBound.y; j++) {
                Vector2d currentPlace = new Vector2d(i, j);
                if (!(isOccupied(currentPlace)) && !(ifGrassAt(currentPlace)) && !(currentPlace.precedes(jungleUpperBound) && currentPlace.follows(jungleLowerBound)))
                    possiblePositionsAtSavanna.add(currentPlace);


            }
        }

        ArrayList<Vector2d> possiblePositionsAtJungle = new ArrayList<>();
        for (int i = jungleLowerBound.x; i <= jungleUpperBound.x; i++) {
            for (int j = jungleLowerBound.y; j <= jungleUpperBound.y; j++) {
                Vector2d currentPlace = new Vector2d(i, j);
                if (!(isOccupied(currentPlace)) && !(ifGrassAt(currentPlace))){
                    possiblePositionsAtJungle.add(currentPlace);
                }


            }
        }
        if (possiblePositionsAtJungle.size() != 0) {
            Vector2d positionJungle = possiblePositionsAtJungle.get(RNG.nextInt(possiblePositionsAtJungle.size()));
            hashMapGrass.put(positionJungle, new Grass(positionJungle));
            currentNrOfGrass += 1;
        }
        if (possiblePositionsAtSavanna.size() != 0) {
            Vector2d positionSavanna = possiblePositionsAtSavanna.get(RNG.nextInt(possiblePositionsAtSavanna.size()));
            hashMapGrass.put(positionSavanna, new Grass(positionSavanna));
            currentNrOfGrass += 1;
        }
    }

    private ArrayList<Vector2d> getJungleBounds() {
        ArrayList<Vector2d> bounds = new ArrayList<>();
        int width = this.upperBound.x + 1;
        int height = this.upperBound.y + 1;
        int jungleWidth = (int) (jungleRatio * width);
        int jungleHeight = (int) (jungleRatio * height);
        Vector2d jungleLowerBound = new Vector2d(Math.floorDiv(width - jungleWidth, 2), Math.floorDiv(height - jungleHeight, 2));
        Vector2d jungleUpperBound = new Vector2d(jungleLowerBound.x + jungleWidth - 1, jungleLowerBound.y + jungleHeight - 1);
        bounds.add(jungleLowerBound);
        bounds.add(jungleUpperBound);
        return bounds;
    }


    public boolean canMoveTo(Vector2d position) {
        int size;
        Object objectAtGivenPlace = objectAt(position);
        if (objectAtGivenPlace instanceof ArrayList) {
            size = ((ArrayList) objectAtGivenPlace).size();
            for (int i = 0; i < size; i++) {
                if (((ArrayList) objectAtGivenPlace).get(i) instanceof Animal) return false;
            }

        }
        return true;
    }


    public boolean isOccupied(Vector2d position) {
        if (this.objectAt(position) == null) return false;
        else if (this.objectAt(position) instanceof ArrayList && ((ArrayList) this.objectAt(position)).size() == 0)
            return false;
        else return true;
    }

    public Object objectAt(Vector2d position) {
        Object objectAt = this.hashMap.get(position);
        if (objectAt == null || (objectAt instanceof ArrayList && ((ArrayList<?>) objectAt).size() == 0)) {
            return hashMapGrass.get(position);
        } else return objectAt;
    }


    public String toString() throws IllegalArgumentException {
        if (upperBound.x <= 90 && upperBound.y <= 60)
            return visualizer.draw(lowerBound, upperBound);
        else return "";
    }

    public void positionChanged(Vector2d oldPosition, Vector2d newPosition, Animal animal) {
        hashMap.get(oldPosition).remove(animal);
        if (hashMap.get(newPosition) == null) this.hashMap.put(newPosition, new ArrayList<Animal>());
        hashMap.get(newPosition).add(animal);
    }

    public ArrayList<Vector2d> availablePlaces(Vector2d position) {
        ArrayList<Vector2d> availablePlaces = new ArrayList<>();
        if (canMoveTo(position.add(new Vector2d(0, 1))))
            availablePlaces.add(fixedPosition(position.add(new Vector2d(0, 1))));
        if (canMoveTo(position.add(new Vector2d(1, 1))))
            availablePlaces.add(fixedPosition(position.add(new Vector2d(1, 1))));
        if (canMoveTo(position.add(new Vector2d(1, 0))))
            availablePlaces.add(fixedPosition(position.add(new Vector2d(1, 0))));
        if (canMoveTo(position.add(new Vector2d(1, -1))))
            availablePlaces.add(fixedPosition(position.add(new Vector2d(1, -1))));
        if (canMoveTo(position.add(new Vector2d(0, -1))))
            availablePlaces.add(fixedPosition(position.add(new Vector2d(0, -1))));
        if (canMoveTo(position.add(new Vector2d(-1, -1))))
            availablePlaces.add(fixedPosition(position.add(new Vector2d(-1, -1))));
        if (canMoveTo(position.add(new Vector2d(-1, 0))))
            availablePlaces.add(fixedPosition(position.add(new Vector2d(-1, 0))));
        if (canMoveTo(position.add(new Vector2d(-1, 0))))
            availablePlaces.add(fixedPosition(position.add(new Vector2d(-1, 0))));
        if (canMoveTo(position.add(new Vector2d(-1, 1))))
            availablePlaces.add(fixedPosition(position.add(new Vector2d(-1, 1))));
        return availablePlaces;
    }

    private void animalsMove() {
        ArrayList<Animal> toMove = new ArrayList<>();
        for (Map.Entry<Vector2d, ArrayList<Animal>> entry : hashMap.entrySet()) {
            ArrayList<Animal> currentAnimals = entry.getValue();


            for (int i = 0; i < currentAnimals.size(); i++) {
                toMove.add(currentAnimals.get(i));
            }


        }
        toMove.stream().forEach(Animal::move);
    }

    public ArrayList<Animal> strongestAnimals(Vector2d position) {
        ArrayList<Animal> currentAnimals = hashMap.get(position);
        ArrayList<Animal> strongestAnimals = new ArrayList<>();
        for (int i = 0; i < currentAnimals.size(); i++) {
            if (strongestAnimals.size() == 0 || strongestAnimals.get(0).getEnergy() < currentAnimals.get(i).getEnergy()) {
                strongestAnimals.clear();
                strongestAnimals.add(currentAnimals.get(i));
            } else if (strongestAnimals.size() != 0 && strongestAnimals.get(0).getEnergy() == currentAnimals.get(i).getEnergy()) {
                strongestAnimals.add(currentAnimals.get(i));
            }
        }
        return strongestAnimals;
    }

    private void replicating() {
        LinkedHashMap<Vector2d, ArrayList<Animal>> copy = new LinkedHashMap<>(hashMap);
        for (Map.Entry<Vector2d, ArrayList<Animal>> entry : copy.entrySet()) {
            ArrayList<Animal> currentAnimals = entry.getValue();
            if (currentAnimals.size() > 1) {
                Animal firstStrongest = null;
                Animal secondStrongest = null;
                for (int i = 0; i < currentAnimals.size(); i++) {
                    Animal currentAnimal = currentAnimals.get(i);
                    if (firstStrongest == null || currentAnimal.getEnergy() > firstStrongest.getEnergy()) {
                        secondStrongest = firstStrongest;
                        firstStrongest = currentAnimal;
                    } else if (secondStrongest == null || currentAnimal.getEnergy() > secondStrongest.getEnergy()) {
                        secondStrongest = currentAnimal;
                    }

                }
                if (firstStrongest.getEnergy() >= firstStrongest.startEnergy / 2 && secondStrongest.getEnergy() >= secondStrongest.startEnergy / 2) {
                    firstStrongest.replicate(secondStrongest);

                }


            }
        }


    }


    private void dailyLoseOfEnergy() {
        for (Map.Entry<Vector2d, ArrayList<Animal>> entry : hashMap.entrySet()) {
            ArrayList<Animal> currentAnimals = entry.getValue();
            for (int i = 0; i < currentAnimals.size(); i++) {
                currentAnimals.get(i).changeEnergy(-moveEnergy);
            }
        }
    }

    public Vector2d fixedPosition(Vector2d currentPosition) {
        Vector2d fixedPosition = currentPosition;
        if (fixedPosition.x > this.getUpperBound().x) {
            fixedPosition.x = 0;
        }
        if (fixedPosition.x < this.getLowerBound().x) {
            fixedPosition.x = 99;
        }
        if (fixedPosition.y > this.getUpperBound().y) {
            fixedPosition.y = 0;
        }
        if (fixedPosition.y < this.getLowerBound().y) {
            fixedPosition.y = 29;
        }
        return fixedPosition;
    }

    public void animalDeath(Animal animal) {
        animal.setDayOfDeath(this.currentDay);
        hashMap.get(animal.getPosition()).remove(animal);
    }

    public void avgDaysAlive(int daysAlive) {
        this.numOfDeadAnimals += 1;
        this.sumOfDaysAlive += daysAlive;
    }


    public Vector2d getUpperBound() {
        return upperBound;
    }

    public Vector2d getLowerBound() {
        return lowerBound;
    }


    private double getAvgNumOfDaysAlive() {
        if (this.numOfDeadAnimals == 0) {
            return 0;
        } else {
            this.sumOfAvgDaysAlive += (double) sumOfDaysAlive / numOfDeadAnimals;
            return (double) sumOfDaysAlive / numOfDeadAnimals;
        }
    }

    private void updateStatistics() {
        ArrayList<int[]> genomesOnMap = new ArrayList<>();
        ArrayList<Integer> genomesCounter = new ArrayList<>();
        int sumEnergyLevel = 0;
        int numOfAnimals = 0;
        int numOfChildren = 0;
        for (Map.Entry<Vector2d, ArrayList<Animal>> entry : hashMap.entrySet()) {
            ArrayList<Animal> currentAnimals = entry.getValue();
            numOfAnimals = numOfAnimals + entry.getValue().size();
            for (int i = 0; i < currentAnimals.size(); i++) {
                Animal currentAnimal = currentAnimals.get(i);
                currentAnimal.resetVisited();
                sumEnergyLevel += currentAnimal.getEnergy();
                numOfChildren += currentAnimal.getNumOfChildren();
                int[] currentGenome = Arrays.copyOf(currentAnimal.getGenome(), currentAnimal.getGenome().length);
                boolean genomeFound = false;
                for (int j = 0; j < genomesOnMap.size(); j++) {
                    if (Arrays.equals(genomesOnMap.get(j), currentGenome)) {
                        genomesCounter.set(j, new Integer(genomesCounter.get(j) + 1));
                        genomeFound = true;
                    }
                }
                if (!(genomeFound)) {
                    genomesOnMap.add(currentGenome);
                    genomesCounter.add(1);
                }

            }
        }

        this.numOfAnimals = numOfAnimals;
        if (numOfAnimals != 0) this.avgEnergyLevel = sumEnergyLevel / numOfAnimals;
        else this.avgEnergyLevel = 0;
        this.numOfChildren = numOfChildren;
        this.sumOfAvgNumOfChildren += numOfChildren;
        if (genomesOnMap.size() != 0) {
            this.dominantGenome = genomesOnMap.get(genomesCounter.indexOf(Collections.max(genomesCounter)));
            updateHistoryOfDominantGenomes(this.dominantGenome);
        } else this.dominantGenome = null;
        this.sumOfAvgEnergyLevel += avgEnergyLevel;
        this.sumOfNumOfAnimals += numOfAnimals;
        this.sumOfNrOfGrass += currentNrOfGrass;
    }

    private void updateHistoryOfDominantGenomes(int[] newGenome) {
        for (int i = 0; i < dominantGenomesOverDays.size(); i++) {
            if (Arrays.equals(newGenome, dominantGenomesOverDays.get(i))) {
                dominantGenomesCounter.set(i, dominantGenomesCounter.get(i) + 1);
                return;
            }
        }
        dominantGenomesOverDays.add(newGenome);
        dominantGenomesCounter.add(1);
    }

    public void dailyRoutineOnMap() {
        spawnGrass();
        dailyLoseOfEnergy();//includes death of animals
        animalsMove();
        eatingGrass();
        replicating();
        updateStatistics();
        currentDay++;
    }

    public String getAnimalsWithDominantGenome() {
        ArrayList<Animal> listOfAnimals = new ArrayList<>();
        for (Map.Entry<Vector2d, ArrayList<Animal>> entry : hashMap.entrySet()) {
            ArrayList<Animal> currentAnimals = entry.getValue();
            for (int i = 0; i < currentAnimals.size(); i++) {
                if (Arrays.equals(this.dominantGenome, currentAnimals.get(i).getGenome())) {
                    listOfAnimals.add(currentAnimals.get(i));
                }

            }
        }
        StringBuilder resultString = new StringBuilder();
        resultString.append("Animals with dominant genome:\n");
        if (this.dominantGenome != null) {
            resultString.append(Arrays.toString(this.dominantGenome) + "\n");
        }
        for (int i = 0; i < listOfAnimals.size(); i++) {
            resultString.append(i + 1 + ". Position:" + listOfAnimals.get(i).getPosition().toString() + "Current energy: " + listOfAnimals.get(i).getEnergy() + "\n");
        }
        return resultString.toString();
    }



    public String showDailyStatistics() {
        StringBuilder resultString = new StringBuilder();
        resultString.append("Number of animals on map: " + this.numOfAnimals + "\n");
        resultString.append("Number of grass clumps on map: " + this.currentNrOfGrass + "\n");
        if (dominantGenome != null)
            resultString.append("Dominant genome: " + "\n" + Arrays.toString(this.dominantGenome) + "\n");
        else
            resultString.append("Dominant genome: " + "\n" + "All animals went extinct" + "\n");
        resultString.append("Average energy level of an animal: " + (double) this.avgEnergyLevel + "\n");
        resultString.append("Average life length of dead animals: " + getAvgNumOfDaysAlive() + "\n");
        if (this.numOfAnimals != 0)
            resultString.append("Average number of living kids per animal: " + (double) this.numOfChildren / this.numOfAnimals + "\n");
        else
            resultString.append("Average number of living kids per animal: " + 0.0 + "\n");
        return resultString.toString();
    }

    public String showHistoryStatistics() {
        StringBuilder resultString = new StringBuilder();
        resultString.append("Average number of animals on map: " + this.sumOfNumOfAnimals / this.currentDay + "\n");
        resultString.append("Average number of grass clumps on map: " + this.sumOfNrOfGrass / this.currentDay + "\n");
        int[] mostDominantGenome = dominantGenomesOverDays.get(dominantGenomesCounter.indexOf(Collections.max(dominantGenomesCounter)));
        if (dominantGenome != null)
            resultString.append("Dominant genome: " + "\n" + Arrays.toString(mostDominantGenome) + "\n");
        else
            resultString.append("Dominant genome: " + "\n" + "No genome was ever dominant" + "\n");
        resultString.append("Average of average energy level of an animal: " + (double) this.sumOfAvgEnergyLevel / this.currentDay + "\n");
        resultString.append("Average of average life length of dead animals: " + this.sumOfAvgDaysAlive / this.currentDay + "\n");
        resultString.append("Average of average number of living kids per animal: " + this.sumOfAvgNumOfChildren / this.currentDay + "\n");
        return resultString.toString();
    }

    public String getInfoAboutTrackedAnimal(Animal trackedAnimal) {
        StringBuilder resultString = new StringBuilder();
        if (trackedAnimal.getIsAlive()) {
            resultString.append("Tracked animal is currently at: " + trackedAnimal.getPosition().toString() + "\n");
        }
        resultString.append("Number of living children of tracked Animal: " + trackedAnimal.getNumOfChildren() + "\n");
        resultString.append("Number of living descendants of tracked Animal: " + trackedAnimal.getNumOfDescendants() + "\n");

        if (trackedAnimal.getDayOfDeath() == -1) {
            resultString.append("Animal is still alive!" + "\n");

        } else
            resultString.append("Animal died in " + trackedAnimal.getDayOfDeath() + "'th day of simulation. R.I.P \n");
        return resultString.toString();
    }

}
