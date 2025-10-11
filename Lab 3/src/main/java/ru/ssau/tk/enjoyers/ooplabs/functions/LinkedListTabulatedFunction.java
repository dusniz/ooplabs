package ru.ssau.tk.enjoyers.ooplabs.functions;

import java.util.Iterator;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable {

    private static class Node {
        public double x;
        public double y;
        public Node next;
        public Node prev;

        public Node(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    private Node head;
    private int count;

    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) {

        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);

        for (int i = 0; i < xValues.length; i++) {
            addNode(xValues[i], yValues[i]);
        }
    }

    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        if (count < 2) throw new IllegalArgumentException("Count must be >= 2");
        if (xFrom > xTo) {
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        if (xFrom == xTo) {
            double y = source.apply(xFrom);
            for (int i = 0; i < count; i++) {
                addNode(xFrom, y);
            }
        } else {
            double step = (xTo - xFrom) / (count - 1);
            for (int i = 0; i < count; i++) {
                double x = xFrom + i * step;
                double y = source.apply(x);
                addNode(x, y);
            }
        }
    }

    private void addNode(double x, double y) {
        Node newNode = new Node(x, y);
        if (head == null) {
            head = newNode;
            head.next = head;
            head.prev = head;
        } else {
            Node last = head.prev;
            last.next = newNode;
            newNode.prev = last;
            newNode.next = head;
            head.prev = newNode;
        }
        count++;
    }

    private Node getNode(int index) {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Index is out of bounds");
        }
        Node current;
        if (index <= count / 2) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = head.prev;
            for (int i = count - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public double getX(int index) {
        return getNode(index).x;
    }

    @Override
    public double getY(int index) {
        return getNode(index).y;
    }

    @Override
    public void setY(int index, double value) {
        getNode(index).y = value;
    }

    @Override
    public int indexOfX(double x) {
        Node current = head;
        for (int i = 0; i < count; i++) {
            if (Math.abs(current.x - x) < 1e-12) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        Node current = head;
        for (int i = 0; i < count; i++) {
            if (Math.abs(current.y - y) < 1e-12) {
                return i;
            }
            current = current.next;
        }
        return -1;
    }

    @Override
    public double leftBound() {
        return head.x;
    }

    @Override
    public double rightBound() {
        return head.prev.x;
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (x < head.x) return 0;
        if (x > head.prev.x) return count;

        Node current = head;
        for (int i = 0; i < count; i++) {
            if (current.x > x) {
                return i - 1;
            }
            current = current.next;
        }
        return count;
    }

    @Override
    protected double extrapolateLeft(double x) {
        return interpolate(x, 0);
    }

    @Override
    protected double extrapolateRight(double x) {
        return interpolate(x, count - 2);
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        Node leftNode = getNode(floorIndex);
        Node rightNode = leftNode.next;
        return interpolate(x, leftNode.x, rightNode.x, leftNode.y, rightNode.y);
    }

    // Для оптимизации (X*) переопределим apply, чтобы не искать дважды узел
    @Override
    public double apply(double x) {
        if (x < leftBound()) {
            return extrapolateLeft(x);
        } else if (x > rightBound()) {
            return extrapolateRight(x);
        } else {
            Node current = head;
            for (int i = 0; i < count; i++) {
                if (Math.abs(current.x - x) < 1e-12) {
                    return current.y;
                }
                if (current.x > x) {
                    // Тогда интерполируем между current.prev и current
                    Node left = current.prev;
                    return interpolate(x, left.x, current.x, left.y, current.y);
                }
                current = current.next;
            }
            // Если не нашли точного совпадения, но x в границах, то интерполируем между последним и первым?
            // Но у нас отсортированный список, поэтому лучше искать как в floorIndexOfX, но без индекса
            // Вместо этого мы уже в цикле нашли, что current.x > x, поэтому интерполируем между current.prev и current
            // Этот случай уже обработан выше. Если мы дошли до конца, то x больше всех, но это уже обработано в rightBound
            // Поэтому просто интерполируем между последним и предпоследним? Но это экстраполяция справа, а у нас x в границах.
            // Значит, мы должны были найти интервал. Если не нашли, то возвращаем интерполяцию по последнему интервалу.
            return interpolate(x, count - 2);
        }
    }

    @Override
    public void insert(double x, double y) {
        if (head == null) {
            addNode(x, y);
            return;
        }

        // Проверяем, существует ли уже такой x
        int existingIndex = indexOfX(x);
        if (existingIndex != -1) {
            setY(existingIndex, y);
            return;
        }

        // Ищем место для вставки
        Node current = head;
        do {
            if (current.x > x) {
                // Вставляем перед current
                Node newNode = new Node(x, y);
                newNode.next = current;
                newNode.prev = current.prev;
                current.prev.next = newNode;
                current.prev = newNode;

                if (current == head) {
                    head = newNode;
                }
                count++;
                return;
            }
            current = current.next;
        } while (current != head);

        // Если все x меньше заданного, добавляем в конец
        addNode(x, y);
    }

    @Override
    public void remove(int index) {
        Node nodeToRemove = getNode(index);
        if (count == 1) {
            head = null;
        } else {
            nodeToRemove.prev.next = nodeToRemove.next;
            nodeToRemove.next.prev = nodeToRemove.prev;
            if (index == 0) {
                head = nodeToRemove.next;
            }
        }
        count--;
    }

    @Override
    public Iterator<Point> iterator() {
        throw new UnsupportedOperationException();
    }
}