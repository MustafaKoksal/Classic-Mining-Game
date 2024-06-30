import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.*;

public class Drill {
    private final int blockWidthHeight = 50;
    private ImageView machineView;
    private Game game;
    private String lastDirection = "LEFT";

    public Drill(Game game) {
        this.game = game;
        setMachineImage();
        checkPossibilities();
        startGravity();
    }

    /**
     * Sets the image of the machine and randomly positions it on the screen.
     */
    private void setMachineImage() {
        // Set image of the machine
        Image machineImage = new Image("assets/drill/drill_01.png");
        machineView = new ImageView(machineImage);
        Random random = new Random();

        // Assign random X value to the machine
        double randomX = blockWidthHeight * random.nextInt(800 / blockWidthHeight) - 20;

        machineView.setX(randomX); machineView.setY(48);

        game.getRoot().getChildren().add(machineView);
    }

    /**
     * Moves the machine to the right, updates fuel consumption and handles collision detection.
     *
     * @param machineImageRight The image representing the machine facing right.
     */
    public void rightMove(Image machineImageRight) {
        game.setFuel(game.getFuel() - 100);
        machineView.setImage(machineImageRight);

        // Machine moves a width of block to the right
        double nextX = machineView.getX() + blockWidthHeight;

        // If there is an obstacle on the right side, machine cannot go to the right
        if (!checkCollisionWithObstacles(nextX, machineView.getY())) {
            // Machine cannot go out of bounds
            if (machineView.getX() + blockWidthHeight * 2 < game.getRoot().getWidth()) {
                machineView.setX(nextX);
            } else {
                machineView.setX(game.getRoot().getWidth() - machineImageRight.getWidth());
            }
        }

        lastDirection = "RIGHT";
    }

    /**
     * Moves the machine to the left, updates fuel consumption and handles collision detection.
     *
     * @param machineImageLeft The image representing the machine facing left.
     */
    public void leftMove(Image machineImageLeft) {
        game.setFuel(game.getFuel() - 100);
        machineView.setImage(machineImageLeft);

        // Machine moves a width of block to the left
        double nextX = machineView.getX() - blockWidthHeight;

        // If there is an obstacle on the left side, machine cannot go to the left
        if (!checkCollisionWithObstacles(nextX, machineView.getY())) {
            if (0 <= machineView.getX() + blockWidthHeight - machineImageLeft.getWidth()) {
                machineView.setX(nextX);
            }
        }

        lastDirection = "LEFT";
    }

