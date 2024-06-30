import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Blocks {

    private List<ImageView> lavaViews; private List<ImageView> obstacleViews; private List<ImageView> valuableViews;
    private HashMap<String, Integer> worthOfValuables;
    private HashMap<String, Integer> weightOfValuables;
    private HashMap<ImageView, String> typeOfValuables;
    private int gameWidth = 900; private int gameHeight = 800;

    public Blocks() {
        lavaViews = new ArrayList<>();
        setLava();
        obstacleViews = new ArrayList<>();
        setObstacles();
        valuableViews = new ArrayList<>();
        setValuables();
    }

    /**
     * Sets up the lavas and their positions randomly within the underground.
     */
    private void setLava() {
        Image[] lavaImages = new Image[3];

        Random random = new Random();

        // Get all type of lavas from assets file
        for (int i = 0; i < 3; i++) {
            lavaImages[i] = new Image("assets/underground/lava_0" + (i + 1) + ".png");
        }

        // Generate random lava positions
        for (int i = 0; i < random.nextInt(10) + 6; i++) {
            ImageView lavaView = new ImageView(lavaImages[random.nextInt(3)]);

            double randomX =  random.nextInt((int)(gameWidth / lavaImages[0].getWidth())) * lavaImages[0].getWidth();
            double randomY =  random.nextInt((int)(gameHeight / lavaImages[0].getHeight())) * lavaImages[0].getHeight();

            // Ensure lava positions are within game bounds
            if (randomX < lavaImages[0].getWidth()) {
                randomX += lavaImages[0].getWidth();
            } else if (randomX >= gameWidth - lavaImages[0].getWidth()) {
                randomX -= lavaImages[0].getWidth();
            }

            // Ensure lava blocks are not in sky
            if (randomY < lavaImages[0].getHeight() * 3) {
                randomY += lavaImages[0].getHeight() * 3;
            } else if (randomY >= gameHeight - lavaImages[0].getHeight()) {
                randomY -= lavaImages[0].getHeight();
            }

            lavaView.setX(randomX);
            lavaView.setY(randomY);

            lavaViews.add(lavaView);
        }
    }

    /**
     * Sets up the obstacles and their positions randomly within the edges of underground.
     */
    private void setObstacles() {
        Image[] obstacleImages = new Image[3];

        Random random = new Random();

        // Get all type of obstacles from assets file
        for (int i = 0; i < 3; i++) {
            obstacleImages[i] = new Image("assets/underground/obstacle_0" + (i + 1) + ".png");
        }

        // Generate random obstacle positions within the edges of underground
        for (int row = 3; row < gameHeight / obstacleImages[0].getHeight(); row++) {
            ImageView obstacleViewFirstLine = new ImageView(obstacleImages[random.nextInt(3)]);
            ImageView obstacleViewSecondLine = new ImageView(obstacleImages[random.nextInt(3)]);

            // Set for left edge
            obstacleViewFirstLine.setX(0);
            obstacleViewFirstLine.setY(row * obstacleImages[0].getHeight());
            obstacleViews.add(obstacleViewFirstLine);

            // Set for right edge
            obstacleViewSecondLine.setX(gameWidth - obstacleImages[0].getWidth());
            obstacleViewSecondLine.setY(row * obstacleImages[0].getHeight());
            obstacleViews.add(obstacleViewSecondLine);

            // Set for bottom edge
            if (row == gameHeight / obstacleImages[0].getHeight() - 1) {
                for (int col = 1; col < gameWidth / obstacleImages[0].getWidth(); col++) {
                    ImageView obstacleViewThirdLine = new ImageView(obstacleImages[random.nextInt(3)]);

                    if (!(col == gameWidth / obstacleImages[0].getWidth() - 1)) {
                        obstacleViewThirdLine.setX(col * obstacleImages[0].getWidth());
                        obstacleViewThirdLine.setY(row * obstacleImages[0].getHeight());
                        obstacleViews.add(obstacleViewThirdLine);
                    }
                }
            }
        }
    }

    /**
     * Sets up the valuable blocks and their positions randomly within the underground.
     */
    private void setValuables() {
        Image[] valuableImages = new Image[5];

        typeOfValuables = new HashMap<>();
        worthOfValuables = new HashMap<>();
        weightOfValuables = new HashMap<>();

        // Set worth and weight values for each valuable block
        setWorthsAndWeights();

        // Get images of valuable blocks
        valuableImages[0] = new Image("assets/underground/valuable_diamond.png");
        valuableImages[1] = new Image("assets/underground/valuable_amazonite.png");
        valuableImages[2] = new Image("assets/underground/valuable_emerald.png");
        valuableImages[3] = new Image("assets/underground/valuable_goldium.png");
        valuableImages[4] = new Image("assets/underground/valuable_silverium.png");

        Random random = new Random();

        // Generate random positions within the underground
        for (int i = 0; i < random.nextInt(9) + 10; i++) {
            boolean validLocation = false;
            while (!validLocation) {
                double randomX =  random.nextInt((int)(gameWidth / valuableImages[0].getWidth())) * valuableImages[0].getWidth();
                double randomY =  random.nextInt((int)(gameHeight / valuableImages[0].getHeight())) * valuableImages[0].getHeight();
                int randomVariableIndex = random.nextInt(5);

                boolean overlap = false;

                // Check for overlap with lava
                for (ImageView lavaView : lavaViews) {
                    if (checkOverlap(randomX, randomY, lavaView.getX(), lavaView.getY())) {
                        overlap = true;
                        break;
                    }
                }

                // Check for overlap with obstacles
                if (!overlap) {
                    for (ImageView obstacleView : obstacleViews) {
                        if (checkOverlap(randomX, randomY, obstacleView.getX(), obstacleView.getY())) {
                            overlap = true;
                            break;
                        }
                    }
                }

                // If there is no overlap, add the valuable block and mark location as valid
                if (!overlap) {
                    validLocation = true;

                    ImageView valuable = new ImageView(valuableImages[randomVariableIndex]);

                    // Ensure valuable block positions are within game bounds
                    if (randomX < valuableImages[0].getWidth()) {
                        randomX += valuableImages[0].getWidth();
                    } else if (randomX >= gameWidth - valuableImages[0].getWidth()) {
                        randomX -= valuableImages[0].getWidth();
                    }

                    // Ensure valuable blocks are not in sky
                    if (randomY < valuableImages[0].getHeight() * 3) {
                        randomY += valuableImages[0].getHeight() * 3;
                    } else if (randomY >= gameHeight - valuableImages[0].getHeight()) {
                        randomY -= valuableImages[0].getHeight();
                    }

                    valuable.setX(randomX);
                    valuable.setY(randomY);
                    valuableViews.add(valuable);

                    // Set name of type for each valuable block
                    switch (randomVariableIndex) {
                        case 0:
                            typeOfValuables.put(valuable, "diamond");
                            break;
                        case 1:
                            typeOfValuables.put(valuable, "amazonite");
                            break;
                        case 2:
                            typeOfValuables.put(valuable, "emerald");
                            break;
                        case 3:
                            typeOfValuables.put(valuable, "goldium");
                            break;
                        case 4:
                            typeOfValuables.put(valuable, "silverium");
                            break;
                    }
                }
            }
        }
    }

    /**
     * Helper method to check overlap between two objects with a margin.
     *
     * @param x1 The X-coordinate of first object
     * @param y1 The Y-coordinate of first object
     * @param x2 The X-coordinate of second object
     * @param y2 The Y-coordinate of second object
     * @return True if there is overlap between two given objects, otherwise false
     */
    private boolean checkOverlap(double x1, double y1, double x2, double y2) {
        double margin = 20;
        return Math.abs(x1 - x2) < margin && Math.abs(y1 - y2) < margin;
    }

    /**
     * Adds worth and weights of valuables to ArrayList.
     */
    private void setWorthsAndWeights() {
        worthOfValuables.put("amazonite", 500000);
        worthOfValuables.put("diamond", 100000);
        worthOfValuables.put("emerald", 5000);
        worthOfValuables.put("goldium", 250);
        worthOfValuables.put("silverium", 750);

        weightOfValuables.put("amazonite", 120);
        weightOfValuables.put("diamond", 100);
        weightOfValuables.put("emerald", 60);
        weightOfValuables.put("goldium", 20);
        weightOfValuables.put("silverium", 30);
    }

    public List<ImageView> getLavaViews() { return lavaViews; }

    public List<ImageView> getObstacleViews() {
        return obstacleViews;
    }

    public List<ImageView> getValuableViews() {
        return valuableViews;
    }

    public HashMap<String, Integer> getWorthOfValuables() { return worthOfValuables; }

    public HashMap<String, Integer> getWeightOfValuables() { return weightOfValuables; }

    public HashMap<ImageView, String> getTypeOfValuables() { return typeOfValuables; }
}