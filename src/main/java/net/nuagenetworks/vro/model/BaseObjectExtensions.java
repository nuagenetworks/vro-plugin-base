package net.nuagenetworks.vro.model;

public interface BaseObjectExtensions {

    String getId();

    BaseSession<?> getSession();

    void setSession(BaseSession<?> session);
}
