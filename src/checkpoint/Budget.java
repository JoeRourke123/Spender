package checkpoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.lang.Math;

/**
 * Store of all the transactions that the user has made with useful methods for interacting with the data, such as:
 *      - Importing and exporting transactions from a file
 *      - Calculating the sum of each category
 *      - Handling addition and removal of new categories
 *      - Adding new transactions
 *      - Removing unwanted transactions
 */
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

    /**
     * Create a new instance of a budget
     */
    public Budget() {
        this.loadCategories();
        this.loadFile();
    }

    /**
     * Loads categories from file
     */
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

    /**
     * Loads transactions from file
     */
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

    /**
     * Calculate the total cash money
     * @return Total cash money amount
     */
    public double getCashFlow() {
        return getTotalIn() - getTotalOut();
    }

    /**
     * Work out the total money for that category
     * @param category The category to calculate for
     * @return The total cash money for the specified category
     */
    public double getCategoryTotal(String category) {
        double sum = 0.0;
        for (Transaction transaction: this.budget) {
            if(transaction.getCategory().equals(category)) {
                sum += transaction.getAmount();
            }
        }
        return sum;
    }

    /**
     * Add a new category with specified metadata
     * @param title Name of the category
     * @param limit Spend limit of the category
     */
    public void addCategory(String title, double limit) {
        categories.put(title, new Category(
                title, limit, this.getCategoryTotal(title)
        ));

        appendToFile("budgets", categories.get(title));
    }

    /**
     * Get a category object from the name of a category
     * @param name of the category to find
     * @return the category object
     */
    public Category getCategory(String name) {
        return categories.get(name);
    }

    /**
     * Get an array of categories
     * @return the array of the categories
     */
    public Category[] getCategories() {
        return categories.values().toArray(new Category[0]);
    }

    /**
     * Add a transaction to a category with chosen data
     * @param amount of the transaction to add
     * @param category of the transaction to add
     * @param title of the transaction to add
     * @param date time the transaction took place
     */
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

    /**
     * Add a new item to the file on disk
     * @param file name to write to
     * @param o the object to write to disk
     */
    public void appendToFile(String file, Object o) {
        try {
            FileWriter writer = new FileWriter(file + ".csv", true);

            writer.write(o.toString() + "\n");
            writer.close();
        } catch(IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Remove a category from the category file on disk
     * @param category to remove from the file
     */
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

    /**
     * Remove a transaction from the transaction file on disk
     * @param transaction
     */
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

    /**
     * Increase the total is accurate
     * @param increment by this amount
     */
    public void incrementTotalIn(double increment) {
        this.setTotalIn(this.getTotalIn() + increment);
    }
    /**
     * Ensure that the total is accurate
     * @param increment by this amount
     */
    public void incrementTotalOut(double increment) {
        this.setTotalOut(this.getTotalOut() + Math.abs(increment));
    }

    /**
     * Reduce the total for the budget
     * @param decrement the total by this amount
     */
    public void decrementTotalIn(double decrement) {
        this.setTotalIn(this.getTotalIn() - Math.abs(decrement));
    }
    /**
     * Reduce the total for the budget
     * @param decrement the total by this amount
     */
    public void decrementTotalOut(double decrement) {
        this.setTotalOut(this.getTotalOut() - Math.abs(decrement));
    }
}