    /**
     * Moves the machine upward, updates fuel consumption and handles collision detection.
     *
     * @param machineImageUp The image representing the machine facing upward.
     */
    public void upMove(Image machineImageUp) {
        game.setFuel(game.getFuel() - 100);
        List<ImageView> soilViews = game.getSoilViews();

        // The machine closes the drill before it goes up, handled for each direction
        if (!lastDirection.equals("UP")) {
            Image[] propellerImages = new Image[10];
            for (int i = 0; i < 9; i++) {
                Image propellerImage = new Image("assets/drill/drill_" + (i + 14) + ".png");

                if (lastDirection.equals("RIGHT")) {
                    propellerImage = new Image("assets/drill/drill_right/" + (25 - i) + ".png");
                } else if (lastDirection.equals("DOWN")) {
                    propellerImage = new Image("assets/drill/drill_" + (i + 43) + ".png");
                }

                propellerImages[i] = propellerImage;
            }

            propellerImages[9] = new Image("assets/drill/drill_23.png");

            // Apply images smoothly
            Timeline propellerAnimation = new Timeline();
            for (int i = 0; i < 10; i++) {
                KeyFrame keyFrame = new KeyFrame(Duration.millis(i * 50), new KeyValue(machineView.imageProperty(), propellerImages[i]));
                propellerAnimation.getKeyFrames().add(keyFrame);
            }

            propellerAnimation.setOnFinished(event -> {
                // Machine moves upward for a width of block
                double nextY = machineView.getY() - blockWidthHeight;
                double margin = machineImageUp.getHeight() - blockWidthHeight;
                boolean isTopSoil = false;

                // If machine tries to go upward from underground to overground, fuel more decreases
                if (nextY > 40 && nextY < 60) {
                    game.setFuel(game.getFuel() - 300);
                }

                // If there is soil above the machine, it cannot drill upward
                for (ImageView soilView : soilViews) {
                    if (nextY <= soilView.getY() + margin && soilView.getY() - margin <= nextY &&
                            machineView.getX() <= soilView.getX() + margin && soilView.getX() - margin <= machineView.getX()) {
                        isTopSoil = true;
                    }
                }

                // Check if there is an obstacle or soil above the machine and if it is within the game boundaries
                if (!checkCollisionWithObstacles(machineView.getX(), nextY) && !isTopSoil && nextY > -3) {
                    machineView.setY(nextY);
                }

            });

            propellerAnimation.play();
        } else {
            // Machine moves upward for a width of block
            double nextY = machineView.getY() - blockWidthHeight;
            double margin = machineImageUp.getHeight() - blockWidthHeight;
            boolean isTopSoil = false;

            // If machine tries to go upward from underground to overground, fuel more decreases
            if (nextY > 40 && nextY < 60) {
                game.setFuel(game.getFuel() - 300);
            }

            // If there is soil above the machine, it cannot drill upward.
            for (ImageView soilView : soilViews) {
                if (nextY <= soilView.getY() + margin && soilView.getY() - margin <= nextY &&
                        machineView.getX() <= soilView.getX() + margin && soilView.getX() - margin <= machineView.getX()) {
                    isTopSoil = true;
                }
            }

            // Check if there is an obstacle or soil above the machine and if it is within the game boundaries.
            if (!checkCollisionWithObstacles(machineView.getX(), nextY) && !isTopSoil && nextY > -3) {
                machineView.setY(nextY);
            }

        }

        lastDirection = "UP";
    }

    /**
     * Moves the machine downward, updates fuel consumption and handles collision detection.
     *
     * @param machineImageDown The image representing the machine facing downward.
     */
    public void downMove(Image machineImageDown) {
        game.setFuel(game.getFuel() - 100);
        machineView.setImage(machineImageDown);

        double nextY = machineView.getY() + blockWidthHeight;

        // If there is an obstacle under of the machine, it cannot drill downward
        if (!checkCollisionWithObstacles(machineView.getX(), nextY)) {
            machineView.setY(nextY);
        }

        lastDirection = "DOWN";
    }

