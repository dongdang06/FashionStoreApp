 
package com.fashionstore;

import javax.swing.SwingUtilities;

import com.fashionstore.view.auth.DangNhapFrame;

public class App {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			DangNhapFrame frame = new DangNhapFrame();
			frame.setVisible(true);
		});
	}
}

