//Sajan Flora and Aser Herman
//This is a game called arkanoid
//in this game there is a ball that you use to break blocks by bouncing the ball off a pad
//the goal is to destroy all the blocks, without losing all your lives (when the ball falls off the screen)
// this game includes powerups such as enlarge (bigger pad) catch (ball sticks to pad until redirected by pressing enter) break (brings you to the next level) and slow(slows down the speed of the ball)
//this game also includes 2 levels, a scoring system and 3 lives

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class arkanoid extends JFrame implements ActionListener{
	Timer myTimer;
	Board board;
	Ball ball;

    public arkanoid() {
		super("Arkanoid");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(845,800);
		myTimer = new Timer(10, this);
		board = new Board(this);
		add(board);

		setResizable(false);
		setVisible(true);
    }

	public void start(){
		myTimer.start();
	}

	public void actionPerformed(ActionEvent evt){
		boolean ifwait=board.getWait();
		if(ifwait==false) {
			ball.move();
		}
		else {
			ball.ballFollow();
		}
		board.collision();
		board.lifeline();
		board.move();
		board.repaint();
		board.getCoords();
	}

    public static void main(String[] args) {
		arkanoid frame = new arkanoid();
    }

}

class Board extends JPanel implements KeyListener{
	private int boardx,boardy,hitx,hity,px,py,width=75;
	private static int ballx,bally,lifeline;//lifeline is number of lives
	private boolean []keys;
	private boolean catcher,lvl2;//catcher for cath power up
	private static String fallPower;
	private static boolean wait;//used when life is lost or new level enterd to prevent ball from movng right away
	private static String power="none";
	private int[]blocksX,blocksY;//list of block coordinates
	private arkanoid mainFrame;
	private Image back,pad,pad2,heart,gameOver;//images
	Ball ball;
	Blocks blocks;
	PowerUps powerUps;

	public Board(arkanoid m){
		keys = new boolean[KeyEvent.KEY_LAST+1];
	    boardx = 420;//sets boardx
        boardy = 700;//sets boardy
        heart = new ImageIcon("heart.png").getImage();//images
        gameOver = new ImageIcon("GameOver.png").getImage();
        back = new ImageIcon("ArkBack.png").getImage();
        pad = new ImageIcon("Vaus_icon.png").getImage();
        back= back.getScaledInstance(845,800,Image.SCALE_SMOOTH);
        pad= pad.getScaledInstance(75,15,Image.SCALE_SMOOTH);
        pad2= pad.getScaledInstance(125,15,Image.SCALE_SMOOTH);
        addKeyListener(this);
        setSize(845,800);
        mainFrame=m;
        wait=false;
        lifeline=3;//lives
        ball=new Ball();
		blocks=new Blocks();
		powerUps=new PowerUps();
	}

