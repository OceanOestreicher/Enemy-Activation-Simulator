import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class StateManager implements ActionListener {

    public static final String MAIN_CANVAS = "Main";
    public static final String BOSS_REFERENCE_CANVAS = "Reference";
    public static final String REACTIONARY_CANVAS = "Reactionary";
    public static final String START_BUTTON = "Start";
    public static final String DRAW_BUTTON = "Draw";
    public static final String RESET_DECK_BUTTON = "Reset";
    public static final String NEXT_REACTION_BUTTON = "Next Reaction";
    public static final String REMOVE_REACTION_BUTTON = "Remove Reaction";
    public static final String SWITCH_PHASE_BUTTON = "Switch Phase";
    public static final String DECK_CYCLE_LABEL = "Deck Cycle";
    public static final String DECK_COUNT_LABEL = "Deck Count";
    public static final String REACTION_COUNT_LABEL = "Reaction Count";
    public static final String DECK_SELECTOR_COMBO_BOX = "Deck Selector";
    private CardCanvas mainCanvas,bossReferenceCanvas,bossReactionCanvas;
    private JButton start,drawCard,resetDeck,cycleReactionaryCard,removeReactionaryCard,switchBossPhase;
    private JLabel deckCycleLabel,deckCountLabel,reactionCountLabel;
    private JComboBox<String>deckSelector;
    private int currentDeckCycle,currentDeckCount,deckSize,lastDrawnIndex;
    private boolean isBoss,switchedPhase,active;
    private ArrayList<Image> selectedDeck;
    private HashSet<Integer> drawnCards, activeReactionaryCards,discardedReactionaryCards;
    private HashMap<Integer,Integer> damagedCards;
    private final CyclingList reactionaryCardIndices;
    private final Random rand;
    private final MainFrame root;
    public StateManager(MainFrame root){
        this.root = root;
        currentDeckCount = 0;
        currentDeckCycle = 1;
        isBoss = false;
        switchedPhase = false;
        active = false;
        drawnCards = new HashSet<>();
        rand = new Random();
        activeReactionaryCards = new HashSet<>();
        discardedReactionaryCards = new HashSet<>();
        reactionaryCardIndices = new CyclingList();
        damagedCards = new HashMap<>();
    }
    public void addComboBox(JComboBox<String>box,String type){
        box.addActionListener(this);
        if(type.equals(DECK_SELECTOR_COMBO_BOX)){
            deckSelector = box;
            deckSelector.setName("Deck Selector");
            selectedDeck = Loader.getDeck(deckSelector.getSelectedItem().toString());
            deckSize = selectedDeck.size();
        }
        else throw new RuntimeException("Invalid Combo Box Type");
    }
    public void addLabel(JLabel label,String type){
        switch (type) {
            case DECK_CYCLE_LABEL -> {
                deckCycleLabel = label;
                deckCycleLabel.setText("Deck Cycle: " + currentDeckCycle);
                deckCycleLabel.setEnabled(false);
            }
            case DECK_COUNT_LABEL -> {
                deckCountLabel = label;
                deckCountLabel.setText("0/24");
                deckCountLabel.setEnabled(false);
            }
            case REACTION_COUNT_LABEL -> {
                reactionCountLabel = label;
                reactionCountLabel.setText("None");
                reactionCountLabel.setEnabled(false);
            }
            default -> throw new RuntimeException("Invalid Label Type");
        }
    }
    public void addCanvas(CardCanvas canvas,String type){
        switch (type) {
            case MAIN_CANVAS -> mainCanvas = canvas;
            case BOSS_REFERENCE_CANVAS -> bossReferenceCanvas = canvas;
            case REACTIONARY_CANVAS ->bossReactionCanvas = canvas;
            default -> throw new RuntimeException("Invalid Canvas Type");
        }
    }
    public void addButton(JButton button,String type){
        button.addActionListener(this);
        switch (type) {
            case START_BUTTON -> {
                start = button;
                start.setName("Start");
            }
            case DRAW_BUTTON -> {
                drawCard = button;
                drawCard.setName("Draw");
                drawCard.setEnabled(false);
            }
            case RESET_DECK_BUTTON -> {
                resetDeck = button;
                resetDeck.setName("Reset Deck");
                resetDeck.setEnabled(false);
            }
            case NEXT_REACTION_BUTTON -> {
                cycleReactionaryCard = button;
                cycleReactionaryCard.setName("Next Reactionary");
                cycleReactionaryCard.setEnabled(false);
            }
            case REMOVE_REACTION_BUTTON -> {
                removeReactionaryCard = button;
                removeReactionaryCard.setName("Remove Reactionary");
                removeReactionaryCard.setEnabled(false);
            }
            case SWITCH_PHASE_BUTTON -> {
                switchBossPhase = button;
                switchBossPhase.setName("Switch Phase");
                switchBossPhase.setEnabled(false);
            }
            default -> throw new RuntimeException("Invalid Button Type");
        }
    }
    public boolean isReady(){
        return mainCanvas != null && bossReferenceCanvas != null && bossReactionCanvas != null && start != null
                && drawCard != null && resetDeck != null && cycleReactionaryCard != null && removeReactionaryCard != null
                && switchBossPhase != null && deckCycleLabel != null && deckCountLabel != null && reactionCountLabel != null
                && deckSelector != null;
    }
    public boolean activeState(){
        return active;
    }
    public int[] getCurrentDeckInformation(){
        return new int[]{currentDeckCycle,currentDeckCount,deckSize};
    }
    public void setCurrentDeckInformation(int[]deckInfo){
        currentDeckCycle = deckInfo[0];
        currentDeckCount = deckInfo[1];
        deckSize = deckInfo[2];
    }
    public boolean[] getFlags(){
        return new boolean[]{isBoss,switchedPhase};
    }
    public void setFlags(boolean[] flags){
        isBoss = flags[0];
        switchedPhase = flags[1];
    }
    public String getSelectedDeck(){
        return deckSelector.getSelectedItem().toString();
    }
    public void setSelectedDeck(String deckName){
        selectedDeck = Loader.getDeck(deckName);
        for(int i = 0; i < deckSelector.getItemCount();i++){
            if(deckSelector.getItemAt(i).toString().equals(deckName)){
                deckSelector.setSelectedIndex(i);
                break;
            }
        }
    }
    public Integer[] getDrawnCards(){
        Integer[] cards = new Integer[drawnCards.size()];
        int index = 0;
        for(Object i: drawnCards.toArray()){
            cards[index] = (Integer)i;
            index++;
        }
        return cards;
    }
    public void setDrawnCards(int[] drawn){
        for(int i: drawn){
            drawnCards.add(i);
        }
    }
    public Integer[] getActiveReactionaryCards(){
        Integer[] cards = new Integer[activeReactionaryCards.size()];
        int index = 0;
        for(Object i: activeReactionaryCards.toArray()){
            cards[index] = (Integer)i;
            index++;
        }
        return cards;
    }
    public void setActiveReactionaryCards(int[] active){
        for(int i: active){
            activeReactionaryCards.add(i);
        }
    }
    public Integer[] getDiscardedReactionaryCard(){
        Integer[] cards = new Integer[discardedReactionaryCards.size()];
        int index = 0;
        for(Object i: discardedReactionaryCards.toArray()){
            cards[index] = (Integer)i;
            index++;
        }
        return cards;
    }
    public void setDiscardedReactionaryCards(int[] discarded){
        for(int i: discarded){
            discardedReactionaryCards.add(i);
        }
    }
    public Integer[] getCycledContents(){
        return reactionaryCardIndices.toArray();
    }
    public void setCycledContents(int[] contents){
        reactionaryCardIndices.addAll(contents);
    }
    public int getLastDrawnIndex(){
        return lastDrawnIndex;
    }
    public void setLastDrawnIndex(int last){
        lastDrawnIndex = last;
    }
    public ArrayList<Integer[]>getDamagedCards(){
        if(damagedCards.size() == 0 && (bossReactionCanvas.getDamage() > 0 || bossReferenceCanvas.getDamage()>0)){
            if(bossReferenceCanvas.getDamage()>0){
                damagedCards.put(0,bossReferenceCanvas.getDamage());
            }
            if(bossReactionCanvas.getDamage()>0){
                damagedCards.put(reactionaryCardIndices.getCurrentVal(),bossReactionCanvas.getDamage());
            }
        }
        else if(damagedCards.size()==0) return new ArrayList<>();
        ArrayList<Integer[]>contents = new ArrayList<>();
        for(int i = 0; i <2;i++){
            if(damagedCards.get(i)!=null){
                Integer[]pair = new Integer[2];
                pair[0] = i;
                pair[1] = damagedCards.get(i);
                contents.add(pair);
            }
        }
        for(Integer i: reactionaryCardIndices.toArray()){
            Integer[] pair = new Integer[2];
            pair[0] = i;
            pair[1] = damagedCards.get(i);
            contents.add(pair);
        }
        return contents;
    }
    public void setDamagedCards(ArrayList<Integer[]> data){
        for(Integer[] pair: data){
            damagedCards.put(pair[0],pair[1]);
        }
    }
    public void restoreState(){
        bossReactionCanvas.setDamage(damagedCards.get(reactionaryCardIndices.getCurrentVal()));
        updateReactionaryComponents();
        updateLabel(deckCountLabel);
        updateLabel(deckCycleLabel);
        updateLabel(reactionCountLabel);
        paintCanvas(mainCanvas,selectedDeck.get(lastDrawnIndex));
        if(isBoss){
            bossReferenceCanvas.setDamage(damagedCards.get(0));
            paintCanvas(bossReferenceCanvas,selectedDeck.get(0));
        }
        root.displayBossPanel(isBoss);
        startScenario();
    }
    private void checkIfBoss(){
        isBoss = !deckSelector.getSelectedItem().equals("Enemy Activation Deck");
    }
    private void startScenario(){
        deckSelector.setEnabled(false);
        deckCountLabel.setEnabled(true);
        deckCycleLabel.setEnabled(true);
        start.setEnabled(false);
        drawCard.setEnabled(true);
        resetDeck.setEnabled(true);
        if(isBoss){
            switchBossPhase.setEnabled(true);
        }
        else root.displayBossPanel(false);
        active = true;
    }
    private void endScenario(){
        deckSelector.setEnabled(true);
        deckSelector.setSelectedIndex(0);
        start.setEnabled(true);
        drawCard.setEnabled(false);
        resetDeck.setEnabled(false);
        cycleReactionaryCard.setEnabled(false);
        removeReactionaryCard.setEnabled(false);
        switchBossPhase.setEnabled(false);
        reactionCountLabel.setEnabled(false);
        deckCycleLabel.setEnabled(false);
        deckCountLabel.setEnabled(false);
        isBoss = false;
        switchedPhase =false;
        active = false;

        drawnCards.clear();
        discardedReactionaryCards.clear();
        activeReactionaryCards.clear();
        reactionaryCardIndices.clear();

        bossReactionCanvas.clearCanvas();
        mainCanvas.clearCanvas();
        bossReferenceCanvas.clearCanvas();

        currentDeckCount = 0;
        currentDeckCycle = 1;
        lastDrawnIndex = -1;
        updateLabel(deckCycleLabel);
        updateLabel(deckCountLabel);
        updateLabel(reactionCountLabel);
        root.displayBossPanel(true);
    }
    private void paintCanvas(CardCanvas canvas,Image card){
        canvas.paintCard(card);
    }
    private void updateLabel(JLabel label){
        if(label == deckCycleLabel){
            label.setText("Deck Cycle: " + currentDeckCycle);
        }
        else if(label == deckCountLabel){
            label.setText(currentDeckCount + "/"+deckSize);
        }
        else if(label == reactionCountLabel){
            if(activeReactionaryCards.size() == 0){
                label.setText("None");
            }
            else label.setText(reactionaryCardIndices.getCurrentPosition()+"/"+activeReactionaryCards.size());
        }
        else throw new IllegalArgumentException("Invalid label passed");
    }

    private void drawCard(){
        if(drawnCards.size() == deckSize){
            drawnCards.clear();
            if(isBoss){
                deckSize = selectedDeck.size()-2-activeReactionaryCards.size();
                discardedReactionaryCards.clear();
            }
            currentDeckCycle += 1;
            currentDeckCount =1;
            updateLabel(deckCycleLabel);
            updateLabel(deckCountLabel);
        }
        else{
            currentDeckCount+= 1;
            updateLabel(deckCountLabel);
        }
        int cardIndex = rand.nextInt((isBoss?2:0),selectedDeck.size());
        while(drawnCards.contains(cardIndex)||activeReactionaryCards.contains(cardIndex)||discardedReactionaryCards.contains(cardIndex)){
            cardIndex = rand.nextInt((isBoss?2:0),selectedDeck.size());
        }
        lastDrawnIndex = cardIndex;
        paintCanvas(mainCanvas,selectedDeck.get(cardIndex));

        if(isBoss && Loader.getReactionaryCardList(deckSelector.getSelectedItem().toString()).contains(cardIndex)){
            activeReactionaryCards.add(cardIndex);
            reactionaryCardIndices.add(cardIndex);
            updateReactionaryComponents();
        }
        drawnCards.add(cardIndex);
    }

    private void updateReactionaryComponents(){

        if(activeReactionaryCards.size() >= 1){
                paintCanvas(bossReactionCanvas,selectedDeck.get(reactionaryCardIndices.getCurrentVal()));
                cycleReactionaryCard.setEnabled(true);
                removeReactionaryCard.setEnabled(true);
                reactionCountLabel.setEnabled(true);
                updateLabel(reactionCountLabel);
        }
        else{
            bossReactionCanvas.clearCanvas();
            cycleReactionaryCard.setEnabled(false);
            removeReactionaryCard.setEnabled(false);
            reactionCountLabel.setEnabled(false);
            updateLabel(reactionCountLabel);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch(((JComponent)e.getSource()).getName()){
            case "Deck Selector"->{
                checkIfBoss();

                selectedDeck = Loader.getDeck(deckSelector.getSelectedItem().toString());
                deckSize = selectedDeck.size();
                if(isBoss){

                    paintCanvas(bossReferenceCanvas,selectedDeck.get(0));
                    deckSize-=2;
                }
                else{
                    bossReferenceCanvas.clearCanvas();
                }
                updateLabel(deckCountLabel);

            }
            case "Start"->{
                startScenario();
            }
            case "Draw"->{
                drawCard();
            }
            case "Reset Deck"->{
                resetDeck.setName("Confirm Reset");
                resetDeck.setText("Confirm Reset");
            }
            case "Confirm Reset"->{
                resetDeck.setName("Reset Deck");
                resetDeck.setText("Reset Deck");
                endScenario();
                Loader.deleteState();
            }
            case "Next Reactionary"->{
                damagedCards.put(reactionaryCardIndices.getCurrentVal(),bossReactionCanvas.getDamage());
                reactionaryCardIndices.move(CyclingList.RIGHT);
                bossReactionCanvas.setDamage(damagedCards.get(reactionaryCardIndices.getCurrentVal()));
                updateReactionaryComponents();
            }
            case "Remove Reactionary"->{
                discardedReactionaryCards.add(reactionaryCardIndices.getCurrentVal());
                activeReactionaryCards.remove(reactionaryCardIndices.getCurrentVal());
                damagedCards.remove(reactionaryCardIndices.getCurrentVal());
                reactionaryCardIndices.remove();
                bossReactionCanvas.setDamage(damagedCards.get(reactionaryCardIndices.getCurrentVal()));
                updateReactionaryComponents();
            }
            case "Switch Phase"->{
                damagedCards.put((!switchedPhase?0:1),bossReferenceCanvas.getDamage());
                bossReferenceCanvas.setDamage(damagedCards.get(switchedPhase?0:1));
                paintCanvas(bossReferenceCanvas,(switchedPhase?selectedDeck.get(0):selectedDeck.get(1)));
                switchedPhase = !switchedPhase;
            }
        }
    }
}
