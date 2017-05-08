/*
 * @(#)Pokemon.java
 *
 *
 * @Shahir Chowdhury
 * @version 1.00 2014/11/27
 */

import java.util.*;
public class Pokemon {
	private String name;
	private int maxhp; //maximum health of the pokemon 
	private int hp; //current health of the pokemon
	private int energy = 50;
	private String type;
	private String resis;
	private String weak;
	private int attacknum;
	private String attackname;
	private String[][]attackarray; //2D array that stores all the attacks and their properties in a pokemon's move set
	private String special;
	private boolean stunned = false;
	private boolean disabled = false;
	private boolean inbattle = false;
	
    public Pokemon(String poke){
    	String[]pokeparts = poke.split(",");    	
    	name = pokeparts[0];
    	hp = Integer.parseInt(pokeparts[1]);
    	maxhp = hp;
    	type = pokeparts[2];
    	resis = pokeparts[3];
    	weak = pokeparts[4];
    	
    	attacknum = Integer.parseInt(pokeparts[5]);
    	attackarray = new String[attacknum][4];
    	for (int i=0;i<attacknum;i++){
    		attackarray[i][0] = pokeparts[6+(i*4)];        //attack name
    		attackarray[i][1] = pokeparts[6+(1+(i*4))];    //energy cost
    		attackarray[i][2] = pokeparts[6+(2+(i*4))];    //damage value
    		attackarray[i][3] = pokeparts[6+(3+(i*4))];    //special
    	}
    }
    public String nameget(){ //accessor method to get name
        return name;
    }
    public int hpget(){
        return hp;
    }
    public void hpset(int h){
        hp = h;
    }
    public int energyget(){
        return energy;
    }
    public boolean alive(){ //accessor method to see if the pokemon is still able to fight
        return hp > 0;
    }
    public String typeget(){ //accessor method to get type
        return type;
    }
    public String weakget(){
        return weak;
    }
    public String resisget(){
        return resis;
    }
    public String[][] attacksget(){
        return attackarray;
    }
    public int attacknumget(){
        return attacknum;
    }
    public boolean stunget(){ //accessor method to see if the pokemon is stunned
        return stunned;
    }
    public boolean disabledget(){ //accessor method to see if pokemon is disabled
        return disabled;
    }
    public boolean inbattleget(){ //accessor method to get if the pokemon is in battle
    	return inbattle; 
    }
    public void regen(){ //initiates regular recovery of stamina at the end of each round
    	energy += 10;
    	if (energy > 50){
    		energy = 50;
    	}	
    }
    public void fullregen(){ //fully regens energy 
    	energy = 50;
    }
    public void heal(){ //heals the pokemon for 20 health in the case the badguy pokemon was knocked out
    	hp += 20;
    	if (hp > maxhp){
    		hp = maxhp;
    	}
    }
    public boolean energypossible(int cost){ //method to check if an attack is possible
    	if (energy >= cost){
    		return true;
    	}
    	return false;
    }
    public void battlestatus(){ //method to declare what the pokemon's current battle status is
    	if (inbattle == false){
    		inbattle = true;
    	}
    	else{
    		inbattle = false;
    	}
    }
    public void healstun(){ //heal stun 
    	stunned = false;
    }
    public void healdisable(){ //heal disable
        disabled = false;
    }
    public double typecheck(Pokemon defending){
    /* Sets the appropriate damage multiplier on an attack based on the defending
     * pokemon's weakness and resistance and the type of the attacking pokemon */
     
    	if (defending.weak.equals(type)){ //super effective
    		return 2;
    	}
    	if (defending.resis.equals(type)){ //not very effective
    		return 0.5;
    	}
    	return 1; //neutral
    }
    public void typechecktext(double multiplier){
    /* Informs the user with dramatic text on how effective an attack was */
    	
    	if (multiplier == 2){
    		pprint(String.format("It was super effective!"));
    	}
    	if (multiplier == 1){
    		pprint(String.format("It was a direct hit!"));
    	}
    	if (multiplier == 0.5){
    		pprint(String.format("It wasn't very effective."));
    	}
    } 
    public boolean chance(){ //random chance method that has a 50% chance of returning true
    	int chance = (int)(Math.random()*100);
    	if (chance >= 50){
    		return true;
    	}
    	return false;
    }
    
