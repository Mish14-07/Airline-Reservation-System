package passenger;

public class PaymentRefund {

    private PaymentMethods methodNamed(String paymentMethod) {
        PaymentMethods method = null;

        if ("Credit / Debit Card".equals(paymentMethod)) {
            method = new CardPayment();
        } else if ("GCash".equals(paymentMethod)) {
            method = new GCashPayment();
        } else if ("Maya".equals(paymentMethod)) {
            method = new MayaPayment();
        }

        return method;
    }

    public boolean refund(String paymentMethod, String accountNumber,double amount) {
        PaymentMethods method = methodNamed(paymentMethod);
        boolean refunded = false;
        if (method != null) {
            refunded = method.refundToAccount(accountNumber, amount);
        }
        return refunded;
    }

    public double balanceOf(String paymentMethod, String accountNumber) {
        PaymentMethods method = methodNamed(paymentMethod);
        double balance = -1;

        if (method != null) {
            balance = method.getBalanceOf(accountNumber);
        }

        return balance;
    }
}
