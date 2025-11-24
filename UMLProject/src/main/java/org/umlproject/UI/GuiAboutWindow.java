package org.umlproject.UI;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Displays the About dialog for the UML Editor.
 * Mirrors GuiHelp's style and lists contributors and project details.
 */
public class GuiAboutWindow {

    public static void showAbout() {
        
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("UML Editor");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        Label subtitle = new Label("CSCD 350 – Software Development | Version 1.0");
        subtitle.setFont(Font.font(13));

        Separator line1 = new Separator();

        Label teamInfo = new Label("""
                Developed by Team Microsoft-2
                Eastern Washington University

                Team Members:
                • Adam
                • Sean
                • Keetyn
                • Ty
                • Aidan
                • Michelle

                Instructor: Dr. Cain
                """);
        teamInfo.setFont(Font.font(13));

        Separator line2 = new Separator();

        Label description = new Label("""
                Project Overview:
                The UML Editor provides an interactive JavaFX environment for designing
                and managing UML class diagrams in real time.

                Key Features:
                • Create, edit, and delete UML classes and relationships.
                • Click-and-drag repositioning of class boxes.
                • Zoom and pan the workspace.
                • Save and load UML projects as JSON files.
                • Selection highlighting and keyboard shortcuts.
                • F1 opens Help | F2 opens About.

                Technical Design:
                Built using JavaFX and an MVC-style architecture.
                Model layer: UMLDocument, UMLClass, UMLRelationship
                View/Controller layer: GuiController, GuiClass, GuiRelationship,
                GuiSelect, GuiCamera, and GuiResizeManager.
                """);
        description.setFont(Font.font(12));
        description.setWrapText(true);

        content.getChildren().addAll(title, subtitle, line1, teamInfo, line2, description);
        Alert aboutBox = FXDialogueFactory.createAlertWindow(Alert.AlertType.INFORMATION, "About UML Editor", null, null, content);
        aboutBox.show();
        
    }
}
