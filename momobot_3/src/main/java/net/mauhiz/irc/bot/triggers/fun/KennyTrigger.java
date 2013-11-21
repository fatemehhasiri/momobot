package net.mauhiz.irc.bot.triggers.fun;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.mauhiz.irc.base.IIrcControl;
import net.mauhiz.irc.base.msg.Privmsg;
import net.mauhiz.irc.base.trigger.IPrivmsgTrigger;
import net.mauhiz.irc.bot.triggers.AbstractTextTrigger;
import net.mauhiz.util.FileUtil;
import net.mauhiz.util.UtfChar;

import org.apache.commons.lang3.StringUtils;

/**
 * Transforme un texte en KennySpeak.
 * 
 * @author mauhiz
 */
public class KennyTrigger extends AbstractTextTrigger implements IPrivmsgTrigger {
    /**
     * longueur d'une kennylettre.
     */
    private static final int KENNY_LETTER_LEN = 3;
    /**
     * The KENNYLETTERS in alphabetical order. Big Letters are the Same with the only difference That the First char is
     * UpperCase
     */
    private static final Map<String, UtfChar> KENNY_TO_NORMAL = new TreeMap<>();
    /**
     * 
     */
    private static final Map<UtfChar, String> NORMAL_TO_KENNY = new TreeMap<>();

    /**
     * @param toTest
     *            la chaine a tester
     * @return ...
     */
    private static boolean isKenny(String toTest) {
        if (toTest.length() % 3 != 0) {
            return false;
        }
        for (int i = 0; i < toTest.length(); i++) {
            UtfChar loopChar = UtfChar.charAt(toTest, i);
            if (loopChar.isLetter() && !isKennyAlphabet(loopChar)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isKennyAlphabet(UtfChar utfChar) {
        UtfChar lowerCh = utfChar.toLowerCase();
        return lowerCh.isEquals('m') || lowerCh.isEquals('f') || lowerCh.isEquals('p');
    }

    /**
     * La map kenny est formee ainsi...
     * 
     * <pre>
     * a mpf\r\n
     * </pre>
     */
    private static void loadKenny() {
        try {
            List<String> lignes = FileUtil.readFileInCp("kenny_map.txt", FileUtil.ISO8859_15);
            if (!KENNY_TO_NORMAL.isEmpty()) {
                KENNY_TO_NORMAL.clear();
            }
            if (!NORMAL_TO_KENNY.isEmpty()) {
                NORMAL_TO_KENNY.clear();
            }
            for (String ligne : lignes) {
                if (ligne.length() < 2) {
                    continue;
                }
                UtfChar normalLetter = UtfChar.charAt(ligne, 0);
                String kennyLetter = ligne.substring(2);
                NORMAL_TO_KENNY.put(normalLetter, kennyLetter);
                KENNY_TO_NORMAL.put(kennyLetter, normalLetter);
            }
        } catch (IOException ioe) {
            LOG.warn(ioe, ioe);
        }
    }

    /**
     * @param toTranslate
     *            String to Translate
     * @return translated String
     */
    public static CharSequence translate(String toTranslate) {
        if (isKenny(toTranslate)) {
            return translateKennyToNormal(toTranslate);
        }
        return translateNormalToKenny(toTranslate);
    }

    /**
     * @param toTranslate
     *            la string a traduire
     * @return ...
     */
    private static UtfChar translateKennyLetterToNormalLetter(String toTranslate) {
        if (KENNY_TO_NORMAL.isEmpty()) {
            loadKenny();
        }
        if (toTranslate.length() != KENNY_LETTER_LEN) {
            throw new IllegalArgumentException(toTranslate + " n'est pas en Kennyspeak");
        }
        // use default Locale is OK
        UtfChar retour = KENNY_TO_NORMAL.get(toTranslate.toLowerCase());
        UtfChar first = UtfChar.charAt(toTranslate, 0);
        return first.isUpperCase() ? retour.toUpperCase() : retour;
    }

    /**
     * @param toTranslate
     * @return ...
     */
    private static CharSequence translateKennyToNormal(String toTranslate) {
        StringBuilder result = new StringBuilder();

        for (int index = 0; index < toTranslate.length();) {
            while (true) {
                UtfChar utfChar = UtfChar.charAt(toTranslate, index);
                if (isKennyAlphabet(utfChar)) {
                    break;
                }
                result.append(utfChar);
                ++index;
                if (toTranslate.length() == index) {
                    return result;
                }
            }

            String kennyLetter = toTranslate.substring(index, index + KENNY_LETTER_LEN);
            try {
                translateKennyLetterToNormalLetter(kennyLetter).appendTo(result);
            } catch (IllegalArgumentException iae) {
                LOG.error(iae, iae);
            }
            index += KENNY_LETTER_LEN;
        }
        return result;
    }

    /**
     * @param normalLetter
     *            un caractere.
     * @return une kennylettre.
     */
    private static String translateNormalLetterToKennyLetter(UtfChar normalLetter) {
        if (NORMAL_TO_KENNY.isEmpty()) {
            loadKenny();
        }
        // use default Locale is OK
        String retour = NORMAL_TO_KENNY.get(normalLetter.toLowerCase());
        return normalLetter.isUpperCase() ? StringUtils.capitalize(retour) : retour;
    }

    /**
     * @param toTranslate
     *            un string
     * @return ...
     */
    private static CharSequence translateNormalToKenny(String toTranslate) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < toTranslate.length(); i++) {
            int ch = UtfChar.charAt(toTranslate, i).getCodePoint();
            if (ch < 'A' || ch > 'Z' && ch < 'a' || ch > 'z') {
                result.append(ch);
            } else {
                result.append(translateNormalLetterToKennyLetter(UtfChar.charAt(toTranslate, i)));
            }
        }
        return result;
    }

    /**
     * @param trigger
     *            le trigger
     */
    public KennyTrigger(String trigger) {
        super(trigger);
    }

    /**
     * @see net.mauhiz.irc.base.trigger.IPrivmsgTrigger#doTrigger(Privmsg, IIrcControl)
     */
    @Override
    public void doTrigger(Privmsg im, IIrcControl control) {
        String args = getTriggerContent(im);
        if (StringUtils.isBlank(args)) {
            String kennyResponse = translate(args).toString();
            Privmsg retour = new Privmsg(im, kennyResponse);
            control.sendMsg(retour);
        } else {
            Privmsg retour = new Privmsg(im, getTriggerHelp());
            control.sendMsg(retour);
        }

    }
}
