package checkpoint;

import javafx.application.Application;
import java.text.DecimalFormat;

import javafx.beans.value.ChangeListener;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

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
        // buildAnalysis();

        stage.setScene(edit);
        stage.setResizable(false);
        stage.show();
    }
}
