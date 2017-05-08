/**
 * @(#)PokemonArena.java
 *
 *
 * @Shahir Chowdhury 
 * @version 1.00 2014/11/28
 */

import java.util.*;
import java.io.*;
public class PokemonArena {
	public static Pokemon current; //current good guy pokemon in battle

    public static void main(String[]args){	
    	intro();
    	spacemaker();
    	String playername = story();
    	//String playername = "Ash"; //comment out the above line and uncomment this line to skip story
    	spacemaker();

    	String[]contenders = fileIO();
		Pokemon[]dreamteam = teambuilder(contenders,contenders); //build pokemon team
		spacemaker();
		current = first(dreamteam); //current pokemon
		
		int knockouts = 0;//counter for number of enemy pokemon that have been defeated

		for (int i=0;i<=contenders.length-5;i++){ //game continues until the player loses or defeats all opponents
			Pokemon enemy = new Pokemon (enemyselect(contenders));
			spacemaker();
			if (knockouts < contenders.length-5){ //battle number
				pprint(String.format("Prepare! Battle Number %-2d: %s\n",i+1,enemy.nameget()));
			}
			else if (i == contenders.length-5){//final fight
				pprint(String.format("FINAL BATTLE! Kanto's Arch Nemesis: %s\n",enemy.nameget()));
				enemy.hpset(enemy.hpget()*2);
			}

			battlecry(current);
			boolean turn = coinflip();
			if (turn == true){//player goes first
				pprint(String.format("Your %s startled the enemy %s!",current.nameget(),enemy.nameget()));
			}
			else{ //enemy goes first
				pprint(String.format("The enemy %s got the drop on your %s!",enemy.nameget(),current.nameget()));
			}
			while (true){//while battle is in progress
				if (turn == true){ //player turn first
					if (current.alive()){//check if player is alive
						battle(current,enemy,dreamteam); //player fights
						suspense(2000);
						if (enemy.alive()){ //if enemy is alive after the player's attack, the enemy fights backs
							enemy.badguyattack(current); 
							suspense(2000);
							if (current.alive()){ //if player is still alive
								spacemaker();
							}
							else{ //if player pokemon fainted
								pprint(String.format("Your %s was knocked out!\n",current.nameget()));
								if (defeated(dreamteam) == false){
									pprint("Please select one of your remaining pokemon");
								}
								else{
									pprint("Your team was wiped out!");
								}
								suspense(2000);	
								retreat(current,dreamteam);	
							}	
						}
						else{ //if enemy pokemon fainted
							pprint(String.format("You knocked out your opponent's %s!\n",enemy.nameget()));
							pprint(String.format("You won the battle!"));
							knockouts += 1;
							refresh(dreamteam);
							suspense(2000);
							break;
						}
					}
					else{ //if player pokemon fainted
						pprint(String.format("Your %s was knocked out!\n",current.nameget()));
						if (defeated(dreamteam) == false){
							pprint("Please select one of your remaining pokemon");
						}
						else{
							pprint("Your team was wiped out!");
						}
						suspense(2000);
						spacemaker();	
						retreat(current,dreamteam);
					}	
				}
				if (turn == false){//enemy turn first
					if (enemy.alive()){ //check if enemy is alive
						enemy.badguyattack(current); //enemy fights
						suspense(2000);
						if (current.alive()){ //if player is alive after attack
							battle(current,enemy,dreamteam);//player fights
							suspense(2000);
							if (enemy.alive()){//if enemy is still alive 
								spacemaker();
							}
							else{ //if enemy pokemon fainted
								pprint(String.format("You knocked out your opponent's %s!\n",enemy.nameget()));
								pprint(String.format("You won the battle!"));
								knockouts += 1;
								refresh(dreamteam);
								suspense(2000);
								break;
							}
						}
						else{ //if player pokemon fainted
							pprint(String.format("Your %s was knocked out!\n",current.nameget()));
							if (defeated(dreamteam) == false){
								pprint("Please select one of your remaining pokemon");
							}
							else{
								pprint("Your team was wiped out!");
							}
							suspense(2000);
							spacemaker();	
							retreat(current,dreamteam);
						}
					}
					else{ //if enemy pokemon fainted.
						pprint(String.format("You knocked out your opponent's %s!\n",enemy.nameget()));
						pprint(String.format("You won the battle!"));
						knockouts += 1;
						refresh(dreamteam);
						suspense(2000);
						break;
					}
				}
				if(defeated(dreamteam)){
					break;
				}
				suspense(1000);
				turn = coinflip();
			}
			if(defeated(dreamteam)){
				break;
			}	
		}
		conclusion(knockouts, contenders.length-5,playername);
    }	
    
