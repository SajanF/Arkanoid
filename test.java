import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//check math in block collison probbaly directly in corner

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
		ball.move();
		board.collision();
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
	private static int ballx,bally;
	private boolean []keys;
	private boolean catcher;
	private static String fallPower;
	private static String power="none";
	private int[]blocksX,blocksY;
	private arkanoid mainFrame;
	private Image back,pad,pad2;
	Ball ball;
	Blocks blocks;
	PowerUps powerUps;

	public Board(arkanoid m){
		keys = new boolean[KeyEvent.KEY_LAST+1];
	    boardx = 420;
        boardy = 700;
        back = new ImageIcon("ArkBack.png").getImage();
        pad = new ImageIcon("Vaus_icon.png").getImage();
        back= back.getScaledInstance(845,800,Image.SCALE_SMOOTH);
        pad= pad.getScaledInstance(75,15,Image.SCALE_SMOOTH);
        pad2= pad.getScaledInstance(125,15,Image.SCALE_SMOOTH);
        addKeyListener(this);
        setSize(845,800);
        mainFrame=m;
        ball=new Ball();
		blocks=new Blocks(0,0);
		powerUps=new PowerUps();
	}

	public void addNotify() {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }

	public void move(){
		fallPower=powerUps.getFallPower();
		ballx=ball.getBallx();
		bally=ball.getBally();
		if (catcher==true){
			power="catch";
		}
		if (power=="enlarge"){
			width=125;
		}
		if(keys[KeyEvent.VK_RIGHT] ){
			boardx += 10;
			if (power=="catch"){
				if (bally<=boardy+15 && bally+15>=boardy){
					if(ballx-boardx<=75 && ballx-boardx>=0){
						ballx += 10;
					}
				}
			}
		}
		if(keys[KeyEvent.VK_LEFT] ){
			boardx -= 10;
			if (power=="catch"){
				if (bally<=boardy+15 && bally+15>=boardy){
					if(ballx-boardx<=75 && ballx-boardx>=0){
						ballx -= 10;
					}
				}
			}
		}
		if(power=="catch"){
			if(keys[KeyEvent.VK_ENTER] ){
				catcher=true;
				power="none";
			}
		}
		//System.out.println(bally+" "+ballx);
		ball.setPower(power);
		blocks.setPower(power);
		ball.setBall(ballx,bally);
		ball.setBoard(boardx,boardy);
	}

	public void getCoords(){
		ballx=ball.getBallx();
		bally=ball.getBally();
	}

	public void collision(){
		px=powerUps.getPx();
		py=powerUps.getPy();
		if(py>800){
			fallPower="none";
			powerUps.setFallPower(fallPower);
			ball.setFallPower(fallPower);
		}
		if (power=="break"){
			if (boardx+75>=830){
				blocks.levelOver();
			}
		}
		if (bally+15>=boardy && bally<=boardy+15){
		//	if(ballx-boardx<=width && ballx-boardx>=0){
				if(power!="catch"){
					hitx=ballx;
					hity=bally;
					ball.angle(hitx,hity);
				}
		//	}
		}
		if(fallPower!="none"){
			if(py+25>=boardy && py<=boardy){
				if(px+width>=boardx && px<=boardx+width){
					catcher=false;
					power=fallPower;
					ball.setPower(power);
					fallPower="none";
					ball.setFallPower(fallPower);
				}
			}
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
			g.drawImage(pad2,boardx,boardy,null);
		}
		else{
			g.drawImage(pad,boardx,boardy,null);
		}
    	ball.draw(g);
    	blocks.draw(g);
    	powerUps.draw(g);
  }
}

class Ball extends JPanel{
	private static int[]blocksX,blocksY;
	private static int ballx,bally,hitx,hity,hitxx,hityy,hor,vert,j,vel,boardx,boardy;
	private static double lenx,leny,dist,xcomp,ycomp,xpos,ypos;
	private static double ang1=25.906507999514385;
	private static String power;
	private static double ang2=90-ang1;
	private static String fallPower="none";
	private static Blocks blocks;
	private static PowerUps powerUps;
	private static Score score;

	public Ball(){
		ballx=100;
		bally=310;
		hor=1;
		vert=1;
		vel=6;
		blocks=new Blocks(0,0);
		powerUps=new PowerUps();
		score =new Score();
	}