	public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }

    public boolean getWait() {//used when life is lost or new level enterd to prevent ball from movng right away
    	return wait;
    }

	public void move(){//move board
		fallPower=powerUps.getFallPower();//getting fallpower
		ballx=ball.getBallx();//gets ballx
		bally=ball.getBally();//gets bally
		if (catcher==true){//used when ball isnt on booard and power is catch, used as place holder
			power="catch";
		}
		if (power=="enlarge"){
			width=125;//increasing size
		}
		if(keys[KeyEvent.VK_RIGHT] &&boardx<=755 ){
			boardx += 10;//moves boardx
			if (power=="catch"){// if power is catch and ball is touching board
				if (bally<=boardy+15 && bally+15>=boardy){
					if(ballx-boardx<=75 && ballx-boardx>=0){
						ballx += 10;//ballx movs with boardx
					}
				}
			}
		}
		if(keys[KeyEvent.VK_LEFT] && boardx>=15 ){
			boardx -= 10;//moves boardx
			if (power=="catch"){// if power is catch and ball is touching board
				if (bally<=boardy+15 && bally+15>=boardy){//if ball is touching board
					if(ballx-boardx<=75 && ballx-boardx>=0){
						ballx -= 10;//ballx movs with boardx
					}
				}
			}
		}
		if(power=="catch"){//when you want to relase the ball
			if(keys[KeyEvent.VK_ENTER] ){
				catcher=true;//place holder for catch
				power="none";//power is none when in the air
			}
		}
		ball.setPower(power);//seting new info
		blocks.setPower(power);
		ball.setBall(ballx,bally);
		ball.setBoard(boardx,boardy);
	}

	public void getCoords(){//gets ball coordinates
		ballx=ball.getBallx();
		bally=ball.getBally();
	}

	public void lifeline() {
		if(bally>800) {//if ball falls off the screen
			lifeline-=1;//life subtracted
			power="none";//gets rid of power
			ball.setBall(boardx+35,boardy);//ball is reset to board
			blocks.setPower(power);
			wait=true;//used to stop ball from moving until unleashed by the user
		}
	}

	public void collision(){
		px=powerUps.getPx();//gets powerups x coord
		py=powerUps.getPy();//gets power up y coord
		if(wait==false) {//if you dont have to wait
			if(py>800){//if power up is off screen
				fallPower="none";//resetting power info to none
				powerUps.setFallPower(fallPower);
				ball.setFallPower(fallPower);
			}
			if (power=="break"){
				if (boardx+75>=830){//if board goes through opening to next level
					lvl2=true;
					power="none";//resets power
					lvl2=false;
					wait=true;//used to stop ball movement
					ball.setPower(power);//sets new info
					ball.setBall(boardx+35,boardy);
					blocks.setPower(power);
					blocks.levelOver();//goes to next level
				}
			}
			if (bally+15>=boardy && bally<=boardy+15){//if board is touching ball
				if(ballx-boardx<=width && ballx-boardx>=0){
					if(power!="catch"){//if power is catch ball is on board
						hitx=ballx;//setting last place it hit
						hity=bally;
						ball.angle(hitx,hity);
					}
				}
			}
			if(fallPower!="none"){//if there is a power up being displayed
				if(py+25>=boardy && py<=boardy){//if power up is touching the board
					if(px+width>=boardx && px<=boardx+width){
						catcher=false;//catcher used as place holder for catch is reset
						power=fallPower;//power is now the powerup that was falling
						ball.setPower(power);//setting power
						powerUps.setPower(power);
						fallPower="none";//reseting fallpower
						ball.setFallPower(fallPower);
					}
				}
			}
		}
		if(keys[KeyEvent.VK_ENTER] ){//if enter is clicked, ball is released
			wait=false;
		}
	}

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }

    public void paintComponent(Graphics g){
    	g.drawImage(back,0,0,null);
    	if (power=="enlarge"){
			g.drawImage(pad2,boardx,boardy,null);//pad2 is a larger image
		}
		else{
			g.drawImage(pad,boardx,boardy,null);//if power isnt enlarge
		}
		if(lifeline==3) {//drawing all the hearts that represent your lives
			g.drawImage(heart,600,32,null);
			g.drawImage(heart,650,32,null);
			g.drawImage(heart,700,32,null);
		}
		if(lifeline==2) {
			g.drawImage(heart,600,32,null);
			g.drawImage(heart,650,32,null);
		}
		if(lifeline==1) {
			g.drawImage(heart,600,32,null);
		}
		if(lifeline==0) {
			g.drawImage(gameOver,0,0,null);
			System.exit(0);//end when you have 0 lives
		}
    	ball.draw(g);
    	blocks.draw(g);
    	powerUps.draw(g);
    	g.setColor(Color.white);
    	g.drawString("Lifeline:",470,50);
  }
}

class Ball extends JPanel{
	private static int[]blocksX,blocksY;//x and y coords for the blocks
	private static int ballx,bally,hitx,hity,hitxx,hityy,hor,vert,j,vel,boardx,boardy,lvl;//j is a specific block that was hit, hitx/y last place hit, hitxx/yy used to make sure hitx/y r the same
	private static double lenx,leny,dist,xcomp,ycomp,xpos,ypos;//lenx/y diffrence in xand y values between last placed hit and current pos of ball, x/ycomp what s being added to ballx/y
	private static double ang1=25.906507999514385;//starting angle
	private static String power;//power
	private static double ang2=90-ang1;//starting 2nd angle
	private static String fallPower="none";//what powerup is falling
	private static boolean collision;//if a collison with the blocks occured
	private static Blocks blocks;
	private static PowerUps powerUps;
	private static Score score;