    public static void battle(Pokemon goodguy,Pokemon badguy,Pokemon[]team){
    /* The method gets the user's inputs for the main three actions they can perform */
    	
    	Scanner kb = new Scanner(System.in);
    	boolean action = false; //flag for whether or not an action has been done this turn
    	while (action == false){
    		if (action == false){
    			if (goodguy.stunget() == false){ //being stunned results in the pokemon being forced into the "Pass" command
    				optiondisplay(goodguy); //display the three pokemon commands 
    				int num = getNum(kb);
	    			if((num < 0) || (num > 2)){ //check to prevent invalid input
	    				pprint(String.format("That is not a valid option"));
	    			}
	    			if (num == 0){//attack
	    				attackdisplay(goodguy, goodguy.attacksget(), goodguy.attacknumget());
	    				action = goodguy.goodguyattack(badguy);
	    			}
	    			if (num == 1){//pass
	    				action = goodguy.pass();
	    			} 
	    			if (num == 2){//retreat
	    				int numalive = 0;
	    				for (int i=0;i<4;i++){
	    					if (team[i].alive()){
	    						numalive += 1;
	    					}
	    				}
	    				if (numalive <= 1){ //only allow retreating if the there are more than one healthy pokemon waiting in the party
	    					pprint(String.format("You don't have any pokemon to retreat to!"));
	    				}
	    				else{ 
	    					action = retreat(goodguy,team);
	    				}	
    				}			
	    		}
	    		else{ //stunned
	    			action = goodguy.pass();
	    			goodguy.healstun();
	    		}
    		}
    		else{//if an action has been done
    			break;
    		}	
    	}
    } 	//STUN CANNOT BE CURED FOR BADGUYS
    	//15 DAMAGE RESISTED RETURNS 0

    public static boolean retreat(Pokemon inbat, Pokemon[]team){
    /* 1 of the 3 possible commands from the player to the pokemon. 
     * This command tells the currently in battle pokemon to fall back and let one of his teammates do the talking.
     * Note that majority of the actual retreating work is done in the method 'switchout'. This method is just used
     * to signal an action did happen in a turn */
    	if (defeated(team)){ //Don't select any pokemon if the entire party was knocked out
    		return true;
    	}

	 	teamdisplay(inbat,team); //display pokemon to retreat to
    	Scanner kb = new Scanner(System.in);
    	while (true){
			int num = getNum(kb);
			if ((num == team.length) && (inbat.alive() == true)){ //back button
				return false;
			}
			else if((num < 0) || (num >= team.length)){ //prevent calling of a non existant pokemon 
				pprint("That is an invalid pokemon! Pick another one!");	
			}
			else if (team[num].alive() == false){ //prevent calling of a knocked out pokemon
				pprint(String.format("%s is unable to battle. Pick another one!", team[num].nameget()));
			} 
			else if (team[num].inbattleget() == true){  //prevent calling of the pokemon currently in battle
   				pprint(String.format("%s is currently in battle. Pick another one!", team[num].nameget()));
			}
			else{
				inbat.battlestatus(); //remove in battle status from preivous pokemon
				team[num].battlestatus(); //set in battle status on current
				current = team[num];
				team[num] = current;
				pprint(String.format("You switched into %s!",current.nameget())); 
				if(inbat.alive() == false){ //clean screen if the player's pokemon was knocked out
					suspense(2000);
					spacemaker();
				}
				return true;
			}
    	}
    }    

