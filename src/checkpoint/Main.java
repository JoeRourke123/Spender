package checkpoint;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {
    private Stage stage;
    private Scene edit, analysis;
    private Budget budget = new Budget();

    public void createPopup() {
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
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
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
                }
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

    public void buildEdit() {

        GridPane grid = new GridPane();
        VBox vbox = new VBox();
        TableView table = new TableView();

        ColumnConstraints statCol = new ColumnConstraints(), tblCol = new ColumnConstraints();
        RowConstraints row = new RowConstraints();
        statCol.setPercentWidth(25);
        tblCol.setPercentWidth(75);
        row.setPrefHeight(500);
        grid.getColumnConstraints().addAll(statCol, tblCol);
        grid.getRowConstraints().addAll(row);

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

        Label totalIns = new Label("Total Incomings: £");
        Label totalOuts = new Label("Total Outgoings: £");
        Label cashFlow = new Label("Cash Flow: £");
        totalIns.setText(totalIns.getText() + "24.54");

        Button newTransactionBtn = new Button("+ New");
        newTransactionBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                createPopup();
            }
        });
        newTransactionBtn.setPrefWidth(100);

        Button deleteTransactionBtn = new Button("- Delete");
        deleteTransactionBtn.setPrefWidth(100);

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(5);
        buttonBox.getChildren().addAll(newTransactionBtn, deleteTransactionBtn);

        vbox.getChildren().addAll(totalIns, totalOuts, cashFlow, buttonBox);

        stage.setTitle("Budgeting");
        edit = new Scene(grid, 900, 500);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        buildEdit();
        // buildAnalysis();

        stage.setScene(edit);
        stage.setResizable(false);
        stage.show();
    }
}
