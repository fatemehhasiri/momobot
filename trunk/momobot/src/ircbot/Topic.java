package ircbot;

/**
 * Simple objet de stockage des informations relatives � un topic.
 * @author mauhiz
 */
public class Topic {
    /**
     * Date � laquelle le topic a �t� set.
     */
    private long    date;

    /**
     * Le mec qui a set le topic. Comme il est sans doute plus sur IRC, on a
     * juste son nick, pas d'objet {@link IrcUser}. C'est d'ailleurs une faille
     * du syst�me d'authentification des users sur IRC.
     */
    private String  setBy;

    /**
     * Contenu du topic.
     */
    private String  topic;

    /**
     * Indique si le topic est prot�g� contre les non-ops.
     */
    private boolean topicProtected = false;

    /**
     * @param topic1
     *            le tropic
     * @param date1
     *            la date
     * @param setBy1
     *            celui qui a mis le topic
     */
    public Topic(final String topic1, final long date1, final String setBy1) {
        this.topic = topic1;
        this.date = date1;
        this.setBy = setBy1;
    }

    /**
     * @return la date
     */
    public final long getDate() {
        return this.date;
    }

    /**
     * @return Returns the setBy.
     */
    public final String getSetBy() {
        return this.setBy;
    }

    /**
     * @return le topic
     */
    public final String getTopic() {
        return this.topic;
    }

    /**
     * @return Returns the topicProtected.
     */
    public final boolean isTopicProtected() {
        return this.topicProtected;
    }

    /**
     * @param date1
     *            la date
     */
    public final void setDate(final long date1) {
        this.date = date1;
    }

    /**
     * @param setBy1
     *            The setBy to set.
     */
    public final void setSetBy(final String setBy1) {
        this.setBy = setBy1;
    }

    /**
     * @param topic1
     *            le topic
     */
    public final void setTopic(final String topic1) {
        this.topic = topic1;
    }

    /**
     * @param topicProtected1
     *            The topicProtected to set.
     */
    public final void setTopicProtected(final boolean topicProtected1) {
        this.topicProtected = topicProtected1;
    }
}
