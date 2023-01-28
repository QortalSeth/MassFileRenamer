open module Editor{
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    exports application to javafx.graphics;
}

//--module-path "/lib/jvm/javafx-sdk-19/lib" --addRow-modules=javafx.base,javafx.controls,javafx.fxml