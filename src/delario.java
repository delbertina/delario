//Buttons on screen to move a ball
//check it doesn't go out of bounds
//define a pane class for displaying the ball

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Light;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.w3c.dom.css.RGBColor;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class delario extends Application{
    public final int screenWidth = 800;
    public final int screenHeight = 550;

    private BallPane ballRedPane = new BallPane();

    @Override
    public void start(Stage primaryStage){
        int realScreenWidth = 800;
        int realScreenHeight = 600;

        //spawn a bunch of food
        ballRedPane.spawnFood(25);

        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane,realScreenWidth,realScreenHeight);

        //make leaderboard
        LeaderPane leaderboard = new LeaderPane();
        borderPane.setRight(leaderboard);
        leaderboard.setAlignment(Pos.TOP_LEFT);

        //set buttons to do things
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT){
                //print("Left");
                ballRedPane.moveLeft();
            }
            else if (e.getCode() == KeyCode.W || e.getCode() == KeyCode.UP){
                //print("Up");
                ballRedPane.moveUp();
            }
            else if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT){
                //print("Right");
                ballRedPane.moveRight();
            }
            else if (e.getCode() == KeyCode.S || e.getCode() == KeyCode.DOWN){
                //print("Down");
                ballRedPane.moveDown();
                ballRedPane.requestFocus();
            }
        });

        scene.setOnMouseClicked(e -> {
            ballRedPane.removeFood(new Point2D(e.getSceneX(),e.getSceneY()));
            ballRedPane.requestFocus();
        });

        Timeline twoSeconds = new Timeline(new KeyFrame(Duration.seconds(2), e -> {

            ballRedPane.spawnFoodSmart();
            leaderboard.updateBoard();

        }));
        twoSeconds.setCycleCount(Timeline.INDEFINITE);
        twoSeconds.play();

        //make options window
        HBox options = new HBox(5);

        CheckBox showSize = new CheckBox();
        showSize.setSelected(true);
        showSize.setOnAction(e -> {
            ballRedPane.toggleSize();
        });

        TextField username = new TextField();
        username.setOnKeyPressed(e -> {
            if(e.getCode() == KeyCode.ENTER){
                leaderboard.changeName(username.getText());
            }
        });

        //spinners for rgb
        Spinner redValue = new Spinner(0,255,255);
        Spinner greenValue = new Spinner(0,255,0);
        Spinner blueValue = new Spinner(0,255,0);

        //configure spinnere
        redValue.setEditable(true);
        greenValue.setEditable(true);
        blueValue.setEditable(true);
        redValue.setMaxWidth(60);
        greenValue.setMaxWidth(60);
        blueValue.setMaxWidth(60);

        //labels
        Text checkLabel = new Text("Controls: WASD    Display Size");
        Text usernameLabel = new Text("Username");
        Text redLabel = new Text("Red");
        Text greenLabel = new Text("Green");
        Text blueLabel = new Text("Blue");

        //make the spinners do stuff
        Button changeColor = new Button("Change Color");
        changeColor.setOnAction(e -> {
            ballRedPane.changeColor((Integer)redValue.getValue(),(Integer)greenValue.getValue(),(Integer)blueValue.getValue());
        });

        options.getChildren().addAll(checkLabel,showSize,usernameLabel,username,redLabel,redValue,greenLabel,greenValue,blueLabel,blueValue,changeColor);

        borderPane.setBottom(options);
        borderPane.setCenter(ballRedPane);
        //borderPane.setBottom(btPane);
        //BorderPane.setAlignment(options, Pos.TOP_RIGHT);
        options.setAlignment(Pos.TOP_RIGHT);
        options.setPadding(new Insets(10));

        //btPane.getChildren().addAll(leftBt,upBt,downBt,rightBt);


        primaryStage.setTitle("Test Agario");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    class LeaderPane extends GridPane {
        //top 10 spots
        private ArrayList<Text> numbs;

        //top 10 names
        private ArrayList<Text> numbNames;

        //top 10 scores
        private ArrayList<Text> numbScores;

        private int playerPlace;

        public LeaderPane(){
            //initialize arrays
            numbs = new ArrayList<>(10);
            numbNames = new ArrayList<>(10);
            numbScores = new ArrayList<>(10);
            playerPlace = 9;

            for(int i=0;i<10;i++){
                numbs.add(i,new Text("#" + (i+1)));
            }
            numbNames.add(0,new Text("Snik"));
            numbNames.add(1,new Text("Taco"));
            numbNames.add(2,new Text("Coal"));
            numbNames.add(3,new Text("Dog"));
            numbNames.add(4,new Text("Frankenstein"));
            numbNames.add(5,new Text("Creek"));
            numbNames.add(6,new Text("Vampire"));
            numbNames.add(7,new Text("Oatmeal"));
            numbNames.add(8,new Text("Cody"));
            numbNames.add(9,new Text("Player"));

            numbScores.add(0,new Text("" +200000.00));
            numbScores.add(1,new Text("" +100000.00));
            numbScores.add(2,new Text("" +75000.00));
            numbScores.add(3,new Text("" +55000.00));
            numbScores.add(4,new Text("" +40000.00));
            numbScores.add(5,new Text("" +24000.00));
            numbScores.add(6,new Text("" +15000.00));
            numbScores.add(7,new Text("" +5000.00));
            numbScores.add(8,new Text("" +3000.00));
            numbScores.add(9,new Text("" +100.00));

            //put em where they go
            for(int i=0;i<10;i++){
                add(numbs.get(i),0,i);
                add(numbNames.get(i),1,i);
                add(numbScores.get(i),2,i);
            }
        }

        public void updateBoard(){
            //set old player place to current score
            numbScores.get(playerPlace).setText(String.format("%.2f",ballRedPane.getScore()));

            //if player score is greater than above ... but also not the top cuz crash
            if(playerPlace != 0) {
                if ((ballRedPane.getScore()) > Double.parseDouble((numbScores.get(playerPlace - 1)).getText())) {
                    //switch values
                    Text tempname = new Text(numbNames.get(playerPlace).getText());
                    Text tempscore = new Text(numbScores.get(playerPlace).getText());

                    //print("Switching " + tempname.getText());
                    //print("Other " + numbNames.get(playerPlace-1).getText());
                    //print("Player " + numbNames.get(playerPlace).getText());

                    numbNames.get(playerPlace).setText(numbNames.get(playerPlace - 1).getText());
                    numbScores.get(playerPlace).setText(numbScores.get(playerPlace - 1).getText());
                    numbNames.get(playerPlace - 1).setText(tempname.getText());
                    numbScores.get(playerPlace - 1).setText(tempscore.getText());

                    playerPlace--;
                    //update board again
                    updateBoard();
                }
            }
        }

        public void changeName(String newName){
            if(newName != null){
                numbNames.get(playerPlace).setText(newName);
            }
        }
    }

    class BallPane extends Pane {
        //make ball
        private Circle ballRed = new Circle(screenWidth/2, screenHeight/2, 15, Paint.valueOf("Red"));
        private Text ballArea = new Text( screenWidth/2, screenHeight/2, "Area: " + String.format("%.2f",(ballRed.getRadius() * ballRed.getRadius())* Math.PI));

        private ArrayList<Circle> food = new ArrayList<>();

        private boolean sizeShown;
        private String playerName;

        public BallPane(){
            getChildren().addAll(ballRed,ballArea);
            ballArea.setFill(Color.WHITE);
            ballArea.setStroke(Color.BLACK);

            sizeShown = true;
            playerName = "Player1";
        }

        public void updateMove{}{
            //update movement independant of keypresses

            //up and down

            //if only pressing up

                //and it wont go out of bounds

            //else if only pressing down

                //and it wont go out of bounds

            //left and right

            //if only pressing left

                //and it wont go out of bounds

            //else if only pressing right

                //and it wont go out of bounds

        }

        public void moveLeft(){
            //dont move out of bounds
            if(((ballRed.getCenterX()-ballRed.getRadius())-10)<0){
                ballRed.setCenterX(ballRed.getRadius());
            }
            else{ballRed.setCenterX(ballRed.getCenterX()-10);}
            checkFeed();
            updateArea();
        }

        public void moveUp(){
            //dont move out of bounds
            if(((ballRed.getCenterY()-ballRed.getRadius())-10)<0){
                ballRed.setCenterY(ballRed.getRadius());
            }
            else{ballRed.setCenterY(ballRed.getCenterY()-10);}
            checkFeed();
            updateArea();
        }

        public void moveRight(){
            //dont move out of bounds
            if(((ballRed.getCenterX()+ballRed.getRadius())+10)>screenWidth){
                ballRed.setCenterX(screenWidth - ballRed.getRadius());
            }
            else{ballRed.setCenterX(ballRed.getCenterX()+10);}
            checkFeed();
            updateArea();
        }

        public void moveDown(){
            //dont move out of bounds
            if(((ballRed.getCenterY()+ballRed.getRadius())+10)>screenHeight){
                ballRed.setCenterY(screenHeight - ballRed.getRadius());
            }
            else{ballRed.setCenterY(ballRed.getCenterY()+10);}
            checkFeed();
            updateArea();
        }

        public void spawnFood(){
            double tempx = randomDouble(0, screenWidth);
            double tempy = randomDouble(0, screenHeight);

            Circle tempFood = new Circle(tempx,tempy,5, randomColor());
            this.getChildren().add(tempFood);
            food.add(tempFood);
        }

        public void spawnFood( int amount){
            double[] tempx = randomDoubleArray(amount,0, screenWidth);
            double[] tempy = randomDoubleArray(amount,0, screenHeight);

            for(int i = 0; i < amount; i++){
                Circle tempFood = new Circle(tempx[i],tempy[i],5, randomColor());
                this.getChildren().add(tempFood);
                food.add(tempFood);
            }
        }

        public void spawnFoodSmart(){
            //if there is less than 5
            if(food.size()<5){
                spawnFood(10);
            }
            else if(food.size()<10){
                spawnFood(5);
            }
            else if(food.size()<=15){
                spawnFood(2);
            }
            else if(food.size()<=25){
                spawnFood();
            }
            else if(food.size()<=50){
                spawnFood();
                removeCorners();
            }
            //else do nothing
        }

        public void removeCorners(){
            double ballRad = ballRed.getRadius();
            double foodX;
            double foodY;
            //loop thru food
            for(int i=0; i < food.size();){
                foodX = food.get(i).getCenterX();
                foodY = food.get(i).getCenterY();
                //if the food is in a corner
                if((foodX<ballRad&&foodY<ballRad) || (foodX<ballRad&&foodY>(screenHeight-ballRad)) ||
                        (foodX>(screenWidth-ballRad)&&foodY>(screenHeight-ballRad)) || (foodX>(screenWidth-ballRad)&&foodY<ballRad)){
                    //remove and make a new one
                    getChildren().remove(i+2);
                    food.remove(i);
                    //print("Found " + i);
                    spawnFood();
                }
                else{i++;}
            }
        }

        public void removeFood(Point2D point){

            //loop thru food
            for(int i=0; i < food.size();){
                //if the food is in a corner
                if(food.get(i).contains(point)){
                    //remove and make a new one
                    getChildren().remove(i+2);
                    food.remove(i);
                    //print("Found " + i);
                    spawnFood();
                }
                else{i++;}
            }
        }

        public void checkFeed(){
            //loop thru food to see if ball is on top
            for(int i=0; i < food.size();){
                if(ballRed.contains(food.get(i).getCenterX(),food.get(i).getCenterY())){
                    addMass(food.remove(i));
                    getChildren().remove(i+2);
                    //print("Found " + i);
                }
                else{i++;}
            }
        }

        public void addMass(Circle eaten){
            double ballArea = (ballRed.getRadius() * ballRed.getRadius()) * Math.PI;
            double foodArea = (eaten.getRadius() * eaten.getRadius()) * Math.PI;

            double newRadius = Math.sqrt((ballArea + foodArea) / Math.PI);

            if(newRadius < ballRed.getRadius()){
                return;
            }
            ballRed.setRadius(newRadius);
        }

        public void updateArea(){
            //set text to circle area
            ballArea.setText("Area: " + String.format("%.2f",((ballRed.getRadius() * ballRed.getRadius())* Math.PI)));
            ballArea.setX(ballRed.getCenterX());
            ballArea.setY(ballRed.getCenterY());
        }

        public double getScore(){
            return (ballRed.getRadius() * ballRed.getRadius())* Math.PI;
        }

        public void toggleSize(){
            if(sizeShown){
                ballArea.setFill(Color.TRANSPARENT);
                ballArea.setStroke(Color.TRANSPARENT);
            }
            else{
                ballArea.setFill(Color.WHITE);
                ballArea.setStroke(Color.BLACK);
            }
            sizeShown = !sizeShown;
        }

        public void changeColor(int r, int g, int b){
            if(r > -1 && g > -1 && b > -1 && r < 256 && g < 256 && b < 256){
                ballRed.setFill(Color.rgb(r,g,b));
                //print("Color Changed! " + r + " " + g + " " + b);
            }
        }
    }

    public double randomDouble(double bottom, double top){
        return (ThreadLocalRandom.current().nextDouble(bottom, top));
    }

    public double[] randomDoubleArray(int length, double bottom, double top){
        //make temp array
        double tempList[] = new double[length];
        //loop for the input length
        for (int i = 0; i < length; i++) {
            //make random double from range bottom to top and add to array
            tempList[i] = (ThreadLocalRandom.current().nextDouble(bottom, top));
            //end loop
        }
        //return array
        return tempList;
    }

    public int randomInt(int bottom, int top){
        return (ThreadLocalRandom.current().nextInt(bottom, top + 1));
    }

    public static int[] randomIntArray(int length, int bottom, int top) {
        //make temp arraylist
        int tempList[] = new int[length];
        //loop for the input length
        for (int i = 0; i < length; i++) {
            //make random int from range bottom to top and add to arraylist
            tempList[i] = (ThreadLocalRandom.current().nextInt(bottom, top + 1));
            //end loop
        }
        //return arraylist
        return tempList;
    }

    public Color randomColor(){
        return Color.rgb(randomInt(0,255),randomInt(0,255),randomInt(0,255));
    }

    public void print(String message){
        System.out.println(message);
    }
}
