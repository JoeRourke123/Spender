package checkpoint;

import java.util.Date;

/**
 * Used to store details about each transaction in the CSV file
 */
public class Transaction {
    private double amount;
    private String category;
    private String title;
    private String time;

    /**
     * @param amount Positive or negative double to store the amount of the transaction
     * @param category The type of payment, i.e. shopping, bill, income
     * @param title The name of the payment, i.e. Tesco, Water bill
     * @param time The time the payment took place, DD-MM-YYYY
     */
    public Transaction(double amount, String category, String title, String time) {
        this.amount = amount;
        this.category = category;
        this.title = title;
        this.time = time;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean getExpense() { return amount < 0; }

    @Override
    public String toString() {
        return String.format("%f,%s,%s,%s", this.amount, this.category, this.title, this.time);
    }
}
