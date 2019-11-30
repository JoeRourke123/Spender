package checkpoint;

import javafx.application.Application;
import java.text.DecimalFormat;

import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.*;

public class Main extends Application {
    private Stage stage;
    private Scene edit, analysis;
    private Button switchView = new Button("Analysis");
    private Budget budget = new Budget();
    private Label totalIns = new Label(), totalOuts = new Label(), cashFlow = new Label();

    /**
     *  Simply updates the label values on the edit scene to be the current values
     *  of the total incoming, total outgoing, and cash flow values from the Budget instance
     */
    private void setLabels() {
        DecimalFormat dec = new DecimalFormat("#.00");

        totalIns.setText("Total Incoming: £" + dec.format(budget.getTotalIn()));
        totalOuts.setText("Total Outgoings: £" + dec.format(budget.getTotalOut()));
        cashFlow.setText("Cash Flow: £" + dec.format(budget.getCashFlow()));
    }

    /**
     *  Creates a new transaction popup window to allow for a new transaction to be added, separated from the rest of
     *  the code to improve modularity and readability of the code
     *
     *  @param  e   An instance of the ActionEvent class, passed by the setOnAction callback from the new Transaction button
     */
    private void newTransactionWindow(ActionEvent e) {
        Stage newTransactionStage = new Stage();
        VBox newTransactionBox = new VBox();
        newTransactionBox.setSpacing(10);
        newTransactionBox.setAlignment(Pos.CENTER);

        TextField titleField = new TextField(), amountField = new TextField(), categoryField = new TextField();
        titleField.setMaxWidth(150); amountField.setMaxWidth(150); categoryField.setMaxWidth(150);
        titleField.setPromptText("Title"); amountField.setPromptText("£"); categoryField.setPromptText("Category");

        ToggleGroup inOutGroup = new ToggleGroup();
        ToggleButton incoming = new ToggleButton("Incoming");
        incoming.setSelected(true);
        incoming.setToggleGroup(inOutGroup);

        ToggleButton outgoing = new ToggleButton("Outgoing");
        outgoing.setToggleGroup(inOutGroup);

        TextField day = new TextField(), month = new TextField(), year = new TextField();
        day.setMaxWidth(40); month.setMaxWidth(40); year.setMaxWidth(55);
        day.setPromptText("DD"); month.setPromptText("MM"); year.setPromptText("YYYY");

        HBox dateField = new HBox(), incomingOutgoingBox = new HBox();
        dateField.getChildren().addAll(day, month, year);
        dateField.setSpacing(5); dateField.setMaxWidth(150);

        incomingOutgoingBox.getChildren().addAll(incoming, outgoing);
        incomingOutgoingBox.setSpacing(5); incomingOutgoingBox.setMaxWidth(150);

        Button save = new Button("Save");
        save.setOnAction((f) -> {
            if(!amountField.getText().isEmpty() && !titleField.getText().isEmpty() && !categoryField.getText().isEmpty()) {
                double amount = (inOutGroup.getSelectedToggle().equals(incoming)) ? Double.valueOf(amountField.getText()) : -Double.valueOf(amountField.getText());

                budget.addTransaction(
                        amount,
                        categoryField.getText(),
                        titleField.getText(),
                        (day.getText() + "-" + month.getText() + "-" + year.getText())
                );

                ((TableView) edit.lookup("TableView")).getItems().add(budget.getBudget().get(budget.getBudget().size() - 1));
                newTransactionStage.close();
                setLabels();
            }
        });

        newTransactionBox.getChildren().addAll(
                titleField,
                incomingOutgoingBox,
                amountField, categoryField,
                dateField,
                save
        );

        newTransactionStage.setScene(new Scene(newTransactionBox, 200, 250));
        newTransactionStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Simply defines the structure and elements for the edit view, adding them to the build Scene instance
     */
    public void buildEdit() {
        setLabels();
        GridPane grid = new GridPane();
        VBox vbox = new VBox();
        TableView table = new TableView();

        ColumnConstraints statCol = new ColumnConstraints(), tblCol = new ColumnConstraints();
        RowConstraints rows = new RowConstraints();
        statCol.setPercentWidth(25);
        tblCol.setPercentWidth(75);
        rows.setPrefHeight(500);
        grid.getColumnConstraints().addAll(statCol, tblCol);
        grid.getRowConstraints().addAll(rows);

        table.setEditable(true);
        TableColumn<String, Transaction> transactionDateCol = new TableColumn<>("Date");
        transactionDateCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<String, Transaction> transactionTitleCol = new TableColumn<>("Title");
        transactionTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<String, Transaction> transactionAmountCol = new TableColumn<>("Amount");
        transactionAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<String, Transaction> transactionCategoryCol = new TableColumn<>("Category");
        transactionCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        table.getColumns().addAll(transactionDateCol, transactionTitleCol, transactionAmountCol, transactionCategoryCol);

        for(Transaction transaction : budget.getBudget()) {
            table.getItems().add(transaction);
        }

        grid.add(vbox, 0, 0);
        grid.add(table, 1, 0);

        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);

        Button newTransactionBtn = new Button("+ New");
        newTransactionBtn.setOnAction(this::newTransactionWindow);
        newTransactionBtn.setPrefWidth(100);

        Button deleteTransactionBtn = new Button("- Delete");
        deleteTransactionBtn.setPrefWidth(100);
        deleteTransactionBtn.setDisable(true);
        deleteTransactionBtn.setOnAction((e) -> {
            Transaction toDelete = budget.getBudget().get(table.getSelectionModel().getFocusedIndex());
            table.getItems().remove(toDelete);
            budget.removeTransaction(toDelete);
            setLabels();
        });

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                deleteTransactionBtn.setDisable(false);
            } else deleteTransactionBtn.setDisable(true);
        });

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(5);
        buttonBox.getChildren().addAll(newTransactionBtn, deleteTransactionBtn);

        Button switchView = new Button("Analysis");
        switchView.setOnAction(this::switchAction);
        vbox.getChildren().addAll(totalIns, totalOuts, cashFlow, buttonBox, switchView);

        table.setRowFactory(tableView -> {
            TableRow<Transaction> row = new TableRow<>();

            row.itemProperty().addListener((obs, prevTransaction, newTransaction) -> {
                if (newTransaction != null) {
                    row.pseudoClassStateChanged(PseudoClass.getPseudoClass("in"), !newTransaction.getExpense());
                    row.pseudoClassStateChanged(PseudoClass.getPseudoClass("out"), newTransaction.getExpense());
                }
            });
            return row;
        });

        stage.setTitle("Budgeting");
        edit = new Scene(grid, 900, 500);
        edit.getStylesheets().add("style.css");
    }

    //Good luck ever reading this, I have created a monster
    public void buildAnalysis() {

        VBox root = new VBox(5);
        root.setAlignment(Pos.TOP_CENTER);

        //Layout for categorised data
        HBox categoryLayout = new HBox(5);
        categoryLayout.setAlignment(Pos.CENTER_LEFT);
        //Builds the first table for categories
        TableView catTable = new TableView();
        catTable.setPrefWidth(350);
        catTable.setEditable(true);
        TableColumn<String, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.prefWidthProperty().bind(catTable.widthProperty().multiply(0.5));
        TableColumn<String, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.prefWidthProperty().bind(catTable.widthProperty().multiply(0.5));
        catTable.getColumns().add(categoryColumn);
        catTable.getColumns().add(amountColumn);
        LinkedList<String> categories = new LinkedList<>();
        for(Transaction transaction : this.budget.getBudget()) {
            if(!categories.contains(transaction.getCategory())) {
                categories.add(transaction.getCategory());
                catTable.getItems().add(new Category(transaction.getCategory(), this.budget.getCategoryTotal(transaction.getCategory())));
            }
        }
        //Builds PieCharts with categorised data
        PieChart categoryIncomePie = new PieChart();
        PieChart categoryExpensePie = new PieChart();
        ObservableList<PieChart.Data> categoryIncomeData = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> categoryExpenseData = FXCollections.observableArrayList();
        for (String next : categories) {
            if (this.budget.getCategoryTotal(next) >= 0) {
                categoryIncomeData.add(new PieChart.Data(next, this.budget.getCategoryTotal(next)));
            } else {
                categoryExpenseData.add(new PieChart.Data(next, this.budget.getCategoryTotal(next)));
            }
        }
        categoryIncomePie.setData(categoryIncomeData);
        categoryExpensePie.setData(categoryExpenseData);

        //Layout for raw data
        HBox overallLayout = new HBox(5);
        //Builds the second table with raw data
        TableView table = new TableView();
        table.setPrefWidth(350);
        table.setEditable(true);
        TableColumn<String, Transaction> transactionDateCol = new TableColumn<>("Date");
        transactionDateCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        transactionDateCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        TableColumn<String, Transaction> transactionTitleCol = new TableColumn<>("Title");
        transactionTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        transactionTitleCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        TableColumn<String, Transaction> transactionAmountCol = new TableColumn<>("Amount");
        transactionAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        transactionAmountCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        TableColumn<String, Transaction> transactionCategoryCol = new TableColumn<>("Category");
        transactionCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        transactionCategoryCol.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        table.getColumns().addAll(transactionDateCol, transactionTitleCol, transactionAmountCol, transactionCategoryCol);
        for(Transaction transaction : budget.getBudget()) {
            table.getItems().add(transaction);
        }
        //Builds PieCharts with raw data
        PieChart incomePie = new PieChart();
        PieChart expensePie = new PieChart();
        ObservableList<PieChart.Data> incomeData = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> expenseData = FXCollections.observableArrayList();
        for(Transaction transaction : this.budget.getBudget()) {
            if(transaction.getAmount() >= 0) {
                incomeData.add(new PieChart.Data(transaction.getTitle(), transaction.getAmount()));
            }
            else {
                expenseData.add(new PieChart.Data(transaction.getTitle(), transaction.getAmount()));
            }
        }
        incomePie.setData(incomeData);
        expensePie.setData(expenseData);

        //Layout for labelled data
        HBox totalLayout = new HBox(100);
        //Set the labels text and size
        Label totalInOut = new Label("The total income:   £" + this.budget.getTotalIn() + "\nThe total expense: £" + this.budget.getTotalOut());
        totalInOut.setFont(Font.font("Arial", 30));
        Label totals = new Label("The total number of transactions: " + this.budget.getBudget().size() + "\nThe total profits: £" + this.budget.getCashFlow());
        totals.setFont(Font.font("Arial", 30));

        //Populate the layouts
        totalLayout.getChildren().addAll(totalInOut, totals);
        overallLayout.getChildren().addAll(table, incomePie, expensePie);
        categoryLayout.getChildren().addAll(catTable, categoryIncomePie, categoryExpensePie);

        //Root layout, populate with other layouts and button
        root.getChildren().addAll(categoryLayout, overallLayout, totalLayout);

        analysis = new Scene(root);
    }

    private void switchAction(ActionEvent e) {
        stage.setScene((switchView.getText().equals("Analysis")) ? analysis : edit);
    }

    /**
     * Calls the methods to build the separate views' Scene objects, and defines functionality for the switcher button
     * @param primaryStage  An instance of the stage object, is the main stage for the program and is passed by launch, called from main
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        switchView.setOnAction((e) -> {
            stage.setScene((switchView.getText().equals("Analysis")) ? analysis : edit);
            switchView.setText((switchView.getText().equals("Analysis")) ? "Edit" : "Analysis");
        });

        buildEdit();
        buildAnalysis();

        stage.setScene(edit);
//        stage.setResizable(false);
        stage.show();
    }

    //Class needed for each category so I could add it to the table
    //Better methods/suggestions for doing this would be appreciated
    public class Category {
        private String name;
        private double amount;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        Category(String name, double amount) {
            this.name = name;
            this.amount = amount;
        }
    }
}
