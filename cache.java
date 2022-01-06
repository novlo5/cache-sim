import java.io.*;
import java.util.Scanner;
public class cache{
	
	private int blocksize; // block size;
	//private final int wordsize = 4;
	private int numsets; // number of sets;
	private int assoc; // associativity
	private int rpolicy; // 0 for writeback/write allocate, 1 for write through/no write allocate
	//private int sizelayers; // size of the levels
	private int latency; // access latency in each level
	private cache_block[][] cache_array;
	private dll[] LRU;

	public cache(int assoc, int rpolicy, int cache_size, int latency, int blocksize){

        this.assoc = assoc;
        this.rpolicy = rpolicy;
        this.latency = latency;
		this.blocksize = blocksize;

		int numblocks = cache_size / blocksize;
		this.numsets = numblocks / assoc;

		this.LRU = new dll[numsets];
		this.cache_array = new cache_block[this.numsets][assoc];
			for(int i = 0; i < this.numsets; i++){
				this.LRU[i] = new dll();
			for(int j = 0; j < assoc; j++){
				this.cache_array[i][j] = new cache_block();
			}
		}
	}

    public class cache_block{
	
	    private int tag;
	    private boolean valid;
		private boolean dirty;
		private int data;

	public cache_block(){
		this.tag = 0;
		this.valid = false;
		this.dirty = false;
		this.data = 0;
	}
}
public class dll{
	private dll_node<cache_block> head;
	private dll_node<cache_block> tail;

	public dll(){
		head = null;
		tail = null;
	}
	
	public void insertAtTail(cache_block LRU){
		dll_node<cache_block> newNode = new dll_node(LRU, null, null);
		if(head == null){
			head = newNode;
			tail = newNode;
		}
		else{
			tail.next = newNode;
			newNode.prev = tail;
			tail = newNode;
		}
	}
	public void SearchAndMove(cache_block replace){
		dll_node<cache_block> curr = head;
		do{
			if ( head == null )
			{
				insertAtTail(replace);
				return;
			}
			if(head.check == replace && tail.check == replace){return;}
			else if(replace == curr.check)
			{
      			curr.prev.next = curr.next;
        		curr.next = null;
        		tail.next = curr; /// I want the old tail to point to the new tail
        		tail = curr;
        		return;
			}
			curr = curr.next;
		}while(curr != tail);
  }
	public int DeleteAtHead(){
		int tag = head.check.tag;
		head = head.next;
		return tag;
	}
	public class dll_node<cache_block>{
		private cache_block check;
		private dll_node<cache_block> prev, next;

		public dll_node(cache_block check){
			this(check, null, null);
		}
		public dll_node(cache_block check, dll_node<cache_block> prev, dll_node<cache_block> next){
			this.check = check;
			this.prev = prev;
			this.next = next;
		}
	}
}
	public int calc_index(int address, int numsets, int blocksize){
		return (address / blocksize) % numsets;
	}
	public int calc_tag(int address, int numsets, int blocksize){
		return (address / blocksize) / numsets;
	}
	public int calc_offset(int address, int blocksize){
		return (address % blocksize);
	}

	public boolean read_cache(int address, cache mem, cache[] levels){

		int index = calc_index(address, mem.numsets, mem.blocksize);
		int tag = calc_tag(address, mem.numsets, mem.blocksize);
			for(int i = 0; i < mem.assoc; i++){
					if(mem.cache_array[index][i].tag == tag && mem.cache_array[index][i].valid == true){ ///if valid and tag match read hit
							mem.LRU[index].SearchAndMove(mem.cache_array[index][i]);
							return true; 
					}
				}
						cache_block found = checkhighercache(index, tag, levels); 
						if(found != null){
							boolean hit = write_cache(address, found.data, mem, levels);
							if(hit){
								this.LRU[index].insertAtTail(found);
							}
								return hit;
						}
							return false; // if we dont find anywhere read miss
}
	public cache_block checkhighercache(int index, int tag, cache[] levels){
		for(int i = 1; i < levels.length; i++){
			for(int k = 0; k < (levels[i].latency*100); k++){
				//no op for cache latency
			}
			for(int j = 0; j < levels[i].assoc; j++){
				if(levels[i].cache_array[index][j].tag == tag && levels[i].cache_array[index][j].valid == true){
					return levels[i].cache_array[index][j];
				}	
			}	
		}
		return null;
	}