	public Ball(){
		ballx=100;//setting starting ball coords
		bally=350;
		hor=1;//direction horizontally
		vert=1;//direction vertically
		vel=6;//velocity
		blocks=new Blocks();
		powerUps=new PowerUps();
		score=new Score();
	}

	public static void angle(int xx,int yy){
		lenx=Math.abs(ballx-hitx);//absolue value of diffence between current ballx and previous spot hit
		leny=Math.abs(bally-hity);//absolue value of diffence between current bally and previous spot hit
		dist=Math.sqrt(lenx*lenx+leny*leny);//distance travelled
		ang1=Math.asin((lenx*Math.sin(90*Math.PI*2/360))/dist)*180/Math.PI;//calculating ang1, had to convert to degrees
		if(ang1>75){//if an angle is too steep it makes it appear slower and slows down the game
			ang1=70;
			ang2=20;
		}
		if(ang2>75){
			ang2=70;
			ang1=20;
		}
		ang2=90-ang1;
		hitx=xx;//sets new hitx
		hity=yy;//sets new hity
		if (bally-700<=15 && bally-700>=0){
			vert=-1;//changes vertical direction
		}
		if(bally<=0){
			vert=1;//changes vertical direction
		}
		move();
	}
	public static void ballFollow() {//used after life is lost of new level is enterd
		ballx=boardx+35;//ball is at the centre of the board
		bally=boardy-15;
		vert=-1;//makes sure ball is always going up in this posotion
	}

	public static void move(){
		if (power=="slow"){//if power is slow
			vel=3;//slows down the velocity
		}
		else{
			vel=6;//if the power isnt slow, vleocity is normal
		}
		if (bally-boardy<=15 && bally-boardy>=0 && ballx-boardx<=75 && ballx-boardx>=0 && power=="catch"){

		}
		else{
			blocksX=blocks.getBlocksX();//gets blocks
			blocksY=blocks.getBlocksY();
			xcomp=Math.abs((vel*Math.sin(ang1*Math.PI*2/360))/(Math.sin(90*Math.PI*2/360)))*hor;//finds x comp, had to convert to degrees
			ycomp=Math.abs((vel*Math.sin(ang2*Math.PI*2/360))/(Math.sin(90*Math.PI*2/360)))*vert;//finds y comp,had to conver to degrees
			ballx+=xcomp;//adding x.ycomps
			bally+=ycomp;
			if(ballx>=830) {//right wall collison
				ballx=830;
				hor=-1;//changing vertical direction
				angle(845,bally);//getting new angle
			}
			else if(ballx<=0) {//left wall collison
				ballx=0;
				hor=1;//changes horizontal direction
				angle(0,bally);//gets new angle
			}
			else if(bally<=0) {
				bally=0;
				angle(ballx,0);
			}
			if (vert==-1){//if ball moving up checks balls closest to it incase multiple blocks are hit at once
				for(int i=0;i<blocksX.length;i++) {
					if(ballx>=blocksX[i]-15 && ballx<=blocksX[i]+75){
						if(bally>=blocksY[i]-15 && bally<=blocksY[i]+25) {//if ball is possibly touching a block
							j=i;//j is the specific block
							if(blocksX[j]!=0){//if block hasnt already been hit
								collide();
							}
						}
					}
				}
			}
			if (vert==1){//if ball moving down checks balls closest to it incase multiple blocks are hit at once
				for(int i=0;i<blocksX.length;i++) {
					if(ballx>=blocksX[blocksX.length-1-i]-15 && ballx<=blocksX[blocksX.length-1-i]+75){//if ball is possibly touching a block
						if(bally>=blocksY[blocksX.length-1-i]-15 && bally<=blocksY[blocksX.length-1-i]+25) {
							j=blocksX.length-1-i;//j is specific block
							if(blocksX[j]!=0){//if block hasnt already been hit
								collide();
							}
						}
					}
				}
			}
		}
	}