    public boolean wildcard(int selected){ 
    /* Wildcard method for attacks containing the special property "wild card".
     * It creates a random chance for an attack to successfully hit the opponent pokemon. */
        
    	boolean successful = chance();
        if (attackarray[selected][3].equals("wild card")){ 		
           if (successful == false){
                pprint(String.format("%s's attack missed!\n",name));
           }
           return successful;
    	}
        return true;
    }

    public void wildstorm(int selected,Pokemon enemy){
    /* Wildstorm method for attacks containing the special property "wild storm".
     * It creates a random chance of an attack to successfully hit the opponent pokemon. 
     * If the attack does land, it will repeat again for free, going through the same chance of 
     * successfully working and repeating, possibly endlessly. */
     
    	int wildhits = 0; //counter for number of times the attack was successful

    	if (attackarray[selected][3].equals("wild storm")){
	    	while (true){
	    		if (chance() == true){
	    			double multiplier = typecheck(enemy);
	    			int damage = (int)((Integer.parseInt(attackarray[selected][2])*multiplier));
	    			if (this.disabled == true){ //
	    				damage -= 10;
	    			} 		
	    			if (wildhits >= 1){ //to make the act of wildstorming more dramatic
	    				pprint("Wild storm!");
	    			}
	    			if (damage > 0){
	    				enemy.hp -= damage;
	    				typechecktext(multiplier);
	    				pprint(String.format("%s took %d damage!",enemy.name,damage));
	    			}
	    			else{
	    				pprint(String.format("%s took no damage!",enemy.name));
	    			}
	    			wildhits += 1;	
	    		}
	    		else{
	    			if (wildhits > 1){
	    				pprint(String.format("%s finally missed!",name));
	    				pprint(String.format("The attack wild stormed %d time(s)!\n",wildhits-1));
	    			}
                    if (wildhits == 1){
                        System.out.println("");
                    }
	    			if (wildhits == 0){
	    				pprint(String.format("%s's attack missed!",name));
	    			}
	    			break;
	    		}
	    	}
    	}
    }
    public void disable(int selected,Pokemon enemy){
    /* Disable method for attacks containig the special property "disable".
     * Opponent's hit by attacks with the disable property are guaranteed to have their
     * damage output reduced by 10 points. */
     
    	if (attackarray[selected][3].equals("disable")){  
    		if (enemy.disabled == false){
                if (enemy.alive() == true){
                    enemy.disabled = true;
                    pprint(String.format("%s is now disabled! It will now deal less damage!\n",enemy.name));
                }
    		}
            else{
                if (enemy.alive() == true){
                    pprint(String.format("%s is already disabled!",enemy.name));
                }
            }

    	}
    }
    public void stun(int selected,Pokemon enemy){
    /* Stun method for attacks containing the special property "stun".
     * Opponent's hit by attacks with the stun property have a 50% chance to be unable to  
     * attack or retreat for 1 whole turn. */
     
    	if (attackarray[selected][3].equals("stun")){
    		if (chance() == true){
                if (enemy.alive() == true){
                    pprint(String.format("%s was stunned! It must pass its next turn and refocus its chi!\n",enemy.name));
                    enemy.stunned = true;
                }
            }
            else{
                if (enemy.alive() == true){
                    pprint(String.format("%s was unphased by %s!\n",enemy.name, name));
                }
            }
    	}
    }
    public void recharge(int selected){
    /* Recharge method for attacks containing the special property "recharge".
     * Attacks with recharge grant additional energy recovery after being used. */
     
    	if (attackarray[selected][3].equals("recharge")){
    		pprint(String.format("%s regained additional energy!\n",name));
    		energy += 20;
    		if (energy > 50){
    			energy = 50;
    		}
    	}
    }
       
