package xyz.hooy.vxi11.entity;

import xyz.hooy.vxi11.util.BitUtils;

import java.util.EventObject;

public class StatusByteEvent extends EventObject {

    private final byte status;

    public StatusByteEvent(byte status) {
        super(status);
        this.status = status;
    }

    public boolean getStatus(int position) {
        return BitUtils.isBit(status, position);
    }
}
