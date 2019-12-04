package checkpoint;

import javafx.application.Application;
import java.text.DecimalFormat;

import javafx.css.PseudoClass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

                ((TableView) edit.lookup("#transactionsTable")).getItems().add(budget.getBudget().get(budget.getBudget().size() - 1));
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

    private void newCategoryWindow(ActionEvent e) {
        Stage newCategoryStage = new Stage();
        VBox newCategoryBox = new VBox();
        newCategoryBox.setSpacing(10);
        newCategoryBox.setAlignment(Pos.CENTER);

        TextField titleField = new TextField(), limitField = new TextField();
        titleField.setMaxWidth(150); limitField.setMaxWidth(150);
        titleField.setPromptText("Category Title"); limitField.setPromptText("Budget Limit (£)");

        Button save = new Button("Save");
        save.setOnAction((f) -> {
            if(!limitField.getText().isEmpty() && !titleField.getText().isEmpty()) {
                double limit = Math.abs(Double.valueOf(limitField.getText()));

                budget.addCategory(
                        titleField.getText(),
                        limit
                );

                ((TableView) edit.lookup("#categoriesTable")).getItems().add(budget.getCategory(titleField.getText()));
                newCategoryStage.close();
            }
        });

        newCategoryBox.getChildren().addAll(
                titleField,
                limitField,
                save
        );

        newCategoryStage.setScene(new Scene(newCategoryBox, 200, 250));
        newCategoryStage.show();
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
        TableView transactionsTable = new TableView(), categoriesTable = new TableView();

        ColumnConstraints statCol = new ColumnConstraints(), tblCol = new ColumnConstraints(), bdgCol = new ColumnConstraints();
        RowConstraints rows = new RowConstraints();
        statCol.setPercentWidth(30);
        tblCol.setPercentWidth(45);
        bdgCol.setPercentWidth(25);
        rows.setPrefHeight(500);
        grid.getColumnConstraints().addAll(statCol, tblCol, bdgCol);
        grid.getRowConstraints().addAll(rows);

        transactionsTable.setEditable(true);
        TableColumn<String, Transaction> transactionDateCol = new TableColumn<>("Date");
        transactionDateCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<String, Transaction> transactionTitleCol = new TableColumn<>("Title");
        transactionTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<String, Transaction> transactionAmountCol = new TableColumn<>("Amount");
        transactionAmountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<String, Transaction> transactionCategoryCol = new TableColumn<>("Category");
        transactionCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        transactionsTable.getColumns().addAll(transactionDateCol, transactionTitleCol, transactionAmountCol, transactionCategoryCol);

        for(Transaction transaction : budget.getBudget()) {
            transactionsTable.getItems().add(transaction);
        }


        TableColumn<String, Category> categoryTitleColumn = new TableColumn<>("Category");
        categoryTitleColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<String, Category> categoryLimitColumn = new TableColumn<>("Budget");
        categoryLimitColumn.setCellValueFactory(new PropertyValueFactory<>("limit"));

        TableColumn<String, Category> categoryRemainingColumn = new TableColumn<>("Remaining");
        categoryRemainingColumn.setCellValueFactory(new PropertyValueFactory<>("remaining"));

        categoriesTable.getColumns().addAll(categoryTitleColumn, categoryLimitColumn, categoryRemainingColumn);

        for(Category category : budget.getCategories()) {
            categoriesTable.getItems().add(category);
        }


        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);
        vbox.setFillWidth(true);

        Button newTransactionBtn = new Button("+ Transaction");
        newTransactionBtn.setOnAction(this::newTransactionWindow);
        newTransactionBtn.setPrefWidth(100);

        Button deleteTransactionBtn = new Button("- Transaction");
        deleteTransactionBtn.setPrefWidth(100);
        deleteTransactionBtn.setDisable(true);
        deleteTransactionBtn.setOnAction((e) -> {
            Transaction toDelete = budget.getBudget().get(transactionsTable.getSelectionModel().getFocusedIndex());
            transactionsTable.getItems().remove(toDelete);
            budget.removeTransaction(toDelete);
            setLabels();
        });

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(5);
        buttonBox.getChildren().addAll(newTransactionBtn, deleteTransactionBtn);

        HBox categoryBox = new HBox();
        categoryBox.setAlignment(Pos.CENTER);
        categoryBox.setSpacing(5);

        Button newCategoryButton = new Button("+ Category");
        newCategoryButton.setOnAction(this::newCategoryWindow);

        Button delCategoryButton = new Button("- Category");
        delCategoryButton.setDisable(true);
        delCategoryButton.setOnAction((e) -> {
            Category toDelete = budget.getCategories()[categoriesTable.getSelectionModel().getFocusedIndex()];
            categoriesTable.getItems().remove(toDelete);
            budget.removeCategory(toDelete);
        });

        categoryBox.getChildren().addAll(newCategoryButton, delCategoryButton);

        categoriesTable.setId("categoriesTable");
        categoriesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            deleteTransactionBtn.setDisable(true);
            if (newSelection != null) {
                delCategoryButton.setDisable(false);
            } else delCategoryButton.setDisable(true);
        });

        transactionsTable.setId("transactionsTable");
        transactionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            delCategoryButton.setDisable(true);
            if (newSelection != null) {
                deleteTransactionBtn.setDisable(false);
            } else deleteTransactionBtn.setDisable(true);
        });

        Button switchView = new Button("Analysis");
        switchView.setOnAction(this::switchAction);
        vbox.getChildren().addAll(totalIns, totalOuts, cashFlow, buttonBox, categoryBox, switchView);

        transactionsTable.setRowFactory(tableView -> {
            TableRow<Transaction> row = new TableRow<>();

            row.itemProperty().addListener((obs, prevTransaction, newTransaction) -> {
                if (newTransaction != null) {
                    row.pseudoClassStateChanged(PseudoClass.getPseudoClass("in"), !newTransaction.getExpense());
                    row.pseudoClassStateChanged(PseudoClass.getPseudoClass("out"), newTransaction.getExpense());
                }
            });
            return row;
        });

        grid.add(vbox, 0, 0);
        grid.add(transactionsTable, 1, 0);
        grid.add(categoriesTable, 2, 0);

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
                categoryExpenseData.add(new PieChart.Data(next, Math.abs(this.budget.getCategoryTotal(next))));
            }
        }
        categoryIncomePie.setData(categoryIncomeData);
        categoryExpensePie.setData(categoryExpenseData);

        //Layout for raw data
        HBox overallLayout = new HBox(20);
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
                expenseData.add(new PieChart.Data(transaction.getTitle(), Math.abs(transaction.getAmount())));
            }
        }
        incomePie.setData(incomeData);
        expensePie.setData(expenseData);

        //Layout for labelled data
        HBox totalLayout = new HBox(50);
        totalLayout.setAlignment(Pos.CENTER);
        //Set the labels text and size
        Label totalIn = new Label("Total Income: £" + this.budget.getTotalIn());
        totalIn.setFont(Font.font("Arial", 14));

        Label totalOut = new Label("Total Outgoings: £" + this.budget.getTotalOut());
        totalOut.setFont(Font.font("Arial", 14));

        Label totalCF = new Label("Cash Flow: £" + (new DecimalFormat("#.00")).format(this.budget.getCashFlow()));
        totalCF.setFont(Font.font("Arial", 14));

        Label totals = new Label("Transaction Count: " + this.budget.getBudget().size());
        totals.setFont(Font.font("Arial", 14));
        int count = 0;
        for(Transaction transaction : this.budget.getBudget()) { if(transaction.getExpense()) { count++; } }
        Label avg = new Label("Average Item Spend: £" + this.budget.getTotalOut()/count);
        avg.setFont(Font.font("Arial", 14));

        Button changeView = new Button("Edit");
        changeView.setOnAction(this::switchAction);

        //Populate the layouts
        totalLayout.getChildren().addAll(changeView, totalIn, totalOut, totals, totalCF, avg);
        overallLayout.getChildren().addAll(table, incomePie, expensePie);
        categoryLayout.getChildren().addAll(catTable, categoryIncomePie, categoryExpensePie);

        //Root layout, populate with other layouts and button
        root.getChildren().addAll(categoryLayout, overallLayout, totalLayout);

        analysis = new Scene(root);
        analysis.getStylesheets().add("style.css");

    }

    private void switchAction(ActionEvent e) {
        stage.setScene((stage.getScene().equals(edit)) ? analysis : edit);
    }

    /**
     * Calls the methods to build the separate views' Scene objects, and defines functionality for the switcher button
     * @param primaryStage  An instance of the stage object, is the main stage for the program and is passed by launch, called from main
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        buildEdit();
        buildAnalysis();

        stage.setScene(edit);
//        stage.setResizable(false);
        stage.show();
    }
}