	public static void collide(){
	    if(blocksX[j]!=0 && blocksY[j]!=0) {//if ball hasnt already been hit
	    //collision on the botton of the bricks
	    	if(ballx+15>=blocksX[j] && ballx<=blocksX[j]+75 && vert==-1 && bally>=blocksY[j]+19 && bally<=blocksY[j]+25) {//if ball hit the block
	    		if (ballx>blocksX[j]+69 && ballx<=blocksX[j]+75 || ballx+15>=blocksX[j] && ballx+15<blocksX[j]+6){//bottom right and left corner
	    		//if ball is in the corner, it could have come from 2 possible spots
	    			ypos=Math.round(Math.abs(ycomp+bally));//find ypos right before collison
	    			for (int i=0; i<Math.abs(ycomp); i++){
	    				if (ypos<=blocksY[j]+24 && ypos>=blocksY[j]){//checking every single spot from ypos before collison to ypos after collison
	    				//if no colliosion occured then it didnt hit bottom
	    					vert=1;//change vertical direction
	    					collision=true;
	    				}
	    				ypos-=1;//decreasing ypos
	    			}
	    		}
	    		else if (ballx+6>blocksX[j]-15 && ballx<blocksX[j]+69){//if it wasnt in the corner
		    		vert=1;//changing vertical direction
		    		collision=true;
	    		}
	         }
	         if(ballx+15>=blocksX[j] && ballx<=blocksX[j]+75 && vert==1 && bally+15>=blocksY[j] && bally+15<=blocksY[j]+6) { //collision when hitting top of block
	           	if (ballx>blocksX[j]+69 && ballx<=blocksX[j]+75 || ballx+15>=blocksX[j] && ballx+15<=blocksX[j]+6){//top right and left corner
	           	//if ball is in the corner, it could have come from 2 possible spots
	    			ypos=Math.abs(ycomp-bally)+15;//find ypos right before collison
	    			for (int i=0; i<Math.abs(ycomp)+1; i++){
	    				if (Math.round(ypos)<=blocksY[j]+1 && Math.round(ypos)>=blocksY[j]){//checking every single spot from ypos before collison to ypos after collison
	    				//if no colliosion occured then it didnt hit top
	    					vert=-1;//change vertical direction
	    					collision=true;
	    				}
	    				ypos+=1;//increase y pos by 1 to check for colliosn
	    			}
	    		}
	    		else if (ballx+15>blocksX[j]+6 && ballx<=blocksX[j]+69){//if colliosn didnt occur in the corner
		    		vert=-1;//change in vertical direction
		    		collision=true;
	    		}
	         }

	         //collision block on the right side
	         if(ballx<=blocksX[j]+75 && ballx>blocksX[j]+69 && bally<=blocksY[j]+25 && bally+6>=blocksY[j] && hor==-1) {//colliosn on right side of brick
		            hor=1;//change in horizontal direction
		            collision=true;
	         }

	         //collision left side of the brick
	         if(ballx+15>=blocksX[j] && ballx+15<=blocksX[j]+6 && bally<=blocksY[j]+25 && bally>=blocksY[j]-15 && hor==1) {
	            hor=-1;//change in horizontal direction
	            collision=true;
	         }
	    }
	    if(collision==true){//if collison occured
	    	String returnedColour=blocks.returnColour(blocksX[j],blocksY[j]);//finds colour
			score.findScore(returnedColour);//uss above string to set score
	    	lvl=blocks.getLevel();//gets level
	    	if (fallPower=="none"){//if no poerup on screen
	    		if(lvl==1){//for level 1
	    			for (int i=0; i<88; i++){//goes through all blocks
	  					if (blocksX[i]==blocksX[j] && blocksY[i]==blocksY[j]){//finds collided block
	  						if (i<55){//if i<55 then it is not grey, which means it only needs to be hit once
	  							fallPower=powerUps.powerType();//gets powerup
								powerUps.setDel(blocksX[j],blocksY[j]);//sets powerup coords
	  						}
	  						else{//if i>55 then it is grey, which means it need to be hit 3 times to be destroyed
		  						if (i>=55 && i<66){//checks the first layer of grey block
		  							if (blocksX[i+11]==0 && blocksX[i+22]==0){//if other layers were hit
		  								fallPower=powerUps.powerType();//gets powerup
										powerUps.setDel(blocksX[j],blocksY[j]);//sets powerup
		  							}
		  						}
		  						if (i>=66 && i<77){//checks second layer
		  							if (blocksX[i]==0 && blocksX[i+11]==0){//checks if other layers were hit
		  								fallPower=powerUps.powerType();//gets powerup
										powerUps.setDel(blocksX[j],blocksY[j]);//setspowerup
		  							}
		  						}
		  						if (i<=87 && i>77){//checks 3rd layer
		  							if (blocksX[i-22]==0 && blocksX[i-11]==0){//cheks if other layers were hit
		  								fallPower=powerUps.powerType();
										powerUps.setDel(blocksX[j],blocksY[j]);
		  							}
		  						}
		  					}
	  					}
	  				}
	    		}
	    		else if (lvl==2){//level 2
	    			for (int i=0; i<86; i++){//checsk all blocks
	    				if (blocksX[i]==blocksX[j] && blocksY[i]==blocksY[j]){//finds collided block
		  					if (blocksX[i]!=760 && blocksY[i]==325){//if grey
		  						if (i>=0 && i<10){//checks first layer
		  							if (blocksX[i+10]==0 && blocksX[i+20]==0){//chcks if other leayrs were hit
		  								fallPower=powerUps.powerType();
										powerUps.setDel(blocksX[j],blocksY[j]);
		  							}
		  						}
		  						if (i>=10 && i<20){//checks second layer
		  							if (blocksX[i]==0 && blocksX[i+10]==0){//checks if other layer was hit
		  								fallPower=powerUps.powerType();
										powerUps.setDel(blocksX[j],blocksY[j]);
		  							}
		  						}
		  						if (i<=30 && i>20){//checks 3rd layer
		  							if (blocksX[i-20]==0 && blocksX[i-10]==0){//checks if other layers were hit
		  								fallPower=powerUps.powerType();
										powerUps.setDel(blocksX[j],blocksY[j]);
		  							}
		  						}
		  					}
		  					else{//if block is nnot grey, only requires one hit
		  						fallPower=powerUps.powerType();//gets powerup
								powerUps.setDel(blocksX[j],blocksY[j]);//sets powerup coords
		  					}
	    				}
	  				}
	    		}
			}
	        blocksX[j]=0; //removes block from created list inside move method
	       	blocksY[j]=0;
	       	collision=false;
	        angle(ballx,bally); //angle method is called to find the new angle
	    }
	    else{
	  		move(); //move method is called
	    }
	}

