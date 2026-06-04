public class PassengerWagon extends Wagon {
    private String typeName;
    private double multiplier;
    private int totalSeats;
    private int occupiedSeats;
    private double pricePerKm;
    private boolean hasTv;
    private boolean hasPhone;
    private double beddingPrice;

    public PassengerWagon(int number, String typeName, double multiplier,
                          int totalSeats, double pricePerKm,
                          boolean hasTv, boolean hasPhone, double beddingPrice) {
        super(number);
        this.typeName = typeName;
        this.multiplier = multiplier;
        this.totalSeats = totalSeats;
        this.pricePerKm = pricePerKm;
        this.hasTv = hasTv;
        this.hasPhone = hasPhone;
        this.beddingPrice = beddingPrice;
    }

    public int getTotalSeats() { return totalSeats; }
    public int getOccupiedSeats() { return occupiedSeats; }
    public boolean hasFreeSeats() { return occupiedSeats < totalSeats; }
    public boolean hasTv() { return hasTv; }
    public boolean hasPhone() { return hasPhone; }

    public void occupySeat() {
        if (hasFreeSeats()) {
            occupiedSeats++;
        }
    }

    public double calculatePrice(double distance, boolean wantBedding) {
        double surcharge = 0;
        if (hasTv) surcharge += 0.10;    // телевизор +10%
        if (hasPhone) surcharge += 0.05; // телефон +5%
        double price = distance * pricePerKm * multiplier * (1 + surcharge);
        if (wantBedding) price += beddingPrice;
        return price;
    }

    public String getTypeName() { return typeName; }

    public String getCategory() { return "Пассажирский"; }

    public String getState() {
        return "занято " + occupiedSeats + " из " + totalSeats + " мест";
    }
}
