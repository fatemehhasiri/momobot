package net.mauhiz.irc.base.data;

import java.util.List;

/**
 * @author mauhiz
 */
public abstract class ChannelProperties {
    List<Mask> bans;
    boolean inviteOnly;
    String key;
    Integer limit;
    boolean moderated;
    boolean noExt;
    boolean prive;
    String topic;
}