package map;

import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;


public class Animal {
    private Vector2d position;
    private MapDirection direction;
    private final IWorldMap map;
    private int[] genome;
    private boolean visited = false;
    private boolean isAlive = true;
    private final ArrayList<Animal> listOfChildren;
    public final int startEnergy;
    private int dayOfDeath = -1;
    private int energy;
    private int daysAlive = 0;
    private final ArrayList<IObserver> listOfObservers = new ArrayList<>();
    private Random RNG = new Random();

    public Animal(IWorldMap map, Vector2d initialPosition, int[] genome, int energy) throws IllegalArgumentException {
        this.map = map;
        this.position = initialPosition;
        this.direction = getRandomMapDirection();
        this.map.place(this);
        this.genome = genome;
        this.energy = energy;
        this.startEnergy = energy;
        this.listOfChildren = new ArrayList<>();
    }

    private MapDirection getRandomMapDirection() {
        MapDirection[] possibleDirections = {MapDirection.NORTH, MapDirection.NORTHEAST
                , MapDirection.EAST, MapDirection.SOUTHEAST, MapDirection.SOUTH, MapDirection.SOUTHWEST, MapDirection.WEST, MapDirection.NORTHWEST};
        return possibleDirections[RNG.nextInt(possibleDirections.length)];
    }

    public String toString() {

        if (this.energy >= startEnergy) return "+";
        else if (this.energy < startEnergy && this.energy >= 0.9 * startEnergy) return "9";
        else if (this.energy < 0.9 * startEnergy && this.energy >= 0.8 * startEnergy) return "8";
        else if (this.energy < 0.8 * startEnergy && this.energy >= 0.7 * startEnergy) return "7";
        else if (this.energy < 0.7 * startEnergy && this.energy >= 0.6 * startEnergy) return "6";
        else if (this.energy < 0.6 * startEnergy && this.energy >= 0.5 * startEnergy) return "5";
        else if (this.energy < 0.5 * startEnergy && this.energy >= 0.4 * startEnergy) return "4";
        else if (this.energy < 0.4 * startEnergy && this.energy >= 0.3 * startEnergy) return "3";
        else if (this.energy < 0.3 * startEnergy && this.energy >= 0.2 * startEnergy) return "2";
        else if (this.energy < 0.2 * startEnergy && this.energy >= 0.1 * startEnergy) return "1";
        else if (this.energy < 0.1 * startEnergy && this.energy >= 0) return "0";
        else return null;
    }

    public void move() {
        this.daysAliveIncrease();
        Vector2d newPosition;
        int randomTurn = RNG.nextInt(genome.length);
        MapDirection newMove = this.direction.turn(genome[randomTurn]);
        Vector2d change = newMove.toUnitVector();
        newPosition = this.position.add(change);
        if (newPosition.x > this.map.getUpperBound().x) {
            newPosition.x = this.map.getLowerBound().x;
        }
        if (newPosition.x < this.map.getLowerBound().x) {
            newPosition.x = this.map.getUpperBound().x;
        }
        if (newPosition.y > this.map.getUpperBound().y) {
            newPosition.y = this.map.getLowerBound().y;
        }
        if (newPosition.y < this.map.getLowerBound().y) {
            newPosition.y = this.map.getUpperBound().y;
        }
        this.positionChanged(this.position, newPosition);
        this.position = newPosition;
    }


    private void positionChanged(Vector2d oldPosition, Vector2d newPosition) {
        for (int i = 0; i < listOfObservers.size(); i++) {
            listOfObservers.get(i).positionChanged(oldPosition, newPosition, this);
        }
    }

    private void animalDeath() {
        for (int i = 0; i < listOfObservers.size(); i++) {
            listOfObservers.get(i).animalDeath(this);
            listOfObservers.get(i).avgDaysAlive(this.daysAlive);
        }
        this.isAlive = false;

    }

    public void setDayOfDeath(int currentDay) {
        this.dayOfDeath = currentDay;
    }

