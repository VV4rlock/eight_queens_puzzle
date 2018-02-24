import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class creates the UI of the board and the mouse events listeners.
 * You do not have to change anything in this file. 
 * 
 * @author Stavros Amanatidis
 * 
 * */
public class QueensGUI extends JComponent implements MouseListener
{
	//final static long serialVersionUID = 1234567890;
	private int 		chosenColumn;	// The value of the last column chosen by the user.
    private int         chosenRow;   // The value of the last row chosen by the user.
	private QueensLogic logic;
	
	// Images for drawing the board
	private Image 		part, queen, invalid, backgroundW, backgroundB;
	private Image 		border_left,border_right,border_top,border_bottom;
	private Image 		corner_left_top, corner_left_bottom,corner_right_top,corner_right_bottom;
	private Image       background,button_solve,button_clear,left_background,top_background,bot_background;
	private Image       blackQueen;
	
	/**
	 * @param logic The implementation of the game logic  
	 */
	public QueensGUI(QueensLogic logic)
	{
		part = Toolkit.getDefaultToolkit().getImage("imgs/maze.png");
		queen = Toolkit.getDefaultToolkit().getImage("imgs/queen.png");
		invalid = Toolkit.getDefaultToolkit().getImage("imgs/invalid.png");
		backgroundW = Toolkit.getDefaultToolkit().getImage("imgs/backgroundWhite.png");
        backgroundB = Toolkit.getDefaultToolkit().getImage("imgs/backgroundBlack.png");
		
		border_left = Toolkit.getDefaultToolkit().getImage("imgs/board_left.png");
		border_right = Toolkit.getDefaultToolkit().getImage("imgs/board_right.png");
		border_top = Toolkit.getDefaultToolkit().getImage("imgs/board_top.png");
		border_bottom = Toolkit.getDefaultToolkit().getImage("imgs/board_bottom.png");
		corner_left_top = Toolkit.getDefaultToolkit().getImage("imgs/corner_top_left.png");
		corner_left_bottom = Toolkit.getDefaultToolkit().getImage("imgs/corner_bottom_left.png");
		corner_right_top = Toolkit.getDefaultToolkit().getImage("imgs/corner_top_right.png");
		corner_right_bottom = Toolkit.getDefaultToolkit().getImage("imgs/corner_bottom_right.png");

		background = Toolkit.getDefaultToolkit().getImage("imgs/empty_background.png");
		button_solve = Toolkit.getDefaultToolkit().getImage("imgs/button_solve.png");
		button_clear = Toolkit.getDefaultToolkit().getImage("imgs/button_clear.png");
		left_background=Toolkit.getDefaultToolkit().getImage("imgs/left_background.png");
		top_background=Toolkit.getDefaultToolkit().getImage("imgs/background_top_corner.png");
		bot_background=Toolkit.getDefaultToolkit().getImage("imgs/background_bot_corner.png");
		blackQueen=Toolkit.getDefaultToolkit().getImage("imgs/black_queen.png");

		this.logic = logic;
		this.addMouseListener(this);
	}

	/*
	 * Draws the current game board and shows if someone won.
	 */
	public void paint(Graphics g){
		this.setDoubleBuffered(true);
		Insets in = getInsets();               
		g.translate(in.left, in.top);            

		int[][] gameboard = logic.getGameBoard();
		int cols = gameboard.length;
		int rows = gameboard[0].length;
        //draw borders
        for (int i = 0;i<cols;i++) {
            g.drawImage(border_left, 0, 50+50*i, this);
            g.drawImage(border_right, 100 + gameboard.length *50, 50+50*i, this);
            g.drawImage(border_top, 50+50*i, 0, this);
            g.drawImage(border_bottom, 50+50*i, 50+gameboard.length*50, this);
        }

        //draw board
		for (int c = 0; c < cols; c++){
			for (int r = 0; r < rows; r++){
				int player = gameboard[c][r];
                
                if ((c+r)%2==0) g.drawImage(backgroundW, 50+50*c, 50+50*r, this);
                else g.drawImage(backgroundB, 50+50*c, 50+50*r, this);
                
                if (player == 1 || player==-1) // red = computer
					g.drawImage(queen, 50+50*c, 50+50*r, this);
                if (player==2 || player==-2)
					g.drawImage(blackQueen, 50+50*c, 50+50*r, this);
				if (player == -1|| player==-2)//invalid
					g.drawImage(invalid, 50+50*c, 50+50*r, this);
				
                g.drawImage(part, 50+50*c, 50+50*r, this);
				
			}
		}

		for(int i=0;i<rows+2;i++){
        	if (i==1)
        		g.drawImage(button_clear,50+50*cols,50*i,this);
        	else if (i==0)
				g.drawImage(top_background,50+50*cols,50*i,this);
			else if(i==3)
				g.drawImage(button_solve,50+50*cols,50*i,this);
			else if (i==rows+1)
				g.drawImage(bot_background,50+50*cols,50*i,this);
			else
				g.drawImage(left_background,50+50*cols,50*i,this);


		}

    
		//draw corners
		g.drawImage(corner_left_top, 0, 0, this);
		g.drawImage(corner_left_bottom, 0, 50+rows*50, this);
		g.drawImage(corner_right_top, 100+50*cols, 0, this);
		g.drawImage(corner_right_bottom, 100+50*cols, 50+rows*50, this);
		
 	}

	/*
	 * When the user clicks on one of the board cells, the corresponding
	 * column and row is kept and parsed to the logic. 
	 */
	public void mouseClicked(MouseEvent e){
        chosenColumn = e.getX()/50 - 1;
        chosenRow = e.getY()/50 - 1;
		//System.out.println("press "+chosenRow+" "+chosenColumn);
        int size=logic.getSize();
        if (chosenColumn==size && chosenRow==0){
        	//System.out.println("press Clear");
        	logic.clear_board();
        	logic.updateBoard();
        	repaint();
        	return;
		} else if (chosenColumn==size && chosenRow==2){
			//System.out.println("press Solve");
			logic.solve();
			logic.updateBoard();
			repaint();
			return;
		}
        
        //this method connects the UI with the logic
		if ((chosenColumn >= 0) && (chosenColumn < size) && (chosenRow >= 0) && (chosenRow < size)) {
			
			//this is the main method that connects the UI with the Logic. 
			logic.insertQueen(chosenColumn, chosenRow);
			logic.updateBoard();
			repaint();
			
		}

	}

	// Not used methods from the interface of MouseListener 
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}

}

