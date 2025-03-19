/*
    Student: Maksym Spizhovyi
    Student ID: 300 362 869
 */

package minesweeper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


import java.net.URL;
import java.util.Random;

public class MinesweeperLab6 extends Application {
    private static final int GRID_SIZE = 5;
    private static final int MINES = 3;
    private Image faceDeadImage = loadImage("/assets/face-dead.png");
    private Image faceSmileImage = loadImage("/assets/face-smile.png");
    private Image faceWinImage = loadImage("/assets/face-win.png");
    private Image mineGreyImage = loadImage("/assets/mine-grey.png");
    private Image btnCoverImage = loadImage("/assets/cover.png");
    private Image[] numberImages = new Image[9];

    private int remainingMines = MINES;
    private int timeElapsed = 0;

    private Label mineCounterLabel = new Label();
    private Label timerLabel = new Label();
    private Label smileyLabel = new Label();

    private int[][] board = new int[GRID_SIZE][GRID_SIZE];
    private final Button[][] cells = new Button[GRID_SIZE][GRID_SIZE];

    @Override
    public void start(Stage primaryStage) {
        generateNumberImages();
        setupUserInterface(primaryStage);
    }

    private void generateNumberImages(){
        for(int i = 0; i < numberImages.length; i++){
            numberImages[i] = loadImage("/assets/" + i + ".png");
        }
    }

    private Image loadImage(String path) {
        URL url = getClass().getResource(path);

        if(url == null){
            System.out.println("Error: Image not found - " + path);
        }

        return new Image(url.toExternalForm());
    }

    private void setupUserInterface(Stage primaryStage){
        VBox rootContainer = createRootContainer();
        HBox header = createHeaderComponent();
        GridPane cellBoard = createCellBoardComponent();

        rootContainer.getChildren().addAll(header, cellBoard);

        Scene scene = new Scene(rootContainer, 400, 500);
        primaryStage.setScene(scene);
        configureStage(primaryStage);
    }

    private void configureStage(Stage primaryStage){
        primaryStage.setResizable(false);
        primaryStage.setTitle("Minesweeper (Maksym Spizhovy)");
        primaryStage.show();
        Platform.runLater(() -> {
            primaryStage.setWidth(300);
            primaryStage.setHeight(400);
        });
    }

    private VBox createRootContainer(){
        VBox newRoot = new VBox();
        newRoot.setAlignment(Pos.CENTER);
        newRoot.setSpacing(10);
        newRoot.setStyle("-fx-background-color: #C0C0C0; -fx-padding: 10px;");
        return newRoot;
    }

    private HBox createHeaderComponent(){
        HBox headerContainer = createHeaderContainer();
        initializeSmileyLabel();
        initializeStyledNumericLabel(mineCounterLabel, remainingMines);
        initializeStyledNumericLabel(timerLabel, timeElapsed);
        appendLabelsIntoHeaderComponent(headerContainer);
        return headerContainer;
    }

    private HBox createHeaderContainer(){
        HBox newHeader = new HBox();
        newHeader.setAlignment(Pos.CENTER);
        newHeader.setSpacing(1);
        newHeader.setStyle(
                        "-fx-padding: 5px;" +
                        " -fx-background-color: #DBDBDB;" +
                        " -fx-border-color:  #a6a6a6;" +
                        " -fx-border-width: 1px;"
        );
        return newHeader;
    }

    private void initializeStyledNumericLabel(Label label, int val){
        label.setText(String.format("%03d", val));
        label.setFont(Font.font("Courier", FontWeight.SEMI_BOLD, 24));
        label.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #EAF2FB, #B0CBE8); " +
                        "-fx-padding: 10px; " +
                        "-fx-border-color: #a6a6a6; " +
                        "-fx-border-width: 1px; " +
                        "-fx-text-fill: #000; " +
                        "-fx-font-size: 24px; " +
                        "-fx-font-family: 'Courier'; " +
                        "-fx-alignment: center;"
        );

