package passenger;

public interface Refundable {

    boolean refundToAccount(String accountNumber, double amount);

    double getBalanceOf(String accountNumber);
}
