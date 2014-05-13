//Michael Lim

//A class to represent a job on a CPU
public class Job {
	public enum Type {CPU, IO}
	
	public int proc;
	public char type;
	public int originalTime;
	public long timeNeeded;
	public long enterTime;
	public long firstAddressedTime;
	public long finishTime;
	
	public Job(int proc, char type, int originalTime){
		this.proc = proc;
		this.type = type;
		this.originalTime = originalTime;
		this.timeNeeded = this.originalTime;
		this.firstAddressedTime = -1; //Initialize these to -1, they'll be set when we address these jobs
		this.enterTime = -1;
		this.finishTime = -1;
	}
	
	public String getTypeString(){ //Converts char representation to a human recognizable name
		if (this.type == 'C'){
			return "CPU";
		}
		else if ( this.type == 'I'){
			return "IO";
		}
		else{
			return "Unknown";
		}
	}
	
	public String toString(){ //Provide a simple string representation of the Job for diagnostics
		return "A "+getTypeString()+" job for process "+ this.proc+", started at "+this.enterTime+", and needs " + this.timeNeeded;
	}
	
}
