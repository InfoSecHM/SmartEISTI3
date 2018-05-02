/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.smarteisti.desgining.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.smarteisti.desgining.internal.AlgorithmListener;
import com.smarteisti.internal.RPanel;
import com.smarteisti.desgining.algorithms.Algorithm;
import com.smarteisti.utils.Util;

public class StatisticsPanel extends RPanel implements AlgorithmListener {

	final static double puissace=Math.pow(10,-6);
	private static final long serialVersionUID = 1L;
	DecimalFormat numberFormat = new DecimalFormat("#.00");

	private double reponse_duration;
	private String rep;
	String strExplored = " Exploré", strBlocked = " Bloqué", strUnExplored = " Pas exploré", strTotal = " Total",
			strPathSize = " Longueur du parcours", strResult = " Résultat", strInstances = " Temps du parcours";
	JLabel lblExplored, lblBlocked, lblUnExplored, lblTotal, lblPathSize, lblResult, lblInstances;

	public StatisticsPanel(int width, int height, String label) {
		super(width, height, label);

		setLayoutMgr(new BorderLayout());

		JPanel pnl = new JPanel();
		createOutputPanelContents(pnl);

		add(pnl, BorderLayout.NORTH);
	}

	private void createOutputPanelContents(JPanel pnlOutput) {

		pnlOutput.setLayout(new GridLayout(8, 1, 5, 3));

		pnlOutput.add(new JLabel(strExplored));
		lblExplored = new JLabel();
		pnlOutput.add(lblExplored);
		pnlOutput.add(new JLabel(strBlocked));
		lblBlocked = new JLabel();
		pnlOutput.add(lblBlocked);
		pnlOutput.add(new JLabel(strUnExplored));
		lblUnExplored = new JLabel();
		pnlOutput.add(lblUnExplored);
		pnlOutput.add(new JLabel(strTotal));
		lblTotal = new JLabel();
		pnlOutput.add(lblTotal);
		pnlOutput.add(new JLabel(strPathSize));
		lblPathSize = new JLabel();
		pnlOutput.add(lblPathSize);
		pnlOutput.add(new JLabel(strInstances));
		lblInstances = new JLabel();
		pnlOutput.add(lblInstances);
		pnlOutput.add(new JLabel(strResult));
		lblResult = new JLabel();
		pnlOutput.add(lblResult);
		lblResult.setForeground(Color.RED);

	}

	@Override
	public void algorithmUpdate(Algorithm algorithm) {
		if (algorithm != null) {


			System.out.println("puissance"+puissace);
			lblExplored.setText(Util.padRight(Integer.toString(algorithm.getExploredSize()), 20));
			lblUnExplored.setText(Util.padRight(Integer.toString(algorithm.getUnExploredSize()), 20));
			lblBlocked.setText(Util.padRight(Integer.toString(algorithm.getBlockedSize()), 20));
			lblTotal.setText(Util.padRight(Integer.toString(algorithm.getTotalSize()), 20));
			lblPathSize.setText(Util.padRight(Integer.toString(algorithm.getPathSize()), 20));
			lblResult.setText(Util.padRight(algorithm.getResult(), 20));
			reponse_duration=algorithm.getDuration()*puissace;
			rep=numberFormat.format(reponse_duration);


			lblInstances.setText(Util.padRight((rep+" ms"), 20));
		} else {
			lblExplored.setText("0");
			lblUnExplored.setText("0");
			lblBlocked.setText("0");
			lblTotal.setText("0");
			lblPathSize.setText("0");
			lblResult.setText("NA");
			lblInstances.setText("0 ms");
			lblTotal.setText("0");
		}

	}

}
