package InputOutput;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import Enums.Direction;
import Enums.State;
import Processes.AccelerateDown;
import Processes.AccelerateUp;
import Processes.MoveTo;
import Processes.PCB;
import Processes.Process;
import model.Elevator;

public class Outside extends JFrame implements Runnable, ActionListener {
	int floor;
	private JButton up;
	private JButton down;
	private JPanel panel;
	// BufferedReader input;
	// boolean dir;

	public Outside(int floor) {
		this.floor = floor;
		setTitle("Outside Panel of floor number " + floor);
		setBounds(50, 50 + (100 * floor), 300, 100);
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(this.getWidth(), getHeight()));
		add(panel);
		if (floor != Elevator.maximumFloor) {
			up = new JButton("up");
			up.addActionListener(this);
			panel.add(up, BorderLayout.NORTH);
		}
		if (floor != Elevator.minimumFloor) {
			down = new JButton("down");
			down.addActionListener(this);
			panel.add(down, BorderLayout.SOUTH);
		}
		revalidate();
		this.setVisible(true);
		// input=new BufferedReader(new InputStreamReader(System.in));
	}

	public static void main(String[] args) {

	}

	public void request(Direction requestedDir) {
		int priority = 0;
		if (floor > Elevator.currentFloor && Elevator.dir == Direction.UP && Elevator.dir==requestedDir)
			priority = (Elevator.currentFloor - floor) + 4;
		/*else if (floor > Elevator.currentFloor && Elevator.dir == Direction.DOWN && requestedDir== Direction.UP)
			priority = (Elevator.currentFloor - floor) - 4;*/
		else if (floor < Elevator.currentFloor && Elevator.dir == Direction.DOWN && Elevator.dir==requestedDir)
			priority = (floor - Elevator.currentFloor) + 4;
		/*else if (floor < Elevator.currentFloor && Elevator.dir == Direction.UP && requestedDir==Direction.DOWN)
			priority = (floor - Elevator.currentFloor) - -4;*/
		else if (Elevator.dir == Direction.IDLE)
			priority = (floor > Elevator.currentFloor) ? -(floor - Elevator.currentFloor)+4
					: -(Elevator.currentFloor - floor)+4;
		else if (floor < Elevator.currentFloor && Elevator.dir != requestedDir)
			priority = floor - Elevator.currentFloor;
		else if (floor > Elevator.currentFloor && Elevator.dir != requestedDir)
			priority = Elevator.currentFloor - floor;
		Process process = null;
		System.out.println("the priority is: " + priority);
		if (requestedDir == Direction.UP) {
			PCB pcb = new PCB(Elevator.processCount++, priority, Enums.State.NEW);
			process = new MoveTo(pcb, floor);
		} else if (requestedDir == Direction.DOWN) {
			PCB pcb = new PCB(Elevator.processCount++, priority, Enums.State.NEW);
			process = new MoveTo(pcb, floor);
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
				((MoveTo) Elevator.processes.peek()).setFloor(floor);
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
		/*
		 * else if (requestedDir == null) { if (floor > Elevator.currentFloor) {
		 * requestedDir = Direction.UP; PCB pcb = new
		 * PCB(Elevator.processCount++, priority, Enums.State.NEW); process =
		 * new AccelerateUp(pcb, floor); } else { requestedDir = Direction.DOWN;
		 * PCB pcb = new PCB(Elevator.processCount++, priority,
		 * Enums.State.NEW); process = new AccelerateDown(pcb, floor); } }
		 */

	}

	public void run() {
		/*
		 * while (true) { repaint(); revalidate(); }
		 */
		/*
		 * BufferedReader outsidePanel=new BufferedReader(new
		 * InputStreamReader(System.in)); while (true) { String s=null; try {
		 * while(outsidePanel.ready()){ s=outsidePanel.readLine(); } } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } if(s!=null&&s.equals("up")){
		 * request(Direction.UP); } else if(s!=null&&s.equals("down")){
		 * request(Direction.DOWN); } }
		 */
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) e.getSource();
		switch (button.getText()) {
		case "up":
			request(Direction.UP);
			break;
		case "down":
			request(Direction.DOWN);
			break;
		}

	}
}
