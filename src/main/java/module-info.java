module org.example {
    requires javafx.controls;
    requires javafx.fxml;
	requires java.prefs;
	requires java.net.http;
	requires org.json;
	requires javafx.graphics;
	requires javafx.base;
	requires java.desktop;
	requires com.google.gson;

    opens org.example to javafx.fxml;
    exports org.example;
}