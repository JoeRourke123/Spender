package checkpoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Math;

public class Budget {
    private ArrayList<Transaction> budget = new ArrayList<Transaction>();

    private double totalOut;
    private double totalIn;

    public ArrayList<Transaction> getBudget() {
        return budget;
    }
    public void setBudget(ArrayList<Transaction> budget) {
        this.budget = budget;
    }

    private void setTotalOut(double totalOut) {
        this.totalOut = totalOut;
    }

    private void setTotalIn(double totalIn) {
        this.totalIn = totalIn;
    }

    public double getTotalOut() {
        return totalOut;
    }

    public double getTotalIn() {
        return totalIn;
    }

    public Budget() {
        this.loadFile();
    }

    public void loadFile() {
        try {
            Scanner file = new Scanner(new File("transactions.csv"));
            while(file.hasNextLine()) {
                String[] temp = file.nextLine().split(",", 4);
                this.addTransaction(Double.parseDouble(temp[0]), temp[1], temp[2], temp[3]);
                if(Double.parseDouble(temp[0]) >= 0) {
                    this.incrementTotalIn(Double.parseDouble(temp[0]));
                }
                else {
                    this.incrementTotalOut(Math.abs(Double.parseDouble(temp[0])));
                }
            }
        }
        catch (FileNotFoundException e) {
            System.err.println(e);
        }
    }

    public double getCashFlow() {
        double sum = 0.0;
        for(Transaction transaction: this.budget) {
            sum += transaction.getAmount();
        }
        return sum;
    }

    public double getCategoryTotal(String category) {
        double sum = 0.0;
        for (Transaction transaction: this.budget) {
            if(transaction.getCategory().equals(category)) {
                sum += transaction.getAmount();
            }
        }
        return sum;
    }

    public void addTransaction(double amount, String category, String title, String date) {
        Transaction newTransaction = new Transaction(amount, category, title, date);
        this.budget.add(newTransaction);
        if(amount < 0) {
            this.incrementTotalOut(amount);
        }
        else {
            this.incrementTotalIn(amount);
        }
        try {
            FileWriter writer = new FileWriter("transactions.csv", true);
            writer.write(newTransaction.toString());
            writer.close();
        } catch(IOException e) {
            System.err.println(e);
        }

    }

    public void removeTransaction(Transaction transaction) {
        this.budget.remove(transaction);
        try {
            FileWriter writer = new FileWriter("transactions.csv", false);
            writer.write("");
            writer.close();
            writer = new FileWriter("transactions.csv", true);
            for(Transaction transaction1 : this.budget) {
                writer.write(transaction1.toString() + "\n");
            }
        }
        catch (IOException e) {
            System.err.println(e);
        }
    }

    public void incrementTotalIn(double increment) {
        this.setTotalIn(this.getTotalIn() + increment);
    }
    public void incrementTotalOut(double increment) {
        this.setTotalOut(this.getTotalOut() + increment);
    }

    public void decrementTotalIn(double decrement) {
        this.setTotalIn(this.getTotalIn() - Math.abs(decrement));
    }
    public void decrementTotalOut(double decrement) {
        this.setTotalOut(this.getTotalOut() - Math.abs(decrement));
    }
}
