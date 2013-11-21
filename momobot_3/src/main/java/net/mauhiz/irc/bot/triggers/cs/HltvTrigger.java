package net.mauhiz.irc.bot.triggers.cs;

import java.io.IOException;
import java.net.SocketTimeoutException;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.data.ArgumentList;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.util.NetUtils;

/**
 * @author mauhiz
 */
public class HltvTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    private static final String CONNECT = "connect";
    private static final String DISCONNECT = "disconnect";
    private static IRconServer hltv;
    private static boolean recording;
    private static final String SELECT = "select";
    private static final String START = "start";
    private static final String STATUS = "status";
    private static final String STOP = "stop";

    private static void closeCurrent() throws IOException {
        try (IRconServer toClose = hltv) {
            // close any current hltv rcon connection
        }
    }

    /**
     * @param trigger
     *            le trigger
     */
    public HltvTrigger(String trigger) {
        super(trigger);
    }

    private String doTrigger(ArgumentList args0) throws IOException {
        String cmd = args0.poll();

        if (SELECT.equalsIgnoreCase(cmd)) {
            String ip = args0.poll();
            String rcon = args0.poll();
            closeCurrent();
            hltv = new RconServer(NetUtils.makeISA(ip), rcon);
            hltv.getClient().changeRcon(rcon);
            return "HLTV selected: " + ip;

        } else if (CONNECT.equalsIgnoreCase(cmd)) {

            if (hltv == null) {
                return "Please select a HLTV before";
            }
            String ip = args0.poll();
            String pass = args0.poll();
            hltv.getClient().rconCmd("serverpassword " + pass);
            NetUtils.makeISA(ip);
            hltv.getClient().rconCmd("connect " + ip);
            return "Connecting...";
        } else if (DISCONNECT.equalsIgnoreCase(cmd)) {
            recording = false;
            hltv.getClient().rconCmd("disconnect");
            return "Disconnecting";

        } else if (START.equalsIgnoreCase(cmd)) {
            hltv.getClient().rconCmd("record ");
            if (recording) {
                return "A demo is already being recorded";
            }

            recording = true;
            return "Starting demo: " + args0.getRemainingData();
        } else if (STOP.equalsIgnoreCase(cmd)) {
            if (recording) {
                recording = false;
                hltv.getClient().rconCmd("stoprecording");
                return "Stopping demo";
            }
            return "No demo was being recorded";
        } else if (STATUS.equalsIgnoreCase(cmd)) {
            if (hltv == null) {
                return "No HLTV selected";
            }
            if (recording) {
                return "HLTV recording";
            }
            return "HLTV stopped";
        } else {
            return getTriggerHelp();
        }
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        String reply = getReply(im);

        Privmsg msg = new Privmsg(im, reply);
        control.sendMsg(msg);
    }

    @Override
    protected void finalize() throws Throwable {
        closeCurrent();
        super.finalize();
    }

    private String getReply(Privmsg im) {
        try {
            return doTrigger(getArgs(im));

        } catch (IllegalArgumentException iae) {
            return "Invalid syntax: " + iae;

        } catch (SocketTimeoutException ste) {
            return "HLTV server offline: " + hltv.getIpay();

        } catch (IOException e) {
            LOG.warn(e, e);
            return "Error while connecting to HLTV: " + e;
        }
    }

    @Override
    public String getTriggerHelp() {
        return super.getTriggerHelp()
                + " [select ip:port rcon | connect ip:port pw | disconnect | start demoname | stop | status]";
    }
}
