package InputOutput;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import Enums.Direction;
import Enums.State;
import Processes.EmergencyStop;
import Processes.MoveTo;
import Processes.PCB;
import Processes.Process;
import model.Elevator;

public class Inside extends JFrame implements Runnable, ActionListener {
	private JPanel panel;
	ArrayList<JButton> buttons;
	public JTextArea curFloor;
	public JTextArea curWeight;
	public final JPanel blocked = new JPanel();

	public Inside() {
		buttons = new ArrayList<JButton>();
		setTitle("Inside Panel");
		setBounds(1000, 50, 200, 400);
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(this.getWidth(), 200));
		add(panel);
		for (int i = 0; i < 4; i++) {
			JButton number = new JButton(i + "");
			buttons.add(number);
			number.addActionListener(this);
			panel.add(number);
		}
		JButton stop = new JButton("stop");
		buttons.add(stop);
		stop.addActionListener(this);
		panel.add(stop);
		
		JButton opendoor = new JButton("open door");
		buttons.add(opendoor);
		opendoor.addActionListener(this);
		panel.add(opendoor);
		
		JButton closedoor = new JButton("close door");
		buttons.add(closedoor);
		closedoor.addActionListener(this);
		panel.add(closedoor);
		
		JButton getOut = new JButton("get out");
		buttons.add(getOut);
		getOut.addActionListener(this);
		panel.add(getOut);
		
		curFloor = new JTextArea();
		curFloor.setEditable(false);
		curFloor.setPreferredSize(new Dimension(20, 20));
		curFloor.setText("0");
		panel.add(curFloor);
		
		curWeight = new JTextArea();
		curWeight.setEditable(false);
		curWeight.setPreferredSize(new Dimension(30, 20));
		curWeight.setText("0");
		panel.add(curWeight);
		
		blocked.setPreferredSize(new Dimension(this.getWidth(), 200));
		add(blocked, BorderLayout.SOUTH);
		blocked.setBackground(Color.GREEN);
		