	public static int getBallx(){ //method used to get the ball x coordinate from other classes
		return ballx;
	}

	public static int getBally(){ //method used to get the ball y coordinate from other classes
		return bally;
	}
	public static void setPower(String n){ //method used to set the power
		power=n;
	}
public static void setFallPower(String n){ //set the fallPower from the other methods
		fallPower=n;
	}

	public void setBall(int x,int y){ //method that is used in other classes to set the ball coordinates
		ballx=x;
		bally=y;

	}
	public void setBoard(int x,int y){ //sets the board coordinates (pad or vaus coordinates)
		boardx=x;
		boardy=y;
	}


	public void draw(Graphics g){
		g.setColor(Color.white);
		g.fillOval(ballx,bally,15,15);
		score.text(g);
		//blocks2.draw(g);
	}
}

class Blocks extends JPanel {

  private static int[]blocksX={10,85,160,235,310,385,460,535,610,685,760, // x coordinates of all the blocks from level 1
  						10,85,160,235,310,385,460,535,610,685,760,
  						10,85,160,235,310,385,460,535,610,685,760,
  						10,85,160,235,310,385,460,535,610,685,760,
  						10,85,160,235,310,385,460,535,610,685,760,

  						10,85,160,235,310,385,460,535,610,685,760, //the gray blocks are blitted 3 times because they have a 3 hit hitbox
  						10,85,160,235,310,385,460,535,610,685,760,
  						10,85,160,235,310,385,460,535,610,685,760};
  private static int[]blocksY={200,200,200,200,200,200,200,200,200,200,200, // y coordinates of all the blocks from level 1
  						175,175,175,175,175,175,175,175,175,175,175,
  						150,150,150,150,150,150,150,150,150,150,150,
  						125,125,125,125,125,125,125,125,125,125,125,
  						100,100,100,100,100,100,100,100,100,100,100,

  						75,75,75,75,75,75,75,75,75,75,75,//the gray blocks are blitted 3 times because they have a 3 hit hitbox
  						75,75,75,75,75,75,75,75,75,75,75,
  						75,75,75,75,75,75,75,75,75,75,75};