    public static boolean defeated(Pokemon[]team){ //flag that checks whether or not the player has lost yet
		for (int i=0;i<team.length;i++){
			if (team[i].alive()){ //player does not lose until their entire team has been knocked out
				return false; 
			}
		}
		return true;
	}
	public static boolean coinflip(){ //random chance method with equal probability of returning true or false
    	int chance = (int)(Math.random()*100);
    	if (chance >= 50){
    		return true;
    	}
    	return false;
    }
	public static void teamregen(Pokemon[]team){ //natural energy regeneration for all resting on the team
		for (int i=0;i<team.length;i++){
			if (team[i].alive() && team[i].inbattleget() == false){ //only those who are alive
				team[i].regen();
			}
		}
	} 
	public static void fullteamregen(Pokemon[]team){ //regen the whole team when an enemy pokemon is defeated
		for (int i=0;i<team.length;i++){
			if (team[i].alive()){ //only those who are alive
				team[i].fullregen();
			}
		}
	}
	public static void teamheal(Pokemon[]team){ //heals the whole team when an enemy pokemon is defeated
		for (int i=0;i<team.length;i++){
			if (team[i].alive()){ //only those who are alive
				team[i].heal();
				team[i].healdisable();			
			}	
		}
	}
	public static void refresh(Pokemon[]team){ //regenerates and heals the whole team
		fullteamregen(team);
		teamheal(team);
	}
 
    public static int getNum(Scanner kb){
    /* This method checks any input to make sure it is an integer */
    
    	String pok;
    	int num = -1;
		while (num == -1){
			pok = kb.nextLine();
			num =- 1;
			try{
				num = Integer.parseInt(pok);
			}
			catch(NumberFormatException ex){//if input cannot be made an integer
				pprint(String.format("That's not a number, and you probably knew that!"));
			}
		}
		return num;    	
    }
    public static String[] fileIO(){ 
    /* This method returns a string array of pokemon strings from a text file
     * which can be later converted into pokemon */
     
    	Scanner infile = null;
    	int n;
    	try{
    		infile = new Scanner(new File ("pokemon.txt"));
    	}
    	catch (IOException ex){
    		System.out.println(ex);
    	}
    	n = Integer.parseInt(infile.nextLine()); //gets the number of lines to read
    	String[]allPoke = new String[n];
    	for (int i=0;i<n;i++){
    		allPoke[i] = infile.nextLine();
    	}
    	return allPoke;
    }
    
