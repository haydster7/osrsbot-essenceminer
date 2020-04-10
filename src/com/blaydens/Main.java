package com.blaydens;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.ArrayUtils;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.commons.predicate.Predicates;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.Script;
import org.rspeer.script.ScriptMeta;
import org.rspeer.ui.Log;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@ScriptMeta(developer = "haydster7", desc = "Mines rune essence", name = "EssenceMiner3000")
public class Main extends Script {

    private Player me;
    private String currentAction = "";

    private static final int frameActionWait = 2;
    private static int iterationsSinceLastAction = frameActionWait;

    private static final String[] ACTIONS = {
            "Walk to wizard",
            "Teleport",
            "Mine rune essence",
            "Portal",
            "Walk to bank",
            "Open bank",
            "Deposit"
    };

    private static enum Level {
        DEBUG,
        INFO,
        USER,
        ERROR
    }

    private static final Level DEBUG_LEVEL = Level.INFO;

    private static final String ESSENCE_NODE_NAME = "Rune Essence";
    private static final Predicate<SceneObject> ESSENCE_NODE_PREDICATE = sceneObject -> sceneObject.getName().equals(ESSENCE_NODE_NAME);

    private static final String PORTAL_ACTION_USE = "Use";
    private static final String PORTAL_ACTION_EXIT = "Exit";
    private static final Predicate<Npc> PORTAL_NPC_PREDICATE = p -> p.containsAction(PORTAL_ACTION_EXIT) || p.containsAction(PORTAL_ACTION_USE);
    private static final Predicate<SceneObject> PORTAL_SO_PREDICATE = p -> p.containsAction(PORTAL_ACTION_EXIT) || p.containsAction(PORTAL_ACTION_USE);

    private static final Position BANK_POSITION = new Position(3253, 3422);
    private static final String BANK_NAME = "Bank booth";
    private static final Predicate<SceneObject> BANK_PREDICATE = sceneObject -> sceneObject.getName().equals(BANK_NAME);

    private static final Position WIZARD_POSITION = new Position(3252, 3399);
    private static final String WIZARD_NAME = "Aubury";
    private static final Predicate<Npc> WIZARD_PREDICATE = npc -> npc.getName().equals(WIZARD_NAME);


    @Override
    public void onStart() {
        cLog("Welcome to essence miner", Level.USER);
    }

    @Override
    public int loop() {
        me = Players.getLocal();
        iterationsSinceLastAction++;
        setAction();

        return Random.nextInt(390, 650);
    }

    @Override
    public void onStop() {
        cLog("Goodbye. Thanks for using essence miner", Level.USER);
    }

    private void setAction(){
        String newAction = "";

        cLog("Choosing action to perform");
        newAction = chooseNextAction();

        if(!newAction.isEmpty()){
            int newActionIndex = getActionIndex(newAction);
            cLog("Action chosen: " +newAction);
            cLog("Action index: " + newActionIndex);
            newAction = ACTIONS[newActionIndex];

            if(!newAction.equals(currentAction)){
                cLog("Completed: " + currentAction, Level.INFO);
                cLog("Starting: " + newAction, Level.INFO);
                currentAction = newAction;
                performAction(newAction);
                iterationsSinceLastAction = 0;
            }

            if(!me.isAnimating() && !me.isMoving()) {
                if(iterationsSinceLastAction >= frameActionWait){
                    performAction(newAction);
                    iterationsSinceLastAction = 0;
                }
            } else {
                //currentAction = "";
            }

        } else {
            cLog("I'm kinda confused, what do I do now?", Level.INFO);
        }

    }

