import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class MMTTC extends Application
{
    final double CELL_WIDTH = 250;
    final double CELL_HEIGHT = 250;

    final double MARGIN_X = 0.16 * CELL_WIDTH;
    final double MARGIN_O = 0.12 * CELL_HEIGHT;

    final double CANVAS_WIDTH = 3 * CELL_WIDTH;
    final double CANVAS_HEIGHT = 3 * CELL_HEIGHT;

    Stage primaryStage;
    Scene scene;
    Canvas canvas;

    Game game = new Game();

    public static void main(String[] args)
    {
        launch(args);
    }

    void drawGrid(GraphicsContext gc)
    {
        gc.setGlobalAlpha(1);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setLineWidth(5);

        gc.setStroke(Color.GREY);

        gc.strokeLine(CELL_WIDTH, 0, CELL_WIDTH, canvas.getHeight());
        gc.strokeLine(2 * CELL_WIDTH, 0, 2 * CELL_WIDTH, canvas.getHeight());

        gc.strokeLine(0, CELL_HEIGHT, canvas.getWidth(), CELL_HEIGHT);
        gc.strokeLine(0, 2 * CELL_HEIGHT, canvas.getWidth(), 2 * CELL_HEIGHT);
    }

    void drawSign(GraphicsContext gc, Game.Sign sign, int x, int y)
    {
        if (sign == Game.Sign.O)
        {
            double drawX = x * CELL_WIDTH + MARGIN_O;
            double drawY = y * CELL_HEIGHT + MARGIN_O;

            double width = CELL_WIDTH - 2 * MARGIN_O;
            double height = CELL_HEIGHT - 2 * MARGIN_O;

            gc.strokeOval(drawX, drawY, width, height);
        } else if (sign == Game.Sign.X)
        {
            double drawX1 = x * CELL_WIDTH + MARGIN_X;
            double drawX2 = (x + 1) * CELL_WIDTH - MARGIN_X;

            double drawY1 = y * CELL_HEIGHT + MARGIN_X;
            double drawY2 = (y + 1) * CELL_HEIGHT - MARGIN_X;

            gc.strokeLine(drawX1, drawY1, drawX2, drawY2);
            gc.strokeLine(drawX1, drawY2, drawX2, drawY1);
        }
    }

    void drawAll(GraphicsContext gc)
    {
        drawGrid(gc);
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                Game.Sign sign = game.get(i, j);
                if (sign != null)
                {
                    drawSign(canvas.getGraphicsContext2D(), sign, i, j);
                }
            }
        }
    }

    void drawEndMessage(GraphicsContext gc, Game.Sign sign)
    {
        String message;

        if (sign == Game.Sign.X)
        {
            message = "Player wins! Wait... That shouldn't happen";
        } else if (sign == Game.Sign.O)
        {
            message = "AI wins!";
        } else
        {
            message = "Draw!";
        }

        double x = 0.2 * CANVAS_WIDTH;
        double y = 0.4 * CANVAS_HEIGHT;

        double width = 0.6 * CANVAS_WIDTH;
        double height = 0.2 * CANVAS_HEIGHT;

        gc.setFill(Color.GRAY);
        gc.setGlobalAlpha(0.8);
        gc.fillRect(x, y, width, height);

        gc.setFill(Color.WHITE);
        gc.setLineWidth(1);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(new Font("Verdana", height * 0.5));
        gc.fillText(message, 0.5 * CANVAS_WIDTH, 0.5 * CANVAS_HEIGHT);


    }

    @Override
    public void start(Stage stage)
    {
        primaryStage = stage;
        primaryStage.setTitle("MMTTC");
        primaryStage.setResizable(false);

        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
        {
            int cellX = (int) (event.getX() / CELL_WIDTH);
            int cellY = (int) (event.getY() / CELL_HEIGHT);

            // System.out.println("cellX: " + cellX + ", cellY: " + cellY);

            if (event.getButton() == MouseButton.PRIMARY)
            {
                if (game.isGameOver())
                {
                    game = new Game();
                    drawGrid(canvas.getGraphicsContext2D());
                } else if (game.set(cellX, cellY, Game.Sign.X))
                {
                    drawSign(canvas.getGraphicsContext2D(), Game.Sign.X, cellX, cellY);
                    game.minMax();
                    drawAll(canvas.getGraphicsContext2D());
                    if (game.isGameOver())
                    {
                        drawEndMessage(canvas.getGraphicsContext2D(), game.winner);
                    }
                }
            }
        });

        drawGrid(canvas.getGraphicsContext2D());

        StackPane root = new StackPane();
        root.setStyle("-fx-background: #FFFFFF;");
        root.getChildren().addAll(canvas);

        scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
