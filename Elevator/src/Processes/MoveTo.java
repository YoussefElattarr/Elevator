package Processes;

import java.awt.Color;

import Enums.*;
import model.Elevator;

public class MoveTo extends Process {
	int floor;
	Direction direction;

	public MoveTo(PCB pcb, int floor) {
		this.pcb = pcb;
		this.floor = floor;
		create();
	}

	@Override
	public void run() {
		pcb.state = Enums.State.RUNNING;
		Elevator.closeDoor();
		if (floor > Elevator.currentFloor)
			Elevator.dir = Direction.UP;
		else
			Elevator.dir = Direction.DOWN;
		while (Elevator.currentFloor != floor && pcb.state == Enums.State.RUNNING) {
			// System.out.println(Elevator.currentFloor);
			// while (pcb.state == State.RUNNING) {
			try {
				sleep(500);
			} catch (InterruptedException e) {
			}
			if (Elevator.dir == Direction.UP) {
				Elevator.currentFloortenth = Elevator.currentFloortenth + 0.1;
				if (Elevator.currentFloortenth >= 1.0) {
					Elevator.currentFloortenth = 0.0;
					Elevator.currentFloor++;
					Elevator.inside.curFloor.setText(""+Elevator.currentFloor);
					Elevator.inside.revalidate();
					System.out.println("this is the current floor " + Elevator.currentFloor);
				}
			} else {
				Elevator.currentFloortenth = Elevator.currentFloortenth + 0.1;
				if (Elevator.currentFloortenth >= 1.0) {
					Elevator.currentFloortenth = 0.0;
					Elevator.currentFloor--;
					Elevator.inside.curFloor.setText(""+Elevator.currentFloor);
					Elevator.inside.revalidate();
					System.out.println("this is the current floor " + Elevator.currentFloor);
				}
			}
			if (Elevator.emergencyStop|| Elevator.currentWeight>Elevator.maximumWeight) {
				this.block();
				System.out.println("process of ID: " + pcb.id + " is currently blocked");
				while (true) {
					try {
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (!Elevator.emergencyStop&& Elevator.currentWeight<=Elevator.maximumWeight) {
						System.out.println("process of ID: " + pcb.id + " is unblocked");
						pcb.state = Enums.State.RUNNING;
						Elevator.inside.blocked.setBackground(Color.GREEN);
						break;
					}
				}
			}
		}
		Elevator.openDoor();
		try {
			sleep(2000);
		} catch (InterruptedException e) {
		}
		synchronized (Elevator.i) {
			Elevator.i.notifyAll();
		}
		terminate();

	}

	public String toString() {
		return "process ID: " + pcb.id + " to floor: " + floor+ " state: "+ pcb.state;
	}
	public int getPriorities(){
		return this.pcb.priority;
	}
	public void setPriorities(int priority){
		this.pcb.priority=priority;
	}
	public int getFloor(){
		return this.floor;
	}
	public void setFloor(int floorin){
		this.floor=floorin;
	}
	public int getID(){
		return this.pcb.id;
	}
	public void setID(int id){
		this.pcb.id=id;
	}
	public Enums.State getStates(){
		return this.pcb.state;
	}
	public void setState(Enums.State state){
		this.pcb.state=state;
	}

}
