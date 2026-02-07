module com.ad.gestordatos {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.sql;
    requires org.mariadb.jdbc;
    requires static lombok;

    opens com.ad.gestordatos to javafx.fxml;

    exports com.ad.gestordatos;

    exports com.ad.gestordatos.controller;

    opens com.ad.gestordatos.controller to javafx.fxml;

    exports com.ad.gestordatos.model;
    exports com.ad.gestordatos.dao;
}
