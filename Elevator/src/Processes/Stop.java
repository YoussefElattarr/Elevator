package Processes;

import Enums.Direction;
import Enums.State;
import model.Elevator;

public class Stop extends Process {

	public Stop(PCB pcb) {
		this.pcb=pcb;
		create();
	}

	@Override
	public void run() {
		pcb.state=Enums.State.RUNNING;
		Elevator.dir=Direction.IDLE;
		Elevator.openDoor();
		try {
			sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Elevator.closeDoor();
		synchronized (Elevator.i) {
			Elevator.i.notifyAll();
		}
		terminate();

	}

}
