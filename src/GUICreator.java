
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

public class GUICreator {
	JFrame frame;
	GamePanel myGamePanel;
	JTextArea oldCluesAcross;
	JTextArea oldCluesDowns;
	JTextArea newCluesAcross;
	JTextArea newCluesDowns;
	Border border2;
	JPanel upperPanel;
	JPanel rightPanel;
	JPanel lowerPanel;
	JPanel rightGrid;
	JTextField date;
	JTextField infos;
	String[] lettersAdjusted;

	// added in v1.3
	JPanel AcUppPanel;
	JPanel DoUppPanel;
	JPanel AcLowPanel;
	JPanel DoLowPanel;

	JPanel AcUppPanelH;
	JPanel DoUppPanelH;
	JPanel AcLowPanelH;
	JPanel DoLowPanelH;

	public GUICreator(
			String cluesAcrossOld,
			String cluesDownOld,
			String cluesAcrossNew,
			String cluesDownNew,
			String date1,
			String[] letters,
			ArrayList<Coordinate> coordinatesOfNumbers
			) {
		System.out.println(letters.length);
		lettersAdjusted = new String[letters.length*letters.length];
		for(int i = 0 ; i<=letters.length-1; i++ ) {
			for(int j = 0 ; j <= letters.length-1 ; j++ ) {
				
				lettersAdjusted[convertCoordinates(i,j)] = String.valueOf(letters[i].charAt(j));
				
			}
		}
		AcUppPanel = new JPanel(new BorderLayout());
		DoUppPanel = new JPanel(new BorderLayout());
		AcLowPanel = new JPanel(new BorderLayout());
		DoLowPanel = new JPanel(new BorderLayout());
		
		AcUppPanelH = new JPanel(new BorderLayout());
		DoUppPanelH = new JPanel(new BorderLayout());
		AcLowPanelH = new JPanel(new BorderLayout());
		DoLowPanelH = new JPanel(new BorderLayout());
		
		
		frame = new JFrame();
		infos = new JTextField(date1 +"    " + "Zero IQ AI");
		JPanel bottomP = new JPanel(new BorderLayout());
		JPanel bottomP2 = new JPanel(new BorderLayout());
		//bottomP2.setSize(700, 700);
		infos.setHorizontalAlignment(JTextField.RIGHT);
		infos.setFont(new Font("Arial", Font.PLAIN, 15));
		infos.setEditable(false);
		infos.setBorder(null);
		bottomP2.add(infos, BorderLayout.EAST);
		bottomP2.setPreferredSize(new Dimension(680,20));
		bottomP.add(bottomP2, BorderLayout.WEST);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myGamePanel = new GamePanel();

		oldCluesAcross = new JTextArea(15, 15); //!setSize WARNING
		oldCluesDowns = new JTextArea(15, 15); //!setSize WARNING
		
		newCluesAcross = new JTextArea(15, 15); //!setSize WARNING
		newCluesDowns = new JTextArea(15, 15); //!setSize WARNING
		
		
		oldCluesAcross.setText("\n"+cluesAcrossOld);
		oldCluesDowns.setText("\n"+cluesDownOld);
		
		newCluesAcross.setText("\n"+cluesAcrossNew);
		newCluesDowns.setText("\n"+cluesDownNew);
	
		oldCluesAcross.setEditable(false);
		oldCluesDowns.setEditable(false);

		newCluesAcross.setEditable(false);
		newCluesDowns.setEditable(false);
		JTextField AcUppLabel = new JTextField("       Across");
		JTextField DoUppLabel = new JTextField("       Down");
		JTextField AcLowLabel = new JTextField("       New Across");
		JTextField DoLowLabel = new JTextField("       New Down");
		
		AcUppLabel.setBorder(null);
		DoUppLabel.setBorder(null);
		AcLowLabel.setBorder(null);
		DoLowLabel.setBorder(null);
		
		
		Font f = new Font (Font.DIALOG, Font.BOLD, 14);
		Font f2 = new Font (Font.DIALOG, Font.PLAIN, 13);
		AcUppLabel.setFont(f);
		DoUppLabel.setFont(f);
		AcLowLabel.setFont(f);
		DoLowLabel.setFont(f);
		oldCluesAcross.setFont(f2);
		oldCluesDowns.setFont(f2);
		newCluesAcross.setFont(f2);
		newCluesDowns.setFont(f2);
		
		AcUppPanelH.setBackground(Color.white);
		DoUppPanelH.setBackground(Color.white);
		AcLowPanelH.setBackground(Color.white);
		DoLowPanelH.setBackground(Color.white);
		
		AcUppPanelH.add(AcUppLabel,BorderLayout.SOUTH);
		DoUppPanelH.add(DoUppLabel,BorderLayout.SOUTH);
		AcLowPanelH.add(AcLowLabel,BorderLayout.SOUTH);
		DoLowPanelH.add(DoLowLabel,BorderLayout.SOUTH);
		
		AcUppPanel.add(AcUppPanelH,BorderLayout.CENTER);
		DoUppPanel.add(DoUppPanelH,BorderLayout.CENTER);
		AcLowPanel.add(AcLowPanelH,BorderLayout.CENTER);
		DoLowPanel.add(DoLowPanelH,BorderLayout.CENTER);
		
		
		AcUppPanel.add(oldCluesAcross,BorderLayout.SOUTH);
		DoUppPanel.add(oldCluesDowns,BorderLayout.SOUTH);
		AcLowPanel.add(newCluesAcross,BorderLayout.SOUTH);
		DoLowPanel.add(newCluesDowns,BorderLayout.SOUTH);
		
		
		
		
		AcUppPanel.setVisible(true);
		DoUppPanel.setVisible(true);
		AcLowPanel.setVisible(true);
		DoLowPanel.setVisible(true);
		

		
		rightPanel = new JPanel(new BorderLayout());
		rightGrid = new JPanel(new GridLayout(2,2));
		upperPanel = new JPanel(new BorderLayout());
		lowerPanel = new JPanel(new BorderLayout());

		
		rightPanel.setBackground(Color.blue);
		upperPanel.setOpaque(false); // WARNING ********
		lowerPanel.setOpaque(true); // WARNING ********
		
		rightGrid.add(AcUppPanel, BorderLayout.EAST);
		rightGrid.add(DoUppPanel, BorderLayout.CENTER);
		rightGrid.add(AcLowPanel, BorderLayout.CENTER);
		rightGrid.add(DoLowPanel, BorderLayout.CENTER);
		
		date = new JTextField();
		
		date.setText(date1);
		date.setHorizontalAlignment(JTextField.LEFT);
		date.setEditable(false);
		date.setBackground(Color.WHITE);
		
		rightPanel.add(rightGrid);
		
		upperPanel.setVisible(true);
		lowerPanel.setVisible(true);
		rightGrid.setVisible(true);
		rightPanel.setVisible(true);

		frame.add(myGamePanel.main_panel, BorderLayout.CENTER);
		frame.add(rightPanel, BorderLayout.EAST);
		frame.add(bottomP, BorderLayout.SOUTH);
		
		frame.setVisible(true);
			
		
		for(int i = 0 ; i<coordinatesOfNumbers.size(); i++) {
			int x = coordinatesOfNumbers.get(i).getX();
			int y = coordinatesOfNumbers.get(i).getY();
				
			myGamePanel.adjustUpperNumber(i+1,convertCoordinates(x,y));		
		}
				
			
	for(int i = 0 ; i<25;i++) {
			for(int j = 0 ;j < coordinatesOfNumbers.size(); j++) {
				
				int convertedCoordinate = convertCoordinates(coordinatesOfNumbers.get(j).getX(), coordinatesOfNumbers.get(j).getY());
				
				if( i == convertedCoordinate) {
					break;
				}
				if(j == coordinatesOfNumbers.size() - 1) {
					myGamePanel.adjustUpperNumber(0,i);
				}
			}
		}
	
	for(int i = 0 ; i<25 ; i++) {
		myGamePanel.enterLetter(lettersAdjusted[i], i );
	
	
	frame.setSize(700 + rightPanel.getWidth(), 700);

	
	frame.setSize(frame.getWidth(), frame.getWidth() - rightPanel.getWidth()  + infos.getHeight());
	
	
	frame.setResizable(true);
	frame.getContentPane().revalidate();
	
}

	public int convertCoordinates(int x, int y) { // converts 2D coordinates to 1D suitable for panels array
		int result = x * 5 + y;
		return result;
	}

	public int getMaxWidthOfRightPanel() {
		return Math.max(upperPanel.getWidth(), lowerPanel.getWidth());
	}

	public int getMaxHeightOfRightPanel() {
		return Math.max(upperPanel.getHeight(), lowerPanel.getHeight());
	}

	public int getTotalHeight() {
		return upperPanel.getHeight() + lowerPanel.getHeight();
	}

	public int getAcrossWidthMax() {
		return Math.max(oldCluesAcross.getWidth(), newCluesAcross.getWidth());
	}

	public int getDownWidthMax() {
		return Math.max(oldCluesDowns.getWidth(), newCluesDowns.getWidth());
	}

}
