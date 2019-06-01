package model;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import Enums.Direction;
import Enums.Doors;
import Enums.State;
import InputOutput.*;
import Processes.*;
import Processes.Process;
import graphing.DrawGraph;
import graphing.graphGUI;

public class Elevator{
	public static Decoder i = new Decoder();
	public static ProcessorGrapher processorGrapher = new ProcessorGrapher();
	public static MemoryGrapher memoryGrapher = new MemoryGrapher();
	//private JButton memgraph;
	//private JButton prograph;
	//private JPanel panel;

	// public static Fetcher fetcher=new Fetcher();
	public static int processCount = 0;
	public static boolean emergencyStop = false;
	public static Inside inside;
	public static Outside floor0;
	public static Outside floor1;
	public static Outside floor2;
	public static Outside floor3;
	public final static int maximumWeight = 500;
	public final static int maximumFloor = 3;
	public final static int minimumFloor = 0;
	public static int currentFloor = 0;
	public static int currentWeight = 0;
	public static double currentFloortenth = 0.0;
	public static Direction dir = Direction.IDLE;
	public static Doors door = Doors.CLOSED;
	public static PriorityQueue<Process> processes = new PriorityQueue<Process>();
	public static ArrayList<Process> memory = new ArrayList<Process>();
	public static Semaphore memsem = new Semaphore(1, true);
	
	/*public void init(){
		setTitle("graphing buttons ");
		setBounds(0,0 , 100, 100);
		panel = new JPanel();
		panel.setPreferredSize(new Dimension(this.getWidth(), getHeight()));
		add(panel);
	}*/

	public static void decode() {
		// System.out.println(processes.isEmpty());
		while (!processes.isEmpty()) {
			try {
				memsem.acquire();
			} catch (InterruptedException e) {
				System.out.println("error in memsem acquiring-> class elevator");
			}
			Process top = processes.peek();
			System.out.println("running proccess is: " +top);
			if (top instanceof AccelerateUp)
				((AccelerateUp) top).run();
			if (top instanceof AccelerateDown)
				((AccelerateDown) top).run();
			if (top instanceof Stop)
				((Stop) top).run();
			if (top instanceof EmergencyStop)
				((EmergencyStop) top).run();
			if (top instanceof MoveTo)
				((MoveTo) top).run();
		}
		if (processes.isEmpty()) {
			dir = Direction.IDLE;
		}
	}

	/*
	 * public static void fetch() { try { memsem.acquire(); } catch
	 * (InterruptedException e) { System.out.println("memsem error"); }
	 * 
	 * }
	 */
	// public static void move(int requestedFloor, Direction requestedDir) {
	// int priority = 0;
	// if (requestedFloor > currentFloor && dir == requestedDir && dir ==
	// Direction.UP)
	// priority = (currentFloor - requestedFloor) + 1;
	// else if (requestedFloor < currentFloor && dir == requestedDir && dir ==
	// Direction.DOWN)
	// priority = (requestedFloor - currentFloor) + 1;
	// else if (dir == Direction.IDLE)
	// priority = (requestedFloor > currentFloor) ? requestedFloor :
	// currentFloor;
	// else if (requestedFloor < currentFloor && dir != requestedDir &&
	// requestedDir == Direction.DOWN)
	// priority = requestedFloor;
	// else if (requestedFloor < currentFloor && dir != requestedDir &&
	// requestedDir == Direction.UP)
	// priority = -requestedFloor;
	// Process process = null;
	// if (requestedDir == Direction.UP) {
	// PCB pcb = new PCB(processCount++, priority, State.NEW);
	// process = new AccelerateUp(pcb, requestedFloor);
	// } else if (requestedDir == Direction.DOWN) {
	// PCB pcb = new PCB(processCount++, priority, State.NEW);
	// process = new AccelerateDown(pcb, requestedFloor);
	// } else if (requestedDir == null) {
	// if (requestedFloor > currentFloor) {
	// requestedDir = Direction.UP;
	// PCB pcb = new PCB(processCount++, priority, State.NEW);
	// process = new AccelerateUp(pcb, requestedFloor);
	// } else {
	// requestedDir = Direction.DOWN;
	// PCB pcb = new PCB(processCount++, priority, State.NEW);
	// process = new AccelerateDown(pcb, requestedFloor);
	// }
	// }
	// if (process.pcb.state == State.READY) {
	// processes.add(process);
	// }
	//
	// }

