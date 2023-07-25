import java.util.Arrays;
import java.util.List;

public class CyclingList {
    private static class Node{

        public Integer data;
        public Node prev,next;
        public Node(Integer num){
            data = num;
        }
    }
    public static final int LEFT = 1, RIGHT = 2;
    private Node head,current, end;
    private int size = 0,currentPosition = -1;
    public int size(){
        return size;
    }
    public int getCurrentVal(){
        return current.data;
    }
    public int getCurrentPosition(){
        return currentPosition;
    }
    public void move(int direction){
        if(size == 0)throw new RuntimeException("List has not been initialized with data!");
        switch(direction){
            case LEFT->{
                current = current.prev;
                currentPosition -= 1;
                if(currentPosition == 0)currentPosition = size;
            }
            case RIGHT ->{
                current = current.next;
                currentPosition += 1;
                if(currentPosition > size)currentPosition = 1;
            }
            default -> throw new IllegalArgumentException("Invalid direction");
        }
    }
    public void addAll(int[] contents){
        for(int i: contents){
            add(i);
        }
    }
    public void add(Integer val){
        if(head == null){
            head = new Node(val);
            head.next = head;
            head.prev = head;
            end = head;
            current = head;
            currentPosition = 1;
        }
        else{
            end.next = new Node(val);
            end.next.prev = end;
            end = end.next;
            head.prev = end;
            end.next = head;
        }
        size++;
    }
    public void clear(){
        head = null;
        size = 0;
    }
    public void remove(){
        if(current == null)throw new RuntimeException("List is empty!");
        if(size == 1){
            head = null;
            current = null;
            end = null;
        }
        else{
            Node temp = current.next;
            current.next.prev = current.prev;
            current.prev.next = temp;
            current = temp;
        }
        size--;
        if(currentPosition > size)currentPosition = size;
    }
    public Integer[] toArray(){
        if(size==0)return new Integer[]{};
        Integer[] contents = new Integer[size];
        Node traverse = head;
        int i = 0;
        do{
            contents[i] = traverse.data;
            i++;
            traverse = traverse.next;
        }while(i<size);
        return contents;
    }
    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        Node traverse = head;
        do{
            result.append(traverse.data);
            if(traverse != end)result.append(" , ");
            traverse = traverse.next;
        }while(traverse!=head);
        return result.toString();
    }
}
