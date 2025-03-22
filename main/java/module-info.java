module com.image2pdf.image2pdf {
    requires javafx.controls;
    requires javafx.fxml;
    requires itextpdf;


    opens com.image2pdf.image2pdf to javafx.fxml;
    exports com.image2pdf.image2pdf;
}