import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Background {
    private Rectangle skyView;
    private List<ImageView> undergroundViews;
    private Rectangle brownOverlayView;

    public Background() {
        skyView = new Rectangle();
        brownOverlayView = new Rectangle();
        undergroundViews = new ArrayList<>();
        setBackgroundImage();
    }

    /**
     * Sets the background image and elements for the game.
     */
    private void setBackgroundImage() {
        double gameWidth = 900;
        double gameHeight = 800;

        Image grassImage = new Image("assets/underground/top_02.png");

        Image[] soilImages = new Image[5];

        Random random = new Random();

        // Create a sky overlay and set its properties
        Rectangle sky = new Rectangle(0, 0, 900, 800);
        sky.setFill(Color.rgb(45, 158, 225));
        skyView = sky; // Store the sky rectangle

        // Create a brown overlay behind soils and set its properties
        Rectangle brownOverlayBehindSoils = new Rectangle(0, 103, gameWidth, 697);
        brownOverlayBehindSoils.setFill(Color.rgb(124, 71, 33));
        brownOverlayView = brownOverlayBehindSoils; // Store the brown overlay

        // Load soil images from assets file
        for (int i = 0; i < 5; i++) {
            soilImages[i] = new Image("assets/underground/soil_0" + (i + 1) + ".png");
        }

        // Generate soils inside the underground
        for (int row = (int) ((gameHeight - soilImages[0].getHeight()) / soilImages[0].getHeight()); 0 < row - 1; row--) {
            for (int col = 0; col < (int) (gameWidth / soilImages[0].getWidth()); col++) {
                ImageView soilView = new ImageView(soilImages[random.nextInt(5)]);

                // Skip the second row
                if (row == 2) {
                    continue;
                }

                soilView.setX(col * soilImages[0].getWidth());
                soilView.setY(row * soilImages[0].getHeight());

                undergroundViews.add(soilView);
            }
        }

        // Generate grass blocks on top of the soils
        for (int col = 0; col < (int) (gameWidth / grassImage.getWidth()); col++) {
            ImageView grassView = new ImageView(grassImage);

            grassView.setX(col * grassImage.getWidth());
            grassView.setY(100);

            undergroundViews.add(grassView);
        }
    }

    public List<ImageView> getUndergroundViews() {
        return undergroundViews;
    }

    public Rectangle getSkyView() {
        return skyView;
    }

    public Rectangle getBrownOverlayView() {
        return brownOverlayView;
    }
}