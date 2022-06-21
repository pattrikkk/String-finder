package com.example.vlakna;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class HelloApplication extends Application {
    BufferedReader reader;
    TextArea textArea;
    ProgressBar progressBar;
    Label casLabel;
    Thread searchThread, casThread;
    ImageView imageView;
    int maxRiadky;
    String nazovSuboru = "subor.txt";
    @Override
    public void start(Stage stage) throws IOException {
        Group root = new Group();
        Scene scene = new Scene(root, 600, 400);

        stage.setOnCloseRequest(event -> {
            if (searchThread!=null) searchThread.stop();
            if (casThread!=null) casThread.stop();
        });

        TextField textField = new TextField();
        Button startbtn = new Button("Hladaj");
        textArea = new TextArea();
        progressBar = new ProgressBar(0);
        imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        VBox vbo = new VBox();

        casLabel = new Label("");
        HBox hbo = new HBox();
        hbo.setAlignment(Pos.BASELINE_LEFT);

        maxRiadky = getMaxRiadky();

        textArea.setPrefSize(600, 200);
        progressBar.setPrefSize(600, 20);

        hbo.getChildren().addAll(startbtn, casLabel);
        vbo.getChildren().addAll(textField, hbo, textArea, progressBar, imageView);
        root.getChildren().add(vbo);

        startbtn.setOnAction(event -> startVyhladavanie(textField.getText()));
        pocitajCas();

        stage.setTitle("Vyhladavanie retazca");
        stage.setScene(scene);
        stage.show();
    }

    public int getMaxRiadky() {
        int maxRiadky = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/" + nazovSuboru))) {
            while (reader.readLine() != null) maxRiadky++;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return maxRiadky;
    }

    public void pocitajCas() {
        Runnable casRun = new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (true) {
                    try {
                        i++;
                        Thread.sleep(1000);
                        int pom = i;
                        Platform.runLater(() -> casLabel.setText("Aplikácia beží " + pom + " sekúnd"));
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        };
        casThread = new Thread(casRun);
        casThread.start();
    }

    public void startVyhladavanie(String text) {
        if (text.isEmpty()) return;
        Runnable searchRun = new Runnable() {
            @Override
            public void run() {
                try {
                    reader = new BufferedReader(new FileReader("src/main/resources/" + nazovSuboru));
                    int i = 0;
                    String riadok;
                    while ((riadok = reader.readLine()) != null) {
                        try {
                            i++;
                            String vypis = "Kontrolujem riadok číslo " + i + "\n";
                            if (riadok.contains(text)) {
                                for (int j = 0; j < riadok.length() - text.length() + 1 ; j++) {
                                    if (riadok.substring(j, j + text.length()).equals(text)) {
                                        vypis += "Nájdený reťazec: " + text + " na riadku " + i + " a pozícii " + (j+1) + "\n";
                                    }
                                }
                            }
                            int pom = i;
                            String poms = vypis;
                            Platform.runLater(() -> {
                                textArea.appendText(poms);
                                progressBar.setProgress(1.0 * pom / maxRiadky);
                            });
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    reader.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        };
        searchThread = new Thread(searchRun);
        searchThread.start();
    }

    public static void main(String[] args) {
        launch();
    }
}