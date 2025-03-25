/*
    Student: Maksym Spizhovyi
    Student ID: 300 362 869
 */

package minesweeper;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.net.URL;
import java.util.*;

public class MinesweeperLab8 extends Application {
    VBox root = new VBox();

    private int cellSize = 20;
    private int gridRows = 16;
    private int gridCols = 16;
    private int minesCount = 40;

    private Image faceDeadImage = loadImage("/assets/face-dead.png");
    private Image faceSmileImage = loadImage("/assets/face-smile.png");
    private Image faceWinImage = loadImage("/assets/face-win.png");
    private Image mineGreyImage = loadImage("/assets/mine-grey.png");
    private Image mineRedImage = loadImage("/assets/mine-red.png");
    private Image btnCoverImage = loadImage("/assets/cover.png");
    private Image btnFlagImage = loadImage("/assets/flag.png");
    private Image mineMisflaggedImage = loadImage("/assets/mine-misflagged.png");
    private Image[] boardNumberImages = new Image[9];
    private Image[] boxNumberImages = new Image[10];

    private int flaggedMines = minesCount;
    private int timeElapsed = 0;

    private Label mineCounterLabel = new Label();
    private Label timerLabel = new Label();
    private Label smileyLabel = new Label();

    private HBox mineCounterBox = new HBox();
    private HBox timerBox = new HBox();


    private int[][] board = new int[gridRows][gridCols];
    private Button[][] cells = new Button[gridRows][gridCols];
    private boolean gameIsOver = false;
    private Random locationGenerator = new Random();
    Map<String, ArrayList<Integer>> difficultyGridSizes = new HashMap<>();
    private boolean firstClickCompleted = false;

    public void start(Stage primaryStage) {
        loadBoardAndBoxNumberImages();
        initializeDifficultyGridSizes();
        setupUserInterface(primaryStage);
    }

    private void loadBoardAndBoxNumberImages() {
        loadBoardNumberImages();
        loadBoxNumberImages();
    }

    private void loadBoardNumberImages() {
        for (int number = 0; number <= 8; number++) {
            boardNumberImages[number] = loadImage(imagePath("board_number", number));
        }
    }

    private void loadBoxNumberImages() {
        for (int number = 0; number <= 9; number++) {
            boxNumberImages[number] = loadImage(imagePath("box_number", number));
        }
    }

    private String imagePath(String folder, int number) {
        return String.format("/assets/%s/%d.png", folder, number);
    }
    
    private Image loadImage(String path) {
        URL url = getClass().getResource(path);

        if(url == null){
            System.out.println("Error: Image not found - " + path);
        }

        return new Image(url.toExternalForm());
    }

    private void initializeDifficultyGridSizes() {
        difficultyGridSizes.put("Beginner", new ArrayList<>(Arrays.asList(8, 8, 10)));
        difficultyGridSizes.put("Intermediate", new ArrayList<>(Arrays.asList(16, 16, 40)));
        difficultyGridSizes.put("Expert", new ArrayList<>(Arrays.asList(32, 16, 99)));
    }

    private void setupUserInterface(Stage primaryStage){
        initializeRoot();
        HBox header = createHeaderComponent();
        HBox panel = createModeSelectionPanel();
        GridPane cellBoard = createCellBoardComponent();

        root.getChildren().addAll(header, panel, cellBoard);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        configureStage(primaryStage);
    }

    private void configureStage(Stage primaryStage){
//        primaryStage.setResizable(false);
        primaryStage.setTitle("Minesweeper (Maksym Spizhovy)");
        primaryStage.show();
//        Platform.runLater(() -> {
//            primaryStage.setWidth(300);
//            primaryStage.setHeight(500);
//        });
    }

    private void initializeRoot(){
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        root.setStyle("-fx-background-color: #C0C0C0; -fx-padding: 10px;");
    }

