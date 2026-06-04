import java.util.ArrayList;

public class Train {
    private int number;
    private Station departure;
    private Station destination;
    private String departureTime;
    private String arrivalTime;
    private ArrayList<Wagon> wagons = new ArrayList<>();

    public Train(int number, Station departure, Station destination,
                 String departureTime, String arrivalTime) {
        this.number = number;
        this.departure = departure;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public void addWagon(Wagon w) { wagons.add(w); }

    public int getNumber() { return number; }
    public Station getDeparture() { return departure; }
    public Station getDestination() { return destination; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime() { return arrivalTime; }
    public ArrayList<Wagon> getWagons() { return wagons; }

    public double getRouteDistance() { return departure.distanceTo(destination); }

    public ArrayList<PassengerWagon> getPassengerWagons() {
        ArrayList<PassengerWagon> result = new ArrayList<>();
        for (Wagon w : wagons) {
            if (w instanceof PassengerWagon) {
                result.add((PassengerWagon) w);
            }
        }
        return result;
    }

    public boolean hasWagonNamed(String typeName) {
        for (Wagon w : wagons) {
            if (w.getTypeName().equals(typeName)) return true;
        }
        return false;
    }

    public int getTotalPassengers() {
        int sum = 0;
        for (PassengerWagon w : getPassengerWagons()) sum += w.getOccupiedSeats();
        return sum;
    }

    public int getTotalSeats() {
        int sum = 0;
        for (PassengerWagon w : getPassengerWagons()) sum += w.getTotalSeats();
        return sum;
    }

    public String toString() {
        return "Поезд №" + number + "  " + departure.getName()
             + " -> " + destination.getName()
             + "  (" + departureTime + " - " + arrivalTime + ")";
    }
}
