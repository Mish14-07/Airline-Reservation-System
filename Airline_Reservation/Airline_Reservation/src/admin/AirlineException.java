package admin;

public class AirlineException extends Exception {

    private final String title;
    private final boolean endsSession;

    public AirlineException(String message) {
        this("Error", message, false);
    }

    public AirlineException(String title, String message, boolean endsSession) {
        super(message);
        this.title = title;
        this.endsSession = endsSession;
    }

    public String getTitle() { return title; }
    public boolean endsSession() { return endsSession; }
}