		revalidate();
		this.setVisible(true);
	}

	public static void emStop() {
		// emergencyStop=!emergencyStop;
		int priority = 90;
		PCB pcb = new PCB(Elevator.processCount++, priority, Enums.State.NEW);
		Process process = new EmergencyStop(pcb);
		if (process.pcb.state == Enums.State.READY) {
			Elevator.processes.add(process);
		}
		Elevator.decode();
	}

	public void moveTo(int requestedFloor) {
		Direction requestedDir;
		if (requestedFloor > Elevator.currentFloor)
			requestedDir = Direction.UP;
		else
			requestedDir = Direction.DOWN;
		int priority = 0;
		if (requestedFloor > Elevator.currentFloor && Elevator.dir == Direction.UP && Elevator.dir==requestedDir)
			priority = (Elevator.currentFloor - requestedFloor) + 4;
		/*else if (floor > Elevator.currentFloor && Elevator.dir == Direction.DOWN && requestedDir== Direction.UP)
			priority = (Elevator.currentFloor - floor) - 4;*/
		else if (requestedFloor < Elevator.currentFloor && Elevator.dir == Direction.DOWN && Elevator.dir==requestedDir)
			priority = (requestedFloor - Elevator.currentFloor) + 4;
		/*else if (floor < Elevator.currentFloor && Elevator.dir == Direction.UP && requestedDir==Direction.DOWN)
			priority = (floor - Elevator.currentFloor) - -4;*/
		else if (Elevator.dir == Direction.IDLE)
			priority = (requestedFloor > Elevator.currentFloor) ? -(requestedFloor - Elevator.currentFloor) +4
					: -(Elevator.currentFloor - requestedFloor)+4;
		else if (requestedFloor < Elevator.currentFloor && Elevator.dir != requestedDir)
			priority = requestedFloor - Elevator.currentFloor;
		else if (requestedFloor > Elevator.currentFloor && Elevator.dir != requestedDir)
			priority = Elevator.currentFloor - requestedFloor;
		Process process = null;
		System.out.println("the priority is: " + priority);
		if (requestedDir == Direction.UP) {
			PCB pcb = new PCB(Elevator.processCount++, priority, Enums.State.NEW);
			process = new MoveTo(pcb, requestedFloor);
		} else if (requestedDir == Direction.DOWN) {
			PCB pcb = new PCB(Elevator.processCount++, priority, Enums.State.NEW);
			process = new MoveTo(pcb, requestedFloor);
		}
		if (process.pcb.state == Enums.State.READY) {
			/*
			 * if (Elevator.processes.peek() != null) {
			 * //System.out.println("this is the process in processes "+Elevator
			 * .processes.peek());
			 * System.out.println("the old process priority "+((MoveTo)
			 * Elevator.processes.peek()).getPriorities()); }
			 * //System.out.println("this is the new process "+process);
			 * System.out.println("the new process priority"+((MoveTo)process).
			 * getPriorities());
			 */
			if (Elevator.processes.peek() != null
					&& ((MoveTo) Elevator.processes.peek()).getPriorities() < ((MoveTo) process).getPriorities()) {
				System.out.println("swap occurs");
				int floorhelp = ((MoveTo) Elevator.processes.peek()).getFloor();
				int priorityhelp = ((MoveTo) Elevator.processes.peek()).getPriorities();
				int IDhelp = ((MoveTo) Elevator.processes.peek()).getID();
				//Enums.State statehelp = ((MoveTo) Elevator.processes.peek()).getStates();
				// System.out.println("floor help"+ floorhelp);
				// System.out.println("floor "+ floor);
				((MoveTo) Elevator.processes.peek()).setFloor(requestedFloor);
				((MoveTo) Elevator.processes.peek()).setPriorities(priority);
				((MoveTo) Elevator.processes.peek()).setID(((MoveTo)process).getID());
				//((MoveTo) Elevator.processes.peek()).setState(((MoveTo)process).getStates());
				((MoveTo) process).setFloor(floorhelp);
				((MoveTo) process).setPriorities(priorityhelp);
				((MoveTo) process).setID(IDhelp);
				//((MoveTo) process).setState(statehelp);
			}
			Elevator.processes.add(process);
			System.out.println("adding process");
			System.out.println("priority queue "+ Elevator.processes);
		}
		// repaint();
		// revalidate();

	}

	public void run() {
		/*
		 * while(true){ repaint(); revalidate(); }
		 */
		/*
		 * BufferedReader insidePanel = new BufferedReader(new
		 * InputStreamReader(System.in)); while (true) { String s = null; try {
		 * while (insidePanel.ready()) { s = insidePanel.readLine(); } } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } if (s!=null&&s.equals("0")) { moveTo(0); }
		 * else if (s!=null&&s.equals("1")) { moveTo(1); } else if
		 * (s!=null&&s.equals("2")) moveTo(2); else if (s!=null&&s.equals("3"))
		 * moveTo(3); else if(s!=null&&s.equals("open")) Elevator.openDoor();
		 * else if(s!=null&&s.equals("close")) Elevator.closeDoor(); else
		 * if(s!=null&&s.equals("emergency"))
		 * Elevator.emergencyStop=!Elevator.emergencyStop;
		 * 
		 * }
		 */
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) e.getSource();
		switch (button.getText()) {
		case "0":
			moveTo(0);
			break;
		case "1":
			moveTo(1);
			break;
		case "2":
			moveTo(2);
			break;
		case "3":
			moveTo(3);
			break;
		case "stop":
			Elevator.emergencyStop = !Elevator.emergencyStop;
			break;
		case "open door":
			if(Elevator.processes.isEmpty())
				Elevator.openDoor();
			else
				System.out.println("cannot open door whilst process is running");
			break;
		case "close door":
			Elevator.closeDoorUser();
			break;
		case "get out":
			Elevator.getout();
			break;
			
		}
		// TODO Auto-generated method stub

	}

}
