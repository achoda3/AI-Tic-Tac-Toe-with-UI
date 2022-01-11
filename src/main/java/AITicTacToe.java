
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.*;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AITicTacToe extends Application {

	String LightDif="expert";
	String LDif="expert";
	Integer index;
	Character turn;
	String winner;
	TextField numberEntered;
	Button Play;
	GridPane theGame;
	int lightWins;
	int LWins;
	TextField lightWin;
	TextField LWin;
	int Current;
	int Curr;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("chonk tac toe!!");
		HashMap<String, Scene> sceneMap = new HashMap<String, Scene>();
		sceneMap.put("welcome",  createWelcomeGUI());
		sceneMap.put("game", createGameGUI());
		
		//Setting Plays Action
		Play.setOnAction(e->{
			int numberOfGames;
			try {
				numberOfGames = Integer.parseInt(numberEntered.getText());
			} catch(NumberFormatException exception) {
				System.out.println("Invalid number of Games entered!");
				numberOfGames=0;
			}
			System.out.println(numberOfGames);
			Current=0;
			Curr=-1;
			for(int i=0; i<numberOfGames; i++) {
				ArrayList<Character> gameBoard = new ArrayList<>(Arrays.asList('-','-','-','-','-','-','-','-','-'));
				
				//Making executor threads to run the algorithm
				ExecutorService ex = Executors.newFixedThreadPool(5);
				
				//Making a seperate Thread to update the UI and not hold application thread
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						final int Id = Curr+1;
						Curr++;
						System.out.println("Thread:"+Curr);
						System.out.println("Current is"+Current);
						while(Current!=Id) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						System.out.println("Reached!");
						turn = 'x';
						String currDif = LightDif;
						for(int iter = 0; iter < 9; iter++) {
								Future<Integer> future = ex.submit(new MyCall(gameBoard, turn, currDif));
								try {
									index=future.get();
									gameBoard.set(index, turn);
									System.out.println("Bruuh");
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ExecutionException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Platform.runLater(()->{
									theGame.getChildren().clear();
									for(int i=0; i<3; i++) {
										for(int j=0; j<3; j++) {
											TextField spot = new TextField(Character.toString(gameBoard.get(i+3*j)));
											spot.setId("tictactoe");
											spot.setEditable(false);
											spot.setPrefSize(100, 100);
											theGame.add(spot, i, j);
										}
									}
								});
								
								//If win condition occurs
								if(checkWin(gameBoard)) {
									ex.shutdown();
									if(turn=='x') {
										winner="Light";
										lightWins++;
									} else {
										winner="L";
										LWins++;
									}
									System.out.println(winner);
									Platform.runLater(()->{
										primaryStage.setScene(createWinGUI());
										PauseTransition pause = new PauseTransition(Duration.seconds(3));
									    pause.setOnFinished(e -> {
									    	lightWin.setText(Integer.toString(lightWins));
											LWin.setText(Integer.toString(LWins));
											Current++;
									        primaryStage.setScene(sceneMap.get("game"));
									        System.out.println("Current is"+Current);
									    });
									    pause.play();
									});
									break;
								}
								
								//If board is full
								if(iter==8) {
									ex.shutdown();
									winner="TIE";
									Platform.runLater(()->{
										primaryStage.setScene(createWinGUI());
										PauseTransition pause = new PauseTransition(Duration.seconds(3));
									    pause.setOnFinished(e -> {
									    	lightWin.setText(Integer.toString(lightWins));
											LWin.setText(Integer.toString(LWins));
											Current++;
									        primaryStage.setScene(sceneMap.get("game"));
									        System.out.println("Current is"+Current);
									    });
									    pause.play();
									});
									break;
								}
								
								if(turn == 'x') {
									currDif=LDif;
									turn = 'o';
								}
								else {
									currDif=LightDif;
									turn = 'x';
								}
						}
						
					}
				});
				thread.setDaemon(true);
				thread.start();
			}
			
		});
		
		primaryStage.setScene(sceneMap.get("welcome"));
		primaryStage.show();
		PauseTransition pause = new PauseTransition(Duration.seconds(3));
	    pause.setOnFinished(e -> {
	        primaryStage.setScene(sceneMap.get("game"));
	    });
	    pause.play();
	}
	
	public Scene createWelcomeGUI() {
		BorderPane welcome = new BorderPane();
		welcome.setPadding(new Insets(70));
		BackgroundImage backGround = new BackgroundImage(new Image("welcome.jpg",1680,1050,false,false),BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				new BackgroundSize(1.0, 1.0, true, true, false, false));
		welcome.setBackground(new Background(backGround));
		Text welcomeText = new Text();
		welcomeText.setText("WELCOME!");
		welcomeText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 50));
		welcomeText.setFill(Color.WHITE); 
		welcome.setCenter(welcomeText);
		
		return new Scene(welcome, 1600, 900);
	}
	
	Boolean checkWin(ArrayList<Character> gameBoard) {
		if(gameBoard.get(0)==gameBoard.get(1)&&gameBoard.get(1)==gameBoard.get(2)&&gameBoard.get(0)!='-') {
			return true;
		}
		if(gameBoard.get(3)==gameBoard.get(4)&&gameBoard.get(4)==gameBoard.get(5)&&gameBoard.get(5)!='-') {
			return true;
		}
		if(gameBoard.get(6)==gameBoard.get(7)&&gameBoard.get(7)==gameBoard.get(8)&&gameBoard.get(8)!='-') {
			return true;
		}
		if(gameBoard.get(0)==gameBoard.get(3)&&gameBoard.get(3)==gameBoard.get(6)&&gameBoard.get(6)!='-') {
			return true;
		}
		if(gameBoard.get(1)==gameBoard.get(4)&&gameBoard.get(4)==gameBoard.get(7)&&gameBoard.get(7)!='-') {
			return true;
		}
		if(gameBoard.get(2)==gameBoard.get(5)&&gameBoard.get(5)==gameBoard.get(8)&&gameBoard.get(8)!='-') {
			return true;
		}
		if(gameBoard.get(2)==gameBoard.get(4)&&gameBoard.get(4)==gameBoard.get(6)&&gameBoard.get(6)!='-') {
			return true;
		}
		if(gameBoard.get(0)==gameBoard.get(4)&&gameBoard.get(4)==gameBoard.get(8)&&gameBoard.get(8)!='-') {
			return true;
		}
		return false;
	}
	
	public Scene createGameGUI() {
		BorderPane game = new BorderPane();
		TextArea dispInstructions = new TextArea();
		
		//-------- Menu
		Menu menuTab = new Menu("Menu");
		MenuItem iOne = new MenuItem("Extra Info");
		MenuItem iTwo = new MenuItem("exit");
		MenuItem iThree = new MenuItem("Information");
		MenuBar menu = new MenuBar();
		menuTab.getItems().addAll(iOne, iTwo);
		menu.getMenus().add(menuTab);
		iOne.setId("menuItems");
		iTwo.setId("menuItems");
		iThree.setId("menuItems");
		menuTab.setId("menuItems");
		iOne.setStyle("-fx-text-fill: red;");
		iTwo.setStyle("-fx-text-fill: red;");
		iThree.setStyle("-fx-text-fill: red;");
		
		//-------- Menu Actions
		iOne.setOnAction(e->{
			dispInstructions.setText("Light Yagami (Tatsuya Fujiwara) is a normal, undistinguished college"+
					"\nstudent -- that is, until he discovers an odd notebook lying on the ground."+
					"\nHe soon discovers that the notebook has magic powers: If someone's loses in"+
					"\nin Tic Tac Toe while the writer imagines that person's face, he or she will die."+
					"\nIntoxicated with his new godlike power, Light kills those he deems unworthy of life."+
					"\nBut a mysterious detective known only as L (Ken'ichi Matsuyama) becomes determined to"+
					"\nput a stop to his reign."+
					"\nThe ultimate game of Tic Tac Toe Begins!");
			menuTab.getItems().remove(0);
			menuTab.getItems().add(0, iThree);
		});
		
		iTwo.setOnAction(e->{
			Platform.exit();
			System.exit(0);
		});
		
		iThree.setOnAction(e->{
			dispInstructions.setText("This is an Auto Played game of Tic Tac Toe"+
					 "\nthat is played between two different AI's!"+
					 "\nPlease select the number of games to be Played"+
					 "\nas well as the difficulty of both the AI's"+
					 "\nThen Press the Play button to begin"+
					 "\nThe scores will be kept track at the bottom");
			menuTab.getItems().remove(0);
			menuTab.getItems().add(0, iOne);
		});
		
		//-------- Instructions
		dispInstructions.setText("This is an Auto Played game of Tic Tac Toe"+
								 "\nthat is played between two different AI's!"+
								 "\nPlease select the number of games to be Played"+
								 "\nas well as the difficulty of both the AI's"+
								 "\nThen Press the Play button to begin"+
								 "\nThe scores will be kept track at the bottom");
		dispInstructions.setEditable(false);
		VBox dispBox = new VBox(dispInstructions);
		dispBox.setPrefSize(210, 250);
		dispBox.setPadding(new Insets(100,0,0,0));
		
		//-------- Number of Games
		TextField gamesPlayed = new TextField("Number of games to be Played:");
		numberEntered = new TextField();
		gamesPlayed.setEditable(false);
		gamesPlayed.setPrefWidth(250);
		HBox games = new HBox(20, gamesPlayed, numberEntered);
		
		//-------- Game Difficulty
		TextField dispDifficulty = new TextField("Select Difficulty of Light(PC1)");
		Button novice = new Button("novice");
		Button advanced = new Button("advanced");
		Button expert = new Button("expert");
		HBox rightDif = new HBox(0, novice, advanced, expert);
		HBox difficulty = new HBox(20,dispDifficulty, rightDif);
		dispDifficulty.setPrefWidth(250);
		
		TextField dispDifficulty1 = new TextField("Select Difficulty of L(PC2)");
		Button novice1 = new Button("novice");
		Button advanced1 = new Button("advanced");
		Button expert1 = new Button("expert");
		HBox rightDif1 = new HBox(0, novice1, advanced1, expert1);
		HBox difficulty1 = new HBox(20,dispDifficulty1, rightDif1);
		dispDifficulty1.setPrefWidth(250);
		
		//-------- Sides
		TextField light = new TextField("Light Yagami(PC1)");
		TextField L = new TextField("L(PC2)");
		lightWin = new TextField();
		LWin = new TextField();
		lightWins=0;
		LWins=0;
		lightWin.setText(Integer.toString(lightWins));
		LWin.setText(Integer.toString(LWins));
		lightWin.setEditable(false);
		light.setEditable(false);
		L.setEditable(false);
		LWin.setEditable(false);
		VBox leftWin = new VBox(20, light, lightWin);
		VBox rightWin = new VBox(20, L, LWin);
		HBox Winnings = new HBox(20, leftWin, rightWin);
		
		//-------- Play
		Play = new Button("PLAY");
		Play.setId("play");
		VBox playBox = new VBox(Play);
		playBox.setPadding(new Insets(0, 0, 0, 50));
		
		//Left Side
		VBox left = new VBox(50,dispBox, games, difficulty, difficulty1, Winnings, playBox);
		
		//-------- GridPane
		theGame = new GridPane();
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				TextField space = new TextField(" ");
				space.setId("tictactoe");
				space.setEditable(false);
				space.setPrefSize(100, 100);
				theGame.add(space, i, j);
			}
		}
		VBox gridBox = new VBox(theGame);
		gridBox.setPadding(new Insets(350,0,0,75));
		
		//-------- actions
		novice.setOnAction(e->{
			LightDif="novice";
		});
		advanced.setOnAction(e->{
			LightDif="advanced";
		});
		expert.setOnAction(e->{
			LightDif="expert";
		});
		novice1.setOnAction(e->{
			LDif="novice";
		});
		advanced1.setOnAction(e->{
			LDif="advanced";
		});
		expert1.setOnAction(e->{
			LDif="expert";
		});
		
		
		
		//-------- setting the Pane
		game.setTop(menu);
		game.setLeft(left);
		game.setCenter(gridBox);
		BackgroundImage backGround = new BackgroundImage(new Image("game.jpg",1680,1050,false,false),BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
				new BackgroundSize(1.0, 1.0, true, true, false, false));
		game.setBackground(new Background(backGround));
		Scene gameScene = new Scene(game, 1600,900);
		gameScene.getStylesheets().add("style1.css");
		return gameScene;
	}
	
	public Scene createWinGUI() {
		BorderPane welcome = new BorderPane();
		welcome.setPadding(new Insets(70));
		System.out.println(winner);
		if(winner=="Light") {
			BackgroundImage backGround = new BackgroundImage(new Image("LightWon.jpg",1680,1050,false,false),BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
					new BackgroundSize(1.0, 1.0, true, true, false, false));
			welcome.setBackground(new Background(backGround));
		} else if(winner=="L") {
			BackgroundImage backGround = new BackgroundImage(new Image("Lwon.jpg",1680,1050,false,false),BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
					new BackgroundSize(1.0, 1.0, true, true, false, false));
			welcome.setBackground(new Background(backGround));
		} else {
			BackgroundImage backGround = new BackgroundImage(new Image("tie.jpg",1680,1050,false,false),BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
					new BackgroundSize(1.0, 1.0, true, true, false, false));
			welcome.setBackground(new Background(backGround));
			Text wonText = new Text();
			wonText.setText("TIE");
			wonText.setStyle(
					"-fx-font-size: 50;"+
					"-fx-font-famile: Courier New;");
			wonText.setFill(Color.BLACK); 
			welcome.setCenter(wonText);
		}
		return new Scene(welcome, 1600, 900);
	}

};



