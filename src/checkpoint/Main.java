package checkpoint;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Date;

public class Main extends Application {
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
        vbox.getChildren().addAll(totalIns, totalOuts, cashFlow, newTransactionBtn);

        stage.setTitle("Budgeting");
        Scene scene = new Scene(grid, 900, 500);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
