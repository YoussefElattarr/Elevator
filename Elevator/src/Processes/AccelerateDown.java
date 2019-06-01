package Processes;

import Enums.Direction;
import Enums.State;
import model.Elevator;

public class AccelerateDown extends Process {
	int requestedFloor;

	public AccelerateDown(PCB pcb, int requestedFloor) {
		this.pcb = pcb;
		this.requestedFloor = requestedFloor;
		create();
	}

	public void run() {
		pcb.state = Enums.State.RUNNING;
		Elevator.closeDoor();
		Elevator.dir=Direction.DOWN;
		while (Elevator.currentFloor > requestedFloor && pcb.state == Enums.State.RUNNING) {
//			System.out.println(Elevator.currentFloor);
			// while (pcb.state == State.RUNNING) {
			try {
				sleep(500);
				System.out.println("this is the current floor " +Elevator.currentFloor);
			} catch (InterruptedException e) {
			}
			Elevator.currentFloortenth=Elevator.currentFloortenth+0.1;
			if(Elevator.currentFloortenth>=1.0){
				Elevator.currentFloortenth=0.0;
				Elevator.currentFloor--;
			}
			if (Elevator.emergencyStop) {
				pcb.state = Enums.State.BLOCKED;
				System.out.println("process of ID: "+pcb.id+" is currently blocked");
				while(true){
					try {
						sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(!Elevator.emergencyStop) {
						System.out.println("process of ID: "+pcb.id+" is unblocked");
						pcb.state= Enums.State.RUNNING;
						break;
					}
				}
			}
//			System.out.println(Elevator.currentFloor);
			/*if (Elevator.emergencyStop) {
				pcb.state = Enums.State.BLOCKED;
				Elevator.emStop();
			}*/
			// }
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
		// Elevator.stop();

	}
	public String toString(){
		return "Accelerate down process of ID "+pcb.id+" is currently running to floor: "+requestedFloor;
	}

}