    public boolean pass(){
    /* 1 of the 3 possible commands from the player to the pokemon. This
     * commands tells the pokemon to do nothing */
        if (stunned == false){ //flavour text does not suit a stunned pokemon
            pprint(String.format("%s rested for the turn and recovered some energy!\n",name,name,energy));
        }
    	regen();
    	return true;
    }   
    public boolean goodguyattack(Pokemon badguy){ 
    /* 1 of the 3 possible commands from the player to the pokemon. This command tells the good guy pokemon attacks the badguy pokemon.
     * True return indicates the attack was attempted and had some form of resolution.
     * False return indicates that the user backed out of their decision and did not attack */
     
    	boolean attackselect = false; //flag for when an attack has been selected
    	int selected = 0; //number corresponding to the attack chosen
    	String attackselected = ""; //name of the attack
    	
    	Scanner kb = new Scanner(System.in);//take in the user's selected attack
    	while (attackselect == false){
    		selected = kb.nextInt();
    		if (selected == attacknum){ //player did not choose to attack
    			return false;
    		}
    		if ((selected < attacknum) || (selected < 0)){ //check if attack is a valid move
    			if (energypossible(Integer.parseInt(attackarray[selected][1])) == true){ //check if attack is performable
    				attackselect = true;
    				attackselected = attackarray[selected][0];
    			}
    			else{
    				pprint(String.format("You don't have enough energy for that!"));
    			}
    		}
    		else{
    			pprint(String.format("That is not a valid move!"));
    		}	
    	}
    	
    	pprint(String.format("%s used %s!",name,attackarray[selected][0]));

        wildstorm(selected,badguy);
		
        if(!attackarray[selected][3].equals("wild storm")){
			double multiplier = typecheck(badguy);
            int damage = (int)((Integer.parseInt(attackarray[selected][2])*multiplier));
            boolean hit = wildcard(selected); //flag for if the attack hit 

    		if (disabled == true){
    			damage -= 10;
    		}
            if (hit == true){
        		if (damage > 0){
        			badguy.hp -= damage;
        			typechecktext(multiplier);
        			pprint(String.format("The enemy %s took %d damage!\n",badguy.name,damage));
        		}
        		else if(Integer.parseInt(attackarray[selected][2]) != 0){
        			pprint(String.format("The enemy %s took no damage!\n",badguy.name));
        		}
            }
		}

		disable(selected,badguy); //attack resolution
    	stun(selected,badguy);
    	recharge(selected);
        energy -= Integer.parseInt(attackarray[selected][1]); 
    	this.regen();
    				
    	return true;
    }
   
    public boolean badguyattack(Pokemon goodguy){ //the badguy pokemon attacks the user
    	boolean attackselect = false;
    	boolean attackpossible = false; //flag to see if any attacks are possible
    	int selected = 0;
    	String attackselected = "";
    	
    	for (int i=0;i<attacknum;i++){ //check if any attacks are possible
    		if (energypossible(Integer.parseInt(attackarray[i][1]))){
    			attackpossible = true;
    		}
    	}
    	if (attackpossible == true && stunned == false){
	    	while (attackselect == false){ //badguy randomly selects a viable move
	    		selected = (int)(Math.random()*attacknum); 
	    		if (energypossible(Integer.parseInt(attackarray[selected][1])) == true){
	    			attackselect = true;
	    			attackselected = attackarray[selected][0];
	    		}
	    	}
	    	pprint(String.format("The enemy %s used %s!",name,attackarray[selected][0]));

    		wildstorm(selected,goodguy);
    		
    		if(!attackarray[selected][3].equals("wild storm")){
				double multiplier = typecheck(goodguy);
                int damage = (int)(Integer.parseInt(attackarray[selected][2]) * multiplier);
                boolean hit = wildcard(selected);

    			if (disabled == true){
    				damage -= 10;
    			}
                if (hit == true){
                    if (damage > 0){
                        goodguy.hp -= damage;
                        typechecktext(multiplier);
                        pprint(String.format("%s took %d damage!\n",goodguy.name,damage));
                    }
                    else if(Integer.parseInt(attackarray[selected][2]) != 0){
                        pprint(String.format("The enemy %s dealt no damage!\n",name));
                    }
                }
			}
 
			disable(selected,goodguy);
    		stun(selected,goodguy);
    		recharge(selected);
    		energy -= Integer.parseInt(attackarray[selected][1]);
            this.regen();

    		return true;	
    	}
    	else{ //passes the turn to recover energy
    		stunned = false;
    		pass();
    		return true;
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