package checkpoint;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
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
        TableColumn<String, String> transactionDateCol = new TableColumn<>("Date");
        TableColumn<String, String> transactionTitleCol = new TableColumn<>("Title");
        TableColumn<String, Double> transactionAmountCol = new TableColumn<>("Amount");
        TableColumn<String, String> transactionCategoryCol = new TableColumn<>("Category");
        table.getColumns().addAll(transactionDateCol, transactionTitleCol, transactionAmountCol, transactionCategoryCol);

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
