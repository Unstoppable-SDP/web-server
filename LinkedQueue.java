public class LinkedQueue {
    // number of elements in the queue
    private int size;
    private int maxSize;
    private Node first;
    private Node last;

    // helper linked list class
    private class Node {
        private Object item;
        private Node next;
    }

    // constructor
    public LinkedQueue(int maxSize) {
        first = null;
        last = null;
        size = 0;
        this.maxSize = maxSize;
    }

    // is the queue empty?
    public boolean isEmpty() {
        return first == null;
    }

    // return the number of elements in the queue
    public int size() {
        return size;
    }

    // add item to the queue
    public void enqueue(Object item) {
        if (size == maxSize)
            throw new RuntimeException("Queue overflow");
        Node oldlast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        if (isEmpty())
            first = last;
        else
            oldlast.next = last;
        size++;
    }

    // remove and return the item on the queue least recently added
    public Object dequeue() {
        if (isEmpty())
            throw new RuntimeException("Queue underflow");
        Object item = first.item;
        first = first.next;
        size--;
        if (isEmpty())
            last = null; // to avoid loitering
        return item;
    }

    // add replace head method
    public void replaceHead(Object item) {
        if (isEmpty())
            throw new RuntimeException("Queue underflow");
        first.item = item;
    }
    
    public void print() {
        Node current = first;
        while (current != null) {
            System.out.println(current.item);
            current = current.next;
        }
    }


    // main method for testing
    public static void main(String[] args) {
        LinkedQueue q = new LinkedQueue(10); 
        q.enqueue("A"); 
        q.enqueue("B"); 
        q.enqueue("C"); 
        q.replaceHead("F");
        System.out.println("queue List:");
        q.print();
        System.out.println("item removed from queue: " + q.dequeue());
        System.out.println("item removed from queue: " + q.dequeue());
        q.print();
    } 

}
