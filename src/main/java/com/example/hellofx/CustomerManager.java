package com.example.hellofx;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CustomerManager extends Application{
    public static class Customer{
        private final StringProperty name = new SimpleStringProperty();
        private final StringProperty province = new SimpleStringProperty();

        public Customer(String name, String province) {
            this.name.set(name);
            this.province.set(province);
        }

        public String getName() {
            return name.get();
        }
        public String getProvince() {
            return province.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public StringProperty provinceProperty() {
            return province;
        }

    }
    private final ObservableList<Customer>customers = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        Label nameLabel= new Label("_Name:");
        nameLabel.setMnemonicParsing(true);
        TextField nameField = new TextField();
        nameField.setPromptText("Customer name");
        nameLabel.setLabelFor(nameField);

        Label provLabel = new Label("_Province:");
        provLabel.setMnemonicParsing(true);
        ComboBox<String> provinceBox = new ComboBox<>(FXCollections.observableArrayList(
                "Northern", "Eastern", "Muchinga", "Lusaka", "Copperbelt", "Central", "North-Western", "Western", "Southern"
        ));
        provinceBox.setPromptText("Select province");
        provLabel.setLabelFor(provinceBox);

        Button addBtn = new Button("_Add");
        addBtn.setDefaultButton(true);
        Button deleteBtn = new Button("_Delete");

        HBox form = new HBox(10, nameLabel, nameField, provLabel, provinceBox,addBtn, deleteBtn);
        form.setPadding(new Insets(10));

        TableView<Customer>table = new TableView<>(customers);
        TableColumn<Customer, String>nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c ->c.getValue().nameProperty());
        TableColumn<Customer, String> provCol = new TableColumn<>("Province");
        provCol.setCellValueFactory(c ->c.getValue().provinceProperty());
        table.getColumns().add(nameCol);
        table.getColumns().add(provCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        deleteBtn.disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        addBtn.setOnAction(e ->{
            String name = nameField.getText().trim();
            String prov = provinceBox.getValue();

            if (name.isEmpty()) {
                showError("Please enter a name");
                nameField.requestFocus();
                return;
            }
            if (!name.matches("[\\p{L}.'-]+")) {
                showError("Please select a province.");
                provinceBox.requestFocus();
                return;
            }
            if (prov == null) {
                showError("Please select a province");
                provinceBox.requestFocus();
                return;
            }

            customers.add(new Customer(name, prov));
            nameField.clear();
            provinceBox.getSelectionModel().clearSelection();
            nameField.requestFocus();
            return;
        });

        deleteBtn.setOnAction(e ->deleteSelected(table));
        table.setOnKeyPressed(e ->{
            if(e.getCode() == KeyCode.DELETE)deleteSelected(table);
        });

        BorderPane root = new BorderPane();
        root.setTop(form);
        root.setCenter(table);

        stage.setTitle("Customer Manager");
        stage.setScene(new Scene(root, 650,400));
        stage.show();
        nameField.requestFocus();
    }

    private void deleteSelected(TableView<Customer>table) {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if(selected==null)return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete" + selected.getName() + "("+selected.getProvince()+ ")?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm deletion");
        confirm.setHeaderText(null);
        confirm.showAndWait()
                .filter(b ->b == ButtonType.YES)
                .ifPresent(b ->customers.remove(selected));
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle("Invald input");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void main (String[] args) {
        launch(args);
    }
}
