package checkpoint;

public class Category {
    private String name;
    private double remaining;
    private double limit;

    Category(String name, double limit) {
        this.name = name;
        this.remaining = limit;
        this.limit = limit;
    }

    Category(String name, double limit, double used) {
        this.name = name;
        this.remaining = limit - Math.abs(used);
        this.limit = limit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRemaining() {
        return remaining;
    }

    public void addTransaction(double amount) { this.remaining += amount; }

    public double getLimit() { return limit; }
    public void setLimit(double limit) { this.limit = limit; }

    @Override
    public String toString() {
        return this.name + "," + this.limit;
    }
}