package com.smarteisti.main;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;

import javax.swing.*;


import com.smarteisti.desgining.path.views.PathSmoothingView2;
import com.smarteisti.internal.RootView;

import com.smarteisti.desgining.path.views.PathSmoothingView;

public class EISTI extends JFrame implements ActionListener {
	private final static String APPLICATION_TITLE = "SmartEISTI";

	final static String version = " 1.0";
	private static final long serialVersionUID = 1L;

	private JDesktopPane desk;

	JMenuItem miExit;
	JMenuItem miAbout;

	public EISTI(String title) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setExtendedState(Frame.MAXIMIZED_BOTH);
		String os = System.getProperty("os.name");
		try {
			if (os != null && os.contains("Windows")) {
				
			} else {

				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	
		desk = new JDesktopPane();
		setContentPane(desk);

		// Install our custom desktop manager.
		desk.setDesktopManager(new SampleDesktopMgr());

		createMenuBar();
		loadBackgroundImage();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (e.getID() == WindowEvent.WINDOW_CLOSING) {
					JInternalFrame[] frms = desk.getAllFrames();
					for (int i = 0; i < frms.length; i++) {
						try {
							if (frms[i] instanceof RootView) {
								((RootView) frms[i]).setClosed(true);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {

			}
		});
	}

	protected void createMenuBar() {
		JMenuBar mb = new JMenuBar();

		JMenu mnFile = new JMenu("Fichier");

		mnFile.add(new TileAction(desk)); 

		miExit = new JMenuItem("Quitter");
		miExit.addActionListener(this);
		mnFile.add(miExit);

		
		JMenu mnOptimization = new JMenu("Parcours");

		JInternalFrame cheminamain = new PathSmoothingView();
		mnOptimization.add(new AddFrameAction("Chemin à main levée", cheminamain));
		desk.add(cheminamain);

		JInternalFrame planmaison = new PathSmoothingView2();
		mnOptimization.add(new AddFrameAction("Plan maison", planmaison));
		desk.add(planmaison);
		
		JMenu mnHelp = new JMenu("Aide");

		miAbout = new JMenuItem("À propos");
		miAbout.addActionListener(this);
		mnHelp.add(miAbout);


		setJMenuBar(mb);
		mb.add(mnFile);
		mb.add(mnOptimization);
		mb.add(mnHelp);
	}

	protected void loadBackgroundImage() {
		ImageIcon icon = new ImageIcon(getClass().getResource("/images/back.png" +
				""));
		JLabel l = new JLabel(icon);
		l.setBounds(0, 0, icon.getIconWidth(), icon.getIconHeight());

		desk.add(l, new Integer(Integer.MIN_VALUE));
	}

	class AddFrameAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		JInternalFrame frame = null;

		public AddFrameAction(String name, JInternalFrame frame) {
			super(name);
			this.frame = frame;
		}

		@Override
		public void actionPerformed(ActionEvent ev) {
			RootView view = (RootView) frame;
			view.setVisible(true);
			try {
				view.setSelected(true);
				if (!view.isInit)
					view.initGUI();
			} catch (PropertyVetoException e) {
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj instanceof JMenuItem) {
			if (obj.equals(miExit)) {
				System.exit(0);
			} else if (obj.equals(miAbout)) {
				showus();

			}
		}
	}

	private void showus() {
		Icon ico = new ImageIcon(getClass().getResource("/images/eistilogo.jpg"));
		JOptionPane.showOptionDialog(null, "Ce projet est développé par : \n Mahdi HRAYBI \n Julien CHIANG \n Antoine DENIS \n Alexis DALEY \n Thomas LAFAURIE", "SmartEISTI", JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, ico, new Object[] {}, null);

	}

	public static void main(String[] args) {

		EISTI robot = new EISTI(APPLICATION_TITLE + version);

		Toolkit tool = Toolkit.getDefaultToolkit();
		robot.setSize(tool.getScreenSize());
		robot.setVisible(true);


	}
}

class TileAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	private JDesktopPane desk; // the desktop to work with

	public TileAction(JDesktopPane desk) {
		super("Afficher toutes les fenêtres");
		this.desk = desk;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {

		// How many frames do we have?
		JInternalFrame[] allframes = desk.getAllFrames();
		int count = allframes.length;
		if (count == 0)
			return;

		// Determine the necessary grid size
		int sqrt = (int) Math.sqrt(count);
		int rows = sqrt;
		int cols = sqrt;
		if (rows * cols < count) {
			cols++;
			if (rows * cols < count) {
				rows++;
			}
		}

		// Define some initial values for size & location.
		Dimension size = desk.getSize();

		int w = size.width / cols;
		int h = size.height / rows;
		int x = 0;
		int y = 0;

		// Iterate over the frames, deiconifying any iconified frames and then
		// relocating & resizing each.
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
				JInternalFrame f = allframes[(i * cols) + j];

				if (!f.isClosed() && f.isIcon()) {
					try {
						f.setIcon(false);
					} catch (PropertyVetoException ignored) {
					}
				}

				desk.getDesktopManager().resizeFrame(f, x, y, w, h);
				x += w;
			}
			y += h; // start the next row
			x = 0;
		}
	}

}

// SampleDesktopMgr.java
// A DesktopManager that keeps its frames inside the desktop.

class SampleDesktopMgr extends DefaultDesktopManager {

	private static final long serialVersionUID = 1L;

	// This is called anytime a frame is moved. This
	// implementation keeps the frame from leaving the desktop.
	@Override
	public void dragFrame(JComponent f, int x, int y) {
		if (f instanceof JInternalFrame) { // Deal only w/internal frames
			JInternalFrame frame = (JInternalFrame) f;
			JDesktopPane desk = frame.getDesktopPane();
			Dimension d = desk.getSize();

			// Nothing all that fancy below, just figuring out how to adjust
			// to keep the frame on the desktop.
			if (x < 0) { // too far left?
				x = 0; // flush against the left side
			} else {
				if (x + frame.getWidth() > d.width) { // too far right?
					x = d.width - frame.getWidth(); // flush against right side
				}
			}
			if (y < 0) { // too high?
				y = 0; // flush against the top
			} else {
				if (y + frame.getHeight() > d.height) { // too low?
					y = d.height - frame.getHeight(); // flush against the
					// bottom
				}
			}
		}

		// Pass along the (possibly cropped) values to the normal drag handler.
		super.dragFrame(f, x, y);
	}
}