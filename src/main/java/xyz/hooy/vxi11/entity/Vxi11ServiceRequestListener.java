package xyz.hooy.vxi11.entity;

import java.util.EventListener;

@FunctionalInterface
public interface Vxi11ServiceRequestListener extends EventListener {

    void action(StatusByte statusByte);
}
