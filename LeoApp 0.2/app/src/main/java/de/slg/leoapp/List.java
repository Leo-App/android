package de.slg.leoapp;

import java.util.Iterator;

@SuppressWarnings({"WeakerAccess", "unused"})
public class List<ContentType> implements Iterable<ContentType> {

    private Node first, last, current;
    private int length;

    public List() {
        first = null;
        last = null;
        current = null;
        length = 0;
    }

    public List(ContentType[] array) {
        first = null;
        last = null;
        current = null;
        length = 0;
        adapt(array);
    }

    public boolean isEmpty() {
        return first == null;
    }

    public boolean hasAccess() {
        return current != null;
    }

    public boolean hasPrevious() {
        return hasAccess() && current.previous != null;
    }

    public boolean hasNext() {
        return hasAccess() && current.next != null;
    }

    public boolean isFirst() {
        return current == first;
    }

    public boolean isLast() {
        return current == last;
    }

    public void next() {
        current = current.next;
    }

    public void previous() {
        current = current.previous;
    }

    public void toFirst() {
        if (!isEmpty()) {
            current = first;
        }
    }

    public void toLast() {
        if (!isEmpty()) {
            current = last;
        }
    }

    public void toIndex(int index) {
        if (index >= length - 1)
            toLast();
        else
            for (toFirst(); hasAccess() && index > 0; next(), index--) ;
    }

    public ContentType getContent() {
        if (this.hasAccess())
            return current.content;
        return null;
    }

    public void setContent(ContentType pContent) {
        if (pContent != null && this.hasAccess())
            current.content = pContent;
    }

    public ContentType getNext() {
        if (hasAccess() && current.next != null)
            return current.next.content;
        return null;
    }

    public ContentType getPrevious() {
        if (hasAccess() && current.previous != null)
            return current.previous.content;
        return null;
    }

    public void insertBefore(ContentType pContent) {
        if (pContent != null) {
            if (hasAccess()) {
                Node newNode = new Node(pContent);
                if (current == first)
                    first = newNode;
                newNode.insertBefore(current, newNode == first);
                length++;
            } else {
                if (isEmpty()) {
                    Node newNode = new Node(pContent);
                    first = newNode;
                    last = newNode;
                    length = 1;
                } else {
                    append(pContent);
                }
            }
        }
    }

    public void insertBehind(ContentType pContent) {
        if (pContent != null) {
            if (hasAccess()) {
                Node newNode = new Node(pContent);
                newNode.insertBehind(current, newNode == last);
                length++;
            } else {
                if (isEmpty()) {
                    Node newNode = new Node(pContent);
                    first = newNode;
                    last = newNode;
                    length = 1;
                } else {
                    append(pContent);
                }
            }
        }
    }

    public List<ContentType> append(ContentType pContent) {
        if (pContent != null) {
            if (this.isEmpty()) {
                this.insertBefore(pContent);
            } else {
                Node newNode = new Node(pContent);
                newNode.insertBehind(last, true);
                last = newNode;
                length++;
            }
        }
        return this;
    }

    public void concat(List<ContentType> pList) {
        if (pList != this && pList != null && !pList.isEmpty()) {
            if (this.isEmpty()) {
                first = pList.first;
                last = pList.last;
            } else {
                last.next = pList.first;
                pList.first.previous = last;
                last = pList.last;
            }
            pList.first = null;
            pList.last = null;
            pList.current = null;
            length += pList.size();
        }
    }

    public void remove() {
        if (this.hasAccess() && !this.isEmpty()) {
            if (current == first)
                first = first.next;
            else
                current.previous.next = current.next;
            if (current == last)
                last = current.previous;
            else
                current.next.previous = current.previous;
            Node temp = current;
            next();
            temp.content = null;
            temp.next = null;
            temp.previous = null;
            length--;
        }
    }

    public void adapt(ContentType[] array) {
        for (ContentType c : array)
            append(c);
    }

    public ContentType[] fill(ContentType[] array) {
        toFirst();
        for (int i = 0; i < array.length; i++, next())
            array[i] = getContent();
        return array;
    }

    public int size() {
        return length;
    }

    public boolean contains(ContentType object) {
        for (toFirst(); hasAccess(); next()) {
            if (object.equals(getContent()))
                return true;
        }
        return false;
    }

    public ContentType getObjectAt(int index) {
        toIndex(index);
        return getContent();
    }

    @Override
    public Iterator<ContentType> iterator() {
        current = null;
        return new Iterator<ContentType>() {
            @Override
            public boolean hasNext() {
                return current != last;
            }

            @Override
            public ContentType next() {
                if (hasAccess())
                    List.this.next();
                else
                    toFirst();
                return getContent();
            }
        };
    }

    private class Node {
        ContentType content;
        Node next, previous;

        Node(ContentType contentObject) {
            this.content = contentObject;
            next = null;
            previous = null;
        }

        void insertBehind(Node pNode, boolean newLast) {
            this.next = pNode.next;
            this.previous = pNode;
            pNode.next = this;
            if (!newLast)
                this.next.previous = this;
        }

        void insertBefore(Node pNode, boolean newFirst) {
            this.previous = pNode.previous;
            this.next = pNode;
            pNode.previous = this;
            if (!newFirst)
                this.previous.next = this;
        }
    }
}