  private static int[]blocksX2={10,85,160,235,310,385,460,535,610,685,// x coordinates of all the blocks from level 2
  							10,85,160,235,310,385,460,535,610,685,//the gray blocks are blitted 3 times because they have a 3 hit hitbox
  							10,85,160,235,310,385,460,535,610,685,
  							760,
							10,85,160,235,310,385,460,535,610,685,
							10,85,160,235,310,385,460,535,610,
							10,85,160,235,310,385,460,535,
							10,85,160,235,310,385,460,
							10,85,160,235,310,385,
							10,85,160,235,310,
							10,85,160,235,
							10,85,160,
							10,85,
							10};

	private static int[]blocksY2={325,325,325,325,325,325,325,325,325,325,//the gray blocks are blitted 3 times because they have a 3 hit hitbox
							325,325,325,325,325,325,325,325,325,325,
							325,325,325,325,325,325,325,325,325,325,
							325,
							300,300,300,300,300,300,300,300,300,300,// y coordinates of all the blocks from level 1
							275,275,275,275,275,275,275,275,275,
							250,250,250,250,250,250,250,250,
							225,225,225,225,225,225,225,
							200,200,200,200,200,200,
							175,175,175,175,175,
							150,150,150,150,
							125,125,125,
							100,100,
							75};

  private static int removed, lvl=1;
  private int length=75; //length of the blocks is 75
  private int height=25; //height of the blocks is 25
  private String power;


  public Blocks() {

  }

  public static int[] getBlocksX(){ //method used in other classes to have access to the x-coordinates of the blocks
  	return blocksX;
  }
  public static int[]getBlocksY(){ //method used in other classes to have access to the y-coordinates of the blocks
  	return blocksY;
  }

  public void setPower(String n){ //method used to set the power (used in defferent classes)
  	power=n;
  }
  public void setBlocks(int[] x, int[]y){ //method used to set the block arrays from different classes
  	blocksX=x;
  	blocksY=y;
  }

  public static int getLevel(){ //returns level 1
  	return lvl;
  }

