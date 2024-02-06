module me.t8d.capstone.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    requires software.amazon.awssdk.services.cognitoidentityprovider;
    requires software.amazon.awssdk.regions;
    requires org.apache.commons.codec;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;

    opens me.t8d.capstone.frontend.controller to javafx.fxml;
    opens me.t8d.capstone.frontend.model to javafx.base, com.fasterxml.jackson.databind;
    exports me.t8d.capstone.frontend.model to com.fasterxml.jackson.databind;
    exports me.t8d.capstone.frontend.utils to com.fasterxml.jackson.databind;

    exports me.t8d.capstone.frontend;

}