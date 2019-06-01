package Processes;

import Enums.State;





public class PCB {
	int id;
	int priority;
	public State state;
	public PCB(int id, int priority,State state){
		this.id=id;
		this.priority=priority;
		this.state=state;
	}
}
