package passenger;

public class PaymentAccount {

    private String accountNumber;
    private String accountName;
    private String password;
    private double balance;

    public PaymentAccount(String accountNumber, String accountName, String password, double balance) {
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.password = password;
        this.balance = balance;
    }

    public boolean checkAccount(String accountNumber, String password) {
        return this.accountNumber.equals(accountNumber) && this.password.equals(password);
    }

    public boolean checkAccount(String accountNumber, String accountName, String password) {
        return checkAccount(accountNumber, password) && (accountName == null || this.accountName.equalsIgnoreCase(accountName));
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public boolean hasEnoughBalance(double amount) {
        return balance >= amount;
    }

    public boolean withdraw(double amount) {
        boolean done = false;

        if (amount >= 0 && hasEnoughBalance(amount)) {
            balance = balance - amount;
            done = true;
        }

        return done;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance = balance + amount;
        }
    }
}