	public boolean write_highercache(int index, int tag, cache[] levels, cache_block towrite){

		for(int i = 1; i < levels.length; i++){
			for(int k = 0; k < (levels[i].latency*100); k++){
				//no op for cache latency
			}
			for(int j = 0; j < levels[i].assoc; j++){
				if(levels[i].cache_array[index][j].tag == tag && levels[i].cache_array[index][j].valid == false){
					System.out.println(index + " " + i);
					levels[i].cache_array[index][j].data = towrite.data;
					levels[i].cache_array[index][j].valid = true;
					return true;
				}	
			}
			if(levels[i].rpolicy == 0){
				this.cache_array[index][0].dirty = true; //just write this t neearest set since I cant figure out LRU
				this.cache_array[index][0].data = towrite.data;
				System.out.println(this.cache_array[index][0].data);
				this.cache_array[index][0].valid = true;
				return true;
			}
		}
		return false;
	}
public boolean write_cache(int address, int data, cache mem, cache[] levels){

		boolean found = false;
		  int index = calc_index(address, this.numsets, this.blocksize);
		  int tag = calc_tag(address, this.numsets, this.blocksize);
	  do{
			  for(int i = 0; i < mem.assoc - 1; i++){
				  if(mem.cache_array[index][i].tag == tag && mem.cache_array[index][i].valid == true){ //if tag and valid its a write hit
						  if(mem.rpolicy == 0 && mem.cache_array[index][i].dirty == true || mem.rpolicy == 1){ //if it is dirty or write through cache write to higher levels
							  write_highercache(index, tag, levels, mem.cache_array[index][i]);
							  System.out.println(tag + " valid");
							  this.cache_array[index][i].dirty = true; //replace old data with new data
							  this.cache_array[index][i].data = data;
							  System.out.println(this.cache_array[index][i].data);
							  this.cache_array[index][i].valid = true;
							  return true;
						  }
					  }
						  else if(mem.cache_array[index][i].valid == false && mem.rpolicy == 0){
							  this.cache_array[index][i].dirty = true;
							  this.cache_array[index][i].data = data;
							  this.cache_array[index][i].tag = tag;
							  System.out.println(tag + " not valid");
							  this.cache_array[index][i].valid = true;
							  this.LRU[index].insertAtTail(this.cache_array[index][i]);
							  return true;
			}
			  }
		
		if (mem.rpolicy == 1) {
		  return false;
		}
		
		  tag = this.LRU[index].DeleteAtHead();
		
		} while(!found);
		
	  return false;
	}
		
	

	public int calcLRU(cache_block calc){
		return 0;
	}
	public void printCache(cache[] levels){
		for(int i = 0; i < levels.length; i++){
			System.out.println("L" + (i+1) + " cache");
				for(int j = 0; j < levels[i].numsets; j++){
					for(int k = 0; k < levels[i].assoc; k++){
							System.out.print(levels[i].cache_array[j][k].data + " ");
					}
					System.out.println();
				}
		}
	}
   public static void main(String args[]) throws Exception{
		Scanner configure = new Scanner(System.in);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		final String read = "r";
		int cache_hit = 0;
		int cache_miss = 0;
		int cache_access = 0;
		System.out.println("Select your number of layers for the cache (1-3):"); //inputs to configure the cache
			int layers = configure.nextInt();
		System.out.println("Select your associativity for the cache(s):");
			int assoc = configure.nextInt();
		System.out.println("Select your blocksize:");
			int blocksize = configure.nextInt();
		System.out.println("Select Access Latency(in cycles):");
			int latency = configure.nextInt();
		System.out.println("Select your write and allocation policy (0 for write back/write allocate, 1 for write-through/no-write allocate):");
			int rpolicy = configure.nextInt();

		cache[] cache_array = new cache[layers];
		for(int i = 0; i < layers; i++){
			System.out.println("Select your size of layer " + i + " to the cache (1-3):");
			int sizelayers = configure.nextInt();
			cache layer = new cache(assoc, rpolicy, sizelayers, latency, blocksize); //create a cache
			cache_array[i] = layer;
		}
			String more = " ";
		do{
			System.out.print("Enter cache entries:");
			String command = in.readLine();
			String[] parse = command.split("\\s+"); //parse inputs
			String operation = parse[0];
			int address = Integer.parseInt(parse[1]);
			int data = Integer.parseInt(parse[2]);
			boolean hit;
			if (operation.equals(read)){
				 hit = cache_array[0].read_cache(address, cache_array[0], cache_array);
				System.out.println(hit);
			}

			else{
				hit = cache_array[0].write_cache(address, data, cache_array[0], cache_array);
			}
			if(hit){cache_hit++;}
			else{cache_miss++;}
			cache_access++;

			
		System.out.println("More entries (y/n) ?");
		more = configure.next();
		}while(more.equals("y"));
		
		calcStats(cache_hit, cache_miss, cache_access);
		cache_array[0].printCache(cache_array);

		configure.close();
		in.close();
	} 
	public static void calcStats(int hit, int miss, int accesses){

		double hit_rate = ((double)hit / (double)accesses) * 100;
		System.out.println("Hit Rate: " + hit_rate + "%");
		double miss_rate = ((double)miss / (double)accesses) * 100;
		System.out.println("Miss Rate: "+ miss_rate + "%");
	}
}