    private HBox createHeaderComponent(){
        HBox headerContainer = createHeaderContainer();
        initializeSmileyLabel();
        initializeStyledNumericBox(mineCounterBox, flaggedMines);
        initializeStyledNumericBox(timerBox, timeElapsed);
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

    private void initializeStyledNumericBox(HBox mineCountContainer, int val){
        mineCountContainer.setStyle(
                "-fx-background-color: #000;" +
                        "-fx-padding: 10px; " +
                        "-fx-border-color: #a6a6a6; " +
                        "-fx-border-width: 1px; " +
                        "-fx-alignment: center; " +
                        "-fx-border-radius: 4px; " +
                        "-fx-background-radius: 4px;"
        );

        updateViewOfTheStyledNumericBox(mineCountContainer, val);
    }

    private void appendLabelsIntoHeaderComponent(HBox headerContainer){
        headerContainer.getChildren().addAll(mineCounterBox, smileyLabel, timerBox);
    }

    private void initializeSmileyLabel(){
        ImageView smileyView = createImageViewForSmiley(faceSmileImage);
        smileyLabel.setGraphic(smileyView);
        smileyLabel.setStyle(
                        "-fx-background-color: #000;" +
                        "-fx-border-color: #a6a6a6;" +
                        "-fx-border-width: 1px;" +
                        "-fx-padding: 1px;"
        );
        smileyLabel.setOnMouseClicked(event -> resetGame());
    }

    private HBox createModeSelectionPanel(){
        HBox panel = new HBox();
        String[] modes = {"Beginner", "Intermediate", "Expert"};

        for(String mode : modes){
            panel.getChildren().add(createModeSelectionButton(mode));
        }

        return panel;
    }


    private Button createModeSelectionButton(String mode){
        Button button = new Button();

        button.setText(mode);
        button.setStyle(
                "-fx-padding: 12px 24px;"
        );
        button.setOnAction(event -> handleResetModeByDifficulty(mode));

        return button;
    }

    private void handleResetModeByDifficulty(String mode) {
        ArrayList<Integer> data = difficultyGridSizes.get(mode);
        if (data == null) return;

        applyDifficultySettings(data);
        resetUIElementsForNewGame();
        generateNewGameBoard();
    }

    private void applyDifficultySettings(ArrayList<Integer> data){
        gridRows = data.get(0);
        gridCols = data.get(1);
        minesCount = data.get(2);
    }

    private void resetUIElementsForNewGame(){
        timeElapsed = 0;
        flaggedMines = minesCount;
        gameIsOver = false;
        drawASmileyFace();
        initializeStyledNumericBox(mineCounterBox, flaggedMines);
        initializeStyledNumericBox(timerBox, timeElapsed);
    }

    private void generateNewGameBoard() {
        if (root.getChildren().size() > 2) {
            root.getChildren().remove(root.getChildren().size() - 1);
        }

        GridPane newCellBoard = createCellBoardComponent();
        root.getChildren().add(newCellBoard);
    }

    private void drawASmileyFace(){
        ImageView smileyView = createImageViewForSmiley(faceSmileImage);
        smileyLabel.setGraphic(smileyView);
    }

    private void resetGame(){
        resetMeaningfulVariables();
        resetDefaultSmileyFace();
        resetLabelsToDefaultState();
        updateViewOfTheStyledNumericBox(mineCounterBox, flaggedMines);
        resetCellsView();
        placeMines();
        placeNumbersIndentifyingMinesCount();
    }

    private void resetMeaningfulVariables(){
        timeElapsed = 0;
        flaggedMines = minesCount;
        board = new int[gridRows][gridCols];
        gameIsOver = false;
        firstClickCompleted = false;
    }

    private void resetDefaultSmileyFace(){
        ImageView smileyView = createImageViewForSmiley(faceSmileImage);
        smileyLabel.setGraphic(smileyView);
    }

    private void resetLabelsToDefaultState(){
        updateViewOfTheStyledNumericBox(mineCounterBox, flaggedMines);
        updateViewOfTheStyledNumericBox(timerBox, timeElapsed);
    }

    private void resetCellsView(){
        for(int row = 0; row < gridRows; row++){
            for(int col = 0; col < gridCols; col++){
                ImageView coverView = createImageViewForBoardCell(btnCoverImage);
                cells[row][col].setGraphic(coverView);
                cells[row][col].setDisable(false);
            }
        }
    }

    private GridPane createCellBoardComponent(){
        GridPane cellBoardContainer = generateNewCellBoardContainer();
        cells = new Button[gridRows][gridCols];
        initializeBoardCellsWithEventHandlers(cellBoardContainer);
        board = new int[gridRows][gridCols];
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

    private void initializeBoardCellsWithEventHandlers(GridPane cellBoardContainer){
        for(int row = 0; row < gridRows; row++){
            for(int col = 0; col < gridCols; col++){
                Button cell = createCellButton();
                cells[row][col] = cell;

                final int finalRow = row, finalCol = col;
                cell.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        handleLeftCellClick(finalRow, finalCol);
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        handleRightCellClick(finalRow, finalCol);
                    }
                });

                cellBoardContainer.add(cell, row, col);
            }
        }
    }

    private void placeMines(){
        int placedMines = 0;

        while(placedMines < minesCount){
            int row = locationGenerator.nextInt(gridRows);
            int col = locationGenerator.nextInt(gridCols);

            if (!isMine(row, col)) {
                board[row][col] = -1;
                placedMines++;
            }
        }
    }

    private Button createCellButton(){
        ImageView coverView = createImageViewForBoardCell(btnCoverImage);
        Button cell = new Button();
        cell.setMinSize(cellSize, cellSize);
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
        for(int row = 0; row < gridRows; row++){
            for(int col = 0; col < gridCols; col++){
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
        return row >= 0 && row < gridRows && col >= 0 && col < gridCols;
    }

    private void handleLeftCellClick(int row, int col){
        if(gameIsOver) return;
        if(isFlaggedCell(row, col) || !isCoveredCell(row, col)) return;

        if(!firstClickCompleted && isMine(row, col)) {
            rebuildBoard(row, col);
        }

        if(isMine(row, col)){
            drawRedMine(row, col);
            gameOver();
        } else {
            revealCells(row, col);
        }

        if(containsUnrevealedSafeCell()){
            drawAWinningFace();
        }

        firstClickCompleted = true;
    }


    private void rebuildBoard(int safeRow, int safeCol) {
        board = new int[gridRows][gridCols];
        gameIsOver = false;
        flaggedMines = minesCount;
        placeMinesSafely(safeRow, safeCol);
        placeNumbersIndentifyingMinesCount();
    }


    private void placeMinesSafely(int safeRow, int safeCol){
        int placedMines = 0;
        while (placedMines < minesCount) {
            int row = locationGenerator.nextInt(gridRows);
            int col = locationGenerator.nextInt(gridCols);

            if (!isMine(row, col) && !(row == safeRow && col == safeCol)) {
                board[row][col] = -1;
                placedMines++;
            }
        }
    }

    private boolean isCoveredCell(int row, int col){
        return hasImage(cells[row][col], btnCoverImage);
    }

    private boolean hasImage(Button cell, Image image) {
        return cell.getGraphic() instanceof ImageView &&
                ((ImageView) cell.getGraphic()).getImage() == image;
    }

    private void revealCells(int row, int col){
        if(isFlaggedCell(row, col)) {
            updateViewOfTheStyledNumericBox(mineCounterBox, ++flaggedMines);
        }

        if(!isWithinBounds(row, col) || !isCoveredOrFlaggedCell(row, col)){
            return;
        }

        drawANumber(row, col);

        if(board[row][col] > 0) return;

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
        gameIsOver = true;
        drawExpectedCell();
    }

    private void drawDeadFace(){
        ImageView deadFaceView = createImageViewForSmiley(faceDeadImage);
        smileyLabel.setGraphic(deadFaceView);
    }

    private ImageView createImageViewForSmiley(Image image) {
        return createImageView(image, 70, 70);
    }

    private void drawExpectedCell(){
        for(int row = 0; row < gridRows; row++){
            for(int col = 0; col < gridCols; col++){
                if(isRedMine(row, col)) continue;
                if(isFlaggedCell(row, col) && isMine(row, col)) {
                    ImageView mineMisflaggedView = createImageViewForBoardCell(mineMisflaggedImage);
                    cells[row][col].setGraphic(mineMisflaggedView);
                    continue;   
                };

                if(isMine(row, col)){
                    ImageView mine = createImageViewForBoardCell(mineGreyImage);
                    cells[row][col].setGraphic(mine);
                }
            }
        }
    }

    private boolean isRedMine(int row, int col){
        return hasImage(cells[row][col], mineRedImage);
    }

    private void drawRedMine(int row, int col){
        ImageView redMineView = createImageViewForBoardCell(mineRedImage);
        cells[row][col].setGraphic(redMineView);
    }

    private void drawANumber(int row, int col){
        ImageView numberView = createImageViewForBoardCell(boardNumberImages[board[row][col]]);
        cells[row][col].setGraphic(numberView);
    }

    private boolean containsUnrevealedSafeCell(){
        for(int row = 0; row < gridRows; row++){
            for(int col = 0; col < gridCols; col++){
                if(isUnrevealedSafeCell(row, col)){
                    return false;
                }
            }
        }

        return true;
    }

    private boolean isUnrevealedSafeCell(int row, int col){
        return !isMine(row, col) && isCoveredCell(row, col);
    }

    private boolean isMine(int row, int col){
        return board[row][col] == -1;
    }

    private void drawAWinningFace(){
        ImageView winFaceView = createImageViewForSmiley(faceWinImage);
        smileyLabel.setGraphic(winFaceView);
    }

    private void handleRightCellClick(int row, int col){
        if(!isCoveredOrFlaggedCell(row, col) || gameIsOver) return;
        if(isFlaggedCell(row, col)) removeFlag(row, col);
        else addFlag(row, col);
    }

    private boolean isCoveredOrFlaggedCell(int row, int col){
        return isFlaggedCell(row, col) || isCoveredCell(row, col);
    }

    private boolean isFlaggedCell(int row, int col) {
        return hasImage(cells[row][col], btnFlagImage);
    }

    private void addFlag(int row, int col){
        if(flaggedMines == 0) return;
        ImageView flagView = createImageViewForBoardCell(btnFlagImage);
        cells[row][col].setGraphic(flagView);
        updateViewOfTheStyledNumericBox(mineCounterBox, --flaggedMines);
    }

    private void removeFlag(int row, int col){
        ImageView coverView = createImageViewForBoardCell(btnCoverImage);
        cells[row][col].setGraphic(coverView);
        updateViewOfTheStyledNumericBox(mineCounterBox, ++flaggedMines);
    }

    private void updateViewOfTheStyledNumericBox(HBox mineCountContainer, int val){
        mineCountContainer.getChildren().clear();

        String formatedDigit = String.format("%03d", val);

        for (int i = 0; i < formatedDigit.length(); i++) {
            Image digit = boxNumberImages[Character.getNumericValue(formatedDigit.charAt(i))];
            ImageView digitImage = createImageView(digit, 30, 50);
            mineCountContainer.getChildren().add(digitImage);
        }

        HBox.setHgrow(mineCountContainer, Priority.ALWAYS);
    }
    
    private ImageView createImageViewForBoardCell(Image image) {
        return createImageView(image, cellSize, cellSize);
    }

    private ImageView createImageView(Image image, int width, int height) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }
}

