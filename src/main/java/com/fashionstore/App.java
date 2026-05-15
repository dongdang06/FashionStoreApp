 
package com.fashionstore;

import javax.swing.SwingUtilities;

import com.fashionstore.view.MainAppFrame;

public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			MainAppFrame frame = new MainAppFrame();
			frame.setVisible(true);
		});
	}
}

