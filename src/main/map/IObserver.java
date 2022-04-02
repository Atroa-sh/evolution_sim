package map;

public interface IObserver {
    void positionChanged(Vector2d oldPosition, Vector2d newPosition,Animal animal);
    void animalDeath(Animal animal);
    void avgDaysAlive(int daysAlive);

}
