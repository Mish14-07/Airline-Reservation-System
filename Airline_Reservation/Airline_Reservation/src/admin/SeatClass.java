package admin;

public class SeatClass {

    private final String name;
    private final double fareMultiplier;
    private String baggage;
    private String meal;

    public SeatClass(String name, double fareMultiplier, String baggage, String meal) {
        this.name = name;
        this.fareMultiplier = fareMultiplier;
        this.baggage = baggage;
        this.meal = meal;
    }

    public static SeatClass[] defaults() {
        return new SeatClass[]{ new SeatClass("Economy", 1, "7 kg hand-carry", "No meal"), new SeatClass("Business", 2, "30 kg checked baggage", "Meal included"), new SeatClass("First Class", 3, "40 kg checked baggage", "Premium meal") };
    }

    public String getName() { return name; }
    public double getFareMultiplier() { return fareMultiplier; }
    public String getBaggage() { return baggage; }
    public String getMeal() { return meal; }

    public void setPerks(String baggage, String meal) {
        this.baggage = baggage;
        this.meal = meal;
    }
}
