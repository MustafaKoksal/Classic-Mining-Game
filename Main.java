import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class Main extends Application {
    private Drill drill;
    private Game game;
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start method to initialize game scene then adjust according to given methods.
     *
     * @param primaryStage the primary stage for this application
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("HU Load");

        game = new Game();

        scene = new Scene(game.getRoot(), 900, 800);

        drill = new Drill(game);

        machineMovement();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Handles the movement of the machine based on keyboard input.
     */
    private void machineMovement() {
        // Load images for different directions
        Image machineImageRight = new Image("assets/drill/drill_55.png");
        Image machineImageLeft = new Image("assets/drill/drill_01.png");
        Image machineImageUp = new Image("assets/drill/drill_24.png");
        Image machineImageDown = new Image("assets/drill/drill_42.png");

        scene.setOnKeyPressed(event -> {
            // Check which key is pressed
            if (event.getCode() == KeyCode.LEFT) {
                drill.leftMove(machineImageLeft);
            } else if (event.getCode() == KeyCode.RIGHT) {
                drill.rightMove(machineImageRight);
            } else if (event.getCode() == KeyCode.UP) {
                drill.upMove(machineImageUp);
            } else if (event.getCode() == KeyCode.DOWN) {
                drill.downMove(machineImageDown);
            }
        });
    }
}