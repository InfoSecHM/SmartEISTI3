/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smarteisti.desgining.path.views;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.smarteisti.desgining.algorithms.Algorithm;
import com.smarteisti.desgining.algorithms.Algo;
import com.smarteisti.desgining.internal.AlgorithmListener;
import com.smarteisti.desgining.internal.DiscreteWorld;
import com.smarteisti.desgining.internal.Path;
import com.smarteisti.desgining.internal.PathNode;
import com.smarteisti.desgining.internal.WorldListener;

public class DrawingPanel extends JPanel implements MouseMotionListener, MouseListener, AlgorithmListener {

	private static final long serialVersionUID = 1L;
	ArrayList<WorldListener> observers = new ArrayList<WorldListener>();

	public final static int NONE = 0;
	public final static int DRAW = 1;
	public final static int ERASE = 2;
	public final static int START = 3;
	public final static int FINISH = 4;
	public final static int CANAPE = 5;
	public final static int CHAISE = 6;
	public final static int PORTE = 7;
    public final static int FENETRE = 8;
	public final static int PLANMAISON = 9;

	int x =0, y =0;
	int x1=0,  y1=0;
	int x2=0,  y2=0;
	int x3=0,  y3=0;
	int x4=0,  y4=0;
	int x5demo=0,y5demo=0;
	int x6demo=0,y6demo=0;

	private int pixelW = 0;
	private int pixelH = 0;
	private int drawMod = NONE;
	DiscreteWorld w = null;
	Path path = null;
	List<PathNode> sPath = null;

	private int count=0;
	private boolean showActualPath = true;
	private boolean showSmoothPath = true;
	private boolean showGrid = true;
	private boolean smoothBoundryPoints = false;
	private double weightData = 0.1;
	private double weightSmooth = 0.9;
	private int smoothingTimeout = 1000;
	private int cellDivisions = 5;

	// private Color color = Color.BLACK;


	public DrawingPanel(DiscreteWorld w, int width, int height) {
		setBounds(0, 0, width, height);
		setWorld(w);

		addMouseMotionListener(this);
		addMouseListener(this);
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {

		g.clearRect(0, 0, getWidth(), getHeight());
		Graphics2D g2 = (Graphics2D) g;




		// paint for block and open cells
		for (int i = 0; i < w.getRows(); i++) {
			for (int j = 0; j < w.getColumns(); j++) {


                    Rectangle2D rec = new Rectangle2D.Double(j * pixelW, i * pixelH, pixelW,pixelH);

                    if (w.isOpen(i, j)) {
                        g2.setColor(Color.white);
                        g2.fill(rec);
                    } else {

						g2.setColor(Color.BLACK);
						g2.fill(rec);


					}

                }

		}

		// draw path
		if (path != null && !path.isEmpty() && showActualPath) {
			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.RED);
			// Splitting path
			List<PathNode> robotPath = mapPathToScreen();
			PathNode last = robotPath.get(0);
			for (int i = 1; i < robotPath.size(); i++) {
				PathNode node = robotPath.get(i);
				int x1, y1, x2, y2;
				x1 = (int) last.getX();
				y1 = (int) last.getY();
				x2 = (int) node.getX();
				y2 = (int) node.getY();

				g2.drawLine(x1, y1, x2, y2);
				// System.out.println(x1 + " : " + y1 + " , " + x2 + " : " +
				// y2);

				last = node;

			}
		}

		// draw smoothed path
		if (sPath != null && !sPath.isEmpty() && showSmoothPath) {
			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.GREEN);
			// Splitting path

			PathNode last = sPath.get(0);
			for (int i = 1; i < sPath.size(); i++) {
				PathNode node = sPath.get(i);
				int x1, y1, x2, y2;
				x1 = (int) last.getX();
				y1 = (int) last.getY();
				x2 = (int) node.getX();
				y2 = (int) node.getY();

				g2.drawLine(x1, y1, x2, y2);
				// System.out.println(x1 + " : " + y1 + " , " + x2 + " : " +
				// y2);

				last = node;

			}
		}

		// draw start and finish cells
		g2.setColor(Color.RED);
		Rectangle2D rec = new Rectangle2D.Double(w.getStart().getyLoc() * pixelW, w.getStart().getxLoc() * pixelH,
				18, 13);
		g2.fill(rec);
		g2.setColor(Color.GREEN);
		rec = new Rectangle2D.Double(w.getGoal().getyLoc() * pixelW, w.getGoal().getxLoc() * pixelH, 18, 13);
		g2.fill(rec);