        label.setMaxWidth(Double.MAX_VALUE);
        label.setMaxHeight(Double.MAX_VALUE);
        HBox.setHgrow(label, Priority.ALWAYS);
    }

    private void appendLabelsIntoHeaderComponent(HBox headerContainer){
        headerContainer.getChildren().addAll(mineCounterLabel, smileyLabel, timerLabel);
    }

    private void initializeSmileyLabel(){
        ImageView smileyView = createImageView(faceSmileImage);
        smileyLabel.setGraphic(smileyView);
        smileyLabel.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #EAF2FB, #B0CBE8);" +
                        " -fx-border-color: #a6a6a6;" +
                        " -fx-border-width: 1px;" +
                        " -fx-padding: 5px;"
        );
        smileyLabel.setOnMouseClicked(event -> resetGame());
    }

    private void resetGame(){
        resetDefaultSmileyFace();
        resetLabelsToDefaultState();
        resetMeaningfulVariables();
        resetCellsView();
        placeMines();
        placeNumbersIndentifyingMinesCount();
    }

    private void resetDefaultSmileyFace(){
        ImageView smileyView = createImageView(faceSmileImage);
        smileyLabel.setGraphic(smileyView);
    }

    private void resetLabelsToDefaultState(){
        mineCounterLabel.setText(String.format("%03d", remainingMines));
        timerLabel.setText(String.format("%03d", timeElapsed));
    }

    private void resetMeaningfulVariables(){
        board = new int[GRID_SIZE][GRID_SIZE];
        remainingMines = MINES;
        timeElapsed = 0;
    }

    private void resetCellsView(){
        for(int row = 0; row < GRID_SIZE; row++){
            for(int col = 0; col < GRID_SIZE; col++){
                ImageView coverView = createImageView(btnCoverImage);
                cells[row][col].setGraphic(coverView);
                cells[row][col].setDisable(false);
            }
        }
    }

    private void placeMines(){
        int placedMines = 0;
        Random locationGenerator = new Random();

        while(placedMines < MINES){
            int row = locationGenerator.nextInt(GRID_SIZE);
            int col = locationGenerator.nextInt(GRID_SIZE);

            if (!isMine(row, col)) {
                board[row][col] = -1;
                placedMines++;
            }
        }
    }

    private GridPane createCellBoardComponent(){
        GridPane cellBoardContainer = generateNewCellBoardContainer();
        initializeCellsToTheBoard(cellBoardContainer);
        placeMines();
        placeNumbersIndentifyingMinesCount();
        return cellBoardContainer;
    }

    private GridPane generateNewCellBoardContainer(){
        GridPane cellBoardContainer = new GridPane();
        cellBoardContainer.setStyle(
                        "-fx-hgap: 2px;" +
                        " -fx-vgap: 2px;" +
                        " -fx-padding: 1px;" +
                        " -fx-background-color: #A0A0A0;"
        );
        return cellBoardContainer;
    }

    private void initializeCellsToTheBoard(GridPane cellBoardContainer){
        for(int row = 0; row < GRID_SIZE; row++){
            for(int col = 0; col < GRID_SIZE; col++){
              Button cell = createCellButton();
              cells[row][col] = cell;

              final int finalRow = row, finalCol = col;
              cell.setOnAction(e -> handleCellClick(finalRow, finalCol));

              cellBoardContainer.add(cell, row, col);
            }
        }
    }

    private Button createCellButton(){
        ImageView coverView = createImageView(btnCoverImage);
        Button cell = new Button();
        cell.setMinSize(50, 50);
        cell.setStyle(
                        "-fx-background-color: lightgray;" +
                        " -fx-border-color: darkgray;" +
                        " -fx-border-width: 2px;" +
                        " -fx-padding: 0px;"
        );
        cell.setGraphic(coverView);
        return cell;
    }


    private void placeNumbersIndentifyingMinesCount(){
        for(int row = 0; row < GRID_SIZE; row++){
            for(int col = 0; col < GRID_SIZE; col++){
                if(isMine(row, col)) continue;
                board[row][col] = getAdjacentMineCount(row, col);
            }
        }
    }

    private int getAdjacentMineCount(int row, int col){
        int mineCount = 0;
        int[] rowsOffsets = {-1, -1, -1,  0, 0,  1, 1, 1};
        int[] colsOffsets = {-1,  0,  1, -1, 1, -1, 0, 1};

        for(int i = 0; i < 8; i++){
            int adjacentRow = rowsOffsets[i] + row;
            int adjacentCol = colsOffsets[i] + col;

            if(isWithinBounds(adjacentRow, adjacentCol) && isMine(adjacentRow, adjacentCol)){
                mineCount++;
            }
        }

        return mineCount;
    }

    private boolean isWithinBounds(int row, int col){
        return row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE;
    }

    private void handleCellClick(int row, int col){
        if(isMine(row, col)){
            gameOver();
        } else {
            revealCells(row, col);
        }

        if(containsUnrevealedSafeCell()){
            drawAWinningFace();
        }
    }

    private void revealCells(int row, int col){
        if(!isWithinBounds(row, col) || cells[row][col].isDisabled()){
            return;
        }

        drawANumber(row, col);

        if(board[row][col] > 0){
            return;
        }

        int[] rowsOffsets = {-1, -1, -1, 0, 0,  1, 1, 1};
        int[] colsOffsets = {-1,  0, 1, -1, 1, -1, 0, 1};

        for(int i = 0; i < 8; i++){
            int adjacentRow = row + rowsOffsets[i];
            int adjacentCol = col + colsOffsets[i];

            if(isWithinBounds(adjacentRow, adjacentCol)){
                revealCells(adjacentRow, adjacentCol);
            }
        }
    }

    private void gameOver(){
        drawDeadFace();
        disableAbilityToClick();
    }

    private void drawDeadFace(){
        ImageView deadFaceView = createImageView(faceDeadImage);
        smileyLabel.setGraphic(deadFaceView);
    }

    private void disableAbilityToClick(){
        for(int row = 0; row < GRID_SIZE; row++){
            for(int col = 0; col < GRID_SIZE; col++){
                if(isMine(row, col)){
                    ImageView mine = createImageView(mineGreyImage);
                    cells[row][col].setGraphic(mine);
                }

                cells[row][col].setDisable(true);
            }
        }
    }

    private void drawANumber(int row, int col){
        ImageView numberView = createImageView(numberImages[board[row][col]]);
        cells[row][col].setGraphic(numberView);
        cells[row][col].setDisable(true);
    }

    private ImageView createImageView(Image image){
        ImageView coverView = new ImageView(image);
        coverView.setFitWidth(50);
        coverView.setFitHeight(50);
        return coverView;
    }

    private boolean containsUnrevealedSafeCell(){
        for(int row = 0; row < GRID_SIZE; row++){
            for(int col = 0; col < GRID_SIZE; col++){
                if(isUnrevealedSafeCell(row, col)){
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isUnrevealedSafeCell(int row, int col){
        return !isMine(row, col) &&
                cells[row][col].getGraphic() instanceof ImageView &&
                ((ImageView) cells[row][col].getGraphic()).getImage() == btnCoverImage;
    }

    private boolean isMine(int row, int col){
        return board[row][col] == -1;
    }

    private void drawAWinningFace(){
        ImageView winFaceView = createImageView(faceWinImage);
        smileyLabel.setGraphic(winFaceView);
    }
}

