package com.sunnydevs.alengame;

import com.sunnydevs.alengame.db.Person;

import javafx.scene.input.MouseEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public abstract class Quiz {
    static int NUM_OF_QUESTIONS = 10;

    static void setOptions(GridPane btnsContainer, ArrayList<Map<String, Object>> questions, int counter) {
        System.out.println(counter);
        List<Node> btns = btnsContainer.getChildren();
        ArrayList<String> opts = (ArrayList<String>) questions.get(counter).get("options");
        System.out.println(opts);
        for (int i = 0; i < btns.size(); i++) {
            Node btn = btns.get(i);
            if (btn instanceof Button) {
                ((Button) btn).setText(opts.get(i));
            }
        }
    }

    protected void showResults(Stage stage, int corrects){
        try {
            stage.close();
            FXMLLoader resultsView = new FXMLLoader(getClass().getResource("Result.fxml"));
            Scene resultsScene = new Scene(resultsView.load());
            ResultsController controller = resultsView.getController();
            controller.setCorrectNum(corrects);

            Stage resultsStage = new Stage();
            resultsStage.setScene(resultsScene);
            resultsStage.setMaximized(true);
            resultsStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void setButtonStyles(@NotNull Button btn) {
        btn.setStyle("-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-border-color: #808080");
    }

    protected void handleMouseEnter(@NotNull MouseEvent event) {
        // Change styles on mouse enter
        ((Button) event.getSource()).setStyle("-fx-background-color: rgb(99,111,255); -fx-text-fill: #fcfafa;");
    }

    protected void handleMouseExit(@NotNull MouseEvent event) {
        // Change styles on mouse exit
        setButtonStyles((Button) event.getSource());
    }

    protected void setUpAnchorPaneConstraints(AnchorPane innerAnchorPane, AnchorPane outerAnchorPane) {
        AnchorPane.setTopAnchor(innerAnchorPane, (outerAnchorPane.getHeight() - innerAnchorPane.getPrefHeight()) / 2);
        AnchorPane.setLeftAnchor(innerAnchorPane, (outerAnchorPane.getWidth() - innerAnchorPane.getPrefWidth()) / 2);

        // Bind layout constraints to outer AnchorPane's size changes
        outerAnchorPane.heightProperty().addListener((observable, oldValue, newValue) ->
                AnchorPane.setTopAnchor(innerAnchorPane, (newValue.doubleValue() - innerAnchorPane.getPrefHeight()) / 2));

        outerAnchorPane.widthProperty().addListener((observable, oldValue, newValue) ->
                AnchorPane.setLeftAnchor(innerAnchorPane, (newValue.doubleValue() - innerAnchorPane.getPrefWidth()) / 2));
    }

    ArrayList<Map<String, Object>> generateQuestions() {
        Random rd = new Random();
        ArrayList<Map<String, Object>> questions = new ArrayList<>();

        try {
            ArrayList<Person> dataSet = Person.generateDataSetForQuiz(1);
            Collections.shuffle(dataSet);
            ArrayList<String> names = Person.allNames();

            for (Person p : dataSet) {
                Map<String, Object> question = new HashMap<>();

                // generate 4 options for question
                ArrayList<String> opts = new ArrayList<>();
                opts.add(p.name());
                while (opts.size() != 4) {
                    String name = names.get(rd.nextInt(dataSet.size()));
                    if (!opts.contains(name))
                        opts.add(name);
                }
                Collections.shuffle(opts);
                question.put("options", opts);
                question.put("correct", p.name());
                questions.add(question);
            }
            return questions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
