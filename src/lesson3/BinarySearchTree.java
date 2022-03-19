package lesson3;

import java.util.*;

import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// attention: Comparable is supported but Comparator is not
public class BinarySearchTree<T extends Comparable<T>> extends AbstractSet<T> implements CheckableSortedSet<T> {

    private static class Node<T> {
        final T value;
        Node<T> left = null;
        Node<T> right = null;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root = null;

    private int size = 0;

    @Override
    public int size() {
        return size;
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        } else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        } else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }

    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    /**
     * Добавление элемента в дерево
     * <p>
     * Если элемента нет в множестве, функция добавляет его в дерево и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * <p>
     * Спецификация: {@link Set#add(Object)} (Ctrl+Click по add)
     * <p>
     * Пример
     */
    @Override
    public boolean add(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        } else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
        } else {
            assert closest.right == null;
            closest.right = newNode;
        }
        size++;
        return true;
    }

    /**
     * Удаление элемента из дерева
     * <p>
     * Если элемент есть в множестве, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     * <p>
     * Спецификация: {@link Set#remove(Object)} (Ctrl+Click по remove)
     * <p>
     * Средняя
     */
    // T = В среднем O(log n)	В худшем O(n)
    // R = O(6) - Используется 6 элементов ?
    @Override
    public boolean remove(Object o) {
        if (o == null) return false;
        Node<T> currentNode = root;
        Node<T> parentNode = root;
        T remove = (T) o;
        boolean leftChild = true;

        while (!remove.equals(currentNode.value)) {
            parentNode = currentNode;

            if (remove.compareTo(currentNode.value) < 0) {
                leftChild = true;
                currentNode = currentNode.left;
            } else {
                leftChild = false;
                currentNode = currentNode.right;
            }
            if (currentNode == null)
                return false;
        }

        if (currentNode.left == null && currentNode.right == null) {
            if (currentNode == root) {
                root = null;
            } else if (leftChild) {
                parentNode.left = null;
            } else parentNode.right = null;

        } else if (currentNode.left == null) {
            if (currentNode == root) {
                root = currentNode.right;
            } else if (leftChild) {
                parentNode.left = currentNode.right;
            } else parentNode.right = currentNode.right;

        } else if (currentNode.right == null) {
            if (currentNode == root) {
                root = currentNode.left;
            } else if (leftChild) {
                parentNode.left = currentNode.left;
            } else parentNode.right = currentNode.left;

        } else if (currentNode.right.left == null) {
            if (currentNode == root) {
                root.right.left = root.left;
                root = currentNode.right;
            } else if (leftChild) {
                parentNode.left = currentNode.right;
                currentNode.right.left = currentNode.left;
            } else {
                parentNode.right = currentNode.right;
                currentNode.right.left = currentNode.left;
            }

        } else {
            Node<T> tmpNodeParent = currentNode;
            Node<T> tmpNode = currentNode.right;

            while (tmpNode.left != null) {
                tmpNodeParent = tmpNode;
                tmpNode = tmpNode.left;
            }
            if (tmpNode != currentNode.right) {
                tmpNodeParent.left = tmpNode.right;
                tmpNode.right = currentNode.right;
                tmpNode.left = currentNode.left;
            }
            if (currentNode == root) {
                root = tmpNode;
            } else if (leftChild) {
                parentNode.left = tmpNode;
            } else {
                parentNode.right = tmpNode;
            }
        }
        size--;
        return true;
    }


    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinarySearchTreeIterator();
    }

    public class BinarySearchTreeIterator implements Iterator<T> {
        private final ArrayDeque<Node<T>> ar = new ArrayDeque<>();
        private Node<T> current = root;
        private Node<T> parent;

        private BinarySearchTreeIterator() {
            // Добавьте сюда инициализацию, если она необходима.
        }

        /**
         * Проверка наличия следующего элемента
         * <p>
         * Функция возвращает true, если итерация по множеству ещё не окончена (то есть, если вызов next() вернёт
         * следующий элемент множества, а не бросит исключение); иначе возвращает false.
         * <p>
         * Спецификация: {@link Iterator#hasNext()} (Ctrl+Click по hasNext)
         * <p>
         * Средняя
         */
        // T = O(1) O(2) ?
        // R = O(1)
        @Override
        public boolean hasNext() {
            return (!ar.isEmpty() || current != null);
        }

        /**
         * Получение следующего элемента
         * <p>
         * Функция возвращает следующий элемент множества.
         * Так как BinarySearchTree реализует интерфейс SortedSet, последовательные
         * вызовы next() должны возвращать элементы в порядке возрастания.
         * <p>
         * Бросает NoSuchElementException, если все элементы уже были возвращены.
         * <p>
         * Спецификация: {@link Iterator#next()} (Ctrl+Click по next)
         * <p>
         * Средняя
         */
        // T = O(N)
        @Override
        public T next() {
            while (current != null) {
                ar.push(current);
                current = current.left;
            }
            current = ar.pop();
            parent = current;
            current = current.right;

            return parent.value;
        }

        /**
         * Удаление предыдущего элемента
         * <p>
         * Функция удаляет из множества элемент, возвращённый крайним вызовом функции next().
         * <p>
         * Бросает IllegalStateException, если функция была вызвана до первого вызова next() или же была вызвана
         * более одного раза после любого вызова next().
         * <p>
         * Спецификация: {@link Iterator#remove()} (Ctrl+Click по remove)
         * <p>
         * Сложная
         */
        // Трудоёмкость такая-же как и у remove
        @Override
        public void remove() {
            if (parent != null) {
                BinarySearchTree.this.remove(parent.value);
                parent = null;
            } else throw new IllegalStateException();
        }
    }

    /**
     * Подмножество всех элементов в диапазоне [fromElement, toElement)
     * <p>
     * Функция возвращает множество, содержащее в себе все элементы дерева, которые
     * больше или равны fromElement и строго меньше toElement.
     * При равенстве fromElement и toElement возвращается пустое множество.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     * <p>
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     * <p>
     * Спецификация: {@link SortedSet#subSet(Object, Object)} (Ctrl+Click по subSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     * <p>
     * Очень сложная (в том случае, если спецификация реализуется в полном объёме)
     */
    // T = O(N)
    // R = O(2N)
    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {

        SortedSet<T> set = new TreeSet<>() {
            final BinarySearchTree<T> bTree = BinarySearchTree.this;

            @Override
            public boolean add(T t) {
                if (fromElement.compareTo(t) > 0 || toElement.compareTo(t) <= 0) throw new IllegalArgumentException();
                bTree.add(t);
                return super.add(t);
            }

            @Override
            public boolean remove(Object o) {
                if (fromElement.compareTo((T) o) > 0 || toElement.compareTo((T) o) <= 0)
                    throw new IllegalArgumentException();
                bTree.remove(o);
                return super.remove(o);
            }

            @Override
            public T first() {
                return super.first();
            }

            @Override
            public T last() {
               return super.last();
            }

        };
        if (root == null)
            return set;
        if (fromElement.compareTo(toElement) == 0) return set;
        ArrayDeque<Node<T>> queue = new ArrayDeque<>();
        queue.push(root);
        while (!queue.isEmpty()) {
            Node<T> node = queue.pop();
            if (node.left != null)
                queue.offer(node.left);
            if (node.right != null)
                queue.offer(node.right);

            if (node.value.compareTo(fromElement) >= 0 && node.value.compareTo(toElement) < 0)
            set.add(node.value);
        }
        return set;
    }

    /**
     * Подмножество всех элементов строго меньше заданного
     * <p>
     * Функция возвращает множество, содержащее в себе все элементы дерева строго меньше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     * <p>
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     * <p>
     * Спецификация: {@link SortedSet#headSet(Object)} (Ctrl+Click по headSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     * <p>
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Подмножество всех элементов нестрого больше заданного
     * <p>
     * Функция возвращает множество, содержащее в себе все элементы дерева нестрого больше toElement.
     * Изменения в дереве должны отображаться в полученном подмножестве, и наоборот.
     * <p>
     * При попытке добавить в подмножество элемент за пределами указанного диапазона
     * должен быть брошен IllegalArgumentException.
     * <p>
     * Спецификация: {@link SortedSet#tailSet(Object)} (Ctrl+Click по tailSet)
     * (настоятельно рекомендуется прочитать и понять спецификацию перед выполнением задачи)
     * <p>
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        // TODO
        throw new NotImplementedError();
    }

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }

    public int height() {
        return height(root);
    }

    private int height(Node<T> node) {
        if (node == null) return 0;
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

}