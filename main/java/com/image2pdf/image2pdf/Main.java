package com.image2pdf.image2pdf;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Pos;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class Main extends Application {
    private List<File> selectedImageFiles = new ArrayList<>();
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        Button btnSelectImages = new Button(" \uD83D\uDCC2 ");
        Button btnConvertToPdf = new Button(" \uD83D\uDCBE ");
        Label statusLabel = new Label();
        ListView<String> imageListView = new ListView<>();
        btnSelectImages.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2px; -fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 20px; -fx-background-radius: 15px; -fx-border-radius: 15px;");
        btnConvertToPdf.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 2px; -fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 20px; -fx-background-radius: 15px; -fx-border-radius: 15px;");
        imageListView.setStyle("-fx-background-color: #191970; -fx-text-fill: white; -fx-font-family: 'Tahoma'; -fx-font-size: 14px;");
        statusLabel.setStyle("-fx-font-family: 'Tahoma'; -fx-font-size: 14px; -fx-text-fill: white;");
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: #008080;");
        root.setAlignment(Pos.CENTER);
        btnSelectImages.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"));
            List<File> files = fileChooser.showOpenMultipleDialog(primaryStage);
            if (files != null) {
                for (File imageFile : files) {
                    selectedImageFiles.add(imageFile);
                    imageListView.getItems().add(imageFile.getName());
                }
            }
        });
        btnConvertToPdf.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (!selectedImageFiles.isEmpty()) {
                FileChooser saveFileChooser = new FileChooser();
                saveFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
                saveFileChooser.setInitialFileName("img2pdf.pdf");
                File outputPdfFile = saveFileChooser.showSaveDialog(primaryStage);
                if (outputPdfFile != null) {
                    statusLabel.setText("Working on it...");
                    Task<Void> conversionTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            try {
                                convertImagesToPdf(selectedImageFiles, outputPdfFile);
                            } catch (IOException | DocumentException e) {
                                statusLabel.setText("Error!! " + e.getMessage());
                            }
                            return null;
                        }
                        @Override
                        protected void succeeded() {
                            statusLabel.setText("PDF created successfully!");
                        }
                        @Override
                        protected void failed() {
                            statusLabel.setText("Conversion failed! Please try again.");
                        }
                    };
                    Thread conversionThread = new Thread(conversionTask);
                    conversionThread.setDaemon(true);
                    conversionThread.start();
                }
            } else {
                statusLabel.setText("Please select images first.");
            }
        });
        root.getChildren().addAll(btnSelectImages, imageListView, btnConvertToPdf, statusLabel);
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("Image to PDF Converter");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    private void convertImagesToPdf(List<File> imageFiles, File outputPdfFile) throws IOException, DocumentException {
        FileOutputStream pdfFile = new FileOutputStream(outputPdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, pdfFile);
        document.open();
        for (File imageFile : imageFiles) {
            try {
                Image image = Image.getInstance(imageFile.getAbsolutePath());
                image.scaleToFit(500, 700);
                document.add(image);
                document.newPage();
            } catch (Exception e) {
                throw new IOException("Error processing image: " + imageFile.getName() + " (" + e.getMessage() + ")");
            }
        }
        document.close();
    }
}
