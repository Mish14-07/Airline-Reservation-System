package passenger;

public interface Payable {

    boolean processPay(double amount);

    boolean hasEnoughBalance(double amount);
}
