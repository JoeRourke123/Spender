package checkpoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.lang.Math;

public class Budget {
    private ArrayList<Transaction> budget = new ArrayList<>();
    private HashMap<String, Category> categories = new HashMap<>();

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
        return Math.abs(totalOut);
    }

    public double getTotalIn() {
        return totalIn;
    }

    public Budget() {
        this.loadCategories();
        this.loadFile();
    }

    public void loadCategories() {
        try {
            Scanner file = new Scanner(new File("budgets.csv"));

            while(file.hasNextLine()) {
                String[] temp = file.nextLine().split(",", 2);
                categories.put(temp[0], new Category(temp[0], Double.parseDouble(temp[1])));
            }
        } catch(FileNotFoundException e) {
            System.err.println("Categories couldn't be read :(");
        }
    }

    public void loadFile() {
        try {
            Scanner file = new Scanner(new File("transactions.csv"));
            while(file.hasNextLine()) {
                String[] temp = file.nextLine().split(",", 4);
                double amount = Double.parseDouble(temp[0]);

                if(categories.containsKey(temp[1])) {
                    categories.get(temp[1]).addTransaction(amount);
                }

                budget.add(new Transaction(amount, temp[1], temp[2], temp[3]));
                if(amount >= 0) {
                    this.incrementTotalIn(amount);
                }
                else {
                    this.incrementTotalOut(amount);
                }
            }
        }
        catch (FileNotFoundException e) {
            System.err.println(e);
        }
    }

    public double getCashFlow() {
        return getTotalIn() - getTotalOut();
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

    public void addCategory(String title, double limit) {
        categories.put(title, new Category(
                title, limit, this.getCategoryTotal(title)
        ));

        appendToFile("budgets", categories.get(title));
    }

    public Category getCategory(String name) {
        return categories.get(name);
    }

    public Category[] getCategories() {
        return categories.values().toArray(new Category[0]);
    }

    public void addTransaction(double amount, String category, String title, String date) {
        Transaction newTransaction = new Transaction(amount, category, title, date);
        this.budget.add(newTransaction);
        if(newTransaction.getExpense()) {
            this.incrementTotalOut(Math.abs(amount));
        }
        else {
            this.incrementTotalIn(amount);
        }

        appendToFile("transactions", newTransaction);
    }

    public void appendToFile(String file, Object o) {
        try {
            FileWriter writer = new FileWriter(file + ".csv", true);

            writer.write(o.toString() + "\n");
            writer.close();
        } catch(IOException e) {
            System.err.println(e);
        }
    }

    public void removeCategory(Category category) {
        categories.remove(category.getName());

        try {
            FileWriter writer = new FileWriter("budgets.csv");
            writer.write("");
            writer.close();

            for(Category c : getCategories()) {
                appendToFile("budgets", c);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void removeTransaction(Transaction transaction) {
        this.budget.remove(transaction);
        try {
            FileWriter writer = new FileWriter("transactions.csv");
            writer.write("");
            writer.close();

            for(Transaction t : getBudget()) {
                appendToFile("transactions", t);
            }

            if(transaction.getExpense()) {
                decrementTotalOut(transaction.getAmount());
            } else {
                decrementTotalIn(transaction.getAmount());
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
        this.setTotalOut(this.getTotalOut() + Math.abs(increment));
    }

    public void decrementTotalIn(double decrement) {
        this.setTotalIn(this.getTotalIn() - Math.abs(decrement));
    }
    public void decrementTotalOut(double decrement) {
        this.setTotalOut(this.getTotalOut() - Math.abs(decrement));
    }
}
