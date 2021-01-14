package hw3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;


public class Saeed_HW3 {
	//set array for wait times
	static int[] wait_time;
	public static void main(String args[]){
		//read file 
		LinkListQ customers = Read_file(args[0]);
		wait_time = new int[customers.size];
		while(!customers.empty()){
			customers.pop();
		}
		//call output file creator
		Output_maker(customers, args[1]);
	}
	
	//method to read files
	private static LinkListQ Read_file(String arg){
		LinkListQ customers = new LinkListQ();
		try {
			String input;
			BufferedReader br = new BufferedReader(new FileReader(arg));
			input = br.readLine();
			Node.cost = Integer.parseInt(input);
			while((input=br.readLine()) !=null){
				int id;
				String clock;
				//split given data add to linked list 
				input = br.readLine();
				id = Integer.parseInt(input.split("  ")[1]);
				input = br.readLine();
				clock = input.split(" ")[1];
				//place the customer into the queue
				customers.add(id, clock);
			}
			
			br.close();
		}
		catch(Exception ex){
			System.out.println( "Unable to open file '" + arg + "'");	
		}
		return customers;
	}
	
	//create output file 
	private static void Output_maker(LinkListQ customers, String arg) {
		try {
			String input;
			BufferedReader br = new BufferedReader(new FileReader(arg));
			FileWriter fw = new FileWriter("Output.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			//loop through the given cases 
			while((input = br.readLine()) != null){
				switch(input){
				case "NUMBER-OF-CUSTOMERS-SERVED":{
					bw.write("NUMBER-OF-CUSTOMERS-SERVED:"+customers.served);
					bw.newLine();
					break;
				}
				case "LONGEST-BREAK-LENGTH":{
					bw.write("LONGEST-BREAK-LENGTH:"+customers.mRest);
					bw.newLine();
					break;
				}
				case "TOTAL-IDLE-TIME":{
					bw.write("TOTAL-IDLE-TIME:"+customers.rest);
					bw.newLine();
					break;
				}
				case "MAXIMUM-NUMBER-OF-PEOPLE-IN-QUEUE-AT-ANY-TIME":{
					bw.write("MAXIMUM-NUMBER-OF-PEOPLE-IN-QUEUE-AT-ANY-TIME:"+customers.mLine);
					bw.newLine();
					break;
				}
				default: {
					if(input.matches("WAITING-TIME-OF \\d*")){
						int id = Integer.parseInt(input.substring(input.indexOf(" ")+1, input.length()));
						bw.write(input+":"+wait_time[id-1]);
						bw.newLine();
					}
					break;
				}
				}
			}
			bw.close();
			br.close();
		}
		catch(Exception ex){
			System.out.println("Unable to open file '" + arg + "'");	
		}
	}
}

//set node class
class Node{
	Node next;
	int ID;
	int time;
	int waitTime;
	static int cost = 100;
	
	Node(int ID, String clock){
		this.ID=ID;
		
		//set times
		String[] times=clock.split(":");
		this.time = Integer.parseInt(times[1])*60+
				Integer.parseInt(times[2]);
		int hour = Integer.parseInt(times[0]);
		if(hour < 7)
			hour += 12;
		this.time += hour*3600;
		
		waitTime = 0;
		if(this.time<9*60*60){
			waitTime += 9*3600-this.time;
			this.time = 9*3600;
		}
	}
	
	@Override
	//override parent to print how we want
	public String toString(){
		return ID + " " + time;
	}
}

//class for linked list
class LinkListQ{
	Node head, tail;
	int served, size, mLine, rest, mRest;
	
	LinkListQ(){
		head = null;
		tail = null;
		served = 0;
		size = 0;
		mLine = 0;
		rest = 0;
		mRest = 0;
		
	}
	
	//method to add to linked list
	void add(int ID, String clock){
		size++;
		if(tail==null){
			head = new Node(ID,clock);
			tail = head;
		}
		else{
			tail.next = new Node(ID,clock);
			tail = tail.next;
		}
	}
	
	//method to remove from linked list 
	Node pop(){
		if(head==null){
			return null;
		}
		int inLine = 0;
		Node line=head.next;
		
		while(line!=null && line.time<17*3600 && line.time-head.time<Node.cost){
			inLine++;
			line=line.next;
		}
		
		if(inLine>mLine)
			mLine=inLine;

		if(head.time>=17*3600){
			head.waitTime-=head.time-17*3600;
			
			if(head.waitTime<0)
				head.waitTime=0;
			
			Saeed_HW3.wait_time[head.ID-1]=head.waitTime;
			
			while(head.next!=null){
				head=head.next;
				
				if(head.time<=17*3600){
					head.waitTime=17*3600-head.time;
				}
				
				Saeed_HW3.wait_time[head.ID-1]=head.waitTime;
			}
			head = null;
			tail = null;
			size = 0;
			return null;
		}
		
		served++;
		size--;
		Node temp = head;
		Saeed_HW3.wait_time[temp.ID-1]=temp.waitTime;
		
		head = head.next;
		if(head == null){ 
			if(temp.time-300<17*3600){
				if(17*3600-temp.time-300>mRest)
					mRest = 17*3600-temp.time-300;
				rest+=17*3600-temp.time-300;
			}
		}
		else {
			if(head.time<temp.time+300){
				head.waitTime+=temp.time+300-head.time;
				head.time=temp.time+300;
			}
			else{
				if(head.time-temp.time-300 > mRest)
					mRest = head.time-temp.time-300;
				rest += head.time-temp.time-300;
			}
		}
		
		return temp;
	}
	
	boolean empty(){
		return head==null;
	}
}