	public static void angle(int xx,int yy){
		lenx=Math.abs(ballx-hitx);
		leny=Math.abs(bally-hity);
		dist=Math.sqrt(lenx*lenx+leny*leny);
		ang1=Math.asin((lenx*Math.sin(90*Math.PI*2/360))/dist)*180/Math.PI;
		if(ang1>75){
			ang1=70;
			ang2=20;
		}
		if(ang2>75){
			ang2=70;
			ang1=20;
		}
		ang2=90-ang1;
		hitx=xx;
		hity=yy;
		if (bally-700<=15 && bally-700>=0){
			vert=-1;
		}
		if(bally<=0){
			vert=1;
		}
		move();
	}

	public static void move(){
	//	System.out.println(ang1+" "+ang2 +" "+hitx+" "+hity);
	//	System.out.println(ballx+" "+bally);
		if (power=="slow"){
			vel=3;
		}
		else{
			vel=6;
		}
		if (bally-boardy<=15 && bally-boardy>=0 && ballx-boardx<=75 && ballx-boardx>=0 && power=="catch"){

		}
		else{
	//		System.out.println(ballx+" "+bally+"not catch on board");
			blocksX=blocks.getBlocksX();
			blocksY=blocks.getBlocksY();
			xcomp=Math.abs((vel*Math.sin(ang1*Math.PI*2/360))/(Math.sin(90*Math.PI*2/360)))*hor;
			ycomp=Math.abs((vel*Math.sin(ang2*Math.PI*2/360))/(Math.sin(90*Math.PI*2/360)))*vert;
			ballx+=xcomp;
			bally+=ycomp;
		//	System.out.println(ballx+" ball "+bally);
			if (vert==-1){
				for(int i=0;i<blocksX.length;i++) {
					if(ballx>=blocksX[i]-15 && ballx<=blocksX[i]+75){
						if(bally>=blocksY[i]-15 && bally<=blocksY[i]+25) {
							j=i;
							if(blocksX[j]!=0){
						//		System.out.println(ballx+" ball "+bally+" blocks "+blocksX[j]+" "+blocksY[j] +" kkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
								collide();
							}
						}
					}
				}
			}
			if (vert==1){
				for(int i=0;i<blocksX.length;i++) {
					if(ballx>=blocksX[65-i]-15 && ballx<=blocksX[65-i]+75){
						if(bally>=blocksY[65-i]-15 && bally<=blocksY[65-i]+25) {
							j=65-i;
							if(blocksX[j]!=0){
							//	System.out.println(ballx+" ball "+bally+" blocks "+blocksX[j]+" "+blocksY[j]+"kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk");
								collide();
							}
						}
					}
				}
			}
			if(ballx>=830) {
				ballx=830;
				hor=-1;
				angle(845,bally);
			}
			else if(ballx<=0) {
				ballx=0;
				hor=1;
				angle(0,bally);
			}
			else if(bally<=0) {
				bally=0;
				angle(ballx,0);
			}
		}
	}

