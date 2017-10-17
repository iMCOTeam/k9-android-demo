package com.example.imco.event;

/**
 * Created by mai on 17-6-21.
 */

public class ItemClickEvent<T> {
    public ItemClickEvent(int position, T message) {
        this.position = position;
        this.message = message;
    }

    public int position;

    public T message;
}