    public void addObserver(IObserver observer) {
        listOfObservers.add(observer);
    }

    public void removeObserver(IObserver observer) {
        listOfObservers.removeIf(it -> it.equals(observer));
    }

    public void changeEnergy(int difference) {
        this.energy = this.energy + difference;
        if (this.energy <= 0) animalDeath();
    }

    public void resetVisited(){
        this.visited=false;
    }


    public void replicate(Animal secondParent) {

        int[] genome1 = this.genome;
        int[] genome2 = secondParent.genome;
        int energy1 = this.getEnergy();
        int energy2 = secondParent.getEnergy();
        int[] newGenome = new int[32];
        int firstDivision = this.RNG.nextInt(31);
        int secondDivision = this.RNG.nextInt(31);
        while (firstDivision == secondDivision) {
            secondDivision = this.RNG.nextInt(31);
        }
        if (secondDivision < firstDivision) {
            int swap = secondDivision;
            secondDivision = firstDivision;
            firstDivision = swap;
        }
        for (int i = 0; i < firstDivision + 1; i++) {
            newGenome[i] = genome1[i];
        }
        for (int i = firstDivision + 1; i < secondDivision + 1; i++) {
            newGenome[i] = genome2[i];
        }
        for (int i = secondDivision + 1; i < genome1.length; i++) {
            newGenome[i] = genome1[i];
        }
        int[] check = missingGenes(newGenome);
        while (!(ifAllGenes(check))) {
            for (int i = 0; i < check.length; i++) {
                if (check[i] != 1) {
                    newGenome[RNG.nextInt(32)] = i;
                }
            }
            check = missingGenes(newGenome);
        }
        Vector2d positionOfNewAnimal;
        ArrayList<Vector2d> availablePositions = this.map.availablePlaces(this.getPosition());
        if (availablePositions.size() == 0) {
            positionOfNewAnimal = this.getPosition();
        } else {
            int randomPosition = RNG.nextInt(availablePositions.size());
            positionOfNewAnimal = availablePositions.get(randomPosition);
        }
        int energyFromFirst = Math.floorDiv(energy1, 4);
        int energyFromSecond = Math.floorDiv(energy2, 4);
        this.changeEnergy(-energyFromFirst);
        secondParent.changeEnergy(-energyFromSecond);
        Animal child = new Animal(this.map, positionOfNewAnimal, newGenome, energyFromFirst + energyFromSecond);
        this.listOfChildren.add(child);
        secondParent.listOfChildren.add(child);

    }


    private int[] missingGenes(int[] genome) {
        int[] check = new int[8];
        for (int i = 0; i < genome.length; i++) {
            check[genome[i]] = 1;
        }

        return check;
    }

    private void daysAliveIncrease() {
        this.daysAlive += 1;
    }

    private boolean ifAllGenes(int[] check) {
        for (int i = 0; i < check.length; i++) {
            if (check[i] != 1) return false;
        }
        return true;
    }

    public boolean getIsAlive() {
        return this.isAlive;
    }
    public Vector2d getPosition() {
        return this.position;
    }
    public int getEnergy() {
        return this.energy;
    }

    public int[] getGenome() {
        return this.genome;
    }

    public int getNumOfChildren() {
        int sumOfLivingChildren = 0;
        for (int i = 0; i < listOfChildren.size(); i++) {
            if (listOfChildren.get(i).isAlive) sumOfLivingChildren += 1;
        }
        return sumOfLivingChildren;
    }

    public int getDayOfDeath() {
        return dayOfDeath;
    }

    public int getNumOfDescendants() {
        this.visited = true;
        int sumOfDescendants = 0;
        if (this.isAlive) sumOfDescendants = 1;
        if (listOfChildren.size() == 0) {
            return sumOfDescendants;
        } else {
            for (int i = 0; i < listOfChildren.size(); i++) {
                if (!(listOfChildren.get(i).visited))
                    sumOfDescendants += listOfChildren.get(i).getNumOfDescendants();
            }
        }
        return sumOfDescendants;

    }


}

