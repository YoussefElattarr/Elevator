package Processes;

import Enums.State;
import model.Elevator;

public class EmergencyStop extends Process {

	public EmergencyStop(PCB pcb) {
		this.pcb=pcb;
		create();
	}

	@Override
	public void run() {
		pcb.state=Enums.State.RUNNING;
		while(true){
			if(!Elevator.emergencyStop) {
				break;
			}
		}
		terminate();
	}
	
}
