package net.mauhiz.irc;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import net.mauhiz.util.FileUtil;

import org.apache.commons.lang.text.StrTokenizer;
import org.apache.log4j.Logger;

/**
 * @author mauhiz
 */
public class Morse {
    /**
     * mon tableau avec le code morse.
     */
    private static final Map<Character, String> CODE_MORSE = new TreeMap<Character, String>();
    /**
     * logger.
     */
    private static final Logger LOG = Logger.getLogger(Morse.class);
    /**
     * mon tableau avec le code morse inverse.
     */
    private static final Map<String, Character> REVERSE_MORSE = new TreeMap<String, Character>();
    
    /**
     * les lettres doivent etre separees par un espace.
     * 
     * @param work
     *            la chaine en Morse
     * @return la chaine en lettres
     */
    public static String fromMorse(String work) {
        if (REVERSE_MORSE.isEmpty()) {
            loadMorse();
        }
        StringBuilder output = new StringBuilder();
        for (String element : new StrTokenizer(work).getTokenArray()) {
            output.append(REVERSE_MORSE.get(element));
        }
        return output.toString();
    }
    
    /**
     * La map morse est forme ainsi...
     * 
     * <pre>
     * A .-\r\n
     * </pre>
     */
    private static void loadMorse() {
        try {
            List<String> lignes = FileUtil.readFileInCp("morse_map.txt", FileUtil.ISO8859_15);
            /*
             * si on est arrives jusque la le fichier existe, donc on peut nettoyer nos maps.
             */
            if (!CODE_MORSE.isEmpty()) {
                CODE_MORSE.clear();
            }
            if (!REVERSE_MORSE.isEmpty()) {
                REVERSE_MORSE.clear();
            }
            Character chara;
            String traitPoint;
            for (String ligne : lignes) {
                chara = Character.valueOf(ligne.charAt(0));
                traitPoint = ligne.substring(2);
                CODE_MORSE.put(chara, traitPoint);
                REVERSE_MORSE.put(traitPoint, chara);
            }
        } catch (IOException ioe) {
            LOG.warn(ioe, ioe);
        }
    }
    /**
     * @param work
     *            la chaine a convertir en Morse
     * @return la chaine en Morse
     */
    public static String toMorse(String work) {
        if (CODE_MORSE.isEmpty()) {
            loadMorse();
        }
        StringBuilder output = new StringBuilder();
        String upper = work.toUpperCase(Locale.FRANCE);
        for (char element : upper.toCharArray()) {
            output.append(CODE_MORSE.get(Character.valueOf(element)));
        }
        return output.toString();
    }
}