class MyCall implements Callable<Integer>{

	ArrayList<Character> board = new ArrayList<Character>();
	Character move;
	String difficulty;
	
	MyCall(ArrayList<Character> game, Character move, String difficulty){
		board = game;
		this.move = move;
		this.difficulty=difficulty;
	}
	
	@Override
	public Integer call() throws Exception {
		// TODO Auto-generated method stub
		boolean bool = true;
		Integer val = 0;
		
		//Rand choice for advanced [If even number, they use smart AI, else
		//they use the dumb AI
		Random randChoice = new Random();
		int choice = randChoice.nextInt(10);
		Boolean hard;
		if(choice%2==0) {
			hard=true;
		} else {
			hard = false;
		}
		
		//Novice [Easy AI]
		if(difficulty == "novice"||(difficulty=="advanced"&&hard==false)) {
			while(bool) {
				Random r = new Random();
				val = r.nextInt(9);
				
				if(board.get(val) == 'x' || board.get(val) == 'o') {
					bool = true;	
				}
				else {
					bool = false;
				}	
			}
		//Expert [Hard AI]
		} else if (difficulty == "expert"||(difficulty=="advanced"&&hard==true)) {
			String[] stringBoard= new String[9];
			//If x is playing
			if(move=='x') {
				for(int i=0; i<9; i++) {
					if(board.get(i)=='-') {
						stringBoard[i]="b";
					}
					if(board.get(i)=='x') {
						stringBoard[i]="X";
					}
					if(board.get(i)=='o') {
						stringBoard[i]="O";
					}
				}			
			//if o is playing	
			} else {
				for(int i=0; i<9; i++) {
					if(board.get(i)=='-') {
						stringBoard[i]="b";
					}
					if(board.get(i)=='x') {
						stringBoard[i]="O";
					}
					if(board.get(i)=='o') {
						stringBoard[i]="X";
					}
				}	
			}
			
			//Running the AI
			MinMax AI = new MinMax(stringBoard);
			ArrayList<Node> movesList = AI.findMoves();
			Boolean check = false;
			
			//Choosing one of the best moves
			for(int i=0; i<movesList.size(); i++) {
				Node temp = movesList.get(i);
				if(temp.getMinMax()==10) {
					val=temp.getMovedTo();
					check=true;
				}
			}
			if(check==false) {
				for(int i=0; i<movesList.size(); i++) {
					Node temp = movesList.get(i);
					if(temp.getMinMax()==0) {
						val=temp.getMovedTo();
						check=true;
					}
				}
			}
			if(check==false) {
				for(int i=0; i<movesList.size(); i++) {
					Node temp = movesList.get(i);
					if(temp.getMinMax()==-10) {
						val=temp.getMovedTo();
						check=true;
					}
				}
			}
			val--;
		}
		
		Thread.sleep(1000);
		System.out.println("\n" + "player: " + move + " chooses index: "+val);
		return val;
	}
	
}