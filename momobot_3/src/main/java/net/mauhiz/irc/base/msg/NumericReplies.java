package net.mauhiz.irc.base.msg;

/**
 * @author mauhiz
 */
public enum NumericReplies {

    ERR_ALREADYREGISTRED(462),

    ERR_BADCHANMASK(476),

    ERR_BADCHANNELKEY(475),

    ERR_BANNEDFROMCHAN(474),

    ERR_CANNOTSENDTOCHAN(404),

    ERR_CANTKILLSERVER(483),

    ERR_CHANNELISFULL(471),

    ERR_CHANOPRIVSNEEDED(482),

    ERR_ERRONEUSNICKNAME(432),

    ERR_FILEERROR(424),

    ERR_INVITEONLYCHAN(473),

    ERR_KEYSET(467),

    ERR_NEEDMOREPARAMS(461),

    ERR_NICKCOLLISION(436),

    ERR_NICKNAMEINUSE(433),

    ERR_NOADMININFO(423),

    ERR_NOLOGIN(444),

    ERR_NOMOTD(422),

    ERR_NONICKNAMEGIVEN(431),

    ERR_NOOPERHOST(491),

    ERR_NOORIGIN(409),

    ERR_NOPERMFORHOST(463),

    ERR_NOPRIVILEGES(481),

    ERR_NORECIPIENT(411),

    ERR_NOSERVICEHOST(492),

    ERR_NOSUCHCHANNEL(403),

    ERR_NOSUCHNICK(401),

    ERR_NOSUCHSERVER(402),

    ERR_NOTEXTTOSEND(412),

    ERR_NOTONCHANNEL(442),

    ERR_NOTOPLEVEL(413),

    ERR_NOTREGISTERED(451),

    ERR_PASSWDMISMATCH(464),
    /**
     * custom error qnet
     */
    ERR_QNETSERVICEIMMUNE(485),

    ERR_SUMMONDISABLED(445),

    ERR_TOOMANYCHANNELS(405),

    ERR_TOOMANYTARGETS(407),

    ERR_UMODEUNKNOWNFLAG(501),

    ERR_UNKNOWNCOMMAND(421),

    ERR_UNKNOWNMODE(472),

    ERR_USERNOTINCHANNEL(441),

    ERR_USERONCHANNEL(443),

    ERR_USERSDISABLED(446),

    ERR_USERSDONTMATCH(502),

    ERR_WASNOSUCHNICK(406),

    ERR_WILDTOPLEVEL(414),

    ERR_YOUREBANNEDCREEP(465),

    ERR_YOUWILLBEBANNED(466),

    RPL_ADMINEMAIL(259),

    RPL_ADMINLOC1(257),

    RPL_ADMINLOC2(258),

    RPL_ADMINME(256),

    RPL_AWAY(301),

    RPL_BANLIST(367),

    RPL_CHANNELMODEIS(324),

    RPL_CLOSEEND(363),

    RPL_CLOSING(362),

    RPL_ENDOFBANLIST(368),

    RPL_ENDOFINFO(374),

    RPL_ENDOFLINKS(365),

    RPL_ENDOFMOTD(376),

    RPL_ENDOFNAMES(366),

    RPL_ENDOFSERVICES(232),

    RPL_ENDOFSTATS(219),

    RPL_ENDOFUSERS(394),

    RPL_ENDOFWHO(315),

    RPL_ENDOFWHOIS(318),

    RPL_ENDOFWHOWAS(369),

    RPL_INFO(371),

    RPL_INFOSTART(373),

    RPL_INVITING(341),

    RPL_ISON(303),

    RPL_KILLDONE(361),

    RPL_LINKS(364),

    RPL_LIST(322),

    RPL_LISTEND(323),

    RPL_LISTSTART(321),

    RPL_LUSERCHANNELS(254),

    RPL_LUSERCLIENT(251),

    RPL_LUSERME(255),

    RPL_LUSEROP(252),

    RPL_LUSERUNKNOWN(253),

    RPL_MOTD(372),

    RPL_MOTDSTART(375),

    RPL_MYPORTIS(384),

    RPL_NAMREPLY(353),

    RPL_NONE(300),

    /**
     * <channel> :No topic is set
     */
    RPL_NOTOPIC(331),

    RPL_NOUSERS(395),

    RPL_NOWAWAY(306),

    RPL_REHASHING(382),

    RPL_SERVICE(233),

    RPL_SERVICEINFO(231),

    RPL_SERVLIST(234),

    RPL_SERVLISTEND(235),

    RPL_STATSCLINE(213),

    RPL_STATSCOMMANDS(212),

    RPL_STATSHLINE(244),

    RPL_STATSILINE(215),

    RPL_STATSKLINE(216),

    RPL_STATSLINKINFO(211),

    RPL_STATSLLINE(241),

    RPL_STATSNLINE(214),

    RPL_STATSOLINE(243),

    RPL_STATSQLINE(217),

    RPL_STATSUPTIME(242),

    RPL_STATSYLINE(218),

    RPL_SUMMONING(342),

    RPL_TIME(391),

    /**
     * <channel> :<topic>
     */
    RPL_TOPIC(332),

    RPL_TOPICINFO(333),

    RPL_TRACECLASS(209),

    RPL_TRACECONNECTING(201),

    RPL_TRACEHANDSHAKE(202),

    RPL_TRACELINK(200),

    RPL_TRACELOG(261),

    RPL_TRACENEWTYPE(208),

    RPL_TRACEOPERATOR(204),

    RPL_TRACESERVER(206),

    RPL_TRACEUNKNOWN(203),

    RPL_TRACEUSER(205),

    RPL_UMODEIS(221),

    RPL_UNAWAY(305),

    RPL_USERHOST(302),

    RPL_USERS(393),

    RPL_USERSSTART(392),

    RPL_VERSION(351),
    /**
     * auth Qnet.
     */
    RPL_WHOISAUTH(330),

    RPL_WHOISCHANNELS(319),

    RPL_WHOISCHANOP(316),

    RPL_WHOISIDLE(317),

    RPL_WHOISOPERATOR(313),

    RPL_WHOISSERVER(312),

    RPL_WHOISUSER(311),

    RPL_WHOREPLY(352),

    RPL_WHOWASUSER(314),

    RPL_YOUREOPER(381);

    public static NumericReplies fromCode(int code) {
        for (NumericReplies reply : values()) {
            if (reply.code == code) {
                return reply;
            }
        }

        return null;
    }

    private int code;

    private NumericReplies(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
