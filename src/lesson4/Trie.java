package lesson4;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Префиксное дерево для строк
 */
public class Trie extends AbstractSet<String> implements Set<String> {

    private static class Node {
        SortedMap<Character, Node> children = new TreeMap<>();
    }

    private final Node root = new Node();

    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root.children.clear();
        size = 0;
    }

    private String withZero(String initial) {
        return initial + (char) 0;
    }

    @Nullable
    private Node findNode(String element) {
        Node current = root;
        for (char character : element.toCharArray()) {
            if (current == null) return null;
            current = current.children.get(character);
        }
        return current;
    }

    @Override
    public boolean contains(Object o) {
        String element = (String) o;
        return findNode(withZero(element)) != null;
    }

    @Override
    public boolean add(String element) {
        Node current = root;
        boolean modified = false;
        for (char character : withZero(element).toCharArray()) {
            Node child = current.children.get(character);
            if (child != null) {
                current = child;
            } else {
                modified = true;
                Node newChild = new Node();
                current.children.put(character, newChild);
                current = newChild;
            }
        }
        if (modified) {
            size++;
        }
        return modified;
    }

    @Override
    public boolean remove(Object o) {
        String element = (String) o;
        Node current = findNode(element);
        if (current == null) return false;
        if (current.children.remove((char) 0) != null) {
            size--;
            return true;
        }
        return false;
    }

    /**
     * Итератор для префиксного дерева
     * <p>
     * Спецификация: {@link Iterator} (Ctrl+Click по Iterator)
     * <p>
     * Сложная
     */

    @NotNull
    @Override
    public Iterator<String> iterator() {
        return new TrieIterator();
    }

    public class TrieIterator implements Iterator<String> {
        ArrayDeque<String> deque = new ArrayDeque<>();
        Stack<Pair<Node, String>> waitStack = new Stack<>();
        String current;

        TrieIterator() {
            root.children.forEach((c, n) -> waitStack.push(new Pair(n, c.toString())));
            if (!waitStack.isEmpty()) {
                Pair<Node, String> head = waitStack.pop();
                dequeInit(head.getFirst(), head.getSecond());
            }
        }

        void dequeInit(Node node, String word) {
            boolean k = true;
            Node curNode = node;
            String last = word;
            while (k) {
                for (Map.Entry<Character, Node> entry : curNode.children.entrySet()) {
                    Character c = entry.getKey();
                    Node n = entry.getValue();
                    if (c.equals((char) 0)) {
                        deque.push(last);
                    } else {
                        waitStack.push(new Pair<>(n, last + c));
                    }
                }
                if (!waitStack.isEmpty()) {
                    Pair<Node, String> head = waitStack.pop();
                    curNode = head.getFirst();
                    last = head.getSecond();
                } else k = false;
            }
        }

        //T = O(1)
        //R = O(1)
        @Override
        public boolean hasNext() {
            return !deque.isEmpty();
        }

        //T = O(N)
        //R = O(N)
        @Override
        public String next() {
            if (hasNext()) {
                current = deque.pop();
                if (!waitStack.isEmpty()) {
                    Pair<Node, String> head = waitStack.pop();
                    dequeInit(head.getFirst(), head.getSecond());
                }
                return current;
            } else throw new NoSuchElementException();
        }

        //T = O(N)
        //R = O(N)
        @Override
        public void remove() {
            if (current != null) {
                Trie.this.remove(current);
                current = null;
            } else throw new IllegalStateException();
        }
    }


}