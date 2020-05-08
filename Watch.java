import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Watch extends JFrame {
	// Create Timer and call function to initialize UI
	final Timer time = new Timer(1000, null);
	
	public Watch() throws IOException {
		initUI();
	}
	
	
	/**
	 * Acts as driver of the program.
	 */
	private void initUI() throws IOException {
		// Set JFrame attributes
		setTitle("My Watch");
		setSize(470, 470);
		setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Instantiate layered pane to hold digital/analog representations
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new GridBagLayout());
        layeredPane.setPreferredSize(new Dimension(450, 470));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        
        // Add Digital to draw digital time
        Digital myDigital = new Digital();
        layeredPane.add(myDigital, gbc);
        myDigital.setVisible(false);
        
        // Add Analog to draw analog clock and its hands
        Analog myAnalog = new Analog();
        layeredPane.add(myAnalog, gbc);
        
        // Add layered pane to JFrame
        add(layeredPane, BorderLayout.CENTER);
        
        // Draw settings dial
        JToggleButton settingsButton = new JToggleButton();
        settingsButton.setPreferredSize(new Dimension(30, 40));
        ImageIcon settingsIcon = new ImageIcon("settingsDial.jpg");
        settingsButton.setIcon(settingsIcon);
        settingsButton.setBorder(null);
        add(settingsButton, BorderLayout.LINE_END);
        
        // Add settings button functionality
        settingsButton.addItemListener(new ItemListener() {
        	@Override
        	public void itemStateChanged(ItemEvent ie) {
        		int state = ie.getStateChange();
        		
        		// Display digital option, if selected
        		if (state == ItemEvent.SELECTED) {
        			myAnalog.setVisible(false);
        			myDigital.setVisible(true);
        		} 
        		else {
        			myAnalog.setVisible(true);
        			myDigital.setVisible(false);
        		}
        	}
        });
        
        
        // Update time displayed every second
        time.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		myAnalog.repaint();
        		myDigital.repaint();
        	}
        });
        time.start();
	}
	
	
	public static void main(String[] args) throws IOException {
		Watch myWatch = new Watch();
		myWatch.setVisible(true);
	}
}


/**
 * Draws analog time in JFrame.
 *
 */
class Analog extends JPanel {
	BufferedImage watchFace = null;
	private int centerX = 220;
	private int centerY = 225;
	
	public Analog() throws IOException {
		File watchFaceFile = new File("watch_face_icon.png");
		watchFace = ImageIO.read(watchFaceFile);
	}
	
	// Draws second hand
	private void drawSecond(Graphics g) {
		// Get time and proper rotation
		LocalDateTime now = LocalDateTime.now();
		double second = (double)now.getSecond();
		double angle = second/60.0*2.0*Math.PI;
		
		// Create rotation
		AffineTransform af = new AffineTransform();
		af.rotate(angle, centerX, centerY);
		
		// Create Second hand
		Line2D.Double secHand = new Line2D.Double(centerX, centerY, centerX, 30);	// second-hand length should be ~205px
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.ORANGE);
		g2.setStroke(new BasicStroke(2));
		g2.setTransform(af);
		g2.draw(secHand);
	}
	
	// Draws minute hand
	private void drawMinute(Graphics g) {
		// Get time and proper rotation
		LocalDateTime now = LocalDateTime.now();
		double minute = (double)now.getMinute() + (double)now.getSecond() / 60.0;
		double angle = minute/60.0*2.0*Math.PI;
		
		// Create rotation
		AffineTransform af = new AffineTransform();
		af.rotate(angle, centerX, centerY);
		
		// Create Minute hand
		Line2D.Double minHand = new Line2D.Double(centerX, centerY, centerX, 30);	// minute-hand length should be ~205px
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.RED);
		g2.setStroke(new BasicStroke(5));
		g2.setTransform(af);
		g2.draw(minHand);
	}
	
	// Draws hour hand
	private void drawHour(Graphics g) {
		// Get time and proper rotation
		LocalDateTime now = LocalDateTime.now();
		double hour = (double)now.getHour() % 12 + (double)now.getMinute() / 60.0;
		double angle = hour/12.0*2.0*Math.PI;
		
		// Create rotation
		AffineTransform af = new AffineTransform();
		af.rotate(angle, centerX, centerY);
		
		// Create Hour hand
		Line2D.Double hourHand = new Line2D.Double(centerX, centerY, centerX, 60);	// hour-hand length should be ~165px
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(7));
		g2.setTransform(af);
		g2.draw(hourHand);	
	}
	
	// Draws watch face
	private void drawFace(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(watchFace, 10, 10, null);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawFace(g);
		drawSecond(g);
		drawMinute(g);
		drawHour(g);
	}
}


/**
 * Draws digital time in JFrame.
 */
class Digital extends JPanel {
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Get/Format time
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String timeStr = dtf.format(now);
		
		// Display time
		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(new Font("Helvetica", Font.BOLD, 45));
		g2.setColor(Color.BLACK);
		g2.drawString(timeStr, 150, 225);
	}
}
