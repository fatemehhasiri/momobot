package net.mauhiz.irc.bot.triggers.memo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import net.mauhiz.irc.SqlUtils;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.text.StrBuilder;
import org.apache.log4j.Logger;

/**
 * TODO : utiliser hibernate?
 * 
 * @author mauhiz
 */
public class DbMemoUtils extends SqlUtils {
    /**
     * logger.
     */
    private static final Logger LOG = Logger.getLogger(DbMemoUtils.class);
    
    /**
     * (cle, memo).
     */
    private static final Map<String, String> MEMOS = new TreeMap<String, String>();
    
    /**
     * @return le nbre de players
     */
    public static int countMemos() {
        return MEMOS.size();
    }
    
    /**
     * @param key
     *            la cl�
     * @return le m�mo associ�
     */
    public static String getMemo(final String key) {
        if (!MEMOS.containsKey(key)) {
            return "Pas de m�mo pour " + key;
        }
        return MEMOS.get(key);
    }
    
    /**
     * @return la liste des memos
     */
    public static String getMemos() {
        final StrBuilder msg = new StrBuilder("m�mos :");
        for (final String string : MEMOS.keySet()) {
            msg.append(' ').append(string);
        }
        return msg.toString();
    }
    
    /**
     * @throws SQLException
     *             en cas de foirage.
     */
    public static void loadMemoDB() throws SQLException {
        ResultSet resultSet = null;
        try {
            resultSet = doSqlQuery("SELECT `cle`, `memo` FROM `memos`");
            while (resultSet.next()) {
                final String cle = resultSet.getString("cle");
                final String memo = resultSet.getString("memo");
                MEMOS.put(cle, memo);
            }
            LOG.debug("MemoDB loaded");
        } finally {
            DbUtils.closeQuietly(resultSet);
        }
    }
    
    /**
     * @param cle
     *            la cl�
     * @param memo
     *            le m�mo � set
     * @return un msg
     */
    public static String setMemo(final String cle, final String memo) {
        final StrBuilder msg = new StrBuilder("M�mo ").append(cle);
        if (MEMOS.containsKey(cle)) {
            msg.append(" mis � jour");
        } else {
            msg.append(" enregistr�");
        }
        MEMOS.put(cle, memo);
        try {
            doSqlUpdate("INSERT INTO `memos`(`cle`, `memo`) VALUES('" + cle + "', '" + memo + "')");
            return msg.toString();
        } catch (final SQLException sqle) {
            LOG.error(sqle, sqle);
        }
        return "Erreur, m�mo non enregistr�";
    }
}
