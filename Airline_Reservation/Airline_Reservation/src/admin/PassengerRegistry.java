package admin;

public class PassengerRegistry {

    private int counter = 0;

    public String createNewId() {
        counter = counter + 1;
        return "P" + InputTools.zeroPad(counter, 3);
    }
}
