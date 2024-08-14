package xyz.hooy.vxi11.entity;

import xyz.hooy.vxi11.Vxi11ClientLink;

import java.util.EventListener;

@FunctionalInterface
public interface Vxi11ServiceRequestListener extends EventListener {

    void action(Vxi11ClientLink link);
}
