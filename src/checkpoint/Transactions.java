package checkpoint;

import java.util.Date;

public class Transaction {
    private double amount;
    private String category;
    private String title;
    private String time;

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
}
