package map;


public interface IEngine {
    void start(int nrOfDays);

    void notification(boolean pause);

    void animalTracker(Vector2d position, int nrOfDays);

    String getTrackedAnimal();

    String getAnimalsWithDominantGenome();

    String getHistoryStatistics();

    boolean isOccupied(Vector2d position);

    boolean isPaused();


}