    private String chooseNextAction(){

        SceneObject essenceNode = SceneObjects.getNearest(ESSENCE_NODE_PREDICATE);

        if(!Inventory.isFull()){
            cLog("Inventory has space");

            Npc wizard = Npcs.getNearest(WIZARD_PREDICATE);

            //No Rune Essence nodes present
            if(essenceNode == null){
                cLog("No Rune Essence nodes present");
                //No teleporting wizard present
                if(wizard == null || !Movement.isInteractable(WIZARD_POSITION)){
                    cLog("No teleporting wizard present or not reachable");
                    //Walk to wizard
                    return "Walk to wizard";

                //Teleporting wizard present
                } else {
                    cLog("Teleporting wizard present");
                    //Get wizard to teleport me
                    return "Teleport";

                }
            //Rune Essence nodes present
            } else {
                cLog("Rune Essence nodes present");
                //Mine essence
                return "Mine rune essence";
            }
        //Inventory is full
        } else {
            cLog("Inventory is full");

            //No Rune Essence nodes present
            if(essenceNode == null){
                cLog("No Rune Essence nodes present");
                SceneObject bank = SceneObjects.getNearest(BANK_PREDICATE);

                //No Bank present or not reachable
                if(bank == null || !Movement.isInteractable(BANK_POSITION)){
                    cLog("No bank present or not reachable");
                    return "Walk to bank";

                //Bank present
                } else {
                    cLog("Bank present");

                    //In bank screen
                    if(Bank.isOpen()){
                        cLog("In bank screen");
                        return "Deposit";

                    //Not in bank screen
                    } else {
                        cLog("Not in bank screen");
                        return "Open bank";
                    }

                }

            //Rune Essence nodes present
            } else {
                cLog("Rune Essence nodes present");
                return "Portal";
            }
        }


    }

    private void performAction(String action){
        cLog("Performing action: " + action, Level.INFO);
        /*
         "Walk to wizard",
         "Teleport",
         "Mine rune essence",
         "Portal",
         "Walk to bank",
         "Open bank",
         "Deposit"
         */

        switch(action) {
            case "Walk to wizard":
                action_walkToWizard();
                break;
            case "Teleport":
                action_teleport();
                break;
            case "Mine rune essence":
                action_mineRuneEssence();
                break;
            case "Portal":
                action_portal();
                break;
            case "Walk to bank":
                action_walkToBank();
                break;
            case "Open bank":
                action_openBank();
                break;
            case "Deposit":
                action_deposit();
                break;
            default:
                cLog("Action: " + action + " has not been coded yet", Level.INFO);
        }
    }

    private void action_walkToWizard(){
        Movement.walkTo(WIZARD_POSITION);
    }

    private void action_teleport(){
        Npc wizard = Npcs.getNearest(WIZARD_PREDICATE);
        wizard.interact("Teleport");
    }

    private void action_mineRuneEssence(){
        SceneObject essenceNode = SceneObjects.getNearest(ESSENCE_NODE_PREDICATE);
        essenceNode.interact("Mine");
    }

    private void action_portal(){
        Npc portalNpc = Npcs.getNearest(PORTAL_NPC_PREDICATE);
        String action = PORTAL_ACTION_USE;

        if(portalNpc != null){
            if(portalNpc.containsAction(PORTAL_ACTION_EXIT)){
                action = PORTAL_ACTION_EXIT;
            }
            portalNpc.interact(action);
        } else {
            SceneObject portalSceneObject = SceneObjects.getNearest(PORTAL_SO_PREDICATE);
            if(portalSceneObject.containsAction(PORTAL_ACTION_EXIT)){
                action = PORTAL_ACTION_EXIT;
            }
            portalSceneObject.interact(action);
        }
    }

    private void action_walkToBank(){
        Movement.walkTo(BANK_POSITION);
    }

    private void action_openBank(){
        SceneObject bank = SceneObjects.getNearest(BANK_PREDICATE);
        bank.interact("Bank");
    }

    private void action_deposit(){
        //Bank.depositInventory();
        Pattern pickaxePattern = Pattern.compile(".*pickaxe");
        Bank.depositAllExcept(pickaxePattern);
    }

    private int getActionIndex(String actionName){
        for(int i = 0; i < ACTIONS.length; i++){
            if(ACTIONS[i].equals(actionName)){
                return i;
            }
        }
        return -1;
    }


    /*
    * Go near to closest TP wizard
    * Interact with closest TP wizard once they are in sight (and maybe obstructions such as doors are clear?)
    * Choose teleport from dialog
    * Wait for teleport to complete
    * Interact with rune essence pillar
    * Wait until inventory full
    * Interact with portal
    * Go to bank
    * Deposit rune essence
    * -->
    * */

}
