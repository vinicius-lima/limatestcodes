package draw;

import java.awt.Color;
import java.awt.Graphics;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.swing.JPanel;

public class DrawString extends JPanel{

	private static final long serialVersionUID = 1L;
	private int frameWidth;
	private int frameHeight;
	private int sx;
	private int sy;
	private int r;
	private int g;
	private int b;
	private int changeColor;
	private boolean up = false;
	private boolean down = true;
	private boolean left = false;
	private boolean right = true;
	private Random rand;
	
	public DrawString(int width, int height){
		super();
		frameWidth = width;
		frameHeight = height;
		super.setSize(frameWidth, frameHeight);
		rand = new Random(System.currentTimeMillis());
		changeColor = r = g = b = 0;
	}
	
	public void paintComponent(Graphics gph){
		String s = "Android";
		int stringWidth = gph.getFontMetrics().charsWidth(s.toCharArray(), 0, s.length());
		
		if (sx >= frameWidth - stringWidth)
        {
            right = false;
            left = true;
        }
        if (sx <= 0)
        {
            right = true;
            left = false;
        }
        if (sy >= frameHeight - 22)
        {
            up = true;
            down = false;
        }
        if (sy <= 7)
        {
            up = false;
            down = true;
        }
        if (up) sy--;
        if (down) sy++;
        if (left) sx--;
        if (right) sx++;
        changeColor++;
        
		//g.setColor(Color.BLACK);
		//g.setColor(Color.BLUE);
        if(changeColor > 100){
        	r = rand.nextInt(256);
        	g = rand.nextInt(256);
        	b = rand.nextInt(256);
        	changeColor = 0;
        }
        gph.setColor(new Color(r, g, b));
		gph.drawString(s, sx, sy);
	}
	
	public byte[] getState(){
		ByteBuffer state = ByteBuffer.allocate(28);
		
		int direction = 0;
		direction = (up) ? direction + 1: direction;
		direction = (right) ? direction + 10: direction;
		
		state.putInt(sx);
		state.putInt(sy);
		state.putInt(r);
		state.putInt(g);
		state.putInt(b);
		state.putInt(changeColor);
		state.putInt(direction);
		
		return state.array();
	}
	
	public boolean updateState(byte[] state){
		if(state.length != 28)
			return false;
		
		ByteBuffer st = ByteBuffer.wrap(state);
		
		sx = st.getInt();
		sy = st.getInt();
		r = st.getInt();
		g = st.getInt();
		b = st.getInt();
		changeColor = st.getInt();
		int direction = st.getInt();
		
		if(direction == 0){
			up = right = false;
			down = left = true;
		}
		else if(direction == 1){
			up = left = true;
			down = right = false;
		}
		else if(direction == 10){
			up = left = false;
			down = right = true;
		}
		else{
			up = right = true;
			down = left = false;
		}
		
		return true;
	}
}
