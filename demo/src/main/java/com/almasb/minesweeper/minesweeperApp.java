package com.almasb.minesweeper;


import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JOptionPane;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class minesweeperApp extends Application {
	
	private static final int TILE_ZIZE = 40;
	public   int dificulty=0;
	public   double seconds=121;
	public   double saved_seconds=121;
	public   int supermine=0;
	public   int mines=11;
	public   int count_mines_in_game=0;
	public   int remeaning_tiles;
	public   int bombs_mark=0;
	public   int H = 800;
	public   int W = 800;
	public   int X_TILES = W/TILE_ZIZE;
	public   Label label;
	public   Label label_tiles;
	public   Label label_mark_bombs;
	public   int Y_TILES = H/TILE_ZIZE;
	public   double duration=1;
	public   int history[][]=new int[][]{{-1,-1,-1,-1,-1},
		{-1,-1,-1,-1,-1},
		{-1,-1,-1,-1,-1}};
	public int num=0;
	
	
	public   Tile[][]gridPane=new Tile[X_TILES][Y_TILES];
	
	private Scene scene;
	private VBox vBox;
	public  Timeline time= new Timeline();
	public  KeyFrame frame;
	
//--------------------------------------Timer Count down-------------------------------------------------------------------------------------------------------------------			
	private void doTime() {
		
		time.getKeyFrames().remove(frame);
		  frame= new KeyFrame(Duration.seconds(1), event->{ 
			 seconds= seconds - duration;
			 label.setText("Countdown Timer: "+ (int)seconds + " Seconds");
			 
//--------------------------------------Time's up----------------------------------------------------------------------------------------------------------------------------------------	--			 
			 if(seconds<=0){
		     time.stop();
             
         	Label TimeLabel = new Label("Defeat...Time's up!!!");
         	TimeLabel.setFont(Font.font ("Calibri", 20));
         	TimeLabel.setTextFill(Color.DARKRED);
         	TimeLabel.setLayoutX(15);
         	TimeLabel.setLayoutY(20);
			
			Pane TimeLayout = new Pane();
			TimeLayout.getChildren().add(TimeLabel);
			
			Scene TimeScene = new Scene(TimeLayout, 340, 70);

			Stage newWindow = new Stage();
			newWindow.setTitle("Time's up");
			newWindow.setScene(TimeScene);

			newWindow.show();

     				history[0][4]=history[0][3];
    		    	history[0][3]=history[0][2];
    		    	history[0][2]=history[0][1];
    		    	history[0][1]=history[0][0];
    		    	history[1][4]=history[1][3];
    		    	history[1][3]=history[1][2];
    		    	history[1][2]=history[1][1];
    		    	history[1][1]=history[1][0];
    		    	history[2][4]=history[2][3];
    		    	history[2][3]=history[2][2];
    		    	history[2][2]=history[2][1];
    		    	history[2][1]=history[2][0];
    		    	history[0][0]= 2;
    		    	history[2][0]=(int)seconds;
    		    	history[1][0]= mines;
		    }
		   
//---------------------------------------------------------------------------------------------------------------------------------------	--------------------------------------------------			     
		  });
		  
		  time.setCycleCount(Timeline.INDEFINITE);
		  time.getKeyFrames().add(frame);
		  if(time!=null){
		   time.stop();
		  }
		  time.play();
		 }
	

	private Parent createContext() {
		
		
		
//--------------------------------------Make Tiles--------------------------------------------------------------------------------------------------------------------	----------------------
		if(dificulty==1) {
			H = 640;
			W = 640;
			X_TILES = W/TILE_ZIZE;
			Y_TILES = H/TILE_ZIZE;
		}
		else {
			H = 360;
			W = 360;
			X_TILES = W/TILE_ZIZE;
			Y_TILES = H/TILE_ZIZE;
		}
		
		Pane root=new Pane();
		
//--------------------------------------Top details-------------------------------------------------------------------------------------------------------------------------------	-----------			
		label_tiles=new Label();
		label=new Label();
		label_mark_bombs=new Label();
		root.getChildren().add(label);
		root.getChildren().add(label_tiles);
	  	root.getChildren().add(label_mark_bombs);
	  
		root.setPrefSize(W, H);
		count_mines_in_game=0;
		remeaning_tiles=0;

//--------------------------------------Assign Bombs---------------------------------------------------------------------------------------------------------------------------------	---------		
		PrintWriter writer;
		try {
			File file = new File("mines.txt");
			if(file.exists()) file.delete();
			writer = new PrintWriter("mines.txt");
				 
			
		for(int y=0;y<Y_TILES;y++) {
			for(int x=0; x<X_TILES;x++) {
				boolean flag=false;
				boolean superflag=false;
				remeaning_tiles++;
				if(count_mines_in_game<mines) {
				if(dificulty==0)flag =Math.random()<0.2;
				if(dificulty==1)flag =Math.random()<0.25;
				if(dificulty==1 && count_mines_in_game==20 && flag==true)superflag=true;
				}
				else {superflag=false; 
				flag=false;
				}
				if(flag==true) {
					if(superflag)writer.println(y+", "+x+", 1");
					else writer.println(y+", "+x+", 0");
					count_mines_in_game++;
				}
				Tile tile =new Tile(x,y,flag,superflag);	
				gridPane[x][y]= tile;
				root.getChildren().add(tile);
			}
		}

			 writer.close();
				
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		bombs_mark=0;
		doTime();
		label_tiles.setText("Remaining Tiles: "+ remeaning_tiles);
		label_mark_bombs.setText("Bombs marked: "+ bombs_mark);
		
	
//-------------------------------------Assign number of neighbor bombs --------------------------------------------------------------------------------------------------------------	----------	
		for(int y=0;y<Y_TILES;y++) {
			for(int x=0; x<X_TILES;x++) {
				Tile tile= gridPane[x][y];
				
				if(tile.hasBomb) continue;
			
				long bombs = getNeighbors(tile).stream().filter(t -> t.hasBomb).count();		
				if(bombs >0 )
					tile.text.setText(String.valueOf(bombs));
				}
		}
		
		
//-----------------------------------------Info-------------------------------------------------------------------------------------------------------------------------------------------	-------------		
		
		Text text = new Text();

		if(dificulty==0) {
		   text.setX(200); 
		   text.setY(45);    
		   text.setText("Mines in game= "+Integer.toString(count_mines_in_game));

		}else {
			   text.setX(200); 
			   text.setY(45);    
			   text.setText("Mines in game= "+Integer.toString(count_mines_in_game));
		
		}
		   
		   root.getChildren().add(text);
		  
		  if(dificulty==0) {
			  label.setLayoutY(20);
			  label_tiles.setLayoutX(200);
			  label_tiles.setLayoutY(20);
			  label_mark_bombs.setLayoutY(35);
			}else {
			  label.setLayoutY(20);
			  label_tiles.setLayoutX(200);
			  label_tiles.setLayoutY(20);
			  label_mark_bombs.setLayoutY(35);
			}
	
			
		  Label Info=new Label();
		    Info.setText("Minesweeper");
			root.getChildren().add(Info);
			Info.setFont(Font.font ("Calibri", 20));
			Info.setTextFill(Color.DARKBLUE);
		  
		return root;
		}
	
	
//--------------------------------------Tiles-------------------------------------------------------------------------------------------------------------------------------------	--------	
	private List<Tile> getNeighbors(Tile tile){
		List<Tile> neighbors=new ArrayList<>();
		
		
		int[] points = new int[] {
				-1, -1,
				-1, 0,
				-1, 1,
				0, -1,
				0, 1,
				1, -1,
				1, 0,
				1, 1
		};
		
		for(int i=0;i<points.length;i++) {
			int dx= points[i];
			int dy=points[++i];
			
			int newX = tile.x + dx;
			int newY= tile.y +dy;
			
			if(newX>= 0 && newX< X_TILES &&newY >= 0 && newY<Y_TILES) {
				neighbors.add(gridPane[newX][newY]);
			}
		}
		
		return neighbors;
	}
	
	private class Tile extends StackPane{
		public int x, y;
		public boolean hasBomb;
		public boolean hasSuperBomb;
		public boolean isOpen= false;
		public boolean flag=false;
		public Text markarisma=new Text();
		
		public Rectangle border = new Rectangle(TILE_ZIZE -2, TILE_ZIZE-2);
		public Text text = new Text();
		
		public Tile(int x, int y, boolean hasBomb, boolean hasSuperBomb) {
			this.x=x;
			this.y=y;
			this.hasBomb= hasBomb;
			this.hasSuperBomb= hasSuperBomb;
			
			border.setStroke(Color.LIGHTGRAY);
			
			markarisma.setFont(Font.font(18));
			markarisma.setText(" ");
			markarisma.setVisible(false);
			
			text.setFont(Font.font(18));
			text.setText(hasBomb ? "X" : "");
			text.setVisible(false);
			
			getChildren().addAll(border, text,markarisma);
		
			setTranslateX(x * TILE_ZIZE);
			setTranslateY(y * TILE_ZIZE+55);
			
//---------------------------------Click Tile-------------------------------------------------------------------------------------------------------------------------------------	----------		
			this.setOnMouseClicked(e->
	        {
	            if (e.getButton() == MouseButton.PRIMARY)
	            {num++;
	                open();
	            } else if (e.getButton() == MouseButton.SECONDARY)
	            {
	                mark();
	            }
	        });
		}

		
//--------------------------------Reveal x axis of supermine-------------------------------------------------------------------------------------------------------------------------------------	----------		
		
		public void revealx(Tile x) {
			if(x.isOpen) return;
			if(x.flag) {x.markarisma.setText(x.hasBomb ? "X" : ""); x.flag=false;
			x.markarisma.setVisible(false);
			bombs_mark--;
			num--;
			label_mark_bombs.setText("Bombs marked: "+ bombs_mark);
			return;}
			remeaning_tiles--;
			label_tiles.setText("Remaining Tiles: "+ remeaning_tiles);
			
			x.isOpen=true;
			x.text.setVisible(true);
			x.border.setFill(null);
			if(x.hasBomb)count_mines_in_game--;
			if(remeaning_tiles<=count_mines_in_game) {
				      
                Label WinLabel = new Label("Congratulations!!!"
                		+ "Victory...You made it!!!");
                WinLabel.setFont(Font.font ("Calibri", 20));
                WinLabel.setTextFill(Color.DARKRED);
                WinLabel.setLayoutX(15);
                WinLabel.setLayoutY(20);
    			
    			Pane WinLayout = new Pane();
    			WinLayout.getChildren().add(WinLabel);
    			
    			Scene WinScene = new Scene(WinLayout, 340, 70);

    			Stage newWindow = new Stage();
    			newWindow.setTitle("Victory");
    			newWindow.setScene(WinScene);

    			newWindow.show();
                
                history[0][4]=history[0][3];
		    	history[0][3]=history[0][2];
		    	history[0][2]=history[0][1];
		    	history[0][1]=history[0][0];
		    	history[1][4]=history[1][3];
		    	history[1][3]=history[1][2];
		    	history[1][2]=history[1][1];
		    	history[1][1]=history[1][0];
		    	history[2][4]=history[2][3];
		    	history[2][3]=history[2][2];
		    	history[2][2]=history[2][1];
		    	history[2][1]=history[2][0];
		    	history[0][0]= 1;
		    	history[2][0]=(int)seconds;
		    	history[1][0]= mines;
                time.stop();
                return;
			}
		}
//--------------------------------Reveal y axis of supermine-------------------------------------------------------------------------------------------------------------------------------------	----------		

		public void revealy(Tile y) {
			if(y.isOpen) return;
			if(y.flag) {y.markarisma.setText(y.hasBomb ? "X" : ""); y.flag=false;
			y.markarisma.setVisible(false);
			bombs_mark--;
			num--;
			label_mark_bombs.setText("Bombs marked: "+ bombs_mark);
			return;}
			remeaning_tiles--;
			label_tiles.setText("Remaining Tiles: "+ remeaning_tiles);
			
			y.isOpen=true;
			if(y.hasBomb)count_mines_in_game--;
			y.text.setVisible(true);
			y.border.setFill(null);
			
			if(remeaning_tiles<=count_mines_in_game) {
				      
                Label WinLabel = new Label("Congratulations!!!"
                		+ "Victory...You made it!!!");
                WinLabel.setFont(Font.font ("Calibri", 20));
                WinLabel.setTextFill(Color.DARKRED);
                WinLabel.setLayoutX(15);
                WinLabel.setLayoutY(20);
    			
    			Pane WinLayout = new Pane();
    			WinLayout.getChildren().add(WinLabel);
    			
    			Scene WinScene = new Scene(WinLayout, 340, 70);

    			Stage newWindow = new Stage();
    			newWindow.setTitle("Victory");
    			newWindow.setScene(WinScene);

    			newWindow.show();
                
                history[0][4]=history[0][3];
		    	history[0][3]=history[0][2];
		    	history[0][2]=history[0][1];
		    	history[0][1]=history[0][0];
		    	history[1][4]=history[1][3];
		    	history[1][3]=history[1][2];
		    	history[1][2]=history[1][1];
		    	history[1][1]=history[1][0];
		    	history[2][4]=history[2][3];
		    	history[2][3]=history[2][2];
		    	history[2][2]=history[2][1];
		    	history[2][1]=history[2][0];
		    	history[0][0]= 1;
		    	history[2][0]=(int)seconds;
		    	history[1][0]= mines;
                time.stop();
                return;
			}
			
		}
//---------------------------------Mark Tile as Bomb----------------------------------------------------------------------------------------------------------------------------	-------------		
		public void mark() {
			if(bombs_mark>=mines)return;
			if(isOpen) return;
		/*	if(flag) {markarisma.setText(hasBomb ? "X" : ""); flag=false;
			markarisma.setVisible(false);
			bombs_mark--;
			label_mark_bombs.setText("Bombs marked: "+ bombs_mark);
			return;}
			
			*/
			

			if(num<=4 && hasSuperBomb) {
				
				System.out.println("Yup...you found the super Bomb");
				
				{
					if(isOpen) return;
					if(flag) {markarisma.setText(hasBomb ? "X" : ""); flag=false;
					markarisma.setVisible(false);
					bombs_mark--;
					num--;
					label_mark_bombs.setText("Bombs marked: "+ bombs_mark);
					return;}
					remeaning_tiles--;
					bombs_mark--;
					label_mark_bombs.setText("Bombs marked: "+ bombs_mark);
					label_tiles.setText("Remaining Tiles: "+ remeaning_tiles);
					
					isOpen=true;
					text.setVisible(true);
					border.setFill(null);
					count_mines_in_game--;
					if(remeaning_tiles<=count_mines_in_game) {
						      
		                Label WinLabel = new Label("Congratulations!!!"
		                		+ "Victory...You made it!!!");
		                WinLabel.setFont(Font.font ("Calibri", 20));
		                WinLabel.setTextFill(Color.DARKRED);
		                WinLabel.setLayoutX(15);
		                WinLabel.setLayoutY(20);
		    			
		    			Pane WinLayout = new Pane();
		    			WinLayout.getChildren().add(WinLabel);
		    			
		    			Scene WinScene = new Scene(WinLayout, 340, 70);

		    			Stage newWindow = new Stage();
		    			newWindow.setTitle("Victory");
		    			newWindow.setScene(WinScene);

		    			newWindow.show();
		                
		                history[0][4]=history[0][3];
				    	history[0][3]=history[0][2];
				    	history[0][2]=history[0][1];
				    	history[0][1]=history[0][0];
				    	history[1][4]=history[1][3];
				    	history[1][3]=history[1][2];
				    	history[1][2]=history[1][1];
				    	history[1][1]=history[1][0];
				    	history[2][4]=history[2][3];
				    	history[2][3]=history[2][2];
				    	history[2][2]=history[2][1];
				    	history[2][1]=history[2][0];
				    	history[0][0]= 1;
				    	history[2][0]=(int)seconds;
				    	history[1][0]= mines;
		                time.stop();
		                return;
					}
					
						for(int i=0; i<16;i++) {
							revealx(gridPane[x][i]);
							revealy(gridPane[i][y]);
						}
					
				}
				
				
			}
					
			
			bombs_mark++;
			label_mark_bombs.setText("Bombs marked: "+ bombs_mark);
			markarisma.setText("X");
			flag=true;
			markarisma.setVisible(true);
			markarisma.setStroke(Color.RED);
			
			}
		
//--------------------------------------Open Tile----------------------------------------------------------------------------------------------------------------------------------	----------		
		public void open() {
			if(isOpen) return;
		/*	if(flag){markarisma.setText(hasBomb ? "X" : ""); flag=true;
			markarisma.setVisible(false);
			bombs_mark--;
			label_mark_bombs.setText("Bombs marked: "+ bombs_mark);
			}
			*/
			if(flag) {markarisma.setText(hasBomb ? "X" : ""); flag=false;
			markarisma.setVisible(false);
			bombs_mark--;
			num--;
			label_mark_bombs.setText("Bombs marked: "+ bombs_mark);
			return;}
			
			
			if(hasBomb) {
                time.stop();
//--------------------------------------Game over stepped on bomb---------------------------------------------------------------------------------------------------------------------	---------------------	
                history[0][4]=history[0][3];
		    	history[0][3]=history[0][2];
		    	history[0][2]=history[0][1];
		    	history[0][1]=history[0][0];
		    	history[1][4]=history[1][3];
		    	history[1][3]=history[1][2];
		    	history[1][2]=history[1][1];
		    	history[1][1]=history[1][0];
		    	history[2][4]=history[2][3];
		    	history[2][3]=history[2][2];
		    	history[2][2]=history[2][1];
		    	history[2][1]=history[2][0];
		    	history[0][0]= 0;
		    	history[2][0]=(int)seconds;
		    	history[1][0]= mines;
		    	
		    	Label GameOverLabel = new Label("Game Over...You stepped on a mine!!!");
		    	GameOverLabel.setFont(Font.font ("Calibri", 20));
		    	GameOverLabel.setTextFill(Color.DARKRED);
		    	GameOverLabel.setLayoutX(15);
		    	GameOverLabel.setLayoutY(20);
				
				Pane GameOverLayout = new Pane();
				GameOverLayout.getChildren().add(GameOverLabel);
				
				Scene GameOverScene = new Scene(GameOverLayout, 340, 70);

				Stage newWindow = new Stage();
				newWindow.setTitle("Game Over");
				newWindow.setScene(GameOverScene);

				newWindow.show();
				
				return;
			}
//--------------------------------------No bomb on tile--------------------------------------------------------------------------------------------------------	----------------------------------				
			remeaning_tiles--;
			label_tiles.setText("Remaining Tiles: "+ remeaning_tiles);
			
			isOpen=true;
			text.setVisible(true);
			border.setFill(null);
			
			if(remeaning_tiles<=count_mines_in_game) {
				      
                Label WinLabel = new Label("Congratulations!!!"
                		+ "Victory...You made it!!!");
                WinLabel.setFont(Font.font ("Calibri", 20));
                WinLabel.setTextFill(Color.DARKRED);
                WinLabel.setLayoutX(15);
                WinLabel.setLayoutY(20);
    			
    			Pane WinLayout = new Pane();
    			WinLayout.getChildren().add(WinLabel);
    			
    			Scene WinScene = new Scene(WinLayout, 340, 70);

    			Stage newWindow = new Stage();
    			newWindow.setTitle("Victory");
    			newWindow.setScene(WinScene);

    			newWindow.show();
                
                history[0][4]=history[0][3];
		    	history[0][3]=history[0][2];
		    	history[0][2]=history[0][1];
		    	history[0][1]=history[0][0];
		    	history[1][4]=history[1][3];
		    	history[1][3]=history[1][2];
		    	history[1][2]=history[1][1];
		    	history[1][1]=history[1][0];
		    	history[2][4]=history[2][3];
		    	history[2][3]=history[2][2];
		    	history[2][2]=history[2][1];
		    	history[2][1]=history[2][0];
		    	history[0][0]= 1;
		    	history[2][0]=(int)seconds;
		    	history[1][0]= mines;
                time.stop();
                return;
			}
			if(text.getText().isEmpty()) {
				getNeighbors(this).forEach(Tile::open);
			}
			
		}
	}
	
	
	
	
	
	
	@Override
	public void start(@SuppressWarnings("exports") Stage stage) throws Exception {

//---------------------------------------------Menu----------------------------------------------------------------------------------------------------------------------------------------	-------		
	   Menu m1 = new Menu("Application");

       MenuBar mb = new MenuBar();

       javafx.scene.control.MenuItem StartItem = new javafx.scene.control.MenuItem("Start");
       m1.getItems().add(StartItem);
       javafx.scene.control.MenuItem LoadItem = new javafx.scene.control.MenuItem("Load");
       m1.getItems().add(LoadItem);
       javafx.scene.control.MenuItem CreateItem = new javafx.scene.control.MenuItem("Create");
       m1.getItems().add(CreateItem);
       javafx.scene.control.MenuItem ExitItem = new javafx.scene.control.MenuItem("Exit");
       m1.getItems().add(ExitItem);
       
       
       Menu m2 = new Menu("Details");

       javafx.scene.control.MenuItem RoundsItem = new javafx.scene.control.MenuItem("Rounds");
       m2.getItems().add(RoundsItem);
       javafx.scene.control.MenuItem SolutionItem = new javafx.scene.control.MenuItem("Solution");
       m2.getItems().add(SolutionItem);


//---------------------------------------------Exit-----------------------------------------------------------------------------------------------------------------------------------------	------
       ExitItem.setOnAction(new EventHandler <ActionEvent>() {
           public void handle(ActionEvent event) {
        	   System.out.println("Exit");
        	   System.out.println("Exiting...");
        	  System.exit(0);
           }
       });
       
//---------------------------------Start with Scenario-Id----------------------------------------------------------------------------------------------------------------------------------	-------
       StartItem.setOnAction(new EventHandler <ActionEvent>() {
           public void handle(ActionEvent event) {
        	   duration=1;
        	try {
        		seconds=saved_seconds;
        		num=0;
        		System.out.println("Starting...");
				start(stage);
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
  
           }
       });
       
//--------------------------------------Exceptions--------------------------------------------------------------------------------------------------------------------------------------	----
       class InvalidValueException extends Exception { 
    	    public InvalidValueException(String errorMessage) {
    	        super(errorMessage);
    	    }
    	}
       class InvalidDescriptionException extends Exception { 
   	    public InvalidDescriptionException(String errorMessage) {
   	        super(errorMessage);
   	    }
   	}
//--------------------------------------Load Scenario-Id--------------------------------------------------------------------------------------------------------------------------------------	----
       LoadItem.setOnAction(new EventHandler <ActionEvent>() {
           public void handle (ActionEvent event)  {
        	  
        	   
        	   File file = new File("SCENARIO-ID.txt");
				if(file.exists()) {
					
					try (BufferedReader reader = new BufferedReader(new FileReader("SCENARIO-ID.txt"))) {
	
						String Int_line;
						System.out.println("loading...");
						int count=0;

						while ((Int_line = reader.readLine()) != null) {
						    int In_Value = Integer.parseInt(Int_line);
						    System.out.println(Int_line);
						    if(count==0) {
						    	if(In_Value!=1 && In_Value!=0)
						    		throw new InvalidValueException("Invalid value for difficulty");
						    	dificulty=In_Value;
						    }
						    if(count==1) {
						    	if(dificulty==0 && (In_Value>11 || In_Value<9))	throw new InvalidValueException("Invalid value for mines");				    	
						    	if(dificulty==1 && (In_Value>45 || In_Value<35)) throw new InvalidValueException("Invalid value for mines");
						    	mines=In_Value;
						    }
						    if(count==2) {
						    	if(dificulty==1 && In_Value==0)	throw new InvalidValueException("Invalid value for supermine");			    	
						    	if(dificulty==0 && In_Value==1) throw new InvalidValueException("Invalid value for supermine");
						    	supermine=In_Value;
						    }
						    if(count==3) {
						    	if(dificulty==0 && (In_Value>(double)180 || In_Value<(double)120))	throw new InvalidValueException("Invalid value for time");			    	
						    	if(dificulty==1 && (In_Value>(double)360 || In_Value<(double)240)) throw new InvalidValueException("Invalid value for time");
						    	saved_seconds=(double)In_Value;
						    	seconds=(double)In_Value;

						    }
						    count++;
						   
							}
						 if(count<=3) {
						    	throw new InvalidDescriptionException("Wrong Description");	
						}
						
					} catch (NumberFormatException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (InvalidValueException g) {
						// TODO Auto-generated catch block
						System.out.println(g);
					} catch (InvalidDescriptionException f) {
						// TODO Auto-generated catch block
						System.out.println(f);

					}
					
					 time.stop();
        	 
				}
			} 
       });
       
       
//---------------------------------Create Scenario-Id-------------------------------------------------------------------------------------------------------------------------------------	----
       CreateItem.setOnAction(new EventHandler <ActionEvent>() {
           public void handle(ActionEvent event) {
				 int name1 = Integer.parseInt(JOptionPane.showInputDialog("Δυσκολία;"));
				 int name2 = Integer.parseInt(JOptionPane.showInputDialog("Πόσες νάρκες;"));
				 int name3 = Integer.parseInt(JOptionPane.showInputDialog("Πόσες Υπερνάρκες;"));
				 int name4 = Integer.parseInt(JOptionPane.showInputDialog("Πόσο χρόνο;"));
				 PrintWriter writer;
				try {
					File file = new File("SCENARIO-ID.txt");
					if(file.exists()) file.delete();
					writer = new PrintWriter("SCENARIO-ID.txt");
					
					 writer.println(name1);
					 writer.println(name2);
					 writer.println(name3);
					 writer.println(name4);
					 writer.close();
					 System.out.println("Creating...");
						
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 time.stop();
        
           }
       });
//---------------------------------Rounds-------------------------------------------------------------------------------------------------------------------------------------	----
       RoundsItem.setOnAction(new EventHandler <ActionEvent>() {
           public void handle(ActionEvent event) {
        	   System.out.println("Rounds...");
        	   
        	   Pane RoundsLayout = new Pane();
          	    Label sol = new Label("History log");
   		    	sol.setFont(Font.font ("Calibri", 20));
   		    	sol.setTextFill(Color.DARKBLUE);

   		    	RoundsLayout.getChildren().add(sol);

   				Scene secondScene = new Scene(RoundsLayout, W, 100);
   				
   				for(int i=0;i<5;i++) {
					Label log=new Label();
					if(history[0][i]==-1) log.setText("No log");
					if(history[0][i]==1) log.setText("Victory - "+ "Mines: "+history[1][i] + " - Seconds remeaning: "+history[2][i]);
					if(history[0][i]==0) log.setText("Defeat...stepped on a mine! - "+ "Mines: "+history[1][i] + " - Seconds remeaning: "+history[2][i]);
					if(history[0][i]==2) log.setText("Defeat...time's up! - "+ "Mines: "+history[1][i] + " - Seconds remeaning: "+history[2][i]);
					if(dificulty==0) log.setLayoutY(i*15+20);
					if(dificulty==1) log.setLayoutY(i*15+20);
					RoundsLayout.getChildren().add(log);
   				}
   				
  
   				Stage newWindow = new Stage();
   				newWindow.setTitle("Rounds");
   				newWindow.setScene(secondScene);

   				newWindow.show();
           }
       });
       
       
//---------------------------------Solution-------------------------------------------------------------------------------------------------------------------------------------	----
       SolutionItem.setOnAction(new EventHandler <ActionEvent>() {
           public void handle(ActionEvent event) {
        	   System.out.println("Solution...");
        	   
        	   Pane SolutionsLayout = new Pane();
       	    Label sol = new Label("Solutions");
		    	sol.setFont(Font.font ("Calibri", 20));
		    	sol.setTextFill(Color.DARKBLUE);

		    	SolutionsLayout.getChildren().add(sol);

				Scene secondScene = new Scene(SolutionsLayout, W-100, H+55);
				
				
				try (BufferedReader reader = new BufferedReader(new FileReader(new File("mines.txt")))) {

					
			        String line;
			        int measure=20;
			        while ((line = reader.readLine()) != null) {
			        	Label solution=new Label();
			        	solution.setText(line);
			        	solution.setLayoutY(measure);
			        	measure=measure+15;
			        	SolutionsLayout.getChildren().add(solution);

			        }
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
				Stage newWindow = new Stage();
				newWindow.setTitle("Solution");
				newWindow.setScene(secondScene);

				newWindow.show();
           }
       });       
       
       
       
       
        mb.getMenus().addAll(m1);
        mb.getMenus().addAll(m2);
       
		VBox vBox=new VBox(mb, createContext());
		Scene scene = new Scene(vBox, W, H+80);
		stage.setTitle("MediaLab Minesweeper");
		
		stage.setScene(scene);
	

		stage.show();

	}

	public static void main(String[] args) {
		
	launch(args);

	}

}