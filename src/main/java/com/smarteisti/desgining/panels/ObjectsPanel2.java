package com.smarteisti.desgining.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.smarteisti.desgining.path.views.DrawingPanel;
import com.smarteisti.desgining.path.views.DrawingPanel2;
import com.smarteisti.internal.RPanel;

public class ObjectsPanel2 extends RPanel implements ActionListener, ChangeListener {

    private static final long serialVersionUID = 1L;
    private JButton clearButton = new JButton("Vider");
    DrawingPanel2 drawingPanel = null;
    JRadioButton rbCanape = new JRadioButton("Canapé");
    JRadioButton rbChaise = new JRadioButton("Chaise");
    JRadioButton rbPorte = new JRadioButton("Porte");
    JRadioButton rbFenetre = new JRadioButton("Fenêtre");
    JToggleButton tbEditWorld = new JToggleButton("Ajouter obstacles");

    private int drawMod = DrawingPanel2.NONE;

    public void setDrawingPanel(DrawingPanel2 observer) {
        this.drawingPanel = observer;
        this.drawingPanel.setDrawingMod(drawMod);

    }
    public ObjectsPanel2(int width, int height, String label) {
        super(width, height, label);

        setLayoutMgr(new BorderLayout());

        JPanel pnl = new JPanel();
        createOutputPanelContents(pnl);

        add(pnl, BorderLayout.NORTH);
    }

    private void createOutputPanelContents(JPanel pnlOutput) {

        pnlOutput.setLayout(new GridLayout(4, 2, 5, 3));
        tbEditWorld.addActionListener(this);
        tbEditWorld.setBackground(Color.PINK);
        pnlOutput.add(tbEditWorld);

        clearButton.addActionListener(this);
        clearButton.setEnabled(tbEditWorld.isSelected());
        clearButton.setVisible(false);
        pnlOutput.add(clearButton);

        rbCanape.addActionListener(this);
        rbCanape.setSelected(drawMod == DrawingPanel2.NONE);
        rbCanape.setEnabled(tbEditWorld.isSelected());
        pnlOutput.add(rbCanape);

        rbChaise.addActionListener(this);
        rbChaise.setSelected(drawMod == DrawingPanel2.NONE);
        rbChaise.setEnabled(tbEditWorld.isSelected());
        pnlOutput.add(rbChaise);

        rbPorte.addActionListener(this);
        rbPorte.setSelected(drawMod == DrawingPanel2.NONE);
        rbPorte.setEnabled(tbEditWorld.isSelected());
        pnlOutput.add(rbPorte);

        rbFenetre.addActionListener(this);
        rbFenetre.setSelected(drawMod == DrawingPanel2.NONE);
        rbFenetre.setEnabled(tbEditWorld.isSelected());
        pnlOutput.add(rbFenetre);

        ButtonGroup gp2 = new ButtonGroup();
        gp2.add(rbCanape);
        gp2.add(rbChaise);
        gp2.add(rbPorte);
        gp2.add(rbFenetre);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();

        if (obj.equals(clearButton)) {
            if (tbEditWorld.isSelected()) {
                drawingPanel.clear();
            }
        } else if (obj.equals(rbCanape)) {
            drawMod = tbEditWorld.isSelected() ? DrawingPanel2.CANAPE : DrawingPanel2.NONE;
        } else if (obj.equals(rbChaise)) {
            drawMod = tbEditWorld.isSelected() ? DrawingPanel2.CHAISE : DrawingPanel2.NONE;
        } else if (obj.equals(rbPorte)) {
            drawMod = tbEditWorld.isSelected() ? DrawingPanel2.PORTE : DrawingPanel2.NONE;
        } else if (obj.equals(rbFenetre)) {
            drawMod = tbEditWorld.isSelected() ? DrawingPanel2.FENETRE : DrawingPanel2.NONE;
        }

        else if (obj.equals(tbEditWorld)) {
            clearButton.setEnabled(tbEditWorld.isSelected());
            rbChaise.setEnabled(tbEditWorld.isSelected());
            rbCanape.setEnabled(tbEditWorld.isSelected());
            rbFenetre.setEnabled(tbEditWorld.isSelected());
            rbPorte.setEnabled(tbEditWorld.isSelected());



            if (tbEditWorld.isSelected()) {
                rbCanape.setSelected(true);
                drawMod = DrawingPanel2.CANAPE;
            } else {
                drawMod = DrawingPanel2.NONE;
            }
        }
        drawingPanel.setDrawingMod(drawMod);
    }


    @Override
    public void stateChanged(ChangeEvent e) {
        drawingPanel.setWorldSize(100,100);
    }
}