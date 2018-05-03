/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smarteisti.desgining.path.views;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DrawingControlPanel2 extends JPanel implements ActionListener, ChangeListener {

    private static final long serialVersionUID = 1L;

    public String status;
    DrawingPanel2 drawingPanel = null;

    private JButton clearButton = new JButton("Vider");
    JRadioButton rbtest = new JRadioButton("Dessiner plan");
    JRadioButton rbErase = new JRadioButton("Effacer");
    JRadioButton rbStart = new JRadioButton("Point départ");
    JRadioButton rbFinish = new JRadioButton("Point arrivé");
    JRadioButton rbDraw = new JRadioButton("Dessiner");
    JToggleButton tbEditWorld = new JToggleButton("Modifier");

    JSpinner spnRows = new JSpinner();
    JSpinner spnColumns = new JSpinner();

    private int drawMod = DrawingPanel2.NONE;

    public void setDrawingPanel(DrawingPanel2 observer) {
        this.drawingPanel = observer;
        this.drawingPanel.setDrawingMod(drawMod);

    }

    public DrawingControlPanel2(int rows, int columns) {
        setLayout(new FlowLayout());

        tbEditWorld.addActionListener(this);
        tbEditWorld.setBackground(Color.RED);
        add(tbEditWorld);

        clearButton.addActionListener(this);
        clearButton.setEnabled(tbEditWorld.isSelected());
        add(clearButton);

        rbDraw.addActionListener(this);
        rbDraw.setSelected(drawMod == DrawingPanel2.NONE);
        rbDraw.setEnabled(tbEditWorld.isSelected());
        add(rbDraw);
        rbDraw.setVisible(false);

        rbtest.addActionListener(this);
        rbtest.setSelected(drawMod == DrawingPanel2.NONE);
        rbtest.setEnabled(tbEditWorld.isSelected());
        add(rbtest);

        rbErase.addActionListener(this);
        rbErase.setSelected(drawMod == DrawingPanel2.ERASE);
        rbErase.setEnabled(tbEditWorld.isSelected());
        add(rbErase);

        rbStart.addActionListener(this);
        rbStart.setSelected(drawMod == DrawingPanel2.START);
        rbStart.setEnabled(tbEditWorld.isSelected());
        add(rbStart);

        rbFinish.addActionListener(this);
        rbFinish.setSelected(drawMod == DrawingPanel2.FINISH);
        rbFinish.setEnabled(tbEditWorld.isSelected());
        add(rbFinish);




        ButtonGroup gp = new ButtonGroup();
        gp.add(rbDraw);
        gp.add(rbErase);
        gp.add(rbStart);
        gp.add(rbFinish);
        gp.add(rbtest);


		/*spnRows.setModel(new SpinnerNumberModel(rows, 100, 100, 1));
		spnRows.addChangeListener(this);
		spnRows.setEnabled(false);
		add(spnRows);

		spnColumns.setModel(new SpinnerNumberModel(columns, 100, 100, 1));
		spnColumns.addChangeListener(this);
		spnColumns.setEnabled(false);
		add(spnColumns); */


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();

        if (obj.equals(clearButton)) {
            if (tbEditWorld.isSelected()) {
                drawingPanel.clear();
            }
        } else if (obj.equals(rbDraw)) {
            drawMod = tbEditWorld.isSelected() ? DrawingPanel2.DRAW : DrawingPanel2.NONE;
        } else if (obj.equals(rbErase)) {
            drawMod = tbEditWorld.isSelected() ? DrawingPanel2.ERASE : DrawingPanel2.NONE;
        } else if (obj.equals(rbStart)) {
            drawMod = tbEditWorld.isSelected() ? DrawingPanel2.START : DrawingPanel2.NONE;
        } else if (obj.equals(rbFinish)) {
            drawMod = tbEditWorld.isSelected() ? DrawingPanel2.FINISH : DrawingPanel2.NONE;
        }
        else if (obj.equals(rbtest)) {
            drawMod = tbEditWorld.isSelected() ? DrawingPanel2.PLANMAISON : DrawingPanel2.NONE;
        }

        else if (obj.equals(tbEditWorld)) {
            spnRows.setEnabled(tbEditWorld.isSelected());
            spnColumns.setEnabled(tbEditWorld.isSelected());
            clearButton.setEnabled(tbEditWorld.isSelected());
            rbDraw.setEnabled(tbEditWorld.isSelected());
            rbErase.setEnabled(tbEditWorld.isSelected());
            rbStart.setEnabled(tbEditWorld.isSelected());
            rbFinish.setEnabled(tbEditWorld.isSelected());
            rbtest.setEnabled(tbEditWorld.isSelected());



            if (tbEditWorld.isSelected()) {
                rbDraw.setSelected(true);
                drawMod = DrawingPanel2.DRAW;
            } else {
                drawMod = DrawingPanel2.NONE;
            }
        }
        drawingPanel.setDrawingMod(drawMod);
    }

    @Override
    public void stateChanged(ChangeEvent arg0) {
        drawingPanel.setWorldSize(Integer.parseInt(spnRows.getValue().toString()),
                Integer.parseInt(spnColumns.getValue().toString()));
    }

}