	public static void collide(){
	    if(blocksX[j]!=0 && blocksY[j]!=0) {
	    //collision on the botton of the bricks
	    	if(ballx+15>=blocksX[j] && ballx<=blocksX[j]+75 && vert==-1 && bally>=blocksY[j]+19 && bally<=blocksY[j]+25) {
	    		if (ballx>blocksX[j]+69 && ballx<=blocksX[j]+75 || ballx+15>=blocksX[j] && ballx+15<blocksX[j]+6){//bottom right and left corner
	    			ypos=Math.round(Math.abs(ycomp+bally));
	    			for (int i=0; i<Math.abs(ycomp); i++){
	    				if (ypos<=blocksY[j]+24 && ypos>=blocksY[j]){
	    					System.out.println("1 c");
	    					vert=1;
	    					if (fallPower=="none"){
								fallPower=powerUps.powerType();
								powerUps.setDel(blocksX[j],blocksY[j]);
							}
							String returnedColour=blocks.returnColour(blocksY[j]);
							score.findScore(returnedColour);
							blocksX[j]=0;
							blocksY[j]=0;
							angle(ballx,bally);
	    				}
	    				ypos-=1;
	    			}
	    		}
	    		else if (ballx+6>blocksX[j]-15 && ballx<blocksX[j]+69){
	    			System.out.println("1");
		    		vert=1;
		    		if (fallPower=="none"){
						fallPower=powerUps.powerType();
						powerUps.setDel(blocksX[j],blocksY[j]);
					}
					String returnedColour=blocks.returnColour(blocksY[j]);
					score.findScore(returnedColour);
					blocksX[j]=0;
					blocksY[j]=0;
					angle(ballx,bally);
	    		}
	         }
	         if(ballx+15>=blocksX[j] && ballx<=blocksX[j]+75 && vert==1 && bally+15>=blocksY[j] && bally+15<=blocksY[j]+6) { //collision when hitting top of block
	        // System.out.println("first if 2");
	           	if (ballx>blocksX[j]+69 && ballx<=blocksX[j]+75 || ballx+15>=blocksX[j] && ballx+15<=blocksX[j]+6){//top right and left corner
	    			ypos=Math.abs(ycomp-bally)+15;
	    			for (int i=0; i<Math.abs(ycomp)+1; i++){
	    				System.out.println("first if 2 c "+Math.round(ypos));
	    				if (Math.round(ypos)<=blocksY[j]+1 && Math.round(ypos)>=blocksY[j]){
	    					System.out.println("2 c");
	    					vert=-1;
	    					if (fallPower=="none"){
								fallPower=powerUps.powerType();
								powerUps.setDel(blocksX[j],blocksY[j]);
							}
							String returnedColour=blocks.returnColour(blocksY[j]);
							score.findScore(returnedColour);
							blocksX[j]=0;
							blocksY[j]=0;
							angle(ballx,bally);
	    				}
	    				ypos+=1;
	    			}
	    		}
	    		else if (ballx+15>blocksX[j]+6 && ballx<=blocksX[j]+69){
	    			System.out.println("2");
		    		vert=-1;
		    		if (fallPower=="none"){
						fallPower=powerUps.powerType();
						powerUps.setDel(blocksX[j],blocksY[j]);
					}
					String returnedColour=blocks.returnColour(blocksY[j]);
					score.findScore(returnedColour);
					blocksX[j]=0;
					blocksY[j]=0;
					angle(ballx,bally);
	    		}
	         }

	         //collision block on the right side
	         if(ballx<=blocksX[j]+75 && ballx>blocksX[j]+69 && bally<=blocksY[j]+25 && bally+6>=blocksY[j] && hor==-1) {//added 6 to bally because checking from bottom to top and bally could be <blocksy[j]
	           		System.out.println("3");
		            hor=1;
		            if (fallPower=="none"){
						fallPower=powerUps.powerType();
						powerUps.setDel(blocksX[j],blocksY[j]);
					}
					String returnedColour=blocks.returnColour(blocksY[j]);
					score.findScore(returnedColour);
		            blocksX[j]=0; //removes block from created list inside move method
		            blocksY[j]=0;
	            	angle(ballx,bally);
	         }

	         //collision left side of the brick
	         if(ballx+15>=blocksX[j] && ballx+15<=blocksX[j]+6 && bally<=blocksY[j]+25 && bally>=blocksY[j]-15 && hor==1) {
	           System.out.println("4");
	            hor=-1;
	            if (fallPower=="none"){
					fallPower=powerUps.powerType();
					powerUps.setDel(blocksX[j],blocksY[j]);
				}
				String returnedColour=blocks.returnColour(blocksY[j]);
				score.findScore(returnedColour);
	            blocksX[j]=0; //removes block from created list inside move method
	            blocksY[j]=0;
	            angle(ballx,bally);
	         }
	    }
	  move();
	}

	public static int getBallx(){
		return ballx;
	}

	public static int getBally(){
		return bally;
	}
	public static void setPower(String n){
		power=n;
	}
	public static void setFallPower(String n){
		fallPower=n;
	}

	public void setBall(int x,int y){
		ballx=x;
		bally=y;

	}
	public void setBoard(int x,int y){
		boardx=x;
		boardy=y;
	}


	public void draw(Graphics g){
		g.setColor(Color.white);
		g.fillOval(ballx,bally,15,15);
	}
}

class Blocks extends JPanel {

  private static int[]blocksX={10,85,160,235,310,385,460,535,610,685,760,
  						10,85,160,235,310,385,460,535,610,685,760,
  						10,85,160,235,310,385,460,535,610,685,760,
  						10,85,160,235,310,385,460,535,610,685,760,
  						10,85,160,235,310,385,460,535,610,685,760,
  						10,85,160,235,310,385,460,535,610,685,760};
  private static int[]blocksY={200,200,200,200,200,200,200,200,200,200,200,
  						175,175,175,175,175,175,175,175,175,175,175,
  						150,150,150,150,150,150,150,150,150,150,150,
  						125,125,125,125,125,125,125,125,125,125,125,
  						100,100,100,100,100,100,100,100,100,100,100,
  						75,75,75,75,75,75,75,75,75,75,75};

