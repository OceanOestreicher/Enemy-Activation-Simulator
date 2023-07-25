import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.util.*;

public class Loader {

    private static String DECK_FOLDER = "./Decks";
    private static LinkedList<String> deckNames;
    private static HashMap<String, ArrayList<Image>>decks;
    private static HashMap<String,ArrayList<Integer>>reactionaryCards;

    public static LinkedList<String> getDeckNames(){
        if(deckNames == null)loadDecks();
        return deckNames;
    }
    public static ArrayList<Image>getDeck(String deckName){
        return decks.get(deckName);
    }
    public static ArrayList<Integer>getReactionaryCardList(String deckName){
        return reactionaryCards.get(deckName);
    }
    public static HashMap<String,ArrayList<Image>> getDecks(){
        if(decks==null)loadDecks();
        return decks;
    }
    public static void loadState(StateManager currentState){
        try{
            Scanner fin = new Scanner(new File("save.dat"));
            ArrayList<String>data = new ArrayList<>();
            while(fin.hasNextLine()){
                data.add(fin.nextLine());
            }
            fin.close();
            for(int i = 0; i < data.size();i++){
                String line = data.get(i);
                if(!(line.isBlank())||!(line.isEmpty())){

                    switch(i){
                        case 0->{
                            String[] elements = line.split(" ");
                            int[] deckInformation = new int[elements.length];
                            for(int j = 0; j< elements.length;j++){
                                deckInformation[j] = Integer.parseInt(elements[j]);
                            }
                            currentState.setCurrentDeckInformation(deckInformation);
                        }
                        case 1->{
                            String[] elements = line.split(" ");
                            boolean[] flagInformation = new boolean[elements.length];
                            for(int j = 0; j< elements.length;j++){
                                flagInformation[j] = elements[j].equals("true");
                            }
                            currentState.setFlags(flagInformation);
                        }
                        case 2->{
                            currentState.setSelectedDeck(line);
                        }
                        case 3->{
                            String[] elements = line.split(" ");
                            int[] drawnCards = new int[elements.length];
                            for(int j = 0; j< elements.length;j++){
                                drawnCards[j] = Integer.parseInt(elements[j]);
                            }
                            currentState.setDrawnCards(drawnCards);
                        }
                        case 4->{
                            String[] elements = line.split(" ");
                            int[] activeReactionaryCards = new int[elements.length];
                            for(int j = 0; j< elements.length;j++){
                                activeReactionaryCards[j] = Integer.parseInt(elements[j]);
                            }
                            currentState.setActiveReactionaryCards(activeReactionaryCards);
                        }
                        case 5->{
                            String[] elements = line.split(" ");
                            int[] discardedReactionaryCards = new int[elements.length];
                            for(int j = 0; j< elements.length;j++){
                                discardedReactionaryCards[j] = Integer.parseInt(elements[j]);
                            }
                            currentState.setDiscardedReactionaryCards(discardedReactionaryCards);
                        }
                        case 6->{
                            String[] elements = line.split(" ");
                            int[] cycledContents = new int[elements.length];
                            for(int j = 0; j< elements.length;j++){
                                cycledContents[j] = Integer.parseInt(elements[j]);
                            }
                            currentState.setCycledContents(cycledContents);
                        }
                        case 7->{
                            currentState.setLastDrawnIndex(Integer.parseInt(line));
                        }
                        default->{
                            throw new RuntimeException(".dat file is wrong size");
                        }
                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
    public static boolean previousStateExists(){
        return new File("save.dat").exists();
    }
    public static void deleteState(){
        File save = new File("save.dat");
        save.delete();
    }
    public static void saveState(StateManager currentState){
        try{
            PrintWriter fout = new PrintWriter("save.dat");
            for(int i: currentState.getCurrentDeckInformation()){
                fout.print(i + " ");
            }
            fout.println();
            for(boolean b: currentState.getFlags()){
                fout.print(b + " ");
            }
            fout.println();
            fout.println(currentState.getSelectedDeck());
            for(Integer i: currentState.getDrawnCards()){
                fout.print(i + " ");
            }
            fout.println();
            for(Integer i: currentState.getActiveReactionaryCards()){
                fout.print(i + " ");
            }
            fout.println();
            for(Integer i: currentState.getDiscardedReactionaryCard()){
                fout.print(i + " ");
            }
            fout.println();
            for(Integer i: currentState.getCycledContents()){
                fout.print(i + " ");
            }
            fout.println();
            fout.println(currentState.getLastDrawnIndex());
            fout.flush();
            fout.close();

        }
        catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static void loadDecks(){
        try{
            File dir = new File(DECK_FOLDER);
            deckNames = new LinkedList<>();
            decks = new HashMap<>();
            reactionaryCards = new HashMap<>();
            for(File d: Objects.requireNonNull(dir.listFiles())){
                ArrayList<Image> deck = new ArrayList<>();
                if(d.getName().equals("base"))deckNames.add("Enemy Activation Deck");
                else deckNames.add(d.getName());
                int index = 0;
                ArrayList<Integer>reactionaryIndices = new ArrayList<>();
                for(File i: Objects.requireNonNull(d.listFiles())){
                    if(i.getName().matches("R_.*")){
                        reactionaryIndices.add(index);
                    }
                    index++;
                    BufferedImage card = ImageIO.read(i);
                    deck.add(card);
                }
                decks.put(deckNames.get(deckNames.size()-1),deck);
                if(reactionaryIndices.size()>0){
                    reactionaryCards.put(d.getName(),reactionaryIndices);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error Loading Decks");
        }
    }
}