	public static void stop() {
		int priority = 10;
		PCB pcb = new PCB(processCount++, priority, State.NEW);
		Process process = new Stop(pcb);
		if (process.pcb.state == State.READY) {
			processes.add(process);
		}
	}

	public static void openDoor() {
		System.out.println("doors are opening");
		door = Doors.OPEN;
	}

	public static void closeDoor() {
		System.out.println("doors are closing");
		door = Doors.CLOSED;
		int randomweight= (int) (Math.random()*700);
		currentWeight= randomweight;
		inside.curWeight.setText(currentWeight+"");
	}
	public static void closeDoorUser() {
		System.out.println("doors are closing");
		door = Doors.CLOSED;
	}
	
	public static void getout(){
		currentWeight-=50;
		inside.curWeight.setText(currentWeight+"");
	}

	public static void emStop() {
		// emergencyStop=!emergencyStop;
		int priority = 90;
		PCB pcb = new PCB(processCount++, priority, State.NEW);
		Process process = new EmergencyStop(pcb);
		if (process.pcb.state == State.READY) {
			processes.add(process);
		}
		decode();
	}

	public static void main(String[] args) {
		new graphGUI();
		i.start();
		memoryGrapher.start();
		processorGrapher.start();
		ExecutorService pool = Executors.newFixedThreadPool(5);
		floor0 = new Outside(0);
		floor1 = new Outside(1);
		floor2 = new Outside(2);
		floor3 = new Outside(3);
		inside = new Inside();
		pool.execute(floor0);
		pool.execute(floor1);
		pool.execute(floor2);
		pool.execute(floor3);
		pool.execute(inside);

		/*
		 * floor0.start(); floor1.start(); floor2.start(); floor3.start();
		 * inside.start();
		 */
		// while (true) {
		// openDoor();
		// System.out.println(door);
		// closeDoor();
		// System.out.println(door);
		// move(4, Direction.UP);
		// System.out.println(currentFloor);
		// move(1, Direction.DOWN);
		// System.out.println(currentFloor);
		// BufferedReader input = new BufferedReader(new
		// InputStreamReader(System.in));
		// System.out.println("are you inside the elevator or out(choose
		// in/out)");
		// System.out.println("choose
		// (move,stop,opendoor,closedoor,emergenecystop)");
		/*
		 * String s = null; try { // while (input.ready()) { s =
		 * input.readLine(); // } } catch (IOException e) {
		 * System.out.println("elevator error"); } if (s != null &&
		 * s.equals("in")) { System.out.
		 * println("choose (move,stop,opendoor,closedoor,emergenecystop)"); try
		 * { s = input.readLine(); } catch (IOException e1) {
		 * System.out.println("error again"); } switch (s) { case "move": int
		 * requestedfloor = -1;
		 * System.out.println("enter requested floor(0,1,2,3,4,5,6)"); try { s =
		 * input.readLine(); } catch (IOException e) {
		 * System.out.println("error"); } switch (s) { case "0": requestedfloor
		 * = 0; inside.moveTo(requestedfloor); break; case "1": requestedfloor =
		 * 1; inside.moveTo(requestedfloor); break; case "2": requestedfloor =
		 * 2; inside.moveTo(requestedfloor); break; case "3": requestedfloor =
		 * 3; inside.moveTo(requestedfloor); break; /*case "4": requestedfloor =
		 * 4; inside.move(requestedfloor, null); break; case "5": requestedfloor
		 * = 5; inside.move(requestedfloor, null); break; case "6":
		 * requestedfloor = 6; inside.move(requestedfloor, null); break;
		 */
		/*
		 * default: System.out.println("error floor"); break; } case "opendoor":
		 * openDoor(); break; case "closedoor": closeDoor(); break; case
		 * "emergencystop": emergencyStop = !emergencyStop; break; default:
		 * System.out.println("invalid");
		 * 
		 * }
		 * 
		 * } else if (s != null && s.equals("out")) {
		 * System.out.println("what floor are you in?"); try {
		 * s=input.readLine(); } catch (IOException e1) { // TODO Auto-generated
		 * catch block e1.printStackTrace(); } int requestedfloor=-1; switch (s)
		 * { case "0": requestedfloor = 0; break; case "1": requestedfloor = 1;
		 * break; case "2": requestedfloor = 2; break; case "3": requestedfloor
		 * = 3; break; /*case "4": requestedfloor = 4; break; case "5":
		 * requestedfloor = 5; break; case "6": requestedfloor = 6; break;
		 */
		/*
		 * default: System.out.println("error floor"); break; }
		 * System.out.println("enter requested direction(up/down)"); try { s =
		 * input.readLine(); } catch (IOException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); } Direction requestedDir; switch (s)
		 * { case "up": requestedDir = Direction.UP;
		 * floor0.request(requestedDir); break; case "down": requestedDir =
		 * Direction.DOWN; floor0.request(requestedDir); break; default:
		 * System.out.println("print valid direction"); }
		 * 
		 * }
		 */
		/*
		 * synchronized (i) { i.notifyAll(); }
		 */
		// }

	}

