package org.pangu.outbox.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

public class LRUCache <T>{
    class Entry<T>{
        String key;
        T data;
        Entry pre;
        Entry next;
    }

    private Map<String, Entry<T>> cache;
    private Entry<T> dlist;
    private int capcity;
    private ReentrantLock lock;
    public LRUCache(int capcity){
        cache = new HashMap<>();
        dlist = createEntry(null);
        dlist.key = LRUCache.class.getName();
        this.capcity = capcity;
        lock = new ReentrantLock();
    }

    private Entry<T> createEntry(T data){
        Entry<T> entry = new Entry<>();
        entry.data = data;
        entry.pre = entry;
        entry.next  = entry;
        return entry;
    }


    public void put(String key, T data){
        lock.lock();
        try{
            Entry<T> entry = cache.get(key);
            if(entry != null){
                entry.data = data;
                moveHead(entry);
            }else {
                if(cache.size() > capcity){
                    removeTail();
                }

                entry = createEntry(data);
                entry.key = key;
                addHead(entry);
                cache.put(key, entry);
            }
        }finally {
            lock.unlock();
        }
    }

    public void reverseOrder(Consumer<T> functor){
        Entry<T> cur = dlist.pre;
        while(cur != dlist){
            functor.accept(cur.data);
            cur = cur.pre;
        }
    }

    public void normalOrder(Consumer<T> functor){
        Entry<T> cur = dlist.next;
        while (cur != dlist){
            functor.accept(cur.data);
            cur = cur.next;
        }
    }

    public T get(String key){
        Entry<T> entry = null;
        lock.lock();
        try{
            entry = cache.get(key);
            if(entry != null){
                moveHead(entry);
            }
            return entry.data;
        }catch (Throwable t){
            return null;
        } finally{
            lock.unlock();
        }
    }

    public T popFirst(){
        if(isEmpty()){
            return null;
        }

        Entry<T> top = dlist.next;
        cache.remove(top.key);
        remove(top);
        return top.data;
    }

    private void removeTail(){
        if(isEmpty()){
            return;
        }

        Entry<T> tail = dlist.pre;
        cache.remove(tail.key);
        remove(tail);
    }

    private void remove(Entry<T> node){
        if(isEmpty()){
            return;
        }

        /***0:1:2:3***/
        Entry<T> pre = node.pre;
        Entry<T> next = node.next;
        pre.next = next;
        next.pre = pre;

        node.pre = null;
        node.next = null;
    }

    private void moveHead(Entry<T> entry){
        remove(entry);
        addHead(entry);
    }

    private void addHead(Entry<T> entry){
        Entry<T> oldHead = dlist.next;
        /**add head**/
        dlist.next = entry;
        entry.pre = dlist;
        entry.next = oldHead;
        oldHead.pre = entry;
    }

    private boolean isEmpty(){
        return ((dlist.next == dlist) &&(dlist.pre == dlist));
    }
}
