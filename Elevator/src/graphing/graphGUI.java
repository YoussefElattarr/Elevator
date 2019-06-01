package graphing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Enums.Direction;
import model.Elevator;
import model.Elevator.MemoryGrapher;
import model.Elevator.ProcessorGrapher;

public class graphGUI extends JFrame implements ActionListener {
	private JButton memgraph;
	private JButton prograph;
	private JPanel panel;
	/*ExecutorService pool = Executors.newFixedThreadPool(5);
	DrawGraph memorygraph=new DrawGraph(MemoryGrapher.seconds, MemoryGrapher.usage);
	DrawGraph processorgraph=new DrawGraph(MemoryGrapher.seconds, MemoryGrapher.usage);
	*/public graphGUI() {
		setTitle("graphing buttons ");
		setBounds(500,500 , 750, 100);
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(this.getWidth(), getHeight()));
		add(panel);
		memgraph = new JButton("memory");
		memgraph.addActionListener(this);
		panel.add(memgraph, BorderLayout.WEST);
		prograph = new JButton("processor");
		prograph.addActionListener(this);
		panel.add(prograph, BorderLayout.EAST);
		revalidate();
		this.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) e.getSource();
		switch (button.getText()) {
		case "memory":
			DrawGraph memory=new DrawGraph(MemoryGrapher.seconds, MemoryGrapher.usage);
			memory.createAndShowGui(MemoryGrapher.seconds, MemoryGrapher.usage);
			break;
		case "processor":
			DrawGraph processor=new DrawGraph(ProcessorGrapher.seconds, ProcessorGrapher.usage);
			processor.createAndShowGui(ProcessorGrapher.seconds, ProcessorGrapher.usage);
			break;
		}
	}

}
