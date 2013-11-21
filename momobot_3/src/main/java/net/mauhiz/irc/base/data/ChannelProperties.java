package net.mauhiz.irc.base.data;

import java.util.ArrayList;
import java.util.List;

import net.mauhiz.util.UtfChar;

import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public class ChannelProperties implements IChannelProperties {
    private static final Logger LOG = Logger.getLogger(ChannelProperties.class);
    private final List<HostMask> bans = new ArrayList<>();
    private boolean inviteOnly;
    private String key;
    private Integer limit;
    private boolean moderated;
    private boolean noExt;
    private boolean opTopic;
    private boolean prive;
    private Topic topic;

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#addBan(net.mauhiz.irc.base.data.HostMask)
     */
    @Override
    public void addBan(HostMask ban) {
        bans.add(ban);
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#getBans()
     */
    @Override
    public List<HostMask> getBans() {
        return bans;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#getKey()
     */
    @Override
    public String getKey() {
        return key;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#getLimit()
     */
    @Override
    public Integer getLimit() {
        return limit;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#getTopic()
     */
    @Override
    public Topic getTopic() {
        return topic;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#isInviteOnly()
     */
    @Override
    public boolean isInviteOnly() {
        return inviteOnly;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#isModerated()
     */
    @Override
    public boolean isModerated() {
        return moderated;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#isNoExt()
     */
    @Override
    public boolean isNoExt() {
        return noExt;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#isOpTopic()
     */
    @Override
    public boolean isOpTopic() {
        return opTopic;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#isPrive()
     */
    @Override
    public boolean isPrive() {
        return prive;
    }

    @Override
    public void process(boolean set, UtfChar mode, String... args) {
        if (mode.isEquals('b')) {
            HostMask hm = HostMask.getInstance(args[0]);
            if (set) {
                bans.add(hm);
            } else {
                bans.remove(hm);
            }
        } else if (mode.isEquals('i')) {
            inviteOnly = set;
        } else if (mode.isEquals('k')) {
            key = set ? args[0] : null;
        } else if (mode.isEquals('l')) {
            limit = set ? Integer.valueOf(args[0]) : null;
        } else if (mode.isEquals('m')) {
            moderated = set;
        } else if (mode.isEquals('n')) {
            noExt = set;
        } else if (mode.isEquals('t')) {
            opTopic = set;
        } else {
            LOG.warn("TODO process mode=" + mode);
        }
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#setInviteOnly(boolean)
     */
    @Override
    public void setInviteOnly(boolean inviteOnly) {
        this.inviteOnly = inviteOnly;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#setKey(java.lang.String)
     */
    @Override
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#setLimit(java.lang.Integer)
     */
    @Override
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#setModerated(boolean)
     */
    @Override
    public void setModerated(boolean moderated) {
        this.moderated = moderated;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#setNoExt(boolean)
     */
    @Override
    public void setNoExt(boolean noExt) {
        this.noExt = noExt;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#setOpTopic(boolean)
     */
    @Override
    public void setOpTopic(boolean opTopic) {
        this.opTopic = opTopic;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#setPrive(boolean)
     */
    @Override
    public void setPrive(boolean prive) {
        this.prive = prive;
    }

    /**
     * @see net.mauhiz.irc.base.data.IChannelProperties#setTopic(net.mauhiz.irc.base.data.Topic)
     */
    @Override
    public void setTopic(Topic topic) {
        this.topic = topic;
    }
}
