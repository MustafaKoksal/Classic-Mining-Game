import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.HashMap;
import java.util.List;

public class Game extends Pane {
    private Pane root;
    private double fuel = 10000; private int haul = 0; private int money = 0;
    private Text fuelText; private Text haulText; private Text moneyText;
    private List<ImageView> soilViews; private List<ImageView> lavaViews; private List<ImageView> obstacleViews;
    private List<ImageView> valuableViews;
    private HashMap<ImageView, String> typeOfValuables; private HashMap<String, Integer> worthOfValuables;
    private HashMap<String, Integer> weightOfValuables;

    public Game() {
        initialize();
        startFuelConsumption();
        displayHaul();
        displayMoney();
        setFuelStation();
    }

    /**
     * Initializes all the game components.
     */
    private void initialize() {
        root = new Pane();

        Background background = new Background();
        Blocks blocks = new Blocks();

        // Get views from background and blocks
        soilViews = background.getUndergroundViews(); lavaViews = blocks.getLavaViews();
        obstacleViews = blocks.getObstacleViews(); valuableViews = blocks.getValuableViews();

        // Get attributes of valuables
        typeOfValuables = blocks.getTypeOfValuables();
        worthOfValuables = blocks.getWorthOfValuables();
        weightOfValuables = blocks.getWeightOfValuables();

        // Add all components to the root
        root.getChildren().addAll(background.getSkyView(), background.getBrownOverlayView());
        root.getChildren().addAll(soilViews); root.getChildren().addAll(valuableViews);
        root.getChildren().addAll(lavaViews); root.getChildren().addAll(obstacleViews);
    }

    /**
     * Starts fuel consumption according to time.
     */
    private void startFuelConsumption() {
        // Create a text showing current fuel amount
        fuelText = new Text(3,30,"Fuel:" + fuel);
        fuelText.setFont(Font.font(30));
        fuelText.setFill(Color.WHITE);
        getRoot().getChildren().add(fuelText);

        // Set up a timer to handle fuel consumption
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                // Decrease fuel amount per 0.1 seconds
                if (now - lastUpdate >= 100_000_000) {
                    fuel -= 0.005516;
                    updateFuelDisplay();
                    updateMoneyDisplay();
                    updateHaulDisplay();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    /**
     * Displays texts and background in case fuel runs out.
     */
    public void isFuelRunOut() {
        // Create a green overlay for background
        Rectangle greenOverlay = new Rectangle(0, 0, root.getWidth(), root.getHeight());
        greenOverlay.setFill(Color.rgb(26, 94, 13));

        // Create a "GAME OVER" text with its properties
        Text gameOver = new Text("GAME OVER");
        gameOver.setX((root.getWidth() - gameOver.getBoundsInLocal().getWidth() * 3.5) / 2);
        gameOver.setY((root.getHeight() / 2.3));
        gameOver.setFont(Font.font(50));
        gameOver.setFill(Color.WHITE);

        // Create a "COLLECTED MONEY" text to display current amount of money
        Text collectedMoney = new Text("Collected Money: " + money);
        collectedMoney.setX((root.getWidth() - collectedMoney.getBoundsInLocal().getWidth() * 3.5) / 2);
        collectedMoney.setY(gameOver.getY() + 65);
        collectedMoney.setFont(Font.font(50));
        collectedMoney.setFill(Color.WHITE);

        // If fuel has run out, display created background and texts
        if (fuel <= 0) {
            root.getChildren().addAll(greenOverlay, gameOver, collectedMoney);
        }
    }

    /**
     * Displays text and background in case collision detected with lava blocks.
     */
    public void gameOver() {
        // Create a red overlay for background
        Rectangle redOverlay = new Rectangle(0, 0, root.getWidth(), root.getHeight());
        redOverlay.setFill(Color.rgb(112, 12, 12));

        // Create a "GAME OVER" text with its properties
        Text gameOver = new Text("GAME OVER");
        gameOver.setX((root.getWidth() - gameOver.getBoundsInLocal().getWidth() * 3.5) / 2);
        gameOver.setY((root.getHeight() / 2));
        gameOver.setFont(Font.font(50));
        gameOver.setFill(Color.WHITE);

        getRoot().getChildren().addAll(redOverlay, gameOver);
    }

    /**
     * Displays the money amount with its properties on the screen.
     */
    private void displayMoney() {
        moneyText = new Text(3, 90, "money: " + money);
        moneyText.setFont(Font.font(30));
        moneyText.setFill(Color.WHITE);
        getRoot().getChildren().add(moneyText);
    }

    /**
     * Displays the haul amount with its properties on the screen.
     */
    private void displayHaul() {
        haulText = new Text(3, 60, "haul: " + haul);
        haulText.setFont(Font.font(30));
        haulText.setFill(Color.WHITE);
        getRoot().getChildren().add(haulText);

    }

    /**
     * Creates a fuel station.
     */
    private void setFuelStation() {
        ImageView fuelStationView = new ImageView(new Image("assets/extras/sprite/Overground.png"));

        // Take image of the fuel station from sprite sheet
        fuelStationView.setViewport(new Rectangle2D(0, 0,150, 150));

        fuelStationView.setX(755);
        fuelStationView.setY(-45);

        root.getChildren().add(fuelStationView);
    }

    private void updateFuelDisplay() {
        fuelText.setText(String.format("fuel:%.3f", fuel));
    }

    private void updateMoneyDisplay() {
        moneyText.setText(String.format("money:%d", money));
    }

    private void updateHaulDisplay() {
        haulText.setText(String.format("haul:%d", haul));
    }

    public Pane getRoot() { return root; }

    public double getFuel() { return fuel; }

    public void setFuel(double fuel) { this.fuel = fuel; }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getHaul() {
        return haul;
    }

    public void setHaul(int haul) {
        this.haul = haul;
    }

    public List<ImageView> getSoilViews() { return soilViews; }

    public void setSoilViews(List<ImageView> soilViews) {
        this.soilViews = soilViews;
    }

    public List<ImageView> getLavaViews() {
        return lavaViews;
    }

    public List<ImageView> getObstacleViews() {
        return obstacleViews;
    }

    public List<ImageView> getValuableViews() {
        return valuableViews;
    }

    public HashMap<ImageView, String> getTypeOfValuables() {
        return typeOfValuables;
    }

    public HashMap<String, Integer> getWorthOfValuables() {
        return worthOfValuables;
    }

    public HashMap<String, Integer> getWeightOfValuables() {
        return weightOfValuables;
    }
}