  public void levelOver(){ //if this method is called and it is now level 2,
  	blocksX=blocksX2; //the block coordinate arrays are changed to the level 2 ones
  	blocksY=blocksY2;
  	power="none"; //power is cancelled
  	if (lvl==2){ //if it is level 2 already and the level is over
  		System.exit(0); //program is exited
  	}
  	lvl=2; //level is changed to level 2
  }
  public String returnColour(int x, int y) { //method used to return the colour (for the score) by using coordinates
  	if(lvl==1){ //if it is level 1
	  	if(y==200) { //all the blocks with a 200 y coordinate are green etc.
	  		return "green";
	  	}
	  	if(y==175) {
	  		return "purple";
	  	}
	  	if(y==150) {
	  		return "blue";
	  	}
	  	if(y==125) {
	  		return "yellow";
	  	}
	  	if(y==100) {
	  		return "red";
	  	}
	  	if(y==75) {
	  		return "grey";
	  	}
  	}
  	else{ //if it is level 2
  		if(x==10 && y!=325) { //every x coordinate has a colomn of the same colour except the blocks with the y coordinate 325 which is grey
	  		return "white";
	  	}
	  	if(x==85 && y!=325) {
	  		return "orange";
	  	}
	  	if(x==160 && y!=325) {
	  		return "cyan";
	  	}
	  	if(x==235 && y!=325) {
	  		return "green";
	  	}
	  	if(x==310 && y!=325) {
	  		return "red";
	  	}
	  	if(x==385 && y!=325) {
	  		return "blue";
	  	}
	  	if(x==460 && y!=325) {
	  		return "pink";
	  	}
	  	if(x==535 && y!=325) {
	  		return "yellow";
	  	}
	  	if(x==610 && y!=325) {
	  		return "white";
	  	}
	  	if(x==685 && y!=325) {
	  		return "orange";
	  	}
	  	if(y==325 && x==760) {
	  		return "cyan";
	  	}
	  	if(y==325 && x!=760) {
	  		return "grey";
	  	}
  	}
  	return ""; //if not, return nothing
  }
  public void draw(Graphics g){ //draw method
  	for(int i=0;i<blocksX.length;i++) { //loops for every x coordinate
  		if (lvl==1){ //if level is 1
	  		if(i<11){ //the colours are arranged in rows, so for each interval, a different colour is set
	  			g.setColor(Color.green); //sets the colour
	  		}
	  		else if(i<22){
	  			g.setColor(new Color(192, 0, 255));
	  		}
	  		else if(i<33){
	  			g.setColor(Color.blue);
	  		}
	  		else if(i<44){
	  			g.setColor(Color.yellow);
	  		}
	  		else if(i<55){
	  			g.setColor(Color.red);
	  		}
	  		else if(i<blocksX.length){
	  			g.setColor(Color.gray);
	  		}
  		}
  		else{ //if level is 2
  			if(blocksX[i]==10 && blocksY[i]!=325){ //every x coordinate has its own colomn of a colour, everything except the ones with 325
  			//as the y value (those are grey blocks)
	  			g.setColor(Color.white);
	  		}
	  		else if(blocksX[i]==85 && blocksY[i]!=325){
	  			g.setColor(Color.orange);
	  		}
	  		else if(blocksX[i]==160 && blocksY[i]!=325){
	  			g.setColor(Color.cyan);
	  		}
	  		else if(blocksX[i]==235 && blocksY[i]!=325){
	  			g.setColor(Color.green);
	  		}
	  		else if(blocksX[i]==310 && blocksY[i]!=325){
	  			g.setColor(Color.red);
	  		}
	  		else if(blocksX[i]==385 && blocksY[i]!=325){
	  			g.setColor(Color.blue);
	  		}
	  		else if(blocksX[i]==460 && blocksY[i]!=325){
	  			g.setColor(Color.pink);
	  		}
	  		else if(blocksX[i]==535 && blocksY[i]!=325){
	  			g.setColor(Color.yellow);
	  		}
	  		else if(blocksX[i]==610 && blocksY[i]!=325){
	  			g.setColor(Color.white);
	  		}
	  		else if(blocksX[i]==685 && blocksY[i]!=325){
	  			g.setColor(Color.orange);
	  		}
	  		else if(blocksY[i]==325 && blocksX[i]!=760){
	  			g.setColor(Color.gray);
	  		}
	  		else if(blocksY[i]==325 && blocksX[i]==760){
	  			g.setColor(Color.cyan);
	  		}
  		}
  		if (blocksX[i]!=0){
	    	g.fillRect(blocksX[i],blocksY[i],75,25);
	  	}
	  	else{
	    	removed+=1; //adds one to the integer
	    }
	    g.setColor(Color.black);
	    g.drawLine(blocksX[i],blocksY[i],blocksX[i]+75,blocksY[i]); //draws black lines on the edges of all the blocks
	    g.drawLine(blocksX[i],blocksY[i],blocksX[i],blocksY[i]+25); //makes it look like they are seperated instead of one huge block
	    g.drawLine(blocksX[i]+75,blocksY[i],blocksX[i]+75,blocksY[i]+25);
	    g.drawLine(blocksX[i],blocksY[i],blocksX[i],blocksY[i]+25);
	    if(removed>=blocksX.length){ //if all the blocks are removed, the level is over
	    	levelOver(); //calls the levelOver method
	    }
  	}
  	g.setColor(Color.gray); //sets the colour to grey for the border of the screen
  	g.fillRect(0,0,10,845); //draws 4 rectangles around the screen to make a border apppearance
  	g.fillRect(830,0,10,845);
  	g.fillRect(0,0,845,10);
  	g.fillRect(0,755,845,10);
  	if (power=="break"){ //if the break powerup is active
  		g.setColor(Color.black); // sets the colour to black
  		g.fillRect(830,670,10,60); //a rectangle is drawn to the side to give the effect of a door to the next level
  	}
  	removed=0; //resets the integer
  	}
}

