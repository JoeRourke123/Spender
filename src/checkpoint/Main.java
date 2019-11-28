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
    public void createPopup(Transaction t) {
        createPopup(t.getTitle(), t.getAmount(), t.getCategory(), t.getTime());
    }

    public void createPopup() {
        createPopup("", 0.0, "", "");
    }

    public void createPopup(String title, double amount, String category, String date) {
        Stage newTransactionStage = new Stage();
                VBox newTransactionBox = new VBox();
                newTransactionBox.setSpacing(10);
                newTransactionBox.setAlignment(Pos.CENTER);


                TextField titleField = new TextField(), amountField = new TextField(), categoryField = new TextField();
                titleField.setMaxWidth(150); amountField.setMaxWidth(150); categoryField.setMaxWidth(150);
                titleField.setPromptText("Title"); amountField.setPromptText("£"); categoryField.setPromptText("Category");

                TextField day = new TextField(), month = new TextField(), year = new TextField();
                day.setMaxWidth(40); month.setMaxWidth(40); year.setMaxWidth(55);
                day.setPromptText("DD"); month.setPromptText("MM"); year.setPromptText("YYYY");
                HBox dateField = new HBox();
                dateField.getChildren().addAll(day, month, year);
                dateField.setSpacing(5);
                dateField.setMaxWidth(150);

                if(!title.isEmpty() && amount != 0 && !category.isEmpty() && !date.isEmpty()) {
                    titleField.setText(title);
                    amountField.setText(String.valueOf(amount));
                    categoryField.setText(category);
                    String[] splitDate = date.split("-");
                    day.setText(splitDate[0]); month.setText(splitDate[1]); year.setText(splitDate[2]);
                }

                Button save = new Button("Save");

                newTransactionBox.getChildren().addAll(
                        titleField, amountField, categoryField,
                        dateField,
                        save
                );

                newTransactionStage.setScene(new Scene(newTransactionBox, 200, 200));
                newTransactionStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        Budget budget = new Budget();

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
        transactionDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
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

        Button newTransactionBtn = new Button("+ New Transaction");
        newTransactionBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                createPopup();
            }
        });
        vbox.getChildren().addAll(totalIns, totalOuts, cashFlow, newTransactionBtn);

        stage.setTitle("Budgeting");
        Scene scene = new Scene(grid, 900, 500);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
