package checkpoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Budget {
    public ArrayList<Transaction> getBudget() {
        return budget;
    }

    public void setBudget(ArrayList<Transaction> budget) {
        this.budget = budget;
    }

    private ArrayList<Transaction> budget = new ArrayList<Transaction>();

    public void loadFile() throws FileNotFoundException, ParseException {
        Scanner file = new Scanner(new File("transactions.txt"));
        while(file.hasNextLine()) {
            String[] temp = file.nextLine().split(",", 4);
            SimpleDateFormat format = new SimpleDateFormat("dd-mm-yyyy");
            this.budget.add(new Transaction(Double.parseDouble(temp[0]),temp[1],temp[2], format.parse(temp[3])));
        }
    }

    public Double getTotal() {
        Double sum = 0.0;
        for(Transaction transaction: this.budget) {
            sum += transaction.getAmount();
        }
        return sum;
    }

    public Double getCategoryTotal(String category) {
        Double sum = 0.0;
        for (Transaction transaction: this.budget) {
            if(transaction.getCategory().equals(category)) {
                sum += transaction.getAmount();
            }
        }
        return sum;
    }
}