    /**
     * Starts gravity effect to simulate downward movement of the machine.
     * If there is soil below the machine, it stops falling when it reaches the top surface of the block.
     */
    private void startGravity() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                // Gravity applies at 50 magnitudes per second
                if (now - lastUpdate >= 1_000_000_000) {
                    double GRAVITY = 50;

                    // Stop falling if machine reaches the top surface of the block
                    if (!checkAnyCollisionWithObjects(machineView.getX(),
                            machineView.getY() + machineView.getImage().getHeight())) {
                        machineView.setY(machineView.getY() + GRAVITY);
                    } else { // Otherwise, the machine continues falling until it reaches the top surface of the block.
                        List<ImageView> soilViews = game.getSoilViews();

                        double machineBottomY = machineView.getY() + machineView.getImage().getHeight();
                        double machineX = machineView.getX();

                        // Check for collision with soils
                        // Soil has been placed under all blocks so there is no need to check other blocks.
                        for (ImageView soilView : soilViews) {
                            double soilTopY = soilView.getY();
                            double soilTopX = soilView.getX();
                            double margin = machineView.getImage().getWidth() - blockWidthHeight;

                            // If it is within the specified range, adjust its position to rest exactly on the block
                            if (machineBottomY + margin >= soilTopY && soilTopX <= machineX + margin &&
                                    machineX - margin <= soilTopX && machineBottomY - margin <= soilTopY) {
                                if (machineBottomY <= soilTopY) {
                                    machineView.setY(machineView.getY() + 5);
                                }
                                break;
                            }
                        }
                    }
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    /**
     * Checks if there is any collision with objects at the given coordinates.
     *
     * @param x The X-coordinate to check.
     * @param y The Y-coordinate to check.
     * @return True if there is a collision, false otherwise.
     */
    private boolean checkAnyCollisionWithObjects(double x, double y) {
        List<ImageView> soilViews = game.getSoilViews();

        // Calculate the margin
        double margin = machineView.getImage().getWidth() - blockWidthHeight;

        // Calculate the boundaries
        double minX = x - margin;
        double minY = y - margin;
        double maxX = x + margin;
        double maxY = y + margin;

        // Check for collisions with soil views
        // There is no need the control other blocks, because soil has been placed under all blocks
        for (ImageView soilView : soilViews) {
            if (minX <= soilView.getX() && maxX >= soilView.getX() &&
                    minY <= soilView.getY() && maxY >= soilView.getY()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks various possibilities such as fuel depletion, collisions with lava,
     * soils, and valuables. This method is called periodically to update game state.
     */
    private void checkPossibilities() {
        // Set up an animation timer to handle periodic updates
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                game.isFuelRunOut();
                checkCollisionWithLava();
                checkCollisionWithSoils();
                checkCollisionsWithValuables();

                // Check if machine arrives at the fuel station
                if (machineView.getX() <= 740 && machineView.getX() >= 720 && machineView.getY() <= 60
                        && machineView.getY() >= 45) {
                    if (game.getFuel() < 10000 - 0.055) {
                        getFuelFromStation();
                    }
                }

                // Check possibilities per 0.01 second
                if (now - lastUpdate >= 10_000_000) {
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    /**
     * Checks collision with lava blocks. If there is a collision, ends the game.
     */
    private void checkCollisionWithLava() {
        List<ImageView> lavaViews = game.getLavaViews();

        // Calculate the margin
        double margin = machineView.getImage().getWidth() - blockWidthHeight;

        // Calculate the boundaries
        double minX = machineView.getX() - margin;
        double minY = machineView.getY() - margin;
        double maxX = machineView.getX() + margin;
        double maxY = machineView.getY() + margin;

        // Check for collision with lava blocks
        for (ImageView lavaView : lavaViews) {
            if (minX <= lavaView.getX() && maxX >= lavaView.getX() &&
                    minY <= lavaView.getY() && maxY >= lavaView.getY()) {
                lavaViews.remove(lavaView);
                gameOverWithExplosion();
                Timeline delayTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(0.5), e -> game.gameOver())
                );
                delayTimeline.play();
                break;
            }
        }
    }

    /**
     * Checks collision with obstacles at the next position.
     *
     * @param nextX The next X-coordinate.
     * @param nextY The next Y-coordinate.
     * @return True if there is a collision, false otherwise.
     */
    private boolean checkCollisionWithObstacles(double nextX, double nextY) {
        List<ImageView> obstacleViews = game.getObstacleViews();

        // Calculate the margin
        double margin = machineView.getImage().getWidth() - blockWidthHeight;

        // Calculate the boundaries
        double minX = nextX - margin;
        double minY = nextY - margin;
        double maxX = nextX + margin;
        double maxY = nextY + margin;

        // Check for collision with obstacle blocks
        for (ImageView obstacleView : obstacleViews) {
            if (minX <= obstacleView.getX() && maxX >= obstacleView.getX() &&
                    minY <= obstacleView.getY() && maxY >= obstacleView.getY()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks collision with soil blocks. If there is a collision, removes the soil block.
     */
    private void checkCollisionWithSoils() {
        List<ImageView> soilViews = game.getSoilViews();

        Pane root = game.getRoot();

        // Calculate the margin
        double margin = machineView.getImage().getWidth() - blockWidthHeight;

        // Calculate the boundaries
        double minX = machineView.getX() - margin;
        double minY = machineView.getY() - margin;
        double maxX = machineView.getX() + margin;
        double maxY = machineView.getY() + margin;

        // Check for collision with soil blocks
        for (ImageView soilView : soilViews) {
            // If it is in boundaries, remove soil block
            if (minX <= soilView.getX() && maxX >= soilView.getX() &&
                    minY <= soilView.getY() && maxY >= soilView.getY()) {
                // Remove the soil block
                root.getChildren().remove(soilView);
                soilViews.remove(soilView);
                break;
            }
        }

        game.setSoilViews(soilViews);
    }

    /**
     * Checks collisions with valuable blocks. If there is a collision, updates game state.
     */
    private void checkCollisionsWithValuables() {
        List<ImageView> valuableViews = game.getValuableViews();

        HashMap<ImageView, String> typeOfValuables = game.getTypeOfValuables();
        HashMap<String, Integer> worthOfValuables = game.getWorthOfValuables();
        HashMap<String, Integer> weightOfValuables = game.getWeightOfValuables();

        Pane root = game.getRoot();

        // Calculate the margin
        double margin = machineView.getImage().getWidth() - blockWidthHeight;

        // Calculate the boundaries
        double minX = machineView.getX() - margin;
        double minY = machineView.getY() - margin;
        double maxX = machineView.getX() + margin;
        double maxY = machineView.getY() + margin;

        // Check for collision with valuable blocks
        for (ImageView valuableView : valuableViews) {
            if (minX <= valuableView.getX() && maxX >= valuableView.getX() &&
                    minY <= valuableView.getY() && maxY >= valuableView.getY()) {
                // Update money and haul value
                game.setMoney(game.getMoney() + worthOfValuables.get(typeOfValuables.get(valuableView)));
                game.setHaul(game.getHaul() + weightOfValuables.get(typeOfValuables.get(valuableView)));

                // Remove the valuable block
                root.getChildren().remove(valuableView);
                valuableViews.remove(valuableView);
                break;
            }
        }
    }

    /**
     * If the machine goes into the lava, it explodes smoothly.
     */
    private void gameOverWithExplosion() {
        // Initialize frame dimensions
        int frameWidth = 50;
        int frameHeight = 50;
        int numOfFrames = 3;
        int frameDurationMillis = 100;

        // Create an ImageView for the sprite sheet
        ImageView explosionView = new ImageView(new Image("assets/extras/sprite/Explosions.png"));
        explosionView.setViewport(new Rectangle2D(0, 0, frameWidth, frameHeight));

        // Set the position of the explosion
        explosionView.setX(machineView.getX());
        explosionView.setY(machineView.getY());

        game.getRoot().getChildren().add(explosionView);

        final int[] currentFrame = {0};

        // Animation timer for changing frames
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= frameDurationMillis * 1_000_000) {
                    // Update the viewport to display the next frame
                    explosionView.setViewport(new Rectangle2D(
                            currentFrame[0] * frameWidth,
                            0,
                            frameWidth,
                            frameHeight
                    ));

                    explosionView.setFitHeight(85); explosionView.setFitWidth(85);

                    currentFrame[0]++;

                    // If we have reached the last frame, stop the animation and end the game
                    if (currentFrame[0] >= numOfFrames) {
                        stop();
                    }

                    lastUpdate = now;
                }
            }
        };

        timer.start();
    }

    /**
     * When the machine arrives at the fuel station, it receives fuel from the station.
     */
    private void getFuelFromStation() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 100 && game.getFuel() < 10000 - 0.055) {
                    lastUpdate = now;

                    // The fuel of the machine cannot exceed 10000
                    if (game.getFuel() >= 9999) {
                        stop();
                    }

                    // Stop refueling if the machine leaves the station
                    if (!(machineView.getX() <= 740 && machineView.getX() >= 720 && machineView.getY() <= 60
                            && machineView.getY() >= 45)) {
                        stop();
                    }

                    game.setFuel(game.getFuel() + 0.055);
                } else {
                    stop();
                }
            }
        };

        timer.start();
    }
}