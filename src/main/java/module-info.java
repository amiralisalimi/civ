module civ {
    requires com.google.gson;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;

    opens ir.ap.model to com.google.gson;
    opens ir.ap.network to com.google.gson;
    opens ir.ap.client to javafx.fxml;
    opens ir.ap.client.components to javafx.base, com.google.gson;
    opens ir.ap.client.components.menu to javafx.fxml;
    opens ir.ap.client.network to com.google.gson;
    exports ir.ap.client;
    opens ir.ap.client.components.map to javafx.fxml, com.google.gson;
    opens ir.ap.client.components.map.serializers to javafx.fxml, com.google.gson;
    opens ir.ap.client.components.map.panel to javafx.fxml;
}