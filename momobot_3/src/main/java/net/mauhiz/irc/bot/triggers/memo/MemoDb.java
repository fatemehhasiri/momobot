package net.mauhiz.irc.bot.triggers.memo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mauhiz.irc.HibernateUtils;
import net.mauhiz.irc.base.data.IrcServer;

import org.apache.log4j.Logger;
import org.hibernate.Query;

/**
 * @author mauhiz
 */
public class MemoDb {
    private static final Map<IrcServer, MemoDb> DBS = new HashMap<IrcServer, MemoDb>();
    
    /**
     * logger.
     */
    private static final Logger LOG = Logger.getLogger(MemoDb.class);
    /**
     * @param server
     * @return
     */
    public static MemoDb getInstance(final IrcServer server) {
        MemoDb db = DBS.get(server);
        if (db == null) {
            db = new MemoDb(server);
            DBS.put(server, db);
        }
        return db;
    }
    IrcServer server;
    
    /**
     * @param server1
     */
    public MemoDb(final IrcServer server1) {
        server = server1;
    }
    
    /**
     * @return le nbre de memos enregistres par rapport a ce serveur
     */
    public int countMemos() {
        Query countQry = HibernateUtils.currentSession().createQuery(
                "select count(1) from Memo where serverAlias = :serverAlias");
        countQry.setString("serverAlias", server.getAlias());
        return (Integer) countQry.uniqueResult();
    }
    
    /**
     * @param key
     *            la cl�
     * @return le m�mo associ�
     */
    public String getMemo(final String key) {
        String result = getValue(key);
        if (result == null) {
            return "Pas de m�mo pour '" + key + "'";
        }
        return result;
    }
    
    /**
     * @return la liste des memos
     */
    public String getMemos() {
        Query getQry = HibernateUtils.currentSession().createQuery(
                "select value from Memo where serverAlias = :serverAlias");
        getQry.setString("serverAlias", server.getAlias());
        List<String> results = getQry.list();
        if (results.isEmpty()) {
            return "Pas de memo pour l'instant";
        }
        
        final StringBuilder msg = new StringBuilder("m�mos :");
        for (final String result : results) {
            msg.append(' ').append(result);
        }
        return msg.toString();
    }
    
    /**
     * @param key
     *            la cl�
     * @return le m�mo associ�
     */
    public String getValue(final String key) {
        Query getQry = HibernateUtils.currentSession().createQuery(
                "select value from Memo where serverAlias = :serverAlias and key = :key");
        getQry.setString("serverAlias", server.getAlias());
        getQry.setString("key", key);
        return (String) getQry.uniqueResult();
    }
    
    /**
     * @param key
     *            la cl�
     * @param value
     *            le m�mo � set
     * @return un msg
     */
    public String setMemo(final String key, final String value) {
        String oldValue = getValue(key);
        final StringBuilder msg = new StringBuilder("M�mo '").append(key).append("' ");
        HibernateUtils.currentSession().getTransaction().begin();
        if (oldValue == null) {
            Memo memo = new Memo();
            memo.setServerAlias(server.getAlias());
            memo.setKey(key);
            memo.setValue(value);
            HibernateUtils.currentSession().save(memo);
            msg.append("enregistr�");
        } else {
            /* TODO mise a jour */
            Query getQry = HibernateUtils.currentSession().createQuery(
                    "update Memo set value = :value where serverAlias = :serverAlias and key = :key");
            getQry.setString("serverAlias", server.getAlias());
            getQry.setString("key", key);
            getQry.setString("value", value);
            int updated = getQry.executeUpdate();
            if (updated == 1) {
                msg.append("mis � jour");
            } else {
                HibernateUtils.currentSession().getTransaction().rollback();
                return "Erreur, m�mo non enregistr�";
            }
        }
        
        HibernateUtils.currentSession().getTransaction().commit();
        return msg.toString();
        
    }
}