	public static class Decoder extends Thread {

		public void run() {
			System.out.println("decoder is running");
			while (true) {
				Elevator.decode();
				try {
					synchronized (this) {
						sleep(1500);
					}
				} catch (InterruptedException e) {
					System.out.println("errorThrown in decoder class");
				}
			}

		}

	}

	public static class MemoryGrapher extends Thread {
		public static ArrayList<Integer> seconds = new ArrayList<Integer>();
		public static ArrayList<Integer> usage = new ArrayList<Integer>();
		int second = 0;

		public void run() {
			System.out.println("memoryGrapher is running");
			while (true) {
				try {
					synchronized (this) {
						sleep(1000);
						second++;
						seconds.add(second);
						if (memory.isEmpty()) {
							//System.out.println("at t=" + second + " seconds, memory usage = 0%");
							usage.add(0);
						} else {
							//System.out.println("at t=" + second + " seconds, memory usage = 100%");
							usage.add(100);
						}
					}
				} catch (InterruptedException e) {
					System.out.println("errorThrown in decoder class");
				}
			}

		}

	}

	public static class ProcessorGrapher extends Thread {
		public static ArrayList<Integer> seconds = new ArrayList<Integer>();
		public static ArrayList<Integer> usage = new ArrayList<Integer>();
		public static int second = 0;

		public void run() {
			System.out.println("ProcessorGrapher is running");
			while (true) {
				boolean running = false;
				try {
					synchronized (this) {
						sleep(1000);
						second++;
						seconds.add(second);
						for (Process proc : processes) {
							if (proc.pcb.state == Enums.State.RUNNING) {
								running = true;
								//System.out.println("at t=" + second + " seconds, processor usage = 100%");
								usage.add(100);
								break;
							}
						}
						if (!running) {
							//System.out.println("at t=" + second + " seconds, processor usage = 0%");
							usage.add(0);
						}
					}
				} catch (InterruptedException e) {
					System.out.println("errorThrown in decoder class");
				}
			}

		}

	}

/*
 * public static class Fetcher extends Thread {
 * 
 * public void run() { while (true) { Elevator.fetch(); try { synchronized
 * (this) { System.out.println("wait"); this.wait();
 * System.out.println("unwait"); } } catch (InterruptedException e) {
 * System.out.println("errorThrown"); } }
 * 
 * }
 * 
 * }
 */
}
