
import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

public class GamePanel {
	JPanel main_panel;
	JPanel[] panels;
	Border border;
	// JTextField groupName;

	GamePanel() {
		main_panel = new JPanel();
		main_panel.setBackground(Color.white);
		panels = new JPanel[25];
		border = BorderFactory.createLineBorder(Color.BLACK, 1);

		for (int i = 0; i < 25; i++) {
			GridLayout gridLayout1 = new GridLayout(3, 3);
			panels[i] = new JPanel(gridLayout1);
			panels[i].setBackground(Color.WHITE);
			panels[i].setBorder(border);
			panels[i].setVisible(true);
			main_panel.add(panels[i]);
		}

		GridLayout gridLayout = new GridLayout(5, 5);
		main_panel.setLayout(gridLayout);
		main_panel.setVisible(true);
	}

	int labelSize = 20;

	public void adjustUpperNumber(int num, int index) {

		if (num == 0) {

			JLabel label1 = new JLabel("");
			label1.setFont(new Font("Arial", Font.PLAIN, labelSize));
			label1.setHorizontalAlignment(SwingConstants.CENTER);
			label1.setVerticalAlignment(SwingConstants.CENTER);

			panels[index].add(label1);

			JLabel label2 = new JLabel("");
			label2.setFont(new Font("Arial", Font.PLAIN, labelSize));
			label2.setHorizontalAlignment(SwingConstants.CENTER);
			label2.setVerticalAlignment(SwingConstants.CENTER);

			panels[index].add(label2);

			JLabel label3 = new JLabel("");
			label3.setFont(new Font("Arial", Font.PLAIN, labelSize));
			label3.setHorizontalAlignment(SwingConstants.CENTER);
			label3.setVerticalAlignment(SwingConstants.CENTER);

			panels[index].add(label3);

			JLabel label4 = new JLabel("");
			label4.setFont(new Font("Arial", Font.PLAIN, labelSize));
			label4.setHorizontalAlignment(SwingConstants.CENTER);
			label4.setVerticalAlignment(SwingConstants.CENTER);

			panels[index].add(label4);
		} else {

			JLabel label5 = new JLabel(Integer.toString(num));
			label5.setFont(new Font("Arial", Font.PLAIN, labelSize));
			label5.setForeground(Color.BLACK);

			label5.setHorizontalAlignment(SwingConstants.CENTER);
			label5.setVerticalAlignment(SwingConstants.CENTER);

			panels[index].add(label5);

			JLabel label6 = new JLabel("");
			label6.setHorizontalAlignment(SwingConstants.CENTER);
			label6.setVerticalAlignment(SwingConstants.CENTER);

			panels[index].add(label6);

			JLabel label7 = new JLabel("");
			label7.setHorizontalAlignment(SwingConstants.CENTER);
			label7.setVerticalAlignment(SwingConstants.CENTER);

			panels[index].add(label7);

			JLabel label8 = new JLabel("");
			label8.setHorizontalAlignment(SwingConstants.CENTER);
			label8.setVerticalAlignment(SwingConstants.CENTER);

			panels[index].add(label8);

		}
	}

	public void enterLetter(String letter, int index) {

		if (letter.charAt(0) == '#') {
			blackCell(index);
		} else {

			JLabel label = new JLabel(letter);
			label.setFont(new Font("Arial", Font.PLAIN, 40));
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setVerticalAlignment(SwingConstants.CENTER);

			panels[index].add(label);

			JLabel label2 = new JLabel("");
			label2.setHorizontalAlignment(SwingConstants.CENTER);
			label2.setVerticalAlignment(SwingConstants.CENTER);

			panels[index].add(label2);

			JLabel label3 = new JLabel("");
			label3.setHorizontalAlignment(SwingConstants.CENTER);
			label3.setVerticalAlignment(SwingConstants.CENTER);

			panels[index].add(label3);
		}
	}

	public void blackCell(int index) {
		panels[index].setBackground(Color.BLACK);
	}

}