    public static Pokemon[]teambuilder(String[]pokemonlist,String[]cleanlist){
    /* This method displays all the possible pokemon the user can choose from
     * and creates a 4 (poke)man team for the user to use for the rest of the game */
     
    	Pokemon[] selectables = new Pokemon[pokemonlist.length]; //all the pokemon to choose from
    	Pokemon[] team = new Pokemon[4]; //the party
    	System.out.println(" CREATE YOUR TEAM "); 
    	System.out.println("");
   		for (int i=0;i<pokemonlist.length;i++){ //display
   			selectables[i] = new Pokemon (pokemonlist[i]);//converts the pokemon strings into genuine pokemon
   			System.out.printf("%2d)Pokemon: %-10s Type: %-10s Weakness: %-10s Resistance: %-10s\n",i,selectables[i].nameget(),selectables[i].typeget(), selectables[i].weakget(), selectables[i].resisget());
   			suspense(150);
   		}
   		
   		Scanner kb = new Scanner(System.in);
   		System.out.println("");
   		while (true){ 
   			for (int x=0;x<4;x++){ //take in the user's input 4 times for all 4 positions on the team
   				if (x == 0){
   					pprint("Please select your 1st pokemon!");
   				}
   				else if (x == 1){
   					pprint("Please select your 2nd pokemon!");
   				}
   				else if (x == 2){
   					pprint("Please select your 3rd pokemon!");
   				}
   				else if (x == 3){
   					pprint("Please select your 4th pokemon!");
   				}

				int num = getNum(kb); //make sure input is a number
				if(num < 0 || num > pokemonlist.length-1){ //check to see if the input is within range
					x -= 1;
   					pprint("That is an invalid pokemon! Pick another one!");
   				}
   				else if (selectables[num] == team[0] || selectables[num] == team[1] || selectables[num] == team[2] || selectables[num] == team[3]){ //check to see if the pokemon is already on the team (no repeats)
   					x -= 1;
   					pprint("That pokemon has already been selected! Pick another one!");
   				}
   				else if (selectables[num] != team[0] && selectables[num] != team[1] && selectables[num] != team[2] && selectables[num] != team[3]){ //check to see if the pokemon is already on the team (no repeats)
   					team[x] = selectables[num];
   					pokemonlist[num] = ""; //replaces the original pokemon text line with a blank, preventing the selected pokemon from showing up as an enemy later during the game
   				}						 
   			}

   			System.out.println("");
   			pprint(String.format("Your selected team is: [0]%s [1]%s [2]%s [3]%s\n",team[0].nameget(),team[1].nameget(),team[2].nameget(),team[3].nameget())); 
   			
   			pprint(String.format("Is this your final team? (Input y/n)")); 
   			String decision = kb.nextLine();

   			while ((!decision.equals("y")) && (!decision.equals("n"))){ //allow the user to repick their team
   				pprint("Please respond using y/n");
   				decision = kb.nextLine();
   			}
   			if(decision.equals("y")){ //team is accepted
   				suspense(1000);
   				break;
   			}
   			else if (decision.equals("n")){ //team is rejected
   				pokemonlist=cleanlist; //resets the list of pokemon to recover the blank spaces
   				team = new Pokemon[4]; //resets the team roster to allow for selection again
   				pprint("Please reselect your team!");
   			}
   		}
   		return team;
    }
    public static Pokemon first(Pokemon[]team){
	/* Allows the user to select the pokemon they will be leading off with at the start of the tourney */
	
		pprint(String.format("Select the pokemon you wish to begin the Pokemon ARENA Tourney with!"));
		System.out.println("");
		for (int i=0;i<team.length;i++){ //display
			System.out.printf("%2d)Pokemon: %-10s Type: %-10s Weakness: %-10s Resistance: %-10s\n",i,team[i].nameget(),team[i].typeget(),team[i].weakget(),team[i].resisget());
		}
		System.out.println("");
		Scanner kb = new Scanner(System.in);
		while (true){
			int num = getNum(kb); //check to see if input is a number
			if((num < 0) || (num > team.length-1)){ //check to see if number is within range of selection
   				pprint(String.format("That is an invalid pokemon! Pick another one!"));
			}
			else{
				team[num].battlestatus(); 
				return team[num];
			}
		}
	}
	public static String enemyselect(String[] pokemonlist){
    /* This method randomly returns a pokemon string without repeats*/
       
    	String chosen = "";
    	while (true){
    		int selected = (int)(Math.random()*pokemonlist.length);
    		while (!pokemonlist[selected].equals("")){ //will not select blank lines 
    			chosen = pokemonlist[selected];
    			pokemonlist[selected] = ""; //prevents the previously picked line from being chosen a second time
    			return chosen; 
    		}
    	}
    }
    
