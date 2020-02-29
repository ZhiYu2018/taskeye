package org.pangu;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.pangu.outbox.impl.LRUCache;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

    @Test
    public void testLru(){
        LRUCache<Integer> lruCache = new LRUCache<>(20);
        for(int i = 0; i < 20; i++){
            lruCache.put(String.valueOf(i), Integer.valueOf(i));
        }

        Integer t = lruCache.popFirst();
        System.out.println(t);
        System.out.println("======================================");
        lruCache.normalOrder(r -> {System.out.println(r);});
        System.out.println("======================================");
        lruCache.reverseOrder(r -> {System.out.println(r);});
        System.out.println("======================================");
        for(int i = 0; i < 100; i++){
            lruCache.put(String.valueOf(i), Integer.valueOf(i));
        }

        for(int i = 0; i < 30; i++){
            t = lruCache.popFirst();
            if(t == null){
                break;
            }

            System.out.println(i + ":" + t);
        }

    }
}
