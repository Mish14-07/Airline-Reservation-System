package passenger;

public abstract class PaymentMethods implements Payable, Refundable {

    public abstract String getPayment();

    @Override
    public abstract boolean processPay(double amount);

    @Override
    public abstract boolean hasEnoughBalance(double amount);

    public String getAccountNumber() {
        return "";
    }

    @Override
    public boolean refundToAccount(String accountNumber, double amount) {
        return false;
    }

    public double getBalance() {
        return 0;
    }

    @Override
    public double getBalanceOf(String accountNumber) {
        return -1;
    }
}

abstract class OnlinePayment extends PaymentMethods {

    protected PaymentAccount verifiedAccount;

    protected abstract PaymentAccount[] getAccounts();

    protected boolean verifyLogin(String number, String accountName, String password) {

        verifiedAccount = null;

        for (PaymentAccount account : getAccounts()) {
            if (account.checkAccount(number, accountName, password)) {
                verifiedAccount = account;
            }
        }

        return verifiedAccount != null;
    }

    private PaymentAccount findAccount(String accountNumber) {
        PaymentAccount found = null;

        for (PaymentAccount account : getAccounts()) {
            if (account.getAccountNumber().equals(accountNumber)) {
                found = account;
            }
        }

        return found;
    }

    @Override
    public String getAccountNumber() {
        if (verifiedAccount != null) {
            return verifiedAccount.getAccountNumber();
        } else {
            return "";
        }
    }

    @Override
    public double getBalance() {
        if (verifiedAccount != null) {
            return verifiedAccount.getBalance();
        } else {
            return 0;
        }
    }

    @Override
    public double getBalanceOf(String accountNumber) {
        PaymentAccount account = findAccount(accountNumber);

        if (account != null) {
            return account.getBalance();
        } else {
            return -1;
        }
    }

    @Override
    public boolean hasEnoughBalance(double amount) {
        if (verifiedAccount != null) {
            return verifiedAccount.hasEnoughBalance(amount);
        } else {
            return false;
        }
    }

    @Override
    public boolean processPay(double amount) {
        boolean paid = false;

        if (verifiedAccount != null && verifiedAccount.withdraw(amount)) {
            paid = true;
        }

        return paid;
    }

    @Override
    public boolean refundToAccount(String accountNumber, double amount) {
        boolean refunded = false;
        PaymentAccount account = findAccount(accountNumber);

        if (account != null) {
            account.deposit(amount);
            refunded = true;
        }

        return refunded;
    }
}

abstract class eWallet extends OnlinePayment {

    public boolean verifyAccount(String mobileNumber, String accountName, String password) {

        return verifyLogin(mobileNumber, accountName, password);
    }
}

class CardPayment extends OnlinePayment {

    private static final PaymentAccount[] accounts = { new PaymentAccount("1234567890123456", "Alex Santos", "card123", 200000), new PaymentAccount("2345678901234567", "Jamie Cruz", "card456", 15000), new PaymentAccount("3456789012345678", "Taylor Reyes", "card789", 10000) };

    @Override
    public String getPayment() {
        return "Credit / Debit Card";
    }

    @Override
    protected PaymentAccount[] getAccounts() {
        return accounts;
    }

    public boolean verifyAccount(String cardNumber, String password) {
        return verifyLogin(cardNumber, null, password);
    }
}

class GCashPayment extends eWallet {

    private static final PaymentAccount[] accounts = { new PaymentAccount("09123456891", "Alex Santos", "gcash123", 100000), new PaymentAccount("09281234567", "Jamie Cruz", "gcash456", 8000), new PaymentAccount("09391234567", "Taylor Reyes", "gcash789", 5000) };

    @Override
    public String getPayment() {
        return "GCash";
    }

    @Override
    protected PaymentAccount[] getAccounts() {
        return accounts;
    }

}

class MayaPayment extends eWallet {

    private static final PaymentAccount[] accounts = { new PaymentAccount("09181234567", "Alex Santos", "maya123", 30000), new PaymentAccount("09281234567", "Jamie Cruz", "maya456", 8000), new PaymentAccount("09381234567", "Taylor Reyes", "maya789", 5000) };

    @Override
    public String getPayment() {
        return "Maya";
    }

    @Override
    protected PaymentAccount[] getAccounts() {
        return accounts;
    }

}

class OTCPayment extends PaymentMethods {

    @Override
    public String getPayment() {
        return "Over-the-Counter (OTC)";
    }

    @Override
    public boolean processPay(double amount) {
        return false;
    }

    @Override
    public boolean hasEnoughBalance(double amount) {
        return true;
    }
}