  private int[]removed;
  private int bX;
  private int bY,score;
  private int length=75;
  private int height=25;
  private String power;
  private boolean collideX=false;
  private boolean collideY=false;
  private boolean finalCollide=false;

  public Blocks(int x, int y) {
    bX=x;
    bY=y;
  }

  public static int[] getBlocksX(){
  	return blocksX;
  }
  public static int[]getBlocksY(){
  	return blocksY;
  }

  public void setPower(String n){
  	power=n;
  }

  public void remove(int x, int y) {
  	blocksX[x]=0;
  	blocksY[y]=0;
  }

  public void levelOver(){
  	for(int i=0; i<66; i++){
  		blocksX[i]=0;
  		blocksY[i]=0;
  	}
  }
  
  public static String returnColour(int x) {
  	if(x==200) {
  		return "green";
  	}
  	if(x==175) {
  		return "purple";
  	}
  	if(x==150) {
  		return "blue";
  	}
  	if(x==125) {
  		return "yellow";
  	}
  	if(x==100) {
  		return "red";
  	}
  	else {
  		return "gray";
  	}
  }

  public void draw(Graphics g){
  	for(int i=0;i<blocksX.length;i++) {
	  		if(i<11){
	  			g.setColor(Color.green);
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
	  		if (blocksX[i]!=0){
	    		g.fillRect(blocksX[i],blocksY[i],75,25);
	  		}
	    	g.setColor(Color.black);
	    	g.drawLine(blocksX[i],blocksY[i],blocksX[i]+75,blocksY[i]);
	    	g.drawLine(blocksX[i],blocksY[i],blocksX[i],blocksY[i]+25);
	    	g.drawLine(blocksX[i]+75,blocksY[i],blocksX[i]+75,blocksY[i]+25);
	    	g.drawLine(blocksX[i],blocksY[i],blocksX[i],blocksY[i]+25);
  		}
  		g.setColor(Color.gray);
  		g.fillRect(0,0,10,845);
  		g.fillRect(830,0,10,845);
  		g.fillRect(0,0,845,10);
  		g.fillRect(0,755,845,10);
  		if (power=="break"){
  			g.setColor(Color.black);
  			g.fillRect(830,670,10,60);
  		}
  		System.out.println(score);
  	}
}

class PowerUps extends JPanel{
	private static String[] powers={"enlarge","catch","break","slow"};
	private static int chance,px,py,delX,delY;
	private static String fallPower="none";
	private Image slow,catcher,enlarge,breaks;
	private static Ball ball;

	public PowerUps(){
		px=500;
		py=200;
		slow=new ImageIcon("slow.png").getImage();
		catcher=new ImageIcon("catch.png").getImage();
		enlarge=new ImageIcon("expand.png").getImage();
		breaks=new ImageIcon("disruption.png").getImage();
	}

	public static int randint(int low, int high){
	  int range=high-low+1;
	  return (int)(Math.random()*range)+low;
	}

	public static String powerType(){
		chance=randint(1,4);
		chance=1;
		if(chance==1){
			fallPower=powers[randint(0,3)];
		}
		else{
			fallPower="";
		}
		//fallPower="catch";
		return fallPower;
	}

	public void move(){
		py+=2;
	}
	public void setFallPower(String n){
		fallPower=n;
	}

	public static int getPx(){
		return px;
	}
	public static int getPy(){
		return py;
	}
	public static String getFallPower(){
		return fallPower;
	}

	public static void setDel(int x,int y){
		delX=x;
		delY=y;
		px=x;
		py=y;
	}

	public void draw(Graphics g){
		if(fallPower=="slow") {
			g.drawImage(slow,px,py,null);
		//	g.fillRect(px,py,75,25);
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
	//	System.out.println(fallPower+" "+delX+" "+delY);
		move();
	}
}

class Score {
	private static int score;
	private static String colour;
	private static String finalString;
	Font fontSys=null;

	public Score() {
		score=0;
	}
	
	public static void findScore(String colour) {
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
			score+=5000;
		}
	}
	public void text(Graphics g) {
		fontSys = new Font("Comic Sans MS",Font.PLAIN,32);
		g.setFont(fontSys);
		finalString="Score: "+Integer.toString(score);
		g.drawString(finalString, 50, 50);

	}
}