		// draw grid
		if (showGrid) {
			g2.setStroke(new BasicStroke(1));
			g2.setColor(Color.black);
			for (int y = 0; y <= w.getRows(); y++) {
				g.drawLine(0, y * pixelH, w.getColumns() * pixelW, y * pixelH);
			}
			for (int x = 0; x <= w.getColumns(); x++) {
				g.drawLine(x * pixelW, 0, x * pixelW, w.getRows() * pixelH);
			}
		}
	}

	/**
	 * This will map (x,y) location to screen to show path in center of cells
	 * 
	 * @return
	 */
	private List<PathNode> mapPathToScreen() {
		List<PathNode> newPath = new ArrayList<PathNode>();
		for (int i = path.size() - 1; i > -1; i--) {
			PathNode node = path.get(i);
			double x = node.getY() * pixelW + pixelW / 2;
			double y = node.getX() * pixelH + pixelH / 2;
			newPath.add(new PathNode(x, y));
		}
		return newPath;
	}

	public void smooth() {

		if (path != null && !path.isEmpty()) {
			List<PathNode> temp = splitPath(cellDivisions);
			sPath = Algo.smooth(temp, weightData, weightSmooth, 0.0001, smoothingTimeout,
					smoothBoundryPoints);
		} else {
			sPath = null;
		}
	}

	private List<PathNode> splitPath(double cnt) {
		List<PathNode> newPath = new ArrayList<PathNode>();
		List<PathNode> robotPath = mapPathToScreen();
		PathNode last = robotPath.get(0);
		for (int i = 1; i < robotPath.size(); i++) {
			PathNode node = robotPath.get(i);
			int x1, y1, x2, y2;
			x1 = (int) last.getX();
			y1 = (int) last.getY();
			x2 = (int) node.getX();
			y2 = (int) node.getY();
			for (int j = 0; j <= cnt; j++) {
				int newx = (int) (x1 + (x2 - x1) * j / cnt);
				int newy = (int) (y1 + (y2 - y1) * j / cnt);
				newPath.add(new PathNode(newx, newy));
			}
			last = node;
		}
		// System.out.println("Old Path = " + path.size() + " , New Path = " +
		// newPath.size());
		return newPath;
	}

	public void changeWorld(MouseEvent e, int count) {
		x = e.getX() / pixelW;
		y = e.getY() / pixelH;

        try {

			if(drawMod==PLANMAISON)
			{

                if(count==1) {
					y1 = e.getX() / pixelW;
					x1 = e.getY() / pixelH;
                                                                           // récupére les coordonnées du curseur de la souris en y
                        System.out.println("x1 et y1 sont egales à"+x1+"/"+y1);



				}else

				if(count==2) {
                    y2 = e.getX() / pixelW;
                    x2 = e.getY() / pixelH;

                    x5demo = x2;
                    y5demo = y2;
                    System.out.println("x2 et y2 sont egales à" + x2 + "/" + y2);


                    for (int a = x1; a <= x5demo; a++) {
                        System.out.println("a is now " + a);


                        if (a == x5demo) {
                            for (int b = y1; b <= y5demo; b++) {
                                System.out.println("b is now " + b);


                                w.setStatus(x5demo, b, DiscreteWorld.BLOCK);
                            }
                        } else
                            w.setStatus(a, y1, DiscreteWorld.BLOCK);
                    }


                    for (int a = y1; a <= y5demo; a++) {
                        System.out.println("a is now " + a);

                        if (a == y5demo) {
                            for (int c = x1; c <= x5demo; c++) {
                                System.out.println("c is now " + c);
                                w.setStatus(c, y5demo, DiscreteWorld.BLOCK);

                            }

                        } else

                            w.setStatus(x1, a, DiscreteWorld.BLOCK);
                    }


                }}

			else

            if(drawMod==CANAPE)
            {
                if (!w.isGoal(y, x) && !w.isStart(y, x)) {

                     {
                              w.setStatus(y, x, DiscreteWorld.BLOCK);
                              w.setStatus(y+1, x+1, DiscreteWorld.BLOCK);
                              w.setStatus(y+1, x, DiscreteWorld.BLOCK);
                              w.setStatus(y+1, x+3, DiscreteWorld.BLOCK);
                              w.setStatus(y, x+1, DiscreteWorld.BLOCK);
                              w.setStatus(y, x+2, DiscreteWorld.BLOCK);
                              w.setStatus(y+2, x, DiscreteWorld.BLOCK);
                              w.setStatus(y+2, x+1, DiscreteWorld.BLOCK);
                              w.setStatus(y+2, x+2, DiscreteWorld.BLOCK);
                              w.setStatus(y+1, x+2, DiscreteWorld.BLOCK);


                     }
            }}else

            if(drawMod==CHAISE)
            {
                if (!w.isGoal(y, x) && !w.isStart(y, x)) {

                    {
                        w.setStatus(y, x, DiscreteWorld.BLOCK);
                        w.setStatus(y+1, x+1, DiscreteWorld.BLOCK);
                        w.setStatus(y+1, x, DiscreteWorld.BLOCK);
                        w.setStatus(y, x+1, DiscreteWorld.BLOCK);
                        w.setStatus(y, x+2, DiscreteWorld.BLOCK);
                        w.setStatus(y+2, x, DiscreteWorld.BLOCK);
                        w.setStatus(y+2, x+1, DiscreteWorld.BLOCK);
                        w.setStatus(y+2, x+2, DiscreteWorld.BLOCK);
                        w.setStatus(y+1, x+2, DiscreteWorld.BLOCK);
                        w.setStatus(y+3, x+2, DiscreteWorld.BLOCK);



                    }
                }}else


            if(drawMod==PORTE)
            {
                if (!w.isGoal(y, x) && !w.isStart(y, x)) {

                    {
                        w.setStatus(y, x, DiscreteWorld.BLOCK);
                        w.setStatus(y+1, x+1, DiscreteWorld.BLOCK);
                        w.setStatus(y+1, x, DiscreteWorld.BLOCK);
                        w.setStatus(y, x+1, DiscreteWorld.BLOCK);
                        w.setStatus(y, x+2, DiscreteWorld.BLOCK);
                        w.setStatus(y+2, x, DiscreteWorld.BLOCK);
                        w.setStatus(y+2, x+1, DiscreteWorld.BLOCK);
                        w.setStatus(y+2, x+2, DiscreteWorld.BLOCK);



                    }
                }}else

            if(drawMod==FENETRE)
            {
                if (!w.isGoal(y, x) && !w.isStart(y, x)) {

                    {
                        w.setStatus(y, x, DiscreteWorld.BLOCK);
                        w.setStatus(y+1, x+1, DiscreteWorld.BLOCK);
                        w.setStatus(y+1, x, DiscreteWorld.BLOCK);
                        w.setStatus(y, x+1, DiscreteWorld.BLOCK);



                    }
                }}

            else if (drawMod == ERASE) {
                w.setStatus(y, x, DiscreteWorld.OPEN);
                System.out.println("erase");

            } else if (drawMod == START && !w.isGoal(y, x)) {
                w.setStartNode(y, x);
                w.setStatus(y, x, DiscreteWorld.OPEN);
            } else if (drawMod == FINISH && !w.isStart(y, x)) {
                w.setGoalNode(y, x);
                w.setStatus(y, x, DiscreteWorld.OPEN);
            }

            if (x >= 0 && x < w.getColumns() && y >= 0 && y < w.getRows() && drawMod != NONE) {
                if (drawMod == DRAW) {

                    if (!w.isGoal(y, x) && !w.isStart(y, x)) {

                        w.setStatus(y, x, DiscreteWorld.BLOCK);



                    }

                    }
                }

                repaint();
                // System.out.println(w.printWorld());

		} catch (ArrayIndexOutOfBoundsException ex) {
			// System.out.println(ex.getMessage());
		}
	}

	public void setWorldSize(int rows, int columns) {
		DiscreteWorld newWorld = new DiscreteWorld(columns, rows);
		for (int i = 0; i < Math.min(rows, w.getRows()); i++) {
			System.arraycopy(w.getGrid()[i], 0, newWorld.getGrid()[i], 0, Math.min(columns, w.getColumns()));
		}
		setWorld(newWorld);
		repaint();
	}

	private void setWorld(DiscreteWorld w) {
		this.w = w;
		this.pixelW = getWidth() / w.getColumns();
		this.pixelH = getHeight() / w.getRows();
	}

	public void setDrawingMod(int mod) {
		this.drawMod = mod;
		if (this.drawMod == NONE) {
			setWorld(w);
			for (Iterator<WorldListener> iter = observers.iterator(); iter.hasNext();) {
				WorldListener type = iter.next();
				type.worldUpdate(w);
			}
		} else {
			setPath(null);
		}
	}

	public void setPath(Path path) {
		this.path = path;
		smooth();
		repaint();
	}

	public void clear() {
		for (int i = 0; i < w.getRows(); i++) {
			for (int j = 0; j < w.getColumns(); j++) {
				w.setStatus(i, j, DiscreteWorld.OPEN);
			}
			count=0;
		}
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	    if(drawMod==PLANMAISON) {
            count++;
            changeWorld(e, count);
        }
        else
        {
            changeWorld(e, count);

        }
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
        if(drawMod==PLANMAISON) {
            count++;
            changeWorld(e, count);
        }
        else
        {
            changeWorld(e, count);

        }
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// changeWorld(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	public void setShowActualPath(boolean showActualPath) {
		this.showActualPath = showActualPath;
	}

	public void setShowSmoothPath(boolean showSmoothPath) {
		this.showSmoothPath = showSmoothPath;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}

	public void setSmoothBoundryPoints(boolean smoothBoundryPoints) {
		this.smoothBoundryPoints = smoothBoundryPoints;
		smooth();
	}

	public void setCellDivisions(int divisions) {
		this.cellDivisions = divisions;
	}

	public void setSmotthingTimeout(int smoothingTimeout) {
		this.smoothingTimeout = smoothingTimeout;
	}

	public void setWeightData(double weightData) {
		this.weightData = weightData;
	}

	public void setWeightSmooth(double weightSmooth) {
		this.weightSmooth = weightSmooth;
	}

	@Override
	public void algorithmUpdate(Algorithm algorithm) {
		setPath(algorithm.getPath());
	}

	public void addWorldObserver(WorldListener listener) {
		observers.add(listener);
	}

}