    public static void intro(){ //opening screen
		pprint("			    	                       	.::.                            ");           
		pprint("			              		      .;:**'                            ");
		pprint("			                                                                ");             
		pprint("		  	 .:XHHHHk.              db.   .;;.     dH  MX               	");
		pprint("			oMMMMMMMMMMM       ~MM  dMMP :MMMMMR   MMM  MR      ~MRMN       ");
		pprint("			QMMMMMb  'MMX       MMMMMMP !MX' :M~   MMM MMM  .oo. XMMM 'MMM  ");
		pprint("			  `MMMM.  )M> :X!Hk. MMMM   XMM.o'  .  MMMMMMM X?XMMM MMM>!MMP  ");
		pprint("			   'MMMb.dM! XM M'?M MMMMMX.`MMMMMMMM~ MM MMM XM `' MX MMXXMM   ");
		pprint("			    ~MMMMM~ XMM. .XM XM`^MMMb.~*?**~ .MMX M t MMbooMM XMMMMMP   ");
		pprint("			     ?MMM>  YMMMMMM! MM   `?MMRb.    `'''   !L'MMMMM XM IMMM    ");
		pprint("			      MMMX   'MMMM'  MM       ~%:           !Mh.''' dMI IMMP    ");
		pprint("			      'MMM.                                             IMX     ");
		pprint("			       ~M!M                                             IMP     ");
		suspense(2000);
		pprint("			                               ARENA                            ");
		suspense(750);
		pprint("			                                                                ");
		pprint("			                     Please press start to begin!               ");
		suspense(3500); //hah
		pprint("			        (Just kidding! There is no start button on a computer.) ");
		suspense(750);
		pprint("			                                                                ");
	    pprint("			                        Let the story begin!                    ");
	    suspense(2000);
	}
	public static String story(){ //gets the player's name
    	Scanner kb = new Scanner (System.in);
    	pprint("Hello trainer! What is your name?");
    	String trainername = kb.nextLine();
    	pprint("Well that's a funny name! Welcome " + trainername + " to the Pokemon ARENA Tourney!");
    	suspense(1000);
    	pprint("This tourney is an international competition, calling in only the strongest pokemon from throughout the Kanto and Johto regions."); 
    	suspense(1000);	
    	pprint("You, lucky trainer, have been selected to compete within the tourney for the coveted title 'Trainer Supreme'...") ;
    	suspense(1000);
    	pprint("...as well as bragging rights and a single hug from a Pikachu!");
    	suspense(1000);
    	pprint("You will be given a list of Pokemon to choose from. Pick four of them. The rest will be your opponents out of jealousy.");
    	suspense(1000); 
    	pprint("In order to be declared 'Trainer Supreme' You must go through the tournament and defeat every last one of your opponents.");
    	suspense(1000);
    	pprint("Good luck!");
    	suspense(1000);
    	pprint("Prepare to create your team!");
    	suspense(1000);
    	return trainername;
    }
    public static void conclusion(int knockouts, int tournamentsize, String playername){
    	suspense(3000);
		spacemaker();
		if (knockouts >= tournamentsize){//Defeated all the opponent pokemon!
			pprint(String.format("Congratulations %s, you won the Pokemon ARENA Tourney! You will now be crowned 'Trainer Supereme'.\nEnjoy your hug from a Pikachu(maybe)!\n",playername));
			suspense (2000);
			pprint("quu..__");
			pprint("$$$b  `---.__");
			pprint("'$$b        `--.                          ___.---uuudP");
			pprint("   `$$b           `.__.------.__     __.---'      $$$$'              .");
			pprint("     '$b          -'            `-.-'            $$$'              .'|");
			pprint("       '.                                       d$'             _.'  |");
			pprint("         `.   /                              ...'             .'     |");
			pprint("           `./                           ..::-'            _.'       |");
			pprint("            /                         .:::-'            .-'         .'");
			pprint("           :                          ::''%          _.'            |");
			pprint("          .' .-.             .-.           `.      .'               |");
			pprint("          : /'$$|           .@$@\\           `.   .'              _.-'");
			pprint("         .'|$u$$|          |$$,$$|           |  <            _.-'");
			pprint("         | `:$$:'          :$$$$$:           `.  `.       .-'");
			pprint("         :                  `'--'             |    `-.     \\ ");
			pprint("        :##.       ==             .###.       `.      `.    `\\ ");
			pprint("        |##:                      :###:        |        >     >");
			pprint("        |#'     `..'`..'          `###'        x:      /     /");
			pprint("         \\                                   xXX|     /    ./");
			pprint("          \\                                xXXX'|    /   ./");
			pprint("          /`-.                                  `.  /   /       Hug me! ");
			pprint("         :    `-  ...........,                   | /  .'");
			pprint("         |         ``:::::::'       .            |<    `.");
			pprint("         |             ```          |           x| \\ `.:``.");
			pprint("         |                         .'    /'   xXX|  `:`M`M':.");
			pprint("         |    |                    ;    /:' xXXX'|  -'MMMMM:'");
			pprint("         `.  .'                   :    /:'       |-'MMMM.-'");
			pprint("          |  |                   .'   /'        .'MMM.-'");
			pprint("          `'`'                   :  ,'          |MMM<");
			pprint("            |                     `'            |tbap\\ ");
			pprint("             \\                                  :MM.-'");
			pprint("              \\                 |              .''");
			pprint("               \\.               `.            /");
			pprint("                /     .:::::::.. :           /");
			pprint("               |     .:::::::::::`.         /");
			pprint("               |   .:::------------\\       /");
			pprint("              /   .''               >::'  /");
			pprint("              `',:                 :    .'");
			pprint("                                   `:.:' ");
		}
		else{ //Was defeated by all the opponent pokemon...
			pprint(String.format("Oh so sorry on your humiliating and crushing defeat.\nYou should have focused your chi more!\nWell better luck next time %s!",playername));
		}
    }
    public static void battlecry(Pokemon goodguy){ //This method sends out a pokemon empowering cry of battle
    	pprint(String.format("%s I choose you!\n",goodguy.nameget()));
    }
	public static void optiondisplay(Pokemon inbat){ //display for the general battle menu 
		System.out.println("");
    	System.out.println("<-------------?WHAT WILL YOU DO?------------->"); 
    	System.out.println("");
    	System.out.println("0} Attack");					
    	System.out.println("");
    	System.out.println("1} Pass ");
    	System.out.println("");
    	System.out.println("2} Retreat");
    	System.out.println("");
    	System.out.printf(" HP: %d                           Energy: %2d\n",inbat.hpget(),inbat.energyget());
    	System.out.println("<-------------?WHAT WILL YOU DO?------------->"); 
    	System.out.println("");
	}
	public static void teamdisplay(Pokemon inbat, Pokemon[]team){ //display for selecting a team member to switch into
    	System.out.println("<---!SELECT A POKEMON!-------------------------------!SELECT A POKEMON!-------------------------------!SELECT A POKEMON!--->");  
    	System.out.println("");   
    	for (int i=0;i<team.length;i++){
    		String status = ""; //display status of the pokemon
			if (team[i].alive()){
				if (team[i].inbattleget() == true){
					status = "In Battle";
				}
				else if (team[i].disabledget()){
					status = "Disabled";
				}
				else{
					status = "Ready";
				}
			}
			else if (team[i].alive() == false){
				status = "Knocked Out";
			}
			System.out.printf("%2d)Pokemon: %-10s Type: %-10s Weakness: %-10s Resistance: %-10s HP:%-5s Energy:%-5s Status: %s\n",i,team[i].nameget(),team[i].typeget(),team[i].weakget(),team[i].resisget(),team[i].hpget(),team[i].energyget(),status);
			System.out.println("");
    	}
    	if (inbat.alive() == true){
    		System.out.printf("%2d)Back\n", team.length);
    		System.out.println("");
    	}
    	System.out.println("<---!SELECT A POKEMON!-------------------------------!SELECT A POKEMON!-------------------------------!SELECT A POKEMON!--->"); 
    	System.out.println(""); 
    }
    public static void attackdisplay(Pokemon inbat, String[][] attackarray, int attacknum){ //displays attack pool of the pokemon
    	System.out.println("");
    	System.out.println("<--------------------!SELECT A MOVE!-------------------->"); 
    	
    	for (int i=0;i<attacknum;i++){
    		System.out.println("");
    		System.out.printf ("%1d) %-12s Energy:%3d Power:%3d Special: %-10s",i, attackarray[i][0], Integer.parseInt(attackarray[i][1]), Integer.parseInt(attackarray[i][2]),attackarray[i][3]);
    		System.out.println("");
    	}

    	System.out.println("");
    	System.out.printf("%d) Back",attacknum); //back out option
    	System.out.println("");
    	System.out.println("");
    	System.out.printf(" HP: %d                                      Energy: %2d\n",inbat.hpget(),inbat.energyget());
    	System.out.println("<--------------------!SELECT A MOVE!-------------------->");
    	System.out.println("");
    }
	
	public static void spacemaker(){ //method to make spaces to provide a false sense of window clearing
		for (int i=0;i<100;i++){
			System.out.println("");
		}
	}
	public static void suspense(int time){ 
	/*Temporarily freezes time and space, resulting in pauses capable 
	 *startling the heavens and providing a more dramatic feel to the text*/
	 
		try {
		    Thread.sleep(time);
		} catch(InterruptedException e) {
		    Thread.currentThread().interrupt();
		}
	}
	public static void pprint(String s){
	/*This methods prints strings in a pretty way*/
		if (s.length() == 0){
			System.out.println("");
		}
		else{
			for (int i=0; i<s.length()-1; i++){
				if (s.charAt(i) != ' '){
					try {
						Thread.sleep(40);
					} catch(InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
				System.out.print(s.charAt(i));
			}
			System.out.println(s.charAt(s.length()-1));
		}
	}
}   