package ar.edu.unlu.poo.VISTA;


import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class ventanaTablero extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600,  true);
        scene.setFill(Color.DARKGREEN);

        PerspectiveCamera camara = new PerspectiveCamera(true);
        camara.getTransforms().add(new Translate(0, -200,-500));
        scene.setCamera(camara);

        Box tablero = new Box(300,10,300);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(Color.BROWN);
        tablero.setMaterial(material);
        tablero.getTransforms().add(new Rotate(-10, Rotate.X_AXIS));

        root.getChildren().add(tablero);

        stage.setScene(scene);
        stage.setTitle("Scrabble");
        stage.show();


    }
}
