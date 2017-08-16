package com.swing;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;

public class RegeditPanel extends JPanel {
	private JTextField textField;
	private JTextField textField_1;

	/**
	 * Create the panel.
	 */
	public RegeditPanel() {
		setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(52, 70, 154, 21);
		add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setBounds(52, 143, 154, 21);
		add(textField_1);
		textField_1.setColumns(10);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.setBounds(52, 207, 93, 23);
		add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("New button");
		btnNewButton_1.setBounds(188, 207, 93, 23);
		add(btnNewButton_1);

	}
}