class PowerUps extends JPanel{
	private static String[] powers={"enlarge","catch","break","slow"}; //the 4 different possible powerups
	private static int chance,px,py,delX,delY;
	private static String fallPower="none",power="none";
	private Image slow,catcher,enlarge,breaks;
	private static Ball ball;

	public PowerUps(){
		px=500; //coordinates of the powerUps
		py=200;
		slow=new ImageIcon("slow.png").getImage(); //loads all the images of the powerups
		catcher=new ImageIcon("catch.png").getImage();
		enlarge=new ImageIcon("expand.png").getImage();
		breaks=new ImageIcon("break.png").getImage();
	}

	public static int randint(int low, int high){ //method that generates a random integer inside a given range
	  int range=high-low+1;
	  return (int)(Math.random()*range)+low;
	}

	public static String powerType(){ //method that is called to generate a powerup
		chance=randint(1,5); //1 in 5 chance of getting a powerup from a destroyed block
		if(chance==1){ //from that 1 in 5:
			fallPower=powers[randint(0,3)]; //a random powerup is chosen and generated
		}
		return fallPower; //returns the generated powerup
	}

	public void move(){ //changes the y value of the powerups (2 pixels down) to make them fall
		py+=2;
	}

	public static int getPx(){ //returns power x coordinate
		return px;
	}
	public static int getPy(){ //returns power y coordinate
		return py;
	}

	public static String getFallPower(){ //method used inside other classes (access to the current calue of fallPower)
		return fallPower;
	}
	public void setFallPower(String n) { //sets the fallPower
		fallPower=n;
	}
	public void setPower(String n) { //sets the power
		power=n;
	}
	public static void setDel(int x,int y){
		delX=x;
		delY=y;
		px=x;
		py=y;
	}

	public void draw(Graphics g){
		if(power=="none"){
			if(fallPower=="slow") { //for every powerup, the corresponding image is drawn
				g.drawImage(slow,px,py,null); //px and py are coordinates (py is changed above to make the powerup "fall"
			}
			if(fallPower.equals("enlarge")) {
				g.drawImage(enlarge,px,py,null);
			}
			if(fallPower.equals("catch")) {
				g.drawImage(catcher,px,py,null);
			}
			if(fallPower.equals("break")) {
				g.drawImage(breaks,px,py,null);
			}
		}
		move();
	}
}
class Score {
	private static int score; //variable keeps track of the score
	private static String colour; //variable keeps track of the block colour
	private static String finalString; //final string to be blitted on the screen
	Font fontSys=null;//null font

	public Score() {
		score=0; //score starts at 0
	}

	public static void findScore(String colour) { //method that takes a colour as a parameter and adds the corresponding
	//number of points to the score
		if(colour.equals("green")) {
			score+=50;
		}
		if(colour.equals("purple")) {
			score+=100;
		}
		if(colour.equals("blue")) {
			score+=500;
		}
		if(colour.equals("yellow")) {
			score+=1000;
		}
		if(colour.equals("red")) {
			score+=2000;
		}
		if(colour.equals("grey")) {
			score+=2500;
		}
		if(colour.equals("cyan")) {
			score+=300;
		}
		if(colour.equals("orange")) {
			score+=1000;
		}
		if(colour.equals("white")) {
			score+=700;
		}
		if(colour.equals("pink")) {
			score+=600;
		}
	}
	public void text(Graphics g) {
		fontSys = new Font("Comic Sans MS",Font.PLAIN,32); //sets new Comic Sans font
		g.setFont(fontSys); //sets the font
		finalString="Score: "+Integer.toString(score); //puts everything into one string
		g.drawString(finalString, 50, 50); //blits the string

	}
}
